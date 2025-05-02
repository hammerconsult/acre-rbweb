/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.enums.TipoProposta;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * @author Usuario
 */
@ManagedBean
@ViewScoped
public class ContabilizaDiariaPesquisaGenerico extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String montaCondicao() {
        String condicao = super.montaCondicao();
        condicao += " and propostaConcessaoDiaria.tipoProposta <> '" + TipoProposta.SUPRIMENTO_FUNDO.name() + "'";
        return condicao;
    }

    @Override
    public String nomeDaClasse() {
        return "Diária Contabilização";
    }


}
