/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Intervalo;
import br.com.webpublico.enums.NaturezaDividaAtivaCreditoReceber;
import br.com.webpublico.enums.OperacaoCreditoReceber;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class SaldoDividaAtivaContabilFacade extends AbstractFacade<SaldoDividaAtivaContabil> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoCreditoReceberFacade saldoCreditoReceberFacade;

    public SaldoDividaAtivaContabilFacade() {
        super(SaldoDividaAtivaContabil.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    private List<SaldoDividaAtivaContabil> getSaldosPosterioresPorDataUnidadeConta(DividaAtivaContabil divida) {
        String hql = "select saldo from SaldoDividaAtivaContabil saldo "
            + " where trunc(saldo.dataSaldo) > to_date(:dataSaldo, 'dd/mm/yyyy') "
            + " and saldo.unidadeOrganizacional = :unidadeOrganizacional "
            + " and saldo.contaReceita = :contaReceita "
            + " and saldo.naturezaDividaAtiva = :natureza "
            + " and saldo.intervalo = :intervalo "
            + " and saldo.contaDeDestinacao = :contaDeDestinacao "
            + " order by saldo.dataSaldo desc";
        Query q = em.createQuery(hql);
        q.setParameter("dataSaldo", DataUtil.getDataFormatada(divida.getDataDivida()));
        q.setParameter("unidadeOrganizacional", divida.getUnidadeOrganizacional());
        q.setParameter("contaReceita", divida.getContaReceita());
        q.setParameter("natureza", divida.getNaturezaDividaAtiva());
        q.setParameter("intervalo", divida.getIntervalo());
        q.setParameter("contaDeDestinacao", divida.getContaDeDestinacao());

        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public void gerarSaldoDividaAtiva(DividaAtivaContabil divida, ContaReceita contaReceitaEquivalente) throws ExcecaoNegocioGenerica {
        ContaReceita contaReceita = definirContaReceita(divida, contaReceitaEquivalente);
        divida.setContaReceita(contaReceita);
        SaldoDividaAtivaContabil ultimoSaldo = getUltimoSaldoPorDataUnidadeConta(divida);
        if (ultimoSaldo == null || ultimoSaldo.getId() == null) {
            ultimoSaldo = criarNovoSaldo(divida);
            ultimoSaldo = atribuirValorParaColunaCorrespondente(ultimoSaldo, divida);
            salvar(ultimoSaldo);
        } else {
            if (DataUtil.getDataFormatada(ultimoSaldo.getDataSaldo()).compareTo(DataUtil.getDataFormatada(divida.getDataDivida())) == 0) {
                ultimoSaldo = atribuirValorParaColunaCorrespondente(ultimoSaldo, divida);
                salvar(ultimoSaldo);
            } else {
                SaldoDividaAtivaContabil novoSaldo = criarNovoSaldo(divida);
                novoSaldo = definirValoresParaNovo(ultimoSaldo, novoSaldo);
                novoSaldo = atribuirValorParaColunaCorrespondente(novoSaldo, divida);
                salvarNovo(novoSaldo);
            }
        }
        gerarSaldoPosteriores(divida);
    }

    private void gerarSaldoPosteriores(DividaAtivaContabil divida) {
        List<SaldoDividaAtivaContabil> saldosPosteriores = getSaldosPosterioresPorDataUnidadeConta(divida);
        if (!saldosPosteriores.isEmpty()) {
            for (SaldoDividaAtivaContabil saldoParaAtualizar : saldosPosteriores) {
                saldoParaAtualizar = atribuirValorParaColunaCorrespondente(saldoParaAtualizar, divida);
                salvar(saldoParaAtualizar);
            }
        }
    }

    private ContaReceita definirContaReceita(DividaAtivaContabil divida, ContaReceita contaReceitaEquivalente) {
        if (contaReceitaEquivalente == null) {
            if (divida.isInscricao()) {
                return ((ContaReceita) divida.getReceitaLOA().getContaDeReceita()).getCorrespondente();
            } else {
                return (ContaReceita) divida.getReceitaLOA().getContaDeReceita();
            }
        }
        return contaReceitaEquivalente;
    }

    private SaldoDividaAtivaContabil definirValoresParaNovo(SaldoDividaAtivaContabil saldoRecuperado, SaldoDividaAtivaContabil novoSaldo) {
        novoSaldo.setInscricao(saldoRecuperado.getInscricao());
        novoSaldo.setProvisao(saldoRecuperado.getProvisao());
        novoSaldo.setRecebimento(saldoRecuperado.getRecebimento());
        novoSaldo.setReversao(saldoRecuperado.getReversao());
        novoSaldo.setAtualizacao(saldoRecuperado.getAtualizacao());
        novoSaldo.setBaixa(saldoRecuperado.getBaixa());
        novoSaldo.setAumentativo(saldoRecuperado.getAumentativo());
        novoSaldo.setDiminutivo(saldoRecuperado.getDiminutivo());
        return novoSaldo;
    }

    private SaldoDividaAtivaContabil getUltimoSaldoPorDataUnidadeConta(DividaAtivaContabil dividaAtivaContabil) {

        String hql = buscarHqlSaldoAtual();
        Query q = em.createQuery(hql);
        q.setParameter("dataSaldo", DataUtil.getDataFormatada(dividaAtivaContabil.getDataDivida()));
        q.setParameter("unidadeOrganizacional", dividaAtivaContabil.getUnidadeOrganizacional());
        q.setParameter("contaReceita", dividaAtivaContabil.getContaReceita());
        q.setParameter("natureza", dividaAtivaContabil.getNaturezaDividaAtiva());
        q.setParameter("intervalo", dividaAtivaContabil.getIntervalo());
        q.setParameter("contaDeDestinacao", dividaAtivaContabil.getContaDeDestinacao());

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (SaldoDividaAtivaContabil) q.getResultList().get(0);
        }
        return new SaldoDividaAtivaContabil();
    }

    private SaldoDividaAtivaContabil buscarUltimoSaldoPorDataUnidadeContaNaturezaIntervalo(Date data, UnidadeOrganizacional unidadeOrganizacional, Conta contaReceita, NaturezaDividaAtivaCreditoReceber naturezaDividaAtiva,
                                                                                           Intervalo intervalo, ContaDeDestinacao contaDeDestinacao) {

        String hql = buscarHqlSaldoAtual();
        Query q = em.createQuery(hql);
        q.setParameter("dataSaldo", DataUtil.getDataFormatada(data));
        q.setParameter("unidadeOrganizacional", unidadeOrganizacional);
        q.setParameter("contaReceita", contaReceita);
        q.setParameter("natureza", naturezaDividaAtiva);
        q.setParameter("intervalo", intervalo);
        q.setParameter("contaDeDestinacao", contaDeDestinacao);
        List<SaldoDividaAtivaContabil> resultado = q.getResultList();
        if (resultado != null && !resultado.isEmpty()) {
            return resultado.get(0);
        }
        return new SaldoDividaAtivaContabil();
    }

    private String buscarHqlSaldoAtual() {
        return "select saldo from SaldoDividaAtivaContabil saldo "
            + " where trunc(saldo.dataSaldo) <= to_date(:dataSaldo, 'dd/mm/yyyy') "
            + " and saldo.unidadeOrganizacional = :unidadeOrganizacional "
            + " and saldo.contaReceita = :contaReceita "
            + " and saldo.naturezaDividaAtiva = :natureza "
            + " and saldo.intervalo = :intervalo "
            + " and saldo.contaDeDestinacao = :contaDeDestinacao "
            + " order by saldo.dataSaldo desc";
    }

    private SaldoDividaAtivaContabil criarNovoSaldo(DividaAtivaContabil dividaAtivaContabil) {
        SaldoDividaAtivaContabil saldo = new SaldoDividaAtivaContabil();
        saldo.setDataSaldo(dividaAtivaContabil.getDataDivida());
        saldo.setUnidadeOrganizacional(dividaAtivaContabil.getUnidadeOrganizacional());
        saldo.setContaReceita(dividaAtivaContabil.getContaReceita());
        saldo.setNaturezaDividaAtiva(dividaAtivaContabil.getNaturezaDividaAtiva());
        saldo.setContaDeDestinacao(dividaAtivaContabil.getContaDeDestinacao());
        saldo.setIntervalo(dividaAtivaContabil.getIntervalo());
        saldo.setInscricao(BigDecimal.ZERO);
        saldo.setProvisao(BigDecimal.ZERO);
        saldo.setRecebimento(BigDecimal.ZERO);
        saldo.setReversao(BigDecimal.ZERO);
        saldo.setAtualizacao(BigDecimal.ZERO);
        saldo.setBaixa(BigDecimal.ZERO);
        saldo.setAumentativo(BigDecimal.ZERO);
        saldo.setDiminutivo(BigDecimal.ZERO);
        return saldo;
    }

    private SaldoDividaAtivaContabil criarNovoSaldo(Date data, UnidadeOrganizacional unidadeOrganizacional, Conta contaReceita, NaturezaDividaAtivaCreditoReceber naturezaDividaAtiva, ContaDeDestinacao contaDeDestinacao, Intervalo intervalo) {
        SaldoDividaAtivaContabil saldo = new SaldoDividaAtivaContabil();
        saldo.setDataSaldo(data);
        saldo.setUnidadeOrganizacional(unidadeOrganizacional);
        saldo.setContaReceita(contaReceita);
        saldo.setNaturezaDividaAtiva(naturezaDividaAtiva);
        saldo.setContaDeDestinacao(contaDeDestinacao);
        saldo.setIntervalo(intervalo);
        saldo.setInscricao(BigDecimal.ZERO);
        saldo.setProvisao(BigDecimal.ZERO);
        saldo.setRecebimento(BigDecimal.ZERO);
        saldo.setReversao(BigDecimal.ZERO);
        saldo.setAtualizacao(BigDecimal.ZERO);
        saldo.setBaixa(BigDecimal.ZERO);
        saldo.setAumentativo(BigDecimal.ZERO);
        saldo.setDiminutivo(BigDecimal.ZERO);
        return saldo;
    }

    private SaldoDividaAtivaContabil atribuirValorParaColunaCorrespondente(SaldoDividaAtivaContabil saldo, DividaAtivaContabil dividaAtivaContabil) throws ExcecaoNegocioGenerica {
        switch (dividaAtivaContabil.getOperacaoDividaAtiva()) {
            case ATUALIZACAO:
                saldo = atualizarValorAtualizacao(saldo, dividaAtivaContabil);
                break;
            case INSCRICAO:
                saldo = atualizaValorInscricao(saldo, dividaAtivaContabil);
                break;
            case REVERSAO:
            case REVERSAO_AJUSTE_LONGO_PRAZO:
                saldo = atualizarValorReversao(saldo, dividaAtivaContabil);
                break;
            case PROVISAO:
            case AJUSTE_PERDAS_LONGO_PRAZO:
                saldo = atualizarValorAjustePerdas(saldo, dividaAtivaContabil);
                break;
            case RECEBIMENTO:
                saldo = atualizarValorRecebimento(saldo, dividaAtivaContabil);
                break;
            case BAIXA:
                saldo = atualizarValorBaixa(saldo, dividaAtivaContabil);
                break;
            case AUMENTATIVO:
                saldo = atualizarValorAumentativo(saldo, dividaAtivaContabil);
                break;
            case DIMINUTIVO:
                saldo = atualizarValorDiminutivo(saldo, dividaAtivaContabil);
                break;
            case TRANSFERENCIA_CURTO_PARA_LONGO_PRAZO:
                saldo = atualizarValorBaixa(saldo, dividaAtivaContabil);
                SaldoDividaAtivaContabil saldoLongoPrazo = buscarUltimoSaldoPorDataUnidadeContaNaturezaIntervalo(dividaAtivaContabil.getDataDivida(),
                    saldo.getUnidadeOrganizacional(),
                    saldo.getContaReceita(),
                    NaturezaDividaAtivaCreditoReceber.ORIGINAL,
                    Intervalo.LONGO_PRAZO,
                    saldo.getContaDeDestinacao());
                atualizarSaldoTransferenciaCurtoParaLongo(saldo, saldoLongoPrazo, dividaAtivaContabil);
                break;
            case TRANSFERENCIA_LONGO_PARA_CURTO_PRAZO:
                saldo = atualizarValorBaixa(saldo, dividaAtivaContabil);
                SaldoDividaAtivaContabil saldoCurtoPrazo = buscarUltimoSaldoPorDataUnidadeContaNaturezaIntervalo(dividaAtivaContabil.getDataDivida(),
                    saldo.getUnidadeOrganizacional(),
                    saldo.getContaReceita(),
                    NaturezaDividaAtivaCreditoReceber.ORIGINAL,
                    Intervalo.CURTO_PRAZO,
                    saldo.getContaDeDestinacao());
                atualizarSaldoTransferenciaLongoParaCurto(saldo, saldoCurtoPrazo, dividaAtivaContabil);
                break;
            case TRANSFERENCIA_AJUSTE_PERDAS_CURTO_PARA_LONGO_PRAZO:
                saldo = atualizarValorAtualizacao(saldo, dividaAtivaContabil);
                SaldoDividaAtivaContabil saldoAjustePerdaLongoPrazo = buscarUltimoSaldoPorDataUnidadeContaNaturezaIntervalo(dividaAtivaContabil.getDataDivida(),
                    saldo.getUnidadeOrganizacional(),
                    saldo.getContaReceita(),
                    NaturezaDividaAtivaCreditoReceber.AJUSTE_DE_PERDAS,
                    Intervalo.LONGO_PRAZO,
                    saldo.getContaDeDestinacao());
                atualizarSaldoTransferenciaAjustePerdaCurtoParaLongo(saldo, saldoAjustePerdaLongoPrazo, dividaAtivaContabil);
                break;
            case TRANSFERENCIA_AJUSTE_PERDAS_LONGO_PARA_CURTO_PRAZO:
                saldo = atualizarValorBaixa(saldo, dividaAtivaContabil);
                SaldoDividaAtivaContabil saldoAjustePerdaCurtoPrazo = buscarUltimoSaldoPorDataUnidadeContaNaturezaIntervalo(dividaAtivaContabil.getDataDivida(),
                    saldo.getUnidadeOrganizacional(),
                    saldo.getContaReceita(),
                    NaturezaDividaAtivaCreditoReceber.AJUSTE_DE_PERDAS,
                    Intervalo.CURTO_PRAZO,
                    saldo.getContaDeDestinacao());
                atualizarSaldoTransferenciaAjustePerdaLongoParaCurto(saldo, saldoAjustePerdaCurtoPrazo, dividaAtivaContabil);
                break;
        }
        return saldo;
    }


    private void atualizarSaldoTransferenciaCurtoParaLongo(SaldoDividaAtivaContabil saldo, SaldoDividaAtivaContabil saldoCreditoCurtoOrLongoPrazo, DividaAtivaContabil dividaAtivaContabil) {
        if (saldoCreditoCurtoOrLongoPrazo.getId() != null && DataUtil.dataSemHorario(saldoCreditoCurtoOrLongoPrazo.getDataSaldo()).compareTo(DataUtil.dataSemHorario(saldo.getDataSaldo())) == 0) {
            saldoCreditoCurtoOrLongoPrazo = atualizarValorAtualizacao(saldoCreditoCurtoOrLongoPrazo, dividaAtivaContabil);
            salvar(saldoCreditoCurtoOrLongoPrazo);
        } else {
            SaldoDividaAtivaContabil novoSaldo = criarNovoSaldo(saldo.getDataSaldo(), saldo.getUnidadeOrganizacional(), saldo.getContaReceita(), saldo.getNaturezaDividaAtiva(), saldo.getContaDeDestinacao(), Intervalo.LONGO_PRAZO);
            novoSaldo = definirValoresParaNovo(saldoCreditoCurtoOrLongoPrazo, novoSaldo);
            novoSaldo = atualizarValorAtualizacao(novoSaldo, dividaAtivaContabil);
            salvarNovo(novoSaldo);
        }
    }

    private void atualizarSaldoTransferenciaLongoParaCurto(SaldoDividaAtivaContabil saldo, SaldoDividaAtivaContabil saldoCreditoCurtoOrLongoPrazo, DividaAtivaContabil dividaAtivaContabil) {
        if (saldoCreditoCurtoOrLongoPrazo.getId() != null && DataUtil.dataSemHorario(saldoCreditoCurtoOrLongoPrazo.getDataSaldo()).compareTo(DataUtil.dataSemHorario(saldo.getDataSaldo())) == 0) {
            saldoCreditoCurtoOrLongoPrazo = atualizarValorAumentativo(saldoCreditoCurtoOrLongoPrazo, dividaAtivaContabil);
            salvar(saldoCreditoCurtoOrLongoPrazo);
        } else {
            SaldoDividaAtivaContabil novoSaldo = criarNovoSaldo(saldo.getDataSaldo(), saldo.getUnidadeOrganizacional(), saldo.getContaReceita(), saldo.getNaturezaDividaAtiva(), saldo.getContaDeDestinacao(), Intervalo.LONGO_PRAZO);
            novoSaldo = definirValoresParaNovo(saldoCreditoCurtoOrLongoPrazo, novoSaldo);
            novoSaldo = atualizarValorAumentativo(novoSaldo, dividaAtivaContabil);
            salvarNovo(novoSaldo);
        }
    }

    private void atualizarSaldoTransferenciaAjustePerdaCurtoParaLongo(SaldoDividaAtivaContabil saldo, SaldoDividaAtivaContabil saldoCreditoCurtoOrLongoPrazo, DividaAtivaContabil dividaAtivaContabil) {
        if (saldoCreditoCurtoOrLongoPrazo.getId() != null && DataUtil.dataSemHorario(saldoCreditoCurtoOrLongoPrazo.getDataSaldo()).compareTo(DataUtil.dataSemHorario(saldo.getDataSaldo())) == 0) {
            saldoCreditoCurtoOrLongoPrazo = atualizarValorBaixa(saldoCreditoCurtoOrLongoPrazo, dividaAtivaContabil);
            salvar(saldoCreditoCurtoOrLongoPrazo);
        } else {
            SaldoDividaAtivaContabil novoSaldo = criarNovoSaldo(saldo.getDataSaldo(), saldo.getUnidadeOrganizacional(), saldo.getContaReceita(), saldo.getNaturezaDividaAtiva(), saldo.getContaDeDestinacao(), Intervalo.LONGO_PRAZO);
            novoSaldo = definirValoresParaNovo(saldoCreditoCurtoOrLongoPrazo, novoSaldo);
            novoSaldo = atualizarValorBaixa(novoSaldo, dividaAtivaContabil);
            salvarNovo(novoSaldo);
        }
    }

    private void atualizarSaldoTransferenciaAjustePerdaLongoParaCurto(SaldoDividaAtivaContabil saldo, SaldoDividaAtivaContabil saldoCreditoCurtoOrLongoPrazo, DividaAtivaContabil dividaAtivaContabil) {
        if (saldoCreditoCurtoOrLongoPrazo.getId() != null && DataUtil.dataSemHorario(saldoCreditoCurtoOrLongoPrazo.getDataSaldo()).compareTo(DataUtil.dataSemHorario(saldo.getDataSaldo())) == 0) {
            saldoCreditoCurtoOrLongoPrazo = atualizarValorAtualizacao(saldoCreditoCurtoOrLongoPrazo, dividaAtivaContabil);
            salvar(saldoCreditoCurtoOrLongoPrazo);
        } else {
            SaldoDividaAtivaContabil novoSaldo = criarNovoSaldo(saldo.getDataSaldo(), saldo.getUnidadeOrganizacional(), saldo.getContaReceita(), saldo.getNaturezaDividaAtiva(), saldo.getContaDeDestinacao(), Intervalo.LONGO_PRAZO);
            novoSaldo = definirValoresParaNovo(saldoCreditoCurtoOrLongoPrazo, novoSaldo);
            novoSaldo = atualizarValorAtualizacao(novoSaldo, dividaAtivaContabil);
            salvarNovo(novoSaldo);
        }
    }

    private SaldoDividaAtivaContabil atualizarValorAtualizacao(SaldoDividaAtivaContabil saldo, DividaAtivaContabil dividaAtivaContabil) {
        if (dividaAtivaContabil.isLancamentoNormal()) {
            saldo.setAtualizacao(saldo.getAtualizacao().add(dividaAtivaContabil.getValor()));
        } else {
            saldo.setAtualizacao(saldo.getAtualizacao().subtract(dividaAtivaContabil.getValor()));
        }
        return saldo;
    }

    private SaldoDividaAtivaContabil atualizarValorBaixa(SaldoDividaAtivaContabil saldo, DividaAtivaContabil dividaAtivaContabil) {
        if (dividaAtivaContabil.isLancamentoNormal()) {
            saldo.setBaixa(saldo.getBaixa().add(dividaAtivaContabil.getValor()));
        } else {
            saldo.setBaixa(saldo.getBaixa().subtract(dividaAtivaContabil.getValor()));
        }
        return saldo;
    }

    private SaldoDividaAtivaContabil atualizarValorAumentativo(SaldoDividaAtivaContabil saldo, DividaAtivaContabil dividaAtivaContabil) {
        if (dividaAtivaContabil.isLancamentoNormal()) {
            saldo.setAumentativo(saldo.getAumentativo().add(dividaAtivaContabil.getValor()));
        } else {
            saldo.setAumentativo(saldo.getAumentativo().subtract(dividaAtivaContabil.getValor()));
        }
        return saldo;
    }

    private SaldoDividaAtivaContabil atualizarValorDiminutivo(SaldoDividaAtivaContabil saldo, DividaAtivaContabil dividaAtivaContabil) {
        if (dividaAtivaContabil.isLancamentoNormal()) {
            saldo.setDiminutivo(saldo.getDiminutivo().add(dividaAtivaContabil.getValor()));
        } else {
            saldo.setDiminutivo(saldo.getDiminutivo().subtract(dividaAtivaContabil.getValor()));
        }
        return saldo;
    }

    private SaldoDividaAtivaContabil atualizarValorReversao(SaldoDividaAtivaContabil saldo, DividaAtivaContabil dividaAtivaContabil) {
        if (dividaAtivaContabil.isLancamentoNormal()) {
            saldo.setReversao(saldo.getReversao().add(dividaAtivaContabil.getValor()));
        } else {
            saldo.setReversao(saldo.getReversao().subtract(dividaAtivaContabil.getValor()));
        }
        return saldo;
    }

    private SaldoDividaAtivaContabil atualizarValorAjustePerdas(SaldoDividaAtivaContabil saldo, DividaAtivaContabil dividaAtivaContabil) {
        if (dividaAtivaContabil.isLancamentoNormal()) {
            saldo.setProvisao(saldo.getProvisao().add(dividaAtivaContabil.getValor()));
        } else {
            saldo.setProvisao(saldo.getProvisao().subtract(dividaAtivaContabil.getValor()));
        }
        return saldo;
    }

    private SaldoDividaAtivaContabil atualizarValorRecebimento(SaldoDividaAtivaContabil saldo, DividaAtivaContabil dividaAtivaContabil) {
        if (dividaAtivaContabil.isLancamentoNormal()) {
            saldo.setRecebimento(saldo.getRecebimento().add(dividaAtivaContabil.getValor()));
        } else {
            saldo.setRecebimento(saldo.getRecebimento().subtract(dividaAtivaContabil.getValor()));
        }
        return saldo;
    }

    private SaldoDividaAtivaContabil atualizaValorInscricao(SaldoDividaAtivaContabil saldo, DividaAtivaContabil dividaAtivaContabil) throws ExcecaoNegocioGenerica {
        if (dividaAtivaContabil.isLancamentoNormal()) {
            saldo.setInscricao(saldo.getInscricao().add(dividaAtivaContabil.getValor()));
            gerarSaldoCreditoReceber(dividaAtivaContabil);
        } else {
            saldo.setInscricao(saldo.getInscricao().subtract(dividaAtivaContabil.getValor()));
            gerarSaldoCreditoReceber(dividaAtivaContabil);
        }
        return saldo;
    }

    private void gerarSaldoCreditoReceber(DividaAtivaContabil dividaAtivaContabil) {
        CreditoReceber cr = new CreditoReceber();
        cr.setDataCredito(dividaAtivaContabil.getDataDivida());
        cr.setUnidadeOrganizacional(dividaAtivaContabil.getUnidadeOrganizacional());
        cr.setValor(dividaAtivaContabil.getValor());
        cr.setReceitaLOA(dividaAtivaContabil.getReceitaLOA());
        cr.setOperacaoCreditoReceber(OperacaoCreditoReceber.BAIXA_RECONHECIMENTO_CREDITO_A_RECEBER);
        cr.setTipoLancamento(dividaAtivaContabil.getTipoLancamento());
        cr.setNaturezaCreditoReceber(dividaAtivaContabil.getNaturezaDividaAtiva());
        cr.setContaDeDestinacao(dividaAtivaContabil.getContaDeDestinacao());
        cr.setIntervalo(dividaAtivaContabil.getIntervalo());
        saldoCreditoReceberFacade.gerarSaldoCreditoReceber(cr, false);
    }

    //Método criado apenas para utilizar no Reprocessamento de Saldo de Dívida Ativa
    public void excluirSaldosPeriodo(Date dataInicial, Date dataFinal) {
        String sql = " delete FROM saldodividaativacontabil saldo "
            + " WHERE trunc(saldo.datasaldo) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        q.executeUpdate();
    }
}
