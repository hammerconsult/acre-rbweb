/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Intervalo;
import br.com.webpublico.enums.OperacaoDiarioDividaPublica;
import br.com.webpublico.enums.OperacaoMovimentoDividaPublica;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Renato
 */
@Stateless
public class SaldoDividaPublicaFacade extends AbstractFacade<SaldoDividaPublica> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private final String MENSAGEM_CONCORRENCIA = "Erro de concorrência com movimentações do mesmo registro com usuários diferentes. Efetue a mesma operação e caso o problema persistir, entre em contato com o suporte.";

    public SaldoDividaPublicaFacade() {
        super(SaldoDividaPublica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void gerarMovimento(Date data, BigDecimal valor, UnidadeOrganizacional unidadeOrganizacional,
                               DividaPublica dividaPublica, OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica,
                               boolean isEstorno, OperacaoDiarioDividaPublica operacaoDiarioDividaPublica,
                               Intervalo intervalo, ContaDeDestinacao contaDeDestinacao, boolean validarValorNegativo) {
        try {
            SaldoDividaPublica ultimoSaldo = recuperarSaldoPorDividaPublicaAndDataAndUnidadeOrganizacionalAndIntervaloAndContaDeDestinacao(data, unidadeOrganizacional, dividaPublica, intervalo, contaDeDestinacao);
            Intervalo intervaloContrario = intervalo.isCurtoPrazo() ? Intervalo.LONGO_PRAZO : Intervalo.CURTO_PRAZO;
            SaldoDividaPublica saldoIntervaloContrario = recuperarSaldoPorDividaPublicaAndDataAndUnidadeOrganizacionalAndIntervaloAndContaDeDestinacao(data, unidadeOrganizacional, dividaPublica, intervaloContrario, contaDeDestinacao);
            if (ultimoSaldo == null || ultimoSaldo.getId() == null) {
                SaldoDividaPublica novoSaldo = new SaldoDividaPublica(data, unidadeOrganizacional, dividaPublica, intervalo, contaDeDestinacao);
                novoSaldo = isEstorno ? alterarValorEstorno(novoSaldo, operacaoMovimentoDividaPublica, valor, validarValorNegativo, dividaPublica, saldoIntervaloContrario) : alterarValorNormal(novoSaldo, operacaoMovimentoDividaPublica, valor, validarValorNegativo, dividaPublica, saldoIntervaloContrario);
                if (validarValorIndividual(valor, novoSaldo, operacaoMovimentoDividaPublica, validarValorNegativo)) {
                    salvarSaldo(novoSaldo, dividaPublica, saldoIntervaloContrario);
                }
            } else {
                if (DataUtil.dataSemHorario(ultimoSaldo.getData()).compareTo(DataUtil.dataSemHorario(data)) == 0) {
                    ultimoSaldo = isEstorno ? alterarValorEstorno(ultimoSaldo, operacaoMovimentoDividaPublica, valor, validarValorNegativo, dividaPublica, saldoIntervaloContrario) : alterarValorNormal(ultimoSaldo, operacaoMovimentoDividaPublica, valor, validarValorNegativo, dividaPublica, saldoIntervaloContrario);
                    if (validarValorIndividual(valor, ultimoSaldo, operacaoMovimentoDividaPublica, validarValorNegativo)) {
                        salvarSaldo(ultimoSaldo, dividaPublica, saldoIntervaloContrario);
                    }
                } else {
                    SaldoDividaPublica novoSaldo = new SaldoDividaPublica(data, unidadeOrganizacional, dividaPublica, intervalo, contaDeDestinacao);
                    atribuirValorApartirUltimoSaldo(ultimoSaldo, novoSaldo);
                    novoSaldo = isEstorno ? alterarValorEstorno(novoSaldo, operacaoMovimentoDividaPublica, valor, validarValorNegativo, dividaPublica, saldoIntervaloContrario) : alterarValorNormal(novoSaldo, operacaoMovimentoDividaPublica, valor, validarValorNegativo, dividaPublica, saldoIntervaloContrario);
                    if (validarValorIndividual(valor, novoSaldo, operacaoMovimentoDividaPublica, validarValorNegativo)) {
                        salvarSaldo(novoSaldo, dividaPublica, saldoIntervaloContrario);
                    }
                }
            }
            atualizarSaldosFuturos(data, unidadeOrganizacional, dividaPublica, valor, operacaoMovimentoDividaPublica, isEstorno, intervalo, contaDeDestinacao, validarValorNegativo);
            gerarMovimentacaoDiario(data, unidadeOrganizacional, dividaPublica, isEstorno, operacaoDiarioDividaPublica, valor, intervalo, contaDeDestinacao);
        } catch (OptimisticLockException optex) {
            throw new ExcecaoNegocioGenerica("O Saldo da dívida pública está sendo movimentado por outro usuário. Por favor, tente salvar o movimento em alguns instantes.");
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void atualizarSaldoDividaPublica(DividaPublica dividaPublica, SaldoDividaPublica novoSaldo, SaldoDividaPublica saldoIntervaloContrario) {
        BigDecimal valorIntervaloContrario = BigDecimal.ZERO;
        if (saldoIntervaloContrario != null) {
            valorIntervaloContrario = saldoIntervaloContrario.getSaldoReal();
        }
        dividaPublica.setSaldo(novoSaldo.getSaldoReal().add(valorIntervaloContrario));
        em.merge(dividaPublica);
    }

    private void atribuirValorApartirUltimoSaldo(SaldoDividaPublica ultimoSaldo, SaldoDividaPublica novoSaldo) {
        novoSaldo.setTransferenciaDebito(ultimoSaldo.getTransferenciaDebito());
        novoSaldo.setTransferenciaCredito(ultimoSaldo.getTransferenciaCredito());
        novoSaldo.setEmpenhado(ultimoSaldo.getEmpenhado());
        novoSaldo.setApropriacao(ultimoSaldo.getApropriacao());
        novoSaldo.setAtualizacao(ultimoSaldo.getAtualizacao());
        novoSaldo.setCancelamento(ultimoSaldo.getCancelamento());
        novoSaldo.setPagamento(ultimoSaldo.getPagamento());
        novoSaldo.setReceita(ultimoSaldo.getReceita());
        novoSaldo.setInscricao(ultimoSaldo.getInscricao());
    }

    private boolean validarValorIndividual(BigDecimal valorMovimento, SaldoDividaPublica saldo, OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica, Boolean validarValorNegativo) {
        if (validarValorNegativo) {
            if (saldo.getSaldoRealComPagamentoEEmpenhado().compareTo(BigDecimal.ZERO) < 0) {
                return getMensagemValidacao(saldo, operacaoMovimentoDividaPublica, valorMovimento, saldo.getSaldoRealComPagamentoEEmpenhado().add(valorMovimento));
            }
            BigDecimal valorIndividual = recuperarValor(saldo, operacaoMovimentoDividaPublica);
            if (valorIndividual.compareTo(BigDecimal.ZERO) < 0) {
                return getMensagemValidacao(saldo, operacaoMovimentoDividaPublica, valorMovimento, valorIndividual.add(valorMovimento));
            }
        }
        return true;
    }

    private boolean getMensagemValidacao(SaldoDividaPublica saldo, OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica, BigDecimal valorMovimento, BigDecimal saldoReal) {
        throw new ExcecaoNegocioGenerica("O lançamento com valor de " + Util.formataValor(valorMovimento)
            + " para o saldo da dívida pública nº " + saldo.getDividapublica().getNumero() + ", operação: \'" + operacaoMovimentoDividaPublica.getDescricao()
            + "\', intervalo: \'" + saldo.getIntervalo().getDescricao()
            + "\' ficou negativo na data: " + DataUtil.getDataFormatada(saldo.getData())
            + ". Saldo atual: " + Util.formataValor(saldoReal));
    }

    @Deprecated
    public void gerarMovimento(Date data, BigDecimal valor, UnidadeOrganizacional unidadeOrganizacional, DividaPublica dividaPublica, OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica, boolean isEstorno, OperacaoDiarioDividaPublica operacaoDiarioDividaPublica) throws ExcecaoNegocioGenerica, Exception {
        SaldoDividaPublica saldoDividaPublica = recupeSaldoPorDividaPublicaDataUnidadeOrganizacional(data, unidadeOrganizacional, dividaPublica, operacaoMovimentoDividaPublica);
        saldoDividaPublica = isEstorno ? alterarValorEstorno(saldoDividaPublica, operacaoMovimentoDividaPublica, valor, true, dividaPublica, null) : alterarValorNormal(saldoDividaPublica, operacaoMovimentoDividaPublica, valor, true, dividaPublica, null);
        salvarSaldo(saldoDividaPublica, dividaPublica, null);
        atualizarSaldosFuturos(data, unidadeOrganizacional, dividaPublica, valor, operacaoMovimentoDividaPublica, isEstorno, null, null, true);
        gerarMovimentacaoDiario(data, unidadeOrganizacional, dividaPublica, isEstorno, operacaoDiarioDividaPublica, valor, null, null);
    }

    private void gerarMovimentacaoDiario(Date data, UnidadeOrganizacional unidadeOrganizacional, DividaPublica dividaPublica,
                                         boolean isEstorno, OperacaoDiarioDividaPublica operacaoDiarioDividaPublica,
                                         BigDecimal valor, Intervalo intervalo, ContaDeDestinacao contaDeDestinacao) {
        MovimentoDiarioDividaPublica movimento = new MovimentoDiarioDividaPublica();
        movimento.setData(data);
        movimento.setUnidadeOrganizacional(unidadeOrganizacional);
        movimento.setDividaPublica(dividaPublica);
        movimento.setTipoLancamento(isEstorno ? TipoLancamento.ESTORNO : TipoLancamento.NORMAL);
        movimento.setOperacaoDiarioDividaPublica(operacaoDiarioDividaPublica);
        movimento.setValor(valor);
        movimento.setIntervalo(intervalo);
        movimento.setContaDeDestinacao(contaDeDestinacao);
        if (contaDeDestinacao != null) {
            movimento.setFonteDeRecursos(contaDeDestinacao.getFonteDeRecursos());
        }
        em.persist(movimento);
    }

    private void atualizarSaldosFuturos(Date data, UnidadeOrganizacional unidadeOrganizacional, DividaPublica dividaPublica,
                                        BigDecimal valor, OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica,
                                        boolean isEstorno,
                                        Intervalo intervalo,
                                        ContaDeDestinacao contaDeDestinacao,
                                        boolean validarValorNegativo) throws Exception {
        if (isRetroativo(data)) {
            List<SaldoDividaPublica> saldosFuturosAData = buscarSaldosFuturos(data, unidadeOrganizacional, dividaPublica, intervalo, contaDeDestinacao);
            for (SaldoDividaPublica saldo : saldosFuturosAData) {
                saldo = isEstorno ? alterarValorEstorno(saldo, operacaoMovimentoDividaPublica, valor, validarValorNegativo, dividaPublica, null) : alterarValorNormal(saldo, operacaoMovimentoDividaPublica, valor, validarValorNegativo, dividaPublica, null);
                salvarSaldo(saldo, dividaPublica, null);
            }
        }
    }

    private void salvarSaldo(SaldoDividaPublica saldo, DividaPublica dividaPublica, SaldoDividaPublica saldoIntervaloContrario) {
        if (saldo.getId() == null) {
            em.persist(saldo);
        } else {
            em.merge(saldo);
        }
        atualizarSaldoDividaPublica(dividaPublica, saldo, saldoIntervaloContrario);
    }

    private BigDecimal recuperarValor(SaldoDividaPublica saldo, OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica) {
        switch (operacaoMovimentoDividaPublica) {
            case INSCRICAO_PRINCIPAL:
                return saldo.getInscricao();
            case ATUALIZACAO_JUROS:
            case ATUALIZACAO_ENCARGOS:
                return saldo.getAtualizacao();
            case APROPRIACAO_JUROS:
            case APROPRIACAO_ENCARGOS:
                return saldo.getApropriacao();
            case CANCELAMENTO_APROPRIACAO_ENCARGOS:
            case CANCELAMENTO_APROPRIACAO_JUROS:
            case CANCELAMENTO_ENCARGOS:
            case CANCELAMENTO_JUROS:
            case CANCELAMENTO_PRINCIPAL:
                return saldo.getCancelamento();
            case PAGAMENTO_AMORTIZACAO:
                return saldo.getPagamento();
            case RECEITA_OPERACAO_CREDITO:
                return saldo.getReceita();
            case TRANSFERENCIA_CURTO_PARA_LONGO_PRAZO:
                return saldo.getTransferenciaDebito();
            case TRANSFERENCIA_LONGO_PARA_CURTO_PRAZO:
                return saldo.getTransferenciaDebito();
            case EMPENHO:
                return saldo.getEmpenhado();
        }
        return BigDecimal.ZERO;
    }

    private SaldoDividaPublica alterarValorNormal(SaldoDividaPublica saldo, OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica, BigDecimal valor, boolean validarValorNegativo, DividaPublica dividaPublica, SaldoDividaPublica saldoIntervaloContrario) throws Exception {
        switch (operacaoMovimentoDividaPublica) {
            case INSCRICAO_PRINCIPAL:
                saldo.setInscricao(saldo.getInscricao().add(valor));
                break;
            case ATUALIZACAO_JUROS:
            case ATUALIZACAO_ENCARGOS:
                saldo.setAtualizacao(saldo.getAtualizacao().add(valor));
                break;
            case APROPRIACAO_JUROS:
            case APROPRIACAO_ENCARGOS:
                saldo.setApropriacao(saldo.getApropriacao().add(valor));
                break;
            case CANCELAMENTO_APROPRIACAO_ENCARGOS:
            case CANCELAMENTO_APROPRIACAO_JUROS:
            case CANCELAMENTO_ENCARGOS:
            case CANCELAMENTO_JUROS:
            case CANCELAMENTO_PRINCIPAL:
                saldo.setCancelamento(saldo.getCancelamento().add(valor));
                break;
            case PAGAMENTO_AMORTIZACAO:
                saldo.setPagamento(saldo.getPagamento().add(valor));
                break;
            case RECEITA_OPERACAO_CREDITO:
                saldo.setReceita(saldo.getReceita().add(valor));
                break;
            case TRANSFERENCIA_CURTO_PARA_LONGO_PRAZO:
                saldo.setTransferenciaDebito(saldo.getTransferenciaDebito().add(valor));
                SaldoDividaPublica saldoCreditoLongoPrazo = recuperarSaldoPorDividaPublicaAndDataAndUnidadeOrganizacionalAndIntervaloAndContaDeDestinacao(saldo.getData(),
                    saldo.getUnidadeOrganizacional(),
                    saldo.getDividapublica(),
                    Intervalo.LONGO_PRAZO,
                    saldo.getContaDeDestinacao());
                atualizarSaldoTransferenciaNormal(saldo, operacaoMovimentoDividaPublica, valor, validarValorNegativo, saldoCreditoLongoPrazo, dividaPublica, saldoIntervaloContrario);
                break;
            case TRANSFERENCIA_LONGO_PARA_CURTO_PRAZO:
                saldo.setTransferenciaDebito(saldo.getTransferenciaDebito().add(valor));
                SaldoDividaPublica saldoCreditoCurtoPrazo = recuperarSaldoPorDividaPublicaAndDataAndUnidadeOrganizacionalAndIntervaloAndContaDeDestinacao(saldo.getData(),
                    saldo.getUnidadeOrganizacional(),
                    saldo.getDividapublica(),
                    Intervalo.CURTO_PRAZO,
                    saldo.getContaDeDestinacao());
                atualizarSaldoTransferenciaNormal(saldo, operacaoMovimentoDividaPublica, valor, validarValorNegativo, saldoCreditoCurtoPrazo, dividaPublica, saldoIntervaloContrario);
                break;
            case EMPENHO:
                saldo.setEmpenhado(saldo.getEmpenhado().add(valor));
                break;
        }
        return saldo;
    }

    private void atualizarSaldoTransferenciaNormal(SaldoDividaPublica saldo, OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica, BigDecimal valor, boolean validarValorNegativo, SaldoDividaPublica saldoCreditoCurtoOrLongoPrazo, DividaPublica dividaPublica, SaldoDividaPublica saldoIntervaloContrario) {
        if (DataUtil.dataSemHorario(saldoCreditoCurtoOrLongoPrazo.getData()).compareTo(DataUtil.dataSemHorario(saldo.getData())) == 0) {
            saldoCreditoCurtoOrLongoPrazo.setTransferenciaCredito(saldoCreditoCurtoOrLongoPrazo.getTransferenciaCredito().add(valor));
            if (validarValorIndividual(valor, saldoCreditoCurtoOrLongoPrazo, operacaoMovimentoDividaPublica, validarValorNegativo)) {
                salvarSaldo(saldoCreditoCurtoOrLongoPrazo, dividaPublica, saldoIntervaloContrario);
            }
        } else {
            SaldoDividaPublica novoSaldo = new SaldoDividaPublica(saldo.getData(), saldoCreditoCurtoOrLongoPrazo.getUnidadeOrganizacional(), saldoCreditoCurtoOrLongoPrazo.getDividapublica(), saldoCreditoCurtoOrLongoPrazo.getIntervalo(), saldoCreditoCurtoOrLongoPrazo.getContaDeDestinacao());
            atribuirValorApartirUltimoSaldo(saldoCreditoCurtoOrLongoPrazo, novoSaldo);
            novoSaldo.setTransferenciaCredito(saldoCreditoCurtoOrLongoPrazo.getTransferenciaCredito().add(valor));
            if (validarValorIndividual(valor, novoSaldo, operacaoMovimentoDividaPublica, validarValorNegativo)) {
                salvarSaldo(novoSaldo, dividaPublica, saldoIntervaloContrario);
            }
        }
    }

    private SaldoDividaPublica alterarValorEstorno(SaldoDividaPublica saldo, OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica, BigDecimal valor, boolean validarValorNegativo, DividaPublica dividaPublica, SaldoDividaPublica saldoIntervaloContrario) throws Exception {
        switch (operacaoMovimentoDividaPublica) {
            case INSCRICAO_PRINCIPAL:
                saldo.setInscricao(saldo.getInscricao().subtract(valor));
                break;
            case ATUALIZACAO_JUROS:
            case ATUALIZACAO_ENCARGOS:
                saldo.setAtualizacao(saldo.getAtualizacao().subtract(valor));
                break;
            case APROPRIACAO_JUROS:
            case APROPRIACAO_ENCARGOS:
                saldo.setApropriacao(saldo.getApropriacao().subtract(valor));
                break;
            case CANCELAMENTO_APROPRIACAO_ENCARGOS:
            case CANCELAMENTO_APROPRIACAO_JUROS:
            case CANCELAMENTO_ENCARGOS:
            case CANCELAMENTO_JUROS:
            case CANCELAMENTO_PRINCIPAL:
                saldo.setCancelamento(saldo.getCancelamento().subtract(valor));
                break;
            case PAGAMENTO_AMORTIZACAO:
                saldo.setPagamento(saldo.getPagamento().subtract(valor));
                break;
            case RECEITA_OPERACAO_CREDITO:
                saldo.setReceita(saldo.getReceita().subtract(valor));
                break;
            case TRANSFERENCIA_CURTO_PARA_LONGO_PRAZO:
                saldo.setTransferenciaDebito(saldo.getTransferenciaDebito().subtract(valor));
                SaldoDividaPublica saldoCreditoLongoPrazo = recuperarSaldoPorDividaPublicaAndDataAndUnidadeOrganizacionalAndIntervaloAndContaDeDestinacao(saldo.getData(),
                    saldo.getUnidadeOrganizacional(),
                    saldo.getDividapublica(),
                    Intervalo.LONGO_PRAZO,
                    saldo.getContaDeDestinacao());
                atualizarSaldoTransferenciaEstorno(saldo, operacaoMovimentoDividaPublica, valor, validarValorNegativo, saldoCreditoLongoPrazo, dividaPublica, saldoIntervaloContrario);
                break;
            case TRANSFERENCIA_LONGO_PARA_CURTO_PRAZO:
                saldo.setTransferenciaDebito(saldo.getTransferenciaDebito().subtract(valor));
                SaldoDividaPublica saldoCreditoCurtoPrazo = recuperarSaldoPorDividaPublicaAndDataAndUnidadeOrganizacionalAndIntervaloAndContaDeDestinacao(saldo.getData(),
                    saldo.getUnidadeOrganizacional(),
                    saldo.getDividapublica(),
                    Intervalo.CURTO_PRAZO,
                    saldo.getContaDeDestinacao());
                atualizarSaldoTransferenciaEstorno(saldo, operacaoMovimentoDividaPublica, valor, validarValorNegativo, saldoCreditoCurtoPrazo, dividaPublica, saldoIntervaloContrario);
                break;
            case EMPENHO:
                saldo.setEmpenhado(saldo.getEmpenhado().subtract(valor));
                break;
        }
        return saldo;
    }

    private void atualizarSaldoTransferenciaEstorno(SaldoDividaPublica saldo, OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica, BigDecimal valor, boolean validarValorNegativo, SaldoDividaPublica saldoCreditoCurtoOrLongoPrazo, DividaPublica dividaPublica, SaldoDividaPublica saldoIntervaloContrario) {
        if (DataUtil.dataSemHorario(saldoCreditoCurtoOrLongoPrazo.getData()).compareTo(DataUtil.dataSemHorario(saldo.getData())) == 0) {
            saldoCreditoCurtoOrLongoPrazo.setTransferenciaCredito(saldoCreditoCurtoOrLongoPrazo.getTransferenciaCredito().subtract(valor));
            if (validarValorIndividual(valor, saldoCreditoCurtoOrLongoPrazo, operacaoMovimentoDividaPublica, validarValorNegativo)) {
                salvarSaldo(saldoCreditoCurtoOrLongoPrazo, dividaPublica, saldoIntervaloContrario);
            }
        } else {
            SaldoDividaPublica novoSaldo = new SaldoDividaPublica(saldo.getData(), saldoCreditoCurtoOrLongoPrazo.getUnidadeOrganizacional(), saldoCreditoCurtoOrLongoPrazo.getDividapublica(), saldoCreditoCurtoOrLongoPrazo.getIntervalo(), saldoCreditoCurtoOrLongoPrazo.getContaDeDestinacao());
            atribuirValorApartirUltimoSaldo(saldoCreditoCurtoOrLongoPrazo, novoSaldo);
            novoSaldo.setTransferenciaCredito(novoSaldo.getTransferenciaCredito().subtract(valor));
            if (validarValorIndividual(valor, novoSaldo, operacaoMovimentoDividaPublica, validarValorNegativo)) {
                salvarSaldo(novoSaldo, dividaPublica, saldoIntervaloContrario);
            }
        }
    }

    public SaldoDividaPublica recupeSaldoPorDividaPublicaDataUnidadeOrganizacional(Date data, UnidadeOrganizacional unidadeOrganizacional, DividaPublica dividaPublica, OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica) throws Exception {
        String sql = "SELECT * FROM saldodividapublica"
            + " WHERE dividapublica_id = :dividapublica"
            + " AND unidadeorganizacional_id = :unidade "
            + " AND trunc(data) = to_date(:data,'dd/MM/yyyy')"
            + " ORDER BY id DESC";
        Query consulta = em.createNativeQuery(sql, SaldoDividaPublica.class);
        consulta.setParameter("dividapublica", dividaPublica.getId());
        consulta.setParameter("unidade", unidadeOrganizacional.getId());
        consulta.setParameter("data", DataUtil.getDataFormatada(data));
        consulta.setMaxResults(1);
        try {
            return (SaldoDividaPublica) consulta.getSingleResult();
        } catch (Exception e) {
            return recuperaUltimoSaldoAnteriorAData(data, unidadeOrganizacional, dividaPublica, operacaoMovimentoDividaPublica);
        }
    }

    public SaldoDividaPublica recuperarSaldoPorDividaPublicaAndDataAndUnidadeOrganizacionalAndIntervaloAndContaDeDestinacao(Date data,
                                                                                                                            UnidadeOrganizacional unidadeOrganizacional,
                                                                                                                            DividaPublica dividaPublica,
                                                                                                                            Intervalo intervalo,
                                                                                                                            ContaDeDestinacao contaDeDestinacao) {
        String sql = " SELECT * FROM saldodividapublica"
            + " WHERE dividapublica_id = :dividapublica"
            + "   AND unidadeorganizacional_id = :unidade "
            + "   AND contadedestinacao_id = :conta "
            + "   AND intervalo = :intervalo "
            + "   AND trunc(data) <= to_date(:data,'dd/MM/yyyy')"
            + " ORDER BY data DESC";
        Query consulta = em.createNativeQuery(sql, SaldoDividaPublica.class);
        consulta.setParameter("dividapublica", dividaPublica.getId());
        consulta.setParameter("unidade", unidadeOrganizacional.getId());
        consulta.setParameter("data", DataUtil.getDataFormatada(data));
        consulta.setParameter("intervalo", intervalo.name());
        consulta.setParameter("conta", contaDeDestinacao.getId());
        consulta.setMaxResults(1);
        try {
            return (SaldoDividaPublica) consulta.getSingleResult();
        } catch (Exception e) {
            return new SaldoDividaPublica(data, unidadeOrganizacional, dividaPublica, intervalo, contaDeDestinacao);
        }
    }

    private boolean isRetroativo(Date date) {
        Query consulta = em.createNativeQuery("SELECT * FROM saldodividapublica WHERE trunc(data) > :data");
        consulta.setParameter("data", DataUtil.getDataFormatada(date));
        return !consulta.getResultList().isEmpty();
    }

    private List<SaldoDividaPublica> buscarSaldosFuturos(Date data, UnidadeOrganizacional unidadeOrganizacional,
                                                         DividaPublica dividaPublica,
                                                         Intervalo intervalo,
                                                         ContaDeDestinacao contaDeDestinacao) throws Exception {
        String sql = " SELECT * FROM saldodividapublica "
            + " WHERE dividapublica_id = :dividapublica "
            + "   AND unidadeorganizacional_id = :unidade "
            + "   AND contadedestinacao_id = :conta "
            + "   AND intervalo = :intervalo "
            + "   AND trunc(data) > to_date(:data,'dd/MM/yyyy') "
            + " ORDER BY id DESC";
        List<SaldoDividaPublica> saldos;
        try {
            Query consulta = em.createNativeQuery(sql, SaldoDividaPublica.class);
            consulta.setParameter("dividapublica", dividaPublica.getId());
            consulta.setParameter("unidade", unidadeOrganizacional.getId());
            consulta.setParameter("data", DataUtil.getDataFormatada(data));
            consulta.setParameter("intervalo", intervalo.name());
            consulta.setParameter("conta", contaDeDestinacao.getId());
            saldos = consulta.getResultList();
            for (SaldoDividaPublica saldo : saldos) {
                em.lock(saldo, LockModeType.PESSIMISTIC_READ);
            }
        } catch (OptimisticLockException e) {
            throw new Exception(MENSAGEM_CONCORRENCIA);
        } catch (Exception e) {
            saldos = Lists.newArrayList();
        }
        return saldos;
    }

    public SaldoDividaPublica recuperaUltimoSaldoAnteriorAData(Date data, UnidadeOrganizacional unidadeOrganizacional, DividaPublica dividaPublica, OperacaoMovimentoDividaPublica operacao) throws Exception {
        String sql = "SELECT * FROM saldodividapublica saldo"
            + " WHERE saldo.dividapublica_id = :dividapublica"
            + " AND saldo.unidadeorganizacional_id = :unidade "
            + " AND trunc(saldo.data) < to_date(:data,'dd/mm/yyyy')"
            + " ORDER BY saldo.data desc";
        Query consulta = em.createNativeQuery(sql, SaldoDividaPublica.class);
        consulta.setParameter("dividapublica", dividaPublica.getId());
        consulta.setParameter("unidade", unidadeOrganizacional.getId());
        consulta.setParameter("data", DataUtil.getDataFormatada(data));
        consulta.setMaxResults(1);
        try {
            SaldoDividaPublica saldo = (SaldoDividaPublica) consulta.getSingleResult();

            if (saldo.getData().compareTo(data) != 0) {
                SaldoDividaPublica novoSaldo = (SaldoDividaPublica) Util.clonarObjeto(saldo);
                novoSaldo.setId(null);
                novoSaldo.setData(data);
                return novoSaldo;
            }
            em.lock(saldo, LockModeType.PESSIMISTIC_READ);
            return saldo;
        } catch (OptimisticLockException e) {
            throw new Exception(MENSAGEM_CONCORRENCIA);
        } catch (Exception e) {
            return new SaldoDividaPublica(data, unidadeOrganizacional, dividaPublica, null, null);
        }
    }

    public void excluirSaldosPeriodo(Date dataInicial, Date dataFinal) {
        String sql = " delete FROM saldodividapublica ssc "
            + "WHERE trunc(ssc.data) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        q.executeUpdate();
    }
}
