package main.java.ZhenyaShvyrkov.javacore.chapter06.repository.csv;

import com.opencsv.CSVWriter;
import main.java.ZhenyaShvyrkov.javacore.chapter06.model.Account;
import main.java.ZhenyaShvyrkov.javacore.chapter06.model.AccountBuilder;
import main.java.ZhenyaShvyrkov.javacore.chapter06.repository.AccountRepository;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CsvAccountRepositoryImpl implements AccountRepository {
    private static AccountRepository repository;
    private static final Path PATH = Paths.get("accounts.csv");
    private static final File file = PATH.toFile();
    private static CSVWriter writer;
    private long id;

    private CsvAccountRepositoryImpl() {}

    public static synchronized AccountRepository getRepository(){
        if (repository == null) {
            repository = new CsvAccountRepositoryImpl();
            if (!file.exists()) {
                try {
                    Files.write(PATH, "".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return repository;
    }

    @Override
    public Account save(Account account) {
        try {
            id = findMaxId();
            account.setId(id++);
            FileWriter fileWriter = new FileWriter(file, true);
            CSVWriter writer = new CSVWriter(fileWriter);
            String[] data = (id + ", " + account.toString()).split(", ");
            writer.writeNext(data, false);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return account;
    }

    @Override
    public List<Account> read() {
        List<Account> accounts = new ArrayList<>();
        try {
            List<String> list = Files.readAllLines(PATH, StandardCharsets.UTF_8);
            for (String data : list) {
                data = data.replaceAll(",", " ");
                String[] acData = data.split(" ");
                accounts.add(toAccount(acData));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    @Override
    public Account readById(Long id) {
        Account account = null;
        try {
            List<String> accounts = Files.readAllLines(PATH, StandardCharsets.UTF_8);
            Optional<String> accountData= accounts.stream().filter(x -> x.startsWith(String.valueOf(id))).findAny();
            String[] separatedData;
            if(accountData.isPresent()){
                separatedData = accountData.get().replaceAll(",", " ").split(" ");
                account = toAccount(separatedData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return account;
    }

    @Override
    public Account update(Account account, Long id) {
        try {
            writer = new CSVWriter(new FileWriter(file, true));
            List<String> accounts = Files.readAllLines(PATH, StandardCharsets.UTF_8);
            Files.write(PATH, "".getBytes());
            String[] accountData;
            for (String line : accounts){
                if(line.startsWith(String.valueOf(id))){
                    line = (id + ", " + account.toString()).trim();
                    accountData = line.split(", ");
                } else {
                    accountData = line.split(",");
                }
                writer.writeNext(accountData, false);
                writer.flush();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return account;
    }

    @Override
    public void delete(Account account) {
        try{
            writer = new CSVWriter(new FileWriter(file, true));
            List<String> accounts = Files.readAllLines(PATH);
            Files.write(PATH, "".getBytes());
            for (String data : accounts){
                if (data.replaceFirst("^\\d,", "").equalsIgnoreCase(account.toString().replaceAll(" ", ""))) {
                    data = data.replaceFirst("ACTIVE|BANNED", "DELETED");
                }
                    String[] accountData = data.split(",");
                    writer.writeNext(accountData, false);
                    writer.flush();
            }
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteByID(Long id) {
        try{
            writer = new CSVWriter(new FileWriter(file, true));
            List<String> accounts = Files.readAllLines(PATH);
            Files.write(PATH, "".getBytes());
            accounts.stream().forEach(data -> {
                if (data.startsWith(String.valueOf(id))) {
                    data = data.replaceFirst("ACTIVE|BANNED", "DELETED");
                }
                String[] accountData = data.split(",");
                writer.writeNext(accountData, false);  // rewrite method
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.close();
        } catch (IOException e){
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

    private static Account toAccount(String[] acData){
        if(acData.length>=4) {
            long id = Long.parseLong(acData[0].trim());
            String firstName = acData[1].trim();
            String lastName = acData[2].trim();
            int age = Integer.parseInt(acData[3].trim());
            Account.AccountStatus status = null;
            if (acData[4].equals(Account.AccountStatus.ACTIVE.toString())) {
                status = Account.AccountStatus.ACTIVE;
            } else if (acData[4].equals(Account.AccountStatus.BANNED.toString())) {
                status = Account.AccountStatus.BANNED;
            } else if (acData[4].equals(Account.AccountStatus.DELETED.toString())) {
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
            return account;
        } else return null;
    }
}
