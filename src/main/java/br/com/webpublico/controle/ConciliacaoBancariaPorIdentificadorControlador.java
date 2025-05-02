package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.RelatorioConciliacaoBancariaPorIdentificadorControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.IdentificadorMovimentoConciliacaoBancaria;
import br.com.webpublico.entidadesauxiliares.contabil.MovimentoConciliacaoBancaria;
import br.com.webpublico.enums.SituacaoReceitaExtra;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mateus on 31/07/17.
 */
@ManagedBean(name = "conciliacaoBancariaPorIdentificadorControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "conciliacao-por-identificador", pattern = "/conciliar-por-identificador/", viewId = "/faces/financeiro/conciliacao/conciliacaoporidentificador/edita.xhtml")
})
public class ConciliacaoBancariaPorIdentificadorControlador extends ConciliacaoBancariaSuperControlador {

    private List<MovimentoConciliacaoBancaria> movimentosSemIdentificador;
    private List<IdentificadorMovimentoConciliacaoBancaria> movimentosComIdentificador;
    private Identificador identificador;

    @URLAction(mappingId = "conciliacao-por-identificador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        movimentosSemIdentificador = Lists.newArrayList();
    }

    public List<Identificador> completarIdentificadores(String filtro) {
        return getFacade().getIdentificadorFacade().buscarIdentificadoresPorNumeroOrDescricao(filtro);
    }

    @Override
    public void preencherFiltrosPendencia() {
        super.preencherFiltrosPendencia();
        filtroPendencia.setDataConciliacaoPorIdentificador(conciliacao.getData());
    }

    @Override
    public void buscarPendencias() {
        if (conciliacao.getContaBancaria() != null) {
            movimentosSemIdentificador = getFacade().buscarMovimentosPorContaBancariaConciliacaoSemIdentificador(filtroPendencia);
            movimentosComIdentificador = getFacade().buscarMovimentosPorContaBancariaConciliacaoComIdentificador(filtroPendencia);
        }
    }

    public BigDecimal getTotalGeral() {
        return getTotalDeCredito().subtract(getTotalDeDebito());
    }

    public BigDecimal getTotalDeCredito() {
        BigDecimal total = BigDecimal.ZERO;
        for (MovimentoConciliacaoBancaria movimento : movimentosSemIdentificador) {
            total = total.add(movimento.getCredito());
        }
        return total;
    }

    public BigDecimal getTotalDeDebito() {
        BigDecimal total = BigDecimal.ZERO;
        for (MovimentoConciliacaoBancaria movimento : movimentosSemIdentificador) {
            total = total.add(movimento.getDebito());
        }
        return total;
    }

