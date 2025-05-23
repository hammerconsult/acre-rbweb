/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Intervalo;
import br.com.webpublico.enums.NaturezaDividaAtivaCreditoReceber;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author claudio
 */
@Stateless
public class SaldoCreditoReceberFacade extends AbstractFacade<SaldoCreditoReceber> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public SaldoCreditoReceberFacade() {
        super(SaldoCreditoReceber.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void gerarSaldoCreditoReceber(CreditoReceber creditoReceber, Boolean validarSaldoNegativo) throws ExcecaoNegocioGenerica {
        SaldoCreditoReceber ultimoSaldo = getUltimoSaldoPorDataUnidadeConta(creditoReceber);
        List<SaldoCreditoReceber> saldosPosteriores = getSaldosPosterioresPorDataUnidadeConta(creditoReceber);

        if (ultimoSaldo == null || ultimoSaldo.getId() == null) {
            ultimoSaldo = criarNovoSaldo(creditoReceber);
            ultimoSaldo = definirValorNaColunaCorrespondente(ultimoSaldo, creditoReceber);
            salvar(ultimoSaldo);
        } else {
            if (DataUtil.dataSemHorario(ultimoSaldo.getDataSaldo()).compareTo(DataUtil.dataSemHorario(creditoReceber.getDataCredito())) == 0) {
                ultimoSaldo = definirValorNaColunaCorrespondente(ultimoSaldo, creditoReceber);
                salvar(ultimoSaldo);
            } else {
                SaldoCreditoReceber novoSaldo;
                novoSaldo = criarNovoSaldo(creditoReceber);
                novoSaldo = definirValoresDoSaldoRecuperado(ultimoSaldo, novoSaldo);
                novoSaldo = definirValorNaColunaCorrespondente(novoSaldo, creditoReceber);
                salvarNovo(novoSaldo);
            }
        }
        gerarSaldoPosteriores(creditoReceber, saldosPosteriores);
    }

    private void gerarSaldoPosteriores(CreditoReceber creditoReceber, List<SaldoCreditoReceber> saldosPosteriores) {
        if (!saldosPosteriores.isEmpty()) {
            for (SaldoCreditoReceber saldoParaAtualizar : saldosPosteriores) {
                saldoParaAtualizar = definirValorNaColunaCorrespondente(saldoParaAtualizar, creditoReceber);
                salvar(saldoParaAtualizar);
            }
        }
    }

    private SaldoCreditoReceber criarNovoSaldo(CreditoReceber cr) {
        SaldoCreditoReceber saldo = new SaldoCreditoReceber();
        saldo.setDataSaldo(cr.getDataCredito());
        saldo.setUnidadeOrganizacional(cr.getUnidadeOrganizacional());
        saldo.setContaReceita(cr.getReceitaLOA().getContaDeReceita());
        saldo.setBaixaDeducao(BigDecimal.ZERO);
        saldo.setBaixaReconhecimento(BigDecimal.ZERO);
        saldo.setDeducaoReconhecimento(BigDecimal.ZERO);
        saldo.setReconhecimentoCredito(BigDecimal.ZERO);
        saldo.setProvisaoPerdaCredito(BigDecimal.ZERO);
        saldo.setReversaoProvisaoPerdaCredito(BigDecimal.ZERO);
        saldo.setRecebimento(BigDecimal.ZERO);
        saldo.setDiminutivo(BigDecimal.ZERO);
        saldo.setAumentativo(BigDecimal.ZERO);
        saldo.setAtualizacao(BigDecimal.ZERO);
        saldo.setNaturezaCreditoReceber(cr.getNaturezaCreditoReceber());
        saldo.setContaDeDestinacao(cr.getContaDeDestinacao());
        saldo.setIntervalo(cr.getIntervalo());
        return saldo;
    }

    private SaldoCreditoReceber criarNovoSaldo(Date data, UnidadeOrganizacional unidadeOrganizacional, Conta contaReceita, NaturezaDividaAtivaCreditoReceber naturezaCreditoReceber, ContaDeDestinacao contaDeDestinacao, Intervalo intervalo) {
        SaldoCreditoReceber saldo = new SaldoCreditoReceber();
        saldo.setDataSaldo(data);
        saldo.setUnidadeOrganizacional(unidadeOrganizacional);
        saldo.setContaReceita(contaReceita);
        saldo.setBaixaDeducao(BigDecimal.ZERO);
        saldo.setBaixaReconhecimento(BigDecimal.ZERO);
        saldo.setDeducaoReconhecimento(BigDecimal.ZERO);
        saldo.setReconhecimentoCredito(BigDecimal.ZERO);
        saldo.setProvisaoPerdaCredito(BigDecimal.ZERO);
        saldo.setReversaoProvisaoPerdaCredito(BigDecimal.ZERO);
        saldo.setRecebimento(BigDecimal.ZERO);
        saldo.setDiminutivo(BigDecimal.ZERO);
        saldo.setAumentativo(BigDecimal.ZERO);
        saldo.setAtualizacao(BigDecimal.ZERO);
        saldo.setNaturezaCreditoReceber(naturezaCreditoReceber);
        saldo.setContaDeDestinacao(contaDeDestinacao);
        saldo.setIntervalo(intervalo);
        return saldo;
    }

    private SaldoCreditoReceber definirValorNaColunaCorrespondente(SaldoCreditoReceber saldo, CreditoReceber cr) {
        switch (cr.getOperacaoCreditoReceber()) {
            case BAIXA_DEDUCAO_RECONHECIMENTO_CREDITO_A_RECEBER:
                saldo = atualizarValorBaixaDeducao(saldo, cr);
                break;
            case BAIXA_RECONHECIMENTO_CREDITO_A_RECEBER:
                saldo = atualizarValorBaixaReconhecimento(saldo, cr);
                break;
            case DEDUCAO_RECONHECIMENTO_CREDITO_A_RECEBER:
                saldo = atualizarValorDeducaoReconhecimento(saldo, cr);
                break;
            case RECONHECIMENTO_CREDITO_A_RECEBER:
                saldo = atualizarValorReconhecimentoCredito(saldo, cr);
                break;
            case AJUSTE_PERDAS_CREDITOS_A_RECEBER:
            case AJUSTE_PERDAS_CREDITOS_A_RECEBER_LONGO_PRAZO:
                saldo = atualizarValorProvisaoPerdaCredito(saldo, cr);
                break;
            case REVERSAO_AJUSTE_PERDAS_CREDITO_A_RECEBER:
            case REVERSAO_AJUSTE_PERDAS_CREDITO_A_RECEBER_LONGO_PRAZO:
                saldo = atualizarValorReversaoProvisaoPerdaCredito(saldo, cr);
                break;
            case RECEBIMENTO:
                saldo = atualizarValorRecebimento(saldo, cr);
                break;
            case AJUSTE_CREDITOS_A_RECEBER_AUMENTATIVO:
            case AJUSTE_CREDITOS_A_RECEBER_AUMENTATIVO_EMPRESA_PUBLICA:
                saldo = atualizarValorAumentativo(saldo, cr);
                break;
            case AJUSTE_CREDITOS_A_RECEBER_DIMINUTIVO:
            case AJUSTE_CREDITOS_A_RECEBER_DIMINUTIVO_EMPRESA_PUBLICA:
                saldo = atualizarValorDiminutivo(saldo, cr);
                break;
            case ATUALIZACAO_DE_CREDITOS_A_RECEBER:
                saldo = atualizarValorAtualizacao(saldo, cr);
                break;
            case TRANSFERENCIA_CREDITO_A_RECEBER_CURTO_PARA_LONGO_PRAZO:
                saldo = atualizarValorBaixaReconhecimento(saldo, cr);
                SaldoCreditoReceber saldoLongoPrazo = buscarUltimoSaldoPorDataUnidadeContaNaturezaIntervalo(cr.getDataCredito(),
                    saldo.getUnidadeOrganizacional(),
                    saldo.getContaReceita(),
                    NaturezaDividaAtivaCreditoReceber.ORIGINAL,
                    Intervalo.LONGO_PRAZO,
                    saldo.getContaDeDestinacao());
                atualizarSaldoTransferenciaCurtoParaLongo(saldo, saldoLongoPrazo, cr);
                break;
            case TRANSFERENCIA_CREDITO_A_RECEBER_LONGO_PARA_CURTO_PRAZO:
                saldo = atualizarValorBaixaReconhecimento(saldo, cr);
                SaldoCreditoReceber saldoCurtoPrazo = buscarUltimoSaldoPorDataUnidadeContaNaturezaIntervalo(cr.getDataCredito(),
                    saldo.getUnidadeOrganizacional(),
                    saldo.getContaReceita(),
                    NaturezaDividaAtivaCreditoReceber.ORIGINAL,
                    Intervalo.CURTO_PRAZO,
                    saldo.getContaDeDestinacao());
                atualizarSaldoTransferenciaLongoParaCurto(saldo, saldoCurtoPrazo, cr);
                break;
            case TRANSFERENCIA_AJUSTE_PERDAS_CREDITO_A_RECEBER_CURTO_PARA_LONGO_PRAZO:
                saldo = atualizarValorAtualizacao(saldo, cr);
                SaldoCreditoReceber saldoAjustePerdaLongoPrazo = buscarUltimoSaldoPorDataUnidadeContaNaturezaIntervalo(cr.getDataCredito(),
                    saldo.getUnidadeOrganizacional(),
                    saldo.getContaReceita(),
                    NaturezaDividaAtivaCreditoReceber.AJUSTE_DE_PERDAS,
                    Intervalo.LONGO_PRAZO,
                    saldo.getContaDeDestinacao());
                atualizarSaldoTransferenciaAjustePerdaCurtoParaLongo(saldo, saldoAjustePerdaLongoPrazo, cr);
                break;
            case TRANSFERENCIA_AJUSTE_PERDAS_CREDITO_A_RECEBER_LONGO_PARA_CURTO_PRAZO:
                saldo = atualizarValorAtualizacao(saldo, cr);
                SaldoCreditoReceber saldoAjustePerdaCurtoPrazo = buscarUltimoSaldoPorDataUnidadeContaNaturezaIntervalo(cr.getDataCredito(),
                    saldo.getUnidadeOrganizacional(),
                    saldo.getContaReceita(),
                    NaturezaDividaAtivaCreditoReceber.AJUSTE_DE_PERDAS,
                    Intervalo.CURTO_PRAZO,
                    saldo.getContaDeDestinacao());
                atualizarSaldoTransferenciaAjustePerdaLongoParaCurto(saldo, saldoAjustePerdaCurtoPrazo, cr);
                break;
            default:
                break;
        }
        return saldo;
    }

    private SaldoCreditoReceber definirValoresDoSaldoRecuperado(SaldoCreditoReceber saldo, SaldoCreditoReceber novoSaldo) {
        novoSaldo.setBaixaDeducao(saldo.getBaixaDeducao());
        novoSaldo.setBaixaReconhecimento(saldo.getBaixaReconhecimento());
        novoSaldo.setDeducaoReconhecimento(saldo.getDeducaoReconhecimento());
        novoSaldo.setProvisaoPerdaCredito(saldo.getProvisaoPerdaCredito());
        novoSaldo.setReconhecimentoCredito(saldo.getReconhecimentoCredito());
        novoSaldo.setReversaoProvisaoPerdaCredito(saldo.getReversaoProvisaoPerdaCredito());
        novoSaldo.setRecebimento(saldo.getRecebimento());
        novoSaldo.setAumentativo(saldo.getAumentativo());
        novoSaldo.setDiminutivo(saldo.getDiminutivo());
        novoSaldo.setAtualizacao(saldo.getAtualizacao());
        return novoSaldo;
    }

    private void atualizarSaldoTransferenciaCurtoParaLongo(SaldoCreditoReceber saldo, SaldoCreditoReceber saldoCreditoCurtoOrLongoPrazo, CreditoReceber creditoReceber) {
        if (saldoCreditoCurtoOrLongoPrazo.getId() != null && DataUtil.dataSemHorario(saldoCreditoCurtoOrLongoPrazo.getDataSaldo()).compareTo(DataUtil.dataSemHorario(saldo.getDataSaldo())) == 0) {
            saldoCreditoCurtoOrLongoPrazo = atualizarValorAtualizacao(saldoCreditoCurtoOrLongoPrazo, creditoReceber);
            salvar(saldoCreditoCurtoOrLongoPrazo);
        } else {
            SaldoCreditoReceber novoSaldo = criarNovoSaldo(saldo.getDataSaldo(), saldo.getUnidadeOrganizacional(), saldo.getContaReceita(), saldo.getNaturezaCreditoReceber(), saldo.getContaDeDestinacao(), Intervalo.LONGO_PRAZO);
            novoSaldo = definirValoresDoSaldoRecuperado(saldoCreditoCurtoOrLongoPrazo, novoSaldo);
            novoSaldo = atualizarValorAtualizacao(novoSaldo, creditoReceber);
            salvarNovo(novoSaldo);
        }
    }

    private void atualizarSaldoTransferenciaLongoParaCurto(SaldoCreditoReceber saldo, SaldoCreditoReceber saldoCreditoCurtoOrLongoPrazo, CreditoReceber creditoReceber) {
        if (saldoCreditoCurtoOrLongoPrazo.getId() != null && DataUtil.dataSemHorario(saldoCreditoCurtoOrLongoPrazo.getDataSaldo()).compareTo(DataUtil.dataSemHorario(saldo.getDataSaldo())) == 0) {
            saldoCreditoCurtoOrLongoPrazo = atualizarValorAtualizacao(saldoCreditoCurtoOrLongoPrazo, creditoReceber);
            salvar(saldoCreditoCurtoOrLongoPrazo);
        } else {
            SaldoCreditoReceber novoSaldo = criarNovoSaldo(saldo.getDataSaldo(), saldo.getUnidadeOrganizacional(), saldo.getContaReceita(), saldo.getNaturezaCreditoReceber(), saldo.getContaDeDestinacao(), Intervalo.CURTO_PRAZO);
            novoSaldo = definirValoresDoSaldoRecuperado(saldoCreditoCurtoOrLongoPrazo, novoSaldo);
            novoSaldo = atualizarValorAtualizacao(novoSaldo, creditoReceber);
            salvarNovo(novoSaldo);
        }
    }

    private void atualizarSaldoTransferenciaAjustePerdaCurtoParaLongo(SaldoCreditoReceber saldo, SaldoCreditoReceber saldoCreditoCurtoOrLongoPrazo, CreditoReceber creditoReceber) {
        if (saldoCreditoCurtoOrLongoPrazo.getId() != null && DataUtil.dataSemHorario(saldoCreditoCurtoOrLongoPrazo.getDataSaldo()).compareTo(DataUtil.dataSemHorario(saldo.getDataSaldo())) == 0) {
            saldoCreditoCurtoOrLongoPrazo = atualizarValorBaixaReconhecimento(saldoCreditoCurtoOrLongoPrazo, creditoReceber);
            salvar(saldoCreditoCurtoOrLongoPrazo);
        } else {
            SaldoCreditoReceber novoSaldo = criarNovoSaldo(saldo.getDataSaldo(), saldo.getUnidadeOrganizacional(), saldo.getContaReceita(), saldo.getNaturezaCreditoReceber(), saldo.getContaDeDestinacao(), Intervalo.LONGO_PRAZO);
            novoSaldo = definirValoresDoSaldoRecuperado(saldoCreditoCurtoOrLongoPrazo, novoSaldo);
            novoSaldo = atualizarValorBaixaReconhecimento(novoSaldo, creditoReceber);
            salvarNovo(novoSaldo);
        }
    }

    private void atualizarSaldoTransferenciaAjustePerdaLongoParaCurto(SaldoCreditoReceber saldo, SaldoCreditoReceber saldoCreditoCurtoOrLongoPrazo, CreditoReceber creditoReceber) {
        if (saldoCreditoCurtoOrLongoPrazo.getId() != null && DataUtil.dataSemHorario(saldoCreditoCurtoOrLongoPrazo.getDataSaldo()).compareTo(DataUtil.dataSemHorario(saldo.getDataSaldo())) == 0) {
            saldoCreditoCurtoOrLongoPrazo = atualizarValorBaixaReconhecimento(saldoCreditoCurtoOrLongoPrazo, creditoReceber);
            salvar(saldoCreditoCurtoOrLongoPrazo);
        } else {
            SaldoCreditoReceber novoSaldo = criarNovoSaldo(saldo.getDataSaldo(), saldo.getUnidadeOrganizacional(), saldo.getContaReceita(), saldo.getNaturezaCreditoReceber(), saldo.getContaDeDestinacao(), Intervalo.CURTO_PRAZO);
            novoSaldo = definirValoresDoSaldoRecuperado(saldoCreditoCurtoOrLongoPrazo, novoSaldo);
            novoSaldo = atualizarValorBaixaReconhecimento(novoSaldo, creditoReceber);
            salvarNovo(novoSaldo);
        }
    }

    private SaldoCreditoReceber atualizarValorBaixaDeducao(SaldoCreditoReceber saldo, CreditoReceber cr) {
        if (cr.isLancamentoNormal()) {
            saldo.setBaixaDeducao(saldo.getBaixaDeducao().add(cr.getValor()));
        } else {
            saldo.setBaixaDeducao(saldo.getBaixaDeducao().subtract(cr.getValor()));
        }
        return saldo;
    }

    private SaldoCreditoReceber atualizarValorBaixaReconhecimento(SaldoCreditoReceber saldo, CreditoReceber cr) {
        if (cr.isLancamentoNormal()) {
            saldo.setBaixaReconhecimento(saldo.getBaixaReconhecimento().add(cr.getValor()));
        } else {
            saldo.setBaixaReconhecimento(saldo.getBaixaReconhecimento().subtract(cr.getValor()));
        }
        return saldo;
    }

    private SaldoCreditoReceber atualizarValorDeducaoReconhecimento(SaldoCreditoReceber saldo, CreditoReceber cr) {
        if (cr.isLancamentoNormal()) {
            saldo.setDeducaoReconhecimento(saldo.getDeducaoReconhecimento().add(cr.getValor()));
        } else {
            saldo.setDeducaoReconhecimento(saldo.getDeducaoReconhecimento().subtract(cr.getValor()));
        }
        return saldo;
    }

    private SaldoCreditoReceber atualizarValorReconhecimentoCredito(SaldoCreditoReceber saldo, CreditoReceber cr) {
        if (cr.isLancamentoNormal()) {
            saldo.setReconhecimentoCredito(saldo.getReconhecimentoCredito().add(cr.getValor()));
        } else {
            saldo.setReconhecimentoCredito(saldo.getReconhecimentoCredito().subtract(cr.getValor()));
        }
        return saldo;
    }

    private SaldoCreditoReceber atualizarValorProvisaoPerdaCredito(SaldoCreditoReceber saldo, CreditoReceber cr) {
        if (cr.isLancamentoNormal()) {
            saldo.setProvisaoPerdaCredito(saldo.getProvisaoPerdaCredito().add(cr.getValor()));
        } else {
            saldo.setProvisaoPerdaCredito(saldo.getProvisaoPerdaCredito().subtract(cr.getValor()));
        }
        return saldo;
    }

    private SaldoCreditoReceber atualizarValorReversaoProvisaoPerdaCredito(SaldoCreditoReceber saldo, CreditoReceber cr) {
        if (cr.isLancamentoNormal()) {
            saldo.setReversaoProvisaoPerdaCredito(saldo.getReversaoProvisaoPerdaCredito().add(cr.getValor()));
        } else {
            saldo.setReversaoProvisaoPerdaCredito(saldo.getReversaoProvisaoPerdaCredito().subtract(cr.getValor()));
        }
        return saldo;
    }

    private SaldoCreditoReceber atualizarValorRecebimento(SaldoCreditoReceber saldo, CreditoReceber cr) {
        if (cr.isLancamentoNormal()) {
            saldo.setRecebimento(saldo.getRecebimento().add(cr.getValor()));
        } else {
            saldo.setRecebimento(saldo.getRecebimento().subtract(cr.getValor()));
        }
        return saldo;
    }

    private SaldoCreditoReceber atualizarValorAumentativo(SaldoCreditoReceber saldo, CreditoReceber cr) {
        if (cr.isLancamentoNormal()) {
            saldo.setAumentativo(saldo.getAumentativo().add(cr.getValor()));
        } else {
            saldo.setAumentativo(saldo.getAumentativo().subtract(cr.getValor()));
        }
        return saldo;
    }

    private SaldoCreditoReceber atualizarValorDiminutivo(SaldoCreditoReceber saldo, CreditoReceber cr) {
        if (cr.isLancamentoNormal()) {
            saldo.setDiminutivo(saldo.getDiminutivo().add(cr.getValor()));
        } else {
            saldo.setDiminutivo(saldo.getDiminutivo().subtract(cr.getValor()));
        }
        return saldo;
    }

    private SaldoCreditoReceber atualizarValorAtualizacao(SaldoCreditoReceber saldo, CreditoReceber cr) {
        if (cr.isLancamentoNormal()) {
            saldo.setAtualizacao(saldo.getAtualizacao().add(cr.getValor()));
        } else {
            saldo.setAtualizacao(saldo.getAtualizacao().subtract(cr.getValor()));
        }
        return saldo;
    }

    private void validarSaldoPosteriorNegativo(List<SaldoCreditoReceber> saldosPosteriores, CreditoReceber cr, Boolean validarSaldoNegativo) {
        ValidacaoException ve = new ValidacaoException();
        if (validarSaldoNegativo) {
            for (SaldoCreditoReceber saldo : saldosPosteriores) {
                if (cr.isLancamentoEstorno() && saldo.getSaldoAtual().compareTo(cr.getValor()) < 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O valor que deseja estornar é maior que o saldo do crédito a receber disponível em datas posteriores.");
                }
                if (cr.isLancamentoEstorno() && getValorDaColuna(saldo, cr).subtract(cr.getValor()).compareTo(BigDecimal.ZERO) < 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O valor que deseja estornar é maior que o valor disponível em datas posteriores para '"
                        + cr.getOperacaoCreditoReceber().getDescricao() + "'. O valor ficará negativo.");

                }
                if (getValorFinalSaldo(saldo, cr).compareTo(BigDecimal.ZERO) < 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Efetuando esta operação o saldo do crédito a receber ficará negativo em datas posteriores na data " + DataUtil.getDataFormatada(saldo.getDataSaldo()) + ".");
                }
            }
        }
        ve.lancarException();
    }

