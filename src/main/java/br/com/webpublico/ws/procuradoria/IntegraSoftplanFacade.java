package br.com.webpublico.ws.procuradoria;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ComposicaoCDA;
import br.com.webpublico.entidadesauxiliares.VOItemCertidaoDividaAtiva;
import br.com.webpublico.entidadesauxiliares.ValoresAtualizadosCDA;
import br.com.webpublico.enums.SituacaoCertidaoDA;
import br.com.webpublico.enums.SituacaoJudicial;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoAlteracaoCertidaoDA;
import br.com.webpublico.enums.tributario.procuradoria.TipoAcaoProcessoJudicial;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author Arthur
 */
@Stateless
public class IntegraSoftplanFacade {


    private final Logger logger = LoggerFactory.getLogger(IntegraSoftplanFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private ProcessoJudicialFacade processoJudicialFacade;
    @EJB
    private CancelamentoParcelamentoFacade cancelamentoParcelamentoFacade;
    @EJB
    private BloqueioJudicialFacade bloqueioJudicialFacade;

    private static final Integer EFETUAR_AJUIZAMENTO = 1;
    private static final Integer CANCELAR_AJUIZAMENTO = 2;

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public List<RespostaIntegraSoftplan> tratarDecisaoJudicial(Long numeroCDA, SituacaoCertidaoDA situacaoCDA, Date dataDecisao, String observacao) throws ExcecaoNegocioGenerica {
        List<RespostaIntegraSoftplan> respostas = new ArrayList<>();
        try {
            CDAAnoNumero cdaAnoNumero = extrairAnoNumeroCDA(numeroCDA);
            CertidaoDividaAtiva cda = findCDAByAnoNumero(cdaAnoNumero.ano, cdaAnoNumero.numero);
            if (cda == null) {
                throw new ExcecaoNegocioGenerica("CDA [" + cdaAnoNumero.numero + "] do ano [" + cdaAnoNumero.ano + "] não encontrada.");
            }
            cda.setSituacaoCertidaoDA(situacaoCDA);
            TipoAlteracaoCertidaoDA tipoAlteracaoCDA = recuperarTipoAlteracaoConformeSituacaoCDA(situacaoCDA);
            AlteracaoSituacaoCDA alteracaoCDA =
                new AlteracaoSituacaoCDA(tipoAlteracaoCDA, dataDecisao,
                    sistemaFacade.getUsuarioCorrente(), null, null, null, observacao, cda);
            em.persist(alteracaoCDA);
            em.persist(cda);
        } catch (Exception e) {
            respostas.add(new RespostaIntegraSoftplan(-1L, e.getMessage()));
        }
        return respostas;
    }

    private TipoAlteracaoCertidaoDA recuperarTipoAlteracaoConformeSituacaoCDA(SituacaoCertidaoDA situacaoCDA) {
        if (situacaoCDA == SituacaoCertidaoDA.CANCELADA) {
            return TipoAlteracaoCertidaoDA.CANCELAR;
        }
        if (situacaoCDA == SituacaoCertidaoDA.SUSPENSA) {
            return TipoAlteracaoCertidaoDA.SUSPENDER;
        }
        throw new ExcecaoNegocioGenerica("Somente as situações SUSPENSA ou CANCELADA são permitidas!");
    }

    public List<RespostaIntegraSoftplan> tratarInformacaoAjuizamento(List<CDAAjuizada> cdasAjuizadas) {
        List<RespostaIntegraSoftplan> respostas = new ArrayList<>();
        try {
            for (CDAAjuizada cdaAjuizada : cdasAjuizadas) {
                CDAAnoNumero cdaAnoNumero = extrairAnoNumeroCDA(cdaAjuizada.numeroCDA);
                if (existeProcessoJudicial(cdaAnoNumero.ano, cdaAnoNumero.numero) && cdaAjuizada.codigoEvento.equals(EFETUAR_AJUIZAMENTO)) {
                    throw new ExcecaoNegocioGenerica("CDA [" + cdaAnoNumero.numero + "] do ano [" + cdaAnoNumero.ano + "] já associada ao processo [" + cdaAjuizada.numeroProcessoJudicial + "].");
                }
            }
            for (CDAAjuizada cdaAjuizada : cdasAjuizadas) {
                CDAAnoNumero cdaAnoNumero = extrairAnoNumeroCDA(cdaAjuizada.numeroCDA);
                if (cdaAjuizada.codigoEvento.equals(EFETUAR_AJUIZAMENTO)) {
                    CertidaoDividaAtiva cda = findCDAByAnoNumero(cdaAnoNumero.ano, cdaAnoNumero.numero);
                    if (cda != null) {
                        if (cdaAjuizada.dataAjuizamento == null || cdaAjuizada.numeroProcessoJudicial == null) {
                            throw new ExcecaoNegocioGenerica("Os campos de Data de Ajuizamento e Número do Processo Judicial são obrigatórios para efetuar um ajuizamento");
                        }
                        ProcessoJudicial processo = new ProcessoJudicial();
                        processo.setNumero(cdaAjuizada.numeroProcessoJudicial);
                        processo.setNumeroProcessoForum(cdaAjuizada.numeroProcessoJudicial);
                        processo.setDataAjuizamento(cdaAjuizada.dataAjuizamento);
                        processo.setSituacao(ProcessoJudicial.Situacao.ATIVO);
                        processo.setTipoAcaoProcessoJudicial(TipoAcaoProcessoJudicial.PETICAO);
                        ProcessoJudicialCDA processoCDA = new ProcessoJudicialCDA();
                        processoCDA.setCertidaoDividaAtiva(cda);
                        processoCDA.setProcessoJudicial(processo);
                        AlteracaoSituacaoCDA alteracaoCDA = new AlteracaoSituacaoCDA(TipoAlteracaoCertidaoDA.AJUIZAR,
                            cdaAjuizada.dataAjuizamento, sistemaFacade.getUsuarioCorrente(),
                            null, null, null, "Processo judicial número " + cdaAjuizada.numeroProcessoJudicial, cda);
                        cda.setSituacaoJudicial(SituacaoJudicial.AJUIZADA);
                        em.persist(cda);
                        em.persist(processo);
                        em.persist(processoCDA);
                        em.persist(alteracaoCDA);

                        for (ItemCertidaoDividaAtiva item : cda.getItensCertidaoDividaAtiva()) {
                            List<ResultadoParcela> resultados = new ConsultaParcela()
                                .addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, item.getItemInscricaoDividaAtiva().getId())
                                .executaConsulta().getResultados();
                            for (ResultadoParcela resultado : resultados) {
                                ParcelaValorDivida pvd = em.find(ParcelaValorDivida.class, resultado.getId());
                                pvd.setDividaAtivaAjuizada(true);
                                if (SituacaoParcela.PAGO_PARCELAMENTO.equals(resultado.getSituacaoEnumValue())) {
                                    Long idParcela = cancelamentoParcelamentoFacade.buscarIdParcelaDoCancelamentoDaParcelaOriginal(resultado.getIdParcela());
                                    if (idParcela != null) {
                                        ParcelaValorDivida pvdCancelamento = em.find(ParcelaValorDivida.class, idParcela);
                                        pvdCancelamento.setDividaAtivaAjuizada(true);
                                    }
                                }
                            }

                        }

                        respostas.add(new RespostaIntegraSoftplan(1L, "Processamento realizado com sucesso"));
                    } else {
                        throw new ExcecaoNegocioGenerica("Não foi possível encontrar a CDA com o número [" + cdaAnoNumero.numero + "] do ano [" + cdaAnoNumero.ano + "].");
                    }
                } else if (cdaAjuizada.codigoEvento.equals(CANCELAR_AJUIZAMENTO)) {
                    ProcessoJudicialCDA processoCDA = findProcessoJudicialCDAByNumero(cdaAnoNumero.ano, cdaAnoNumero.numero);

                    ProcessoJudicialExtincao novoProcessoJudicialExtincao = getNovoProcessoJudicialExtincaoPorProcessoJudicial(processoCDA.getProcessoJudicial());
                    processoJudicialFacade.inativarProcessoJudicial(novoProcessoJudicialExtincao);
                    novoProcessoJudicialExtincao = em.merge(novoProcessoJudicialExtincao);
                } else {
                    respostas.add(new RespostaIntegraSoftplan(-1L, "Código do evento inválido"));
                }
            }
        } catch (Exception e) {
            respostas.add(new RespostaIntegraSoftplan(-1L, e.getMessage()));
        }
        return respostas;
    }

