/*
 * Codigo gerado automaticamente em Tue Apr 03 16:26:00 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoPenalidadeFP;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoPenalidadeFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "tipoPenalidadeControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoTipoPenalidadeFP", pattern = "/tipo-penalidade/novo/", viewId = "/faces/rh/administracaodepagamento/tipopenalidade/edita.xhtml"),
        @URLMapping(id = "editarTipoPenalidadeFP", pattern = "/tipo-penalidade/editar/#{tipoPenalidadeControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipopenalidade/edita.xhtml"),
        @URLMapping(id = "listarTipoPenalidadeFP", pattern = "/tipo-penalidade/listar/", viewId = "/faces/rh/administracaodepagamento/tipopenalidade/lista.xhtml"),
        @URLMapping(id = "verTipoPenalidadeFP", pattern = "/tipo-penalidade/ver/#{tipoPenalidadeControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipopenalidade/visualizar.xhtml")
})
public class TipoPenalidadeControlador extends PrettyControlador<TipoPenalidadeFP> implements Serializable, CRUD {

    @EJB
    private TipoPenalidadeFacade tipoPenalidadeFacade;

    public TipoPenalidadeControlador() {
        super(TipoPenalidadeFP.class);
    }

    public TipoPenalidadeFacade getFacade() {
        return tipoPenalidadeFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoPenalidadeFacade;
    }

    public TipoPenalidadeFP getTipoPenalidadeFP() {
        return (TipoPenalidadeFP) selecionado;
    }

    @URLAction(mappingId = "editarTipoPenalidadeFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verTipoPenalidadeFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "novoTipoPenalidadeFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        Long novoCodigo = tipoPenalidadeFacade.retornaUltimoCodigoLong();
        selecionado.setCodigo(novoCodigo + "");
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            if (operacao == Operacoes.NOVO) {
                Long novoCodigo = tipoPenalidadeFacade.retornaUltimoCodigoLong();
                if (!novoCodigo.equals(selecionado.getCodigo())) {
                    FacesUtil.addInfo("Atenção!", "O Código " + getSelecionado().getCodigo() + " já está sendo usado e foi gerado o novo código " + novoCodigo + " !");
                    selecionado.setCodigo(novoCodigo+"");
                }
            }
            super.salvar();
        }
    }

    public Boolean validaCampos() {
        if (tipoPenalidadeFacade.existeCodigo(getTipoPenalidadeFP())) {
            FacesUtil.addMessageWarn("Atenção", "O Código do Tipo de Penalidade já existe em um outro tipo de penalidade!");
            return false;
        }

        return true;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-penalidade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
