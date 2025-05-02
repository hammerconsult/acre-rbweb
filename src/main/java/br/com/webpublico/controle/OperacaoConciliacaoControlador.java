/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.OperacaoConciliacao;
import br.com.webpublico.enums.SituacaoCadastralContabil;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.OperacaoConciliacaoFacade;
import br.com.webpublico.util.EntidadeMetaData;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Renato
 */
@ManagedBean
@SessionScoped
public class OperacaoConciliacaoControlador extends SuperControladorCRUD {

    @EJB
    private OperacaoConciliacaoFacade operacaoConciliacaoFacade;

    @Override
    public AbstractFacade getFacede() {
        return operacaoConciliacaoFacade;
    }

    public OperacaoConciliacaoControlador() {
        metadata = new EntidadeMetaData(OperacaoConciliacao.class);
    }

    public List<SelectItem> getSituacoesOperacaoConciliacao() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        retorno.add(new SelectItem(SituacaoCadastralContabil.ATIVO, SituacaoCadastralContabil.ATIVO.getDescricao()));
        retorno.add(new SelectItem(SituacaoCadastralContabil.INATIVO, SituacaoCadastralContabil.INATIVO.getDescricao()));
        return retorno;
    }

    @Override
    public String salvar() {
        if (((OperacaoConciliacao) selecionado).getId() == null) {
            ((OperacaoConciliacao) selecionado).setNumero(operacaoConciliacaoFacade.retornaUltimoCodigoLong());
        }
        return super.salvar();
    }
}