    private ProcessoJudicialExtincao getNovoProcessoJudicialExtincaoPorProcessoJudicial(ProcessoJudicial processo) {
        ProcessoJudicialExtincao processoJudicialExtincao = new ProcessoJudicialExtincao();

        processoJudicialExtincao.setProcessoJudicial(processo);
        processoJudicialExtincao.setSituacao(ProcessoJudicialExtincao.Situacao.EFETIVADO);
        processoJudicialExtincao.setCancelamento(new Date());
        processoJudicialExtincao.setMotivoCancelamento("Processo gerado automaticamente pela integração com a SoftPlan.");

        return processoJudicialExtincao;
    }

    public CDAAnoNumero extrairAnoNumeroCDA(Long numeroCDA) throws NumberFormatException {
        String numeroCertidaoAsString = String.valueOf(numeroCDA);
        String numero = numeroCertidaoAsString.substring(0, numeroCertidaoAsString.length() - 4);
        String ano = numeroCertidaoAsString.substring(numeroCertidaoAsString.length() - 4);
        CDAAnoNumero numeror = new CDAAnoNumero(Long.parseLong(numero), Integer.parseInt(ano));
        return numeror;
    }

    private CertidaoDividaAtiva findCDAByAnoNumero(Integer ano, Long numero) {
        Query q = em.createQuery("select cda from CertidaoDividaAtiva cda where cda.numero = :numero and cda.exercicio.ano = :ano");
        q.setParameter("numero", numero);
        q.setParameter("ano", ano);
        if (!q.getResultList().isEmpty()) {
            return (CertidaoDividaAtiva) q.getResultList().get(0);
        }
        return null;
    }

