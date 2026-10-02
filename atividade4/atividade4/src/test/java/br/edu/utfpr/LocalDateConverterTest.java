package br.edu.utfpr;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalDateConverterTest {

    @Test
    void assert_convertePrimeiroDeJaneiroDeDoisMilEDez() {
        assertThat(new LocalDateConverter().convert("01/01/2010"))
                .isEqualTo(LocalDate.of(2010, 1, 1));
    }

    @Test
    void assert_converteTrintaEUmDeDezembroDeDoisMilEQuatorze() {
        assertThat(new LocalDateConverter().convert("31/12/2014"))
                .isEqualTo(LocalDate.of(2014, 12, 31));
    }
}
