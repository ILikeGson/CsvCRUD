package main.java.ZhenyaShvyrkov.javacore.chapter06.repository.csv;

import com.opencsv.CSVWriter;
import main.java.ZhenyaShvyrkov.javacore.chapter06.model.Specialty;
import main.java.ZhenyaShvyrkov.javacore.chapter06.repository.SpecialtyRepository;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CsvSpecialtyRepositoryImpl implements SpecialtyRepository {
    private static CsvSpecialtyRepositoryImpl csvSpecialtyRepository;
    private static final Path PATH = Paths.get("specialty.csv");
    private static final File file = PATH.toFile();
    private static long id;

    private CsvSpecialtyRepositoryImpl() {}

    public static synchronized  CsvSpecialtyRepositoryImpl getCsvSpecialtyRepository() {
        if (csvSpecialtyRepository == null) {
            csvSpecialtyRepository = new CsvSpecialtyRepositoryImpl();
            if (!file.exists()){
                try {
                    Files.write(PATH, "".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return csvSpecialtyRepository;
    }

    @Override
    public Specialty save(Specialty specialty) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(file, true));
            id = findMaxId();
            specialty.setId(++id);
            writer.writeNext(new String[] {String.valueOf(id), specialty.getName()}, false);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return specialty;
    }

    @Override
    public List<Specialty> read() {
        List<Specialty> specialties = new ArrayList<>();
        try {
            List<String> specialtyData = Files.readAllLines(PATH, StandardCharsets.UTF_8);
            specialtyData.forEach(line -> {
                String[] data = line.split(",");
                id = Long.parseLong(data[0]);
                String name = data[1];
                Specialty specialty = new Specialty(name);
                specialty.setId(id);
                specialties.add(specialty);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return specialties;
    }

    @Override
    public Specialty readById(Long id) {
        Specialty specialty = null;
        try {
            List<String> specialties = Files.readAllLines(PATH, StandardCharsets.UTF_8);
            Optional<String> specialtyName = specialties.stream().filter(line -> line.startsWith(String.valueOf(id))).findAny();
            if (specialtyName.isPresent()) {
                specialty = new Specialty(specialtyName.get().replaceFirst("\\d,", ""));
                specialty.setId(id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return specialty;
    }

    @Override
    public Specialty update(Specialty specialty, Long id) {
        try {
            List<String> fileData = Files.readAllLines(PATH, StandardCharsets.UTF_8);
            Files.write(PATH, "".getBytes());
            CSVWriter writer = new CSVWriter(new FileWriter(file, true));
            String[] specialtyData;
            for (String line : fileData) {
                if (line.startsWith(String.valueOf(id))) {
                    specialtyData = (id + "," + specialty.getName()).split(",");
                } else {
                    specialtyData = line.split(",");
                }
                specialty.setId(Long.parseLong(specialtyData[0]));
                writer.writeNext(specialtyData, false);
                writer.flush();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return specialty;
    }

    @Override
    public void delete(Specialty specialty) {
        try {
            List<String> fileData = Files.readAllLines(PATH, StandardCharsets.UTF_8);
            Files.write(PATH, "".getBytes());
            CSVWriter writer = new CSVWriter(new FileWriter(file, true));
            String[] specialtyData;
            for (String line : fileData) {
                if (!line.replaceFirst("\\d,", "").equalsIgnoreCase(specialty.getName())) {
                    specialtyData = line.split(",");
                    writer.writeNext(specialtyData, false);
                    writer.flush();
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteByID(Long id) {
        try {
            List<String> specialties = Files.readAllLines(PATH, StandardCharsets.UTF_8);
            CSVWriter writer = new CSVWriter(new FileWriter(file, true));
            Files.write(PATH, "".getBytes());
            String[] fileData;
            for (String line : specialties) {
                if (!line.startsWith(String.valueOf(id))) {
                    fileData = line.split(",");
                    writer.writeNext(fileData, false);
                    writer.flush();
                }
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
}
