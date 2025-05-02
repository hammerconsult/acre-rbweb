/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.controle.*;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Renato Romanini
 */
@SessionScoped
@ManagedBean
public class UtilBeanContabil implements Serializable {

    public static int QUANTIDADE_CARACTERES_HISTORICO = 3000;
    public static String SEPARADOR_HISTORICO = " - ";

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private UnidadeOrganizacional unidadeOrganizacionalORC;

    public UnidadeOrganizacional getUnidadeOrganizacionalORC() {
        return unidadeOrganizacionalORC;
    }

    public void setUnidadeOrganizacionalORC(UnidadeOrganizacional unidadeOrganizacionalORC) {
        this.unidadeOrganizacionalORC = unidadeOrganizacionalORC;
    }

    public void recuperaUnidadeOrganizacionalOrcamentariaVigente(Date data) {
        try {
            SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
            HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(data, sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
            this.unidadeOrganizacionalORC = ho.getHierarquiaOrganizacionalOrcamentaria().getSubordinada();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível recupera A Unidade Organizacional Orcamentária do usuário Logado.", ""));
            this.unidadeOrganizacionalORC = null;
        }
    }

    public void naoFazNada() {

    }

    public void redirecionarParaObrigacaoPagar(ObrigacaoAPagar selecionado) {
        ObrigacaoAPagarControlador controlador = (ObrigacaoAPagarControlador) Util.getControladorPeloNome("obrigacaoAPagarControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }


    public void redirecionarParaEmpenho(Empenho selecionado) {
        EmpenhoControlador controlador = (EmpenhoControlador) Util.getControladorPeloNome("empenhoControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaEmpenhoEstorno(EmpenhoEstorno selecionado) {
        EmpenhoEstornoControlador controlador = (EmpenhoEstornoControlador) Util.getControladorPeloNome("empenhoEstornoControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaLiquidacao(Liquidacao selecionado) {
        LiquidacaoControlador controlador = (LiquidacaoControlador) Util.getControladorPeloNome("liquidacaoControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaLiquidacaoEstorno(LiquidacaoEstorno selecionado) {
        LiquidacaoEstornoControlador controlador = (LiquidacaoEstornoControlador) Util.getControladorPeloNome("liquidacaoEstornoControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }


    public void redirecionarParaPagamento(Pagamento selecionado) {
        PagamentoControlador controlador = (PagamentoControlador) Util.getControladorPeloNome("pagamentoControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaPagamentoEstorno(PagamentoEstorno selecionado) {
        PagamentoEstornoControlador controlador = (PagamentoEstornoControlador) Util.getControladorPeloNome("pagamentoEstornoControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaReceitaExtra(ReceitaExtra selecionado) {
        ReceitaExtraControlador controlador = (ReceitaExtraControlador) Util.getControladorPeloNome("receitaExtraControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaReceitaExtraEstorno(ReceitaExtraEstorno selecionado) {
        ReceitaExtraEstornoControlador controlador = (ReceitaExtraEstornoControlador) Util.getControladorPeloNome("receitaExtraEstornoControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaPagamentoExtra(PagamentoExtra selecionado) {
        PagamentoExtraControlador controlador = (PagamentoExtraControlador) Util.getControladorPeloNome("pagamentoExtraControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaPagamentoExtraEstorno(PagamentoExtraEstorno selecionado) {
        PagamentoExtraEstornoControlador controlador = (PagamentoExtraEstornoControlador) Util.getControladorPeloNome("pagamentoExtraEstornoControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaAlteracaoOrcamentaria(AlteracaoORC selecionado) {
        AlteracaoORCControlador controlador = (AlteracaoORCControlador) Util.getControladorPeloNome("alteracaoORCControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaLiberacaoFinanceira(LiberacaoCotaFinanceira selecionado) {
        LiberacaoCotaFinanceiraControlador controlador = (LiberacaoCotaFinanceiraControlador) Util.getControladorPeloNome("liberacaoCotaFinanceiraControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaSolicitacaoFinanceira(SolicitacaoCotaFinanceira selecionado) {
        SolicitacaoCotaFinanceiraControlador controlador = (SolicitacaoCotaFinanceiraControlador) Util.getControladorPeloNome("solicitacaoCotaFinanceiraControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaTransferenciaFinanceira(TransferenciaContaFinanceira selecionado) {
        TransferenciaContaFinanceiraControlador controlador = (TransferenciaContaFinanceiraControlador) Util.getControladorPeloNome("transferenciaContaFinanceiraControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaObn600(Long idRemessaBanco) {
        ArquivoRemessaBancoControlador controlador = (ArquivoRemessaBancoControlador) Util.getControladorPeloNome("arquivoRemessaBancoControlador");
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + idRemessaBanco + "/");
    }

    public void redirecionarParaObn350(Long idRemessaBanco) {
        UploadArquivoOBN350Controlador controlador = (UploadArquivoOBN350Controlador) Util.getControladorPeloNome("uploadArquivoOBN350Controlador");
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + idRemessaBanco + "/");
    }

    public void redirecionarParaUnidadeOrganizacional(Long idUnidade) {
        UnidadeOrganizacionalControlador controlador = (UnidadeOrganizacionalControlador) Util.getControladorPeloNome("unidadeOrganizacionalControlador");
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "editar/" + idUnidade + "/");
    }

    public String getValorAsString(BigDecimal valor) {
        return Util.formataValor(valor);
    }

    public void redirecionarParaAnexoPortalTransparencia(PortalTransparencia selecionado) {
        PortalTransparenciaControlador controlador = (PortalTransparenciaControlador) Util.getControladorPeloNome("portalTransparenciaControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaTransfBensMoveis(Long idTransferencia) {
        TransfBensMoveisControlador controlador = (TransfBensMoveisControlador) Util.getControladorPeloNome("transfBensMoveisControlador");
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + idTransferencia + "/");
    }

    public void redirecionarParaTransfFerenciaBensEstoque(Long idTransferencia) {
        TransferenciaBensEstoqueControlador controlador = (TransferenciaBensEstoqueControlador) Util.getControladorPeloNome("transferenciaBensEstoqueControlador");
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + idTransferencia + "/");
    }
}
