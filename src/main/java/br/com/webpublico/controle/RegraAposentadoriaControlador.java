package br.com.webpublico.controle;

import br.com.webpublico.entidades.RegraAposentadoria;
import br.com.webpublico.entidades.TipoAposentadoria;
import br.com.webpublico.enums.TipoReajusteAposentadoria;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.RegraAposentadoriaFacade;
import br.com.webpublico.negocios.TipoAposentadoriaFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "regraAposentadoriaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRegraAposentadoria", pattern = "/regra-aposentadoria/novo/", viewId = "/faces/rh/administracaodepagamento/regraaposentadoria/edita.xhtml"),
        @URLMapping(id = "editarRegraAposentadoria", pattern = "/regra-aposentadoria/editar/#{regraAposentadoriaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/regraaposentadoria/edita.xhtml"),
        @URLMapping(id = "listarRegraAposentadoria", pattern = "/regra-aposentadoria/listar/", viewId = "/faces/rh/administracaodepagamento/regraaposentadoria/lista.xhtml"),
        @URLMapping(id = "verRegraAposentadoria", pattern = "/regra-aposentadoria/ver/#{regraAposentadoriaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/regraaposentadoria/visualizar.xhtml")
})
public class RegraAposentadoriaControlador extends PrettyControlador<RegraAposentadoria> implements Serializable, CRUD {

    @EJB
    private RegraAposentadoriaFacade regraAposentadoriaFacade;
    @EJB
    private TipoAposentadoriaFacade tipoAposentadoriaFacade;
    private ConverterGenerico converterTipoAposentadoria;
    boolean podeEditar = true;


    public RegraAposentadoriaControlador() {
        super(RegraAposentadoria.class);
    }

    @URLAction(mappingId = "novoRegraAposentadoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }
    @URLAction(mappingId = "verRegraAposentadoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }
    @URLAction(mappingId = "editarRegraAposentadoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        try {
            recuperarObjeto();
            validarForeinKeysComRegistro(selecionado);
            super.editar();
        } catch (ValidacaoException ve) {
            podeEditar = false;
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public boolean isPodeEditar() {
        return podeEditar;
    }

    public void setPodeEditar(boolean podeEditar) {
        this.podeEditar = podeEditar;
    }

    @Override
    public void excluir() {
        super.excluir();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/regra-aposentadoria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return regraAposentadoriaFacade;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(selecionado.getDescricao())) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o campo a 'Descrição' !");
        }
        if (selecionado.getTipoAposentadoria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o campo 'Tipo de Aposentadoria' !");
        }
        if (selecionado.getTipoReajusteAposentadoria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o campo 'Tipo de Reajuste da Aposentadoria' !");
        }
        ve.lancarException();
    }


    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<SelectItem> getTipoAposentadoria() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAposentadoria object : tipoAposentadoriaFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterTipoAposentadoria() {
        if (converterTipoAposentadoria == null) {
            converterTipoAposentadoria = new ConverterGenerico(TipoAposentadoria.class, tipoAposentadoriaFacade);
        }
        return converterTipoAposentadoria;
    }

    public List<SelectItem> getTipoReajusteAposentadoria() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoReajusteAposentadoria object : TipoReajusteAposentadoria.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRegraAposentadorias() {
        return Util.getListSelectItem(regraAposentadoriaFacade.lista());
    }

}
