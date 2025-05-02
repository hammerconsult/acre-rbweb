package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoVencimentoRevisaoIPTU;
import br.com.webpublico.exception.ConflitoDeDividasDentroDoExercicio;
import br.com.webpublico.negocios.tributario.auxiliares.AssistenteEfetivacaoIPTU;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Stateless
public class GeraValorDividaRevisaoIPTU extends ValorDividaFacade {

    private static final Logger log = LoggerFactory.getLogger(GeraValorDividaRevisaoIPTU.class);

    @EJB
    private FacadeAutoGerenciado facadeAutoGerenciado;
    @EJB
    private ParametroParcelamentoFacade parametroParcelamentoFacade;
    private Map<OpcaoPagamento, Boolean> adimplente;
    private Map<OpcaoPagamento, List<ConsultaParcela>> mapaConsultas;
    private Divida dividaIPTU = null;
    private List<Long> idsDividasIptu = null;


    public GeraValorDividaRevisaoIPTU() {
        super(true);
    }

    @Override
    public List<OpcaoPagamentoDivida> validaOpcoesPagamento(Divida divida, Date dataBase) throws IllegalArgumentException {
        return super.validaOpcoesPagamento(divida, dataBase);
    }

    public List<ValorDivida> geraDebito(AssistenteEfetivacaoIPTU assistente,
                                        List<CalculoIPTU> calculos,
                                        Exercicio exercicio,
                                        TipoVencimentoRevisaoIPTU tipoVencimentoRevisaoIPTU) {
        List<ValorDivida> valorDividas = Lists.newArrayList();
        try {
            List<OpcaoPagamentoDivida> opcoesPagamento =
                validaOpcoesPagamento(configuracaoTributarioFacade.retornaUltimo().getDividaIPTU(),
                    getDataLancamento(exercicio));
            for (CalculoIPTU calculo : calculos) {
                try {
                    valorDividas = geraIPTU(valorDividas, calculo, opcoesPagamento, tipoVencimentoRevisaoIPTU);
                } catch (Exception e) {
                    log.error("Erro ao gerar débito na revisão de iptu. {}", e.getMessage());
                    log.debug("Detalhes do erro ao gerar débito na revisão de iptu.", e);
                } finally {
                    assistente.contarLinha();
                }
            }
        } finally {
            assistente.finalizar();
        }
        return valorDividas;
    }

    private Date getDataLancamento(Exercicio exercicio) {
        Calendar cal = Calendar.getInstance();
        if (exercicio != null) {
            cal.set(Calendar.YEAR, exercicio.getAno());
        }
        return cal.getTime();
    }

    private List<ValorDivida> geraIPTU(List<ValorDivida> valorDividas,
                                       CalculoIPTU calculoIPTU,
                                       List<OpcaoPagamentoDivida> opcoesPagamento,
                                       TipoVencimentoRevisaoIPTU tipoVencimentoRevisaoIPTU) throws ConflitoDeDividasDentroDoExercicio {
        if (calculoIPTU.getIsento() || calculoIPTU.getValorEfetivo().compareTo(BigDecimal.ZERO) <= 0) {
            return valorDividas;
        }
        if (calculoIPTU.getConstrucao() != null && calculoIPTU.getConstrucao().getEnglobado() != null) {
            return valorDividas;
        }
        return geraValorDividaIPTU(valorDividas, calculoIPTU, opcoesPagamento, tipoVencimentoRevisaoIPTU);
    }

    private List<ValorDivida> geraValorDividaIPTU(List<ValorDivida> valorDividas,
                                                  CalculoIPTU calculoIPTU,
                                                  List<OpcaoPagamentoDivida> opcoesPagamento,
                                                  TipoVencimentoRevisaoIPTU tipoVencimentoRevisaoIPTU) {
        try {
            BigDecimal valorEnglobado = calculaValorEnglobadoIPTU(calculoIPTU.getConstrucao());
            ValorDivida valorDivida = inicializaValorDivida(calculoIPTU.getValorEfetivo().add(valorEnglobado), calculoIPTU, calculoIPTU.getProcessoCalculoIPTU().getDivida(), calculoIPTU.getProcessoCalculoIPTU().getExercicio());
            lancaItem(valorDivida);
            if (!valorDivida.getItemValorDividas().isEmpty()) {
                lancaOpcaoPagamento(valorDivida, opcoesPagamento, tipoVencimentoRevisaoIPTU);
            }
            valorDividas.add(valorDivida);
        } catch (Exception ex) {
            log.error("Erro ao gerar ValorDivida para revisão de iptu. {}", ex.getMessage());
            log.debug("Detalhes do erro ao gerar ValorDivida para revisão de iptu.", ex);
        }
        return valorDividas;
    }

