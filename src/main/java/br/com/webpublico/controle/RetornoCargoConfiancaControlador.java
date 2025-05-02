/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
@ManagedBean(name = "retornoCargoConfiancaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRetornoCargoConfianca", pattern = "/retornocargocarreira/novo/", viewId = "/faces/rh/administracaodepagamento/retornocargoconfianca/edita.xhtml"),
    @URLMapping(id = "editarRetornoCargoConfianca", pattern = "/retornocargocarreira/editar/#{retornoCargoConfiancaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/retornocargoconfianca/edita.xhtml"),
    @URLMapping(id = "listarRetornoCargoConfianca", pattern = "/retornocargocarreira/listar/", viewId = "/faces/rh/administracaodepagamento/retornocargoconfianca/lista.xhtml"),
    @URLMapping(id = "verRetornoCargoConfianca", pattern = "/retornocargocarreira/ver/#{retornoCargoConfiancaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/retornocargoconfianca/visualizar.xhtml")
})
public class RetornoCargoConfiancaControlador extends PrettyControlador<CargoConfianca> implements Serializable, CRUD {

    @EJB
    private CargoConfiancaFacade cargoConfiancaFacade;
    private ConverterAutoComplete converterContratoFP;
    private ConverterAutoComplete converterAtoLegal;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    private AtoLegal atoLegal;
    private AtoLegal atoLegalExoneracao;
    private Date dataRetorno;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    private EnquadramentoCC enquadramentoCC;
    private CargoConfianca cargoConfiancaSelecionado;

    public RetornoCargoConfiancaControlador() {
        super(CargoConfianca.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return cargoConfiancaFacade;
    }

    public List<CargoConfianca> getLista() {
        return cargoConfiancaFacade.listaCargosRetornados();
    }

    public EnquadramentoCC getEnquadramentoCC() {
        return enquadramentoCC;
    }

    @Override
    public void novo() {
        enquadramentoCC = new EnquadramentoCC();
    }

    @URLAction(mappingId = "novoRetornoCargoConfianca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoMetodoNovo() {
        super.novo();
        enquadramentoCC = new EnquadramentoCC();
        cargoConfiancaSelecionado = new CargoConfianca();
    }

    @URLAction(mappingId = "verRetornoCargoConfianca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarRetornoCargoConfianca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public void setarCargoConfiancaSelecionado() {
        selecionado = cargoConfiancaSelecionado;
    }

    public List<CargoConfianca> completaCargoConfianca(String parte) {
        return cargoConfiancaFacade.completaCargoConfianca(parte.trim());
    }

    public Converter getConverterContratoFP() {
        return new ConverterAutoComplete(CargoConfianca.class, cargoConfiancaFacade);
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoAtoLegalPorPropositoAtoLegal(parte.trim(), PropositoAtoLegal.ATO_DE_PESSOAL);
    }

    public Converter getConverterAtoLegal() {
        return new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
    }

    public List<CargoConfianca> getCargos() {
        if (selecionado != null && selecionado.getContratoFP() != null) {
            return cargoConfiancaFacade.buscarFuncaoGratificadaPorContratoFP(selecionado.getContratoFP());
        }
        return new ArrayList<CargoConfianca>();
    }

    public boolean validarCampos() {
        if (cargoConfiancaSelecionado == null && !isOperacaoEditar()) {
            FacesUtil.addCampoObrigatorio("O campo servidor é obrigatório!");
            return false;
        }
        if (selecionado.getFinalVigencia() == null) {
            FacesUtil.addCampoObrigatorio("O campo data de retorno é obrigatório!");
            return false;
        }
        if (selecionado.getAtoLegal() == null && !isOperacaoEditar()) {
            FacesUtil.addCampoObrigatorio("O campo ato legal de retorno é obrigatório!");
            return false;
        }
        if (selecionado.getAtoLegalExoneracao() == null) {
            FacesUtil.addCampoObrigatorio("O campo ato legal de exoneração é obrigatório!");
            return false;
        }
        return true;
    }

    @Override
    public void salvar() {
        if (podeSalvar()) {
            try {
                cargoConfiancaFacade.salvarRetornoProvimentoCargoConfianca(selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
            } catch (ValidacaoException ex) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
                return;
            } catch (Exception e) {
                descobrirETratarException(e);

            }
            redireciona();
        }
    }

    private boolean podeSalvar() {
        if (!validarCampos()) return false;
        if (!tipoProvimentoFacade.existeTipoProvimentoPorCodigo(TipoProvimento.RETORNO_CARGO_CARREIRA)) {
            FacesUtil.addOperacaoNaoPermitida("Não foi encontrado provimento do tipo 27 - Retorno Cargo Carreira!");
            return false;
        }
        return true;
    }

    @Override
    public boolean validaRegrasParaExcluir() {
        if (cargoConfiancaFacade.getFichaFinanceiraFPFacade().existeFolhaProcessadaPorVinculoFPAndPeriodoDeVigencia(selecionado.getContratoFP(), selecionado.getInicioVigencia(), selecionado.getFinalVigencia())) {
            FacesUtil.addOperacaoNaoPermitida("Foi encontrado folha processada para este retorno ao cargo de carreira no período de vigência. É impossível excluir este registro!");
            return false;
        }
        return true;
    }

    public Date getDataRetorno() {
        return dataRetorno;
    }

    public void setDataRetorno(Date dataRetorno) {
        this.dataRetorno = dataRetorno;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public AtoLegal getAtoLegalExoneracao() {
        return atoLegalExoneracao;
    }

    public void setAtoLegalExoneracao(AtoLegal atoLegalExoneracao) {
        this.atoLegalExoneracao = atoLegalExoneracao;
    }

    public CargoConfianca getCargoConfiancaSelecionado() {
        return cargoConfiancaSelecionado;
    }

    public void setCargoConfiancaSelecionado(CargoConfianca cargoConfiancaSelecionado) {
        this.cargoConfiancaSelecionado = cargoConfiancaSelecionado;
    }

    public String recuperaEnquadramentoCargo() {
        EnquadramentoFuncional enquadramentoFuncional = new EnquadramentoFuncional();
        if (selecionado != null && selecionado.getId() != null) {
            selecionado = cargoConfiancaFacade.recuperar(selecionado.getId());
            recuperaEnquadramentoCC();
            enquadramentoFuncional = enquadramentoFuncionalFacade.recuperaEnquadramentoFuncionalVigente(selecionado.getContratoFP());
        }

        if (enquadramentoFuncional != null && enquadramentoFuncional.getId() != null) {
            if (selecionado.getContratoFP() != null && selecionado.getContratoFP().getId() != null) {
                return selecionado.getContratoFP().getCargo().toString() + " - " + enquadramentoFuncional.toString();
            }
            return enquadramentoFuncional.toString();
        }
        return "";
    }

    public void recuperaEnquadramentoCC() {
        enquadramentoCC = cargoConfiancaFacade.getEnquadramentoCCFacade().getEnquadramentoCCPorCargoConfianca(selecionado);

        if (enquadramentoCC == null || enquadramentoCC.getId() == null) {
            enquadramentoCC = new EnquadramentoCC();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/retornocargocarreira/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
