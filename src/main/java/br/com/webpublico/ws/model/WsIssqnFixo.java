package br.com.webpublico.ws.model;

import java.io.Serializable;

public class WsIssqnFixo implements Serializable {
    private Integer ano;
    private String cpfCnpj;

    public WsIssqnFixo() {
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }
}
