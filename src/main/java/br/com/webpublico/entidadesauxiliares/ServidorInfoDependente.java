package br.com.webpublico.entidadesauxiliares;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 02/06/2017.
 */
public class ServidorInfoDependente {

    private Integer item;
    private String dependente;
    private Date nacimento;
    private String idade;
    private String tipoDependente;
    private List<ServidorInfoDependente> tiposDependentes;

    public ServidorInfoDependente() {
        tiposDependentes = new ArrayList<>();
    }

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public String getDependente() {
        return dependente;
    }

    public void setDependente(String dependente) {
        this.dependente = dependente;
    }

    public Date getNacimento() {
        return nacimento;
    }

    public void setNacimento(Date nacimento) {
        this.nacimento = nacimento;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getTipoDependente() {
        return tipoDependente;
    }

    public void setTipoDependente(String tipoDependente) {
        this.tipoDependente = tipoDependente;
    }

    public List<ServidorInfoDependente> getTiposDependentes() {
        return tiposDependentes;
    }

    public void setTiposDependentes(List<ServidorInfoDependente> tiposDependentes) {
        this.tiposDependentes = tiposDependentes;
    }
}