    public boolean existeProcessoJudicial(Integer ano, Long numero) {
        Query q = criarConsultaFindProcessoJudicialCDAByNumero(ano, numero);
        return !q.getResultList().isEmpty();
    }

    public ProcessoJudicialCDA findProcessoJudicialCDAByNumero(Integer ano, Long numero) {
        Query q = criarConsultaFindProcessoJudicialCDAByNumero(ano, numero);
        if (!q.getResultList().isEmpty()) {
            return (ProcessoJudicialCDA) q.getResultList().get(0);
        }
        return null;
    }

    private Query criarConsultaFindProcessoJudicialCDAByNumero(Integer ano, Long numero) {
        Query q = em.createQuery("select pjc from ProcessoJudicialCDA pjc "
            + "where pjc.certidaoDividaAtiva.numero = :numero "
            + "and pjc.certidaoDividaAtiva.exercicio.ano = :ano "
            + "and pjc.processoJudicial.situacao = :situacao ");
        q.setParameter("numero", numero);
        q.setParameter("ano", ano);
        q.setParameter("situacao", ProcessoJudicial.Situacao.ATIVO);
        return q;
    }

    private boolean processoJudicialNaoTemOutraCDA(ProcessoJudicial processoJudicial) {
        Query q = em.createQuery("select count(distinct pjc.certidaoDividaAtiva) "
            + "from ProcessoJudicialCDA pjc "
            + "where pjc.processoJudicial = :processoJudicial");
        q.setParameter("processoJudicial", processoJudicial);
        if (!q.getResultList().isEmpty()) {
            return ((Long) q.getResultList().get(0)) == 1;
        }
        return true;
    }

