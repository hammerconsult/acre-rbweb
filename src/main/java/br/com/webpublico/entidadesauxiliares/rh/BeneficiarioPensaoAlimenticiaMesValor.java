package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.enums.Mes;

import java.util.HashMap;

public class BeneficiarioPensaoAlimenticiaMesValor {

    private String cpf;
    private String dataNascimento;
    private String nome;
    private String grauParentesco;
    private HashMap<Mes, String> valoresMesRTPA;
    private HashMap<Mes, String> valoresMesESPA;
    private String decimoTerceiroRTPA;
    private String decimoTerceiroESPA;

    public BeneficiarioPensaoAlimenticiaMesValor() {
        this.valoresMesRTPA = new HashMap<>();
        this.valoresMesESPA = new HashMap<>();
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGrauParentesco() {
        return grauParentesco;
    }

    public void setGrauParentesco(String grauParentesco) {
        this.grauParentesco = grauParentesco;
    }

    public HashMap<Mes, String> getValoresMesRTPA() {
        return valoresMesRTPA;
    }

    public void setValoresMesRTPA(HashMap<Mes, String> valoresMesRTPA) {
        this.valoresMesRTPA = valoresMesRTPA;
    }

    public HashMap<Mes, String> getValoresMesESPA() {
        return valoresMesESPA;
    }

    public void setValoresMesESPA(HashMap<Mes, String> valoresMesESPA) {
        this.valoresMesESPA = valoresMesESPA;
    }

    public String getDecimoTerceiroRTPA() {
        return decimoTerceiroRTPA;
    }

    public void setDecimoTerceiroRTPA(String decimoTerceiroRTPA) {
        this.decimoTerceiroRTPA = decimoTerceiroRTPA;
    }

    public String getDecimoTerceiroESPA() {
        return decimoTerceiroESPA;
    }

    public void setDecimoTerceiroESPA(String decimoTerceiroESPA) {
        this.decimoTerceiroESPA = decimoTerceiroESPA;
    }
}
