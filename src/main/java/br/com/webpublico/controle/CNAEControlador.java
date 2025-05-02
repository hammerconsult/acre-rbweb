/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CNAEFacade;
import br.com.webpublico.negocios.LotacaoVistoriadoraFacade;
import br.com.webpublico.negocios.TipoVistoriaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

/**
 * @author gustavo
 */
@ManagedBean(name = "cNAEControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCNAE", pattern = "/cnae/novo/", viewId = "/faces/tributario/cadastromunicipal/cnae/edita.xhtml"),
    @URLMapping(id = "editarCNAE", pattern = "/cnae/editar/#{cNAEControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cnae/edita.xhtml"),
    @URLMapping(id = "listarCNAE", pattern = "/cnae/listar/", viewId = "/faces/tributario/cadastromunicipal/cnae/lista.xhtml"),
    @URLMapping(id = "verCNAE", pattern = "/cnae/ver/#{cNAEControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cnae/visualizar.xhtml")
})
public class CNAEControlador extends PrettyControlador<CNAE> implements Serializable, CRUD {

    @EJB
    private CNAEFacade facade;
    @EJB
    private LotacaoVistoriadoraFacade lotacaoFacade;
    @EJB
    private TipoVistoriaFacade vistoriaFacade;
    private Servico servico;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public CNAEControlador() {
        super(CNAE.class);
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    @URLAction(mappingId = "novoCNAE", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setSituacao(CNAE.Situacao.ATIVO);
    }

    @URLAction(mappingId = "verCNAE", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarCNAE", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cnae/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getNiveis() {
        List<SelectItem> itens = Lists.newArrayList();
        itens.add(new SelectItem(" ", null));
        for (GrauDeRiscoDTO nivel : GrauDeRiscoDTO.values()) {
            itens.add(new SelectItem(nivel, nivel.getDescricao()));
        }
        return itens;
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> itens = Lists.newArrayList();
        for (CNAE.Situacao situacao : CNAE.Situacao.values()) {
            itens.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return itens;
    }

    public List<CNAE> completarSomenteAtivos(String parte) {
        return facade.listaCnaesAtivos(parte);
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    public void validarAdicaoServico() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.temServico(servico)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Serviço já adicionado.");
        }
        ve.lancarException();
    }

    public void adicionarCnaeServico() {
        try {
            if (servico == null)
                return;
            validarAdicaoServico();
            selecionado.adicionarServico(servico);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
        servico = null;
    }

    public void removerCnaeServico(CNAEServico cnaeServico) {
        selecionado.getServicos().remove(cnaeServico);
    }
}
