/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.SituacaoTransfMovimentoPessoa;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Edi
 */
@ManagedBean
@ViewScoped
public class ParecerTransferenciaMovPessoaPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getHqlConsulta() {
        return super.getHqlConsulta();
    }

    @Override
    public String getComplementoQuery() {
        return " where obj.situacao = '" + SituacaoTransfMovimentoPessoa.ABERTA + "'"
            + "  and " + montaCondicao() + montaOrdenacao();
    }
}