    private void validarSaldo(SaldoCreditoReceber scr, List<SaldoCreditoReceber> saldosPosteriores, CreditoReceber cr, Boolean validarSaldoNegativo) {
        ValidacaoException ve = new ValidacaoException();
        if (validarSaldoNegativo) {
            if (cr.isLancamentoEstorno() && scr.getSaldoAtual().compareTo(cr.getValor()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor que deseja estornar é maior que o saldo do crédito a receber disponível.Data:" + DataUtil.getDataFormatada(scr.getDataSaldo()));
            }
            if (cr.isLancamentoEstorno() && getValorDaColuna(scr, cr).subtract(cr.getValor()).compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor que deseja estornar é maior que o valor disponível para '"
                    + cr.getOperacaoCreditoReceber().getDescricao() + "'. O valor ficará negativo. Data:" + DataUtil.getDataFormatada(scr.getDataSaldo()));
            }
            if (getValorFinalSaldo(scr, cr).compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Efetuando esta operação o saldo do crédito a receber ficará negativo. Valor Final: " + Util.formataValor(getValorFinalSaldo(scr, cr)) + "; Data:" + DataUtil.getDataFormatada(scr.getDataSaldo()));
            }
        }
        validarSaldoPosteriorNegativo(saldosPosteriores, cr, validarSaldoNegativo);
        ve.lancarException();
    }

