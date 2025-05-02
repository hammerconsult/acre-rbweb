/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class ExoneracaoRescisaoPesquisaGenericaControlador extends PesquisaGenericaRHControlador implements Serializable {

    public ExoneracaoRescisaoPesquisaGenericaControlador() {
        setNomeVinculo("obj.vinculoFP");
    }

    @Override
    public void getCampos() {
        super.getCampos();
        adicionaItemPesquisaGenerica("Nome Tratamento/Social do Servidor", "obj.vinculoFP.matriculaFP.pessoa.nomeTratamento", String.class, Boolean.FALSE);
    }

}
