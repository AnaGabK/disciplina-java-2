package br.edu.utfpr;

import br.edu.utfpr.model.FormaPagamento;
import com.opencsv.bean.AbstractBeanField;

public class FormaPagamentoConverter extends AbstractBeanField<String, FormaPagamento> {

    @Override
    public FormaPagamento convert(String value) {
        return FormaPagamento.doTexto(value);
    }
}
