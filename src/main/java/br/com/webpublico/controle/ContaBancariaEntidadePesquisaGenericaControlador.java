/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Renato Romanini
 */
@ManagedBean
@ViewScoped
public class ContaBancariaEntidadePesquisaGenericaControlador extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getHqlConsulta() {
        return "select new br.com.webpublico.entidades.ContaBancariaEntidade "
                + " (obj.id, obj.agencia.banco.numeroBanco, "
                + "  obj.agencia.numeroAgencia ||' - '|| obj.agencia.digitoVerificador,"
                + "  obj.numeroConta ||' - '|| obj.digitoVerificador, obj.nomeConta, "
                + "  obj.tipoContaBancaria, obj.situacao, obj.dataAbertura, obj.dataEncerramento)"
                + " from " + classe.getSimpleName() + " obj ";
    }

    @Override
    public void getCampos() {
        super.getCampos();
        for (ItemPesquisaGenerica itemPesquisaGenerica : itens) {
            if (itemPesquisaGenerica.getCondicao().equals("obj.numeroBanco")) {
                itemPesquisaGenerica.setCondicao("agencia.banco.numeroBanco");
            }
            if (itemPesquisaGenerica.getCondicao().equals("obj.numeroDigitoAgencia")) {
                itemPesquisaGenerica.setCondicao("agencia.numeroAgencia");
            }
            if (itemPesquisaGenerica.getCondicao().equals("obj.numeroDigitoConta")) {
                itemPesquisaGenerica.setCondicao("obj.numeroConta");
            }
        }
        for (ItemPesquisaGenerica itemPesquisaGenerica : itensOrdenacao) {
            if (itemPesquisaGenerica.getCondicao().equals("obj.numeroBanco")) {
                itemPesquisaGenerica.setCondicao("agencia.banco.numeroBanco");
            }
            if (itemPesquisaGenerica.getCondicao().equals("numeroDigitoAgencia")) {
                itemPesquisaGenerica.setCondicao("agencia.numeroAgencia");
            }
            if (itemPesquisaGenerica.getCondicao().equals("obj.numeroDigitoConta")) {
                itemPesquisaGenerica.setCondicao("obj.numeroConta");
            }
        }
    }
}