    private BigDecimal getValorFinalSaldo(SaldoCreditoReceber saldoCreditoReceber, CreditoReceber cr) {
        if (isOperacaoReconhecimentoReversaoOuBaixadeducaoNormal(cr) || isOperacaoDeducaoProvisaoBaixareconhecimentoEstorno(cr)) {
            return saldoCreditoReceber.getSaldoAtual().add(cr.getValor());
        } else {
            return saldoCreditoReceber.getSaldoAtual().subtract(cr.getValor());
        }
    }

    private boolean isOperacaoReconhecimentoReversaoOuBaixadeducaoNormal(CreditoReceber cr) {
        return cr.isLancamentoNormal()
            && (cr.isOperacaoBaixaDeducaoReconhecimento() || cr.isOperacaoReconhecimento() || cr.isOperacaoReversaoAjustePerdas() || cr.isOperacaoReversaoAjustePerdasLongoPrazo());
    }

    private boolean isOperacaoDeducaoProvisaoBaixareconhecimentoEstorno(CreditoReceber cr) {
        return cr.isLancamentoEstorno()
            && (cr.isOperacaoDeducaoReconhecimento() || cr.isOperacaoAjustePerdas() || cr.isOperacaoAjustePerdasLongoPrazo() || cr.isOperacaoBaixaReconhecimento());
    }

