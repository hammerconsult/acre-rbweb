/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ReferenciaFP;
import br.com.webpublico.enums.TipoReferenciaFP;
import br.com.webpublico.entidades.TipoRegime;
import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Claudio
 */
@ManagedBean
@SessionScoped
public class ConfiguracaoFaltasInjustificadasPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

//    @Override
//    public void getCampos() {
//        super.adicionaNovoCampoPesquisa();
//        adicionaItemPesquisaGenerica("Início de Vigência", "inicioVigencia", Date.class, Boolean.FALSE);
//        adicionaItemPesquisaGenerica("Final de Vigência", "finalVigencia", Date.class, Boolean.FALSE);
//        adicionaItemPesquisaGenerica("Tipo de Regime", "tipoRegime.descricao", TipoRegime.class, Boolean.TRUE);
//        adicionaItemPesquisaGenerica("Código Referência FP", "referenciaFP.codigo", ReferenciaFP.class, Boolean.TRUE);
//        adicionaItemPesquisaGenerica("Descrição Referência FP", "referenciaFP.descricao", ReferenciaFP.class, Boolean.TRUE);
//        adicionaItemPesquisaGenerica("Tipo Referência FP", "referenciaFP.tipoReferenciaFP", TipoReferenciaFP.class, Boolean.TRUE, Boolean.TRUE);
//    }

}
