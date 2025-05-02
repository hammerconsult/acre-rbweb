package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class BeneficiarioProvaDeVidaDTO implements Serializable {

    private String cpf;
    private String nome;
    private String dataNascimento;

    public BeneficiarioProvaDeVidaDTO(){
    }
    public BeneficiarioProvaDeVidaDTO(String cpf, String nome, String dataNascimento) {
        this.cpf = cpf;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }


}