    private BigDecimal getValorDaColuna(SaldoCreditoReceber saldoCreditoReceber, CreditoReceber cr) {
        if (cr.isOperacaoReconhecimento()) {
            return saldoCreditoReceber.getReconhecimentoCredito();
        }
        if (cr.isOperacaoDeducaoReconhecimento()) {
            return saldoCreditoReceber.getDeducaoReconhecimento();
        }
        if (cr.isOperacaoAjustePerdas() || cr.isOperacaoAjustePerdasLongoPrazo()) {
            return saldoCreditoReceber.getProvisaoPerdaCredito();
        }
        if (cr.isOperacaoReversaoAjustePerdas() || cr.isOperacaoReversaoAjustePerdasLongoPrazo()) {
            return saldoCreditoReceber.getReversaoProvisaoPerdaCredito();
        }
        if (cr.isOperacaoBaixaReconhecimento()) {
            return saldoCreditoReceber.getBaixaReconhecimento();
        }
        if (cr.isOperacaoBaixaDeducaoReconhecimento()) {
            return saldoCreditoReceber.getBaixaDeducao();
        }
        if (cr.isOperacaoRecebimento()) {
            return saldoCreditoReceber.getRecebimento();
        }
        if (cr.isOperacaoAumentativo()) {
            return saldoCreditoReceber.getAumentativo();
        }
        if (cr.isOperacaoDiminutivo()) {
            return saldoCreditoReceber.getDiminutivo();
        }
        if (cr.isOperacaoAtualizacao()) {
            return saldoCreditoReceber.getAtualizacao();
        }
        return BigDecimal.ZERO;
    }

