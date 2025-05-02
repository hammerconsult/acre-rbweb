/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrupoDoctoOficial;
import br.com.webpublico.enums.TipoSequenciaDoctoOficial;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.GrupoDoctoOficialFacade;
import br.com.webpublico.util.FacesUtil;
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

@ManagedBean(name = "grupoDoctoOficialControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoGrupoDoctoOficial", pattern = "/grupo-de-documento-oficial/novo/", viewId = "/faces/tributario/certidao/grupodocumento/edita.xhtml"),
        @URLMapping(id = "editarGrupoDoctoOficial", pattern = "/grupo-de-documento-oficial/editar/#{grupoDoctoOficialControlador.id}/", viewId = "/faces/tributario/certidao/grupodocumento/edita.xhtml"),
        @URLMapping(id = "listarGrupoDoctoOficial", pattern = "/grupo-de-documento-oficial/listar/", viewId = "/faces/tributario/certidao/grupodocumento/lista.xhtml"),
        @URLMapping(id = "verGrupoDoctoOficial", pattern = "/grupo-de-documento-oficial/ver/#{grupoDoctoOficialControlador.id}/", viewId = "/faces/tributario/certidao/grupodocumento/visualizar.xhtml")
})
public class GrupoDoctoOficialControlador extends PrettyControlador<GrupoDoctoOficial> implements Serializable, CRUD {

    @EJB
    private GrupoDoctoOficialFacade grupoDoctoOficialFacade;

    public GrupoDoctoOficialControlador() {
        super(GrupoDoctoOficial.class);
    }

    public GrupoDoctoOficialFacade getGrupoDoctoOficialFacade() {
        return grupoDoctoOficialFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/grupo-de-documento-oficial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return grupoDoctoOficialFacade;
    }

    @URLAction(mappingId = "novoGrupoDoctoOficial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        this.selecionado.setCodigo(grupoDoctoOficialFacade.ultimoCodigoMaisUm());
    }

    @URLAction(mappingId = "editarGrupoDoctoOficial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verGrupoDoctoOficial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<SelectItem> getTiposSequencia() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoSequenciaDoctoOficial object : TipoSequenciaDoctoOficial.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
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
            FacesUtil.addError("Atenção!", "Informe o Código.");
            retorno = false;
        } else if (selecionado.getId() == null && grupoDoctoOficialFacade.existeCodigo(selecionado.getCodigo())) {
            selecionado.setCodigo(grupoDoctoOficialFacade.ultimoCodigoMaisUm());
            FacesUtil.addWarn("Atenção!", "O Código informado já está em uso em outro registro. O sistema gerou um novo código. Por favor, pressione o botão SALVAR novamente.");
            retorno = false;
        } else if (selecionado.getId() != null && !grupoDoctoOficialFacade.existeCodigoGrupoDoctoOficial(selecionado)) {
            FacesUtil.addError("Atenção!", "O Código informado já existe.");
            retorno = false;
        }
        if (selecionado.getDescricao() == null || "".equals(selecionado.getDescricao())) {
            FacesUtil.addError("Atenção!", "Informe a Descrição.");
            retorno = false;
        }
        if (selecionado.getTipoSequencia() == null || "".equals(selecionado.getTipoSequencia())) {
            FacesUtil.addError("Atenção!", "Informe o Tipo de Sequência.");
            retorno = false;
        }
        return retorno;
    }
}
