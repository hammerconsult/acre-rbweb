package br.com.webpublico.controle.rh.saudeservidor;


import br.com.webpublico.controle.PrettyControlador;
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

@ManagedBean(name = "retornoCargoConfiancaSubsidioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRetornoCargoConfiancaSubsidio", pattern = "/retornocargocarreirasubsidio/novo/", viewId = "/faces/rh/administracaodepagamento/retornocargoconfiancasubsidio/edita.xhtml"),
    @URLMapping(id = "editarRetornoCargoConfiancaSubsidio", pattern = "/retornocargocarreirasubsidio/editar/#{retornoCargoConfiancaSubsidioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/retornocargoconfiancasubsidio/edita.xhtml"),
    @URLMapping(id = "listarRetornoCargoConfiancaSubsidio", pattern = "/retornocargocarreirasubsidio/listar/", viewId = "/faces/rh/administracaodepagamento/retornocargoconfiancasubsidio/lista.xhtml"),
    @URLMapping(id = "verRetornoCargoConfiancaSubsidio", pattern = "/retornocargocarreirasubsidio/ver/#{retornoCargoConfiancaSubsidioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/retornocargoconfiancasubsidio/visualizar.xhtml")
})
public class RetornoCargoConfiancaSubsidioControlador extends PrettyControlador<AcessoSubsidio> implements Serializable, CRUD {

    @EJB
    private AcessoSubsidioFacade acessoSubsidioFacade;
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
    private AcessoSubsidio acessoSubsidioSelecionado;

    public RetornoCargoConfiancaSubsidioControlador() {
        super(AcessoSubsidio.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return acessoSubsidioFacade;
    }

//    public List<CargoConfianca> getLista() {
//        return acessoSubsidioFacade.listaCargosRetornados();
//    }

    public EnquadramentoCC getEnquadramentoCC() {
        return enquadramentoCC;
    }

    @Override
    public void novo() {
        enquadramentoCC = new EnquadramentoCC();
    }

    @URLAction(mappingId = "novoRetornoCargoConfiancaSubsidio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoMetodoNovo() {
        super.novo();
        enquadramentoCC = new EnquadramentoCC();
        acessoSubsidioSelecionado = new AcessoSubsidio();
    }

    @URLAction(mappingId = "verRetornoCargoConfiancaSubsidio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarRetornoCargoConfiancaSubsidio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public void setarAcessoSubsidioSelecionado() {
        selecionado = acessoSubsidioSelecionado;
    }

//    public List<CargoConfianca> completaCargoConfianca(String parte) {
//        return acessoSubsidioFacade.completaCargoConfianca(parte.trim());
//    }

    public Converter getConverterContratoFP() {
        return new ConverterAutoComplete(CargoConfianca.class, acessoSubsidioFacade);
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoAtoLegalPorPropositoAtoLegal(parte.trim(), PropositoAtoLegal.ATO_DE_PESSOAL);
    }

    public Converter getConverterAtoLegal() {
        return new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
    }

    public List<AcessoSubsidio> getCargos() {
        if (selecionado != null && selecionado.getContratoFP() != null) {
            return acessoSubsidioFacade.buscarFuncaoGratificadaPorContratoFP(selecionado.getContratoFP());
        }
        return new ArrayList<>();
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (acessoSubsidioSelecionado == null && !isOperacaoEditar()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo servidor é obrigatório!");
        }
        if (selecionado.getFinalVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de retorno é obrigatório!");
        }
        if (selecionado.getAtoLegal() == null && !isOperacaoEditar()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo ato legal de retorno é obrigatório!");
        }
        if (selecionado.getAtoLegalExoneracao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo ato legal de exoneração é obrigatório!");
        }
        if (!tipoProvimentoFacade.existeTipoProvimentoPorCodigo(TipoProvimento.RETORNO_CARGO_CARREIRA)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado provimento do tipo 27 - Retorno Cargo Carreira!");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            acessoSubsidioFacade.salvarRetornoProvimentoAcessoSubsidio(selecionado);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            return;
        } catch (Exception e) {
            descobrirETratarException(e);

        }
        redireciona();
    }

    @Override
    public boolean validaRegrasParaExcluir() {
        if (acessoSubsidioFacade.getFichaFinanceiraFPFacade().existeFolhaProcessadaPorVinculoFPAndPeriodoDeVigencia(selecionado.getContratoFP(), selecionado.getInicioVigencia(), selecionado.getFinalVigencia())) {
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

    public AcessoSubsidio getAcessoSubsidioSelecionado() {
        return acessoSubsidioSelecionado;
    }

    public void setAcessoSubsidioSelecionado(AcessoSubsidio acessoSubsidioSelecionado) {
        this.acessoSubsidioSelecionado = acessoSubsidioSelecionado;
    }

    public String recuperaEnquadramentoCargo() {
        EnquadramentoFuncional enquadramentoFuncional = new EnquadramentoFuncional();
        if (selecionado != null && selecionado.getId() != null) {
            selecionado = acessoSubsidioFacade.recuperar(selecionado.getId());
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
        enquadramentoCC = acessoSubsidioFacade.getEnquadramentoCCFacade().getEnquadramentoCCPorAcessoSubsidio(selecionado);

        if (enquadramentoCC == null || enquadramentoCC.getId() == null) {
            enquadramentoCC = new EnquadramentoCC();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/retornocargocarreirasubsidio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<AcessoSubsidio> completaAcessoSubsidio(String parte) {
        return acessoSubsidioFacade.completaAcessoSubsidio(parte.trim());
    }
}