    private SaldoCreditoReceber buscarUltimoSaldoPorDataUnidadeContaNaturezaIntervalo(Date data, UnidadeOrganizacional unidadeOrganizacional, Conta contaReceita, NaturezaDividaAtivaCreditoReceber naturezaCreditoReceber,
                                                                                      Intervalo intervalo, ContaDeDestinacao contaDeDestinacao) {
        String sql = getSqlSaldoCreditoReceber();

        Query q = em.createNativeQuery(sql, SaldoCreditoReceber.class);
        q.setParameter("dataSaldo", DataUtil.getDataFormatada(data));
        q.setParameter("unidadeOrganizacional", unidadeOrganizacional.getId());
        q.setParameter("contaReceita", contaReceita.getId());
        q.setParameter("natureza", naturezaCreditoReceber.name());
        q.setParameter("intervalo", intervalo.name());
        q.setParameter("contaDeDestinacao", contaDeDestinacao.getId());

        List<SaldoCreditoReceber> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return new SaldoCreditoReceber();
    }

    private String getSqlSaldoCreditoReceber() {
        return "select saldo.* from SaldoCreditoReceber saldo "
            + " where trunc(saldo.dataSaldo) <= to_date(:dataSaldo, 'dd/mm/yyyy') "
            + " and saldo.unidadeOrganizacional_id = :unidadeOrganizacional "
            + " and saldo.contaReceita_id = :contaReceita "
            + " and saldo.naturezaCreditoReceber = :natureza "
            + " and saldo.intervalo = :intervalo "
            + " and saldo.contaDeDestinacao_id = :contaDeDestinacao "
            + " order by saldo.dataSaldo desc";
    }


