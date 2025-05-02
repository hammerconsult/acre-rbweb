/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 * @author Usuario
 */
@ManagedBean
@ViewScoped
public class ReceitaLoaPesquisaGenerico extends ComponentePesquisaGenerico implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    @Override
    public String montaCondicao() {
        String condicao = super.montaCondicao();
        condicao += "and obj.ldo.exercicio.ano = " + sistemaControlador.getExercicioCorrente().getAno() + "";
        return condicao;
    }

    @Override
    public String nomeDaClasse() {
        return "Previsão Inicial da Receita";
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

}
