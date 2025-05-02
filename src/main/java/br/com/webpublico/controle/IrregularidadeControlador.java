package br.com.webpublico.controle;

import br.com.webpublico.entidades.Irregularidade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.IrregularidadeFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
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

@ManagedBean(name = "irregularidadeControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novaIrregularidade", pattern = "/irregularidade-de-vistoria/novo/",
                viewId = "/faces/tributario/alvara/tipoirregularidade/edita.xhtml"),

        @URLMapping(id = "editarIrregularidade", pattern = "/irregularidade-de-vistoria/editar/#{irregularidadeControlador.id}/",
                viewId = "/faces/tributario/alvara/tipoirregularidade/edita.xhtml"),

        @URLMapping(id = "listarIrregularidade", pattern = "/irregularidade-de-vistoria/listar/",
                viewId = "/faces/tributario/alvara/tipoirregularidade/lista.xhtml"),

        @URLMapping(id = "verIrregularidade", pattern = "/irregularidade-de-vistoria/ver/#{irregularidadeControlador.id}/",
                viewId = "/faces/tributario/alvara/tipoirregularidade/visualizar.xhtml")
})
public class IrregularidadeControlador extends PrettyControlador<Irregularidade> implements Serializable, CRUD {

    @EJB
    private IrregularidadeFacade irregularidadeFacade;

    public IrregularidadeControlador() {
        super(Irregularidade.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return irregularidadeFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/irregularidade-de-vistoria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaIrregularidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        this.selecionado.setCodigo(irregularidadeFacade.ultimoCodigoMaisUm());
    }

    @URLAction(mappingId = "verIrregularidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarIrregularidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    public List<SelectItem> getTipoDeIrregularidades() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Irregularidade.Tipo object : Irregularidade.Tipo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListSelectItemIrregularidadeParaAlvara() {
        return Util.getListSelectItem(irregularidadeFacade.listarEmOrdemAlfabeticaParaTipos(Irregularidade.Tipo.IRREGULARIDADE, Irregularidade.Tipo.SOLICITACAO));
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getCodigo() == null || selecionado.getCodigo() <= 0) {
            FacesUtil.addError("Atenção!", "Informe um Código maior que zero.");
            retorno = false;
        } else if (selecionado.getId() == null && irregularidadeFacade.existeCodigo(selecionado.getCodigo())) {
            selecionado.setCodigo(irregularidadeFacade.ultimoCodigoMaisUm());
            FacesUtil.addWarn("Impossível Continuar", "O Código informado já está em uso em outro Registro. Foi Calculado um Novo Código");
            retorno = false;
        } else if (selecionado.getId() != null && !irregularidadeFacade.existeCodigoTipoIrregularidade(selecionado)) {
            FacesUtil.addError("Atenção!", "O Código informado já existe.");
            retorno = false;
        }
        if (selecionado.getDescricao() == null || "".equals(selecionado.getDescricao())) {
            FacesUtil.addError("Atenção!", "Informe uma descrição.");
            retorno = false;
        }
        if (selecionado.getTipoDeIrregularidade() == null) {
            FacesUtil.addError("Atenção!", "Informe um tipo de irregularidade.");
            retorno = false;
        }
        return retorno;
    }
}
