package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class ConsultaBeneficiarioProvaDeVidaVO implements Serializable {

    private String matContNome;
    private String cpf;
    private String dataNascimento;
    private String tipoBeneficiario ;

    public ConsultaBeneficiarioProvaDeVidaVO(){
    }
    public ConsultaBeneficiarioProvaDeVidaVO(String matContNome, String cpf, String dataNascimento, String tipoBeneficiario) {
        this.matContNome = matContNome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.tipoBeneficiario = tipoBeneficiario;
    }


    public String getMatContNome() {
        return matContNome;
    }

    public void setMatContNome(String matContNome) {
        this.matContNome = matContNome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTipoBeneficiario() {
        return tipoBeneficiario;
    }

    public void setTipoBeneficiario(String tipoBeneficiario) {
        this.tipoBeneficiario = tipoBeneficiario;
    }


}
