/*
 * Codigo gerado automaticamente em Tue Nov 20 10:39:10 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.enums.SituacaoCadastralContabil;
import br.com.webpublico.entidades.TipoConciliacao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoConciliacaoFacade;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "tipoConciliacaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-tipo-de-conciliacao",   pattern = "/conciliacao/tipo-de-conciliacao/novo/",                                    viewId = "/faces/financeiro/conciliacao/tipoconciliacao/edita.xhtml"),
        @URLMapping(id = "editar-tipo-de-conciliacao", pattern = "/conciliacao/tipo-de-conciliacao/editar/#{tipoConciliacaoControlador.id}/", viewId = "/faces/financeiro/conciliacao/tipoconciliacao/edita.xhtml"),
        @URLMapping(id = "ver-tipo-de-conciliacao",    pattern = "/conciliacao/tipo-de-conciliacao/ver/#{tipoConciliacaoControlador.id}/",    viewId = "/faces/financeiro/conciliacao/tipoconciliacao/visualizar.xhtml"),
        @URLMapping(id = "listar-tipo-de-conciliacao", pattern = "/conciliacao/tipo-de-conciliacao/listar/",                                  viewId = "/faces/financeiro/conciliacao/tipoconciliacao/lista.xhtml")
})
public class TipoConciliacaoControlador extends PrettyControlador<TipoConciliacao> implements Serializable, CRUD {

    @EJB
    private TipoConciliacaoFacade tipoConciliacaoFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public TipoConciliacaoControlador() {
        super(TipoConciliacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoConciliacaoFacade;
    }

    public TipoConciliacaoFacade getFacade() {
        return tipoConciliacaoFacade;
    }


    @Override
    public String getCaminhoPadrao() {
        return "/conciliacao/tipo-de-conciliacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "ver-tipo-de-conciliacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-tipo-de-conciliacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }


    @Override
    @URLAction(mappingId = "novo-tipo-de-conciliacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataLancamento(sistemaControlador.getDataOperacao());
    }

    public void recuperarEditarVer() {
        selecionado = tipoConciliacaoFacade.recuperar(((TipoConciliacao) selecionado).getId());
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            try {
                if (operacao == Operacoes.NOVO) {
                    selecionado.setNumero(tipoConciliacaoFacade.getUltimoNumeroMaisUm());
                    tipoConciliacaoFacade.salvarNovo(selecionado);
                } else {
                    tipoConciliacaoFacade.salvar(selecionado);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Operação Realizada! ", " Registro salvo com sucesso. "));
                redireciona();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operação não Realizada! ", e.getMessage()));
            }
        }
    }

    public List<SelectItem> listaSituacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (SituacaoCadastralContabil scc : SituacaoCadastralContabil.values()) {
            toReturn.add(new SelectItem(scc, scc.getDescricao()));
        }
        return toReturn;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
