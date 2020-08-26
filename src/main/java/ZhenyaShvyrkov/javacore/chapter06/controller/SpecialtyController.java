package main.java.ZhenyaShvyrkov.javacore.chapter06.controller;

import main.java.ZhenyaShvyrkov.javacore.chapter06.model.Specialty;
import main.java.ZhenyaShvyrkov.javacore.chapter06.repository.csv.CsvSpecialtyRepositoryImpl;

import java.util.List;

public class SpecialtyController extends Controller<Specialty> {
    private static SpecialtyController specialtyController;
    private static CsvSpecialtyRepositoryImpl csvSpecialtyRepository;

    private SpecialtyController() {
    }

    public static synchronized SpecialtyController getSpecialtyController() {
        if (specialtyController == null) {
            specialtyController = new SpecialtyController();
            csvSpecialtyRepository = CsvSpecialtyRepositoryImpl.getCsvSpecialtyRepository();
        }
        return specialtyController;
    }

    @Override
    public String[] handleRequest(String data) {
        data = data.trim();
        return data.split(", ");
    }

    @Override
    public Specialty create(Specialty specialty) {
        return csvSpecialtyRepository.save(specialty);
    }

    @Override
    public List<Specialty> read() {
        return csvSpecialtyRepository.read();
    }

    @Override
    public Specialty readById(Long id) {
        return csvSpecialtyRepository.readById(id);
    }

    @Override
    public Specialty update(Long id, Specialty specialty) {
        return csvSpecialtyRepository.update(specialty, id);
    }

    @Override
    public void delete(Specialty specialty) {
        csvSpecialtyRepository.delete(specialty);
    }

    @Override
    public void deleteByID(Long id) {
        csvSpecialtyRepository.deleteByID(id);
    }

}
