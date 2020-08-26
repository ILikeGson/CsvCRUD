package main.java.ZhenyaShvyrkov.javacore.chapter06.repository.csv;

import com.opencsv.CSVWriter;
import main.java.ZhenyaShvyrkov.javacore.chapter06.model.Account;
import main.java.ZhenyaShvyrkov.javacore.chapter06.model.AccountBuilder;
import main.java.ZhenyaShvyrkov.javacore.chapter06.model.Customer;
import main.java.ZhenyaShvyrkov.javacore.chapter06.model.Specialty;
import main.java.ZhenyaShvyrkov.javacore.chapter06.repository.AccountRepository;
import main.java.ZhenyaShvyrkov.javacore.chapter06.repository.CustomerRepository;
import main.java.ZhenyaShvyrkov.javacore.chapter06.repository.SpecialtyRepository;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class CsvCustomerRepositoryImpl implements CustomerRepository {
    private static final Path PATH = Paths.get("customer.csv");
    private static final File FILE = PATH.toFile();
    private static CustomerRepository csvCustomerRepository;
    private static SpecialtyRepository csvSpecialtyRepository;
    private static AccountRepository csvAccountRepository;
    private long id;


    private CsvCustomerRepositoryImpl() {}

    public static synchronized CustomerRepository getCsvCustomerRepository(){
        if (csvCustomerRepository == null) {
            csvCustomerRepository = new CsvCustomerRepositoryImpl();
            csvAccountRepository = CsvAccountRepositoryImpl.getRepository();
            csvSpecialtyRepository = CsvSpecialtyRepositoryImpl.getCsvSpecialtyRepository();
            if (!FILE.exists()) {
                try {
                    Files.write(PATH, "".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return csvCustomerRepository;
    }

    @Override
    public Customer save(Customer customer) {
        csvAccountRepository.save(customer.getAccount());
        customer.getSpecialties().forEach(specialty -> specialty = csvSpecialtyRepository.save(specialty));
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(FILE, true));
            id = findMaxId();
            customer.setId(id++);
            String[] customerData = (id + "," + customer.toString().replaceAll("[\\[\\]]", "").replaceAll(", ", ",")).split(",");
            writer.writeNext(customerData, false);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customer;
    }

    @Override
    public List<Customer> read() {
        List<Customer> customers = new ArrayList<>();
        try {
            List<String> fileData = Files.readAllLines(PATH, StandardCharsets.UTF_8);
            fileData.forEach(line -> customers.add(toCustomer(line.replaceAll("\\[|]", "").split(","))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customers;
    }

    @Override
    public Customer readById(Long id) {
        Customer customer = null;
        try {
            List<String> fileData = Files.readAllLines(PATH, StandardCharsets.UTF_8);
            for (String line : fileData) {
                if (line.startsWith(String.valueOf(id))) {
                    String[] customerData = line.replaceAll("\\[|]", "").split(",");
                    customer = toCustomer(customerData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customer;
    }

    @Override
    public Customer update(Customer customer, Long customerID) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(FILE, true));
            List<String> fileData = Files.readAllLines(PATH, StandardCharsets.UTF_8);
            Files.write(PATH, "".getBytes());
            String[] customerData;
            for (String line : fileData) {
                if (line.startsWith(String.valueOf(customerID))) {
                    csvAccountRepository.update(customer.getAccount(), customerID);
                    String[] specialtiesID = line.replaceAll(".*(ACTIVE|BANNED|DELETED),", "").split(",");
                    Iterator<Specialty> iterator = customer.getSpecialties().iterator();
                    if (specialtiesID.length <= customer.getSpecialties().size()){
                        for (int i = 0; i < customer.getSpecialties().size(); i++) {
                            Specialty specialty = iterator.next();
                            if (i < specialtiesID.length) {
                                id = Long.parseLong(specialtiesID[i]);
                                csvSpecialtyRepository.update(specialty, id);
                                specialty.setId(id);
                            } else {
                                csvSpecialtyRepository.save(specialty);
                            }
                        }
                    } else {
                        for (int i = 0; i < specialtiesID.length; i++) {
                            id = Long.parseLong(specialtiesID[i]);
                            if (i < customer.getSpecialties().size()) {
                                Specialty specialty = iterator.next();
                                csvSpecialtyRepository.update(specialty, id);
                                specialty.setId(id);
                            } else {
                                csvSpecialtyRepository.deleteByID(id);
                            }
                        }
                    }
                    line = customer.toString().replaceAll(", ", ",").replaceAll("\\[|]","");
                    customerData = (customerID + "," + line).split(",");
                } else {
                    customerData = (line).split(",");
                }
                writer.writeNext(customerData, false);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customer;
    }

    @Override
    public void delete(Customer customer) {
        try {
            String[] customerData;
            CSVWriter writer = new CSVWriter(new FileWriter(FILE, true));
            List<String> customersInfo = Files.readAllLines(PATH, StandardCharsets.UTF_8);
            Files.write(PATH, "".getBytes());
            csvAccountRepository.delete(customer.getAccount());
            for (String line : customersInfo) {
                if (line.contains(customer.getAccount().toString().replaceAll(", ", ","))){
                    line = line.replaceFirst("ACTIVE|BANNED", "DELETED");
                    String[] specialties = line.split(",");
                    for (int i = 5; i < specialties.length; i++){
                        csvSpecialtyRepository.deleteByID(Long.parseLong(specialties[i]));
                    }
                    customerData = line.replaceAll("DELETED.+", "DELETED,[]").split(",");
                } else {
                    customerData = line.split(",");
                }
                writer.writeNext(customerData, false);
                writer.flush();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteByID(Long id) {
        try {
            List<String> customersInfo = Files.readAllLines(PATH, StandardCharsets.UTF_8);
            csvAccountRepository.deleteByID(id);
            String[] customerData;
            CSVWriter writer = new CSVWriter(new FileWriter(FILE, true));
            Files.write(PATH, "".getBytes());
            for (String customer : customersInfo) {
                if (customer.startsWith(String.valueOf(id))) {
                    String[] specialties = customer.split(",");
                    for (int i = 5; i < specialties.length; i++){
                        csvSpecialtyRepository.deleteByID(Long.parseLong(specialties[i]));
                    }
                    customer = customer.replaceAll("(ACTIVE|BANNED)", "DELETED");
                    customer = customer.replaceAll("DELETED.+", "DELETED,[]");
                }
                customerData = customer.split(",");
                writer.writeNext(customerData, false);
                writer.flush();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long findMaxId()  {
        String allInfo = "";
        try {
            allInfo = Files.readString(PATH);
        }catch (IOException e) {e.printStackTrace();}
        if(allInfo.length() == 0) return 0;
        else {
            String[] array = allInfo.split(",.+\\s*");
            long [] arrayOfId = Arrays.stream(array).mapToLong(Long::parseLong).sorted().toArray();
            return arrayOfId[arrayOfId.length-1];
        }
    }
    private static Customer toCustomer(String[] customerData){
        if(customerData.length>=4) {
            long id = Long.parseLong(customerData[0].trim());
            String firstName = customerData[1].trim();
            String lastName = customerData[2].trim();
            int age = Integer.parseInt(customerData[3].trim());
            Account.AccountStatus status = null;
            if (customerData[4].equals(Account.AccountStatus.ACTIVE.toString())) {
                status = Account.AccountStatus.ACTIVE;
            } else if (customerData[4].equals(Account.AccountStatus.BANNED.toString())) {
                status = Account.AccountStatus.BANNED;
            } else if (customerData[4].equals(Account.AccountStatus.DELETED.toString())) {
                status = Account.AccountStatus.DELETED;
            }
            Account account = new AccountBuilder()
                    .buildAccount()
                    .buildFirstName(firstName)
                    .buildLastName(lastName)
                    .buildAge(age)
                    .buildStatus(status)
                    .build();
            account.setId(id);
            Set<Specialty> specialties = new HashSet<>();
            for (int i = 5; i < customerData.length; i++) {
                id = Long.parseLong(customerData[i]);
                Specialty specialty = csvSpecialtyRepository.readById(Long.parseLong(customerData[i]));
                specialty.setId(id);
                specialties.add(specialty);
            }
            return new Customer(account, specialties);
        } else return null;
    }
}
