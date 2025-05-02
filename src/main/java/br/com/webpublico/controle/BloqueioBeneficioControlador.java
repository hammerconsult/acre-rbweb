/*
 * Codigo gerado automaticamente em Mon Sep 05 15:28:59 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.BloqueioBeneficio;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.TipoBloqueio;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BloqueioBeneficioFacade;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.OpcaoValeTransporteFPFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "bloqueioBeneficioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoBloqueioBeneficio", pattern = "/bloqueio-beneficio/novo/", viewId = "/faces/rh/administracaodepagamento/bloqueiobeneficio/edita.xhtml"),
    @URLMapping(id = "editarBloqueioBeneficio", pattern = "/bloqueio-beneficio/editar/#{bloqueioBeneficioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/bloqueiobeneficio/edita.xhtml"),
    @URLMapping(id = "listarBloqueioBeneficio", pattern = "/bloqueio-beneficio/listar/", viewId = "/faces/rh/administracaodepagamento/bloqueiobeneficio/lista.xhtml"),
    @URLMapping(id = "verBloqueioBeneficio", pattern = "/bloqueio-beneficio/ver/#{bloqueioBeneficioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/bloqueiobeneficio/visualizar.xhtml")
})
public class BloqueioBeneficioControlador extends PrettyControlador<BloqueioBeneficio> implements Serializable, CRUD {

    @EJB
    private BloqueioBeneficioFacade bloqueioBeneficioFacade;
    @EJB
    private OpcaoValeTransporteFPFacade opcaoValeTransporteFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterContratoFP;
//    private List<OpcaoValeTransporteFP> opcaoValeTransporteFPs;


    public BloqueioBeneficioControlador() {
        super(BloqueioBeneficio.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return bloqueioBeneficioFacade;
    }


    public List<SelectItem> getTipoBloqueios() {
        return Util.getListSelectItem(Arrays.asList(TipoBloqueio.values()));
    }

    public ConverterAutoComplete getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }


    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public void setContratoFPFacade(ContratoFPFacade contratoFPFacade) {
        this.contratoFPFacade = contratoFPFacade;
    }


    @URLAction(mappingId = "verBloqueioBeneficio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver(); //To change body of generated methods, choose Tools | Templates.
    }

    @URLAction(mappingId = "editarBloqueioBeneficio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar(); //To change body of generated methods, choose Tools | Templates.
    }


    @URLAction(mappingId = "novoBloqueioBeneficio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @Override
    public void salvar() {
        if (validarCampos() && verificarBloqueios()) {
            super.salvar();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/bloqueio-beneficio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public boolean validarCampos() {
        boolean retorno = true;

        if (selecionado.getContratoFP() == null) {
            FacesUtil.addCampoObrigatorio("O campo Servidor deve ser informado!");
            retorno = false;
        }
        if (selecionado.getInicioVigencia() == null) {
            FacesUtil.addCampoObrigatorio("O campo Data de Início de Bloqueio deve ser informado!");
            retorno = false;
        }
        if (selecionado.getTipoBloqueio() == null) {
            FacesUtil.addCampoObrigatorio("O campo Tipo de Bloqueio deve ser informado!");
            retorno = false;
        }
        return retorno;
    }

    public boolean verificarBloqueios() {
        boolean valida = true;
        if (!operacao.equals(Operacoes.EDITAR)) {
            if (selecionado.getContratoFP() != null && selecionado.getTipoBloqueio() != null && bloqueioBeneficioFacade.buscarBloqueiosVigentesPorVinculoFPAndTipoBloqueio(selecionado.getContratoFP(), selecionado.getTipoBloqueio())) {
                FacesUtil.addOperacaoNaoPermitida("Atualmente existe um bloqueio vigente para o Servidor selecionado.");
                valida = false;
            }
        }
        if (selecionado.getInicioVigencia() != null && selecionado.getFinalVigencia() != null && selecionado.getInicioVigencia().after(selecionado.getFinalVigencia())) {
            FacesUtil.addOperacaoNaoPermitida("A Data de Início do Bloqueio deve ser igual ou inferior a Data de Término do Bloqueio.");
            valida = false;
        }

        return valida;
    }

//
//    public void filtrarValesTransporte() {
//        if (selecionado.getContratoFP() != null && (selecionado.getTipoBloqueio() != null && selecionado.getTipoBloqueio().equals(TipoBloqueio.VALE_TRANSPORTE))) {
//            opcaoValeTransporteFPs = opcaoValeTransporteFPFacade.recuperaOpcaoValeTransporteFPPorContratoFP(selecionado.getContratoFP());
//        }
//
//    }
}