    private SaldoCreditoReceber getUltimoSaldoPorDataUnidadeConta(CreditoReceber cr) {
        String hql = getSqlSaldoCreditoReceber();

        Query q = em.createNativeQuery(hql, SaldoCreditoReceber.class);
        q.setParameter("dataSaldo", DataUtil.getDataFormatada(cr.getDataCredito()));
        q.setParameter("unidadeOrganizacional", cr.getUnidadeOrganizacional().getId());
        q.setParameter("contaReceita", cr.getReceitaLOA().getContaDeReceita().getId());
        q.setParameter("natureza", cr.getNaturezaCreditoReceber().name());
        q.setParameter("intervalo", cr.getIntervalo().name());
        q.setParameter("contaDeDestinacao", cr.getContaDeDestinacao().getId());
        List<SaldoCreditoReceber> resultado = q.getResultList();

        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return new SaldoCreditoReceber();
    }

    public List<SaldoCreditoReceber> getSaldosPosterioresPorDataUnidadeConta(CreditoReceber cr) {
        String hql = "select saldo.* from SaldoCreditoReceber saldo "
            + " where trunc(saldo.dataSaldo) > trunc(:dataSaldo) "
            + " and saldo.unidadeOrganizacional_id = :unidadeOrganizacional "
            + " and saldo.contaReceita_id = :contaReceita "
            + " and saldo.naturezaCreditoReceber = :natureza "
            + " and saldo.intervalo = :intervalo "
            + " and saldo.contaDeDestinacao_id = :contaDeDestinacao "
            + " order by saldo.dataSaldo desc";
        Query q = em.createNativeQuery(hql, SaldoCreditoReceber.class);
        q.setParameter("dataSaldo", DataUtil.dataSemHorario(cr.getDataCredito()));
        q.setParameter("unidadeOrganizacional", cr.getUnidadeOrganizacional().getId());
        q.setParameter("contaReceita", cr.getReceitaLOA().getContaDeReceita().getId());
        q.setParameter("natureza", cr.getNaturezaCreditoReceber().name());
        q.setParameter("intervalo", cr.getIntervalo().name());
        q.setParameter("contaDeDestinacao", cr.getContaDeDestinacao().getId());
        List<SaldoCreditoReceber> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado;
        }
        return Lists.newArrayList();
    }

    //Método criado apenas para utilizar no Reprocessamento de Saldo de Crédito a Receber
    public void excluirSaldosPeriodo(Date dataInicial, Date dataFinal) {
        String sql = " delete FROM saldocreditoreceber ssc "
            + " WHERE trunc(ssc.datasaldo) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        q.executeUpdate();
    }
}