    private BigDecimal calculaValorEnglobadoIPTU(Construcao construcao) {
        Query qValorEnglobado = em.createQuery("select coalesce(sum(c.valorEfetivo), 0) FROM CalculoIPTU c where c.construcao.englobado = :construcao");
        qValorEnglobado.setParameter("construcao", construcao);
        qValorEnglobado.setMaxResults(1);
        return (BigDecimal) qValorEnglobado.getSingleResult();
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        for (ItemCalculoIPTU itemCalculo : ((CalculoIPTU) valorDivida.getCalculo()).getItensCalculo()) {
            if (itemCalculo.getEvento().getEventoCalculo().getTributo() != null || (!itemCalculo.getIsento() && itemCalculo.getEvento().getEventoCalculo().getDescontoSobre() == null)) {
                ItemValorDivida itemValorDivida = new ItemValorDivida();
                itemValorDivida.setTributo(itemCalculo.getEvento().getEventoCalculo().getTributo());
                itemValorDivida.setValor(itemCalculo.getValorEfetivo().setScale(2, RoundingMode.HALF_EVEN));
                itemValorDivida.setValorDivida(valorDivida);
                itemValorDivida.setIsento(itemCalculo.getIsento());
                valorDivida.getItemValorDividas().add(itemValorDivida);
            }
        }
    }

    private Divida getDividaIptu() {
        if (dividaIPTU == null) {
            dividaIPTU = configuracaoTributarioFacade.retornaUltimo().getDividaIPTU();
        }
        return dividaIPTU;
    }

    private List<Long> getIdsDividasIptuDeParcelamento() {
        return parametroParcelamentoFacade.buscarIdsDividasParcelamento(getDividaIptu());
    }