    public List<RespostaIntegraSoftplan> tratarInformacaoPenhora(Long numeroCDA, Date dataPenhora, List<VeiculoCDA> veiculos, List<ImovelCDA> imoveis, List<TituloCDA> titulos, List<GarantiaCDA> garantias) {
        List<RespostaIntegraSoftplan> respostas = new ArrayList<>();
        try {
            CDAAnoNumero cdaAnoNumero = extrairAnoNumeroCDA(numeroCDA);
            CertidaoDividaAtiva cda = findCDAByAnoNumero(cdaAnoNumero.ano, cdaAnoNumero.numero);

            AlteracaoSituacaoCDA alteracaoCDA = new AlteracaoSituacaoCDA(TipoAlteracaoCertidaoDA.PENHORAR,
                dataPenhora, sistemaFacade.getUsuarioCorrente(), null, null, null, "Penhora", cda);
            cda.setSituacaoCertidaoDA(SituacaoCertidaoDA.PENHORA_REALIZADA);
            for (VeiculoCDA obj : veiculos) {
                obj.setCertidaoDividaAtiva(cda);
                em.persist(obj);
            }
            for (ImovelCDA obj : imoveis) {
                obj.setCertidaoDividaAtiva(cda);
                em.persist(obj);
            }
            for (TituloCDA obj : titulos) {
                obj.setCertidaoDividaAtiva(cda);
                em.persist(obj);
            }
            for (GarantiaCDA obj : garantias) {
                obj.setCertidaoDividaAtiva(cda);
                em.persist(obj);
            }
            em.persist(cda);
            em.persist(alteracaoCDA);
        } catch (Exception e) {
            respostas.add(new RespostaIntegraSoftplan(-1L, e.getMessage()));
            e.printStackTrace();
        }
        return respostas;
    }

