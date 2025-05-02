package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoPontoComercial;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoPontoComercialFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "tipoPontoComercialControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoTipoPontoComercial", pattern = "/tipo-de-ponto-comercial/novo/", viewId = "/faces/tributario/rendaspatrimoniais/tipopontocomercial/edita.xhtml"),
        @URLMapping(id = "editarTipoPontoComercial", pattern = "/tipo-de-ponto-comercial/editar/#{tipoPontoComercialControlador.id}/", viewId = "/faces/tributario/rendaspatrimoniais/tipopontocomercial/edita.xhtml"),
        @URLMapping(id = "listarTipoPontoComercial", pattern = "/tipo-de-ponto-comercial/listar/", viewId = "/faces/tributario/rendaspatrimoniais/tipopontocomercial/lista.xhtml"),
        @URLMapping(id = "verTipoPontoComercial", pattern = "/tipo-de-ponto-comercial/ver/#{tipoPontoComercialControlador.id}/", viewId = "/faces/tributario/rendaspatrimoniais/tipopontocomercial/visualizar.xhtml")
})
public class TipoPontoComercialControlador extends PrettyControlador<TipoPontoComercial> implements Serializable, CRUD {

    @EJB
    private TipoPontoComercialFacade tipoPontoComercialFacade;

    public TipoPontoComercialControlador() {
        super(TipoPontoComercial.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-de-ponto-comercial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoPontoComercialFacade;
    }

    @URLAction(mappingId = "novoTipoPontoComercial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCodigo(tipoPontoComercialFacade.retornaUltimoCodigoLong());
    }

    @URLAction(mappingId = "verTipoPontoComercial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTipoPontoComercial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        selecionado.setCodigo(tipoPontoComercialFacade.retornaUltimoCodigoLong());
        if (validaCampos()) {
            super.salvar();
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getCodigo() == null) {
            retorno = false;
            FacesUtil.addError("Impossível Continuar", "O Campo Código deve ser Preenchido");
        }
        if (selecionado.getCodigo() == null || selecionado.getCodigo().intValue() <= 0) {
            retorno = false;
            FacesUtil.addError("Impossível Continuar", "O Código deve ser Maior que Zero");
        } else if (tipoPontoComercialFacade.jaExisteCodigo(selecionado)) {
            retorno = false;
            FacesUtil.addError("Impossível Continuar", "Código já Existente");
        }
        if (selecionado.getDescricao() == null || selecionado.getDescricao().isEmpty()) {
            retorno = false;
            FacesUtil.addError("Impossível Continuar", "O Campo Descrição deve ser Preenchido");
        } else if (tipoPontoComercialFacade.jaExisteDescricao(selecionado)) {
            retorno = false;
            FacesUtil.addError("Impossível Continuar", "Descrição já Existente");
        }
        return retorno;
    }
}
