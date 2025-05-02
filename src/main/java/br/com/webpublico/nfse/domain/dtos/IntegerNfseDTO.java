package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;

public class IntegerNfseDTO implements Serializable {

    private Integer value;

    public IntegerNfseDTO() {
    }

    public IntegerNfseDTO(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
