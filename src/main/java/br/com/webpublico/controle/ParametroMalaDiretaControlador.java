/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ParametroMalaDireta;
import br.com.webpublico.enums.TagMalaDireta;
import br.com.webpublico.enums.TipoMalaDireta;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParametroMalaDiretaFacade;
import br.com.webpublico.util.FacesUtil;
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

@ManagedBean(name = "parametroMalaDiretaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParametroMalaDireta", pattern = "/parametro-mala-direta/novo/", viewId = "/faces/tributario/parametromaladireta/edita.xhtml"),
    @URLMapping(id = "editarParametroMalaDireta", pattern = "/parametro-mala-direta/editar/#{parametroMalaDiretaControlador.id}/", viewId = "/faces/tributario/parametromaladireta/edita.xhtml"),
    @URLMapping(id = "listarParametroMalaDireta", pattern = "/parametro-mala-direta/listar/", viewId = "/faces/tributario/parametromaladireta/lista.xhtml"),
    @URLMapping(id = "verParametroMalaDireta", pattern = "/parametro-mala-direta/ver/#{parametroMalaDiretaControlador.id}/", viewId = "/faces/tributario/parametromaladireta/visualizar.xhtml")
})
public class ParametroMalaDiretaControlador extends PrettyControlador<ParametroMalaDireta> implements Serializable, CRUD {

    @EJB
    private ParametroMalaDiretaFacade parametroMalaDiretaFacade;

    public ParametroMalaDiretaControlador() {
        super(ParametroMalaDireta.class);
    }

    @URLAction(mappingId = "novoParametroMalaDireta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoMalaDireta(TipoMalaDireta.NOTIFICACAO);
    }

    @URLAction(mappingId = "verParametroMalaDireta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarParametroMalaDireta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametro-mala-direta/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return parametroMalaDiretaFacade;
    }

    public List<SelectItem> getTags() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TagMalaDireta object : TagMalaDireta.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar a descrição!");
        }
        if (selecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o início de vigência!");
        }
        if (selecionado.getTipoMalaDireta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o tipo da mala direta!");
        }
        if (selecionado.getCabecalho() == null || selecionado.getCabecalho().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o cabeçalho do conteúdo!");
        }
        if (selecionado.getCorpo() == null || selecionado.getCorpo().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o corpo do conteúdo!");
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
}
