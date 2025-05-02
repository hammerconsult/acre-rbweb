/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrauEscolaridadeSiprev;
import br.com.webpublico.entidades.NivelEscolaridade;
import br.com.webpublico.enums.GrauEscolaridadeDependente;
import br.com.webpublico.enums.GrauInstrucaoCAGED;
import br.com.webpublico.enums.rh.GrauEscolaridadeSICAP;
import br.com.webpublico.enums.rh.esocial.GrauEscolaridadeESocial;
import br.com.webpublico.enums.rh.previdencia.GrauInstrucaoBBPrev;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.GrauEscolaridadeSiprevFacade;
import br.com.webpublico.negocios.NivelEscolaridadeFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;
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

/**
 * @author Usuário
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoNivelEscolaridade", pattern = "/nivel-escolaridade/novo/", viewId = "/faces/tributario/cadastromunicipal/nivelescolaridade/edita.xhtml"),
        @URLMapping(id = "editarNivelEscolaridade", pattern = "/nivel-escolaridade/editar/#{nivelEscolaridadeControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/nivelescolaridade/edita.xhtml"),
        @URLMapping(id = "listarEscolaridade", pattern = "/nivel-escolaridade/listar/", viewId = "/faces/tributario/cadastromunicipal/nivelescolaridade/lista.xhtml"),
        @URLMapping(id = "verNivelEscolaridade", pattern = "/nivel-escolaridade/ver/#{nivelEscolaridadeControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/nivelescolaridade/visualizar.xhtml")
})
public class NivelEscolaridadeControlador extends PrettyControlador<NivelEscolaridade> implements Serializable, CRUD {

    @EJB
    private NivelEscolaridadeFacade facade;
    @EJB
    private GrauEscolaridadeSiprevFacade grauEscolaridadeSiprevFacade;
    private ConverterGenerico converterGrauEscolaridadeSiprev;
    private ConverterGenerico converterGrauEscolaridadeRPPS;

    public NivelEscolaridadeControlador() {
        metadata = new EntidadeMetaData(NivelEscolaridade.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public NivelEscolaridadeFacade getFacade() {
        return facade;

    }

    public List<SelectItem> getGrauInstrucaoCAGED() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (GrauInstrucaoCAGED object : GrauInstrucaoCAGED.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getGrauEscolaridadeSiprev() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (GrauEscolaridadeSiprev object : grauEscolaridadeSiprevFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getCodigo() + " - " + object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getGrauEscolaridadeDependente() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (GrauEscolaridadeDependente object : GrauEscolaridadeDependente.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getGrauEscolaridadeSICAP() {
        return Util.getListSelectItem(GrauEscolaridadeSICAP.values(), false);
    }

    public List<SelectItem> getGrauEscolaridadeESocial() {
        return Util.getListSelectItem(GrauEscolaridadeESocial.values(), false);
    }

    public List<SelectItem> getGrauInstrucaoBBPrev() {
        return Util.getListSelectItem(GrauInstrucaoBBPrev.values());
    }

    public ConverterGenerico getConverterGrauEscolaridadeSiprev() {
        if (converterGrauEscolaridadeSiprev == null) {
            converterGrauEscolaridadeSiprev = new ConverterGenerico(GrauEscolaridadeSiprev.class, grauEscolaridadeSiprevFacade);
        }
        return converterGrauEscolaridadeSiprev;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível salvar. Detalhes do erro: " + e.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();

        if (selecionado.getOrdem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo ordem é obrigatório.");
        } else {
            if (facade.existeNivelEscolaridadeOrdem(selecionado)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida( "Já existe um nível de escolaridade com a ordem '" + selecionado.getOrdem() + "'.");
            }
        }
        if (Strings.isNullOrEmpty(selecionado.getDescricao())) {
            ve.adicionarMensagemDeCampoObrigatorio( "O campo descrição é obrigatório.");
        } else {
            if (facade.existeNivelEscolaridadeDescricao(selecionado)) {
               ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um nível de escolaridade com a descrição '" + selecionado.getDescricao() + "'.");
            }
        }
        if (selecionado.getGrauInstrucaoCAGED() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo grau de instrução do CAGED é obrigatório.");
        }
        if (selecionado.getGrauEscolaridadeSiprev() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo grau de escolaridade SIPREV é obrigatório.");
        }
        if (selecionado.getGrauEscolaridadeESocial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo grau de escolaridade E-Social é obrigatório.");
        }
        if (selecionado.getGrauInstrucaoBBPrev() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo grau de instrução BBPrev é obrigatório.");
        }
        ve.lancarException();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nivel-escolaridade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoNivelEscolaridade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "verNivelEscolaridade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarNivelEscolaridade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }
}
