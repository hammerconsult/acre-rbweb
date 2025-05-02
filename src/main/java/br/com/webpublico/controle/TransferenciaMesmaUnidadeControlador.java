/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.IConciliar;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.TransferenciaMesmaUnidadeFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Claudio
 */
@ManagedBean(name = "transferenciaMesmaUnidadeControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-transferencia-financeira-mesma-unidade", pattern = "/transferencia-financeira-mesma-unidade/novo/", viewId = "/faces/financeiro/orcamentario/transferenciamesmaunidade/edita.xhtml"),
    @URLMapping(id = "editar-transferencia-financeira-mesma-unidade", pattern = "/transferencia-financeira-mesma-unidade/editar/#{transferenciaMesmaUnidadeControlador.id}/", viewId = "/faces/financeiro/orcamentario/transferenciamesmaunidade/edita.xhtml"),
    @URLMapping(id = "ver-transferencia-financeira-mesma-unidade", pattern = "/transferencia-financeira-mesma-unidade/ver/#{transferenciaMesmaUnidadeControlador.id}/", viewId = "/faces/financeiro/orcamentario/transferenciamesmaunidade/visualizar.xhtml"),
    @URLMapping(id = "listar-transferencia-financeira-mesma-unidade", pattern = "/transferencia-financeira-mesma-unidade/listar/", viewId = "/faces/financeiro/orcamentario/transferenciamesmaunidade/lista.xhtml")
})
public class TransferenciaMesmaUnidadeControlador extends PrettyControlador<TransferenciaMesmaUnidade> implements Serializable, CRUD, IConciliar {

    @EJB
    private TransferenciaMesmaUnidadeFacade transferenciaMesmaUnidadeFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterContaFinanceira;
    private ConverterAutoComplete converterFinalidadePagamento;
    private BigDecimal saldoContaFinanceiraConcedida;
    private ContaBancariaEntidade contaBancariaEntidadeConcedida;
    private ContaBancariaEntidade contaBancariaEntidadeRecebida;
    private ConverterAutoComplete converterContaBancaria;

    public TransferenciaMesmaUnidadeControlador() {
        super(TransferenciaMesmaUnidade.class);
    }