    public List<RespostaIntegraSoftplan> tratarRecuperarSaldoAtualizado(Long numeroCDA, ValoresAtualizadosCDA valoresAtualizadosCDA) {
        List<RespostaIntegraSoftplan> respostas = new ArrayList<>();
        try {
            CDAAnoNumero cdaAnoNumero = extrairAnoNumeroCDA(numeroCDA);
            CertidaoDividaAtiva cda = findCDAByAnoNumero(cdaAnoNumero.ano, cdaAnoNumero.numero);

            calcularValoresDaCDA(valoresAtualizadosCDA, cda);
        } catch (Exception e) {
            respostas.add(new RespostaIntegraSoftplan(-1L, e.getMessage()));
            e.printStackTrace();
        }
        return respostas;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ComposicaoCDA calcularValorDoTributo(ResultadoParcela resultadoParcela, Long idItemInscricaoDividaAtiva, Tributo.TipoTributo tipoTributo, BigDecimal valorOriginal, BigDecimal valorTributo, BigDecimal multa, BigDecimal juros, BigDecimal correcao, BigDecimal honorarios) {
        BigDecimal cem = new BigDecimal("100");
        int escala = 8;

        BigDecimal percentualTributo = BigDecimal.ZERO;
        if (valorTributo.compareTo(BigDecimal.ZERO) > 0 && valorOriginal.compareTo(BigDecimal.ZERO) > 0) {
            percentualTributo = ((valorTributo.multiply(cem))
                .divide(valorOriginal.compareTo(BigDecimal.ZERO) == 0 ? valorTributo : valorOriginal, escala, RoundingMode.HALF_UP))
                .divide(cem, escala, RoundingMode.HALF_UP);
        }

        BigDecimal percentualMulta = percentualTributo.multiply(multa);
        BigDecimal percentualJuros = percentualTributo.multiply(juros);
        BigDecimal percentualCorrecao = percentualTributo.multiply(correcao);
        BigDecimal percentualHonorarios = percentualTributo.multiply(honorarios);

        ComposicaoCDA composicao = new ComposicaoCDA(resultadoParcela, idItemInscricaoDividaAtiva);
        composicao.setTipoTributo(tipoTributo);
        composicao.setValorTributo(valorTributo);
        composicao.setValorMulta(percentualMulta);
        composicao.setValorJuros(percentualJuros);
        composicao.setValorCorrecao(percentualCorrecao);
        composicao.setValorHonorarios(percentualHonorarios);
        composicao.recalcularTotal();
        return composicao;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void calcularValoresDaCDA(ValoresAtualizadosCDA valoresAtualizadosCDA, CertidaoDividaAtiva cda) {
        valoresAtualizadosCDA.setDetalhamento(new ArrayList<String>());

        BigDecimal valorImposto = BigDecimal.ZERO;
        BigDecimal valorTaxa = BigDecimal.ZERO;
        BigDecimal valorJuros = BigDecimal.ZERO;
        BigDecimal valorMulta = BigDecimal.ZERO;
        BigDecimal valorCorrecao = BigDecimal.ZERO;
        BigDecimal valorHonorarios = BigDecimal.ZERO;
        BigDecimal valorPago = BigDecimal.ZERO;
        BigDecimal valorTotal = BigDecimal.ZERO;

        List<VOItemCertidaoDividaAtiva> itens = certidaoDividaAtivaFacade.buscarVOItensCertidao(cda.getId());
        for (VOItemCertidaoDividaAtiva item : itens) {
            List<ResultadoParcela> parcelasCda = Lists.newArrayList();

            if (ItemInscricaoDividaAtiva.Situacao.ATIVO.equals(item.getSituacaoItemInscricao())) {
                parcelasCda.addAll(certidaoDividaAtivaFacade.buscarParcelasDoCalculo(item.getIdItemInscricao()));

                Set<ResultadoParcela> parcelasSemRepeticao = new HashSet<>(parcelasCda);
                parcelasCda = Lists.newArrayList(parcelasSemRepeticao);

                List<ResultadoParcela> parcelasDoUltimoParcelamentoAtivo = certidaoDividaAtivaFacade.separarParcelasDoUltimoParcelamentoAtivo(valoresAtualizadosCDA, parcelasCda);
                BigDecimal valorOriginalUltimoParcelamento = certidaoDividaAtivaFacade.buscarValorOriginalDoUltimoParcelamento(parcelasDoUltimoParcelamentoAtivo);
                BigDecimal valorAtualizadoUltimoParcelamento = certidaoDividaAtivaFacade.separarValorTotalDoUltimoParcelamento(parcelasDoUltimoParcelamentoAtivo);
                ResultadoParcela parcelaOriginalCda = certidaoDividaAtivaFacade.separarValorOriginalDaCda(parcelasCda);

                if (SituacaoCertidaoDA.ABERTA.equals(cda.getSituacaoCertidaoDA())
                    && naoTemParcelaAberta(parcelasCda) && itens.size() > 1) {
                    continue;
                }

                if ((SituacaoCertidaoDA.QUITADA.equals(cda.getSituacaoCertidaoDA()) || naoTemParcelaAberta(parcelasCda))
                    && parcelaOriginalCda != null) {
                    valorJuros = valorJuros.add(parcelaOriginalCda.getValorJuros());
                    valorMulta = valorMulta.add(parcelaOriginalCda.getValorMulta());
                    valorImposto = valorImposto.add(parcelaOriginalCda.getValorImposto());
                    valorTaxa = valorTaxa.add(parcelaOriginalCda.getValorTaxa());
                    valorCorrecao = valorCorrecao.add(parcelaOriginalCda.getValorCorrecao());
                    valorHonorarios = valorHonorarios.add(parcelaOriginalCda.getValorHonorarios());
                    valorPago = valorPago.add(parcelaOriginalCda.getValorPago());

                    if (valorImposto.compareTo(BigDecimal.ZERO) > 0) {
                        valoresAtualizadosCDA.addComposicao(calcularValorDoTributo(parcelaOriginalCda,
                            item.getIdItemInscricao(), Tributo.TipoTributo.IMPOSTO, parcelaOriginalCda.getValorOriginal(),
                            parcelaOriginalCda.getValorImposto(), parcelaOriginalCda.getValorMulta(),
                            parcelaOriginalCda.getValorJuros(), parcelaOriginalCda.getValorCorrecao(),
                            parcelaOriginalCda.getValorHonorarios()));
                    }
                    if (valorTaxa.compareTo(BigDecimal.ZERO) > 0) {
                        valoresAtualizadosCDA.addComposicao(calcularValorDoTributo(parcelaOriginalCda,
                            item.getIdItemInscricao(), Tributo.TipoTributo.TAXA, parcelaOriginalCda.getValorOriginal(),
                            parcelaOriginalCda.getValorTaxa(), parcelaOriginalCda.getValorMulta(),
                            parcelaOriginalCda.getValorJuros(), parcelaOriginalCda.getValorCorrecao(),
                            parcelaOriginalCda.getValorHonorarios()));
                    }
                    valorTotal = valorTotal.add(parcelaOriginalCda.getValorImposto().add(parcelaOriginalCda.getValorTaxa()).add(parcelaOriginalCda.getValorMulta()).add(parcelaOriginalCda.getValorJuros()).add(parcelaOriginalCda.getValorCorrecao()).add(parcelaOriginalCda.getValorHonorarios()));
                } else if (valorAtualizadoUltimoParcelamento.compareTo(BigDecimal.ZERO) > 0 && parcelaOriginalCda != null) {
                    valorImposto = calcularValorProporcional(parcelaOriginalCda.getValorImposto(), valorOriginalUltimoParcelamento, valorAtualizadoUltimoParcelamento);
                    valorTaxa = calcularValorProporcional(parcelaOriginalCda.getValorTaxa(), valorOriginalUltimoParcelamento, valorAtualizadoUltimoParcelamento);
                    valorJuros = calcularValorProporcional(parcelaOriginalCda.getValorJuros(), valorOriginalUltimoParcelamento, valorAtualizadoUltimoParcelamento);
                    valorMulta = calcularValorProporcional(parcelaOriginalCda.getValorMulta(), valorOriginalUltimoParcelamento, valorAtualizadoUltimoParcelamento);
                    valorCorrecao = calcularValorProporcional(parcelaOriginalCda.getValorCorrecao(), valorOriginalUltimoParcelamento, valorAtualizadoUltimoParcelamento);
                    valorHonorarios = calcularValorProporcional(parcelaOriginalCda.getValorHonorarios(), valorOriginalUltimoParcelamento, valorAtualizadoUltimoParcelamento);
                    valorPago = calcularValorProporcional(parcelaOriginalCda.getValorPago(), valorOriginalUltimoParcelamento, valorAtualizadoUltimoParcelamento); // TODO verificar o calculo do valor pago

                    if (valorImposto.compareTo(BigDecimal.ZERO) > 0) {
                        valoresAtualizadosCDA.addComposicao(calcularValorDoTributo(parcelaOriginalCda, item.getIdItemInscricao(), Tributo.TipoTributo.IMPOSTO, valorImposto.add(valorTaxa), valorImposto, valorMulta, valorJuros, valorCorrecao, valorHonorarios));
                    }
                    if (valorTaxa.compareTo(BigDecimal.ZERO) > 0) {
                        valoresAtualizadosCDA.addComposicao(calcularValorDoTributo(parcelaOriginalCda, item.getIdItemInscricao(), Tributo.TipoTributo.TAXA, valorImposto.add(valorTaxa), valorTaxa, valorMulta, valorJuros, valorCorrecao, valorHonorarios));
                    }
                    valorTotal = valorTotal.add(valorImposto.add(valorTaxa).add(valorMulta).add(valorJuros).add(valorCorrecao).add(valorHonorarios));
                } else {
                    for (ResultadoParcela resultado : parcelasCda) {
                        if (parcelasCda.size() > 1 && (SituacaoParcela.PARCELADO.equals(resultado.getSituacaoEnumValue())
                            || SituacaoParcela.PAGO_PARCELAMENTO.equals(resultado.getSituacaoEnumValue())
                            || SituacaoParcela.ESTORNADO.equals(resultado.getSituacaoEnumValue())
                            || (resultado.isParcelaBloqueioJudicial()) && !SituacaoParcela.AGUARDANDO_PAGAMENTO_BLOQUEIO_JUDICIAL.equals(resultado.getSituacaoEnumValue()))) {
                            continue;
                        }
                        valorJuros = valorJuros.add(resultado.getValorJuros());
                        valorMulta = valorMulta.add(resultado.getValorMulta());
                        valorImposto = valorImposto.add(resultado.getValorImposto());
                        valorTaxa = valorTaxa.add(resultado.getValorTaxa());
                        valorCorrecao = valorCorrecao.add(resultado.getValorCorrecao());
                        valorHonorarios = valorHonorarios.add(resultado.getValorHonorarios());
                        valorPago = valorPago.add(resultado.getValorPago());

                        if (valorImposto.compareTo(BigDecimal.ZERO) > 0) {
                            valoresAtualizadosCDA.addComposicao(calcularValorDoTributo(resultado, item.getIdItemInscricao(), Tributo.TipoTributo.IMPOSTO, resultado.getValorOriginal(), resultado.getValorImposto(), resultado.getValorMulta(), resultado.getValorJuros(), resultado.getValorCorrecao(), resultado.getValorHonorarios()));
                        }
                        if (valorTaxa.compareTo(BigDecimal.ZERO) > 0) {
                            valoresAtualizadosCDA.addComposicao(calcularValorDoTributo(resultado, item.getIdItemInscricao(), Tributo.TipoTributo.TAXA, resultado.getValorOriginal(), resultado.getValorTaxa(), resultado.getValorMulta(), resultado.getValorJuros(), resultado.getValorCorrecao(), resultado.getValorHonorarios()));
                        }
                        valorTotal = valorTotal.add(resultado.getValorImposto().add(resultado.getValorTaxa()).add(resultado.getValorMulta()).add(resultado.getValorJuros()).add(resultado.getValorCorrecao()).add(resultado.getValorHonorarios()));
                    }
                }
            }

        }
        valoresAtualizadosCDA.setValorImposto(valorImposto);
        valoresAtualizadosCDA.setValorTaxa(valorTaxa);
        valoresAtualizadosCDA.setValorJuros(valorJuros);
        valoresAtualizadosCDA.setValorMulta(valorMulta);
        valoresAtualizadosCDA.setValorCorrecao(valorCorrecao);
        valoresAtualizadosCDA.setValorHonorarios(valorHonorarios);
        valoresAtualizadosCDA.setValorTotal(valorTotal);
        valoresAtualizadosCDA.setValorPago(valorPago);
        valoresAtualizadosCDA.getDetalhamento().add("Valor Atualizado da CDA (" + cda.getNumero() + "/" + (cda.getExercicio() == null ? cda.getAno() : cda.getExercicio().getAno()) + "): " + Util.formataValor(valorTotal));
        valoresAtualizadosCDA.arredondarValoresDaComposicao();
    }

    private boolean naoTemParcelaAberta(List<ResultadoParcela> parcelasCda) {
        for (ResultadoParcela resultadoParcela : parcelasCda) {
            if (resultadoParcela.isEmAberto()) {
                return false;
            }
        }
        return true;
    }

    private BigDecimal calcularValorProporcional(BigDecimal valor, BigDecimal valorOriginal, BigDecimal valorAtualizado) {
        if (valor.compareTo(BigDecimal.ZERO) > 0) {
            return valor.multiply(valorAtualizado).divide(valorOriginal, 8, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public static class CDAAnoNumero {

        public final Long numero;
        public final Integer ano;

        public CDAAnoNumero(Long numero, Integer ano) {
            this.numero = numero;
            this.ano = ano;
        }
    }

    public static class CDAAjuizada {
        public Long numeroCDA;
        public Integer codigoEvento;
        public Date dataAjuizamento;
        public String numeroProcessoJudicial;
    }
}
