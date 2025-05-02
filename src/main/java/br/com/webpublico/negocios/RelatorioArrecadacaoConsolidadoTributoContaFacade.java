package br.com.webpublico.negocios;

import br.com.webpublico.controle.RelatorioArrecadacaoConsolidadoTributoContaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.TipoAgrupamentoMapaConsolidado;
import br.com.webpublico.enums.tributario.arrecadacao.TipoMapaArrecadacao;
import br.com.webpublico.exception.ContaReceitaException;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 27/03/15
 * Time: 14:46
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class RelatorioArrecadacaoConsolidadoTributoContaFacade {

    public static final BigDecimal MENOS_UM_CENTAVO = new BigDecimal("-0.01");
    public static final BigDecimal UM_CENTAVO = new BigDecimal("0.01");
    private static final Logger logger = LoggerFactory.getLogger(RelatorioArrecadacaoConsolidadoTributoContaFacade.class);
    @EJB
    private EnquadramentoTributoExercicioFacade enquadramentoTributoExercicioFacade;
    @EJB
    private LoteBaixaFacade loteBaixaFacade;

    private Map<TipoMapaArrecadacao, Map<AgrupamentoMapaConsolidadoTributoConta, BigDecimal>> montarMapasArrecadacao(AssistenteBarraProgresso assistenteBarraProgresso,
                                                                                                                     Map<Long, List<VOItemLoteBaixa>> mapaItensLoteBaixaPorLote,
                                                                                                                     TipoAgrupamentoMapaConsolidado tipoAgrupamentoMapaConsolidado) throws ContaReceitaException {
        Map<TipoMapaArrecadacao, Map<AgrupamentoMapaConsolidadoTributoConta, BigDecimal>> toReturn = Maps.newHashMap();
        Map<AgrupamentoMapaConsolidadoTributoConta, BigDecimal> mapaArrecadacao = Maps.newHashMap();
        Map<AgrupamentoMapaConsolidadoTributoConta, BigDecimal> mapaAcrescimo = Maps.newHashMap();
        Map<AgrupamentoMapaConsolidadoTributoConta, BigDecimal> mapaDesconto = Maps.newHashMap();
        if (mapaItensLoteBaixaPorLote.size() > 0) {
            for (Long idLote : mapaItensLoteBaixaPorLote.keySet()) {
                List<VOItemIntegracaoTribCont> itensIntegracao = loteBaixaFacade.buscarItensIntegracaoGerados(idLote);
                LoteBaixa loteBaixa = loteBaixaFacade.recuperarPadrao(idLote);

                List<VOItemArrecadadoPorTributo> itensArrecadacao = loteBaixaFacade.buscarItensArrecadadoPorTributo(idLote,
                    Lists.newArrayList(Tributo.TipoTributo.IMPOSTO.name(), Tributo.TipoTributo.TAXA.name()));
                loteBaixaFacade.carregarValoresArrecadadosDeAcordoComIntegracao(itensIntegracao, itensArrecadacao, true);
                for (VOItemArrecadadoPorTributo item : itensArrecadacao) {
                    AgrupamentoMapaConsolidadoTributoConta chave = definirTipoAgrupamento(tipoAgrupamentoMapaConsolidado,
                        item.getContaReceita(), loteBaixa.getBanco(), item.getDataPagamento(),
                        loteBaixaFacade.getTributoFacade().recuperar(item.getIdTributo()),
                        loteBaixaFacade.getExercicioFacade().recuperarExercicioPeloAno(item.getExercicio()));

                    if (mapaArrecadacao.get(chave) == null) {
                        mapaArrecadacao.put(chave, item.getValor());
                    } else {
                        mapaArrecadacao.put(chave, mapaArrecadacao.get(chave).add(item.getValor()));
                    }
                }

                List<VOItemArrecadadoPorTributo> itensAcrescimo = loteBaixaFacade.buscarItensArrecadadoPorTributo(idLote, Lists.newArrayList(Tributo.TipoTributo.JUROS.name(), Tributo.TipoTributo.MULTA.name(), Tributo.TipoTributo.CORRECAO.name(), Tributo.TipoTributo.HONORARIOS.name()));
                loteBaixaFacade.carregarValoresArrecadadosDeAcordoComIntegracao(itensIntegracao, itensAcrescimo, true);
                for (VOItemArrecadadoPorTributo item : itensAcrescimo) {
                    AgrupamentoMapaConsolidadoTributoConta chave = definirTipoAgrupamento(tipoAgrupamentoMapaConsolidado,
                        item.getContaReceita(), loteBaixa.getBanco(), item.getDataPagamento(),
                        loteBaixaFacade.getTributoFacade().recuperar(item.getIdTributo()),
                        loteBaixaFacade.getExercicioFacade().recuperarExercicioPeloAno(item.getExercicio()));

                    if (mapaAcrescimo.get(chave) == null) {
                        mapaAcrescimo.put(chave, item.getValor());
                    } else {
                        mapaAcrescimo.put(chave, mapaAcrescimo.get(chave).add(item.getValor()));
                    }
                }

                List<VOItemArrecadadoPorTributo> itensDesconto = loteBaixaFacade.buscarItensArrecadadoPorTributo(idLote, null);
                loteBaixaFacade.carregarValoresArrecadadosDeAcordoComIntegracao(itensIntegracao, itensDesconto, false);
                for (VOItemArrecadadoPorTributo item : itensDesconto) {
                    AgrupamentoMapaConsolidadoTributoConta chave = definirTipoAgrupamento(tipoAgrupamentoMapaConsolidado,
                        item.getContaReceita(), loteBaixa.getBanco(), item.getDataPagamento(),
                        loteBaixaFacade.getTributoFacade().recuperar(item.getIdTributo()),
                        loteBaixaFacade.getExercicioFacade().recuperarExercicioPeloAno(item.getExercicio()));

                    if (mapaDesconto.get(chave) == null) {
                        mapaDesconto.put(chave, item.getValor());
                    } else {
                        mapaDesconto.put(chave, mapaDesconto.get(chave).add(item.getValor()));
                    }
                }
                assistenteBarraProgresso.conta();
            }
        }
        somarDescontoNasContasAcrecimosArrecadacao(mapaArrecadacao, mapaAcrescimo, mapaDesconto);

        toReturn.put(TipoMapaArrecadacao.ARRECADACAO, mapaArrecadacao);
        toReturn.put(TipoMapaArrecadacao.ACRESCIMO, mapaAcrescimo);
        toReturn.put(TipoMapaArrecadacao.DESCONTO, mapaDesconto);
        return toReturn;
    }

    private void somarDescontoNasContasAcrecimosArrecadacao(Map<AgrupamentoMapaConsolidadoTributoConta, BigDecimal> mapaArrecadacao, Map<AgrupamentoMapaConsolidadoTributoConta, BigDecimal> mapaAcrescimo, Map<AgrupamentoMapaConsolidadoTributoConta, BigDecimal> mapaDesconto) {
        for (AgrupamentoMapaConsolidadoTributoConta desconto : mapaDesconto.keySet()) {
            for (AgrupamentoMapaConsolidadoTributoConta acrescimo : mapaAcrescimo.keySet()) {
                if (acrescimo.getTributo().equals(desconto.getTributo())) {
                    BigDecimal valor = mapaAcrescimo.get(acrescimo).add(mapaDesconto.get(desconto));
                    mapaAcrescimo.put(acrescimo, valor);
                    break;
                }
            }

            for (AgrupamentoMapaConsolidadoTributoConta arrecadacao : mapaArrecadacao.keySet()) {
                if (arrecadacao.getTributo().equals(desconto.getTributo())) {
                    BigDecimal valor = mapaArrecadacao.get(arrecadacao).add(mapaDesconto.get(desconto));
                    mapaArrecadacao.put(arrecadacao, valor);
                    break;
                }
            }
        }
    }

    private void montarMapa(Map<AgrupamentoMapaConsolidadoTributoConta, BigDecimal> mapaArrecadacao,
                            Map<AgrupamentoMapaConsolidadoTributoConta, BigDecimal> mapaAcrescimo,
                            Map<AgrupamentoMapaConsolidadoTributoConta, BigDecimal> mapaDesconto,
                            VOItemLoteBaixa item,
                            TipoAgrupamentoMapaConsolidado tipoAgrupamento,
                            Map<MapaDeContas, ContaReceita> contasDeReceita) {

        AgrupamentoMapaConsolidadoTributoConta chave;
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
                BigDecimal totalItem = BigDecimal.ZERO;
                BigDecimal valorPagoDoItem = item.getValorPago();
                if (item.getTotal().compareTo(BigDecimal.ZERO) > 0) {
                    valorPagoDoItem = itemDam.getValorTotal().multiply(item.getValorPago()).divide(item.getTotal(), 8, RoundingMode.HALF_UP);
                }

                for (VOTributoDam tributoDAM : tributosDam) {
                    if (tributoDAM.getTipoTributo().isAcrescimosSemHonorarios()
                        && item.getTotalAcrescimosDamSemHonorarios().compareTo(BigDecimal.ZERO) > 0) {

                        BigDecimal valorAcrescimos = tributoDAM.getValorComDesconto();
                        if (valorPagoDoItem.compareTo(item.getValorPago()) != 0) {
                            BigDecimal proporcaoAcrescimos = tributoDAM.getValorComDesconto().compareTo(BigDecimal.ZERO) != 0 ? tributoDAM.getValorComDesconto().divide(totalDam, 8, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                            valorAcrescimos = (proporcaoAcrescimos.multiply(valorPagoDoItem));
                        }
                        valorAcrescimos = valorAcrescimos.setScale(2, RoundingMode.HALF_UP);

                        if (valorAcrescimos.compareTo(BigDecimal.ZERO) > 0 || tributoDAM.getValorOriginal().compareTo(BigDecimal.ZERO) > 0) {
                            chave = definirTipoAgrupamento(
                                tipoAgrupamento,
                                contasDeReceita,
                                TipoContaReceitaAgrupamento.ACRESCIMOS,
                                new Banco(item.getIdBanco(), item.getBanco()),
                                item.getDataPagamento(),
                                itemDam,
                                new Tributo(tributoDAM.getIdTributo(), tributoDAM.getCodigoTributo(), tributoDAM.getDescricaoTributo(), tributoDAM.getTipoTributo())
                            );
                            totalItem = totalItem.add(valorAcrescimos);

                            if (mapaAcrescimo.get(chave) == null) {
                                mapaAcrescimo.put(chave, valorAcrescimos);
                            } else {
                                mapaAcrescimo.put(chave, mapaAcrescimo.get(chave).add(valorAcrescimos));
                            }
                        }
                    }

                    if (tributoDAM.getTipoTributo().isHonorarios()
                        && item.getHonorariosDam().compareTo(BigDecimal.ZERO) > 0) {

                        BigDecimal valorHonorarios = tributoDAM.getValorComDesconto();
                        if (valorPagoDoItem.compareTo(item.getValorPago()) != 0) {
                            BigDecimal proporcaoHonorarios = tributoDAM.getValorComDesconto().compareTo(BigDecimal.ZERO) != 0 ? tributoDAM.getValorComDesconto().divide(totalDam, 8, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                            valorHonorarios = (proporcaoHonorarios.multiply(valorPagoDoItem));
                        }
                        valorHonorarios = valorHonorarios.setScale(2, RoundingMode.HALF_UP);

                        if (valorHonorarios.compareTo(BigDecimal.ZERO) > 0 || tributoDAM.getValorOriginal().compareTo(BigDecimal.ZERO) > 0) {
                            chave = definirTipoAgrupamento(
                                tipoAgrupamento,
                                contasDeReceita,
                                TipoContaReceitaAgrupamento.HONORARIOS,
                                new Banco(item.getIdBanco(), item.getBanco()),
                                item.getDataPagamento(),
                                itemDam,
                                new Tributo(tributoDAM.getIdTributo(), tributoDAM.getCodigoTributo(), tributoDAM.getDescricaoTributo(), tributoDAM.getTipoTributo())
                            );
                            totalItem = totalItem.add(valorHonorarios);

                            if (mapaAcrescimo.get(chave) == null) {
                                mapaAcrescimo.put(chave, valorHonorarios);
                            } else {
                                mapaAcrescimo.put(chave, mapaAcrescimo.get(chave).add(valorHonorarios));
                            }
                        }
                    }

                    if (tributoDAM.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal valorDesconto = tributoDAM.getDesconto();
                        valorDesconto = valorDesconto.setScale(2, RoundingMode.HALF_UP);

                        chave = definirTipoAgrupamento(
                            tipoAgrupamento,
                            contasDeReceita,
                            TipoContaReceitaAgrupamento.DESCONTO,
                            new Banco(item.getIdBanco(), item.getBanco()),
                            item.getDataPagamento(),
                            itemDam,
                            new Tributo(tributoDAM.getIdTributo(), tributoDAM.getCodigoTributo(), tributoDAM.getDescricaoTributo(), tributoDAM.getTipoTributo())
                        );

                        if (mapaDesconto.get(chave) == null) {
                            mapaDesconto.put(chave, valorDesconto);
                        } else {
                            mapaDesconto.put(chave, mapaDesconto.get(chave).add(valorDesconto));
                        }
                    }

                    if (tributoDAM.getTipoTributo().isImpostoTaxa()) {

                        BigDecimal valorArrecadacao = tributoDAM.getValorComDesconto();
                        if (valorPagoDoItem.compareTo(item.getValorPago()) != 0) {
                            BigDecimal proporcaoArrecadacao = tributoDAM.getValorComDesconto().compareTo(BigDecimal.ZERO) != 0 ? tributoDAM.getValorComDesconto().divide(totalDam, 8, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                            valorArrecadacao = (proporcaoArrecadacao.multiply(valorPagoDoItem));
                        }
                        valorArrecadacao = valorArrecadacao.setScale(2, RoundingMode.HALF_UP);

                        if (valorArrecadacao.compareTo(BigDecimal.ZERO) > 0 || tributoDAM.getValorOriginal().compareTo(BigDecimal.ZERO) > 0) {
                            chave = definirTipoAgrupamento(
                                tipoAgrupamento,
                                contasDeReceita,
                                TipoContaReceitaAgrupamento.EXERCICIO,
                                new Banco(item.getIdBanco(), item.getBanco()),
                                item.getDataPagamento(),
                                itemDam,
                                new Tributo(tributoDAM.getIdTributo(), tributoDAM.getCodigoTributo(), tributoDAM.getDescricaoTributo(), tributoDAM.getTipoTributo())
                            );
                            totalItem = totalItem.add(valorArrecadacao);

                            if (mapaArrecadacao.get(chave) == null) {
                                mapaArrecadacao.put(chave, valorArrecadacao);
                            } else {
                                mapaArrecadacao.put(chave, mapaArrecadacao.get(chave).add(valorArrecadacao));
                            }
                        }
                    }
                }
                BigDecimal diferenca = totalItem.subtract(valorPagoDoItem);
                if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
                    logger.debug("No lote: " + item.getCodigoLote() + " diferença: " + Util.formataValor(diferenca));

                    arredondarDiferencaNaIntegracao(mapaArrecadacao, valorPagoDoItem, totalItem);
                }
            }
        }
    }

    private void arredondarDiferencaNaIntegracao(Map<AgrupamentoMapaConsolidadoTributoConta, BigDecimal> mapa, BigDecimal total, BigDecimal lancado) {
        if (total.compareTo(lancado) != 0 && lancado.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diferenca = total.subtract(lancado).setScale(2, RoundingMode.HALF_UP);

            while (diferenca.compareTo(BigDecimal.ZERO) != 0 && mapa.size() > 0) {
                BigDecimal valorProporcionalDeferenca = BigDecimal.ZERO;
                for (AgrupamentoMapaConsolidadoTributoConta item : mapa.keySet()) {
                    if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
                        valorProporcionalDeferenca = diferenca.compareTo(BigDecimal.ZERO) > 0 ? UM_CENTAVO : diferenca.compareTo(BigDecimal.ZERO) < 0 ? MENOS_UM_CENTAVO : BigDecimal.ZERO;
                        mapa.put(item, mapa.get(item).add(valorProporcionalDeferenca));
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

    public static enum TipoContaReceitaAgrupamento {
        EXERCICIO(ContaTributoReceita.TipoContaReceita.EXERCICIO),
        RENUNCIA(ContaTributoReceita.TipoContaReceita.RENUNCIA),
        ACRESCIMOS(ContaTributoReceita.TipoContaReceita.EXERCICIO),
        RESTITUICAO(ContaTributoReceita.TipoContaReceita.RESTITUICAO),
        DESCONTO(ContaTributoReceita.TipoContaReceita.DESCONTO),
        HONORARIOS(ContaTributoReceita.TipoContaReceita.EXERCICIO),
        DEDUCAO(ContaTributoReceita.TipoContaReceita.DEDUCAO);

        private ContaTributoReceita.TipoContaReceita tipoContaReceita;

        TipoContaReceitaAgrupamento(ContaTributoReceita.TipoContaReceita tipoContaReceita) {
            this.tipoContaReceita = tipoContaReceita;
        }

        public ContaTributoReceita.TipoContaReceita getTipoContaReceita() {
            return tipoContaReceita;
        }
    }

    private AgrupamentoMapaConsolidadoTributoConta definirTipoAgrupamento(
        TipoAgrupamentoMapaConsolidado tipoAgrupamento,
        ContaReceita contaReceita,
        Banco banco, Date dataPagamento, Tributo tributo, Exercicio exercicio) {

        AgrupamentoMapaConsolidadoTributoConta chave = null;
        if (tipoAgrupamento.equals(TipoAgrupamentoMapaConsolidado.TRIBUTO)) {
            chave = new AgrupamentoMapaConsolidadoTributoConta(null, null, tributo, null, exercicio);
        } else {
            if (tipoAgrupamento.equals(TipoAgrupamentoMapaConsolidado.CONTA_RECEITA)) {
                chave = new AgrupamentoMapaConsolidadoTributoConta(null, contaReceita, null, null, exercicio);

            } else if (tipoAgrupamento.equals(TipoAgrupamentoMapaConsolidado.BANCO_CONTA_RECEITA) ||
                tipoAgrupamento.equals(TipoAgrupamentoMapaConsolidado.BANCO_CONTA_RECEITA_EXERCICIO)) {
                if (tipoAgrupamento.equals(TipoAgrupamentoMapaConsolidado.BANCO_CONTA_RECEITA)) {
                    chave = new AgrupamentoMapaConsolidadoTributoConta(banco, contaReceita, null, null, exercicio);
                }

            } else if (tipoAgrupamento.equals(TipoAgrupamentoMapaConsolidado.BANCO_CONTA_RECEITA_DATA)) {
                chave = new AgrupamentoMapaConsolidadoTributoConta(banco, contaReceita, null, dataPagamento, exercicio);
            }
        }

        return chave;
    }

    private AgrupamentoMapaConsolidadoTributoConta definirTipoAgrupamento(
        TipoAgrupamentoMapaConsolidado tipoAgrupamento,
        Map<MapaDeContas, ContaReceita> contasDeReceita,
        TipoContaReceitaAgrupamento tipoContaReceita,
        Banco banco, Date dataPagamento, VOItemDam itemDam, Tributo tributo) {

        AgrupamentoMapaConsolidadoTributoConta chave = null;
        MapaDeContas mapaDeContas;
        ContaReceita contaReceita;

        if (tipoAgrupamento.equals(TipoAgrupamentoMapaConsolidado.TRIBUTO)) {
            chave = new AgrupamentoMapaConsolidadoTributoConta(null, null, tributo, null, null);
        } else {
            if (tipoAgrupamento.equals(TipoAgrupamentoMapaConsolidado.CONTA_RECEITA)) {

                mapaDeContas = new MapaDeContas(tributo.getId(), itemDam.getIdExercicio(),
                    tipoContaReceita, itemDam.getDividaAtiva());
                contaReceita = contasDeReceita.get(mapaDeContas);
                if (contaReceita == null) {
                    contaReceita = enquadramentoTributoExercicioFacade.buscarContasDoTributo(tributo.getId(),
                        dataPagamento,
                        tipoContaReceita.getTipoContaReceita(), itemDam.getDividaAtiva());
                    if (contaReceita == null) {
                        contaReceita = getContaReceitaTempNaoConfigurada();
                    }
                    contasDeReceita.put(mapaDeContas, contaReceita);
                }
                chave = new AgrupamentoMapaConsolidadoTributoConta(null, contaReceita, null, null, null);

            } else if (tipoAgrupamento.equals(TipoAgrupamentoMapaConsolidado.BANCO_CONTA_RECEITA) ||
                tipoAgrupamento.equals(TipoAgrupamentoMapaConsolidado.BANCO_CONTA_RECEITA_EXERCICIO)) {

                mapaDeContas = new MapaDeContas(tributo.getId(), itemDam.getIdExercicio(),
                    tipoContaReceita, itemDam.getDividaAtiva());
                contaReceita = contasDeReceita.get(mapaDeContas);
                if (contaReceita == null) {
                    contaReceita = enquadramentoTributoExercicioFacade.buscarContasDoTributo(tributo.getId(),
                        dataPagamento,
                        tipoContaReceita.getTipoContaReceita(), itemDam.getDividaAtiva());
                    if (contaReceita == null) {
                        contaReceita = getContaReceitaTempNaoConfigurada();
                    }
                    contasDeReceita.put(mapaDeContas, contaReceita);
                }

                if (tipoAgrupamento.equals(TipoAgrupamentoMapaConsolidado.BANCO_CONTA_RECEITA)) {
                    chave = new AgrupamentoMapaConsolidadoTributoConta(banco, contaReceita, null, null, null);
                }

            } else if (tipoAgrupamento.equals(TipoAgrupamentoMapaConsolidado.BANCO_CONTA_RECEITA_DATA)) {
                mapaDeContas = new MapaDeContas(tributo.getId(), itemDam.getIdExercicio(),
                    tipoContaReceita, itemDam.getDividaAtiva());
                contaReceita = contasDeReceita.get(mapaDeContas);
                if (contaReceita == null) {
                    contaReceita = enquadramentoTributoExercicioFacade.buscarContasDoTributo(tributo.getId(),
                        dataPagamento,
                        tipoContaReceita.getTipoContaReceita(), itemDam.getDividaAtiva());
                    if (contaReceita == null) {
                        contaReceita = getContaReceitaTempNaoConfigurada();
                    }
                    contasDeReceita.put(mapaDeContas, contaReceita);
                }
                chave = new AgrupamentoMapaConsolidadoTributoConta(banco, contaReceita, null, dataPagamento, null);
            }
        }

        return chave;
    }

    private ContaReceita getContaReceitaTempNaoConfigurada() {
        ContaReceita contaReceita = new ContaReceita();
        contaReceita.setCodigo("0");
        contaReceita.setDescricao("Conta de Receita não configurada.");
        return contaReceita;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    @Asynchronous
    public Future<RelatorioArrecadacaoConsolidadoTributoConta> montarRelatorio(AssistenteBarraProgresso assistenteBarraProgresso,
                                                                               Map<Long, List<VOItemLoteBaixa>> mapaItensLoteBaixaPorLote,
                                                                               RelatorioArrecadacaoConsolidadoTributoContaControlador.Filtro filtro) throws ContaReceitaException {
        RelatorioArrecadacaoConsolidadoTributoConta relatorioArrecadacaoConsolidadoTributoConta = new RelatorioArrecadacaoConsolidadoTributoConta();

        Map<TipoMapaArrecadacao, Map<AgrupamentoMapaConsolidadoTributoConta, BigDecimal>> mapasArrecadacaoPorTipo =
            montarMapasArrecadacao(assistenteBarraProgresso, mapaItensLoteBaixaPorLote, filtro.getTipoAgrupamento());

        for (AgrupamentoMapaConsolidadoTributoConta chave : mapasArrecadacaoPorTipo.get(TipoMapaArrecadacao.ARRECADACAO).keySet()) {
            relatorioArrecadacaoConsolidadoTributoConta.addArrecadacao(chave, mapasArrecadacaoPorTipo.get(TipoMapaArrecadacao.ARRECADACAO).get(chave));
        }
        Collections.sort(relatorioArrecadacaoConsolidadoTributoConta.getArrecadacoes());
        for (AgrupamentoMapaConsolidadoTributoConta chave : mapasArrecadacaoPorTipo.get(TipoMapaArrecadacao.ACRESCIMO).keySet()) {
            relatorioArrecadacaoConsolidadoTributoConta.addAcrescimo(chave, mapasArrecadacaoPorTipo.get(TipoMapaArrecadacao.ACRESCIMO).get(chave));
        }
        Collections.sort(relatorioArrecadacaoConsolidadoTributoConta.getAcrescimos());
        for (AgrupamentoMapaConsolidadoTributoConta chave : mapasArrecadacaoPorTipo.get(TipoMapaArrecadacao.DESCONTO).keySet()) {
            relatorioArrecadacaoConsolidadoTributoConta.addDesconto(chave, mapasArrecadacaoPorTipo.get(TipoMapaArrecadacao.DESCONTO).get(chave));
        }
        Collections.sort(relatorioArrecadacaoConsolidadoTributoConta.getDescontos());
        return new AsyncResult<>(relatorioArrecadacaoConsolidadoTributoConta);
    }

    private class MapaDeContas {
        public Long idTributo;
        public Long idExercicio;
        public TipoContaReceitaAgrupamento tipoContaReceita;
        public Boolean dividaAtiva;


        private MapaDeContas() {
        }

        private MapaDeContas(Long idTributo, Long idExercicio, TipoContaReceitaAgrupamento tipoContaReceita, Boolean dividaAtiva) {
            this.idTributo = idTributo;
            this.idExercicio = idExercicio;
            this.tipoContaReceita = tipoContaReceita;
            this.dividaAtiva = dividaAtiva;
        }

        private Long getIdTributo() {
            return idTributo;
        }

        private void setIdTributo(Long idTributo) {
            this.idTributo = idTributo;
        }

        private Long getIdExercicio() {
            return idExercicio;
        }

        private void setIdExercicio(Long idExercicio) {
            this.idExercicio = idExercicio;
        }

        private TipoContaReceitaAgrupamento getTipoContaReceita() {
            return tipoContaReceita;
        }

        private void setTipoContaReceita(TipoContaReceitaAgrupamento tipoContaReceita) {
            this.tipoContaReceita = tipoContaReceita;
        }

        private Boolean getDividaAtiva() {
            return dividaAtiva;
        }

        private void setDividaAtiva(Boolean dividaAtiva) {
            this.dividaAtiva = dividaAtiva;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MapaDeContas that = (MapaDeContas) o;

            if (dividaAtiva != null ? !dividaAtiva.equals(that.dividaAtiva) : that.dividaAtiva != null) return false;
            if (idExercicio != null ? !idExercicio.equals(that.idExercicio) : that.idExercicio != null) return false;
            if (idTributo != null ? !idTributo.equals(that.idTributo) : that.idTributo != null) return false;
            if (tipoContaReceita != that.tipoContaReceita) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = idTributo != null ? idTributo.hashCode() : 0;
            result = 31 * result + (idExercicio != null ? idExercicio.hashCode() : 0);
            result = 31 * result + (tipoContaReceita != null ? tipoContaReceita.hashCode() : 0);
            result = 31 * result + (dividaAtiva != null ? dividaAtiva.hashCode() : 0);
            return result;
        }
    }

}