    @URLAction(mappingId = "novo-transferencia-financeira-mesma-unidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        selecionado = new TransferenciaMesmaUnidade();
        operacao = Operacoes.NOVO;
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        selecionado.setStatusPagamento(StatusPagamento.ABERTO);
        selecionado.setDataTransferencia(sistemaControlador.getDataOperacao());
        selecionado.setTipoDocPagto(TipoDocPagto.ORDEMPAGAMENTO);
        selecionado.setResultanteIndependente(ResultanteIndependente.RESULTANTE_EXECUCAO_ORCAMENTARIA);
        saldoContaFinanceiraConcedida = new BigDecimal(BigInteger.ZERO);

        if (transferenciaMesmaUnidadeFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso! ", transferenciaMesmaUnidadeFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
        setaEventoDeposito();
        setaEventoRetirada();
    }

    @URLAction(mappingId = "ver-transferencia-financeira-mesma-unidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "editar-transferencia-financeira-mesma-unidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    public void recuperaEditaVer() {
        contaBancariaEntidadeConcedida = selecionado.getSubContaRetirada().getContaBancariaEntidade();
        contaBancariaEntidadeRecebida = selecionado.getSubContaDeposito().getContaBancariaEntidade();
        selecionado.setTipoDocPagto(TipoDocPagto.ORDEMPAGAMENTO);
    }

    public void excluirSelecionado() {
        try {
            transferenciaMesmaUnidadeFacade.remover(selecionado);
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O Registro não pode ser excluido, o mesmo possui dependencias.");
        }
    }

    public List<TransferenciaMesmaUnidade> getLista() {
        return transferenciaMesmaUnidadeFacade.listaDecrescente();
    }

    public List<ContaBancariaEntidade> completaContaBancariaEntidade(String parte) {
        try {
            return transferenciaMesmaUnidadeFacade.getContaBancariaEntidadeFacade().listaFiltrandoAtivaPorUnidade(parte.trim(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), getSistemaControlador().getExercicioCorrente(), selecionado.getFonteDeRecursosRetirada(), null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<SubConta> completaContaFinanceira(String parte) {
        try {
            return transferenciaMesmaUnidadeFacade.getSubContaFacade().buscarContasFinanceirasPorBancariaEntidadeOuTodosPorUnidade(parte.trim(), contaBancariaEntidadeConcedida, sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), getSistemaControlador().getExercicioCorrente(), selecionado.getFonteDeRecursosRetirada(), null, null, true);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<SelectItem> getValoresResultanteIndependente() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(ResultanteIndependente.RESULTANTE_EXECUCAO_ORCAMENTARIA, ResultanteIndependente.RESULTANTE_EXECUCAO_ORCAMENTARIA.getDescricao()));
        lista.add(new SelectItem(ResultanteIndependente.INDEPENDENTE_EXECUCAO_ORCAMENTARIA, ResultanteIndependente.INDEPENDENTE_EXECUCAO_ORCAMENTARIA.getDescricao()));
        return lista;
    }

    public List<SelectItem> getTiposTransferencia() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoTransferenciaMesmaUnidade tipo : TipoTransferenciaMesmaUnidade.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public List<SelectItem> getTipoOperacaoPagamento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoOperacaoPagto tipoOperacaoPagto : TipoOperacaoPagto.values()) {
            toReturn.add(new SelectItem(tipoOperacaoPagto, tipoOperacaoPagto.getDescricao()));
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public List<SelectItem> getTipoDocumento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoDocPagto tipoDocPagto : TipoDocPagto.values()) {
            toReturn.add(new SelectItem(tipoDocPagto, tipoDocPagto.getDescricao()));
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public Boolean getHabilitaEditar() {
        TransferenciaMesmaUnidade transf = ((TransferenciaMesmaUnidade) selecionado);
        if (transf.getStatusPagamento() == null) {
            return false;
        }
        if (transf.getStatusPagamento().equals(StatusPagamento.INDEFERIDO)
            || transf.getStatusPagamento().equals(StatusPagamento.DEFERIDO)
            || transf.getStatusPagamento().equals(StatusPagamento.ABERTO)) {
            return false;
        }
        return true;
    }

    public Boolean getHabilitaBotaoEditar() {
        TransferenciaMesmaUnidade transf = ((TransferenciaMesmaUnidade) selecionado);
        if (transf.getStatusPagamento() == null) {
            return false;
        }
        if (transf.getStatusPagamento().equals(StatusPagamento.INDEFERIDO)
            || transf.getStatusPagamento().equals(StatusPagamento.DEFERIDO)
            || transf.getStatusPagamento().equals(StatusPagamento.ABERTO)) {
            return true;
        }
        return false;
    }

    public Boolean getHabilitaExcluir() {
        TransferenciaMesmaUnidade transf = ((TransferenciaMesmaUnidade) selecionado);
        Boolean controle = false;
        if (transf.getStatusPagamento() == null) {
            return controle;
        }
        if (transf.getStatusPagamento().equals(StatusPagamento.ABERTO)) {
            controle = true;
        }
        return controle;
    }

    public Boolean habilitaBotaoEditarNoLista(TransferenciaMesmaUnidade transf) {
        if (transf.getStatusPagamento().equals(StatusPagamento.INDEFERIDO)
            || transf.getStatusPagamento().equals(StatusPagamento.DEFERIDO)
            || transf.getStatusPagamento().equals(StatusPagamento.ABERTO)) {
            return true;
        }
        if (transf.getStatusPagamento() == null) {
            return false;
        }
        return false;
    }

    public Boolean verificaEdicao() {
        if (operacao.equals(Operacoes.EDITAR)) {
            return true;
        } else {
            return false;
        }
    }

    public List<SelectItem> getContasDeDestinacaoConcedida() {
        List<SelectItem> dados = Lists.newArrayList();
        if (selecionado.getSubContaRetirada() != null && selecionado.getSubContaRetirada().getId() != null) {
            selecionado.setSubContaRetirada(transferenciaMesmaUnidadeFacade.getSubContaFacade().recuperar(selecionado.getSubContaRetirada().getId()));
            for (SubContaFonteRec subContaFonteRec : selecionado.getSubContaRetirada().getSubContaFonteRecs()) {
                if (subContaFonteRec.getContaDeDestinacao().getExercicio().equals(sistemaControlador.getExercicioCorrente())) {
                    dados.add(new SelectItem(subContaFonteRec.getContaDeDestinacao(), subContaFonteRec.getContaDeDestinacao().toString()));
                }
            }
        }
        return Util.ordenaSelectItem(dados);
    }

    public void atualizarFonteConcedida() {
        if (selecionado.getContaDeDestinacaoRetirada() != null) {
            selecionado.setFonteDeRecursosRetirada(selecionado.getContaDeDestinacaoRetirada().getFonteDeRecursos());
        }
    }

    public List<SelectItem> getContasDeDestinacaoRecebida() {
        List<SelectItem> dados = Lists.newArrayList();
        if (selecionado.getSubContaDeposito() != null && selecionado.getSubContaDeposito().getId() != null) {
            selecionado.setSubContaDeposito(transferenciaMesmaUnidadeFacade.getSubContaFacade().recuperar(selecionado.getSubContaDeposito().getId()));
            for (SubContaFonteRec subContaFonteRec : selecionado.getSubContaDeposito().getSubContaFonteRecs()) {
                if (subContaFonteRec.getContaDeDestinacao().getExercicio().equals(sistemaControlador.getExercicioCorrente())) {
                    dados.add(new SelectItem(subContaFonteRec.getContaDeDestinacao(), subContaFonteRec.getContaDeDestinacao().toString()));
                }
            }
        }
        return Util.ordenaSelectItem(dados);
    }

    public void atualizarFonteRecebida() {
        if (selecionado.getContaDeDestinacaoDeposito() != null) {
            selecionado.setFonteDeRecursosDeposito(selecionado.getContaDeDestinacaoDeposito().getFonteDeRecursos());
        }
    }

    public void atribuirContaBancariaConcedida() {
        try {
            contaBancariaEntidadeConcedida = selecionado.getSubContaRetirada().getContaBancariaEntidade();
            List<SelectItem> fontesDeRecursosConcedida = getContasDeDestinacaoConcedida();
            if (!fontesDeRecursosConcedida.isEmpty()) {
                selecionado.setContaDeDestinacaoRetirada((ContaDeDestinacao) fontesDeRecursosConcedida.get(0).getValue());
                selecionado.setFonteDeRecursosRetirada(selecionado.getContaDeDestinacaoRetirada().getFonteDeRecursos());
            }

            if (selecionado.getSubContaRetirada().getContaVinculada() == null) {
                throw new ExcecaoNegocioGenerica("Não foi encontrado uma Conta Vinculada a conta financeira: " + selecionado.getSubContaRetirada().toStringAutoCompleteContaFinanceira());
            }
            selecionado.setSubContaDeposito(selecionado.getSubContaRetirada().getContaVinculada());
            atribuirContaBancariaRecebida();

            recuperaSaldoContaFinanceiraConcedida();
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
            logger.error("Erro ao atribuir a conta bancaria concedida: {}", e);
        }
    }

    public UnidadeOrganizacional selecionarUnidade() {
        return transferenciaMesmaUnidadeFacade.recuperaUnidadeDaSubContaConcedida(selecionado, sistemaControlador.getExercicioCorrente());
    }

    public void atribuirContaBancariaRecebida() {
        try {
            contaBancariaEntidadeRecebida = selecionado.getSubContaDeposito().getContaBancariaEntidade();
            List<SelectItem> fontesDeRecursosRecebida = getContasDeDestinacaoRecebida();
            if (!fontesDeRecursosRecebida.isEmpty()) {
                selecionado.setContaDeDestinacaoDeposito((ContaDeDestinacao) fontesDeRecursosRecebida.get(0).getValue());
                selecionado.setFonteDeRecursosDeposito(selecionado.getContaDeDestinacaoDeposito().getFonteDeRecursos());
            }
        } catch (Exception e) {
            logger.error("Erro ao atribuir a conta bancaria recebida: {}", e);
        }
    }

    public BigDecimal recuperaSaldoContaFinanceiraConcedida() {
        BigDecimal saldo = BigDecimal.ZERO;
        if (selecionado.getContaDeDestinacaoRetirada() != null && selecionado.getUnidadeOrganizacional() != null && selecionado.getSubContaRetirada() != null && selecionado.getDataTransferencia() != null) {
            SaldoSubConta saldoSubConta = transferenciaMesmaUnidadeFacade.getSaldoSubContaFacade().recuperaUltimoSaldoSubContaPorData(selecionado.getUnidadeOrganizacional(), selecionado.getSubContaRetirada(), selecionado.getContaDeDestinacaoRetirada(), selecionado.getDataTransferencia());            if (saldoSubConta != null && saldoSubConta.getId() != null) {
                saldo = saldoSubConta.getTotalCredito().subtract(saldoSubConta.getTotalDebito());
            }
        }
        return saldoContaFinanceiraConcedida = saldo;
    }

    public String setaEventoRetirada() {
        ConfiguracaoTranferenciaMesmaUnidade configuracao;
        selecionado.setEventoContabilRetirada(null);
        try {
            if (selecionado.getResultanteIndependente() != null && selecionado.getTipoTransferencia() != null) {
                configuracao = transferenciaMesmaUnidadeFacade.getConfiguracaoTranferenciaMesmaUnidadeFacade().recuperaEvento(TipoLancamento.NORMAL, selecionado.getResultanteIndependente(), selecionado.getTipoTransferencia(), OrigemTipoTransferencia.CONCEDIDO, selecionado.getDataTransferencia());
                selecionado.setEventoContabilRetirada(configuracao.getEventoContabil());
                return selecionado.getEventoContabilRetirada().toString();
            } else {
                return "Nenhum evento encontrado";
            }
        } catch (ExcecaoNegocioGenerica e) {
            return e.getMessage();
        }
    }

    public String setaEventoDeposito() {
        TransferenciaMesmaUnidade ad = ((TransferenciaMesmaUnidade) selecionado);
        ConfiguracaoTranferenciaMesmaUnidade configuracao;
        ad.setEventoContabil(null);
        try {
            if (ad.getResultanteIndependente() != null
                && ad.getTipoTransferencia() != null) {
                configuracao = transferenciaMesmaUnidadeFacade.getConfiguracaoTranferenciaMesmaUnidadeFacade().recuperaEvento(TipoLancamento.NORMAL, ad.getResultanteIndependente(), ad.getTipoTransferencia(), OrigemTipoTransferencia.RECEBIDO, ad.getDataTransferencia());
                ad.setEventoContabil(configuracao.getEventoContabil());
                return ad.getEventoContabil().toString();
            } else {
                return "Nenhum evento encontrado";
            }
        } catch (ExcecaoNegocioGenerica e) {
            return e.getMessage();
        }
    }

    public ConverterAutoComplete getConverterFinalidadePagamento() {
        if (converterFinalidadePagamento == null) {
            converterFinalidadePagamento = new ConverterAutoComplete(FinalidadePagamento.class, transferenciaMesmaUnidadeFacade.getFinalidadePagamentoFacade());
        }
        return converterFinalidadePagamento;
    }

    public ConverterAutoComplete getConverterContaBancaria() {
        if (converterContaBancaria == null) {
            converterContaBancaria = new ConverterAutoComplete(ContaBancariaEntidade.class, transferenciaMesmaUnidadeFacade.getContaBancariaEntidadeFacade());
        }
        return converterContaBancaria;
    }

    public ConverterAutoComplete getConverterContaFinanceira() {
        if (converterContaFinanceira == null) {
            converterContaFinanceira = new ConverterAutoComplete(SubConta.class, transferenciaMesmaUnidadeFacade.getSubContaFacade());
        }
        return converterContaFinanceira;
    }

    public Boolean isMesmaContaBancaria() {
        if (selecionado.getSubContaDeposito() != null && selecionado.getSubContaRetirada() != null) {
            if (selecionado.getSubContaDeposito().getContaBancariaEntidade().equals(selecionado.getSubContaRetirada().getContaBancariaEntidade())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void salvar() {
        try {
            validarTransferencia();
            validarDataConciliacao();
            if (isOperacaoNovo()) {
                validarIntegracaoBancaria();
                transferenciaMesmaUnidadeFacade.salvarNovo(selecionado);
            } else {
                transferenciaMesmaUnidadeFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(" Registro salvo com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            transferenciaMesmaUnidadeFacade.getSingletonConcorrenciaContabil().tratarExceptionConcorrenciaSaldos(ex);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarTransferencia() {
        selecionado.realizarValidacoes();
        validarMovimentacao();
    }

    private void validarIntegracaoBancaria() {
        ValidacaoException ve = new ValidacaoException();
        if (!isMesmaContaBancaria()) {
            if (selecionado.getTipoDocPagto() == null) {
                ve.adicionarMensagemDeCampoObrigatorio(" O campo Tipo de Documento deve ser informado.");
            }
            if (selecionado.getTipoOperacaoPagto() == null) {
                ve.adicionarMensagemDeCampoObrigatorio(" O campo Tipo de Operação deve ser informado.");
            }
            if (selecionado.getFinalidadePagamento() == null && !TipoOperacaoPagto.NAO_APLICAVEL.equals(selecionado.getTipoOperacaoPagto())) {
                ve.adicionarMensagemDeCampoObrigatorio(" O campo Finalidade do Pagamento deve ser informado.");
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void validarMovimentacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getSubContaRetirada().equals(selecionado.getSubContaDeposito())) {
            if (selecionado.getFonteDeRecursosRetirada().equals(selecionado.getFonteDeRecursosDeposito())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Não é possivel efetuar uma transferência para a mesma conta financeira e mesma fonte de recurso.");
            }
        }
        if (selecionado.getValor() == null || selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo valor deve ser maior que " + "<b>" + Util.formataValor(selecionado.getValor()) + "</b>");
        }
        if (selecionado.getDataConciliacao() != null) {
            if (Util.getDataHoraMinutoSegundoZerado(selecionado.getDataConciliacao()).before(Util.getDataHoraMinutoSegundoZerado(selecionado.getDataTransferencia()))) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo data de conciliação concedida deve ser superior ou igual a data da transferência.");
            }
        }
        if (selecionado.getRecebida() != null) {
            if (Util.getDataHoraMinutoSegundoZerado(selecionado.getRecebida()).before(Util.getDataHoraMinutoSegundoZerado(selecionado.getDataTransferencia()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo data de conciliação recebida deve ser superior a data da transferência.");
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void validaDataLancamento(FacesContext context, UIComponent component, Object value) {
        Date dataSelecionada = (Date) value;
        FacesMessage message = new FacesMessage();
        if (dataSelecionada.after(new Date())) {
            message.setDetail("Data invalida!");
            message.setSummary("Não é possivel executar lançamento com data Posterior a data de Hoje! ");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    @Override
    public AbstractFacade getFacede() {
        return transferenciaMesmaUnidadeFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/transferencia-financeira-mesma-unidade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void baixarTransferencia() {
        try {
            if (validarBaixaTransferencia()) {
                transferenciaMesmaUnidadeFacade.baixarTransferncia(selecionado);
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "A transferência financeira N° <b>" + selecionado.getNumero() + "</b> foi baixada com sucesso.");
                redirecionaParaLista();
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
        }
    }

    public boolean validarBaixaTransferencia() {
        if (selecionado.getDataConciliacao() == null) {
            FacesUtil.addCampoObrigatorio(" O campo Data de Conciliação Concedida deve ser informado.");
            return false;
        } else if (selecionado.getDataConciliacao().before(selecionado.getDataTransferencia())) {
            FacesUtil.addCampoObrigatorio(" A Data de Conciliação Concedida deve ser maior ou igual a Data da Transferência.");
            return false;
        }
        if (selecionado.getRecebida() == null) {
            FacesUtil.addCampoObrigatorio(" O campo Data de Conciliação Recebida deve ser informado.");
            return false;
        } else if (selecionado.getRecebida().before(selecionado.getDataTransferencia())) {
            FacesUtil.addCampoObrigatorio(" A Data de Conciliação Recebida deve ser maior ou igual a Data da Transferência.");
            return false;
        }
        return true;
    }

    public void redirecionaParaLista() {
        FacesUtil.redirecionamentoInterno("/transferencia-financeira-mesma-unidade/listar/");
    }


    public Boolean mostrarBotaoBaixar() {
        if (selecionado.getId() != null
            && selecionado.getStatusPagamento().equals(StatusPagamento.DEFERIDO)) {
            return true;
        }
        return false;
    }

    public Boolean mostrarBotaoEstornarBaixar() {
        if (selecionado.getId() != null
            && selecionado.getStatusPagamento().equals(StatusPagamento.PAGO)) {
            return true;
        }
        return false;
    }

    @Override
    public void baixar() {
        try {
            validarTransferencia();
            validarDataConciliacao();
            transferenciaMesmaUnidadeFacade.baixar(selecionado);
            FacesUtil.addOperacaoRealizada(" A Transferência Financeira Mesma Unidade N° <b>" + selecionado.getNumero() + "</b> foi baixada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoRealizada(ex.getMessage());
        }
    }

    private void validarDataConciliacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataConciliacao() != null && DataUtil.getAno(selecionado.getDataTransferencia()).compareTo(DataUtil.getAno(selecionado.getDataConciliacao())) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de conciliação está com o exercício diferente da data do registro.");
        }
        if (selecionado.getRecebida() != null && DataUtil.getAno(selecionado.getDataTransferencia()).compareTo(DataUtil.getAno(selecionado.getRecebida())) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de conciliação está com o exercício diferente da data do registro.");
        }
        ve.lancarException();
    }

    @Override
    public void estornarBaixa() {
        try {
            validarTransferencia();
            transferenciaMesmaUnidadeFacade.estornarBaixa(selecionado);
            FacesUtil.addOperacaoRealizada(" A Transferência Financeira Mesma Unidade N° <b>" + selecionado.getNumero() + "</b> foi estornada a baixa com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoRealizada(ex.getMessage());
        }
    }

    public BigDecimal getSaldoContaFinanceiraConcedida() {
        return saldoContaFinanceiraConcedida;
    }

    public void setSaldoContaFinanceiraConcedida(BigDecimal saldoContaFinanceiraConcedida) {
        this.saldoContaFinanceiraConcedida = saldoContaFinanceiraConcedida;
    }

    public ContaBancariaEntidade getContaBancariaEntidadeConcedida() {
        return contaBancariaEntidadeConcedida;
    }

    public void setContaBancariaEntidadeConcedida(ContaBancariaEntidade contaBancariaEntidadeConcedida) {
        this.contaBancariaEntidadeConcedida = contaBancariaEntidadeConcedida;
    }

    public ContaBancariaEntidade getContaBancariaEntidadeRecebida() {
        return contaBancariaEntidadeRecebida;
    }

    public void setContaBancariaEntidadeRecebida(ContaBancariaEntidade contaBancariaEntidadeRecebida) {
        this.contaBancariaEntidadeRecebida = contaBancariaEntidadeRecebida;
    }

    public void setarFinalidade(ActionEvent evento) {
        selecionado.setFinalidadePagamento((FinalidadePagamento) evento.getComponent().getAttributes().get("objeto"));
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}

