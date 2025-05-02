package br.com.webpublico.entidadesauxiliares;

import com.google.common.base.Strings;

public class EnderecoLocalEstoque {

    private String logradouro;
    private String numero;
    private String bairro;
    private String complemento;
    private String cep;

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }


    public String getEnderecoCompleto() {
        StringBuilder sb = new StringBuilder();
        if (!Strings.isNullOrEmpty(logradouro)) {
            sb.append(logradouro).append(" ");
        }
        if (!Strings.isNullOrEmpty(numero)) {
            sb.append("nº ").append(numero).append(", ");
        }
        if (!Strings.isNullOrEmpty(bairro)) {
            sb.append("Bairro: ").append(bairro).append(", ");
        }
        if (!Strings.isNullOrEmpty(cep)) {
            sb.append("CEP: ").append(cep).append(", ");
        }
        if (!Strings.isNullOrEmpty(complemento)) {
            sb.append(complemento).append(", ");
        }
        if (sb.toString().endsWith(", ")) {
            sb.replace(sb.toString().length() - 2, sb.toString().length(), "");
        }
        return sb.toString();
    }
}
