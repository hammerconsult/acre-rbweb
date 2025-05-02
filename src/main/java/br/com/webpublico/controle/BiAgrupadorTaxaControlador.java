/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.BiAgrupadorTaxa;
import br.com.webpublico.entidades.BiAgrupadorTaxaTributo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BiAgrupadorTaxaFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Usuario
 */
@ManagedBean(name = "biAgrupadorTaxaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoBiAgrupador", pattern = "/exportacao-bi/tributario/agrupador/novo/",
        viewId = "/faces/bi-exportacao/agrupador/edita.xhtml"),

    @URLMapping(id = "editarBiAgrupador", pattern = "/exportacao-bi/tributario/agrupador/editar/#{biAgrupadorTaxaControlador.id}/",
        viewId = "/faces/bi-exportacao/agrupador/edita.xhtml"),

    @URLMapping(id = "listarBiAgrupador", pattern = "/exportacao-bi/tributario/agrupador/listar/",
        viewId = "/faces/bi-exportacao/agrupador/lista.xhtml"),

    @URLMapping(id = "verBiAgrupador", pattern = "/exportacao-bi/tributario/agrupador/ver/#{biAgrupadorTaxaControlador.id}/",
        viewId = "/faces/bi-exportacao/agrupador/visualizar.xhtml")})
public class BiAgrupadorTaxaControlador extends PrettyControlador<BiAgrupadorTaxa> implements Serializable, CRUD {

    @EJB
    private BiAgrupadorTaxaFacade biAgrupadorTaxaFacade;
    private BiAgrupadorTaxaTributo biAgrupadorTaxaTributo;

    public BiAgrupadorTaxaControlador() {
        super(BiAgrupadorTaxa.class);
        metadata = new EntidadeMetaData(BiAgrupadorTaxa.class);
    }

    public BiAgrupadorTaxaFacade getFacade() {
        return biAgrupadorTaxaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return biAgrupadorTaxaFacade;
    }

    public BiAgrupadorTaxaTributo getBiAgrupadorTaxaTributo() {
        return biAgrupadorTaxaTributo;
    }

    public void setBiAgrupadorTaxaTributo(BiAgrupadorTaxaTributo biAgrupadorTaxaTributo) {
        this.biAgrupadorTaxaTributo = biAgrupadorTaxaTributo;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/exportacao-bi/tributario/agrupador/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoBiAgrupador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        selecionado = (BiAgrupadorTaxa) Web.pegaDaSessao(BiAgrupadorTaxa.class);
        if (selecionado == null) {
            super.novo();
            selecionado.setCodigo(biAgrupadorTaxaFacade.buscarUltimoNumeroMaisUm());
        }
        biAgrupadorTaxaTributo = new BiAgrupadorTaxaTributo();
    }

    @URLAction(mappingId = "verBiAgrupador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarBiAgrupador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        selecionado = (BiAgrupadorTaxa) Web.pegaDaSessao(BiAgrupadorTaxa.class);
        if (selecionado == null) {
            super.editar();
        }
        biAgrupadorTaxaTributo = new BiAgrupadorTaxaTributo();
    }

    public void adicionarTributo() {
        try {
            validarTributo();
            biAgrupadorTaxaTributo.setBiAgrupadorTaxa(selecionado);
            selecionado.getTributos().add(biAgrupadorTaxaTributo);
            biAgrupadorTaxaTributo = new BiAgrupadorTaxaTributo();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void removerTributo(BiAgrupadorTaxaTributo biAgrupadorTaxaTributo) {
        try {
            selecionado.getTributos().remove(biAgrupadorTaxaTributo);
        } catch (Exception e) {
            FacesUtil.addError("Operação Não Permitida", e.getMessage());
        }
    }

    public void validarTributo() {
        ValidacaoException ve = new ValidacaoException();
        if (biAgrupadorTaxaTributo.getTributo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Tributo deve ser informado!");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCodigo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo código deve ser informado!");
        }
        if (selecionado.getDescricao().trim().equals("")) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo descrição deve ser informado!");
        }
        ve.lancarException();
    }
}
