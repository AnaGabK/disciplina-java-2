package br.edu.utfpr;

import br.edu.utfpr.model.Status;
import com.opencsv.bean.AbstractBeanField;

public class StatusConverter extends AbstractBeanField<String, Status> {

    @Override
    protected Status convert(String value) {
        return Status.doTexto(value);
    }
}