    protected void lancaOpcaoPagamento(ValorDivida valorDivida,
                                       List<OpcaoPagamentoDivida> opcoesPagamento,
                                       TipoVencimentoRevisaoIPTU tipoVencimentoRevisaoIPTU) throws Exception {
        mapaConsultas = Maps.newHashMap();
        this.adimplente = null;
        for (OpcaoPagamentoDivida opcaoPagamentoDivida : opcoesPagamento) {
            consideraDesconto = opcaoPagamentoDivida.getOpcaoPagamento().getDataVerificacaoDebito() != null;

            mapaConsultas.put(opcaoPagamentoDivida.getOpcaoPagamento(), new ArrayList<>());

            mapaConsultas.get(opcaoPagamentoDivida.getOpcaoPagamento()).add(criarConsultaParcela(opcaoPagamentoDivida.getOpcaoPagamento().getDataVerificacaoDebito(),
                (CalculoIPTU) valorDivida.getCalculo(), getIdsDividasIptuDeParcelamento()));

            ConsultaParcela consultaParcela = criarConsultaParcela(opcaoPagamentoDivida.getOpcaoPagamento().getDataVerificacaoDebito(),
                (CalculoIPTU) valorDivida.getCalculo(), Lists.newArrayList(getDividaIptu().getId()));
            consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MENOR,
                valorDivida.getCalculo().getProcessoCalculo().getExercicio().getAno());

            mapaConsultas.get(opcaoPagamentoDivida.getOpcaoPagamento()).add(consultaParcela);

            geraParcelas(valorDivida, opcaoPagamentoDivida, tipoVencimentoRevisaoIPTU);
        }
    }

    private void geraParcelas(ValorDivida valorDivida,
                              OpcaoPagamentoDivida opcaoPagamentoDivida,
                              TipoVencimentoRevisaoIPTU tipoVencimentoRevisaoIPTU) {
        List<Parcela> parcelas = calculaPercentualParcelas(valorDivida,
            opcaoPagamentoDivida.getOpcaoPagamento(), tipoVencimentoRevisaoIPTU);
        if (!parcelas.isEmpty()) {
            parcelasOpcaoPagamento.put(opcaoPagamentoDivida.getOpcaoPagamento(), new ArrayList<ParcelaValorDivida>());
            BigDecimal valorTotalBruto = valorDivida.getValor().setScale(2, MODO_ARREDONDAMENTO);
            OpcaoPagamento opcaoPagamento = em.find(OpcaoPagamento.class, opcaoPagamentoDivida.getOpcaoPagamento().getId());
            BigDecimal valorParcelaBruto;
            BigDecimal valorLancadoBruto = BigDecimal.ZERO;
            for (Parcela p : parcelas) {
                valorParcelaBruto = valorTotalBruto.multiply(p.getPercentualValorTotal()).divide(CEM, NUMERO_CASAS_DECIMAIS, RoundingMode.DOWN);
                ParcelaValorDivida parcelaValorDivida = new ParcelaValorDivida();
                parcelaValorDivida.setDividaAtiva(Boolean.FALSE);
                parcelaValorDivida.setItensParcelaValorDivida(new ArrayList<ItemParcelaValorDivida>());
                parcelaValorDivida.setOpcaoPagamento(opcaoPagamento);
                parcelaValorDivida.setValorDivida(valorDivida);
                parcelaValorDivida.setValor(valorParcelaBruto);
                parcelaValorDivida.setVencimento(p.getVencimento());
                parcelaValorDivida.setPercentualValorTotal(p.getPercentualValorTotal());
                parcelaValorDivida.setSequenciaParcela(p.getSequenciaParcela());
                parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO, parcelaValorDivida, parcelaValorDivida.getValor()));
                parcelasOpcaoPagamento.get(opcaoPagamento).add(parcelaValorDivida);
                valorDivida.getParcelaValorDividas().add(parcelaValorDivida);
                valorLancadoBruto = valorLancadoBruto.add(parcelaValorDivida.getValor());
            }
            processaDiferencaValorDivida(valorLancadoBruto, valorTotalBruto, opcaoPagamento);
            lancaItensParcela(valorDivida, opcaoPagamentoDivida);
        }
    }

    public List<Parcela> calculaPercentualParcelas(ValorDivida valorDivida,
                                                   OpcaoPagamento opcaoPagamento,
                                                   TipoVencimentoRevisaoIPTU tipoVencimentoRevisaoIPTU) {

        opcaoPagamento = em.find(OpcaoPagamento.class, opcaoPagamento.getId());
        List<Parcela> retorno = new ArrayList<>();
        BigDecimal percentualMinimo = calculaPercentualMinimo(valorDivida, opcaoPagamento);
        BigDecimal percentualJaLancado = BigDecimal.ZERO;
        int i = 0;
        List<Parcela> parcelas = getParcelasaDaOpcaoDePagamento(opcaoPagamento);

        if (tipoVencimentoRevisaoIPTU.equals(TipoVencimentoRevisaoIPTU.VENCIMENTO_ATUAL)) {
            parcelas = recalculaPercentuaisParaNaoTerVencimentoAlemDoAnoAtual(parcelas, valorDivida);
        }

        while ((percentualJaLancado.compareTo(CEM) < 0) && (i < parcelas.size())) {
            Parcela parcela = parcelas.get(i++);

            Parcela novaParcela;
            if (parcela instanceof ParcelaFixa) {
                novaParcela = new ParcelaFixa();
            } else {
                novaParcela = new ParcelaFixaPeriodica();
                ((ParcelaFixaPeriodica) novaParcela).setDiaVencimento(((ParcelaFixaPeriodica) parcela).getDiaVencimento());
            }
            novaParcela.setMensagem(parcela.getMensagem());
            novaParcela.setSequenciaParcela(parcela.getSequenciaParcela());
            novaParcela.setVencimento(parcela.getVencimento());
            novaParcela.setPercentualValorTotal(parcela.getPercentualValorTotal());
            if (novaParcela.getPercentualValorTotal().compareTo(percentualMinimo) < 0) {
                novaParcela.setPercentualValorTotal(percentualMinimo);
            }
            BigDecimal totalTemporario = percentualJaLancado.add(novaParcela.getPercentualValorTotal());
            if (totalTemporario.compareTo(CEM) > 0) {
                novaParcela.setPercentualValorTotal(novaParcela.getPercentualValorTotal().subtract(totalTemporario.subtract(CEM)));
            }
            percentualJaLancado = percentualJaLancado.add(novaParcela.getPercentualValorTotal());
            retorno.add(novaParcela);
        }
        BigDecimal percentualUltimaParcela;
        if (retorno.isEmpty()) {
            percentualUltimaParcela = BigDecimal.ZERO;
        } else {
            percentualUltimaParcela = retorno.get(retorno.size() - 1).getPercentualValorTotal();
        }
        if (percentualUltimaParcela.compareTo(percentualMinimo) < 0) {
            retorno.remove(retorno.size() - 1);
            if (!retorno.isEmpty()) {
                BigDecimal percentualADistribuir = percentualUltimaParcela.divide(new BigDecimal(retorno.size()), 4, MODO_ARREDONDAMENTO);
                BigDecimal novoValorJaLancado = BigDecimal.ZERO;
                for (Parcela parcela : retorno) {
                    parcela.setPercentualValorTotal(parcela.getPercentualValorTotal().add(percentualADistribuir));
                    novoValorJaLancado = novoValorJaLancado.add(parcela.getPercentualValorTotal());
                }
                BigDecimal diferenca = CEM.subtract(novoValorJaLancado);
                if (diferenca.compareTo(CEM) != 0) {
                    retorno.get(0).setPercentualValorTotal(retorno.get(0).getPercentualValorTotal().add(diferenca));
                }
            }
        }
        return retorno;
    }

    private List<Parcela> recalculaPercentuaisParaNaoTerVencimentoAlemDoAnoAtual(List<Parcela> parcelas, ValorDivida valorDivida) {
        List<Parcela> novasParcelas = Lists.newArrayList();
        Calendar calendar = Calendar.getInstance();
        Parcela novaParcela;
        for (Parcela parcela : parcelas) {
            if (parcela instanceof ParcelaFixa) {
                novaParcela = new ParcelaFixa();
            } else {
                novaParcela = new ParcelaFixaPeriodica();
                ((ParcelaFixaPeriodica) novaParcela).setDiaVencimento(((ParcelaFixaPeriodica) parcela).getDiaVencimento());
            }
            novaParcela.setMensagem(parcela.getMensagem());
            novaParcela.setSequenciaParcela(parcela.getSequenciaParcela());
            novaParcela.setPercentualValorTotal(parcela.getPercentualValorTotal());

            if (valorDivida.getExercicio().getAno() >= calendar.get(Calendar.YEAR)) {
                novaParcela.setVencimento(calendar.getTime());
                int atual = calendar.get(Calendar.YEAR);
                calendar.add(Calendar.MONTH, 1);
                calendar = DataUtil.ultimoDiaMes(calendar.getTime());
                calendar = DataUtil.ultimoDiaUtil(calendar, feriadoFacade);
                novasParcelas.add(novaParcela);
                if (atual < calendar.get(Calendar.YEAR)) {
                    break;
                }
            } else if (Integer.parseInt(parcela.getSequenciaParcela()) >= 2) {
                calendar.add(Calendar.MONTH, 1);
                calendar = DataUtil.ultimoDiaMes(calendar.getTime());
                calendar = DataUtil.ultimoDiaUtil(calendar, feriadoFacade);
                novaParcela.setVencimento(calendar.getTime());
                novasParcelas.add(novaParcela);
            } else {
                calendar = DataUtil.ultimoDiaMes(calendar.getTime());
                calendar = DataUtil.ultimoDiaUtil(calendar, feriadoFacade);
                novaParcela.setVencimento(calendar.getTime());
                novasParcelas.add(novaParcela);
            }

        }

        BigDecimal percentualLancado = BigDecimal.ZERO;
        for (Parcela novasParcela : novasParcelas) {
            percentualLancado = (percentualLancado.add(novasParcela.getPercentualValorTotal()));
        }
        if (percentualLancado.compareTo(CEM) < 0) {
            BigDecimal diferenca = CEM.subtract(percentualLancado);
            diferenca = diferenca.divide(new BigDecimal(novasParcelas.size()), 4, RoundingMode.HALF_UP);
            for (Parcela parcela : novasParcelas) {
                parcela.setPercentualValorTotal(parcela.getPercentualValorTotal().add(diferenca));
            }
        }
        return novasParcelas;
    }

    public List<Parcela> getParcelasaDaOpcaoDePagamento(OpcaoPagamento op) {
        return em.createQuery("select p from Parcela p where p.opcaoPagamento = :op").setParameter("op", op).getResultList();
    }

    private BigDecimal calculaPercentualMinimo(ValorDivida valorDivida, OpcaoPagamento opcaoPagamento) {
        BigDecimal retorno = BigDecimal.ZERO;
        try {
            BigDecimal valorMinimo = opcaoPagamento.getValorMinimoParcela();
            if (valorMinimo.compareTo(valorDivida.getValor()) > 0) {
                valorMinimo = valorDivida.getValor();
            }
            retorno = (valorMinimo.multiply(CEM)).divide(valorDivida.getValor(), NUMERO_CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
        } catch (Exception e) {
            //NADA
        }
        return retorno;
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAdimplente(OpcaoPagamento opcaoPagamento) {
        if (adimplente != null && adimplente.containsKey(opcaoPagamento)) {
            return adimplente.get(opcaoPagamento);
        }
        if (adimplente == null) {
            adimplente = Maps.newHashMap();
        }
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        for (ConsultaParcela consulta : mapaConsultas.get(opcaoPagamento)) {
            if (parcelas.isEmpty()) {
                parcelas.addAll(consulta.executaConsulta().getResultados());
            }
        }
        adimplente.put(opcaoPagamento, parcelas.isEmpty());
        return adimplente.get(opcaoPagamento);
    }

    private ConsultaParcela criarConsultaParcela(Date dataVerificacaoDebito, CalculoIPTU calculo, List<Long> idsDividasIptu) {
        ConsultaParcela consultaParcela = dataVerificacaoDebito != null ? new ConsultaParcela(dataVerificacaoDebito) : new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, calculo.getCadastroImobiliario().getId());
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consultaParcela.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IN, idsDividasIptu);
        return consultaParcela;
    }


}
