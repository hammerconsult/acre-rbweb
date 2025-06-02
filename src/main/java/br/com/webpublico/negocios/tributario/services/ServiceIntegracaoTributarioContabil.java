package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.OperacaoIntegracaoTribCont;
import br.com.webpublico.enums.OperacaoReceita;
import br.com.webpublico.enums.TipoIntegracao;
import br.com.webpublico.exception.ContaReceitaException;
import br.com.webpublico.exception.SemUnidadeException;
import br.com.webpublico.negocios.ArrecadacaoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LoteBaixaFacade;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ServiceIntegracaoTributarioContabil {

    private static final Logger logger = LoggerFactory.getLogger(ServiceIntegracaoTributarioContabil.class);
    public static final BigDecimal CEM = BigDecimal.valueOf(100);
    public static final BigDecimal MENOS_UM_CENTAVO = new BigDecimal("-0.01");
    public static final BigDecimal UM_CENTAVO = new BigDecimal("0.01");
    @PersistenceContext
    protected transient EntityManager em;
    private Map<Long, Map<Integer, ContaTributoReceita>> mapaContas;
    private Map<Integer, Exercicio> mapaExercicio;
    private LoteBaixaFacade loteBaixaFacade;

    @PostConstruct
    private void init() {
        try {
            loteBaixaFacade = (LoteBaixaFacade) new InitialContext().lookup("java:module/LoteBaixaFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar o facade: {}", e);
        }
    }

    public void limparMapaContas() {
        mapaContas = Maps.newHashMap();
    }

    public ContaTributoReceita getContaTributoReceitaPorTributoExercicio(Long idTributo, Date dataPagamento) throws ContaReceitaException {
        Calendar dataReferencia = Calendar.getInstance();
        dataReferencia.setTime(dataPagamento);
        Integer anoDataPagamento = dataReferencia.get(Calendar.YEAR);

        if (mapaContas == null) {
            mapaContas = Maps.newHashMap();
        }
        if (mapaContas.get(idTributo) == null) {
            mapaContas.put(idTributo, new HashMap<Integer, ContaTributoReceita>());
        }
        if (mapaContas.get(idTributo).get(anoDataPagamento) == null) {
            Query q = em.createQuery("select conta from ContaTributoReceita conta " +
                    " where conta.tributo.id = :idTributo " +
                " and conta.enquadramento.exercicioVigente.ano = :exercicioAtual" +
                " and :dataReferencia between conta.inicioVigencia and coalesce(conta.fimVigencia ,:dataReferencia)");
            q.setParameter("idTributo", idTributo);
            q.setParameter("exercicioAtual", anoDataPagamento);
            q.setParameter("dataReferencia", dataPagamento);
            List resultList = q.getResultList();
            if (resultList.isEmpty()) {
                Tributo tributo = em.find(Tributo.class, idTributo);
                throw new ContaReceitaException(tributo, anoDataPagamento);
            }
            mapaContas.get(idTributo).put(anoDataPagamento, (ContaTributoReceita) resultList.get(0));
        }
        return mapaContas.get(idTributo).get(anoDataPagamento);
    }

    public Exercicio getAno(Date data) {
        return getAno(new DateTime(data));
    }

    public Exercicio getAno(DateTime data) {
        if (mapaExercicio == null) {
            mapaExercicio = Maps.newHashMap();
        }
        if (mapaExercicio.get(data.getYear()) == null) {
            Query q = em.createQuery("select e from Exercicio e where e.ano = :ano").setParameter("ano", data.getYear());
            if (!q.getResultList().isEmpty()) {
                mapaExercicio.put(data.getYear(), (Exercicio) q.getResultList().get(0));
            }
        }
        return mapaExercicio.get(data.getYear());
    }

    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    public void integrarArrecadacao(LoteBaixa loteBaixa, List<VOItemLoteBaixa> itensLoteBaixa, TipoIntegracao tipoIntegracao) {
        List<IntegraTributoDam> principais = Lists.newArrayList();
        List<IntegraTributoDam> descontos = Lists.newArrayList();
        List<IntegraTributoDam> acrescimos = Lists.newArrayList();
        List<IntegraTributoDam> honorarios = Lists.newArrayList();

        BigDecimal lancadoArrecadacao = BigDecimal.ZERO;
        BigDecimal lancadoDesconto = BigDecimal.ZERO;
        BigDecimal lancadoAcrescimo = BigDecimal.ZERO;
        BigDecimal lancadoHonorarios = BigDecimal.ZERO;

        ArrecadacaoFacade.AssistenteArrecadacao assistenteArrecadacao = new ArrecadacaoFacade.AssistenteArrecadacao(null, loteBaixa, itensLoteBaixa.size());
        assistenteArrecadacao.setDescricaoProcesso("Gerando a integração da arrecadação do lote " + loteBaixa.getCodigoLote());
        if (!itensLoteBaixa.isEmpty()) {
            Collections.sort(itensLoteBaixa);
            for (VOItemLoteBaixa item : itensLoteBaixa) {
                assistenteArrecadacao.conta();
                List<IntegraTributoDam> principaisItem = Lists.newArrayList();
                List<IntegraTributoDam> descontosItem = Lists.newArrayList();
                List<IntegraTributoDam> acrescimosItem = Lists.newArrayList();
                List<IntegraTributoDam> honorariosItem = Lists.newArrayList();

                BigDecimal totalItemAcrescimo = BigDecimal.ZERO;
                BigDecimal totalItemHonorarios = BigDecimal.ZERO;
                BigDecimal totalItemDesconto = BigDecimal.ZERO;
                BigDecimal totalItemArrecadacao = BigDecimal.ZERO;

                if (item.getIdDam() != null) {
                    List<VOItemDam> itensDam = loteBaixaFacade.buscarVOItemDamDoDam(item.getIdDam());
                    Collections.sort(itensDam);
                    for (VOItemDam itemDam : itensDam) {
                        List<VOTributoDam> tributosDam = loteBaixaFacade.buscarVOTributosDamDoItem(itemDam.getId());
                        BigDecimal totalDam = BigDecimal.ZERO;
                        Collections.sort(tributosDam);
                        for (VOTributoDam tributoDAM : tributosDam) {
                            totalDam = totalDam.add(tributoDAM.getValorComDesconto());
                        }
                        BigDecimal valorPagoDoItem = itemDam.getValorTotal().multiply(item.getValorPago()).divide(item.getTotal(), 8, RoundingMode.HALF_UP);
                        for (VOTributoDam tributoDAM : tributosDam) {
                            totalItemAcrescimo = totalItemAcrescimo.add(processarItensParaIntegracaoAcrescimos(item, acrescimosItem, valorPagoDoItem, totalDam, itemDam, tributoDAM));
                            totalItemHonorarios = totalItemHonorarios.add(processarItensParaIntegracaoHonorarios(item, honorariosItem, valorPagoDoItem, totalDam, itemDam, tributoDAM));
                            totalItemDesconto = totalItemDesconto.add(processarItensParaIntegracaoDesconto(item, descontosItem, itemDam, tributoDAM));
                            totalItemArrecadacao = totalItemArrecadacao.add(processarItensParaIntegracaoArrecadacao(item, principaisItem, valorPagoDoItem, totalDam, itemDam, tributoDAM));
                        }
                    }
                }

                BigDecimal totalItem = (totalItemArrecadacao.add(totalItemAcrescimo).add(totalItemHonorarios)).setScale(2, RoundingMode.HALF_UP);
                BigDecimal diferenca = totalItem.subtract(item.getValorPago());
                if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
                    arredondarDiferencaNaIntegracao(principaisItem, item.getValorPago(), totalItem);
                }
                lancadoArrecadacao = lancadoArrecadacao.add(totalItemArrecadacao);
                lancadoAcrescimo = lancadoAcrescimo.add(totalItemAcrescimo);
                lancadoHonorarios = lancadoHonorarios.add(totalItemHonorarios);
                lancadoDesconto = lancadoDesconto.add(totalItemDesconto);

                principais.addAll(principaisItem);
                acrescimos.addAll(acrescimosItem);
                honorarios.addAll(honorariosItem);
                descontos.addAll(descontosItem);
            }

            try {
                for (IntegraTributoDam acrescimo : acrescimos) {
                    if (BigDecimal.ZERO.compareTo(acrescimo.valor) != 0) {
                        integrarAcrescimoArrecadacao(loteBaixa, acrescimo, tipoIntegracao, OperacaoIntegracaoTribCont.MULTA_JUROS_CORRECAO);
                    }
                }

                for (IntegraTributoDam principal : principais) {
                    if (BigDecimal.ZERO.compareTo(principal.valor) != 0) {
                        integrarPrincipalArrecadacao(loteBaixa, principal, tipoIntegracao, OperacaoIntegracaoTribCont.PRINCIPAL);
                    }
                }
                for (IntegraTributoDam honorario : honorarios) {
                    if (BigDecimal.ZERO.compareTo(honorario.valor) != 0) {
                        integrarHonorarioArrecadacao(loteBaixa, honorario, tipoIntegracao, OperacaoIntegracaoTribCont.HONORARIOS);
                    }
                }
            } catch (Exception e) {
                logger.error("Erro ao integrarArrecadacao: {}", e);
            }
        }
    }

    private void arredondarDiferencaNaIntegracao(List<IntegraTributoDam> itens, BigDecimal total, BigDecimal lancado) {
        if (total.compareTo(lancado) != 0 && lancado.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diferenca = total.subtract(lancado).setScale(2, RoundingMode.HALF_UP);

            while (diferenca.compareTo(BigDecimal.ZERO) != 0 && itens.size() > 0) {
                BigDecimal valorProporcionalDeferenca = BigDecimal.ZERO;
                for (IntegraTributoDam tributoDam : itens) {
                    if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
                        valorProporcionalDeferenca = diferenca.compareTo(BigDecimal.ZERO) > 0 ? UM_CENTAVO : diferenca.compareTo(BigDecimal.ZERO) < 0 ? MENOS_UM_CENTAVO : BigDecimal.ZERO;
                        tributoDam.valor = tributoDam.valor.add(valorProporcionalDeferenca);
                        lancado = lancado.add(valorProporcionalDeferenca);
                        diferenca = total.subtract(lancado).setScale(2, RoundingMode.HALF_UP);
                    }
                }
                if (valorProporcionalDeferenca.compareTo(BigDecimal.ZERO) != 0) {
                    diferenca = total.subtract(lancado).setScale(2, RoundingMode.HALF_UP);
                } else {
                    diferenca = BigDecimal.ZERO;
                }
            }
        }
    }

    private BigDecimal processarItensParaIntegracaoAcrescimos(VOItemLoteBaixa item, List<IntegraTributoDam> acrescimos, BigDecimal valorPagoDoItem, BigDecimal totalDam, VOItemDam itemDam, VOTributoDam tributoDAM) {
        BigDecimal lancadoAcrescimo = BigDecimal.ZERO;
        if (tributoDAM.getTipoTributo().isAcrescimosSemHonorarios()
            && itemDam.getTotalAcrescimosSemHonorarios().compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal valorAcrescimos = tributoDAM.getValorComDesconto();
            if (valorPagoDoItem.compareTo(item.getValorPago()) != 0) {
                BigDecimal proporcaoAcrescimos = tributoDAM.getValorComDesconto().compareTo(BigDecimal.ZERO) != 0 ? tributoDAM.getValorComDesconto().divide(totalDam, 8, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                valorAcrescimos = (proporcaoAcrescimos.multiply(valorPagoDoItem));
            }
            valorAcrescimos = valorAcrescimos.setScale(2, RoundingMode.HALF_UP);

            if (valorAcrescimos.compareTo(BigDecimal.ZERO) > 0 || tributoDAM.getValorOriginal().compareTo(BigDecimal.ZERO) > 0) {
                IntegraTributoDam tributo = new IntegraTributoDam(tributoDAM.getIdTributo(),
                        itemDam.getDividaAtiva(), valorAcrescimos, item.getIdLoteBaixa(), item.getDataPagamento(),
                        itemDam.getIdEntidade(), itemDam.getExercicio(), tributoDAM.getTipoTributo());

                if (acrescimos.contains(tributo)) {
                    acrescimos.get(acrescimos.indexOf(tributo)).addValor(tributo.valor);
                } else {
                    acrescimos.add(tributo);
                }
                lancadoAcrescimo = lancadoAcrescimo.add(valorAcrescimos);
            }
        }
        return lancadoAcrescimo;
    }

    private BigDecimal processarItensParaIntegracaoHonorarios(VOItemLoteBaixa item, List<IntegraTributoDam> honorarios,  BigDecimal valorPagoDoItem, BigDecimal totalDam, VOItemDam itemDam, VOTributoDam tributoDAM) {
        BigDecimal lancadoHonorarios = BigDecimal.ZERO;
        if (tributoDAM.getTipoTributo().isHonorarios() && itemDam.getHonorarios().compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal valorHonorarios = tributoDAM.getValorComDesconto();
            if (valorPagoDoItem.compareTo(item.getValorPago()) != 0) {
                BigDecimal proporcaoHonorarios = tributoDAM.getValorComDesconto().compareTo(BigDecimal.ZERO) != 0 ? tributoDAM.getValorComDesconto().divide(totalDam, 8, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                valorHonorarios = (proporcaoHonorarios.multiply(valorPagoDoItem));
            }
            valorHonorarios = valorHonorarios.setScale(2, RoundingMode.HALF_UP);

            if (valorHonorarios.compareTo(BigDecimal.ZERO) > 0 || tributoDAM.getValorOriginal().compareTo(BigDecimal.ZERO) > 0) {
                IntegraTributoDam tributo = new IntegraTributoDam(tributoDAM.getIdTributo(),
                        itemDam.getDividaAtiva(), valorHonorarios, item.getIdLoteBaixa(), item.getDataPagamento(),
                        itemDam.getIdEntidade(), itemDam.getExercicio(), tributoDAM.getTipoTributo());

                if (honorarios.contains(tributo)) {
                    honorarios.get(honorarios.indexOf(tributo)).addValor(tributo.valor);
                } else {
                    honorarios.add(tributo);
                }
                lancadoHonorarios = lancadoHonorarios.add(valorHonorarios);
            }
        }
        return lancadoHonorarios;
    }

    private BigDecimal processarItensParaIntegracaoDesconto(VOItemLoteBaixa item, List<IntegraTributoDam> descontos, VOItemDam itemDam, VOTributoDam tributoDAM) {
        BigDecimal lancadoDesconto = BigDecimal.ZERO;
        if (tributoDAM.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal desconto = tributoDAM.getDesconto().setScale(2, RoundingMode.HALF_UP);
            IntegraTributoDam tributo = new IntegraTributoDam(tributoDAM.getIdTributo(),
                itemDam.getDividaAtiva(), desconto, item.getIdLoteBaixa(), item.getDataPagamento(),
                itemDam.getIdEntidade(), itemDam.getExercicio(), tributoDAM.getTipoTributo());
            if (descontos.contains(tributo)) {
                descontos.get(descontos.indexOf(tributo)).addValor(tributo.valor);
            } else {
                descontos.add(tributo);
            }
            lancadoDesconto = lancadoDesconto.add(desconto);
        }
        return lancadoDesconto;
    }

    private BigDecimal processarItensParaIntegracaoArrecadacao(VOItemLoteBaixa item, List<IntegraTributoDam> principais, BigDecimal valorPagoDoItem, BigDecimal totalDam, VOItemDam itemDam, VOTributoDam tributoDAM) {
        BigDecimal lancadoArrecadacao = BigDecimal.ZERO;
        if (tributoDAM.getTipoTributo().isImpostoTaxa()) {

            BigDecimal valorArrecadacao = tributoDAM.getValorComDesconto();
            if (valorPagoDoItem.compareTo(item.getValorPago()) != 0) {
                BigDecimal proporcaoArrecadacao = tributoDAM.getValorComDesconto().compareTo(BigDecimal.ZERO) != 0 ? tributoDAM.getValorComDesconto().divide(totalDam, 8, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                valorArrecadacao = (proporcaoArrecadacao.multiply(valorPagoDoItem));
            }
            valorArrecadacao = valorArrecadacao.setScale(2, RoundingMode.HALF_UP);

            if (valorArrecadacao.compareTo(BigDecimal.ZERO) > 0 || tributoDAM.getValorOriginal().compareTo(BigDecimal.ZERO) > 0) {
                IntegraTributoDam tributo = new IntegraTributoDam(tributoDAM.getIdTributo(),
                    itemDam.getDividaAtiva(), valorArrecadacao, item.getIdLoteBaixa(), item.getDataPagamento(),
                    itemDam.getIdEntidade(), itemDam.getExercicio(), tributoDAM.getTipoTributo());
                if (principais.contains(tributo)) {
                    principais.get(principais.indexOf(tributo)).addValor(tributo.valor);
                } else {
                    principais.add(tributo);
                }
                lancadoArrecadacao = lancadoArrecadacao.add(valorArrecadacao);
            }
        }
        return lancadoArrecadacao;
    }

    private void integrarAcrescimoArrecadacao(LoteBaixa loteBaixa, IntegraTributoDam integraTributoDam, TipoIntegracao tipoIntegracao, OperacaoIntegracaoTribCont operacaoIntegracao) throws ContaReceitaException, SemUnidadeException, ExcecaoNegocioGenerica {
        ContaTributoReceita contaTributo = getContaTributoReceitaPorTributoExercicio(integraTributoDam.idTributo, integraTributoDam.dataPagamento);
        ContaReceita contaAcrescimo = contaTributo.getContaExercicio();
        FonteDeRecursos fonteDeRecursos = contaTributo.getFonteDeRecursosExercicio();
        OperacaoReceita operacao = contaTributo.getOperacaoArrecadacaoExercicio();
        if (integraTributoDam.dividaAtiva) {
            contaAcrescimo = contaTributo.getContaDividaAtiva();
            operacao = contaTributo.getOperacaoArrecadacaoDivAtiva();
            fonteDeRecursos = contaTributo.getFonteDeRecursosDividaAtiva();
        }
        integrarTributoArrecadacao(integraTributoDam.idTributo, integraTributoDam.idEntidade, integraTributoDam.exercicio,
            integraTributoDam.valor, loteBaixa, contaAcrescimo, tipoIntegracao, operacao, fonteDeRecursos, operacao.isReceitaBruta() ? operacaoIntegracao : OperacaoIntegracaoTribCont.PRINCIPAL);
    }

    private void integrarHonorarioArrecadacao(LoteBaixa loteBaixa, IntegraTributoDam integraTributoDam, TipoIntegracao tipoIntegracao, OperacaoIntegracaoTribCont operacaoIntegracao) throws ContaReceitaException, SemUnidadeException, ExcecaoNegocioGenerica {
        ContaTributoReceita contaTributo = getContaTributoReceitaPorTributoExercicio(integraTributoDam.idTributo, integraTributoDam.dataPagamento);
        ContaReceita contaAcrescimo = contaTributo.getContaExercicio();
        FonteDeRecursos fonteDeRecursos = contaTributo.getFonteDeRecursosExercicio();
        OperacaoReceita operacao = contaTributo.getOperacaoArrecadacaoExercicio();
        if (integraTributoDam.dividaAtiva) {
            contaAcrescimo = contaTributo.getContaDividaAtiva();
            operacao = contaTributo.getOperacaoArrecadacaoDivAtiva();
            fonteDeRecursos = contaTributo.getFonteDeRecursosDividaAtiva();
        }
        integrarTributoArrecadacao(integraTributoDam.idTributo, integraTributoDam.idEntidade, integraTributoDam.exercicio,
            integraTributoDam.valor, loteBaixa, contaAcrescimo, tipoIntegracao, operacao, fonteDeRecursos, operacao.isReceitaBruta() ? operacaoIntegracao : OperacaoIntegracaoTribCont.PRINCIPAL);
    }

    private void integrarPrincipalArrecadacao(LoteBaixa loteBaixa, IntegraTributoDam integraTributoDam, TipoIntegracao tipoIntegracao, OperacaoIntegracaoTribCont operacaoIntegracao) throws ContaReceitaException, SemUnidadeException, ExcecaoNegocioGenerica {
        ContaTributoReceita contaTributo = getContaTributoReceitaPorTributoExercicio(integraTributoDam.idTributo, integraTributoDam.dataPagamento);
        ContaReceita contaArrecadacao = contaTributo.getContaExercicio();
        OperacaoReceita operacao = contaTributo.getOperacaoArrecadacaoExercicio();
        FonteDeRecursos fonteDeRecursos = contaTributo.getFonteDeRecursosExercicio();
        if (integraTributoDam.dividaAtiva) {
            contaArrecadacao = contaTributo.getContaDividaAtiva();
            operacao = contaTributo.getOperacaoArrecadacaoDivAtiva();
            fonteDeRecursos = contaTributo.getFonteDeRecursosDividaAtiva();
        }
        integrarTributoArrecadacao(integraTributoDam.idTributo, integraTributoDam.idEntidade,integraTributoDam.exercicio,
            integraTributoDam.valor, loteBaixa, contaArrecadacao, tipoIntegracao, operacao, fonteDeRecursos, operacao.isReceitaBruta() ? operacaoIntegracao : OperacaoIntegracaoTribCont.PRINCIPAL);
    }

    private void integrarTributoArrecadacao(Long idTributo,
                                            Long idEntidade,
                                            Integer exercicio,
                                            BigDecimal valor,
                                            LoteBaixa lote,
                                            ContaReceita conta,
                                            TipoIntegracao tipo,
                                            OperacaoReceita operacaoReceitaRealizada,
                                            FonteDeRecursos fonteDeRecursos,
                                            OperacaoIntegracaoTribCont operacaoIntegracao) throws ContaReceitaException, SemUnidadeException, ExcecaoNegocioGenerica {
        if (conta == null) {
            Tributo tributo = em.find(Tributo.class, idTributo);
            throw new ContaReceitaException(tributo, exercicio);
        }
        geraItemArracadacao(lote, idEntidade, valor, conta, tipo, operacaoReceitaRealizada, fonteDeRecursos, operacaoIntegracao);
    }

    public ItemIntegracaoTributarioContabil geraItemArracadacao(LoteBaixa loteBaixa,
                                                                Long idEntidade,
                                                                BigDecimal valorCreditar,
                                                                ContaReceita conta,
                                                                TipoIntegracao tipo,
                                                                OperacaoReceita operacaoReceitaRealizada,
                                                                FonteDeRecursos fonteDeRecursos,
                                                                OperacaoIntegracaoTribCont operacaoIntegracao) throws SemUnidadeException {
        Entidade entidade = em.find(Entidade.class, idEntidade);
        if (entidade == null || entidade.getUnidadeOrganizacionalOrc() == null || entidade.getUnidadeOrganizacional() == null) {
            throw new ExcecaoNegocioGenerica("Não há uma Unidade Organizacional Orçamentária definida para a entidade " + entidade);
        }
        ItemIntegracaoTributarioContabil itemIntegracao = new ItemIntegracaoTributarioContabil();
        itemIntegracao.setDataPagamento(loteBaixa.getDataPagamento());
        itemIntegracao.setDataCredito(loteBaixa.getDataFinanciamento());
        itemIntegracao.setValor(valorCreditar);

        itemIntegracao.setOrcamentaria(entidade.getUnidadeOrganizacionalOrc());
        itemIntegracao.setAdministrativa(entidade.getUnidadeOrganizacional());

        itemIntegracao.setContaReceita(conta);
        itemIntegracao.setTipo(tipo);
        itemIntegracao.setOperacaoReceitaRealizada(operacaoReceitaRealizada);
        itemIntegracao.setFonteDeRecursos(fonteDeRecursos);
        if (operacaoReceitaRealizada.isReceitaBruta()) {
            itemIntegracao.setOperacaoIntegracaoTribCont(operacaoIntegracao);
        } else {
            itemIntegracao.setOperacaoIntegracaoTribCont(OperacaoIntegracaoTribCont.PRINCIPAL);
        }
        itemIntegracao.setLoteBaixa(loteBaixa);
        itemIntegracao.setSubConta(loteBaixa.getSubConta());
        LoteBaixaIntegracao loteBaixaIntegracao = new LoteBaixaIntegracao();
        loteBaixaIntegracao.setItemItengracao(itemIntegracao);
        loteBaixaIntegracao.setLoteBaixa(loteBaixa);
        loteBaixaIntegracao = em.merge(loteBaixaIntegracao);
        return loteBaixaIntegracao.getItemItengracao();
    }

    public List<ItemInconsistenciaIntegracaoTribCont> buscarItensParaInconsistenciaDaIntegracao(LoteBaixa loteBaixa) {
        String sql = "select * from ITEMINTEGRACAOTRIBCONT " +
            " where LOTEBAIXA_ID = :loteBaixa" +
            " and TIPO = :tipo";
        Query q = em.createNativeQuery(sql, ItemIntegracaoTributarioContabil.class);
        q.setParameter("loteBaixa", loteBaixa.getId());
        q.setParameter("tipo", loteBaixa.isEstornado() ? TipoIntegracao.ESTORNO_ARRECADACAO.name() : TipoIntegracao.ARRECADACAO.name());
        List<ItemIntegracaoTributarioContabil> integracoes = q.getResultList();
        List<ItemInconsistenciaIntegracaoTribCont> retorno = Lists.newArrayList();
        Map<UnidadeOrganizacional, Entidade> mapaEntidade = Maps.newHashMap();
        for (ItemIntegracaoTributarioContabil integracao : integracoes) {
            ItemInconsistenciaIntegracaoTribCont item = new ItemInconsistenciaIntegracaoTribCont();
            item.setDataPagamento(integracao.getDataPagamento());
            item.setDataCredito(integracao.getDataCredito());
            item.setContaReceita(integracao.getContaReceita());
            item.setOperacaoReceitaRealizada(integracao.getOperacaoReceitaRealizada());
            item.setFonteDeRecursos(integracao.getFonteDeRecursos());
            item.setOperacaoIntegracao(integracao.getOperacaoIntegracaoTribCont());
            item.setTipo(loteBaixa.isEstornado() ? TipoIntegracao.ESTORNO_ARRECADACAO : TipoIntegracao.ARRECADACAO);
            item.getIntegracoes().add(integracao);
            if (!mapaEntidade.containsKey(integracao.getOrcamentaria())) {
                item.setEntidade(buscarEntidadeDaUnidadeOrganizacional(integracao.getOrcamentaria()));
                if (item.getEntidade() != null) {
                    mapaEntidade.put(integracao.getOrcamentaria(), item.getEntidade());
                }
            } else {
                item.setEntidade(mapaEntidade.get(integracao.getOrcamentaria()));
            }
            if (retorno.contains(item)) {
                retorno.get(retorno.indexOf(item)).setValorTributario(retorno.get(retorno.indexOf(item)).getValorTributario().add(integracao.getValor()));
            } else {
                item.setValorTributario(integracao.getValor());
                retorno.add(item);
            }
        }
        return retorno;
    }

    private Entidade buscarEntidadeDaUnidadeOrganizacional(UnidadeOrganizacional orcamentaria) {
        Query q = em.createNativeQuery("select e.* from entidade e where e.unidadeorganizacionalorc_id = :idOrcamentaria", Entidade.class);
        q.setParameter("idOrcamentaria", orcamentaria.getId());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (Entidade) resultList.get(0);
        }
        return null;
    }
}