    public void conciliarPorIdentificador(IdentificadorMovimentoConciliacaoBancaria identificadorMovimentoConciliacaoBancaria) {
        try {
            validarDataConciliacaoParaIdentificador(identificadorMovimentoConciliacaoBancaria);
            for (MovimentoConciliacaoBancaria movimento : identificadorMovimentoConciliacaoBancaria.getMovimentos()) {
                conciliarMovimento(movimento, false);
            }
            FacesUtil.addOperacaoRealizada("Movimentos conciliados com sucesso.");
            buscarPendencias();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarDataConciliacaoParaIdentificador(IdentificadorMovimentoConciliacaoBancaria identificadorMovimentoConciliacaoBancaria) {
        ValidacaoException ve = new ValidacaoException();
        if (conciliacao.getData().after(identificadorMovimentoConciliacaoBancaria.getIdentificador().getData())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Conciliação deve ser anterior ou igual a Data do Identificador.");
        }
        ve.lancarException();
    }

    public void estornarConciliacaoPorIdentificador(IdentificadorMovimentoConciliacaoBancaria identificadorMovimentoConciliacaoBancaria) {
        try {
            for (MovimentoConciliacaoBancaria movimento : identificadorMovimentoConciliacaoBancaria.getMovimentos()) {
                estornarConciliacaoMovimento(movimento, false);
            }
            FacesUtil.addOperacaoRealizada("Estorno da conciliação dos movimentos realizada com sucesso.");
            buscarPendencias();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void baixarPorIdentificador(IdentificadorMovimentoConciliacaoBancaria identificadorMovimentoConciliacaoBancaria) {
        for (MovimentoConciliacaoBancaria movimento : identificadorMovimentoConciliacaoBancaria.getMovimentos()) {
            baixarMovimento(movimento, false);
        }
        FacesUtil.addOperacaoRealizada("Movimentos baixados com sucesso.");
        buscarPendencias();
    }

    public boolean hasTodosMovimentosConciliados(IdentificadorMovimentoConciliacaoBancaria identificadorMovimentoConciliacaoBancaria) {
        for (MovimentoConciliacaoBancaria movimento : identificadorMovimentoConciliacaoBancaria.getMovimentos()) {
            if (movimento.getDataConciliacao() == null) {
                return false;
            }
        }
        return true;
    }

    public boolean hasMovimentosDiferenteDeNaoAplicavel(IdentificadorMovimentoConciliacaoBancaria identificadorMovimentoConciliacaoBancaria) {
        for (MovimentoConciliacaoBancaria movimento : identificadorMovimentoConciliacaoBancaria.getMovimentos()) {
            if (!MovimentoConciliacaoBancaria.Situacao.NAO_APLICAVEL.equals(movimento.getSituacao())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTodosMovimentosBaixados(IdentificadorMovimentoConciliacaoBancaria identificadorMovimentoConciliacaoBancaria) {
        for (MovimentoConciliacaoBancaria movimento : identificadorMovimentoConciliacaoBancaria.getMovimentos()) {
            if (MovimentoConciliacaoBancaria.Situacao.ABERTO.equals(movimento.getSituacao())) {
                return false;
            }
        }
        return true;
    }

    public void removerMovimento(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        if (MovimentoConciliacaoBancaria.TipoMovimento.LANCCONCILIACAOBANCARIA.equals(movimentoConciliacaoBancaria.getTipoMovimento())) {
            recuperarLancamentoConciliacaBancaria(movimentoConciliacaoBancaria);
            getFacade().remover(lancamentoConciliacaoBancaria);
        }
        buscarPendencias();
    }

    public void retirarIdentificador(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        switch (movimentoConciliacaoBancaria.getTipoMovimento()) {
            case LANCCONCILIACAOBANCARIA:
                recuperarLancamentoConciliacaBancaria(movimentoConciliacaoBancaria);
                lancamentoConciliacaoBancaria.setIdentificador(null);
                getFacade().salvar(lancamentoConciliacaoBancaria);
                break;
            case PAGAMENTO:
            case PAGAMENTO_RESTO:
                Pagamento pagamento = recuperarPagamento(movimentoConciliacaoBancaria);
                pagamento.setIdentificador(null);
                getFacade().getPagamentoFacade().salvar(pagamento);
                break;
            case ESTORNO_PAGAMENTO:
            case ESTORNO_PAGAMENTO_RESTO:
                PagamentoEstorno pagamentoEstorno = recuperarPagamentoEstorno(movimentoConciliacaoBancaria);
                pagamentoEstorno.setIdentificador(null);
                getFacade().getPagamentoEstornoFacade().salvar(pagamentoEstorno);
                break;
            case DESPESA_EXTRA:
                PagamentoExtra pagamentoExtra = recuperarPagamentoExtra(movimentoConciliacaoBancaria);
                pagamentoExtra.setIdentificador(null);
                getFacade().getPagamentoExtraFacade().salvar(pagamentoExtra);
                break;
            case ESTORNO_DESPESA_EXTRA:
                PagamentoExtraEstorno pagamentoExtraEstorno = recuperarPagamentoExtraEstorno(movimentoConciliacaoBancaria);
                pagamentoExtraEstorno.setIdentificador(null);
                getFacade().getPagamentoExtraEstornoFacade().salvar(pagamentoExtraEstorno);
                break;
            case RECEITA_EXTRA:
                ReceitaExtra receitaExtra = recuperarReceitaExtra(movimentoConciliacaoBancaria);
                receitaExtra.setIdentificador(null);
                getFacade().getReceitaExtraFacade().salvar(receitaExtra);
                break;
            case ESTORNO_RECEITA_EXTRA:
                ReceitaExtraEstorno receitaExtraEstorno = recuperarReceitaExtraEstorno(movimentoConciliacaoBancaria);
                receitaExtraEstorno.setIdentificador(null);
                getFacade().getReceitaExtraEstornoFacade().salvar(receitaExtraEstorno);
                break;
            case RECEITA_REALIZADA:
                LancamentoReceitaOrc lancamentoReceitaOrc = recuperarLancamentoReceitaOrc(movimentoConciliacaoBancaria);
                lancamentoReceitaOrc.setIdentificador(null);
                getFacade().getLancamentoReceitaOrcFacade().salvar(lancamentoReceitaOrc);
                break;
            case ESTORNO_RECEITA_REALIZADA:
                ReceitaORCEstorno receitaORCEstorno = recuperarReceitaOrcEstorno(movimentoConciliacaoBancaria);
                receitaORCEstorno.setIdentificador(null);
                getFacade().getReceitaORCEstornoFacade().salvar(receitaORCEstorno);
                break;
            case LIBERACAO_FINANCEIRA:
            case LIBERACAO_FINANCEIRA_REPASSE:
                LiberacaoCotaFinanceira liberacaoCotaFinanceira = recuperarLiberacaoCotaFinanceira(movimentoConciliacaoBancaria);
                liberacaoCotaFinanceira.setIdentificador(null);
                getFacade().getLiberacaoCotaFinanceiraFacade().salvar(liberacaoCotaFinanceira);
                break;
            case ESTORNO_LIBERACAO_FINANCEIRA:
            case ESTORNO_LIBERACAO_FINANCEIRA_REPASSE:
                EstornoLibCotaFinanceira estornoLibCotaFinanceira = recuperarEstornoLibCotaFinanceira(movimentoConciliacaoBancaria);
                estornoLibCotaFinanceira.setIdentificador(null);
                getFacade().getEstornoLibCotaFinanceiraFacade().salvar(estornoLibCotaFinanceira);
                break;
            case TRANSFERENCIA_FINANCEIRA_DEPOSITO:
            case TRANSFERENCIA_FINANCEIRA_RETIRADA:
                TransferenciaContaFinanceira transferenciaContaFinanceira = recuperarTransferenciaContaFinanceira(movimentoConciliacaoBancaria);
                transferenciaContaFinanceira.setIdentificador(null);
                getFacade().getTransferenciaContaFinanceiraFacade().salvar(transferenciaContaFinanceira);
                break;
            case ESTORNO_TRANSFERENCIA_FINANCEIRA_DEPOSITO:
            case ESTORNO_TRANSFERENCIA_FINANCEIRA_RETIRADA:
                EstornoTransferencia estornoTransferencia = recuperarEstornoTransferencia(movimentoConciliacaoBancaria);
                estornoTransferencia.setIdentificador(null);
                getFacade().getEstornoTransferenciaFacade().salvar(estornoTransferencia);
                break;
            case AJUSTE_ATIVO_DISPONIVEL_AUMENTATIVO:
            case AJUSTE_ATIVO_DISPONIVEL_DIMINUTIVO:
            case ESTORNO_AJUSTE_ATIVO_DISPONIVEL_AUMENTATIVO:
            case ESTORNO_AJUSTE_ATIVO_DISPONIVEL_DIMINUTIVO:
                AjusteAtivoDisponivel ajusteAtivoDisponivel = recuperarAjusteAtivoDisponivel(movimentoConciliacaoBancaria);
                ajusteAtivoDisponivel.setIdentificador(null);
                getFacade().getAjusteAtivoDisponivelFacade().salvar(ajusteAtivoDisponivel);
                break;
        }
        buscarPendencias();
    }

    public void baixarMovimento(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria, Boolean buscarMovimentos) {
        try {
            if (!MovimentoConciliacaoBancaria.Situacao.ABERTO.equals(movimentoConciliacaoBancaria.getSituacao())) {
                return;
            }
            switch (movimentoConciliacaoBancaria.getTipoMovimento()) {
                case PAGAMENTO:
                case PAGAMENTO_RESTO:
                    Pagamento pagamento = recuperarPagamento(movimentoConciliacaoBancaria);
                    if (!StatusPagamento.PAGO.equals(pagamento.getStatus())) {
                        validarBaixar(pagamento.getDataConciliacao(), pagamento.getDataPagamento());
                        getFacade().getPagamentoFacade().baixar(pagamento, StatusPagamento.PAGO);
                    }
                    break;
                case DESPESA_EXTRA:
                    PagamentoExtra pagamentoExtra = recuperarPagamentoExtra(movimentoConciliacaoBancaria);
                    if (!StatusPagamento.PAGO.equals(pagamentoExtra.getStatus())) {
                        validarBaixar(pagamentoExtra.getDataConciliacao(), pagamentoExtra.getDataPagto());
                        getFacade().getPagamentoExtraFacade().baixar(pagamentoExtra, StatusPagamento.PAGO);
                    }
                    break;
                case RECEITA_EXTRA:
                    ReceitaExtra receitaExtra = recuperarReceitaExtra(movimentoConciliacaoBancaria);
                    if (SituacaoReceitaExtra.ABERTO.equals(receitaExtra.getSituacaoReceitaExtra())) {
                        validarBaixar(receitaExtra.getDataConciliacao(), receitaExtra.getDataReceita());
                        getFacade().getReceitaExtraFacade().baixar(receitaExtra);
                    }
                    break;
                case RECEITA_REALIZADA:
                    LancamentoReceitaOrc lancamentoReceitaOrc = recuperarLancamentoReceitaOrc(movimentoConciliacaoBancaria);
                    if (lancamentoReceitaOrc.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                        validarBaixar(lancamentoReceitaOrc.getDataConciliacao(), lancamentoReceitaOrc.getDataLancamento());
                        getFacade().getLancamentoReceitaOrcFacade().baixar(lancamentoReceitaOrc);
                    }
                    break;
                case LIBERACAO_FINANCEIRA:
                case LIBERACAO_FINANCEIRA_REPASSE:
                    LiberacaoCotaFinanceira liberacaoCotaFinanceira = recuperarLiberacaoCotaFinanceira(movimentoConciliacaoBancaria);
                    if (StatusPagamento.DEFERIDO.equals(liberacaoCotaFinanceira.getStatusPagamento())) {
                        validarBaixar(liberacaoCotaFinanceira.getDataConciliacao(), liberacaoCotaFinanceira.getDataLiberacao());
                        getFacade().getLiberacaoCotaFinanceiraFacade().baixar(liberacaoCotaFinanceira);
                    }
                    break;
                case TRANSFERENCIA_FINANCEIRA_DEPOSITO:
                case TRANSFERENCIA_FINANCEIRA_RETIRADA:
                    TransferenciaContaFinanceira transferenciaContaFinanceira = recuperarTransferenciaContaFinanceira(movimentoConciliacaoBancaria);
                    if (StatusPagamento.DEFERIDO.equals(transferenciaContaFinanceira.getStatusPagamento())) {
                        validarBaixar(transferenciaContaFinanceira.getDataConciliacao(), transferenciaContaFinanceira.getDataTransferencia());
                        getFacade().getTransferenciaContaFinanceiraFacade().baixar(transferenciaContaFinanceira);
                    }
                    break;
            }
            if (buscarMovimentos) {
                FacesUtil.addOperacaoRealizada("Movimento baixado com sucesso.");
                buscarPendencias();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarBaixar(Date dataConciliacao, Date dataMovimento) {
        ValidacaoException ve = new ValidacaoException();
        if (dataConciliacao != null && Util.getDataHoraMinutoSegundoZerado(dataConciliacao).before(Util.getDataHoraMinutoSegundoZerado(dataMovimento))) {
            ve.adicionarMensagemDeCampoObrigatorio(" A Data de Conciliação deve ser maior ou igual a data do movimento.");
        }
        ve.lancarException();
    }

    public void estornarConciliacaoMovimento(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        try {
            estornarConciliacaoMovimento(movimentoConciliacaoBancaria, true);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void estornarConciliacaoMovimento(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria, Boolean buscarMovimentos) {
        switch (movimentoConciliacaoBancaria.getTipoMovimento()) {
            case LANCCONCILIACAOBANCARIA:
                recuperarLancamentoConciliacaBancaria(movimentoConciliacaoBancaria);
                validarEstornoDeConciliacao(lancamentoConciliacaoBancaria.getDataConciliacao(), lancamentoConciliacaoBancaria.getUnidadeOrganizacional());
                lancamentoConciliacaoBancaria.setDataConciliacao(null);
                getFacade().salvar(lancamentoConciliacaoBancaria);
                break;
            case PAGAMENTO:
            case PAGAMENTO_RESTO:
                Pagamento pagamento = recuperarPagamento(movimentoConciliacaoBancaria);
                validarEstornoDeConciliacao(pagamento.getDataConciliacao(), pagamento.getUnidadeOrganizacional());
                pagamento.setDataConciliacao(null);
                getFacade().getPagamentoFacade().salvar(pagamento);
                break;
            case ESTORNO_PAGAMENTO:
            case ESTORNO_PAGAMENTO_RESTO:
                PagamentoEstorno pagamentoEstorno = recuperarPagamentoEstorno(movimentoConciliacaoBancaria);
                validarEstornoDeConciliacao(pagamentoEstorno.getDataConciliacao(), pagamentoEstorno.getUnidadeOrganizacional());
                pagamentoEstorno.setDataConciliacao(null);
                getFacade().getPagamentoEstornoFacade().salvar(pagamentoEstorno);
                break;
            case DESPESA_EXTRA:
                PagamentoExtra pagamentoExtra = recuperarPagamentoExtra(movimentoConciliacaoBancaria);
                validarEstornoDeConciliacao(pagamentoExtra.getDataConciliacao(), pagamentoExtra.getUnidadeOrganizacional());
                pagamentoExtra.setDataConciliacao(null);
                getFacade().getPagamentoExtraFacade().salvar(pagamentoExtra);
                break;
            case ESTORNO_DESPESA_EXTRA:
                PagamentoExtraEstorno pagamentoExtraEstorno = recuperarPagamentoExtraEstorno(movimentoConciliacaoBancaria);
                validarEstornoDeConciliacao(pagamentoExtraEstorno.getDataConciliacao(), pagamentoExtraEstorno.getUnidadeOrganizacional());
                pagamentoExtraEstorno.setDataConciliacao(null);
                getFacade().getPagamentoExtraEstornoFacade().salvar(pagamentoExtraEstorno);
                break;
            case RECEITA_EXTRA:
                ReceitaExtra receitaExtra = recuperarReceitaExtra(movimentoConciliacaoBancaria);
                validarEstornoDeConciliacao(receitaExtra.getDataConciliacao(), receitaExtra.getUnidadeOrganizacional());
                receitaExtra.setDataConciliacao(null);
                getFacade().getReceitaExtraFacade().salvar(receitaExtra);
                break;
            case ESTORNO_RECEITA_EXTRA:
                ReceitaExtraEstorno receitaExtraEstorno = recuperarReceitaExtraEstorno(movimentoConciliacaoBancaria);
                validarEstornoDeConciliacao(receitaExtraEstorno.getDataConciliacao(), receitaExtraEstorno.getUnidadeOrganizacional());
                receitaExtraEstorno.setDataConciliacao(null);
                getFacade().getReceitaExtraEstornoFacade().salvar(receitaExtraEstorno);
                break;
            case RECEITA_REALIZADA:
                LancamentoReceitaOrc lancamentoReceitaOrc = recuperarLancamentoReceitaOrc(movimentoConciliacaoBancaria);
                validarEstornoDeConciliacao(lancamentoReceitaOrc.getDataConciliacao(), lancamentoReceitaOrc.getUnidadeOrganizacional());
                lancamentoReceitaOrc.setDataConciliacao(null);
                getFacade().getLancamentoReceitaOrcFacade().salvar(lancamentoReceitaOrc);
                break;
            case ESTORNO_RECEITA_REALIZADA:
                ReceitaORCEstorno receitaORCEstorno = recuperarReceitaOrcEstorno(movimentoConciliacaoBancaria);
                validarEstornoDeConciliacao(receitaORCEstorno.getDataConciliacao(), receitaORCEstorno.getUnidadeOrganizacionalOrc());
                receitaORCEstorno.setDataConciliacao(null);
                getFacade().getReceitaORCEstornoFacade().salvar(receitaORCEstorno);
                break;
            case LIBERACAO_FINANCEIRA:
            case LIBERACAO_FINANCEIRA_REPASSE:
                LiberacaoCotaFinanceira liberacaoCotaFinanceira = recuperarLiberacaoCotaFinanceira(movimentoConciliacaoBancaria);
                if (movimentoConciliacaoBancaria.getTipoOperacaoConciliacao().isOperacaoCredito()) {
                    validarEstornoDeConciliacao(liberacaoCotaFinanceira.getDataConciliacao(), liberacaoCotaFinanceira.getUnidadeRetirada());
                    liberacaoCotaFinanceira.setDataConciliacao(null);
                    getFacade().getLiberacaoCotaFinanceiraFacade().salvar(liberacaoCotaFinanceira);
                } else {
                    validarEstornoDeConciliacao(liberacaoCotaFinanceira.getRecebida(), liberacaoCotaFinanceira.getUnidadeRecebida());
                    liberacaoCotaFinanceira.setRecebida(null);
                    getFacade().getLiberacaoCotaFinanceiraFacade().salvar(liberacaoCotaFinanceira);
                }
                break;
            case ESTORNO_LIBERACAO_FINANCEIRA:
            case ESTORNO_LIBERACAO_FINANCEIRA_REPASSE:
                EstornoLibCotaFinanceira estornoLibCotaFinanceira = recuperarEstornoLibCotaFinanceira(movimentoConciliacaoBancaria);
                if (movimentoConciliacaoBancaria.getTipoOperacaoConciliacao().isOperacaoCredito()) {
                    validarEstornoDeConciliacao(estornoLibCotaFinanceira.getDataConciliacao(), estornoLibCotaFinanceira.getUnidadeOrganizacional());
                    estornoLibCotaFinanceira.setDataConciliacao(null);
                    getFacade().getEstornoLibCotaFinanceiraFacade().salvar(estornoLibCotaFinanceira);
                } else {
                    validarEstornoDeConciliacao(estornoLibCotaFinanceira.getRecebida(), estornoLibCotaFinanceira.getUnidadeOrganizacional());
                    estornoLibCotaFinanceira.setRecebida(null);
                    getFacade().getEstornoLibCotaFinanceiraFacade().salvar(estornoLibCotaFinanceira);
                }
                break;
            case TRANSFERENCIA_FINANCEIRA_DEPOSITO:
                TransferenciaContaFinanceira transferenciaDeposito = recuperarTransferenciaContaFinanceira(movimentoConciliacaoBancaria);
                validarEstornoDeConciliacao(transferenciaDeposito.getRecebida(), transferenciaDeposito.getUnidadeOrganizacional());
                transferenciaDeposito.setRecebida(null);
                getFacade().getTransferenciaContaFinanceiraFacade().salvar(transferenciaDeposito);
                break;
            case TRANSFERENCIA_FINANCEIRA_RETIRADA:
                TransferenciaContaFinanceira transferenciaRetirada = recuperarTransferenciaContaFinanceira(movimentoConciliacaoBancaria);
                validarEstornoDeConciliacao(transferenciaRetirada.getDataConciliacao(), transferenciaRetirada.getUnidadeOrganizacional());
                transferenciaRetirada.setDataConciliacao(null);
                getFacade().getTransferenciaContaFinanceiraFacade().salvar(transferenciaRetirada);
                break;
            case ESTORNO_TRANSFERENCIA_FINANCEIRA_DEPOSITO:
                EstornoTransferencia estornoTransferenciaDeposito = recuperarEstornoTransferencia(movimentoConciliacaoBancaria);
                validarEstornoDeConciliacao(estornoTransferenciaDeposito.getDataConciliacao(), estornoTransferenciaDeposito.getUnidadeOrganizacional());
                estornoTransferenciaDeposito.setRecebida(null);
                getFacade().getEstornoTransferenciaFacade().salvar(estornoTransferenciaDeposito);
                break;
            case ESTORNO_TRANSFERENCIA_FINANCEIRA_RETIRADA:
                EstornoTransferencia estornoTransferenciaRetirada = recuperarEstornoTransferencia(movimentoConciliacaoBancaria);
                validarEstornoDeConciliacao(estornoTransferenciaRetirada.getDataConciliacao(), estornoTransferenciaRetirada.getUnidadeOrganizacional());
                estornoTransferenciaRetirada.setDataConciliacao(null);
                getFacade().getEstornoTransferenciaFacade().salvar(estornoTransferenciaRetirada);
                break;
            case AJUSTE_ATIVO_DISPONIVEL_AUMENTATIVO:
            case AJUSTE_ATIVO_DISPONIVEL_DIMINUTIVO:
            case ESTORNO_AJUSTE_ATIVO_DISPONIVEL_AUMENTATIVO:
            case ESTORNO_AJUSTE_ATIVO_DISPONIVEL_DIMINUTIVO:
                AjusteAtivoDisponivel ajusteAtivoDisponivel = recuperarAjusteAtivoDisponivel(movimentoConciliacaoBancaria);
                validarEstornoDeConciliacao(ajusteAtivoDisponivel.getDataConciliacao(), ajusteAtivoDisponivel.getUnidadeOrganizacional());
                ajusteAtivoDisponivel.setDataConciliacao(null);
                getFacade().getAjusteAtivoDisponivelFacade().salvar(ajusteAtivoDisponivel);
                break;
        }
        if (buscarMovimentos) {
            FacesUtil.addOperacaoRealizada("Estorno da conciliação do movimento realizada com sucesso.");
            buscarPendencias();
        }
    }

    public void conciliarMovimento(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria, Boolean buscarMovimentos) {
        try {
            getFacade().conciliarMovimento(movimentoConciliacaoBancaria, conciliacao.getData());
            if (buscarMovimentos) {
                FacesUtil.addOperacaoRealizada("Movimento conciliado com sucesso.");
                buscarPendencias();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private EstornoTransferencia recuperarEstornoTransferencia(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        return getFacade().getEstornoTransferenciaFacade().recuperar(movimentoConciliacaoBancaria.getMovimentoId());
    }

    private EstornoLibCotaFinanceira recuperarEstornoLibCotaFinanceira(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        return getFacade().getEstornoLibCotaFinanceiraFacade().recuperar(movimentoConciliacaoBancaria.getMovimentoId());
    }

    private ReceitaORCEstorno recuperarReceitaOrcEstorno(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        return getFacade().getReceitaORCEstornoFacade().recuperar(movimentoConciliacaoBancaria.getMovimentoId());
    }

    private ReceitaExtraEstorno recuperarReceitaExtraEstorno(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        return getFacade().getReceitaExtraEstornoFacade().recuperar(movimentoConciliacaoBancaria.getMovimentoId());
    }

    private PagamentoExtraEstorno recuperarPagamentoExtraEstorno(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        return getFacade().getPagamentoExtraEstornoFacade().recuperar(movimentoConciliacaoBancaria.getMovimentoId());
    }

    private PagamentoEstorno recuperarPagamentoEstorno(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        return getFacade().getPagamentoEstornoFacade().recuperar(movimentoConciliacaoBancaria.getMovimentoId());
    }

    private void recuperarLancamentoConciliacaBancaria(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        this.lancamentoConciliacaoBancaria = getFacade().recuperar(movimentoConciliacaoBancaria.getMovimentoId());
    }

    public void identificarMovimento(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        try {
            validarIdentificador();
            switch (movimentoConciliacaoBancaria.getTipoMovimento()) {
                case LANCCONCILIACAOBANCARIA:
                    recuperarLancamentoConciliacaBancaria(movimentoConciliacaoBancaria);
                    lancamentoConciliacaoBancaria.setIdentificador(identificador);
                    getFacade().salvar(lancamentoConciliacaoBancaria);
                    break;
                case PAGAMENTO:
                case PAGAMENTO_RESTO:
                    Pagamento pagamento = recuperarPagamento(movimentoConciliacaoBancaria);
                    pagamento.setIdentificador(identificador);
                    getFacade().getPagamentoFacade().salvar(pagamento);
                    break;
                case ESTORNO_PAGAMENTO:
                case ESTORNO_PAGAMENTO_RESTO:
                    PagamentoEstorno pagamentoEstorno = recuperarPagamentoEstorno(movimentoConciliacaoBancaria);
                    pagamentoEstorno.setIdentificador(identificador);
                    getFacade().getPagamentoEstornoFacade().salvar(pagamentoEstorno);
                    break;
                case DESPESA_EXTRA:
                    PagamentoExtra pagamentoExtra = recuperarPagamentoExtra(movimentoConciliacaoBancaria);
                    pagamentoExtra.setIdentificador(identificador);
                    getFacade().getPagamentoExtraFacade().salvar(pagamentoExtra);
                    break;
                case ESTORNO_DESPESA_EXTRA:
                    PagamentoExtraEstorno pagamentoExtraEstorno = recuperarPagamentoExtraEstorno(movimentoConciliacaoBancaria);
                    pagamentoExtraEstorno.setIdentificador(identificador);
                    getFacade().getPagamentoExtraEstornoFacade().salvar(pagamentoExtraEstorno);
                    break;
                case RECEITA_EXTRA:
                    ReceitaExtra receitaExtra = recuperarReceitaExtra(movimentoConciliacaoBancaria);
                    receitaExtra.setIdentificador(identificador);
                    getFacade().getReceitaExtraFacade().salvar(receitaExtra);
                    break;
                case ESTORNO_RECEITA_EXTRA:
                    ReceitaExtraEstorno receitaExtraEstorno = recuperarReceitaExtraEstorno(movimentoConciliacaoBancaria);
                    receitaExtraEstorno.setIdentificador(identificador);
                    getFacade().getReceitaExtraEstornoFacade().salvar(receitaExtraEstorno);
                    break;
                case RECEITA_REALIZADA:
                    LancamentoReceitaOrc lancamentoReceitaOrc = recuperarLancamentoReceitaOrc(movimentoConciliacaoBancaria);
                    lancamentoReceitaOrc.setIdentificador(identificador);
                    getFacade().getLancamentoReceitaOrcFacade().salvar(lancamentoReceitaOrc);
                    break;
                case ESTORNO_RECEITA_REALIZADA:
                    ReceitaORCEstorno receitaORCEstorno = recuperarReceitaOrcEstorno(movimentoConciliacaoBancaria);
                    receitaORCEstorno.setIdentificador(identificador);
                    getFacade().getReceitaORCEstornoFacade().salvar(receitaORCEstorno);
                    break;
                case LIBERACAO_FINANCEIRA:
                case LIBERACAO_FINANCEIRA_REPASSE:
                    LiberacaoCotaFinanceira liberacaoCotaFinanceira = recuperarLiberacaoCotaFinanceira(movimentoConciliacaoBancaria);
                    liberacaoCotaFinanceira.setIdentificador(identificador);
                    getFacade().getLiberacaoCotaFinanceiraFacade().salvar(liberacaoCotaFinanceira);
                    break;
                case ESTORNO_LIBERACAO_FINANCEIRA:
                case ESTORNO_LIBERACAO_FINANCEIRA_REPASSE:
                    EstornoLibCotaFinanceira estornoLibCotaFinanceira = recuperarEstornoLibCotaFinanceira(movimentoConciliacaoBancaria);
                    estornoLibCotaFinanceira.setIdentificador(identificador);
                    getFacade().getEstornoLibCotaFinanceiraFacade().salvar(estornoLibCotaFinanceira);
                    break;
                case TRANSFERENCIA_FINANCEIRA_DEPOSITO:
                case TRANSFERENCIA_FINANCEIRA_RETIRADA:
                    TransferenciaContaFinanceira transferenciaContaFinanceira = recuperarTransferenciaContaFinanceira(movimentoConciliacaoBancaria);
                    transferenciaContaFinanceira.setIdentificador(identificador);
                    getFacade().getTransferenciaContaFinanceiraFacade().salvar(transferenciaContaFinanceira);
                    break;
                case ESTORNO_TRANSFERENCIA_FINANCEIRA_DEPOSITO:
                case ESTORNO_TRANSFERENCIA_FINANCEIRA_RETIRADA:
                    EstornoTransferencia estornoTransferencia = recuperarEstornoTransferencia(movimentoConciliacaoBancaria);
                    estornoTransferencia.setIdentificador(identificador);
                    getFacade().getEstornoTransferenciaFacade().salvar(estornoTransferencia);
                    break;
                case AJUSTE_ATIVO_DISPONIVEL_AUMENTATIVO:
                case AJUSTE_ATIVO_DISPONIVEL_DIMINUTIVO:
                case ESTORNO_AJUSTE_ATIVO_DISPONIVEL_AUMENTATIVO:
                case ESTORNO_AJUSTE_ATIVO_DISPONIVEL_DIMINUTIVO:
                    AjusteAtivoDisponivel ajusteAtivoDisponivel = recuperarAjusteAtivoDisponivel(movimentoConciliacaoBancaria);
                    ajusteAtivoDisponivel.setIdentificador(identificador);
                    getFacade().getAjusteAtivoDisponivelFacade().salvar(ajusteAtivoDisponivel);
                    break;
            }
            buscarPendencias();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private AjusteAtivoDisponivel recuperarAjusteAtivoDisponivel(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        return getFacade().getAjusteAtivoDisponivelFacade().recuperar(movimentoConciliacaoBancaria.getMovimentoId());
    }

    private TransferenciaContaFinanceira recuperarTransferenciaContaFinanceira(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        return getFacade().getTransferenciaContaFinanceiraFacade().recuperar(movimentoConciliacaoBancaria.getMovimentoId());
    }

    private LiberacaoCotaFinanceira recuperarLiberacaoCotaFinanceira(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        return getFacade().getLiberacaoCotaFinanceiraFacade().recuperar(movimentoConciliacaoBancaria.getMovimentoId());
    }

    private LancamentoReceitaOrc recuperarLancamentoReceitaOrc(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        return getFacade().getLancamentoReceitaOrcFacade().recuperar(movimentoConciliacaoBancaria.getMovimentoId());
    }

    private ReceitaExtra recuperarReceitaExtra(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        return getFacade().getReceitaExtraFacade().recuperar(movimentoConciliacaoBancaria.getMovimentoId());
    }

    private PagamentoExtra recuperarPagamentoExtra(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        return getFacade().getPagamentoExtraFacade().recuperar(movimentoConciliacaoBancaria.getMovimentoId());
    }

    private Pagamento recuperarPagamento(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        return getFacade().getPagamentoFacade().recuperar(movimentoConciliacaoBancaria.getMovimentoId());
    }

    private void validarIdentificador() {
        ValidacaoException ve = new ValidacaoException();
        if (identificador == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Identificador é obrigatório para identificar um movimento.");
        }
        ve.lancarException();
    }

    public void alterarMovimento(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria) {
        if (MovimentoConciliacaoBancaria.TipoMovimento.LANCCONCILIACAOBANCARIA.equals(movimentoConciliacaoBancaria.getTipoMovimento())) {
            recuperarLancamentoConciliacaBancaria(movimentoConciliacaoBancaria);
            definirUnidadeDaContaFinancieraNaPendencia();
        }
    }

    public void gerarRelatorioConciliacao(Boolean diferenteZero) {
        try {
            validarRelatorioConciliacao(diferenteZero);
            RelatorioConciliacaoBancariaPorIdentificadorControlador relatorioConciliacaoBancariaPorIdentificadorControlador = (RelatorioConciliacaoBancariaPorIdentificadorControlador) Util.getControladorPeloNome("relatorioConciliacaoBancariaPorIdentificadorControlador");
            relatorioConciliacaoBancariaPorIdentificadorControlador.setFiltros("");
            relatorioConciliacaoBancariaPorIdentificadorControlador.setContaBancariaEntidade(saldoConstContaBancaria.getContaBancariaEntidade());
            relatorioConciliacaoBancariaPorIdentificadorControlador.setDataReferencia(conciliacao.getData());
            relatorioConciliacaoBancariaPorIdentificadorControlador.setListaUnidades(new ArrayList<HierarquiaOrganizacional>());
            relatorioConciliacaoBancariaPorIdentificadorControlador.setDiferenteZero(diferenteZero);
            relatorioConciliacaoBancariaPorIdentificadorControlador.gerarRelatorio();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public String getCaminhoOrigem() {
        return "/conciliar-por-identificador/";
    }

    public List<MovimentoConciliacaoBancaria> getMovimentosSemIdentificador() {
        return movimentosSemIdentificador;
    }

    public void setMovimentosSemIdentificador(List<MovimentoConciliacaoBancaria> movimentosSemIdentificador) {
        this.movimentosSemIdentificador = movimentosSemIdentificador;
    }

    public List<IdentificadorMovimentoConciliacaoBancaria> getMovimentosComIdentificador() {
        return movimentosComIdentificador;
    }

    public void setMovimentosComIdentificador(List<IdentificadorMovimentoConciliacaoBancaria> movimentosComIdentificador) {
        this.movimentosComIdentificador = movimentosComIdentificador;
    }

    public Identificador getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Identificador identificador) {
        this.identificador = identificador;
    }

    public void navegarParaConciliacaoManual() {
        Web.navegacao(getCaminhoOrigem(), redirecionarConciliacaoManual(), conciliacao);
    }

    public void navegarParaGerarReceita() {
        Web.navegacao(getCaminhoOrigem(), "/receita-realizada/novo/", conciliacao);
    }
}
