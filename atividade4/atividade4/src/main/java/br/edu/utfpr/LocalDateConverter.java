package br.edu.utfpr;

import com.opencsv.bean.AbstractBeanField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter extends AbstractBeanField<String, LocalDate> {

    // TODO qual formato usar aqui?
    private static final String FORMATO = "dd/MM/yyyy";

    @Override
    public LocalDate convert(String value) {
        return LocalDate.parse(value, DateTimeFormatter.ofPattern(FORMATO));
    }
}
