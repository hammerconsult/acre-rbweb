/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.administracaodepagamento.BloqueioFuncionalidade;
import br.com.webpublico.entidades.rh.administracaodepagamento.EventoFPTipoFolha;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.previdencia.ItemPrevidenciaComplementar;
import br.com.webpublico.entidadesauxiliares.EventoTotalItemFicha;
import br.com.webpublico.entidadesauxiliares.ReferenciaFPFuncao;
import br.com.webpublico.entidadesauxiliares.rh.ResumoFichaFinanceira;
import br.com.webpublico.entidadesauxiliares.rh.calculo.MemoriaCalculoRHDTO;
import br.com.webpublico.entidadesauxiliares.rh.calculo.TipoMemoriaCalculo;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.TipoAvisoPrevio;
import br.com.webpublico.enums.rh.TipoFuncaoGratificada;
import br.com.webpublico.enums.rh.TipoFuncionalidadeRH;
import br.com.webpublico.enums.rh.administracaopagamento.TipoFormulaEventoFP;
import br.com.webpublico.exception.FuncoesFolhaFacadeException;
import br.com.webpublico.interfaces.EntidadePagavelRH;
import br.com.webpublico.interfaces.FolhaCalculavel;
import br.com.webpublico.script.ItemErroScript;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.util.anotacoes.DescricaoMetodo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.YearMonth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import static br.com.webpublico.util.DataUtil.*;

/**
 * @author munif, everton, peixe
 */
public class FolhaDePagamentoNovoCalculador implements Serializable {

    public static final String CODIGO_REFERENCIAFP_SALARIO_MINIMO = "5";
    private static final String MESES_ANO = "12";
    private static final Logger logger = LoggerFactory.getLogger(FolhaDePagamentoNovoCalculador.class);
    public static List<ItemErroScript> itemErroScripts = new ArrayList<>();
    private static boolean ADICIONA_LOG = false;
    private final boolean LOGA = false;
    private final boolean LOG = false;
    private ScriptEngine engine;
    private Map<String, Object> contexto;
    private FuncoesFolhaFacade funcoesFolhaFacade;
    //@EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    private Map<EventoFP, BigDecimal> eventoValorFormula;
    private Map<EventoFP, TipoEventoFP> tipoEventoTipoEventoFP;
    private Map<EventoFP, BigDecimal> eventoValorIntegral;
    private Map<EventoFP, BigDecimal> eventoValorReferencia;
    private Map<EventoFP, BigDecimal> eventoValorBase;
    private Map<EventoFP, TipoEventoFP> eventoEstornoValor;

    private Map<EventoFP, Boolean> eventoValorRegra;
    private Map<String, BigDecimal> baseValor;
    private Map<String, BigDecimal> baseValorSemRetroativo;
    private Map<String, BigDecimal> baseValorIntegral;
    private Map<String, BigDecimal> baseMultVincValor;
    private Map<String, BigDecimal> baseMultVincValorIR;
    private Map<String, List<EventoFP>> irrfFicticioParaPensao;
    List<EventoTotalItemFicha> itemPagosNaFolhaNormal;
    private Map<String, Object> valorMetodo;
    private FolhaCalculavel folhaDePagamento;
    private Map<EventoFP, LancamentoFP> lancamentos;
    //private Integer ano;
    //private Integer mes;
    //private TipoFolhaDePagamento tipoFolhaDePagamento;
    private EntidadePagavelRH ep;
    private TipoCalculoFP tipoCalculo;
    private DetalheProcessamentoFolha detalheProcessamentoFolha;
    private EventoFP eventoCalculandoAgora;

    private LancamentoFP lancamentoFP;

    private Map<String, List<MemoriaCalculoRHDTO>> memoriaCalculoEventosBases;
    private FolhaDePagamentoNovoCalculador contextoTransient;
    private Map<String, List<LancamentoFP>> lancamentosReferenciaJaCalculados;

    public FolhaDePagamentoNovoCalculador(EntidadePagavelRH ep, FolhaCalculavel folhaDePagamento, TipoCalculoFP tipoCalculo, FuncoesFolhaFacade funcoesFolhaFacade, DetalheProcessamentoFolha detalhe, FolhaDePagamentoFacade folhaDePagamentoFacade) {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("javascript");
        engine.getBindings(ScriptContext.ENGINE_SCOPE).clear();
        this.eventoValorFormula = new HashMap<>();
        this.eventoValorReferencia = new HashMap<>();
        this.eventoValorBase = new HashMap<>();
        this.eventoValorRegra = new HashMap<>();
        this.eventoValorIntegral = new HashMap<>();
        this.baseValor = new HashMap<>();
        this.baseValorIntegral = new HashMap<>();
        this.baseValorSemRetroativo = new HashMap<>();
        this.baseMultVincValor = new HashMap<>();
        this.baseMultVincValorIR = new HashMap<>();
        this.tipoEventoTipoEventoFP = new HashMap<>();
        this.valorMetodo = new HashMap<>();
        this.irrfFicticioParaPensao = new HashMap<>();
        this.eventoEstornoValor = new HashMap<>();
        this.memoriaCalculoEventosBases = new HashMap<>();
        this.lancamentosReferenciaJaCalculados = new HashMap<>();
        this.folhaDePagamento = folhaDePagamento;
        this.ep = ep;
        this.funcoesFolhaFacade = funcoesFolhaFacade;
        this.folhaDePagamentoFacade = folhaDePagamentoFacade;
        this.tipoCalculo = tipoCalculo;

        //this.contexto = contexto;
        this.contexto = new HashMap<>();
        this.contexto.put("ep", ep);
        this.contexto.put("folhaJava", funcoesFolhaFacade);
        this.contexto.put("ano", folhaDePagamento.getAno());
        this.contexto.put("mes", folhaDePagamento.getMes().getNumeroMes());
        this.contexto.put("calculador", this);
        for (String s : this.contexto.keySet()) {
            engine.put(s, this.contexto.get(s));
        }

        this.detalheProcessamentoFolha = detalhe;
        this.lancamentos = new HashMap<>();
        this.itemPagosNaFolhaNormal = new ArrayList<>();
        ADICIONA_LOG = folhaDePagamento.isImprimeLogEmArquivo();
    }

    public FolhaDePagamentoNovoCalculador(FuncoesFolhaFacade funcoesFolhaFacade, FolhaDePagamentoFacade folhaDePagamentoFacade) {
        this.eventoValorFormula = new HashMap<>();
        this.eventoValorReferencia = new HashMap<>();
        this.eventoValorBase = new HashMap<>();
        this.eventoValorRegra = new HashMap<>();
        this.eventoValorIntegral = new HashMap<>();
        this.baseValor = new HashMap<>();
        this.baseValorIntegral = new HashMap<>();
        this.baseValorSemRetroativo = new HashMap<>();
        this.baseMultVincValor = new HashMap<>();
        this.baseMultVincValorIR = new HashMap<>();
        this.tipoEventoTipoEventoFP = new HashMap<>();
        this.valorMetodo = new HashMap<>();
        this.irrfFicticioParaPensao = new HashMap<>();
        this.eventoEstornoValor = new HashMap<>();
        this.memoriaCalculoEventosBases = new HashMap<>();
        this.funcoesFolhaFacade = funcoesFolhaFacade;
        this.folhaDePagamentoFacade = folhaDePagamentoFacade;
        this.detalheProcessamentoFolha = new DetalheProcessamentoFolha();
    }

    public void limparCacheValoresMetodos(){
        this.valorMetodo = new HashMap<>();
    }

    public FolhaDePagamentoNovoCalculador getContextoTransient() {
        return contextoTransient;
    }

    public void setContextoTransient(FolhaDePagamentoNovoCalculador contextoTransient) {
        this.contextoTransient = contextoTransient;
    }

    public Map<String, List<MemoriaCalculoRHDTO>> getMemoriaCalculoEventosBases() {
        return memoriaCalculoEventosBases;
    }

    public void setMemoriaCalculoEventosBases(Map<String, List<MemoriaCalculoRHDTO>> memoriaCalculoEventosBases) {
        this.memoriaCalculoEventosBases = memoriaCalculoEventosBases;
    }

    public static List<ItemErroScript> getItemErroScripts() {
        return itemErroScripts;
    }


    public Map<EventoFP, TipoEventoFP> getEventoEstornoValor() {
        return eventoEstornoValor;
    }

    public void setEventoEstornoValor(Map<EventoFP, TipoEventoFP> eventoEstornoValor) {
        this.eventoEstornoValor = eventoEstornoValor;
    }

    public LancamentoFP getLancamentoFP() {
        return lancamentoFP;
    }

    public void setLancamentoFP(LancamentoFP lancamentoFP) {
        this.lancamentoFP = lancamentoFP;
    }

    public Map<String, Object> getContexto() {
        return contexto;
    }

    public EntidadePagavelRH getEp() {
        return ep;
    }

    public void setEp(EntidadePagavelRH ep) {
        this.ep = ep;
    }

    public Integer getMes() {
        return getFolhaDePagamento().getMes().getNumeroMes();
    }

    public Integer getAno() {
        return getFolhaDePagamento().getAno();
    }

    public Map<EventoFP, BigDecimal> getEventoValorFormula() {
        return eventoValorFormula;
    }

    public Map<EventoFP, BigDecimal> getEventoValorReferencia() {
        return eventoValorReferencia;
    }

    public Map<EventoFP, BigDecimal> getEventoValorIntegral() {
        return eventoValorIntegral;
    }

    public FolhaCalculavel getFolhaDePagamento() {
        return folhaDePagamento;
    }

    public void setFolhaDePagamento(FolhaDePagamento folhaDePagamento) {
        this.folhaDePagamento = folhaDePagamento;
    }

    public EventoFP getEventoCalculandoAgora() {
        return eventoCalculandoAgora;
    }

    public void setEventoCalculandoAgora(EventoFP eventoCalculandoAgora) {
        this.eventoCalculandoAgora = eventoCalculandoAgora;
    }

    public Map<EventoFP, TipoEventoFP> getTipoEventoTipoEventoFP() {
        return tipoEventoTipoEventoFP;
    }

    public BigDecimal avaliaFormulaDoEventoFP(EventoFP eventoFP) {
        if (tipoCalculo.equals(TipoCalculoFP.RETROATIVO) && !eventoFP.getCalculoRetroativo()) {
            return BigDecimal.ZERO;
        }
        if (isServidorEVerbaBloqueado(ep, eventoFP)) {
            return BigDecimal.ZERO;
        }

        if (eventoValorFormula.containsKey(eventoFP)) {
            return eventoValorFormula.get(eventoFP);
        }
        eventoValorFormula.put(eventoFP, BigDecimal.ZERO); // Inclui no hash antes do cálculo para evitar referência circular
        BigDecimal valor;
        try {
            if (eventoFP.getArredondarValor()) {
                valor = BigDecimal.valueOf(avaliaFormula(eventoFP)).setScale(2, BigDecimal.ROUND_HALF_UP);
            } else {
                valor = UtilRH.truncate(String.valueOf(avaliaFormula(eventoFP)));
            }
            eventoValorFormula.put(eventoFP, valor);
        } catch (NumberFormatException nfe) {
            logger.error("NumberFormatException ao executar o método avaliaFormula(evento) erro: ", nfe);
            detalheProcessamentoFolha.getErros().put((VinculoFP) contexto.get("ep"), "Problemas no Evento " + eventoFP + "\nFormula\n" + eventoFP.getFormula() + "\nex:" + nfe);
            valor = BigDecimal.ZERO;
        }
        return valor;
    }

    public BigDecimal avaliaReferenciaDoEventoFP(EventoFP eventoFP) {
        if (tipoCalculo.equals(TipoCalculoFP.RETROATIVO) && !eventoFP.getCalculoRetroativo()) {
            return BigDecimal.ZERO;
        }

        if (eventoValorReferencia.containsKey(eventoFP)) {
            return eventoValorReferencia.get(eventoFP);
        }

        eventoValorReferencia.put(eventoFP, BigDecimal.ZERO); // Inclui no hash antes do cálculo para evitar referência circular
        BigDecimal valor = new BigDecimal(avaliaReferencia(eventoFP));
        eventoValorReferencia.put(eventoFP, valor);
        return valor;
    }

    public Map<EventoFP, BigDecimal> getEventoValorBase() {
        return eventoValorBase;
    }

    public BigDecimal avaliaValorBaseDoEventoFP(EventoFP eventoFP) {
        if (tipoCalculo.equals(TipoCalculoFP.RETROATIVO) && !eventoFP.getCalculoRetroativo()) {
            return BigDecimal.ZERO;
        }

        if (eventoValorBase.containsKey(eventoFP)) {
            return eventoValorBase.get(eventoFP);
        }

        eventoValorBase.put(eventoFP, BigDecimal.ZERO); // Inclui no hash antes do cálculo para evitar referência circular
        BigDecimal valor = new BigDecimal(avaliaBaseDeCalculo(eventoFP) + "");
        eventoValorBase.put(eventoFP, valor);
        return valor;
    }

    public BigDecimal avaliaValorIntegralDoEventoFP(EventoFP eventoFP) {
        if (tipoCalculo.equals(TipoCalculoFP.RETROATIVO) && !eventoFP.getCalculoRetroativo()) {
            return BigDecimal.ZERO;
        }
        if (isServidorEVerbaBloqueado(ep, eventoFP)) {
            return BigDecimal.ZERO;
        }

        if (eventoValorIntegral.containsKey(eventoFP)) {
            return eventoValorIntegral.get(eventoFP);
        }

        eventoValorIntegral.put(eventoFP, BigDecimal.ZERO); // Inclui no hash antes do cálculo para evitar referência circular
        BigDecimal valor = new BigDecimal(avaliaValorIntegral(eventoFP) + "");
        eventoValorIntegral.put(eventoFP, valor);
        return valor;
    }

    public boolean avaliaRegraDoEventoFP(EventoFP eventoFP) {
        if (tipoCalculo.equals(TipoCalculoFP.RETROATIVO) && !eventoFP.getCalculoRetroativo()) {
            return false;
        }
        if (isServidorEVerbaBloqueado(ep, eventoFP)) {
            return false;
        }

        if (eventoValorRegra.containsKey(eventoFP)) {
            return eventoValorRegra.get(eventoFP);
        }

        eventoValorRegra.put(eventoFP, false); // Inclui no hash antes do cálculo para evitar referência circular
        boolean condicao = avaliaRegra(eventoFP);
        eventoValorRegra.put(eventoFP, condicao);
        return condicao;
    }

    private double avaliaBaseDeCalculo(EventoFP evento) {
        if (LOG) {
            logger.debug("iniciando o calculo do  método avaliaBaseDeCalculo " + evento);
        }
        long incio = System.currentTimeMillis();
        double resultado = 0;
        try {
            String formula = "function formula(){\n" + evento.getBaseFormulaCalculo() + "\n}\nformula();";
            Object valor = engine.eval(formula);
            if (valor instanceof String) {
                valor = new BigDecimal(valor + "");
            }
            resultado = ((Number) valor).doubleValue();
            if (Double.isNaN(resultado)) {
                detalheProcessamentoFolha.getErros().put((VinculoFP) contexto.get("ep"), "Problemas no Evento " + evento + "\nBase de cálculo\n" + evento.getValorBaseDeCalculo() + "\nex:NumberFormatException");
                logger.error("Problemas no Evento " + evento + " Vinculo " + contexto.get("ep") + "\nBase de cálculo\n" + evento.getValorBaseDeCalculo() + "\nex:NumberFormatException");
                addItensDetalhesErro((VinculoFP) contexto.get("ep"), " Problemas no Evento " + evento + "\nBase de Calculo\n" + evento.getFormula() + "\nex:NumberFormatException");
            }
            if (LOG) {
                logger.debug("valor agregado a base " + evento + " : " + resultado);
            }
        } catch (Exception ex) {
            detalheProcessamentoFolha.getErros().put((VinculoFP) contexto.get("ep"), "Problemas no Evento " + evento + "\nBase de cálculo\n" + evento.getValorBaseDeCalculo() + "\nex:" + ex);
            logger.error("Problemas no Evento " + evento + " Vinculo " + contexto.get("ep") + "\nBase de cálculo\n" + evento.getValorBaseDeCalculo() + "\nex:", ex);
            addItensDetalhesErro((VinculoFP) contexto.get("ep"), " Problemas no Evento " + evento + "\nBase de Calculo\n" + evento.getFormula() + "\nex:" + ex);
        }
        if (LOGA) {
            //System.out.println((System.currentTimeMillis() - incio) + "ms para calcular base de cálculo do " + evento);
        }
        return resultado;
    }

    private double avaliaValorIntegral(EventoFP evento) {
        long incio = System.currentTimeMillis();
        double resultado = 0;
        try {
            String formula = "function formula(){\n" + evento.getFormulaValorIntegralCalculo() + "\n}\nformula();";
            resultado = ((Number) engine.eval(formula)).doubleValue();

            if (Double.isNaN(resultado)) {
                detalheProcessamentoFolha.getErros().put((VinculoFP) contexto.get("ep"), "Problemas no Evento " + evento + "\nValor Integral\n" + evento.getFormulaValorIntegral() + "\nex: NumberFormatException");
                logger.error("Problemas no Evento " + evento + " Vinculo " + contexto.get("ep") + "\nValor Integral\n" + evento.getFormulaValorIntegral() + "\nex: NumberFormatException");
                addItensDetalhesErro((VinculoFP) contexto.get("ep"), " Problemas no Evento " + evento + "\nValor Integral\n" + evento.getFormula() + "\nex: NumberFormatException");
                resultado = 0.0;
            }

        } catch (Exception ex) {
            detalheProcessamentoFolha.getErros().put((VinculoFP) contexto.get("ep"), "Problemas no Evento " + evento + "\nValor Integral\n" + evento.getFormulaValorIntegral() + "\nex:" + ex);
            logger.error("Problemas no Evento " + evento + " Vinculo " + contexto.get("ep") + "\nValor Integral\n" + evento.getFormulaValorIntegral() + "\nex:", ex);
            addItensDetalhesErro((VinculoFP) contexto.get("ep"), " Problemas no Evento " + evento + "\nValor Integral\n" + evento.getFormula() + "\nex:" + ex);
            ex.printStackTrace();
        }
        if (LOGA) {
            //System.out.println((System.currentTimeMillis() - incio) + "ms para calcular base de cálculo do " + evento);
        }
        return resultado;
    }

    private double avaliaFormula(EventoFP evento) {
        if (LOG) {
            logger.debug("iniciando o calculo do  método avaliaFormula " + evento);
        }
        long incio = System.currentTimeMillis();

        double resultado = 0;
        try {

            String formula = "function formula(){\n" + evento.getFormulaCalculo() + "\n}\nformula();";

            if (isCalculo13(evento)) {
                switch (evento.getTipoCalculo13()) {
                    case ULTIMO_ACUMULADO:
                        return recuperarValor13ProporcionalUltimoMes(evento);
                    case HORAS_ANO:
                        return ((Number) engine.eval(formula)).doubleValue();
                    case VALOR_ANO:
                        return recuperarValor13ProporcionalMediaAno(evento);
                    case LER_FORMULA:
                        Double valor = (Double) engine.eval(formula);
                        if (evento.getPropMesesTrabalhados() && valor > 0) {
                            return valor / 12 * quantidadeMesesTrabalhadosAno();
                        } else {
                            return valor;
                        }
                    case NAO:
                        return resultado;
                }
            } else {
                resultado = ((Number) engine.eval(formula)).doubleValue();
            }
            if (LOG) {
                logger.debug("valor do evento " + evento + " : " + resultado);
            }
            addLog("avaliaFormula evento" + evento, resultado);
            if (Double.isNaN(resultado)) {
                detalheProcessamentoFolha.getErros().put((VinculoFP) contexto.get("ep"), "Problemas no Evento " + evento + "\nFormula\n" + evento.getFormula() + "\nex: NumberFormatException");
                logger.error("Problemas no Evento " + evento + " Vinculo " + contexto.get("ep") + "\nFormula\n" + evento.getFormula() + "\nex: NumberFormatException");
                addItensDetalhesErro((VinculoFP) contexto.get("ep"), " Problemas no Evento " + evento + "\nFormula\n" + evento.getFormula() + "\nex: NumberFormatException");
                resultado = 0;
            }

            Boolean naoPagaEventoEmFerias = isNaoPagaEventoEmFerias(evento);
            Boolean naoPagaEventoEmLicencaPremio = isNaoPagaEventoEmLicencaPremio(evento);
            if (naoPagaEventoEmFerias || naoPagaEventoEmLicencaPremio) {
                double diasFeriasELicenca = 0d;
                if (naoPagaEventoEmFerias) {
                    diasFeriasELicenca += quantidadeDiasFeriasOuLicencaConcedidasPeloPeriodoDeGozo(TipoPeriodoAquisitivo.FERIAS.name());
                }
                if (naoPagaEventoEmLicencaPremio) {
                    diasFeriasELicenca += quantidadeDiasFeriasOuLicencaConcedidasPeloPeriodoDeGozo(TipoPeriodoAquisitivo.LICENCA.name());
                }
                double dias = diasTrabalhados() - diasFeriasELicenca;
                resultado = resultado / obterDiasDoMes() * dias;
            }

        } catch (Exception ex) {
            detalheProcessamentoFolha.getErros().put((VinculoFP) contexto.get("ep"), "Problemas no Evento " + evento + "\nFormula\n" + evento.getFormula() + "\nex:" + ex);
            logger.error("Problemas no Evento " + evento + " Vinculo " + contexto.get("ep") + "\nFormula\n" + evento.getFormula() + "\nex:", ex);
            addItensDetalhesErro((VinculoFP) contexto.get("ep"), " Problemas no Evento " + evento + "\nFormula\n" + evento.getFormula() + "\nex:" + ex);
            return 0;
        }

        return resultado;
    }

    private double avaliaFormulaPensaoJudicial(EventoFP evento) {
        if (LOG) {
            logger.debug("iniciando o calculo do  método avaliaFormulaPensaoJudicial " + evento);
        }
        long incio = System.currentTimeMillis();

        double resultado = 0;
        try {
            String formula = "function formula(){\n" + evento.getFormula() + "\n}\nformula();";
            if (isCalculo13(evento)) {
                switch (evento.getTipoCalculo13()) {
                    case ULTIMO_ACUMULADO:
                        return recuperarValor13ProporcionalUltimoMes(evento);
                    case HORAS_ANO:
                        return ((Number) engine.eval(formula)).doubleValue();
                    case VALOR_ANO:
                        return recuperarValor13ProporcionalMediaAno(evento);
                    case LER_FORMULA:
                        return ((Number) engine.eval(formula)).doubleValue();
                    case NAO:
                        return resultado;
                }
            } else {
                resultado = ((Number) engine.eval(formula)).doubleValue();
            }
            addLog("avaliaFormulaPensaoJudicial evento" + evento, resultado);
        } catch (Exception ex) {
            detalheProcessamentoFolha.getErros().put((VinculoFP) contexto.get("ep"), "Problemas no Evento " + evento + "\nFormula\n" + evento.getFormula() + "\nex:" + ex);
            logger.error("Problemas no Evento " + evento + " Vinculo " + contexto.get("ep") + "\n avaliaFormulaPensaoJudicial\n" + evento.getFormula() + "\nex:", ex);
            //itemErroScripts.add(new ItemErroScript(TiposErroScript.ERRO_DESCONHECIDO, null, "Problemas no Evento " + evento + " Vinculo " + contexto.get("ep") + "\n erro na Formula ex:" + ex, null));
            addItensDetalhesErro((VinculoFP) contexto.get("ep"), " Problemas no Evento " + evento + "\nFormula\n" + evento.getFormula() + "\nex:" + ex);
            ex.printStackTrace();
            return 0;
        }
        if (LOGA) {
            //System.out.println((System.currentTimeMillis() - incio) + "ms para calcular fórmula do " + evento);
        }
        return resultado;
    }

    public boolean isUltimoAcumulado(EventoFP evento) {
        return evento.getTipoCalculo13().equals(TipoCalculo13.ULTIMO_ACUMULADO);
    }

    public boolean isHorasAno(EventoFP evento) {
        return evento.getTipoCalculo13().equals(TipoCalculo13.HORAS_ANO);
    }

    public boolean isValorAno(EventoFP evento) {
        return evento.getTipoCalculo13().equals(TipoCalculo13.VALOR_ANO);
    }

    public boolean isLerFormula(EventoFP evento) {
        return evento.getTipoCalculo13().equals(TipoCalculo13.LER_FORMULA);
    }

    private double avaliaReferencia(EventoFP evento) {
        if (LOG) {
            logger.debug("iniciando o calculo do  método avaliaReferencia " + evento);
        }
        long incio = System.currentTimeMillis();

        double resultado = 0;
        //EM TESTE - feito para os testes onde o na referencia está valor 0, está lancaçado e na formula chama valor da referencia.
//        double resultadoTeste = funcoesFolhaFacade.recuperaQuantificacaoLancamentoTipoReferencia(getEp(), evento.getCodigo(), (Integer) contexto.get("mes"), (Integer) contexto.get("ano")).doubleValue();
        try {
//            if (resultadoTeste > 0) {
//                return resultadoTeste;
//            }
            String formula = "function formula(){\n" + evento.getReferenciaCalculo() + "\n}\nformula();";
            if (isCalculo13(evento)) {
                switch (evento.getTipoCalculo13()) {
                    case ULTIMO_ACUMULADO:
                        return funcoesFolhaFacade.quantidadeOcorrenciaEventoByAno(getEp(), evento);
                    case HORAS_ANO:
                        return ((Number) engine.eval(formula)).doubleValue();
                    case VALOR_ANO:
                        return recuperarValor13ProporcionalMediaAno(evento);
                    case LER_FORMULA:
                        return ((Number) engine.eval(formula)).doubleValue();
                    case NAO:
                        return resultado;
                }
            } else {
                resultado = ((Number) engine.eval(formula)).doubleValue();
            }
            if (LOG) {
                logger.debug("valor do evento " + evento + " : " + resultado);
            }
            addLog("avaliaReferencia evento" + evento, resultado);
            if (Double.isNaN(resultado)) {
                detalheProcessamentoFolha.getErros().put((VinculoFP) contexto.get("ep"), "Problemas no Evento " + evento + "\nReferencia\n" + evento.getReferencia() + "\nex:NumberFormatException");
                logger.error("Problemas no Evento " + evento + " Vinculo " + contexto.get("ep") + "\nReferencia\n" + evento.getReferencia() + "\nex:NumberFormatException");
                addItensDetalhesErro((VinculoFP) contexto.get("ep"), " Problemas no Evento " + evento + "\nReferencia\n" + evento.getFormula() + "\nex:NumberFormatException");
            }
        } catch (Exception ex) {
            detalheProcessamentoFolha.getErros().put((VinculoFP) contexto.get("ep"), "Problemas no Evento " + evento + "\nReferencia\n" + evento.getReferencia() + "\nex:" + ex);
            logger.error("Problemas no Evento " + evento + " Vinculo " + contexto.get("ep") + "\nReferencia\n" + evento.getReferencia() + "\nex:", ex);
            addItensDetalhesErro((VinculoFP) contexto.get("ep"), " Problemas no Evento " + evento + "\nReferencia\n" + evento.getFormula() + "\nex:" + ex);
        }
        if (LOGA) {
            //System.out.println((System.currentTimeMillis() - incio) + "ms para calcular referência do " + evento);
        }
        return resultado;
    }

    public boolean avaliaRegra(EventoFP evento) {
        if (LOG) {
            logger.debug("iniciando o calculo do  método avaliaRegra " + evento);
        }
        long incio = System.currentTimeMillis();
        boolean resultado = false;
        try {
            String regra = "function formula(){\n" + evento.getRegraCalculo() + "\n}\nformula();";
            resultado = avaliarRegrasPrimarias(evento);
            if (resultado) {
                resultado = (boolean) engine.eval(regra);
            }
            if (LOG) {
                logger.debug("valor do evento " + evento + " : " + resultado);
            }
        } catch (Exception ex) {
            detalheProcessamentoFolha.getErros().put((VinculoFP) contexto.get("ep"), "Problemas no Evento " + evento + "\nRegra\n" + evento.getRegra() + "\nex:" + ex);
            logger.error("Problemas no Evento " + evento + " Vinculo " + contexto.get("ep") + "\nRegra\n" + evento.getRegra() + "\nex:", ex);
            addItensDetalhesErro((VinculoFP) contexto.get("ep"), " Problemas no Evento " + evento + "\nRegra\n" + evento.getFormula() + "\nex:" + ex);
        }
        if (LOGA) {
            //System.out.println((System.currentTimeMillis() - incio) + "ms para avaliar regra do " + evento);
        }
        addLog("avaliaRegra evento " + evento, resultado);
        return resultado;
    }

    /**
     * Método avalia se o evento pode ser executado para aquele tipo de folha
     */
    public boolean avaliarRegrasPrimarias(EventoFP evento) {
        boolean podeCalcularParaTipoFolha = avaliarRegraTipoFolha(evento);
        if (podeCalcularParaTipoFolha) {
            //return avaliarRegraEmpregador(evento);
            return true; //TODO aguardando melhor definição do esocial
        }
        return podeCalcularParaTipoFolha;
    }

    private boolean avaliarRegraEmpregador(EventoFP evento) {

        if (evento.getEntidade() == null) {
            return true; //quando entidade é nula(não tem configuração de empregador), processa para todos os empregadores/entidade
        }
        Map<ConfiguracaoEmpregadorESocial, List<HierarquiaOrganizacional>> empregadores = detalheProcessamentoFolha.getEmpregadores();
        if (empregadores.isEmpty()) {
            return true;
        }
        for (Map.Entry<ConfiguracaoEmpregadorESocial, List<HierarquiaOrganizacional>> entry : empregadores.entrySet()) {
            if (evento.getEntidade().equals(entry.getKey().getEntidade())) {
                for (HierarquiaOrganizacional ho : entry.getValue()) {
                    if (verificarOrgaoServidor(ho.getCodigoDo2NivelDeHierarquia())) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    private boolean avaliarRegraTipoFolha(EventoFP evento) {
        List<EventoFPTipoFolha> eventoFPTipoFolhas = Lists.newLinkedList();
        if (detalheProcessamentoFolha.getEventoFPTiposFolha().containsKey(evento.getCodigo())) {
            eventoFPTipoFolhas = detalheProcessamentoFolha.getEventoFPTiposFolha().get(evento.getCodigo());
        } else {
            eventoFPTipoFolhas = evento.getTiposFolha();
            detalheProcessamentoFolha.getEventoFPTiposFolha().put(evento.getCodigo(), new ArrayList<>(eventoFPTipoFolhas));
        }

        if (eventoFPTipoFolhas != null) {
            for (EventoFPTipoFolha eventoFPTipoFolha : eventoFPTipoFolhas) {
                if (eventoFPTipoFolha.getTipoFolhaDePagamento().equals(folhaDePagamento.getTipoFolhaDePagamento())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean avaliaRegraPensaoJudicial(EventoFP evento) {
        if (LOG) {
            logger.debug("iniciando o calculo do  método avaliaRegra " + evento);
        }
        long incio = System.currentTimeMillis();
        boolean resultado = false;
        try {
            String regra = "function formula(){\n" + evento.getRegra() + "\n}\nformula();";
            resultado = (boolean) engine.eval(regra);
            if (LOG) {
                logger.debug("valor do evento " + evento + " : " + resultado);
            }
            addLog("avaliaRegraPensaoJudicial " + evento, resultado);
        } catch (Exception ex) {
            detalheProcessamentoFolha.getErros().put((VinculoFP) contexto.get("ep"), "Problemas no Evento " + evento + "\nRegra\n" + evento.getRegra() + "\nex:" + ex);
            logger.error("Problemas no Evento " + evento + " Vinculo " + contexto.get("ep") + "\navaliaRegraPensaoJudicial\n" + evento.getRegra() + "\nex:" + ex);
            addItensDetalhesErro((VinculoFP) contexto.get("ep"), " Problemas no Evento " + evento + "\nRegra\n" + evento.getFormula() + "\nex:" + ex);
        }
        if (LOGA) {
            //System.out.println((System.currentTimeMillis() - incio) + "ms para avaliar regra do " + evento);
        }
        return resultado;
    }

    public boolean isCalculo13eEventoVerbaFixa(EventoFP eventoFP) {
        if (eventoFP.getVerbaFixa() == null) {
            return false;
        }
        if (folhaDePagamento.getTipoFolhaDePagamento().equals(TipoFolhaDePagamento.SALARIO_13) && eventoFP.getVerbaFixa()) {
            return true;
        }
        return false;
    }

    public boolean isCalculo13(EventoFP evento) {
        return folhaDePagamento.getTipoFolhaDePagamento().equals(TipoFolhaDePagamento.SALARIO_13);
    }

    public boolean isCalculo13() {
        return TipoFolhaDePagamento.isFolha13Salario(folhaDePagamento);
    }

    @DescricaoMetodo("avaliaEvento(codigoEvento)")
    public double avaliaEvento(String codigoEvento) throws Exception {  //Para ser chamado do JavaScript
        EventoFP evento = funcoesFolhaFacade.recuperaEvento(codigoEvento, TipoExecucaoEP.FOLHA);
        if (eventoValorFormula.containsKey(evento)) {
            return avaliaFormulaDoEventoFP(evento).doubleValue();
        } else if (avaliaRegraDoEventoFP(evento)) {
            return avaliaFormulaDoEventoFP(evento).doubleValue();
        }
        return 0;
    }

    @DescricaoMetodo("recuperaEvento('codigoEvento', 'tipoFormula')")
    public double recuperaEvento(String codigoEvento, String tipoFormula) throws Exception {
        try {
            TipoFormulaEventoFP tipo = TipoFormulaEventoFP.valueOf(tipoFormula);
            EventoFP evento = funcoesFolhaFacade.recuperaEvento(codigoEvento, TipoExecucaoEP.FOLHA);

            switch (tipo) {
                case FORMULA:
                    if (eventoValorFormula.containsKey(evento)) {
                        return avaliaFormulaDoEventoFP(evento).doubleValue();
                    } else if (avaliaRegraDoEventoFP(evento)) {
                        return avaliaFormulaDoEventoFP(evento).doubleValue();
                    }
                    break;
                case INTEGRAL:
                    if (eventoValorIntegral.containsKey(evento)) {
                        return avaliaValorIntegralDoEventoFP(evento).doubleValue();
                    } else if (avaliaRegraDoEventoFP(evento)) {
                        return avaliaValorIntegralDoEventoFP(evento).doubleValue();
                    }
                    break;
                case REFERENCIA:
                    if (eventoValorReferencia.containsKey(evento)) {
                        return avaliaReferenciaDoEventoFP(evento).doubleValue();
                    } else if (avaliaRegraDoEventoFP(evento)) {
                        return avaliaReferenciaDoEventoFP(evento).doubleValue();
                    }
                    break;
                case BASE:
                    if (eventoValorBase.containsKey(evento)) {
                        return avaliaValorBaseDoEventoFP(evento).doubleValue();
                    } else if (avaliaRegraDoEventoFP(evento)) {
                        return avaliaValorBaseDoEventoFP(evento).doubleValue();
                    }
                    break;
                default:
                    return 0;
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    @DescricaoMetodo("avaliaReferencia(codigoEvento)")
    public double avaliaReferencia(String codigoEvento) throws Exception {  //Para ser chamado do JavaScript
        EventoFP evento = funcoesFolhaFacade.recuperaEvento(codigoEvento, TipoExecucaoEP.FOLHA);
        if (eventoValorFormula.containsKey(evento)) {
            return avaliaReferenciaDoEventoFP(evento).doubleValue();
        } else if (avaliaRegra(evento)) {
            return avaliaReferenciaDoEventoFP(evento).doubleValue();
        }
        return 0;
    }

    public Map<String, BigDecimal> getBaseValor() {
        return baseValor;
    }

    @DescricaoMetodo("calculaBaseValorIntegral('codigoBase')")
    public double calculaBaseValorIntegral(String codigoBase) throws Exception { //Para ser chamado do JavaScript
        boolean LOGBASE = LOGA;
        if (LOGBASE) {
            logger.debug("iniciando o calculo do método calculaBase " + codigoBase);
        }
        if (baseValorIntegral.containsKey(codigoBase)) {
            if (LOGBASE) {
                logger.debug("valor da base: " + codigoBase + " : " + baseValorIntegral.get(codigoBase).doubleValue());
            }
            return baseValorIntegral.get(codigoBase).doubleValue();
        }
        if (LOGBASE) logger.debug("============================##========================");
        BigDecimal soma = BigDecimal.ZERO;
        List<ItemBaseFP> itensBaseFP = funcoesFolhaFacade.obterItensBaseFP(codigoBase);
        List<MemoriaCalculoRHDTO> listaEventosBase = Lists.newLinkedList();
        FiltroBaseFP filtroBaseFP = null;
        for (ItemBaseFP item : itensBaseFP) {
            if (filtroBaseFP == null && TipoFolhaDePagamento.isFolha13Salario(folhaDePagamento)) {
                filtroBaseFP = item.getBaseFP().getFiltroBaseFP();
            }
            EventoFP eventoFP = item.getEventoFP();
            BigDecimal valor = BigDecimal.ZERO;
            if (LOGBASE) logger.debug("calculando o evento: " + eventoFP + " dentro da base: " + codigoBase);
            if (isServidorEVerbaBloqueado(ep, eventoFP)) {
                continue;
            }
            if (getEventoValorIntegral().containsKey(eventoFP)) {
                valor = getEventoValorIntegral().get(eventoFP);
            } else if (eventoFP.getAutomatico() && eventoFP.getAtivo() && avaliaRegra(eventoFP)) { // São calculadas apenas as fórmulas de eventos automáticos
                valor = avaliaValorIntegralDoEventoFP(eventoFP);
            } else {
                if (lancamentos.containsKey(eventoFP)) {
                    LancamentoFP lancamento = lancamentos.get(eventoFP);
                    if (lancamento.getTipoLancamentoFP().equals(TipoLancamentoFP.VALOR)) {
                        valor = lancamento.getQuantificacao();
                    } else {
                        valor = avaliaValorIntegralDoEventoFP(eventoFP);
                    }
                } else {
                    continue;
                }
            }
            if (contextoTransient != null) {
                if (contextoTransient.getEventoValorFormula().containsKey(eventoFP)) {
                    BigDecimal valorEventoTransient = contextoTransient.getEventoValorFormula().get(eventoFP);
                    if (valor != null && valor.compareTo(BigDecimal.ZERO) == 0) {
                        if (!FiltroBaseFP.PENSAO_ALIMENTICIA.equals(filtroBaseFP)) {
                            valor = valorEventoTransient;
                        }
                    }
                }
            }
            if (LOGBASE && valor.compareTo(BigDecimal.ZERO) > 0)
                logger.debug("valor do evento " + eventoFP + ": " + valor + " dentro da base: " + codigoBase);
            OperacaoFormula opItem = getDeParaOperacaoFormula(item, null);
            if (opItem.equals(OperacaoFormula.ADICAO)) {
                soma = soma.add(valor);
            } else {
                soma = soma.subtract(valor);
            }
            if (valor.compareTo(BigDecimal.ZERO) != 0) {
                listaEventosBase.add(new MemoriaCalculoRHDTO(codigoBase, eventoFP, TipoMemoriaCalculo.BASEFP, opItem, valor));
            }
        }

        if (LOGBASE) logger.debug("Total da Base " + codigoBase + " sem retroativo: " + soma);
//        if (!isCalculandoRetroativo()) {
//        BigDecimal valor = new BigDecimal(funcoesFolhaFacade.valorTotalBaseRetroativos(codigoBase, getEp(), getAno(), getMes(),new ArrayList<EventoFP>(getEventoValorFormula().keySet())) + "");
        BigDecimal valor = new BigDecimal(funcoesFolhaFacade.valorTotalBaseRetroativos(codigoBase, getEp(), getAno(), getMes(), irrfFicticioParaPensao.get(codigoBase)) + "");
        if (LOGBASE) logger.debug("Valor do Retroativo da Base" + codigoBase + " : " + valor);
        soma = soma.add(valor);
        if (valor.compareTo(BigDecimal.ZERO) != 0) {
            listaEventosBase.add(new MemoriaCalculoRHDTO(codigoBase, "Valor Retroação Base Valor Integral", null, TipoMemoriaCalculo.BASEFP, OperacaoFormula.ADICAO, valor));
        }
        if (LOGBASE) {
            logger.debug("Total da Base " + codigoBase + " : " + soma);
        }
        if (!listaEventosBase.isEmpty() && getFolhaDePagamento().isGravarMemoriaCalculo()) {
            memoriaCalculoEventosBases.put("calculaBaseValorIntegral('" + codigoBase + "')", listaEventosBase);
        }
        baseValorIntegral.put(codigoBase, soma);
        if (LOGBASE) logger.debug("============================##========================");
        return soma.doubleValue();
    }

    // O calculo da base obedece ao tipo de valor informado no cadastro da Base Folha de Pagamento.

    // Se o tipo for igual a NORMAL: No caso de calculo que contenha o contexto transiente, só é somado a base, eventos que não tenham sido calculados na folha normal,
    // pois se houver alguma diferença a ser ressarcida ou ser descontada, esses valores já vão compor base na comparação de uma folha com a outra, assim impedindo dos
    // valores compor base duas vezes no cálculo. Caso a folha em calculo não contenha o contexto transiente, a soma ocorre normalmente avaliando a fórmula do evento
    // no próprio calculo.

    // Se o tipo for igual a INTEGRAL: Se existir calculo transiente será recuperado o valor integral do evento calculado no mesmo, caso o tipo de folha não contenha o calculo
    // transiente será recuperado o resultado da fórmula valor integral do evento, no próprio calculo que está ocorrendo no momento.
    @DescricaoMetodo("calculaBase('codigoBase')")
    public double calculaBase(String codigoBase) throws Exception { //Para ser chamado do JavaScript
        boolean LOG_BASE = LOGA;
        if (LOG_BASE) {
            logger.debug("iniciando o calculo do método calculaBase " + codigoBase);
        }
        if (baseValor.containsKey(codigoBase)) {
            if (LOG_BASE) {
                logger.debug("valor da base: " + codigoBase + " : " + baseValor.get(codigoBase).doubleValue());
            }
            return baseValor.get(codigoBase).doubleValue();
        }
        if (LOG_BASE) logger.debug("============================##========================");
        if (LOG_BASE) logger.debug("Evento calculando now!! {} ", getEventoCalculandoAgora());
        //removerCacheDeIRRFParaPensaoAlimenticia(filtroBaseFP);
        BigDecimal soma = BigDecimal.ZERO;
        List<ItemBaseFP> itensBaseFP = buscarItensBase(codigoBase);
        List<MemoriaCalculoRHDTO> listaEventosBase = Lists.newLinkedList();
        FiltroBaseFP filtroBaseFP = null;
        for (ItemBaseFP item : itensBaseFP) {
            if (filtroBaseFP == null && TipoFolhaDePagamento.isFolha13Salario(folhaDePagamento)) {
                filtroBaseFP = item.getBaseFP().getFiltroBaseFP();
            }
            EventoFP eventoFP = item.getEventoFP();
            BigDecimal valor = BigDecimal.ZERO;
            Map<EventoFP, TipoEventoFP> mapTipoEventoCalculado = new HashMap<>();

            if (isServidorEVerbaBloqueado(ep, eventoFP)) {
                continue;
            }
            if ((item.getTipoValor() == null || TipoValor.NORMAL.equals(item.getTipoValor())) && getEventoValorFormula().containsKey(eventoFP) && !eventoPagoNaFolhaNormal(eventoFP)) {
                valor = getEventoValorFormula().get(eventoFP);
            } else if ((item.getTipoValor() == null || TipoValor.NORMAL.equals(item.getTipoValor())) && eventoFP.getAutomatico() && eventoFP.getAtivo() && avaliaRegra(eventoFP) && !eventoPagoNaFolhaNormal(eventoFP)) { // São calculadas apenas as fórmulas de eventos automáticos
                valor = avaliaFormulaDoEventoFP(eventoFP);
            } else if (TipoValor.INTEGRAL.equals(item.getTipoValor()) && getEventoValorIntegral().containsKey(eventoFP) && contextoTransient == null) {
                valor = getEventoValorIntegral().get(eventoFP);
            } else if (TipoValor.INTEGRAL.equals(item.getTipoValor()) && eventoFP.getAutomatico() && eventoFP.getAtivo() && avaliaRegra(eventoFP) && contextoTransient == null) {
                valor = avaliaValorIntegralDoEventoFP(eventoFP);
            } else if (lancamentos.containsKey(eventoFP)) {
                LancamentoFP lancamento = lancamentos.get(eventoFP);
                if (contextoTransient != null && TipoValor.INTEGRAL.equals(item.getTipoValor()) && contextoTransient.getEventoValorIntegral().containsKey(eventoFP)) {
                    if (!FiltroBaseFP.PENSAO_ALIMENTICIA.equals(filtroBaseFP) && podeSomar(detalheProcessamentoFolha, eventoFP, folhaDePagamento)) {
                        valor = contextoTransient.getEventoValorIntegral().get(eventoFP);
                    }
                } else if (lancamento.getTipoLancamentoFP().equals(TipoLancamentoFP.VALOR)) {
                    valor = getValorLancamento(lancamento, eventoFP);
                } else {
                    if (lancamento.getTipoLancamentoFP().equals(TipoLancamentoFP.REFERENCIA) && lancamento.getBaseFP() != null) {
                        valor = calculaBaseSobreParcentualDeLancamentofp(this, lancamento, item);
                    } else {
                        if (TipoValor.INTEGRAL.equals(item.getTipoValor())) {
                            valor = avaliaValorIntegralDoEventoFP(eventoFP);
                        } else {
                            valor = avaliaFormulaDoEventoFP(eventoFP);
                        }
                    }
                }

                if (valor != null && valor.compareTo(BigDecimal.ZERO) > 0) {
                    if (TipoValor.NORMAL.equals(item.getTipoValor()) && folhaDePagamentoFacade.isPodeProporcionalizar(lancamento)) {
                        valor = folhaDePagamentoFacade.proporcionalizarValorParaLancamentos(this, valor, lancamento, (VinculoFP) ep);
                    }
                }

                if (eventoPagoNaFolhaNormal(eventoFP) && TipoValor.NORMAL.equals(item.getTipoValor())) {
                    valor = BigDecimal.ZERO;
                }
            } else if (contextoTransient != null && TipoValor.INTEGRAL.equals(item.getTipoValor()) && contextoTransient.getEventoValorIntegral().containsKey(eventoFP)) {
                if (!FiltroBaseFP.PENSAO_ALIMENTICIA.equals(filtroBaseFP) && podeSomar(detalheProcessamentoFolha, eventoFP, folhaDePagamento)) {
                    valor = contextoTransient.getEventoValorIntegral().get(eventoFP);
                }
            } else {
                continue;
            }

            if (LOG_BASE && valor.compareTo(BigDecimal.ZERO) != 0)
                logger.debug("valor do evento " + eventoFP + ": " + valor + " dentro da base: " + codigoBase);
            OperacaoFormula opItem = getDeParaOperacaoFormula(item, mapTipoEventoCalculado);
            if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
                valor = valor.abs();
            }
            if (opItem.equals(OperacaoFormula.ADICAO)) {
                soma = soma.add(valor);
            } else {
                soma = soma.subtract(valor);
            }
            if (valor.compareTo(BigDecimal.ZERO) != 0) {
                listaEventosBase.add(new MemoriaCalculoRHDTO(codigoBase, eventoFP, TipoMemoriaCalculo.BASEFP, opItem, valor));
            }
        }

        if (LOG_BASE) logger.debug("Total da Base " + codigoBase + " sem retroativo: " + soma);
        if (!isCalculandoRetroativo() && !isCalculo13() && !TipoFolhaDePagamento.COMPLEMENTAR.equals(folhaDePagamento.getTipoFolhaDePagamento())) {
            BigDecimal valor = new BigDecimal(funcoesFolhaFacade.valorTotalBaseRetroativos(codigoBase, getEp(), getAno(), getMes(), irrfFicticioParaPensao.get(codigoBase)) + "");
            if (LOG_BASE) logger.debug("Valor do Retroativo da Base" + codigoBase + " : " + valor);
            if (valor.compareTo(BigDecimal.ZERO) != 0) {
                listaEventosBase.add(new MemoriaCalculoRHDTO(codigoBase, "Valor Retroação", null, TipoMemoriaCalculo.BASEFP, OperacaoFormula.ADICAO, valor));
            }
            soma = soma.add(valor);
        }

        if (Util.isValorNegativo(soma)) {
            soma = BigDecimal.ZERO;
        }

        soma = soma.setScale(10, RoundingMode.HALF_UP);
        if (LOG_BASE) {
            logger.debug("Total da Base " + codigoBase + " : " + soma);
        }
        baseValor.put(codigoBase, soma);
        if (!listaEventosBase.isEmpty() &&

            getFolhaDePagamento().

                isGravarMemoriaCalculo()) {
            memoriaCalculoEventosBases.put("calculaBase('" + codigoBase + "')", listaEventosBase);
        }
        if (LOG_BASE) logger.debug("============================##========================");
        engine.put("EVENTO", null);
        return soma.doubleValue();
    }

    private boolean eventoPagoNaFolhaNormal(EventoFP ev) {
        for (EventoTotalItemFicha itemFicha : itemPagosNaFolhaNormal) {
            if (ev.getCodigo().equals(itemFicha.getEvento().getCodigo())) {
                return true;
            }
        }
        return false;
    }

    private boolean podeSomar(DetalheProcessamentoFolha detalheProcessamentoFolha, EventoFP eventoFP, FolhaCalculavel folhaDePagamento) {
        if(TipoFolhaDePagamento.isFolha13Salario(folhaDePagamento)){
            return !detalheProcessamentoFolha.getEventosPensaoAlimenticia().contains(eventoFP);
        }
        return true;
    }


    private BigDecimal getValorLancamento(LancamentoFP lancamento, EventoFP eventoFP) {
        if (isCalculo13() && eventoFP.getTipoCalculo13() != null && TipoCalculo13.NAO.equals(eventoFP.getTipoCalculo13())) {
            return BigDecimal.ZERO;
        }
        return lancamento.getQuantificacao();
    }

    private List<ItemBaseFP> buscarItensBase(String codigoBase) {
        List<ItemBaseFP> obterItens = Lists.newArrayList();
        if (detalheProcessamentoFolha.getCacheItensBaseFP().containsKey(codigoBase)) {
            obterItens = Lists.newArrayList(detalheProcessamentoFolha.getCacheItensBaseFP().get(codigoBase));
        } else {
            List<ItemBaseFP> itemBaseFPS = funcoesFolhaFacade.obterItensBaseFP(codigoBase);
            detalheProcessamentoFolha.addItensBase(codigoBase, Lists.newArrayList(itemBaseFPS));
            obterItens = Lists.newArrayList(itemBaseFPS);
        }

        if (!irrfFicticioParaPensao.isEmpty() && irrfFicticioParaPensao.get(codigoBase) != null) {
            List<EventoFP> eventoFPS = irrfFicticioParaPensao.get(codigoBase);
            for (Iterator<ItemBaseFP> iter = obterItens.iterator(); iter.hasNext(); ) {
                ItemBaseFP next = iter.next();
                if (!eventoFPS.contains(next.getEventoFP())) {
                    iter.remove();
                }
            }
        }
        return obterItens;
    }


    private BigDecimal calculaBaseSobreParcentualDeLancamentofp(FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador, LancamentoFP lancamento, ItemBaseFP item) {
        try {
            if (isCalculo13() && lancamento.getEventoFP().getTipoCalculo13() != null && TipoCalculo13.NAO.equals(lancamento.getEventoFP().getTipoCalculo13())) {
                return BigDecimal.ZERO;
            }
            String codigoBase = lancamento.getBaseFP().getCodigo();
            folhaDePagamentoNovoCalculador.calculaBase(codigoBase);
            BigDecimal valorBase = BigDecimal.ZERO;
            if (folhaDePagamentoNovoCalculador.getBaseValor().containsKey(codigoBase)) {
                valorBase = folhaDePagamentoNovoCalculador.getBaseValor().get(codigoBase);
                BigDecimal resultado = valorBase.multiply(lancamento.getQuantificacao()).divide(new BigDecimal("100"));
                if (resultado != null && resultado.compareTo(BigDecimal.ZERO) > 0) {
                    if (TipoValor.NORMAL.equals(item.getTipoValor()) && folhaDePagamentoFacade.isPodeProporcionalizar(lancamento)) {
                        resultado = folhaDePagamentoFacade.proporcionalizarValorParaLancamentos(this, resultado, lancamento, (VinculoFP) ep);
                    }
                }
                folhaDePagamentoNovoCalculador.getEventoValorFormula().put(lancamento.getEventoFP(), resultado);
                folhaDePagamentoNovoCalculador.getEventoValorBase().put(lancamento.getEventoFP(), valorBase);
                addLog("calculaBaseSobreParcentualDeLancamentofp " + lancamento.getEventoFP(), resultado);
                return resultado;
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return BigDecimal.ZERO;
    }

    @DescricaoMetodo("calculaBasePensaoAlimenticia('codigoBase')")
    public double calculaBasePensaoAlimenticia(String codigoBase) throws Exception { //Para ser chamado do JavaScript
        boolean LOG_BASE = LOG;
        adicionarCodigoPensaoAlimenticia(codigoBase);
        if (LOG_BASE) {
            logger.debug("inicio===============================================================");
            logger.debug("iniciando o calculo do método calculaBasePensaoAlimenticia " + codigoBase);
        }
        if (baseValor.containsKey(codigoBase)) {
            if (LOG_BASE) {
                logger.debug("valor da base pensão alimenticia: " + codigoBase + " : " + baseValor.get(codigoBase).doubleValue());
                logger.debug("fim===============================================================");
            }
            return baseValor.get(codigoBase).doubleValue();
        }

        BigDecimal soma = BigDecimal.ZERO;
        List<ItemBaseFP> itensBaseFP = funcoesFolhaFacade.obterItensBaseFP(codigoBase);
        List<MemoriaCalculoRHDTO> listaEventosBase = Lists.newLinkedList();

        for (ItemBaseFP item : itensBaseFP) {
            EventoFP eventoFP = item.getEventoFP();
            BigDecimal valor = BigDecimal.ZERO;
            if (LOG)
                logger.debug("calculando o evento: " + eventoFP + " dentro da base Pensão Alimentícia: " + codigoBase);
            if (isServidorEVerbaBloqueado(ep, eventoFP)) {
                continue;
            }
            if (getEventoValorFormula().containsKey(eventoFP)) {
                valor = getEventoValorFormula().get(eventoFP);
            } else if (eventoFP.getAutomatico() && eventoFP.getAtivo() && avaliaRegra(eventoFP)) { // São calculadas apenas as fórmulas de eventos automáticos
                valor = avaliaFormulaDoEventoFP(eventoFP);
            } else {
                if (lancamentos.containsKey(eventoFP)) {
                    LancamentoFP lancamento = lancamentos.get(eventoFP);
                    if (lancamento.getTipoLancamentoFP().equals(TipoLancamentoFP.VALOR)) {
                        valor = getValorLancamento(lancamento, eventoFP);
                    } else {
                        if (lancamento.getTipoLancamentoFP().equals(TipoLancamentoFP.REFERENCIA) && lancamento.getBaseFP() != null) {
                            valor = calculaBaseSobreParcentualDeLancamentofp(this, lancamento, item);
                        } else {
                            valor = avaliaFormulaDoEventoFP(eventoFP);
                        }
                    }
                } else {
                    continue;
                }
            }
            if (LOG_BASE)
                logger.debug("valor do evento " + eventoFP + ": " + valor + " dentro da base de Pensão Alimentícia: " + codigoBase);
            OperacaoFormula opItem = getDeParaOperacaoFormula(item, null);
            if (opItem.equals(OperacaoFormula.ADICAO)) {
                soma = soma.add(valor);
            } else {
                soma = soma.subtract(valor);
            }
            if (valor.compareTo(BigDecimal.ZERO) != 0) {
                listaEventosBase.add(new MemoriaCalculoRHDTO(codigoBase, eventoFP, TipoMemoriaCalculo.BASEFP, opItem, valor));
            }
        }

        if (LOG_BASE) logger.debug("Total da Base Pensão Alimentícia " + codigoBase + " sem retroativo: " + soma);
        if (!isCalculandoRetroativo()) {
            BigDecimal valor = new BigDecimal(funcoesFolhaFacade.valorTotalBaseRetroativos(codigoBase, getEp(), getAno(), getMes(), irrfFicticioParaPensao.get(codigoBase)) + "");
            if (LOG_BASE) logger.debug("Valor do Retroativo da Base Pensão Alimentícia " + codigoBase + " : " + valor);
            if (valor.compareTo(BigDecimal.ZERO) != 0) {
                listaEventosBase.add(new MemoriaCalculoRHDTO(codigoBase, "Valor Retroação Base Pensão Alimentícia", null, TipoMemoriaCalculo.BASEFP, OperacaoFormula.ADICAO, valor));
            }
            soma = soma.add(valor);
        }
        if (TipoFolhaDePagamento.isFolhaRescisao(folhaDePagamento)) {
            BigDecimal valorBaseMes = new BigDecimal(funcoesFolhaFacade.valorTotalBaseParaRescisao(codigoBase, getEp(), getAno(), getMes(), new ArrayList<>(getEventoValorFormula().keySet())) + "");
            soma = soma.add(valorBaseMes);
        }
//        }
        if (LOG_BASE) {
            logger.debug("Total da Base " + codigoBase + " : " + soma);
        }
        if (!listaEventosBase.isEmpty() && getFolhaDePagamento().isGravarMemoriaCalculo()) {
            memoriaCalculoEventosBases.put("calculaBasePensaoAlimenticia('" + codigoBase + "')", listaEventosBase);
        }
//        baseValor.put(codigoBase, soma);
        if (LOG_BASE) logger.debug("============================##========================");
        return soma.doubleValue();
    }

    private List<EventoFP> getEventos(List<ItemBaseFP> itensBases) {
        List<EventoFP> eventos = new ArrayList<>();
        for (ItemBaseFP itensBase : itensBases) {
            eventos.add(itensBase.getEventoFP());
        }
        return eventos;
    }

    public boolean isCalculandoRetroativo() {
        return ep.isCalculandoRetroativo();
    }

    private OperacaoFormula getDeParaOperacaoFormula(ItemBaseFP item, Map<EventoFP, TipoEventoFP> mapTipoEventoCalculado) {
        if (eventoEstornoValor.containsKey(item.getEventoFP())) {
            TipoEventoFP tipo = eventoEstornoValor.get(item.getEventoFP());
            if (tipo.equals(TipoEventoFP.INFORMATIVO)) {
                return item.getOperacaoFormula();
            }
            return tipo.equals(TipoEventoFP.VANTAGEM) ? OperacaoFormula.ADICAO : OperacaoFormula.SUBTRACAO;
        }
        if (mapTipoEventoCalculado != null && mapTipoEventoCalculado.containsKey(item.getEventoFP())) {
            TipoEventoFP tipo = mapTipoEventoCalculado.get(item.getEventoFP());
            if (TipoEventoFP.VANTAGEM.equals(item.getEventoFP().getTipoEventoFP()) && OperacaoFormula.ADICAO.equals(item.getOperacaoFormula())
                && TipoEventoFP.DESCONTO.equals(tipo)) {
                return OperacaoFormula.SUBTRACAO;
            }
            if (TipoEventoFP.DESCONTO.equals(item.getEventoFP().getTipoEventoFP()) && OperacaoFormula.SUBTRACAO.equals(item.getOperacaoFormula())
                && TipoEventoFP.VANTAGEM.equals(tipo)) {
                return OperacaoFormula.ADICAO;
            }
            return item.getOperacaoFormula();
        }
        return item.getOperacaoFormula();
    }

    @DescricaoMetodo("recuperarCodigoBaseSextaParte()") //
    public String recuperarCodigoBaseSextaParte() throws Exception {
        boolean LOGBASE = LOG;
        if (LOGBASE) {
            logger.debug("Buscando base da sexta parte");
        }
        return funcoesFolhaFacade.recuperarBaseSextaParte(ep);
    }

    @DescricaoMetodo("calculaBaseSemRetroativo('codigoBase')") //
    public double calculaBaseSemRetroativo(String codigoBase) throws Exception { //Para ser chamado do JavaScript
        boolean LOGBASE = LOG;
        if (LOGBASE) {
            logger.debug("iniciando o calculo do método calculaBase " + codigoBase);
        }
        if (baseValorSemRetroativo.containsKey(codigoBase)) {
            if (LOGBASE) {
                logger.debug("valor da base dentro do cache: " + codigoBase + " : " + baseValorSemRetroativo.get(codigoBase).doubleValue());
            }
            return baseValorSemRetroativo.get(codigoBase).doubleValue();
        }

        BigDecimal soma = BigDecimal.ZERO;
        List<ItemBaseFP> itensBaseFP = funcoesFolhaFacade.obterItensBaseFP(codigoBase);
        List<MemoriaCalculoRHDTO> listaEventosBase = Lists.newLinkedList();
        for (ItemBaseFP item : itensBaseFP) {
            EventoFP eventoFP = item.getEventoFP();
            BigDecimal valor = BigDecimal.ZERO;
            if (isServidorEVerbaBloqueado(ep, eventoFP)) {
                continue;
            }
            if (getEventoValorFormula().containsKey(eventoFP)) {
                if (TipoFolhaDePagamento.isFolhaRescisao(folhaDePagamento) && eventoPagoNaFolhaNormal(eventoFP)) {
                    folhaDePagamentoFacade.removerEventosJaPagosNaFolhaNormalParaRescisao(this, itemPagosNaFolhaNormal);
                }
                valor = new BigDecimal(getEventoValorFormula().get(eventoFP) + "");
            } else if (eventoFP.getAutomatico() && eventoFP.getAtivo() && avaliaRegra(eventoFP)) { // São calculadas apenas as fórmulas de eventos automáticos
                if (TipoFolhaDePagamento.isFolhaRescisao(folhaDePagamento) && eventoPagoNaFolhaNormal(eventoFP)) {
                    avaliaFormulaDoEventoFP(eventoFP);
                    folhaDePagamentoFacade.removerEventosJaPagosNaFolhaNormalParaRescisao(this, itemPagosNaFolhaNormal);
                    valor = new BigDecimal(getEventoValorFormula().get(eventoFP) + "");
                } else {
                    valor = new BigDecimal(avaliaFormulaDoEventoFP(eventoFP) + "");
                }
            } else {
                if (lancamentos.containsKey(eventoFP)) {
                    LancamentoFP lancamento = lancamentos.get(eventoFP);
                    if (lancamento.getTipoLancamentoFP().equals(TipoLancamentoFP.VALOR)) {
                        valor = getValorLancamento(lancamento, eventoFP);
                    } else {
                        if (lancamento.getTipoLancamentoFP().equals(TipoLancamentoFP.REFERENCIA) && lancamento.getBaseFP() != null) {
                            valor = calculaBaseSobreParcentualDeLancamentofp(this, lancamento, item);
                        } else {
                            valor = avaliaFormulaDoEventoFP(eventoFP);
                        }
                    }
                } else {
                    continue;
                }
            }
            OperacaoFormula opItem = getDeParaOperacaoFormula(item, null);
            if (opItem.equals(OperacaoFormula.ADICAO)) {
                soma = soma.add(valor);
            } else {
                soma = soma.subtract(valor);
            }
            if (LOGBASE) {
                logger.debug("valor do evento " + eventoFP + ": " + valor + " dentro da base sem retroativo: " + codigoBase);
            }
            if (valor.compareTo(BigDecimal.ZERO) != 0) {
                listaEventosBase.add(new MemoriaCalculoRHDTO(codigoBase, eventoFP, TipoMemoriaCalculo.BASEFP, opItem, valor));
            }
        }
        if (LOGBASE) logger.debug("Total da Base Sem Retroativo " + codigoBase + " sem retroativo: " + soma);

        if (TipoFolhaDePagamento.isFolhaRescisao(folhaDePagamento) || TipoFolhaDePagamento.isFolhaAdiantamento13Salario(folhaDePagamento)) {
            BigDecimal valorBaseMes = new BigDecimal(funcoesFolhaFacade.valorTotalBaseParaRescisao(codigoBase, getEp(), getAno(), getMes(), new ArrayList<>(getEventoValorFormula().keySet())) + "");
            soma = soma.add(valorBaseMes);
            if (valorBaseMes.compareTo(BigDecimal.ZERO) != 0) {
                listaEventosBase.add(new MemoriaCalculoRHDTO(codigoBase, "Valor Total Base Retroação", null, TipoMemoriaCalculo.BASEFP, OperacaoFormula.ADICAO, valorBaseMes));
            }
        }

        if (LOGBASE) {
            logger.debug("Total da Base Sem Retroativo " + codigoBase + " : " + soma);
        }
        if (!listaEventosBase.isEmpty() && getFolhaDePagamento().isGravarMemoriaCalculo()) {
            memoriaCalculoEventosBases.put("calculaBaseSemRetroativo('" + codigoBase + "')", listaEventosBase);
        }
        baseValorSemRetroativo.put(codigoBase, soma);
        return soma.doubleValue();
    }

    @DescricaoMetodo("imprimir(descricao, variavel)")
    public void imprimir(String descricao, String variavel) {
        if (descricao != null) {
            logger.debug(descricao + " [{}]", variavel);
        } else {
            logger.debug("[{}]", variavel);
        }
    }

    @DescricaoMetodo("calculaBaseSemRetroativoMesCompleto('codigoBase')") //
    public double calculaBaseSemRetroativoMesCompleto(String codigoBase) throws Exception { //Para ser chamado do JavaScript
        if (baseValor.containsKey(codigoBase)) {
            if (LOG) {
                logger.debug("valor da base: " + codigoBase + " : " + baseValor.get(codigoBase).doubleValue());
            }
            return baseValor.get(codigoBase).doubleValue();
        }
        Double diasTrabalhados = diasTrabalhados();
        Double diasTrabalhadosFG = diasTrabalhadosFG();
        defineDiasTrabalhadosComoMesCompleto();
        if (LOG) {
            logger.debug("iniciando o calculo do método calculaBase " + codigoBase);
        }

        BigDecimal soma = BigDecimal.ZERO;
        List<ItemBaseFP> itensBaseFP = funcoesFolhaFacade.obterItensBaseFP(codigoBase);
        List<MemoriaCalculoRHDTO> listaEventosBase = Lists.newLinkedList();

        for (ItemBaseFP item : itensBaseFP) {
            EventoFP eventoFP = item.getEventoFP();
            BigDecimal valor = BigDecimal.ZERO;
            if (isServidorEVerbaBloqueado(ep, eventoFP)) {
                continue;
            }
            LancamentoFP lancamento = lancamentos.get(eventoFP);
            if (lancamento != null) {
                if (lancamento.getTipoLancamentoFP().equals(TipoLancamentoFP.VALOR)) {
                    if (item.getTipoValor().equals(TipoValor.INTEGRAL)) {
                        valor = new BigDecimal(lancamento.getQuantificacao() + "");
                    } else {
                        BigDecimal valorDoLancamento = getEventoValorFormula().get(eventoFP);
                        if (valorDoLancamento != null) {
                            valor = new BigDecimal(valorDoLancamento + "");
                        }
                    }
                } else {
                    if (item.getTipoValor().equals(TipoValor.INTEGRAL)) {
                        if (lancamento.getTipoLancamentoFP().equals(TipoLancamentoFP.REFERENCIA) && lancamento.getBaseFP() != null) {
                            valor = calculaBaseSobreParcentualDeLancamentofp(this, lancamento, item);
                        } else {
                            valor = new BigDecimal(avaliaFormula(eventoFP) + "");
                        }
                    } else {
                        if (getEventoValorFormula().containsKey(eventoFP)) {
                            valor = new BigDecimal(getEventoValorFormula().get(eventoFP) + "");
                        } else {
                            if (lancamento.getTipoLancamentoFP().equals(TipoLancamentoFP.REFERENCIA) && lancamento.getBaseFP() != null) {
                                valor = calculaBaseSobreParcentualDeLancamentofp(this, lancamento, item);
                            } else {
                                valor = new BigDecimal(avaliaFormula(eventoFP) + "");
                            }
                        }
                    }
                }
            } else if (eventoFP.getAtivo() && avaliaRegra(eventoFP)) { // São calculadas apenas as fórmulas de eventos automáticos
                if (item.getTipoValor().equals(TipoValor.NORMAL)) {
                    defineValoresOriginaisParaDiasTrabalhados(diasTrabalhados, diasTrabalhadosFG);
                    valor = new BigDecimal(avaliaFormula(eventoFP) + "");
                } else {
                    defineDiasTrabalhadosComoMesCompleto();
                    valor = new BigDecimal(avaliaFormula(eventoFP) + "");
                }
            } else {
                continue;
            }
            OperacaoFormula opItem = getDeParaOperacaoFormula(item, null);
            if (opItem.equals(OperacaoFormula.ADICAO)) {
                soma = soma.add(valor);
            } else {
                soma = soma.subtract(valor);
            }
            if (valor.compareTo(BigDecimal.ZERO) != 0) {
                listaEventosBase.add(new MemoriaCalculoRHDTO(codigoBase, eventoFP, TipoMemoriaCalculo.BASEFP, opItem, valor));
            }
            defineDiasTrabalhadosComoMesCompleto();
        }
        if (!isCalculandoRetroativo()) {
            BigDecimal valorRetroativoBase = getValorRetroativoBase(codigoBase, getEp(), getAno(), getMes());
            soma = soma.add(valorRetroativoBase);
            if (valorRetroativoBase.compareTo(BigDecimal.ZERO) != 0) {
                listaEventosBase.add(new MemoriaCalculoRHDTO(codigoBase, "Valor Retroação", null, TipoMemoriaCalculo.BASEFP, OperacaoFormula.ADICAO, valorRetroativoBase));
            }
        }
        if (LOG) {
            logger.debug("valor da base " + codigoBase + " : " + soma);
        }
//        baseValor.put(codigoBase, soma);
        if (!listaEventosBase.isEmpty() && getFolhaDePagamento().isGravarMemoriaCalculo()) {
            memoriaCalculoEventosBases.put("calculaBaseSemRetroativoMesCompleto('" + codigoBase + "')", listaEventosBase);
        }
        defineValoresOriginaisParaDiasTrabalhados(diasTrabalhados, diasTrabalhadosFG);
        return soma.doubleValue();
    }

    public BigDecimal getValorRetroativoBase(String codigoBase, EntidadePagavelRH ep, Integer ano, Integer mes) {
        return new BigDecimal(funcoesFolhaFacade.valorTotalBaseRetroativos(codigoBase, ep, ano, mes, irrfFicticioParaPensao.get(codigoBase)) + "");
    }

    private void defineValoresOriginaisParaDiasTrabalhados(Double diasTrabalhados, Double diasTrabalhadosFG) {
        valorMetodo.put("diasTrabalhados", diasTrabalhados);
        valorMetodo.put("diasTrabalhadosFG", diasTrabalhadosFG);
    }

    private void defineDiasTrabalhadosComoMesCompleto() {
        if (diasTrabalhados() != obterDiasDoMes()) {
            valorMetodo.put("diasTrabalhados", obterDiasDoMes());
        }
        if (diasTrabalhadosFG() != obterDiasDoMes()) {
            valorMetodo.put("diasTrabalhadosFG", obterDiasDoMes());
        }
    }

    public boolean isLancamento(EventoFP eventoFP) {
        return funcoesFolhaFacade.isLancamentosValidoPorTipo(getEp(), eventoFP, getEp().getMes(), getEp().getAno(), getEp().getFolha().getTipoFolhaDePagamento(), detalheProcessamentoFolha.getItemCalendarioFP().getDataUltimoDiaProcessamento());
    }

    public LancamentoFP getLancamento(EventoFP eventoFP) {
        return funcoesFolhaFacade.getLancamentosValidoPorTipo(getEp(), eventoFP, getEp().getMes(), getEp().getAno(), getEp().getFolha().getTipoFolhaDePagamento(), dateToLocalDate(detalheProcessamentoFolha.getItemCalendarioFP().getDataUltimoDiaProcessamento()));
    }

    @DescricaoMetodo("calculaBaseMultiplosVinculos(codigoBase)")
    public double calculaBaseMultiplosVinculos(String codigoBase) throws Exception { //Para ser chamado do JavaScript
        if (baseMultVincValor.containsKey(codigoBase)) {
            if (!IdentificacaoEventoFP.PENSAO_ALIMENTICIA.equals(eventoCalculandoAgora.getIdentificacaoEventoFP())) {
                return baseMultVincValor.get(codigoBase).doubleValue();
            }
        }

        BigDecimal soma = new BigDecimal(calculaBase(codigoBase) + "");
        BigDecimal valorOutrosVinculos = new BigDecimal(funcoesFolhaFacade.valorTotalBaseOutrosVinculos(codigoBase, getEp(), getAno(), getMes(), false, this.folhaDePagamento.getTipoFolhaDePagamento()) + "");
        soma = soma.add(valorOutrosVinculos);

        List<MemoriaCalculoRHDTO> memoriaCalculoRHDTOS = getMemoriaCalculoEventosBases().get("calculaBase('" + codigoBase + "')");
        if (memoriaCalculoRHDTOS != null && !memoriaCalculoRHDTOS.isEmpty()) {
            if (valorOutrosVinculos.compareTo(BigDecimal.ZERO) != 0) {
                memoriaCalculoRHDTOS.add(new MemoriaCalculoRHDTO(codigoBase, "Valor outros vinculos", null, TipoMemoriaCalculo.BASEFP, valorOutrosVinculos.compareTo(BigDecimal.ZERO) > 0 ? OperacaoFormula.ADICAO : OperacaoFormula.SUBTRACAO, valorOutrosVinculos));
            }
            getMemoriaCalculoEventosBases().put("calculaBaseMultiplosVinculos('" + codigoBase + "')", memoriaCalculoRHDTOS);
        }

        baseMultVincValor.put(codigoBase, soma);
        return soma.doubleValue();
    }

    @DescricaoMetodo("calculaBaseMultiplosVinculosIR(codigoBase)")
    public double calculaBaseMultiplosVinculosIR(String codigoBase) throws Exception { //Para ser chamado do JavaScript
        if (baseMultVincValorIR.containsKey(codigoBase)) {
            if (!IdentificacaoEventoFP.PENSAO_ALIMENTICIA.equals(eventoCalculandoAgora.getIdentificacaoEventoFP())) {
                return baseMultVincValorIR.get(codigoBase).doubleValue();
            }
        }

        BigDecimal soma = new BigDecimal(calculaBase(codigoBase) + "");

        BigDecimal valorOutrosVinculos = BigDecimal.ZERO;
        if (!ep.getPrimeiroContrato()) {
            valorOutrosVinculos = new BigDecimal(funcoesFolhaFacade.valorTotalBaseOutrosVinculos(codigoBase, getEp(), getAno(), getMes(), true, this.folhaDePagamento.getTipoFolhaDePagamento()) + "");
            soma = soma.add(valorOutrosVinculos);
        }
        List<MemoriaCalculoRHDTO> memoriaCalculoRHDTOS = getMemoriaCalculoEventosBases().get("calculaBase('" + codigoBase + "')");
        if (memoriaCalculoRHDTOS != null && !memoriaCalculoRHDTOS.isEmpty()) {
            if (valorOutrosVinculos.compareTo(BigDecimal.ZERO) != 0) {
                memoriaCalculoRHDTOS.add(new MemoriaCalculoRHDTO(codigoBase, "Valor outros vinculos IR", null, TipoMemoriaCalculo.BASEFP, valorOutrosVinculos.compareTo(BigDecimal.ZERO) > 0 ? OperacaoFormula.ADICAO : OperacaoFormula.SUBTRACAO, valorOutrosVinculos));
            }
            getMemoriaCalculoEventosBases().put("calculaBaseMultiplosVinculosIR('" + codigoBase + "')", memoriaCalculoRHDTOS);
        }

        baseMultVincValorIR.put(codigoBase, soma);
        return soma.doubleValue();
    }

    @DescricaoMetodo("isEstornoValor()")
    public boolean isEstornoValor() { //Para ser chamado do JavaScript
        String metodo = "isEstornoValor";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean isEstornoValor = funcoesFolhaFacade.caracterizaDevolucaoDeValor(ep, obterDiasDoMes());
        valorMetodo.put(metodo, isEstornoValor);
        return isEstornoValor;
    }

    //    @DescricaoMetodo("temEventoOutraFolhaNoMesmoMesParaRescisao()")
    public boolean temEventoOutraFolhaNoMesmoMesParaRescisao() { //Para ser chamado do JavaScript
        String metodo = "temEventoOutraFolhaNoMesmoMesParaRescisao";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean isEstornoValor = funcoesFolhaFacade.temEventosPagosEmOutraFolhaNoMesmoMesParaRescisao(this, ep, detalheProcessamentoFolha);
        valorMetodo.put(metodo, isEstornoValor);
        return isEstornoValor;
    }

    public boolean temEventoOutraFolhaNoMesmoMesFolhaNormal() {
        String metodo = "temEventoOutraFolhaNoMesmoMesFolhaNormal";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean isEstornoValor = !funcoesFolhaFacade.eventosPagosEmOutraFolhaNoMesmoMes(ep).isEmpty();
        valorMetodo.put(metodo, isEstornoValor);
        return isEstornoValor;
    }

    public boolean hasEventoOutraFolhaNoMesmoMesFolhaNormalComSaldo() {
        String metodo = "hasEventoOutraFolhaNoMesmoMesFolhaNormalComSaldo";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean isEstornoValor = !funcoesFolhaFacade.eventosPagosEmOutraFolhaNoMesmoMesComSaldo(ep);
        valorMetodo.put(metodo, isEstornoValor);
        return isEstornoValor;
    }

    @DescricaoMetodo("diasDeDevolucao()")
    public int diasDeDevolucao() { //Para ser chamado do JavaScript
        String metodo = "diasDeDevolucao";
        if (valorMetodo.containsKey(metodo)) {
            return (int) valorMetodo.get(metodo);
        }
        int diasDeDevolucao = funcoesFolhaFacade.diasDeDevolucao(ep, obterDiasDoMes());
        valorMetodo.put(metodo, diasDeDevolucao);
        return diasDeDevolucao;
    }

    @DescricaoMetodo("salarioBase()")
    public Double salarioBase() {
        String metodo = "salarioBase";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.salarioBase(getEp());
        if (ep instanceof Pensionista) {
            valor = valor / obterNumeroCotas();
        }
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("salarioBaseServidorReferenciaFG(tipoFG)")
    public Double salarioBaseServidorReferenciaFG(String tipoFG) {
        String metodo = "salarioBaseServidorReferenciaFG" + tipoFG;
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = 0.0;
        if (TipoFuncaoGratificada.GESTOR_RECURSOS.name().equals(tipoFG) || TipoFuncaoGratificada.CONTROLE_INTERNO.name().equals(tipoFG)) {

            TipoFuncaoGratificada tipoBusca = TipoFuncaoGratificada.valueOf(tipoFG);

            FuncaoGratificada funcaoGratificada = funcoesFolhaFacade.buscarFuncaoGratificada(tipoBusca, (VinculoFP) getEp(), getDataReferencia());

            if (funcaoGratificada != null) {
                EntidadePagavelRH contratoFPRef = funcaoGratificada.getContratoFPRef();
                contratoFPRef.setAno(ep.getAno());
                contratoFPRef.setMes(ep.getMes());
                contratoFPRef.setFolha(ep.getFolha());
                valor = funcoesFolhaFacade.salarioBase(contratoFPRef);
            }
        }

        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("valorRetroativo('Codigo da Verba')")
    public Double valorRetroativo(String codigo) {
        String metodo = "valorRetroativo" + codigo;
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.valorTotalEventoRetroativos(codigo, getEp(), getAno(), getMes());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("salarioBaseUltimo()")
    public Double salarioBaseUltimo() {
        String metodo = "salarioBaseUltimo";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.salarioBaseContratoServidor(ep, dateToLocalDate(getDataReferencia()), dateToLocalDate(getDataReferencia()));
        if (ep instanceof Pensionista) {
            valor = valor / obterNumeroCotas();
        }
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("mesFinalVigencia()")
    public Integer mesFinalVigencia() {
        String metodo = "mesFinalVigencia()";
        if (valorMetodo.containsKey(metodo)) {
            return (Integer) valorMetodo.get(metodo);
        }
        Date finalVigencia = ep.getFinalVigencia();
        Integer mesFinalVigencia = -1;
        if (finalVigencia != null) {
            mesFinalVigencia = new DateTime(finalVigencia).getMonthOfYear();
        }

        valorMetodo.put(metodo, mesFinalVigencia);
        return mesFinalVigencia;
    }

    @DescricaoMetodo("anoFinalVigencia()")
    public Integer anoFinalVigencia() {
        String metodo = "anoFinalVigencia()";
        if (valorMetodo.containsKey(metodo)) {
            return (Integer) valorMetodo.get(metodo);
        }
        Date finalVigencia = ep.getFinalVigencia();
        Integer anoFinalVigencia = -1;
        if (finalVigencia != null) {
            anoFinalVigencia = new DateTime(finalVigencia).getYear();
        }
        valorMetodo.put(metodo, anoFinalVigencia);
        return anoFinalVigencia;
    }


    @DescricaoMetodo("mesInicioVigenciaVinculo()")
    public Integer mesInicioVigenciaVinculo() {
        String metodo = "mesInicioVigenciaVinculo()";
        if (valorMetodo.containsKey(metodo)) {
            return (Integer) valorMetodo.get(metodo);
        }
        LocalDate inicioVigencia = DataUtil.dateToLocalDate(((VinculoFP) ep).getInicioVigencia());
        Integer mesInicioVigencia = inicioVigencia.getMonthValue();

        valorMetodo.put(metodo, mesInicioVigencia);
        return mesInicioVigencia;
    }

    @DescricaoMetodo("anoInicioVigenciaVinculo()")
    public Integer anoInicioVigenciaVinculo() {
        String metodo = "anoInicioVigenciaVinculo()";
        if (valorMetodo.containsKey(metodo)) {
            return (Integer) valorMetodo.get(metodo);
        }
        LocalDate inicioVigencia = DataUtil.dateToLocalDate(((VinculoFP) ep).getInicioVigencia());
        Integer anoFinalVigencia = inicioVigencia.getYear();

        valorMetodo.put(metodo, anoFinalVigencia);
        return anoFinalVigencia;
    }

    @DescricaoMetodo("obterTipoRegime().codigo")
    public TipoRegime obterTipoRegime() {
        String metodo = "obterTipoRegime";
        if (valorMetodo.containsKey(metodo)) {
            return (TipoRegime) valorMetodo.get(metodo);
        }

        TipoRegime valor = funcoesFolhaFacade.obterTipoRegime(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterDiasDoMes()")
    public Double obterDiasDoMes() {
        String metodo = "obterDiasDoMes";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Integer valor = getDataReferenciaJoda().dayOfMonth().getMaximumValue();
        valorMetodo.put(metodo, valor.doubleValue());
        return valor.doubleValue();
    }

    @DescricaoMetodo("obterModalidadeBeneficioFP()")
    public ModalidadeBeneficioFP obterModalidadeBeneficioFP() {
        String metodo = "obterModalidadeBeneficioFP";
        if (valorMetodo.containsKey(metodo)) {
            return (ModalidadeBeneficioFP) valorMetodo.get(metodo);
        }

        ModalidadeBeneficioFP valor = funcoesFolhaFacade.obterModalidadeBeneficioFP(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterValorBeneficioFP()")
    public Double obterValorBeneficioFP() {
        String metodo = "obterValorBeneficioFP";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.obterValorBeneficioFP(getEp(), getMes(), getAno());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("diasTrabalhados()")
    public Double diasTrabalhados() {
        String metodo = "diasTrabalhados";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.diasTrabalhados(getEp(), getMes(), getAno(), TipoDiasTrabalhados.NORMAL, null, null, null);
        addLog(metodo, valor);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("diasTrabalhadosFG()")
    public Double diasTrabalhadosFG() {
        String metodo = "diasTrabalhadosFG";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = diasTrabalhadosFGPorTipo("NORMAL");
        addLog(metodo, valor);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("diasTrabalhadosFGPorTipo('tipoFuncao')")
    public Double diasTrabalhadosFGPorTipo(String tipoFuncao) {
        String metodo = "diasTrabalhadosFG" + tipoFuncao;
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.diasTrabalhados(getEp(), getMes(), getAno(), TipoDiasTrabalhados.FG, tipoFuncao, null, null);
        addLog(metodo, valor);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("diasTrabalhadosCC()")
    public Double diasTrabalhadosCC() {
        String metodo = "diasTrabalhadosCC";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        Double valor = funcoesFolhaFacade.diasTrabalhados(getEp(), getMes(), getAno(), TipoDiasTrabalhados.CC, null, null, null);
        addLog(metodo, valor);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("diasTrabalhadosAS()")
    public Double diasTrabalhadosAS() {
        String metodo = "diasTrabalhadosAS";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        Double valor = funcoesFolhaFacade.diasTrabalhados(getEp(), getMes(), getAno(), TipoDiasTrabalhados.AS, null, null, null);
        addLog(metodo, valor);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterItensBaseFP(codigoBase)")
    public ItemBaseFP[] obterItensBaseFP(String codigoBase) {
        String metodo = "obterItensBaseFP".concat(codigoBase);
        if (valorMetodo.containsKey(metodo)) {
            return (ItemBaseFP[]) valorMetodo.get(metodo);
        }
        ItemBaseFP[] obterItens = {};
        ItemBaseFP[] valor = funcoesFolhaFacade.obterItensBaseFP(codigoBase).toArray(obterItens);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterTipoPrevidenciaFP()")
    public String obterTipoPrevidenciaFP() {
        String metodo = "obterTipoPrevidenciaFP";
        if (valorMetodo.containsKey(metodo)) {
            return (String) valorMetodo.get(metodo);
        }

        String valor = funcoesFolhaFacade.obterTipoPrevidenciaFP(getEp(), getMes(), getAno());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterReferenciaFaixaFP(codigoReferencia, valorRef)")
    public ReferenciaFPFuncao obterReferenciaFaixaFP(String codigoReferencia, Double valorRef) {
        String metodo = "obterReferenciaFaixaFP".concat(codigoReferencia).concat(valorRef.toString());
        if (valorMetodo.containsKey(metodo)) {
            return (ReferenciaFPFuncao) valorMetodo.get(metodo);
        }

        ReferenciaFPFuncao valor = funcoesFolhaFacade.obterReferenciaFaixaFP(ep, codigoReferencia, valorRef, getMes(), getAno());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterReferenciaValorFP(codigoReferencia)")
    public ReferenciaFPFuncao obterReferenciaValorFP(String codigoReferencia) {
        String metodo = "obterReferenciaValorFP".concat(codigoReferencia);
        if (valorMetodo.containsKey(metodo)) {
            return (ReferenciaFPFuncao) valorMetodo.get(metodo);
        }

        ReferenciaFPFuncao valor = funcoesFolhaFacade.obterReferenciaValorFP(ep, codigoReferencia, getMes(), getAno());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("recuperaQuantificacaoLancamentoTipoReferencia(codigoEvento)")
    public Double recuperaQuantificacaoLancamentoTipoReferencia(String codigoEvento) {
        String metodo = "recuperaQuantificacaoLancamentoTipoReferencia".concat(codigoEvento);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.recuperaQuantificacaoLancamentoTipoReferenciaExcetoLancamentosCalculados(getEp(), codigoEvento, lancamentosReferenciaJaCalculados.get(codigoEvento));
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("truncarResultado(Base, Percentual)")
    public Double truncarResultado(Double base, Double percentual) {
        String metodo = "truncarResultado".concat(base.toString() + " " + percentual.toString());
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.truncarResultado(BigDecimal.valueOf(base), BigDecimal.valueOf(percentual)).doubleValue();
        valorMetodo.put(metodo, valor);
        return valor;
    }

    public Map<String, Object> getValorMetodo() {
        return valorMetodo;
    }

    @DescricaoMetodo("recuperaQuantificacaoLancamentoTipoValor(codigoEvento)")
    public Double recuperaQuantificacaoLancamentoTipoValor(String codigoEvento) {
        String metodo = "recuperaQuantificacaoLancamentoTipoValor".concat(codigoEvento);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.recuperaQuantificacaoLancamentoTipoValor(getEp(), codigoEvento, getMes(), getAno());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterHoraMensal()")
    public Double obterHoraMensal() {
        String metodo = "obterHoraMensal";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.obterHoraMensal(getEp());

        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("temSindicato(1,2,3,4...)")
    public boolean temSindicato(Long... codigoSindicato) {
        String metodo = "temSindicato".concat(codigoSindicato + "");
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.temSindicato(getEp(), codigoSindicato);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterNumeroDependentes(codigoTipoDepend)")
    public int obterNumeroDependentes(String codigoTipoDepend) {
        String metodo = "obterNumeroDependentes".concat(codigoTipoDepend);
        if (valorMetodo.containsKey(metodo)) {
            return (int) valorMetodo.get(metodo);
        }

        Integer valor = funcoesFolhaFacade.obterNumeroDependentes(codigoTipoDepend, getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("temFeriasConcedidas()")
    public boolean temFeriasConcedidas() {
        String metodo = "temFeriasConcedidas";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean valor = false;
        try {
            valor = quantidadeDiasFeriasConcedidas() > 0;
        } catch (FuncoesFolhaFacadeException fx) {
            addItensDetalhesErro((VinculoFP) ep, fx.getMessage());
        }
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("paga13FeriasAut()")
    public boolean paga13FeriasAut() {
        String metodo = "paga13FeriasAut";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean valor = funcoesFolhaFacade.paga13FeriasAut(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("diasSaldo13FeriasAut()")
    public Double diasSaldo13FeriasAut() {
        String metodo = "diasSaldo13FeriasAut";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        Double valor = funcoesFolhaFacade.diasSaldo13FeriasAut(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("quantidadeDiasDireitoBasePA('FERIAS' ou 'LICPREMIO')")
    public Double quantidadeDiasDireitoBasePA(String tipo) {
        String metodo = "quantidadeDiasDireitoBasePA".concat(tipo + "") ;
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        Double valor = funcoesFolhaFacade.quantidadeDiasDireitoBasePA(getEp(), tipo);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("quantidadeDiasFeriasConcedidasNaoLancadasAutomaticamente()")
    public Double quantidadeDiasFeriasConcedidasNaoLancadasAutomaticamente() {
        String metodo = "quantidadeDiasFeriasConcedidasNaoLancadasAutomaticamente";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        Double valor = funcoesFolhaFacade.quantidadeDiasFeriasConcedidasNaoLancadasAutomaticamente(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("tipoPagamento13FeriasAutomatico()")
    public Integer tipoPagamento13FeriasAutomatico() {
        String metodo = "tipoPagamento13FeriasAutomatico";
        if (valorMetodo.containsKey(metodo)) {
            return (Integer) valorMetodo.get(metodo);
        }
        Integer valor = funcoesFolhaFacade.tipoPagamento13FeriasAutomatico(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    public boolean isNaoPagaEventoEmFerias(EventoFP e) {
        String metodo = "isNaoPagaEventoEmFerias" + e.getCodigo();
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = false;
        if (e.getBloqueioFerias() == null) {
            valorMetodo.put(metodo, valor);
            return false;
        }
        if (estaEmFerias() && (e.getBloqueioFerias().equals(BloqueioFerias.NAO_CALCULAVEL) || e.getBloqueioFerias().equals(BloqueioFerias.NAO_CALCULAVEL_NAO_LANCAVEL))) {
            valor = true;
        }
        valorMetodo.put(metodo, valor);
        return valor;
    }

    public boolean isNaoPagaEventoEmLicencaPremio(EventoFP e) {
        String metodo = "isNaoPagaEventoEmLicencaPremio" + e.getCodigo();
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = false;
        if (e.getBloqueioLicencaPremio() == null) {
            valorMetodo.put(metodo, valor);
            return false;
        }
        if (estaEmLicencaPremio() && (BloqueioFerias.NAO_CALCULAVEL.equals(e.getBloqueioLicencaPremio()) || BloqueioFerias.NAO_CALCULAVEL_NAO_LANCAVEL.equals(e.getBloqueioLicencaPremio()))) {
            valor = true;
        }
        valorMetodo.put(metodo, valor);
        return valor;
    }

    public boolean estaEmFerias() {
        String metodo = "estaEmFerias";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean valor = funcoesFolhaFacade.estaEmFeriasOuLicencaEsteMes(ep, TipoPeriodoAquisitivo.FERIAS);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    public boolean estaEmLicencaPremio() {
        String metodo = "estaEmLicencaPremio";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean valor = funcoesFolhaFacade.estaEmFeriasOuLicencaEsteMes(ep, TipoPeriodoAquisitivo.LICENCA);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("totalDiasDireitoFerias()")
    public Double totalDiasDireitoFerias() {
        String metodo = "totalDiasDireitoFerias";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        Double valor = 0.0;
        try {
            valor = funcoesFolhaFacade.totalDiasDireitoFerias(getEp());
        } catch (Exception fx) {
            fx.printStackTrace();
            addItensDetalhesErro((VinculoFP) ep, fx.getMessage());
        }

        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("quantidadeDiasFeriasConcedidas()")
    public Double quantidadeDiasFeriasConcedidas() {
        String metodo = "quantidadeDiasFeriasConcedidas";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        Double valor = 0.0;
        try {
            valor = funcoesFolhaFacade.quantidadeDiasFeriasConcedidas(getEp());
            valorMetodo.put(metodo, valor);
        } catch (FuncoesFolhaFacadeException fx) {
            addItensDetalhesErro((VinculoFP) ep, fx.getMessage());
        }
        return valor;
    }

    @DescricaoMetodo("quantidadeDiasFeriasDobradas()")
    public Double quantidadeDiasFeriasDobradas() {
        String metodo = "quantidadeDiasFeriasDobradas";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        Double valor = funcoesFolhaFacade.quantidadeDiasFeriasDobradas(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("quantidadeDiasFeriasVencidas()")
    public Double quantidadeDiasFeriasVencidas() {
        String metodo = "quantidadeDiasFeriasVencidas";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        Double valor = funcoesFolhaFacade.quantidadeDiasFeriasVencidas(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("quantidadeDiasFeriasProporcional()")
    public Double quantidadeDiasFeriasProporcional() {
        String metodo = "quantidadeDiasFeriasProporcional";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        Double valor = funcoesFolhaFacade.quantidadeDiasFeriasProporcional(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("quantidadePeriodoAquisitivoFeriasDobradas()")
    public Integer quantidadePeriodoAquisitivoFeriasDobradas() {
        String metodo = "quantidadePeriodoAquisitivoFeriasDobradas";
        if (valorMetodo.containsKey(metodo)) {
            return (Integer) valorMetodo.get(metodo);
        }
        Integer valor = funcoesFolhaFacade.quantidadePeriodoAquisitivoFeriasDobradas(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("quantidadePeriodoAquisitivoFeriasVencidas()")
    public Integer quantidadePeriodoAquisitivoFeriasVencidas() {
        String metodo = "quantidadePeriodoAquisitivoFeriasVencidas";
        if (valorMetodo.containsKey(metodo)) {
            return (Integer) valorMetodo.get(metodo);
        }
        Integer valor = funcoesFolhaFacade.quantidadePeriodoAquisitivoFeriasVencidas(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("quantidadeDiasFeriasOuLicencaConcedidasPeloPeriodoDeGozo('FERIAS' ou 'LICENCA')")
    public Double quantidadeDiasFeriasOuLicencaConcedidasPeloPeriodoDeGozo(String tipoPa) {
        String metodo = "quantidadeDiasFeriasOuLicencaConcedidasPeloPeriodoDeGozo".concat(tipoPa);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        Double valor = 0.0;
        try {
            TipoPeriodoAquisitivo tipo = "LICENCA".equalsIgnoreCase(tipoPa.trim()) ? TipoPeriodoAquisitivo.LICENCA : TipoPeriodoAquisitivo.FERIAS;
            valor = funcoesFolhaFacade.quantidadeDiasFeriasOuLicencaConcedidasPeloPeriodoDeGozo(getEp(), tipo, null, null);
            valorMetodo.put(metodo, valor);
        } catch (FuncoesFolhaFacadeException fx) {
            addItensDetalhesErro((VinculoFP) ep, fx.getMessage());
        }
        return valor;
    }

    @DescricaoMetodo("temAbonoPecuniarioFerias()")
    public boolean temAbonoPecuniarioFerias() {
        String metodo = "temAbonoPecuniarioFerias";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = quantidadeDiasAbonoPecuniarioFerias() > 0;
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("quantidadeDiasAbonoPecuniarioFerias()")
    public Double quantidadeDiasAbonoPecuniarioFerias() {
        String metodo = "quantidadeDiasAbonoPecuniarioFerias";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.quantidadeDiasAbonoPecuniarioFerias(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("quantidadeDiasFeriasGozadasNoMes()")
    public Double quantidadeDiasFeriasGozadasNoMes() {
        String metodo = "quantidadeDiasFeriasGozadasNoMes";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        Double valor = 0.0;
        try {
            valor = funcoesFolhaFacade.quantidadeDiasFeriasGozadasNoMes(getEp());
            valorMetodo.put(metodo, valor);
        } catch (FuncoesFolhaFacadeException fx) {
            addItensDetalhesErro((VinculoFP) ep, fx.getMessage());
        }
        return valor;
    }

    @DescricaoMetodo("obterIdadeServidor()")
    public int obterIdadeServidor() {
        String metodo = "obterIdadeServidor";
        if (valorMetodo.containsKey(metodo)) {
            return (int) valorMetodo.get(metodo);
        }

        int valor = funcoesFolhaFacade.obterIdadeServidor(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("recuperarPercentualPorTipoNaturezaAtividade(codigoNaturezaAtividade)")
    public Double recuperarPercentualPorTipoNaturezaAtividade(Integer codigoNaturezaAtividade) {
        String metodo = "recuperarPercentualPorTipoNaturezaAtividade".concat(codigoNaturezaAtividade.toString());
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.recuperarPercentualPorTipoNaturezaAtividade(getEp(), getMes(), getAno(), codigoNaturezaAtividade);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("retornaMenorSalario()")
    public BigDecimal retornaMenorSalario() {
        String metodo = "retornaMenorSalario";
        if (valorMetodo.containsKey(metodo)) {
            return (BigDecimal) valorMetodo.get(metodo);
        }

        BigDecimal valor = funcoesFolhaFacade.retornaMenorSalario();
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("recuperaValorItemSindicato(tipoSindicato)")
    public BigDecimal recuperaValorItemSindicato(Integer tipoSindicato) {
        String metodo = "recuperaValorItemSindicato".concat(tipoSindicato.toString());
        if (valorMetodo.containsKey(metodo)) {
            return (BigDecimal) valorMetodo.get(metodo);
        }

        BigDecimal valor = funcoesFolhaFacade.recuperaValorItemSindicato(getEp(), tipoSindicato);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("recuperaValorItemAssociacao(tipoAssociacao)")
    public BigDecimal recuperaValorItemAssociacao(Integer tipoAssociacao) {
        String metodo = "recuperaValorItemAssociacao".concat(tipoAssociacao.toString());
        if (valorMetodo.containsKey(metodo)) {
            return (BigDecimal) valorMetodo.get(metodo);
        }

        BigDecimal valor = funcoesFolhaFacade.recuperaValorItemAssociacao(getEp(), tipoAssociacao);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("identificaAposentado()")
    public boolean identificaAposentado() {
        String metodo = "identificaAposentado";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.identificaAposentado(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterCodigoSituacaoFuncional()")
    public Long obterCodigoSituacaoFuncional() {
        String metodo = "obterCodigoSituacaoFuncional";
        if (valorMetodo.containsKey(metodo)) {
            return (Long) valorMetodo.get(metodo);
        }
        Long valor = funcoesFolhaFacade.obterCodigoSituacaoFuncional(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("estaAfastado(TipoAfas 1, TipoAfas 2, TipoAfas 3...)")
    public boolean estaAfastado(Integer... codigos) {
        String metodo = "estaAfastado".concat(codigos + "");
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.estaAfastadoPorTipos(getEp(), codigos);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("estaAfastado()")
    public boolean estaAfastado() {
        String metodo = "estaAfastado";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.estaAfastado(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("estaCedido('CONUS' ou 'SONUS')")
    public boolean estaCedido(String tipo) {
        String metodo = "estaCedido".concat(tipo + "");
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.estaCedidoPorTipo(getEp(), tipo);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("estaCedido()")
    public boolean estaCedido() {
        String metodo = "estaCedido";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.estaCedido(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("identificaTipoPeq('tipo')")
    public boolean identificaTipoPeq(String tipo) {
        String metodo = "identificaTipoPeq" + tipo;
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean valor = funcoesFolhaFacade.identificaTipoPeq(getEp(), tipo);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("quantidadeTipoPeq('tipo')")
    public Integer quantidadeTipoPeq(String tipo) {
        String metodo = "quantidadeTipoPeq" + tipo;
        if (valorMetodo.containsKey(metodo)) {
            return (Integer) valorMetodo.get(metodo);
        }
        Integer valor = funcoesFolhaFacade.quantidadeTipoPeq(getEp(), tipo);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("podeCalcularParaAposentado('codigoEvento')")
    public boolean podeCalcularParaAposentado(String codigo) {
        String metodo = "podeCalcularParaAposentado" + codigo;
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.podeCalcularParaAposentado(getEp(), codigo);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("podeCalcularParaPensionista('codigoEvento')")
    public boolean podeCalcularParaPensionista(String codigo) {
        String metodo = "podeCalcularParaPensionista" + codigo;
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.podeCalcularParaPensionista(getEp(), codigo);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterValorAposentadoria(['101','360'])")
    public Double obterValorAposentadoria(String[] eventos) {
        String metodo = "obterValorAposentadoria".concat(Arrays.toString(eventos));
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.obterValorAposentadoria(getEp(), eventos);
        //System.out.println("valor aposentadoria -> " + valor);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("temFuncaoGratificada()")
    public boolean temFuncaoGratificada() {
        return temFuncaoGratificada("NORMAL");
    }

    @DescricaoMetodo("temFuncaoGratificada('tipoFuncao')")
    public boolean temFuncaoGratificada(String tipoFuncao) {
        String metodo = "temFuncaoGratificada" + tipoFuncao;
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean valor;

        if (TipoFuncaoGratificada.GESTOR_RECURSOS.name().equals(tipoFuncao) ||
            TipoFuncaoGratificada.CONTROLE_INTERNO.name().equals(tipoFuncao)) {
            valor = percentualAcessoFG(tipoFuncao) > 0;
        } else {
            valor = obterValorFuncaoGratificadaPorTipo(tipoFuncao) > 0;
        }
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterValorFuncaoGratificada()")
    public Double obterValorFuncaoGratificada() {
        String metodo = "obterValorFuncaoGratificada";
        return obterValorFuncaoGratificadaPorTipo("NORMAL");
    }

    @DescricaoMetodo("obterValorFuncaoGratificadaPorTipo('tipoFuncao')")
    public Double obterValorFuncaoGratificadaPorTipo(String tipoFuncao) {
        String metodo = "obterValorFuncaoGratificada " + tipoFuncao;
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        Double valor = funcoesFolhaFacade.obterValorFuncaoGratificadaPorTipo(getEp(), tipoFuncao);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("identificaPensionista()")
    public boolean identificaPensionista() {
        String metodo = "identificaPensionista";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.identificaPensionista(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterValorPensionista()")
    public Double obterValorPensionista() {
        String metodo = "obterValorPensionista";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.obterValorPensionista(getEp(), null, false);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterValorBasePensionista(codigo do evento)")
    public Double obterValorBasePensionista(String codigoEvento) {
        String metodo = "obterValorBasePensionista"+ codigoEvento;
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.obterValorPensionista(getEp(), codigoEvento, true);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterValorBasePensionista()")
    public Double obterValorBasePensionista() {
        String metodo = "obterValorBasePensionista";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.obterValorPensionista(getEp(), null, true);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterReferenciaPensionista(codigo do evento)")
    public Double obterReferenciaPensionista(String codigoEvento) {
        String metodo = "obterReferenciaPensionista" + codigoEvento;
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.obterReferenciaPensionista(getEp(), getMes(), getAno(), codigoEvento);
        addLog(metodo, valor);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterValorPensionista(codigo do evento)")
    public Double obterValorPensionista(String codigoEvento) {
        String metodo = "obterValorPensionista";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.obterValorPensionista(getEp(), codigoEvento, false);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterValorPensionista('Modo', 'Códigos dos Eventos')")
    public Double obterValorPensionista(String modo, String... codigosEvento) {
        String metodo = "obterValorPensionista".concat(Arrays.toString(codigosEvento));
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.obterValorPensionista(getEp(), false, modo, codigosEvento);
        valorMetodo.put(metodo, valor);
        return valor;
    }


    @DescricaoMetodo("calculaBaseInstituidorPensaoPrev()")
    public Double calculaBaseInstituidorPensaoPrev() {
        String metodo = "calculaBaseInstituidorPensaoPrev";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.calculaBaseInstituidorPensaoPrev(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("temLocalInsalubre()")
    public boolean temLocalInsalubre() {
        String metodo = "temLocalInsalubre";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = obterValorLocalInsalubre() > 0;
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterValorLocalInsalubre()")
    public Double obterValorLocalInsalubre() {
        String metodo = "obterValorLocalInsalubre";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.obterValorLocalInsalubre(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("temLocalPenoso()")
    public boolean temLocalPenoso() {
        String metodo = "temLocalPenoso";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = obterValorLocalPenosidade() > 0;
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterValorLocalPenosidade()")
    public Double obterValorLocalPenosidade() {
        String metodo = "obterValorLocalPenosidade".concat(getEp().getIdCalculo() + "");
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.obterValorLocalPenosidade(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("temLocalPericuloso()")
    public boolean temLocalPericuloso() {
        String metodo = "temLocalPericuloso".concat(getEp().getIdCalculo() + "");
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = obterValorLocalPericuloso() > 0;
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterValorLocalPericuloso()")
    public Double obterValorLocalPericuloso() {
        String metodo = "obterValorLocalPenosidade".concat(getEp().getIdCalculo() + "");
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.obterValorLocalPericuloso(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("temPensaoJudicial()")
    public boolean temPensaoJudicial() {
        String metodo = "temPensaoJudicial".concat(getEp().getIdCalculo() + "").concat(eventoCalculandoAgora.getCodigo()) + "";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.temPensaoJudicial(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterParametroPensaoJudicial()")
    public Double obterParametroPensaoJudicial() {
        String metodo = "obterParametroPensaoJudicial".concat(getEp().getIdCalculo() + "".concat(eventoCalculandoAgora.getCodigo()));
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.obterParametroPensaoJudicial(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("temPensaoAlimenticia(codigoEvento)")
    public boolean temPensaoAlimenticia(String codigoEvento) throws Exception {
        String metodo = "temPensaoAlimenticia".concat(getEp().getIdCalculo() + "" + codigoEvento);
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean valor = funcoesFolhaFacade.temPensaoAlimenticia(getEp(), codigoEvento);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("temPensaoAlimenticiaEIrrfFicticio(codigoEvento)")
    public boolean temPensaoAlimenticiaEIrrfFicticio(String codigoEvento) throws Exception {
        String metodo = "temPensaoAlimenticiaEIrrfFicticio".concat(getEp().getIdCalculo() + "" + codigoEvento);
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean valor = funcoesFolhaFacade.temPensaoAlimenticiaEIrrfFicticio(getEp(), codigoEvento);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("recuperaTipoFolha()")
    public String recuperaTipoFolha() {
        String metodo = "recuperaTipoFolha";
        if (valorMetodo.containsKey(metodo)) {
            return (String) valorMetodo.get(metodo);
        }

        String valor = getFolhaDePagamento().getTipoFolhaDePagamento().name();
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterValorPensaoAlimenticia('603')")
    public Double obterValorPensaoAlimenticia(String codigoEvento) throws Exception {
        String metodo = "obterValorPensaoAlimenticia".concat(getEp().getIdCalculo() + "" + codigoEvento);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        double total = 0;
        TipoValorPensaoAlimenticia tipo = recuperaTipoValorPensaoAlimenticia(codigoEvento);
        ValorPensaoAlimenticia valor = recuperarValorBasePensaoAlimenticia(codigoEvento);

        if (tipo != null && valor != null) {
            if (TipoValorPensaoAlimenticia.isTipoBaseOuBaseBruto(tipo)) {
                verificarBasePenaoAlimenticiaSobreIRRFFicticio(valor);
                logger.debug("Calculando base de pensão alimentícia: [{}]", valor.getBaseFP());
                if (!valor.getIrrfFicticio() && !detalheProcessamentoFolha.getCodigosBasesPensaoAlimenticia().contains(valor.getBaseFP().getCodigo())) {
                    zeraValoresImpostoDeRenda();
                }
                double valorBase = calculaBase(valor.getBaseFP().getCodigo());
                total = (valorBase * valor.getValor().doubleValue()) / 100;
                irrfFicticioParaPensao = Maps.newHashMap();
                if (!valor.getIrrfFicticio() && !detalheProcessamentoFolha.getCodigosBasesPensaoAlimenticia().contains(valor.getBaseFP().getCodigo())) {
                    zeraValoresBaseImpostoDeRenda();
                }
                adicionarCodigoPensaoAlimenticia(valor.getBaseFP().getCodigo());
            }
            if (tipo.equals(TipoValorPensaoAlimenticia.PERCENTUAL_SOBRE_SALARIO_MINIMO)) {
                ReferenciaFPFuncao salarioMinimo = funcoesFolhaFacade.obterReferenciaValorFP(ep, CODIGO_REFERENCIAFP_SALARIO_MINIMO, getMes(), getAno());
                total = (salarioMinimo.getValor() * valor.getValor().doubleValue()) / 100;
            }
            if (tipo.equals(TipoValorPensaoAlimenticia.SALARIO_MINIMO_INTEGRAL)) {
                ReferenciaFPFuncao salarioMinimo = funcoesFolhaFacade.obterReferenciaValorFP(ep, CODIGO_REFERENCIAFP_SALARIO_MINIMO, getMes(), getAno());
                total = salarioMinimo.getValor() * (valor.getValor() != null && valor.getValor().compareTo(BigDecimal.ZERO) != 0 ? valor.getValor().doubleValue() : 1);
            }
            if (tipo.equals(TipoValorPensaoAlimenticia.VALOR_FIXO)) {
                total = valor.getValor() != null ? valor.getValor().doubleValue() : BigDecimal.ZERO.doubleValue();
            }
        }

        valorMetodo.put(metodo, total);
        return total;
    }

    public void zeraValoresImpostoDeRenda() {
        List<EventoFP> verbasIRRF = detalheProcessamentoFolha.getEventosImpostoDeRenda();

        for (EventoFP verba : verbasIRRF) {
            if (eventoValorFormula.containsKey(verba)) {
                eventoValorFormula.put(verba, BigDecimal.ZERO);
            }
            if (eventoValorBase.containsKey(verba)) {
                eventoValorBase.put(verba, BigDecimal.ZERO);
            }
        }
    }

    private BaseFP getBaseIRRFPorConfiguracao() {
        if (detalheProcessamentoFolha != null && detalheProcessamentoFolha.getConfiguracaoFP() != null) {
            if (detalheProcessamentoFolha.getConfiguracaoFP().getBaseImpostoRenda() != null) {
                return detalheProcessamentoFolha.getConfiguracaoFP().getBaseImpostoRenda();
            } else {
                logger.debug("Não foi encontrado Base IRRF nas configurações do RH, verifique!");
            }
        }
        return null;
    }

    private void adicionarCodigoPensaoAlimenticia(String codigo) {
        if (!detalheProcessamentoFolha.getCodigosBasesPensaoAlimenticia().contains(codigo)) {
            detalheProcessamentoFolha.getCodigosBasesPensaoAlimenticia().add(codigo);
        }
    }


    /**
     * No caso de pensão Judicial, deve-se calcular novamente o imposto de renda base e evento, sendo assim este método retira do log tais valores.
     */
    public void zeraValoresBaseImpostoDeRenda() {

        zerarBasesIRRF();

        List<EventoFP> verbasIRRF = detalheProcessamentoFolha.getEventosImpostoDeRenda();

        for (EventoFP verba : verbasIRRF) {
            if (eventoValorFormula.containsKey(verba)) {
                eventoValorFormula.remove(verba);
            }

            if (eventoValorBase.containsKey(verba)) {
                eventoValorBase.remove(verba);
            }
        }
    }

    public void zerarBasesIRRF() {
        if (detalheProcessamentoFolha.getConfiguracaoFP().getBaseImpostoRenda() == null) {
            throw new RuntimeException("Não foi encontrada configuração da base de Imposto de Renda, vá em Configurações do RH, Aba Configuração da Folha de Pagamento");
        }
        BaseFP baseImpostoRenda = detalheProcessamentoFolha.getConfiguracaoFP().getBaseImpostoRenda();
        if (baseValor.containsKey(baseImpostoRenda.getCodigo())) {
            baseValor.remove(baseImpostoRenda.getCodigo());
        }
        if (baseMultVincValor.containsKey(baseImpostoRenda.getCodigo())) {
            baseMultVincValor.remove(baseImpostoRenda.getCodigo());
        }
        if (baseMultVincValorIR.containsKey(baseImpostoRenda.getCodigo())) {
            baseMultVincValorIR.remove(baseImpostoRenda.getCodigo());
        }
    }

    public void limparMetodosCache() {
        for (Iterator<Map.Entry<String, Object>> objeto = valorMetodo.entrySet().iterator(); objeto.hasNext(); ) {
            Map.Entry<String, Object> valor = objeto.next();
            if (valor.getKey().contains("obterNumeroDependentes") || valor.getKey().contains("obterReferenciaValorFP3")) {
                objeto.remove();
            }
        }
    }

    @DescricaoMetodo("obterValorBaseDeCalculoPensaoAlimenticia(codigoEvento)")
    public Double obterValorBaseDeCalculoPensaoAlimenticia(String codigoEvento) throws Exception {
        String metodo = "obterValorBaseDeCalculoPensaoAlimenticia".concat(getEp().getIdCalculo() + "" + codigoEvento);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        double total = 0;
        TipoValorPensaoAlimenticia tipo = recuperaTipoValorPensaoAlimenticia(codigoEvento);
        ValorPensaoAlimenticia valor = recuperarValorBasePensaoAlimenticia(codigoEvento);

        if (tipo != null && valor != null) {
            if (TipoValorPensaoAlimenticia.isTipoBaseOuBaseBruto(tipo)) {
                limparCacheParaPensaoAlimenticia(valor);
                double valorBase = calculaBase(valor.getBaseFP().getCodigo());
                total = valorBase;
            }
            if (tipo.equals(TipoValorPensaoAlimenticia.PERCENTUAL_SOBRE_SALARIO_MINIMO)) {
                ReferenciaFPFuncao salarioMinimo = obterReferenciaValorFP(CODIGO_REFERENCIAFP_SALARIO_MINIMO);
                total = salarioMinimo.getValor();
            }
            if (tipo.equals(TipoValorPensaoAlimenticia.SALARIO_MINIMO_INTEGRAL)) {
                ReferenciaFPFuncao salarioMinimo = obterReferenciaValorFP(CODIGO_REFERENCIAFP_SALARIO_MINIMO);
                total = salarioMinimo.getValor();
            }
            if (tipo.equals(TipoValorPensaoAlimenticia.VALOR_FIXO)) {
                total = valor.getValor().doubleValue();
            }
        }
        valorMetodo.put(metodo, total);
        return total;
    }

    private void verificarBasePenaoAlimenticiaSobreIRRFFicticio(ValorPensaoAlimenticia valor) {
        if (valor.getIrrfFicticio()) {
            BaseFP baseIFFP = getBaseIRRFPorConfiguracao();
            if (baseIFFP != null) {
                irrfFicticioParaPensao.put(detalheProcessamentoFolha.getConfiguracaoFP().getBaseImpostoRenda().getCodigo(), funcoesFolhaFacade.obterEventosItensBaseFP(valor.getBaseFP().getCodigo()));
            }
        }
    }

    private void limparCacheParaPensaoAlimenticia(ValorPensaoAlimenticia valor) {
       /* if (baseValor.containsKey(valor.getBaseFP().getCodigo())) {
            baseValor.remove(valor.getBaseFP().getCodigo());
        }
        if (eventoValorReferencia.containsKey(valor.getBeneficioPensaoAlimenticia().getEventoFP())) {
            eventoValorReferencia.remove(valor.getBeneficioPensaoAlimenticia().getEventoFP());
        }*/
    }

    @DescricaoMetodo("obterPercentualPensaoAlimenticia(codigoEvento)")
    public double obterPercentualPensaoAlimenticia(String codigoEvento) throws Exception {
        String metodo = "obterPercentualPensaoAlimenticia".concat(getEp().getIdCalculo() + "" + codigoEvento);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        double total = 0;
        TipoValorPensaoAlimenticia tipo = funcoesFolhaFacade.recuperaTipoValorPensaoAlimenticia(ep, codigoEvento);
        ValorPensaoAlimenticia valor = funcoesFolhaFacade.recuperarValorBasePensaoAlimenticia(ep, codigoEvento);

        if (tipo != null && valor != null) {
            if (TipoValorPensaoAlimenticia.isTipoBaseOuBaseBruto(tipo)) {

                total = valor.getValor().doubleValue();
            }
            if (tipo.equals(TipoValorPensaoAlimenticia.PERCENTUAL_SOBRE_SALARIO_MINIMO)) {
                total = valor.getValor().doubleValue();
            }
            if (tipo.equals(TipoValorPensaoAlimenticia.SALARIO_MINIMO_INTEGRAL)) {
                ReferenciaFPFuncao salarioMinimo = funcoesFolhaFacade.obterReferenciaValorFP(ep, CODIGO_REFERENCIAFP_SALARIO_MINIMO, getMes(), getAno());
                total = salarioMinimo.getValor();
            }
            if (tipo.equals(TipoValorPensaoAlimenticia.VALOR_FIXO)) {
                total = 0;
            }
        }
        valorMetodo.put(metodo, total);
        return total;
    }


    @DescricaoMetodo("temOpcaoValeTransporte()")
    public boolean temOpcaoValeTransporte() {
        String metodo = "temOpcaoValeTransporte".concat(getEp().getIdCalculo() + "");
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.temOpcaoValeTransporte(getEp());
        if (isBeneficioBloqueado(TipoBloqueio.VALE_TRANSPORTE)) {
            valor = false;
        }
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("recuperaPercentualAposentadoria()")
    public double recuperaPercentualAposentadoria() {
        String metodo = "recuperaPercentualAposentadoria";
        if (valorMetodo.containsKey(metodo)) {
            return (double) valorMetodo.get(metodo);
        }
        double valor = 0;
        if (ep instanceof Aposentadoria) {
            valor = ((Aposentadoria) ep).getPercentual() != null ? ((Aposentadoria) ep).getPercentual().doubleValue() : 0;
        }
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("quantidadeValeTransporte()")
    public int quantidadeValeTransporte() {
        String metodo = "quantidadeValeTransporte".concat(getEp().getIdCalculo() + "");
        if (valorMetodo.containsKey(metodo)) {
            return (int) valorMetodo.get(metodo);
        }

        int valor = funcoesFolhaFacade.quantidadeValeTransporte(getEp());
        if (isBeneficioBloqueado(TipoBloqueio.VALE_TRANSPORTE)) {
            valor = 0;
        }
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("recuperaTipoReajusteAposentadoria()")
    public String recuperaTipoReajusteAposentadoria() {
        String metodo = "recuperaTipoReajusteAposentadoria".concat(getEp().getIdCalculo() + "");
        if (valorMetodo.containsKey(metodo)) {
            return (String) valorMetodo.get(metodo);
        }

        String valor = funcoesFolhaFacade.recuperaTipoReajusteAposentadoria(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("recuperaTipoValorPensaoAlimenticia()")
    public TipoValorPensaoAlimenticia recuperaTipoValorPensaoAlimenticia(String codigoEvento) {
        String metodo = "recuperaTipoValorPensaoAlimenticia".concat(getEp().getIdCalculo() + "" + codigoEvento);
        if (valorMetodo.containsKey(metodo)) {
            return (TipoValorPensaoAlimenticia) valorMetodo.get(metodo);
        }

        TipoValorPensaoAlimenticia valor = funcoesFolhaFacade.recuperaTipoValorPensaoAlimenticia(getEp(), codigoEvento);
        if (valor == null) {
            return null;
        }
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("temFaltas(codigoEvento)")
    public boolean temFaltas(String codigoEvento) {
        String metodo = "temFaltas".concat(codigoEvento);
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = recuperaNumeroFaltas(codigoEvento) > 0;
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("recuperaNumeroFaltas(codigoEvento)")
    public double recuperaNumeroFaltas(String codigoEvento) {
        String metodo = "recuperaNumeroFaltas".concat(codigoEvento);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.recuperaNumeroFaltas(getEp(), codigoEvento, detalheProcessamentoFolha);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("recuperarUltimaDataFalta(codigoEvento)")
    public Date recuperarUltimaDataFalta(String codigoEvento) {
        String metodo = "recuperarUltimaDataFalta".concat(codigoEvento);
        if (valorMetodo.containsKey(metodo)) {
            return (Date) valorMetodo.get(metodo);
        }

        Date valor = funcoesFolhaFacade.recuperarUltimaDataFalta(getEp(), codigoEvento);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @Deprecated
    @DescricaoMetodo("recuperarPercentualOpcaoSalarial(codigoEvento)")
    public Double recuperarPercentualOpcaoSalarial(String codigoEvento) {
        String metodo = "recuperarPercentualOpcaoSalarial".concat(codigoEvento);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.recuperarPercentualOpcaoSalarial(codigoEvento);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("recuperarPercentualOpcaoSalarial()")
    public Double recuperarPercentualOpcaoSalarial() {
        String metodo = "recuperarPercentualOpcaoSalarial";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.recuperarPercentualOpcaoSalarial(ep);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("tempoServicoSextaParte(codigoEvento)")
    public Double tempoServicoSextaParte(String codigoEvento) {
        String metodo = "tempoServicoSextaParte".concat(codigoEvento);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.tempoServicoSextaParte(getEp(), codigoEvento);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("recuperarUltimaDataEventoCalculado(codigoEvento)")
    public Date recuperarUltimaDataEventoCalculado(String codigoEvento) {
        String metodo = "recuperarUltimaDataEventoCalculado".concat(codigoEvento);
        if (valorMetodo.containsKey(metodo)) {
            return (Date) valorMetodo.get(metodo);
        }

        Date valor = funcoesFolhaFacade.recuperarUltimaDataEventoCalculado(getEp(), codigoEvento);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("recuperarPercentualIRRF()")
    public Double recuperarPercentualIRRF() {
        String metodo = "recuperarPercentualIRRF";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.recuperarPercentualIRRF(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("temFolhaCalculada('Tipo de Folha', 'Modo de Busca', 'Quantidade de Meses');")
    public boolean temFolhaCalculada(String tipofolha, String modoBusca, Integer meses) {
        ModoBusca modo = ModoBusca.getTipoPorName(modoBusca);
        List<FichaFinanceiraFP> fichas = new ArrayList<>();

        DateTime dateTime = DataUtil.criarDataComMesEAno(Mes.getMesToInt(ep.getMes()).getNumeroMes(), ep.getAno());
        if (meses != null) {
            dateTime = dateTime.minusMonths(meses);
        }
        switch (modo) {
            case SALARIO_13:
                fichas = funcoesFolhaFacade.buscarFichasFinanceirasFPPorAno(getAno(), getEp().getContratoFP(), TipoFolhaDePagamento.valueOf(tipofolha));
                break;
            case ULTIMOSMESES:
                if (meses == 0) {
                    fichas = funcoesFolhaFacade.buscarFichasFinanceirasFPPorAno(getAno(), getEp().getContratoFP(), TipoFolhaDePagamento.valueOf(tipofolha));
                } else {
                    fichas = funcoesFolhaFacade.buscarListaFichaFinanceiraPorVinculoFPMesAnoTipoFolha(getEp().getContratoFP(), dateTime.getMonthOfYear(), dateTime.getYear(), TipoFolhaDePagamento.valueOf(tipofolha));
                }
                break;
            default:
                fichas = funcoesFolhaFacade.buscarListaFichaFinanceiraPorVinculoFPMesAnoTipoFolha(getEp().getContratoFP(), getMes(), getAno(), TipoFolhaDePagamento.valueOf(tipofolha));
        }
        return !fichas.isEmpty();
    }

    @DescricaoMetodo(value = "quantidadeMesesDecimoTerceiroProporcional()", documentacao = "calculador.quantidadeMesesDecimoTerceiroProporcional()")
    public Double quantidadeMesesDecimoTerceiroProporcional() {
        String metodo = "quantidadeMesesDecimoTerceiroProporcional";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.quantidadeMesesTrabalhadosAno(getEp(), true, true);
        valorMetodo.put(metodo, valor);
        return valor;
    }


    @DescricaoMetodo("quantidadeMesesTrabalhadosAno()")
    public Double quantidadeMesesTrabalhadosAno() {
        String metodo = "quantidadeMesesTrabalhadosAno";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.quantidadeMesesTrabalhadosAno(getEp(), false, true);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("mesesTrabalhadosPorAno()")
    public Double mesesTrabalhadosPorAno() {
        String metodo = "mesesTrabalhadosPorAno";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.getQuantidadeMesesTrabalhadosPorAno(getEp()).doubleValue();
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("quantidadeMesesTrabalhadosPorAnoDescontandoFaltaAndAfastamento()")
    public List<Mes> quantidadeMesesTrabalhadosPorAnoDescontandoFaltaAndAfastamento() {
        String metodo = "quantidadeMesesTrabalhadosPorAnoDescontandoFaltaAndAfastamento";
        if (valorMetodo.containsKey(metodo)) {
            return (List<Mes>) valorMetodo.get(metodo);
        }

        List<Mes> valor = funcoesFolhaFacade.quantidadeMesesTrabalhadosPorAnoDescontandoFaltaAndAfastamento(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("quantidadeMesesTrabalhadosPorAno()//pega a quantidade de meses trabalhados descontando dias trabalhados")
    public Double quantidadeMesesTrabalhadosPorAno() {
        String metodo = "quantidadeMesesTrabalhadosPorAno";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.quantidadeMesesTrabalhadosPorAno(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    public Integer quantidadeOcorrenciaEvento(EventoFP eventoFP) {
        Integer valor = funcoesFolhaFacade.quantidadeOcorrenciaEventoByAno(getEp(), eventoFP);
        return valor;
    }

    @DescricaoMetodo("quantidadeOcorrenciaEventoAno(String codigo)")
    public Integer quantidadeOcorrenciaEventoAno(String codigo) {
        EventoFP eventoFP = funcoesFolhaFacade.recuperaEvento(codigo, TipoExecucaoEP.FOLHA);
        Integer valor = funcoesFolhaFacade.quantidadeOcorrenciaEventoByAno(getEp(), eventoFP);
        return valor;
    }

    @DescricaoMetodo("temCargoConfianca()")
    public boolean temCargoConfianca() {
        String metodo = "temCargoConfianca";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean valor = funcoesFolhaFacade.temCargoConfianca(getEp(), getMes(), getAno());
        addLog(metodo, valor);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("temAcessoSubsidio()")
    public boolean temAcessoSubsidio() {
        String metodo = "temAcessoSubsidio";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean valor = funcoesFolhaFacade.temAcessoSubsidio(getEp(), getMes(), getAno());
        addLog(metodo, valor);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("codigoBaseFPCargoConfianca()")
    public String codigoBaseFPCargoConfianca() {
        String metodo = "codigoBaseFPCargoConfianca";
        if (valorMetodo.containsKey(metodo)) {
            return (String) valorMetodo.get(metodo);
        }

        String valor = funcoesFolhaFacade.codigoBaseFPCargoConfianca(getEp(), getMes(), getAno());
        addLog(metodo, valor);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("codigoBaseFPSubsidio()")
    public String codigoBaseFPSubsidio() {
        String metodo = "codigoBaseFPSubsidio";
        if (valorMetodo.containsKey(metodo)) {
            return (String) valorMetodo.get(metodo);
        }

        String valor = funcoesFolhaFacade.codigoBaseFPSubsidio(getEp(), getMes(), getAno());
        addLog(metodo, valor);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    public void addLog(String metodo, Object valor) {
        if (ADICIONA_LOG) {
            detalheProcessamentoFolha.getLogs().append(getMesAnoFormatado() + " " + metodo + " : " + valor + "<br/>");
        }
    }

    public String getMesAnoFormatado() {
        return ep.getMes() + "/" + ep.getAno();
    }

    public YearMonth getMesAnoCalculo() {
        return new YearMonth(ep.getAno(), ep.getMes());
    }

    @DescricaoMetodo("diasUteisMes()")
    public Double diasUteisMes() {
        String metodo = "diasUteisMes";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.diasUteisMes(ep, getMes(), getAno());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("diasDomingosFeriados()")
    public Double diasDomingosFeriados() {
        String metodo = "diasDomingosFeriados";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.diasDomingosFeriados(ep);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("valorCargoComissaoPCS()")
    public Double valorCargoComissaoPCS() {
        String metodo = "valorCargoComissaoPCS";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.valorCargoComissaoPCS(getEp(), getMes(), getAno());
        addLog(metodo, valor);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("valorSubsidioPCS()")
    public Double valorSubsidioPCS() {
        String metodo = "valorSubsidioPCS";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.valorSubsidioPCS(getEp(), getMes(), getAno());
        addLog(metodo, valor);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("optanteFGTS()")
    public boolean optanteFGTS() {
        String metodo = "optanteFGTS";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.optanteFGTS(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("valorTotalEventoOutrosVinculos(codigoEvento)")
    public Double valorTotalEventoOutrosVinculos(String codigoEvento) {
        String metodo = "valorTotalEventoOutrosVinculos".concat(codigoEvento);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.valorTotalEventoOutrosVinculos(codigoEvento, getEp(), getAno(), getMes(), false, this.folhaDePagamento.getTipoFolhaDePagamento(), null);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("valorTotalEventoOutrosVinculos(codigoEvento, 'Tipos de folha...')")
    public Double valorTotalEventoOutrosVinculos(String codigoEvento, String... tiposFolha) {
        String metodo = "valorTotalEventoOutrosVinculos".concat(codigoEvento + Arrays.toString(tiposFolha));
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.valorTotalEventoOutrosVinculos(codigoEvento, getEp(), getAno(), getMes(), false, this.folhaDePagamento.getTipoFolhaDePagamento(), tiposFolha);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("valorTotalEventoOutrosVinculosTransient(codigoEvento)")
    public Double valorTotalEventoOutrosVinculosTransient(String codigoEvento) {
        String metodo = "valorTotalEventoOutrosVinculosTransient".concat(codigoEvento);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = 0.0;
        EventoFP eventoFP = funcoesFolhaFacade.recuperaEvento(codigoEvento, TipoExecucaoEP.FOLHA);
        if (eventoFP != null && contextoTransient != null && contextoTransient.getEventoValorFormula().containsKey(eventoFP)) {
            valor = contextoTransient.getEventoValorFormula().get(eventoFP).doubleValue();
        }

        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("valorTotalEventoOutrosVinculosIR(codigoEvento)")
    public Double valorTotalEventoOutrosVinculosIR(String codigoEvento) {
        String metodo = "valorTotalEventoOutrosVinculosIR".concat(codigoEvento);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.valorTotalEventoOutrosVinculos(codigoEvento, getEp(), getAno(), getMes(), true, this.folhaDePagamento.getTipoFolhaDePagamento(), null);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("valorTotalBaseOutrosVinculos(codigoBase)")
    public Double valorTotalBaseOutrosVinculos(String codigoBase) {
        String metodo = "valorTotalBaseOutrosVinculos".concat(codigoBase);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.valorTotalBaseOutrosVinculos(codigoBase, getEp(), getAno(), getMes(), false, this.folhaDePagamento.getTipoFolhaDePagamento());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("valorTotalItensFolhaNormal(codigoBase)")
    public Double valorTotalItensFolhaNormal(String codigoBase) {
        String metodo = "valorTotalItensFolhaNormal".concat(codigoBase);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = 0.0;
        List<ItemBaseFP> itemBaseFPs = buscarItensBase(codigoBase);
        for (ItemBaseFP itemBaseFP : itemBaseFPs) {
            for (EventoTotalItemFicha eventoTotalItemFicha : getItemPagosNaFolhaNormal()) {
                if (eventoTotalItemFicha.getEvento().getCodigo().equals(itemBaseFP.getEventoFP().getCodigo())) {
                    valor = valor + eventoTotalItemFicha.getTotal().doubleValue();
                }
            }
        }

        BigDecimal valorRetroativo = new BigDecimal(funcoesFolhaFacade.valorTotalBaseRetroativos(codigoBase, getEp(), getAno(), getMes(), irrfFicticioParaPensao.get(codigoBase)) + "");
        valor = valor + valorRetroativo.doubleValue();


        valorMetodo.put(metodo, valor);
        return valor;
    }


    public Double somaEventosPeriodoAquisitivoFeriasConcedidasPorEventoTipoCalculo(List<EventoTipoCaclulo> eventos, List<TipoFolhaDePagamento> tiposFolhaDePagamento) {
        return funcoesFolhaFacade.somaEventosPeriodoAquisitivoFeriasConcedidas(getEp(), getMes(), getAno(), tiposFolhaDePagamento, converterEventosParaStrings(eventos));
    }

    public Double somaEventosPeriodoAquisitivoTercoAutomaticoPorEventoTipoCalculo(List<EventoTipoCaclulo> eventos, List<TipoFolhaDePagamento> tiposFolhaDePagamento) {
        return funcoesFolhaFacade.somaEventosPeriodoAquisitivoTercoAutomatico(getEp(), getMes(), getAno(), tiposFolhaDePagamento, converterEventosParaStrings(eventos));
    }

    @DescricaoMetodo("somaEventosPeriodoAquisitivoFeriasConcedidas(eventos)")
    public Double somaEventosPeriodoAquisitivoFeriasConcedidas(String[] eventos) {
        String metodo = "somaEventosPeriodoAquisitivoFeriasConcedidas".concat(Arrays.toString(eventos));
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.somaEventosPeriodoAquisitivoFeriasConcedidas(getEp(), getMes(), getAno(), null, eventos);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    public Double somaReferenciaEventosNoPeriodoAquisitoFeriasConcedidaPorEventoTipoCalculo(List<EventoTipoCaclulo> eventos, String[] tiposFolha) {
        return funcoesFolhaFacade.somaReferenciaEventosNoPeriodoAquisitoFeriasConcedida(getEp(), getMes(), getAno(), this, tiposFolha, converterEventosParaStrings(eventos));
    }

    public Double somaReferenciaEventosNoPeriodoAquisitoTercoAutomaticoPorEventoTipoCalculo(List<EventoTipoCaclulo> eventos, String[] tiposFolha) {
        return funcoesFolhaFacade.somaReferenciaEventosNoPeriodoAquisitoTercoAutomatico(getEp(), getMes(), getAno(), this, tiposFolha, converterEventosParaStrings(eventos));
    }

    public String[] converterEventosParaStrings(List<EventoTipoCaclulo> eventos) {
        if (eventos == null) {
            return null;
        }
        List<String> eventosString = new ArrayList<>();
        for (EventoTipoCaclulo eventoFP : eventos) {
            eventosString.add(eventoFP.getCodigo());
        }
        String[] eventosArray = new String[eventosString.size()];
        return eventosString.toArray(eventosArray);
    }

    @DescricaoMetodo("somaReferenciaEventosNoPeriodoAquisitoFeriasConcedida(eventos)")
    public Double somaReferenciaEventosNoPeriodoAquisitoFeriasConcedida(String[] eventos) {
        String metodo = "somaReferenciaEventosNoPeriodoAquisitoFeriasConcedida".concat(Arrays.toString(eventos));
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.somaReferenciaEventosNoPeriodoAquisitoFeriasConcedida(getEp(), getMes(), getAno(), this, null, eventos);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("quantidadeReferenciaEventosNoPeriodoAquisitoFeriasConcedida(eventos)")
    public Double quantidadeReferenciaEventosNoPeriodoAquisitoFeriasConcedida(String[] eventos) {
        String metodo = "quantidadeReferenciaEventosNoPeriodoAquisitoFeriasConcedida".concat(Arrays.toString(eventos));
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.quantidadeOcorrenciaEventosNoPeriodoAquisitoFeriasConcedida(getEp(), getMes(), getAno(), this, eventos);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("somarReferenciaHorasPagasNoAno(eventos)")
    public Double somarReferenciaHorasPagasNoAno(String[] eventos) {
        String metodo = "somarReferenciaHorasPagasNoAno".concat(Arrays.toString(eventos));
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.somarReferenciaHorasPagasNoAno(getEp(), getMes(), getAno(), eventos);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("somarValorHorasPagasNoAno(eventos)")
    public Double somarValorHorasPagasNoAno(String[] eventos) {
        String metodo = "somarValorHorasPagasNoAno".concat(Arrays.toString(eventos));
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.somarValorHorasPagasNoAno(getEp(), getMes(), getAno(), eventos);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("somaReferenciaEventosNoPeriodoAquisitoLancamento(eventos)")
    public Double somaReferenciaEventosNoPeriodoAquisitoLancamento(String[] eventos) {
        String metodo = "somaReferenciaEventosNoPeriodoAquisitoLancamento".concat(Arrays.toString(eventos));
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.somaReferenciaEventosNoPeriodoAquisitoComProporcionalizacaoDiasLancamentoFP(getEp(), getMes(), getAno(), this, eventos);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("diasDeAfastamento(considerarCarência, codigos...)")
    public Double diasDeAfastamento(Boolean considerarCarencia, String codigos) {
        String metodo = "diasDeAfastamento".concat(considerarCarencia ? "Sim" : "Não").concat(codigos);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.diasDeAfastamento(getEp(), getMes(), getAno(), considerarCarencia, codigos);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("diasDeAfastamentoAteCompAtual('codAfastamento', considerarCarência)")
    public Double diasDeAfastamentoAteCompAtual(String codigo, Boolean considerarCarencia) {
        String metodo = "diasDeAfastamentoAteCompAtual".concat(codigo).concat(considerarCarencia ? "Sim" : "Não");
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.diasDeAfastamentoAteCompAtual(getEp(), getMes(), getAno(), codigo, considerarCarencia);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("quantidadeMesesAfastadosPorAno('3','5')")
    public Double quantidadeMesesAfastadosPorAno(String codigos) {
        String metodo = "quantidadeMesesAfastadosPorAno".concat(codigos + "");
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.quantidadeMesesAfastadosNoAno(getEp(), codigos);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("tipoAvisoPrevio()")
    public String tipoAvisoPrevio() {
        String metodo = "tipoAvisoPrevio";
        if (valorMetodo.containsKey(metodo)) {
            return (String) valorMetodo.get(metodo);
        }

        TipoAvisoPrevio tipoAvisoPrevio = funcoesFolhaFacade.recuperarTipoAvisoPrevio(getEp());
        valorMetodo.put(metodo, tipoAvisoPrevio != null ? tipoAvisoPrevio.name() : "");
        return tipoAvisoPrevio != null ? tipoAvisoPrevio.name() : "";
    }

    @DescricaoMetodo("totalHorasExtras(percentual)")
    public double totalHorasExtras(Integer percentual) {
        String metodo = "totalHorasExtras".concat(percentual.toString());
        if (valorMetodo.containsKey(metodo)) {
            return (double) valorMetodo.get(metodo);
        }

        TipoHoraExtra tipoHoraExtra = TipoHoraExtra.getTipoHoraExtraPorValor(percentual.intValue());
        double valor;
        if (tipoHoraExtra == null) {
            valor = 0;
        } else {
            valor = funcoesFolhaFacade.totalHorasExtras(getEp(), getAno(), getMes(), tipoHoraExtra, detalheProcessamentoFolha);
        }

        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("isentoDePrevidencia()")
    public boolean isentoDePrevidencia() {
        String metodo = "isentoDePrevidencia";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.isentoDePrevidencia(getEp(), getFolhaDePagamento().getCalculadaEm());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("isentoIRRF()")
    public boolean isentoIRRF() {
        String metodo = "isentoIRRF";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.isentoDeIrrf(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("aposentadoInvalido()")
    public boolean aposentadoInvalido() {
        String metodo = "aposentadoInvalido";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.aposentadoInvalido(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("pensionistaInvalido()")
    public boolean pensionistaInvalido() {
        String metodo = "pensionistaInvalido";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.invalidezPensionsita(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterModalidadeContratoFP()")
    public ModalidadeContratoFP obterModalidadeContratoFP() {
        String metodo = "obterModalidadeContratoFP";
        if (valorMetodo.containsKey(metodo)) {
            return (ModalidadeContratoFP) valorMetodo.get(metodo);
        }

        ModalidadeContratoFP valor = funcoesFolhaFacade.obterModalidadeContratoFP(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterCargo(codigoCargo)")
    public boolean obterCargo(String codigoCargo) {
        String metodo = "obterCargo".concat(codigoCargo);
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.obterCargo(getEp(), codigoCargo);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterCargoComissao()")
    public String obterCargoComissao() {
        String metodo = "obterCargoComissao".concat(getEp().getIdCalculo() + "");
        if (valorMetodo.containsKey(metodo)) {
            return (String) valorMetodo.get(metodo);
        }

        String valor = funcoesFolhaFacade.obterCargoComissao(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("obterTipoPensionista()")
    public String obterTipoPensionista() {
        String metodo = "tipoPensionista".concat(getEp().getIdCalculo() + "");
        if (valorMetodo.containsKey(metodo)) {
            return (String) valorMetodo.get(metodo);
        }
        TipoPensao valor = funcoesFolhaFacade.recuperaTipoPensao(getEp());
        if (valor != null) {
            valorMetodo.put(metodo, valor);
            return valor.name();
        }
        return "";
    }

    @DescricaoMetodo("obterValorPensao()")
    public Double obterValorPensao() {
        String metodo = "obterValorPensao".concat(getEp().getIdCalculo() + "");
        if (valorMetodo.containsKey(metodo)) {
            return (double) valorMetodo.get(metodo);
        }
        Double valor = funcoesFolhaFacade.recuperaValorPensao(getEp());
        if (valor != null) {
            valorMetodo.put(metodo, valor);
            return valor.doubleValue();
        }
        return Double.valueOf(0);
    }

    @DescricaoMetodo("obterNumeroCotas()")
    public int obterNumeroCotas() {
        String metodo = "obterNumeroCotas".concat(getEp().getIdCalculo() + "");
        if (valorMetodo.containsKey(metodo)) {
            return (int) valorMetodo.get(metodo);
        }
        Integer valor = funcoesFolhaFacade.recuperaNumeroCotas(getEp());
        if (valor != null) {
            valorMetodo.put(metodo, valor);
            return valor;
        }
        return 0;
    }

    @DescricaoMetodo("obterMotivoExoneracao()")
    public int obterMotivoExoneracao() {
        String metodo = "obterMotivoExoneracao".concat(getEp().getIdCalculo() + "");
        if (valorMetodo.containsKey(metodo)) {
            return (Integer) valorMetodo.get(metodo);
        }
        Integer valor = funcoesFolhaFacade.recuperaMotivoExoneracao(getEp());
        if (valor != null) {
            valorMetodo.put(metodo, valor);
            return valor.intValue();
        }
        return 0;
    }

    @DescricaoMetodo("obterCategoriaSefip()")
    public int obterCategoriaSefip() {
        String metodo = "obterCategoriaSefip".concat(getEp().getIdCalculo() + "");
        if (valorMetodo.containsKey(metodo)) {
            return (Integer) valorMetodo.get(metodo);
        }
        Long valor = funcoesFolhaFacade.recuperaCategoriaSefip(getEp());
        if (valor != null) {
            valorMetodo.put(metodo, valor.intValue());
            return valor.intValue();
        }
        return 0;
    }


    @DescricaoMetodo("obterCategoriaeSocial()")
    public int obterCategoriaeSocial() {
        String metodo = "obterCategoriaeSocial".concat(getEp().getIdCalculo() + "");
        if (valorMetodo.containsKey(metodo)) {
            return (Integer) valorMetodo.get(metodo);
        }
        Integer valor = funcoesFolhaFacade.recuperaCategoriaeSocial(getEp());
        if (valor != null) {
            valorMetodo.put(metodo, valor);
            return valor;
        }
        return 0;
    }

    //Método retorna a quantidade de horas extras do ano.. no caso de uma exoneraçao o método usará a data de final de vigencia do contrato(data da exoneraçao)
    @DescricaoMetodo("totalHorasExtrasAnual(argumentos)")
    public Double totalHorasExtrasAnual(String... argumentos) {
        String metodo = "totalHorasExtrasAnual".concat(argumentos + "" + getEp().getIdCalculo() + "");
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.totalHorasExtrasAnual(getEp(), getAno(), getMes(), argumentos);
        if (valor != null) {
            valorMetodo.put(metodo, valor);
            return valor;
        }
        return 0.0;
    }

    @DescricaoMetodo("totalDiasDireitoProporcional()")
    public double totalDiasDireitoProporcional() {
        String metodo = "totalDiasDireitoProporcional".concat(getEp().getIdCalculo() + "");
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.totalDiasDireitoProporcional(getEp());
        if (valor != null) {
            valorMetodo.put(metodo, valor);
            return valor.doubleValue();
        }
        return 0;
    }

    @DescricaoMetodo("totalMesesIndenizacaoFerias()")
    public double totalMesesIndenizacaoFerias() {
        String metodo = "totalMesesIndenizacaoFerias".concat(getEp().getIdCalculo() + "");
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        Double valor = null;
        try {
            valor = funcoesFolhaFacade.totalMesesIndenizacaoFerias(getEp());
        } catch (Exception fx) {
            addItensDetalhesErro((VinculoFP) ep, fx.getMessage());
            return 0;
        }
        if (valor != null) {
            valorMetodo.put(metodo, valor);
            return valor.doubleValue();
        }
        return 0;
    }

    @DescricaoMetodo("temFaltasJustificadas()")
    public boolean temFaltasJustificadas() {
        return totalFaltasRestituidas() > 0.0;
    }

    @DescricaoMetodo("valorTotalFaltasDescontadas(codigoEventoFalta)")
    public double valorTotalFaltasDescontadas(String codigoEventoFalta) {
        String metodo = "valorTotalFaltasDescontadas".concat(codigoEventoFalta);

        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        BigDecimal valor = funcoesFolhaFacade.valorTotalFaltasDescontadas(getEp(), codigoEventoFalta, this);
        if (valor != null) {
            valorMetodo.put(metodo, valor.doubleValue());
            return valor.doubleValue();
        }
        return 0.0;
    }

    @DescricaoMetodo("totalFaltasDescontadas(codigoEventoFalta)")
    public double totalFaltasDescontadas(String codigoEventoFalta) {
        String metodo = "totalFaltasDescontadas".concat(codigoEventoFalta);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        BigDecimal valor = funcoesFolhaFacade.totalFaltasDescontadas(getEp(), codigoEventoFalta);
        if (valor != null) {
            valorMetodo.put(metodo, valor.doubleValue());
            return valor.doubleValue();
        }
        return 0.0;
    }

    @DescricaoMetodo("totalFaltasRestituidas()")
    public double totalFaltasRestituidas() {
        String metodo = "totalFaltasRestituidas".concat(getEp().getIdCalculo() + "");
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.totalFaltasRestituidas(getEp());
        if (valor != null) {
            valorMetodo.put(metodo, valor.doubleValue());
            return valor.doubleValue();
        }
        return 0.0;
    }

    @DescricaoMetodo("recuperarValorBasePensaoAlimenticia()")
    public ValorPensaoAlimenticia recuperarValorBasePensaoAlimenticia(String codigoEvento) throws Exception {
        String metodo = "recuperarValorBasePensaoAlimenticia".concat(getEp().getIdCalculo() + "" + codigoEvento);
        if (valorMetodo.containsKey(metodo)) {
            return (ValorPensaoAlimenticia) valorMetodo.get(metodo);
        }

        ValorPensaoAlimenticia valor = funcoesFolhaFacade.recuperarValorBasePensaoAlimenticia(getEp(), codigoEvento);

        if (valor != null) {
            valorMetodo.put(metodo, valor);
            return valor;
        }
        return null;
    }

    /**
     * Método que verifica se o servidor tem direto a sexta parte, utiliza a classe SextaParte(identifica servidores pela rotina de sexta parte).
     *
     * @return tem direito ou nao a sexta Parte
     */
    @DescricaoMetodo("temSextaParte()")
    public boolean temSextaParte() {
        String metodo = "temSextaParte";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.temSextaParte(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("temSextaParte('Código da Verba')")
    public boolean temSextaParte(String codigo) {
        String metodo = "temSextaParte" + codigo;
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }

        boolean valor = funcoesFolhaFacade.temSextaParte(getEp(), codigo);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    public void addItensDetalhesErro(VinculoFP vinculo, String erro) {
        try {
            ItensDetalhesErrosCalculo i = new ItensDetalhesErrosCalculo();
            vinculo.getFolha().getDetalhesCalculoRHAtual().contaTotalComProblemasNoCalculo();
            i.setDetalhesCalculoRH(vinculo.getFolha().getDetalhesCalculoRHAtual());
            i.setVinculoFP(vinculo);
            i.setErros(erro);
            vinculo.getFolha().getDetalhesCalculoRHAtual().getItensDetalhesErrosCalculos().add(i);
        } catch (Exception e) {
            logger.error("Erro ao tentar adicionar detalhes de erros", e);
        }
    }

    /*
     * Para esta operação, sempre recuperar o valor do ultimo mês (Novembro), multiplicar pelo número de
     * meses que o servidor trabalhou e em seguida dividir por 12
     *
     */
    public Double recuperarValor13ProporcionalUltimoMes(EventoFP evento) {
        Double valorDecimoTerceiro = 0.0;
        Mes mes = null;
        if (evento.getUltimoAcumuladoEmDezembro()) {
            mes = Mes.DEZEMBRO;
        } else {
            mes = funcoesFolhaFacade.buscarUltimoMesCalculado(getEp(), getAno(), evento.getCodigo());
        }

        if (mes == null) {
            //addItensDetalhesErro((VinculoFP) getEp(), "Não foi encontrado nenhum mês calculado no ano " + getAno() + " para o evento " + evento);
            return 0.0;
        }
        Integer quantidadeMeses = funcoesFolhaFacade.quantidadeOcorrenciaEventoByAno(getEp(), evento);

        EventoTotalItemFicha eventoTotalItemFichaMes = funcoesFolhaFacade.recuperarValorFicha(evento, ep, mes, getAno());
        if (eventoTotalItemFichaMes == null) {
            return valorDecimoTerceiro;
        }
        if (evento.getPropMesesTrabalhados()) {
            valorDecimoTerceiro = getValorProporcionalizadoUltimoAcumulado(quantidadeMeses, eventoTotalItemFichaMes.getTotal(), MESES_ANO);
        } else {
            valorDecimoTerceiro = eventoTotalItemFichaMes.getTotal() != null ? eventoTotalItemFichaMes.getTotal().doubleValue() : 0;
        }
        return valorDecimoTerceiro;
    }


    public Double buscarValorUltimoMesPago(String tipoFolha, String meses) {
        Double valorDecimoTerceiro = 0.0;
        TipoFolhaDePagamento tipo = TipoFolhaDePagamento.valueOf(tipoFolha);
        Mes mes = funcoesFolhaFacade.buscarUltimoMesCalculadoPorTipoFolha(getEp(), getAno(), tipo);
        if (mes == null) {
            addItensDetalhesErro((VinculoFP) getEp(), "Não foi encontrado nenhum mês calculado no ano " + getAno());
            return 0.0;
        }

        Integer quantidadeMeses = funcoesFolhaFacade.quantidadeOcorrenciaEventoByAno(getEp(), null);

        ResumoFichaFinanceira eventoTotalItemFichaMes = funcoesFolhaFacade.getFichaFinanceiraFPFacade().buscarValorPorMesAndAnoAndTipoFolha(ep.getId(), mes, getAno(), tipo, 1080, true);

        valorDecimoTerceiro = getValorProporcionalizadoUltimoAcumulado(quantidadeMeses, eventoTotalItemFichaMes.getValorBruto(), meses);

        if (valorDecimoTerceiro != null) {
            return valorDecimoTerceiro;
        }
        valorDecimoTerceiro = eventoTotalItemFichaMes.getValorBruto() != null ? eventoTotalItemFichaMes.getValorBruto().doubleValue() : 0;
        return valorDecimoTerceiro;
    }

    @DescricaoMetodo("buscarValorUltimoMesPago('codigoBase')")
    public double buscarValorUltimoMesPago(String codigoBase) throws Exception {
        String metodo = "buscarValorUltimoMesPago".concat(codigoBase);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        final boolean LOG_BASE = true;
        BigDecimal soma = BigDecimal.ZERO;
        List<MemoriaCalculoRHDTO> listaEventosBase = Lists.newLinkedList();
        double somaTransient = 0;
        logger.debug("Base do Contexto Transient [{}]", codigoBase);

        for (ItemBaseFP itemBaseFP : obterItensBaseFP(codigoBase)) {
            if (contextoTransient != null) {
                if (contextoTransient.getEventoValorFormula().containsKey(itemBaseFP.getEventoFP())) {
                    BigDecimal valorEventoTransient = contextoTransient.getEventoValorFormula().get(itemBaseFP.getEventoFP());
                    if (OperacaoFormula.SUBTRACAO.equals(itemBaseFP.getOperacaoFormula())) {
                        valorEventoTransient = valorEventoTransient.negate();
                    }
                    if (itemBaseFP.getEventoFP() != null && valorEventoTransient.compareTo(BigDecimal.ZERO) != 0) {
                        listaEventosBase.add(new MemoriaCalculoRHDTO(codigoBase, itemBaseFP.getEventoFP(), TipoMemoriaCalculo.BASEFP, valorEventoTransient.doubleValue() < 0 ? OperacaoFormula.SUBTRACAO : OperacaoFormula.ADICAO, valorEventoTransient));
                    }
                    logger.debug("Verba {} valor: {} ", itemBaseFP.getEventoFP(), valorEventoTransient);
                    somaTransient += valorEventoTransient.doubleValue();
                }
            }
        }
        logger.debug("Total Transient : " + somaTransient);
        soma = BigDecimal.valueOf(somaTransient);

        if (LOG_BASE) {
            logger.debug("Total da Base " + codigoBase + " : " + soma);
        }
        memoriaCalculoEventosBases.put("buscarValorUltimoMesPago('" + codigoBase + "')", listaEventosBase);
        valorMetodo.put(metodo, soma.doubleValue());
        return soma.doubleValue();
    }

    @DescricaoMetodo("buscarValorVerbaContextoTransient('codigo do evento')")
    public double buscarValorVerbaContextoTransient(String codigo) {
        if (contextoTransient != null) {
            EventoFP eventoFP = funcoesFolhaFacade.recuperaEvento(codigo, TipoExecucaoEP.FOLHA);
            if (isServidorEVerbaBloqueado(ep, eventoFP)) {
                return 0;
            }
            if (contextoTransient.getEventoValorFormula().containsKey(eventoFP)) {
                BigDecimal valorEventoTransient = contextoTransient.getEventoValorFormula().get(eventoFP);
                return valorEventoTransient != null ? valorEventoTransient.doubleValue() : 0;
            }
        }
        return 0;
    }


    private double getValorProporcionalizadoUltimoAcumulado(Integer quantidadeMeses, BigDecimal valor, String meses) {
        return valor.multiply(new BigDecimal(quantidadeMeses + "")).divide(new BigDecimal(meses), RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Método, para esse valor deve ter calculados todos os meses do ano.
     *
     * @param evento
     * @return
     */
    //TODO para esse valor deve ter calculados todos os meses do ano.
    public Double recuperarValor13ProporcionalMediaAno(EventoFP evento) {

        //recuperar valor do evento do ano inteiro
        Double valorDecimoTerceiro = 0.0;

        EventoTotalItemFicha eventoTotalItemFicha = funcoesFolhaFacade.recuperarValorFichaByAnoAndEvento(evento.getCodigo(), ep, getAno(), null);
        if (eventoTotalItemFicha != null) {
            BigDecimal valor = eventoTotalItemFicha.getTotal();
            //no turmalina, em todos os casos era divido por 12 meses, idependente se o servidor tem ou não 12 meses trablhados TODO verificar uma possível alteração para meses trablhados(isso seria o lógico!!!!)
            valorDecimoTerceiro = valor.divide(new BigDecimal("12"), 4, RoundingMode.HALF_EVEN).doubleValue();
        }
        return valorDecimoTerceiro;
    }

    /**
     * recupera valor de todas as parcelas do adiantamento do 13 salario..
     *
     * @return
     */
    @DescricaoMetodo("temAdiantamento13Salario(codigoEvento)")
    public boolean temAdiantamento13Salario(Integer codigo) {
        return recuperarValorAdiantamento13(codigo) > 0.0;
    }

    @DescricaoMetodo("recuperarValorAdiantamento13(codigoEvento)")
    public Double recuperarValorAdiantamento13(Integer codigo) {
        String metodo = "recuperarValorAdiantamento13" + codigo;
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        //recuperar valor do evento do ano inteiro
        Double valorDecimoTerceiro = 0.0;

        EventoTotalItemFicha eventoTotalItemFicha = funcoesFolhaFacade.recuperarValorFichaByAnoAndEvento(codigo + "", getEp(), getAno(), null);
        if (eventoTotalItemFicha != null) {

            valorDecimoTerceiro = eventoTotalItemFicha.getTotal().doubleValue();
            valorMetodo.put(metodo, valorDecimoTerceiro);
            return valorDecimoTerceiro;
        }
        return valorDecimoTerceiro;
    }

    @DescricaoMetodo("totalDeDiasLancados()")
    public int totalDeDiasLancados() {
        if (lancamentoFP != null) {
            String metodo = "totalDeDiasLancados" + lancamentoFP.getId();
            if (valorMetodo.containsKey(metodo)) {
                return (int) valorMetodo.get(metodo);
            }
            int totalDias = funcoesFolhaFacade.entreDiasMesCalculo(ep, lancamentoFP.getMesAnoInicial(), lancamentoFP.getMesAnoFinal());
            valorMetodo.put(metodo, totalDias);
            return totalDias;
        }
        return 0;
    }

    @DescricaoMetodo("proporcionalizaLancamento()")
    public boolean proporcionalizaLancamento() {
        if (lancamentoFP != null) {
            String metodo = "proporcionalizaLancamento" + lancamentoFP.getId();
            if (valorMetodo.containsKey(metodo)) {
                return (boolean) valorMetodo.get(metodo);
            }
            boolean proporcionaliza = lancamentoFP.getEventoFP().getProporcionalizaDiasTrab();
            valorMetodo.put(metodo, proporcionaliza);
            return proporcionaliza;
        }
        return false;
    }

    @DescricaoMetodo("numeroDiasPenalidade()")
    public double numeroDiasPenalidade() {
        String metodo = "numeroDiasPenalidade";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        Double valor = funcoesFolhaFacade.numeroDiasDescontoDePenalidalidade(ep);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("temPenalidade()")
    public boolean temPenalidade() {
        String metodo = "temPenalidade";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean valor = numeroDiasPenalidade() > 0;
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("diasSextaParte()")
    public int diasSextaParte() {
        String metodo = "diasSextaParte";
        if (valorMetodo.containsKey(metodo)) {
            return (int) valorMetodo.get(metodo);
        }
        int valor = funcoesFolhaFacade.diasSextaParte(ep);
        double diasTrabalhados = diasTrabalhados();
        if (valor <= 0) {
            if (ep instanceof Aposentadoria || ep instanceof Pensionista) {
                valor = (int) diasTrabalhados;
            }
        }
        if (valor > diasTrabalhados) {
            valor = (int) diasTrabalhados;
        }
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("codigoOpcaoSalarial()")
    public String codigoOpcaoSalarial() {
        String metodo = "codigoOpcaoSalarial";
        if (valorMetodo.containsKey(metodo)) {
            return (String) valorMetodo.get(metodo);
        }
        String valor = funcoesFolhaFacade.recuperarOpcaoSalarialCodigo(ep);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("codigoOpcaoSalarial('tipo')")
    public String codigoOpcaoSalarial(String tipo) {
        String metodo = "codigoOpcaoSalarial('tipo')";
        if (valorMetodo.containsKey(metodo)) {
            return (String) valorMetodo.get(metodo);
        }
        if (tipo.equals("COMISSAO")) {
            String valor = funcoesFolhaFacade.recuperarOpcaoSalarialCodigo(ep);
            valorMetodo.put(metodo, valor);
            return valor;
        } else {
            String valor = funcoesFolhaFacade.recuperarOpcaoSalarialCodigo(ep);
            valorMetodo.put(metodo, valor);
            return valor;
        }

    }

    @DescricaoMetodo("temEventoCategoria(codigoEvento)")
    public boolean temEventoCategoria(String codigo) {
        String metodo = "temEventoCategoria".concat(codigo);
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean valor = funcoesFolhaFacade.categoriaEvento(ep, codigo);
        addLog(metodo, valor);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("pensionistaEncerrandoVigencia()")
    public boolean pensionistaEncerrandoVigencia() {
        String metodo = "pensionistaEncerrandoVigencia".concat(ep.getId().toString());
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean valor = funcoesFolhaFacade.hasVinculoEncerrandoNoMes(ep);
        addLog(metodo, valor);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    public boolean isServidorEVerbaBloqueado(EntidadePagavelRH vinculoFP, EventoFP eventoFP) {
        String key = vinculoFP.getIdCalculo() + "" + eventoFP.getCodigo();
        if (servidorEstaBloqueadoPorFuncionalidade(vinculoFP, eventoFP)) {
            logger.debug("Verba bloqueada por funcionalidade 'Licença Prêmio' " + vinculoFP + " verba: " + eventoFP);
            return true;
        }
        if (isVerbaBloqueada(vinculoFP, eventoFP)) {
            logger.debug("Verba bloqueada via tela de calculo " + eventoFP);
            return true;
        }
        if (detalheProcessamentoFolha.getBloqueioVerbaMap().containsKey(key)) {
            List<BloqueioEventoFP> bloqueios = detalheProcessamentoFolha.getBloqueioVerbaMap().get(key);
            for (BloqueioEventoFP bloqueio : bloqueios) {
                DateTime dt = new DateTime(getDataReferenciaCompleta());
                DateTime ini = new DateTime(bloqueio.getDataInicial());
                DateTime fim = new DateTime(bloqueio.getDataFinal());
                if ((dt.isAfter(ini) || dt.isEqual(ini)) && (dt.isBefore(fim) || dt.isEqual(fim))) {
                    logger.debug("servidor bloqueado " + vinculoFP + " - " + eventoFP);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isVerbaBloqueada(EntidadePagavelRH vinculoFP, EventoFP eventoFP) {
        if (detalheProcessamentoFolha.getEventosBloqueados() == null) {
            return false;
        }
        return detalheProcessamentoFolha.getEventosBloqueados().contains(eventoFP);
    }

    private boolean servidorEstaBloqueadoPorFuncionalidade(EntidadePagavelRH vinculoFP, EventoFP eventoFP) {
        if (eventoFP.getBloqueiosFuncionalidade() != null) {
            for (BloqueioFuncionalidade bloqueio : eventoFP.getBloqueiosFuncionalidade()) {
                if (TipoFuncionalidadeRH.LICENCA_PREMIO.equals(bloqueio.getTipoFuncionalidadeRH())) {
                    return funcoesFolhaFacade.buscarDiasDeLicencaPremio(vinculoFP, dateToLocalDate(getDataReferencia())) >= 15;
                }
            }
        }

        return false;
    }

    public boolean isBeneficioBloqueado(TipoBloqueio tipoBloqueio) {
        String key = ep.getIdCalculo() + "" + tipoBloqueio.name();
        if (detalheProcessamentoFolha.getBloqueioBeneficioMap().containsKey(key)) {
            List<BloqueioBeneficio> bloqueios = detalheProcessamentoFolha.getBloqueioBeneficioMap().get(key);
            for (BloqueioBeneficio bloqueio : bloqueios) {
                DateTime dt = new DateTime(getDataReferenciaCompleta());
                DateTime ini = new DateTime(bloqueio.getInicioVigencia());
                DateTime fim = bloqueio.getFinalVigencia() != null ? new DateTime(bloqueio.getFinalVigencia()) : dt;
                if ((dt.isAfter(ini) || dt.isEqual(ini)) && (dt.isBefore(fim) || dt.isEqual(fim))) {
                    logger.debug("benefício bloqueado  " + ep + " - " + tipoBloqueio);
                    return true;
                }
            }
        }
        return false;
    }

    public Date getDataReferencia() {
        String metodo = "getDataReferencia";
        if (valorMetodo.containsKey(metodo)) {
            return (Date) valorMetodo.get(metodo);
        }
        Date valor = getDataReferenciaCompleta();
        valorMetodo.put(metodo, valor);
        return valor;
    }

    public Date getDataReferenciaCompleta() {
        String metodo = "getDataReferenciaCompleta";
        if (valorMetodo.containsKey(metodo)) {
            return (Date) valorMetodo.get(metodo);
        }
        Date valor = localDateToDate(funcoesFolhaFacade.getDataReferencia(ep));
        valor = zerarHoraMinutoSegundo(new DateTime(valor)).toDate();
        valorMetodo.put(metodo, valor);
        return valor;
    }

    public DateTime zerarHoraMinutoSegundo(DateTime dateTime) {
        dateTime = dateTime.withHourOfDay(0);
        dateTime = dateTime.withMillisOfSecond(0);
        dateTime = dateTime.withMinuteOfHour(0);
        dateTime = dateTime.withSecondOfMinute(0);
        return dateTime;
    }

    public DateTime getDataReferenciaJoda() {
        String metodo = "getDataReferenciaJoda" + getDataReferenciaCompleta();
        if (valorMetodo.containsKey(metodo)) {
            return (DateTime) valorMetodo.get(metodo);
        }
        DateTime valor = new DateTime(getDataReferenciaCompleta());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("valorUltimosMeses('codigoEvento',qtdeMeses)")
    public double valorUltimosMeses(String codigo, int quantidadeMeses) {
        String metodo = "valorUltimosMeses".concat(codigo);
        if (valorMetodo.containsKey(metodo)) {
            return (double) valorMetodo.get(metodo);
        }
        double valor = funcoesFolhaFacade.valorUltimosMeses(ep, codigo, quantidadeMeses);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("quantidadeMeses13Proporcional()")
    public double quantidadeMeses13Proporcional() {
        String metodo = "quantidadeMeses13Proporcional";
        if (valorMetodo.containsKey(metodo)) {
            return (double) valorMetodo.get(metodo);
        }
        double valor = funcoesFolhaFacade.recuperaQuantidadeMesesPropocionaisA13Salario(ep);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("tipoCalculoAposentadoria() == 'PROPORCIONAL' ou 'INTEGRAL' ")
    public String tipoCalculoAposentadoria() {
        String metodo = "tipoCalculoAposentadoria";
        if (valorMetodo.containsKey(metodo)) {
            return (String) valorMetodo.get(metodo);
        }
        String retorno = "";
        if (ep instanceof Aposentadoria) {
            retorno = (((Aposentadoria) ep).getTipoCalculoAposentadoria() != null ? ((Aposentadoria) ep).getTipoCalculoAposentadoria().name() : "");
        }
        valorMetodo.put(metodo, retorno);
        return retorno;
    }

    @DescricaoMetodo("estaNoOrgao('25')")
    public boolean estaNoOrgao(String codigo) {
        String metodo = "estaNoOrgao".concat(codigo);
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean valor = funcoesFolhaFacade.isServidorLotadoNoOrgao(ep, codigo);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("optantePrevidenciaComplementar()")
    public boolean optantePrevidenciaComplementar() {
        String metodo = "optantePrevidenciaComplementar";
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean valor = funcoesFolhaFacade.obterPrevidenciaComplementar(getEp()) != null;
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("aliquotaPrevidenciaComplementarServidor()")
    public double aliquotaPrevidenciaComplementarServidor() {
        String metodo = "aliquotaPrevidenciaComplementarServidor";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        double valor = 0d;
        ItemPrevidenciaComplementar previdencia = funcoesFolhaFacade.obterPrevidenciaComplementar(getEp());
        if (previdencia != null && previdencia.getAliquotaServidor() != null) {
            valor = previdencia.getAliquotaServidor().doubleValue();
        }
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("aliquotaPrevidenciaComplementarPatrocinador()")
    public double aliquotaPrevidenciaComplementarPatrocinador() {
        String metodo = "aliquotaPrevidenciaComplementarPatrocinador";
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        double valor = 0d;
        ItemPrevidenciaComplementar previdencia = funcoesFolhaFacade.obterPrevidenciaComplementar(getEp());
        if (previdencia != null && previdencia.getAliquotaPatrocinador() != null) {
            valor = previdencia.getAliquotaPatrocinador().doubleValue();
        }
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("verificarOrgaoServidor()")
    public boolean verificarOrgaoServidor(String codigo) {
        String metodo = "verificarOrgaoServidor".concat(codigo);
        if (valorMetodo.containsKey(metodo)) {
            return (boolean) valorMetodo.get(metodo);
        }
        boolean valor = false;
        Map<EntidadePagavelRH, String> vinculoOrgao = detalheProcessamentoFolha.getVinculoOrgao();
        if (vinculoOrgao.containsKey(ep)) {
            valor = vinculoOrgao.get(ep).equalsIgnoreCase(codigo);
        }
        valorMetodo.put(metodo, valor);
        return valor;
    }

    public void preencheLacamentosCache(List<LancamentoFP> lista) {
        if (lancamentos == null) return;
        for (LancamentoFP l : lista) {
            lancamentos.put(l.getEventoFP(), l);
        }
    }


    public DetalheProcessamentoFolha getDetalheProcessamentoFolha() {
        return detalheProcessamentoFolha;
    }

    public void gravarDescontoIRRF65Anos(Double valor) {
        System.out.println("Opa.. gravar o valor de: " + valor);
        if (!ep.isCalculandoRetroativo() && ep.getFicha() != null) {
            ep.getFicha().setValorIsento(BigDecimal.valueOf(valor));
        }
    }


    @DescricaoMetodo(value = "buscarTotalBases('ULTIMOSMESES', 'VALOR', new Array('NORMAL'), codigo Base FP, true, 12, true, )", documentacao = "Função:\n" +
        "\tcalculador.buscarTotalBases(String modoBusca, String tipoBusca, String[] tiposFolha, String[] eventos, Boolean limitarAno, Integer quantidadeMes, considerarMesAtual);\n" +
        "\n" +
        "\t\t1º Parametro: modoBusca aceita os seguintes valores:\n" +
        "\t\t\tULTIMOSMESES = busca os valores da pessoa no ano e/ou pela quantidade de meses indicados, utiliza para filtro os eventos passados por parametro.\n" +
        "\t        FERIAS = faz a busca dos valores dos eventos passados por parametros dentro do intervalo do periodo aquisitivo da férias(só funciona quando tem férias com o campo mes e ano igual ao mes de calculo)\n" +
        "\t        SALARIO_13 = faz a busca dos valores dentro do ano(ignora o campo quantidadeMeses)\n" +
        "\n" +
        "\t    2º Parametro: tipoBusca\n" +
        "\t    \tVALOR = Busca todos os valores baseados no campo valor da ficha financeira;\n" +
        "\t    \tREFERENCIA = Busca todos os valores baseados no campo referencia da ficha financeira\n" +
        "\n" +
        "\t    3º Parametro: tiposFolha\n" +
        "\t    \tUtilizado para filtrar que tipo de folha será utilizado no filtro, quando informado \"null\", o sistema irá buscar por todos os tipos.\n" +
        "\t    \tSintaxe new Array('NORMAL','COMPLEMENTAR')\n" +
        "\n" +
        "\t    4º Parametro: eventos\n" +
        "\t    \tDiz qual os eventos serão utilizados para realizar a busca.\n" +
        "\n" +
        "\t    5º Parametro: limitarAno\n" +
        "\t    \tUtilizando para verificar se vai ser limitado o filtro no ano de calculo, quando nulo, o sistema não limita, para o tipo SALARIO_13 esse campo não tem efeito.\n" +
        "\n" +
        "\t    6º Parametro: quantidadeMes\n" +
        "\t    \té referente a quantidade de meses que o sistema utilizará pra fazer a busca, quando o modo busca for SALARIO_13 o sistema ignora esse campo.\n" +

        "\t    7º Parametro: considerarRetroacao\n" +
        "\t    \té referente se o sistema vai considear as retroações da base de calculo ou não.\n")
    public Double buscarTotalBases(String modoBusca, String tipoBusca, String[] tiposFolha, String base, Boolean limitarAno, Integer quantidadeMes, Boolean considerarMesAtual) {
             return buscarTotalBases(modoBusca,  tipoBusca, tiposFolha,  base,  limitarAno,  quantidadeMes, considerarMesAtual, "LER_BASE");
    }

    @DescricaoMetodo(value = "buscarTotalBases('ULTIMOSMESES', 'VALOR', new Array('NORMAL'), codigo Base FP, true, 12, true)", documentacao = "Função:\n" +
        "\tcalculador.buscarTotalBases(String modoBusca, String tipoBusca, String[] tiposFolha, String[] eventos, Boolean limitarAno, Integer quantidadeMes, considerarMesAtual);\n" +
        "\n" +
        "\t\t1º Parametro: modoBusca aceita os seguintes valores:\n" +
        "\t\t\tULTIMOSMESES = busca os valores da pessoa no ano e/ou pela quantidade de meses indicados, utiliza para filtro os eventos passados por parametro.\n" +
        "\t        FERIAS = faz a busca dos valores dos eventos passados por parametros dentro do intervalo do periodo aquisitivo da férias(só funciona quando tem férias com o campo mes e ano igual ao mes de calculo)\n" +
        "\t        SALARIO_13 = faz a busca dos valores dentro do ano(ignora o campo quantidadeMeses)\n" +
        "\n" +
        "\t    2º Parametro: tipoBusca\n" +
        "\t    \tVALOR = Busca todos os valores baseados no campo valor da ficha financeira;\n" +
        "\t    \tREFERENCIA = Busca todos os valores baseados no campo referencia da ficha financeira\n" +
        "\n" +
        "\t    3º Parametro: tiposFolha\n" +
        "\t    \tUtilizado para filtrar que tipo de folha será utilizado no filtro, quando informado \"null\", o sistema irá buscar por todos os tipos.\n" +
        "\t    \tSintaxe new Array('NORMAL','COMPLEMENTAR')\n" +
        "\n" +
        "\t    4º Parametro: eventos\n" +
        "\t    \tDiz qual os eventos serão utilizados para realizar a busca.\n" +
        "\n" +
        "\t    5º Parametro: limitarAno\n" +
        "\t    \tUtilizando para verificar se vai ser limitado o filtro no ano de calculo, quando nulo, o sistema não limita, para o tipo SALARIO_13 esse campo não tem efeito.\n" +
        "\n" +
        "\t    6º Parametro: quantidadeMes\n" +
        "\t    \té referente a quantidade de meses que o sistema utilizará pra fazer a busca, quando o modo busca for SALARIO_13 o sistema ignora esse campo.\n")
    public Double buscarTotalBases(String modoBusca, String tipoBusca, String[] tiposFolha, String base, Boolean limitarAno, Integer quantidadeMes, Boolean considerarMesAtual, String considerarRetroacao) {
        String metodo = "buscarTotalBases".concat((tiposFolha != null ? Arrays.asList(tiposFolha) : "") + tipoBusca + modoBusca + base + limitarAno + quantidadeMes + considerarMesAtual);
        logger.debug("Parametros buscarTotalBases [{}]", (tiposFolha != null ? Arrays.asList(tiposFolha) : "") + tipoBusca + modoBusca + base + limitarAno + quantidadeMes + considerarMesAtual);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        if (limitarAno == null) {
            limitarAno = false;
        }

        TipoBusca tipo = TipoBusca.getTipoPorName(tipoBusca);
        ModoBusca modo = ModoBusca.getTipoPorName(modoBusca);

        if (modo == null) {
            return 0.0;
        }
        List<MemoriaCalculoRHDTO> listaEventosBase = Lists.newLinkedList();

        EventoTotalItemFicha totalItemFicha = null;
        List<ItemBaseFP> eventosFP = Arrays.asList(obterItensBaseFP(base));
        List<EventoTipoCaclulo> eventosString = new ArrayList<>();
        TipoBuscaRetroacao tipoBuscaRetroacao = TipoBuscaRetroacao.valueOf(considerarRetroacao);
        for (ItemBaseFP eventoFP : eventosFP) {
            if(TipoBuscaRetroacao.LER_BASE.equals(tipoBuscaRetroacao)){
                eventosString.add(new EventoTipoCaclulo(eventoFP.getEventoFP().getCodigo(), TipoCalculoFP.NORMAL));
                if(eventoFP.getSomaValorRetroativo()) {
                    eventosString.add(new EventoTipoCaclulo(eventoFP.getEventoFP().getCodigo(), TipoCalculoFP.RETROATIVO));
                }
            }
            if(TipoBuscaRetroacao.COM_RETROACAO.equals(tipoBuscaRetroacao)){
                eventosString.add(new EventoTipoCaclulo(eventoFP.getEventoFP().getCodigo(), TipoCalculoFP.NORMAL));
                eventosString.add(new EventoTipoCaclulo(eventoFP.getEventoFP().getCodigo(), TipoCalculoFP.RETROATIVO));
            }
            if(TipoBuscaRetroacao.SEM_RETROACAO.equals(tipoBuscaRetroacao)){
                eventosString.add(new EventoTipoCaclulo(eventoFP.getEventoFP().getCodigo(), TipoCalculoFP.NORMAL));
            }

        }

        Double valor = 0.0;
        valor = buscarTotalEventos(modoBusca, tipoBusca, tiposFolha, eventosString, limitarAno, quantidadeMes, considerarMesAtual, listaEventosBase);

        logger.debug("Resultado: [{}]", totalItemFicha);
        if (!listaEventosBase.isEmpty() && getFolhaDePagamento().isGravarMemoriaCalculo()) {
            memoriaCalculoEventosBases.put("buscarTotalBases('" + base + "')", listaEventosBase);
        }
        valorMetodo.put(metodo, valor.doubleValue());
        if (!listaEventosBase.isEmpty() && getFolhaDePagamento().isGravarMemoriaCalculo()) {
            for (MemoriaCalculoRHDTO memoriaCalculoRHDTO : listaEventosBase) {
                memoriaCalculoRHDTO.setCodigoBase(base);
            }
            EventoFP evento = (EventoFP) engine.get("EVENTO");
            EventoFP eventoFP = evento == null ? eventoCalculandoAgora : evento;
            memoriaCalculoEventosBases.put(eventoFP != null ? eventoFP.getCodigo() : base, listaEventosBase);
        }
        return valor.doubleValue();
    }

    @DescricaoMetodo(value = "buscarTotalEventos('ULTIMOSMESES', 'VALOR', new Array('NORMAL'), new Array('1','10', '13', '4'), true, 12, true)", documentacao = "Função:\n" +
        "\tcalculador.buscarTotalEventos(String modoBusca, String tipoBusca, String[] tiposFolha, String[] eventos, Boolean limitarAno, Integer quantidadeMes, considerarMesAtual);\n" +
        "\n" +
        "\t     1º Parametro: modoBusca aceita os seguintes valores:\n" +
        "\t        Opções aceitas:" +
        "\t            ULTIMOSMESES = busca os valores da pessoa no ano e/ou pela quantidade de meses indicados, utiliza para filtro os eventos passados por parametro.\n" +
        "\t              SALARIO_13 = Busca dos valores dentro do ano(ignora o campo quantidadeMeses)\n" +
        "\t                  FERIAS = Busca dos valores dos eventos passados por parametros dentro do intervalo do periodo aquisitivo da férias \n" +
        "\t                           (só funciona quando tem férias com o campo mes e ano igual ao mes de calculo - necessário mês/ano da concessão = mês/ano do cálculo)\n" +
        "\t              FERIASPROP = Busca os valores dentro do período aquisitivo de férias proporcionais (no caso de rescisões/exonerações). \n" +
        "\t                           O período aquisitivo de férias proporcionais é aquele não concedido que, na data da rescisão, ainda não se completou, ou seja, \n" +
        "\t                           a data da rescisão está entre o início do período aquisitivo e o final, incluindo as datas de inicío e fim. \n" +
        "\t                           Assim: Início de Vigência do PA <= Data da Rescisão <= Final de Vigência do PA.\n" +
        "\t              FERIASVENC = Busca os valores dentro do(s) período(s) aquisitivo(s) de férias vencida(s) (no caso de resciões/exonerações). \n" +
        "\t                           O período aquisitivo de férias vencidas é aquele não concedido que, na data da rescisão, já se completou, ou seja, \n" +
        "\t                           a data da rescisão é maior que o final do período aquisitivo. Assim: Final de Vigência do PA < Data da Rescisão.\n" +
        "\t              FERIASDOBR = Busca os valores dentro do(s) período(s) aquisitivo(s) de férias vencida(s) (no caso de resciões/exonerações), que esteja \"dobrado\". \n" +
        "\t                           O período aquisitivo de férias dobradas é aquele não concedido que, na data da rescisão + dias de saldo de férias não gozadas, \n" +
        "\t                           já ultrapassou o final do período concessivo (que compreende um período igual ao do PA após o vencimento deste), \n" +
        "\t                           ou seja, a data da rescisão é maior que o final do período concessivo. \n" +
        "\t                           Assim: Final de Vigência do PC < Data da Rescisão + dias de saldo de férias não gozadas.\n" +
        "\n" +
        "\t    2º Parametro: tipoBusca\n" +
        "\t        Opções aceitas:\n" +
        "\t                     VALOR = Busca todos os valores baseados no campo valor da ficha financeira;\n" +
        "\t                REFERENCIA = Busca todos os valores baseados no campo referencia da ficha financeira\n" +
        "\t                      null = Equivale a VALOR.\n" +
        "\n" +
        "\t    3º Parametro: tiposFolha\n" +
        "\t        Utilizado para filtrar que tipo de folha será utilizado no filtro, quando informado \"null\", o sistema irá buscar por todos os tipos.\n" +
        "\t        Sintaxe new Array('NORMAL','COMPLEMENTAR')\n" +
        "\n" +
        "\t    4º Parametro: eventos\n" +
        "\t        Diz qual os eventos serão utilizados para realizar a busca.\n" +
        "\n" +
        "\t    5º Parametro: limitarAno (*OBS.:Exclusivo para modoBusca = ULTIMOSMESES, ignorado para outras opções de modoBusca.)\n" +
        "\t        Opções aceitas:\n" +
        "\t                      true = Limita a busca no ano de cálculo.\n" +
        "\t                     false = Não limita a busca no ano de cálculo.\n" +
        "\t                      null = Equivale a false.\n"+
        "\n" +
        "\t    6º Parametro: quantidadeMes (*OBS.:Exclusivo para modoBusca = ULTIMOSMESES, ignorado para outras opções de modoBusca.)\n" +
        "\t        Indica a quantidade de meses (número inteiro) que a função utilizará para fazer a busca. O mês de cálculo atual será o mês \"0\" (zero).\n" +
        "\n" +
        "\t    7º parâmetro: consideraMesAtual (*OBS.:Exclusivo para modoBusca = ULTIMOSMESES, ignorado para outras opções de modoBusca.)\n" +
        "\t        Opções aceitas:\n" +
        "\t                      true = Considera os valores calculados do mês atual para a somatória.\n" +
        "\t                     false = Não considera os valores calculados no mês atual para a somatória.\n" +
        "\t                      null = Equivale a false.\n" +
        "\t        Indica se a busca deve considerar os valores calculados do mês atual.")
    public Double buscarTotalEventos(String modoBusca, String tipoBusca, String[] tiposFolha, String[] eventos, Boolean limitarAno, Integer quantidadeMes, Boolean considerarMesAtual) {
        return buscarTotalEventos(modoBusca, tipoBusca, tiposFolha, eventos, limitarAno, quantidadeMes, considerarMesAtual, null);
    }



    public Double buscarTotalEventos(String modoBusca, String tipoBusca, String[] tiposFolha, String[] eventos, Boolean limitarAno, Integer quantidadeMes, Boolean considerarMesAtual, List<MemoriaCalculoRHDTO> memoriasCalculo) {
        List<EventoTipoCaclulo> eventosTipo = new ArrayList<>();
        for (TipoCalculoFP value : TipoCalculoFP.values()) {
            for (String evento : eventos) {
                eventosTipo.add(new EventoTipoCaclulo(evento, value));
            }
        }
        return buscarTotalEventos(modoBusca, tipoBusca, tiposFolha, eventosTipo, limitarAno, quantidadeMes, considerarMesAtual, memoriasCalculo);
    }

    public Double buscarTotalEventos(String modoBusca, String tipoBusca, String[] tiposFolha,  List<EventoTipoCaclulo> eventos, Boolean limitarAno, Integer quantidadeMes, Boolean considerarMesAtual, List<MemoriaCalculoRHDTO> memoriasCalculo) {
        String metodo = "buscarTotalEventos".concat((tiposFolha != null ? Arrays.asList(tiposFolha) : "") + tipoBusca + modoBusca + (eventos != null ? Arrays.asList(eventos).toString() : "") + limitarAno + quantidadeMes + considerarMesAtual);
        logger.debug("Parametros buscarTotalEventos [{}]", (tiposFolha != null ? Arrays.asList(tiposFolha) : "") + tipoBusca + modoBusca + (eventos != null ? Arrays.asList(eventos).toString() : "") + limitarAno + quantidadeMes + considerarMesAtual);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        if (limitarAno == null) {
            limitarAno = false;
        }
        if (memoriasCalculo == null) {
            memoriasCalculo = new ArrayList<>();
        }
        TipoBusca tipo = TipoBusca.getTipoPorName(tipoBusca);
        ModoBusca modo = ModoBusca.getTipoPorName(modoBusca);
        List<TipoFolhaDePagamento> tiposFolhaDePagamento = TipoFolhaDePagamento.convertStringToTipoFolha(tiposFolha);
        if (modo == null) {
            return 0.0;
        }

        LocalDate dateTime = dateTimeToLocalDateTime(DataUtil.criarDataComMesEAno(Mes.getMesToInt(ep.getMes()).getNumeroMes(), ep.getAno())).toLocalDate();
        if (quantidadeMes != null) {
            dateTime = dateTime.minusMonths(quantidadeMes);
        }
        EventoTotalItemFicha totalItemFicha = null;

        switch (modo) {
            case FERIAS:
                if (TipoBusca.REFERENCIA.equals(tipo)) {
                    totalItemFicha = new EventoTotalItemFicha(null, BigDecimal.ZERO, BigDecimal.valueOf(somaReferenciaEventosNoPeriodoAquisitoFeriasConcedidaPorEventoTipoCalculo(eventos, tiposFolha)));
                } else {
                    totalItemFicha = new EventoTotalItemFicha(null, BigDecimal.valueOf(somaEventosPeriodoAquisitivoFeriasConcedidasPorEventoTipoCalculo(eventos, tiposFolhaDePagamento)));
                }
                break;
            case FERIASPROP:
                if (TipoBusca.REFERENCIA.equals(tipo)) {
                    totalItemFicha = new EventoTotalItemFicha(null, BigDecimal.ZERO, BigDecimal.valueOf(funcoesFolhaFacade.somaEventosFeriasProporcionais(ep, true, tiposFolha, this, converterEventosParaStrings(eventos))));
                } else {
                    totalItemFicha = new EventoTotalItemFicha(null, BigDecimal.valueOf(funcoesFolhaFacade.somaEventosFeriasProporcionais(ep, false, tiposFolha, this, converterEventosParaStrings(eventos))));
                }
                break;
            case FERIASVENC:
                if (TipoBusca.REFERENCIA.equals(tipo)) {
                    totalItemFicha = new EventoTotalItemFicha(null, BigDecimal.ZERO, BigDecimal.valueOf(funcoesFolhaFacade.somaEventosFeriasVencidas(ep, true, tiposFolha, this, converterEventosParaStrings(eventos))));
                } else {
                    totalItemFicha = new EventoTotalItemFicha(null, BigDecimal.valueOf(funcoesFolhaFacade.somaEventosFeriasVencidas(ep, false, tiposFolha, this, converterEventosParaStrings(eventos))));
                }
                break;
            case FERIASDOBR:
                if (TipoBusca.REFERENCIA.equals(tipo)) {
                    totalItemFicha = new EventoTotalItemFicha(null, BigDecimal.ZERO, BigDecimal.valueOf(funcoesFolhaFacade.somaEventosFeriasDobradas(ep, true, tiposFolha, this, converterEventosParaStrings(eventos))));
                } else {
                    totalItemFicha = new EventoTotalItemFicha(null, BigDecimal.valueOf(funcoesFolhaFacade.somaEventosFeriasDobradas(ep, false, tiposFolha, this, converterEventosParaStrings(eventos))));
                }
                break;
            case FERIASAUT13:
                if (TipoBusca.REFERENCIA.equals(tipo)) {
                    totalItemFicha = new EventoTotalItemFicha(null, BigDecimal.ZERO, BigDecimal.valueOf(somaReferenciaEventosNoPeriodoAquisitoTercoAutomaticoPorEventoTipoCalculo(eventos, tiposFolha)));
                } else {
                    totalItemFicha = new EventoTotalItemFicha(null, BigDecimal.valueOf(somaEventosPeriodoAquisitivoTercoAutomaticoPorEventoTipoCalculo(eventos, tiposFolhaDePagamento)));
                }
                break;
            case ULTIMOSMESES:
                totalItemFicha = getTotalItemFicha(eventos, limitarAno, considerarMesAtual, tipo, tiposFolhaDePagamento, dateTime, memoriasCalculo);
                break;
            case SALARIO_13:
                totalItemFicha = getTotalItemFicha(eventos, true, true, tipo, tiposFolhaDePagamento, dateTime.minusMonths(12), memoriasCalculo);
                break;
            default:
        }
        BigDecimal valor = BigDecimal.ZERO;
        if (totalItemFicha != null) {
            valor = TipoBusca.REFERENCIA.equals(tipo) ? totalItemFicha.getReferencia() : totalItemFicha.getTotal();
        }
        logger.debug("Resultado: [{}]", totalItemFicha);
        valorMetodo.put(metodo, valor.doubleValue());
        return valor.doubleValue();
    }

    private EventoTotalItemFicha getTotalItemFicha(List<EventoTipoCaclulo> eventos, Boolean limitarAno, Boolean considerarMesAtual, TipoBusca tipo, List<TipoFolhaDePagamento> tiposFolhaDePagamento, LocalDate dateTime, List<MemoriaCalculoRHDTO> memoriasCalculo) {
        EventoTotalItemFicha total = new EventoTotalItemFicha();
        List<String> eventosString = new ArrayList<>();
        for (EventoTipoCaclulo tipoCaclulo : eventos) {
            eventosString.add(tipoCaclulo.getCodigo());
        }

        List<EventoTotalItemFicha> totalItemFicha = funcoesFolhaFacade.recuperarValorFichaPorParametro(eventosString, ep, dateTime, ep.getAno(), tipo, tiposFolhaDePagamento, limitarAno, considerarMesAtual);

        for (EventoTotalItemFicha eventoTotalItemFicha : totalItemFicha) {
            for (EventoTipoCaclulo evento : eventos) {
                if (eventoTotalItemFicha.getEvento().getCodigo().equals(evento.getCodigo()) && evento.getTipoCalculoFP().equals(eventoTotalItemFicha.getTipoCalculoFP())) {
                    total.setTotal(total.getTotal().add(eventoTotalItemFicha.getTotal()));
                    total.setReferencia(total.getReferencia().add(eventoTotalItemFicha.getReferencia()));
                    if (eventoTotalItemFicha.getEvento() != null && eventoTotalItemFicha.getTotal().compareTo(BigDecimal.ZERO) != 0) {
                        memoriasCalculo.add(new MemoriaCalculoRHDTO(null, eventoTotalItemFicha.getEvento(), TipoMemoriaCalculo.BASEFP, eventoTotalItemFicha.getEvento().getTipoEventoFP().equals(TipoEventoFP.DESCONTO) ? OperacaoFormula.SUBTRACAO : OperacaoFormula.ADICAO, eventoTotalItemFicha.getTotal()));
                    }
                }
            }
        }

        if (considerarMesAtual && totalItemFicha != null) {
            for (EventoTipoCaclulo evento : eventos) {
                if (TipoCalculoFP.NORMAL.equals(evento.tipoCalculoFP)) {
                    EventoFP eventoFP = funcoesFolhaFacade.recuperaEvento(evento.codigo, TipoExecucaoEP.FOLHA);
                    if (this.getEventoValorFormula().containsKey(eventoFP)) {
                        BigDecimal valor = this.getEventoValorFormula().get(eventoFP);
                        if (valor != null && valor.compareTo(BigDecimal.ZERO) != 0) {
                            total.setTotal(total.getTotal().add(valor));
                            memoriasCalculo.add(new MemoriaCalculoRHDTO(null, eventoFP, TipoMemoriaCalculo.BASEFP, eventoFP.getTipoEventoFP().equals(TipoEventoFP.DESCONTO) ? OperacaoFormula.SUBTRACAO : OperacaoFormula.ADICAO, valor));
                        }
                    }
                    if (this.getEventoValorReferencia().containsKey(eventoFP)) {
                        BigDecimal referencia = this.getEventoValorReferencia().get(eventoFP);
                        if (referencia != null && referencia.compareTo(BigDecimal.ZERO) != 0) {
                            total.setReferencia(total.getReferencia().add(this.getEventoValorReferencia().get(eventoFP)));
                        }
                    }
                }
            }
        }
        return total;
    }


    @DescricaoMetodo(value = "buscarValorPorFolhaEvento('VALOR', new Array('NORMAL'), new Array('1','10', '13', '4'))", documentacao = "Função:\n" +
        "\tcalculador.buscarValorPorFolhaEvento(String tipoBusca, String[] tiposFolha, String[] eventos);\n" +
        "\t    1º Parametro: tipoBusca\n" +
        "\t    \tVALOR = Busca todos os valores baseados no campo valor da ficha financeira;\n" +
        "\t    \tREFERENCIA = Busca todos os valores baseados no campo referencia da ficha financeira\n" +
        "\t    2º Parametro: tiposFolha\n" +
        "\t    \tUtilizado para filtrar que tipo de folha será utilizado no filtro, quando informado \"null\", o sistema irá buscar por todos os tipos.\n" +
        "\t    \tSintaxe new Array('NORMAL','COMPLEMENTAR')\n" +
        "\n" +
        "\t    3º Parametro: eventos\n" +
        "\t    \tDiz qual os eventos serão utilizados para realizar a busca.\n")
    public Double buscarValorPorFolhaEvento(String tipoBusca, String[] tiposFolha, String[] eventos) {
        String metodo = "buscarValorPorFolhaEvento".concat((tiposFolha != null ? Arrays.asList(tiposFolha) : "") + tipoBusca + (eventos != null ? Arrays.asList(eventos).toString() : ""));
        logger.debug("Parametros buscarValorPorFolhaEvento [{}]", (tiposFolha != null ? Arrays.asList(tiposFolha) : "") + tipoBusca + (eventos != null ? Arrays.asList(eventos).toString() : ""));
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        if (eventos == null) {
            return 0.0;
        }
        TipoBusca tipo = TipoBusca.getTipoPorName(tipoBusca);
        LocalDate dateTime = dateTimeToLocalDateTime(DataUtil.criarDataComMesEAno(Mes.getMesToInt(ep.getMes()).getNumeroMes(), ep.getAno())).toLocalDate();

        List<TipoFolhaDePagamento> tiposFolhaDePagamento = TipoFolhaDePagamento.convertStringToTipoFolha(tiposFolha);
        EventoTotalItemFicha total = new EventoTotalItemFicha();
        List<EventoTotalItemFicha> totalItemFicha = funcoesFolhaFacade.recuperarValorFichaPorParametro(Arrays.asList(eventos), ep, dateTime, ep.getAno(), tipo, tiposFolhaDePagamento, false, true);

        for (EventoTotalItemFicha eventoTotalItemFicha : totalItemFicha) {
            for (String evento : eventos) {
                if (eventoTotalItemFicha.getEvento().getCodigo().equals(evento)) {
                    total.setTotal(total.getTotal().add(eventoTotalItemFicha.getTotal()));
                }
            }
        }

        BigDecimal valor = BigDecimal.ZERO;
        if (total != null) {
            valor = TipoBusca.REFERENCIA.equals(tipo) ? total.getReferencia() : total.getTotal();
        }
        logger.debug("Resultado: [{}]", totalItemFicha);
        valorMetodo.put(metodo, valor.doubleValue());
        return valor.doubleValue();

    }

    @DescricaoMetodo("duracaoBasePeriodoAquisitivo(tipoPeriodoAquisitivo)")
    public Double duracaoBasePeriodoAquisitivo(String tipoPeriodoAquisitivo) {
        String metodo = "duracaoBasePeriodoAquisitivo".concat(tipoPeriodoAquisitivo);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        Integer valor = funcoesFolhaFacade.getDuracaoBasesPeriodoAquisitivo(getEp(), tipoPeriodoAquisitivo);
        valorMetodo.put(metodo, valor.doubleValue());
        return valor.doubleValue();
    }

    @DescricaoMetodo("diasDireitoBasePA('tipoPeriodoAquisitivo')")
    public Double diasDireitoBasePA(String tipoPeriodoAquisitivo) {
        String metodo = "diasDireitoBasePA".concat(tipoPeriodoAquisitivo);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }
        Integer valor = funcoesFolhaFacade.buscarDiasDireitoBasePA(getEp(), tipoPeriodoAquisitivo);
        valorMetodo.put(metodo, valor.doubleValue());
        return valor.doubleValue();
    }

    @DescricaoMetodo("percentualAcessoFG(tipoFG)")
    public double percentualAcessoFG(String tipoFG) {
        String metodo = "percentualAcessoFG".concat(tipoFG);
        if (valorMetodo.containsKey(metodo)) {
            return (double) valorMetodo.get(metodo);
        }
        double valor = funcoesFolhaFacade.buscarPercentualFuncaoGratificada(TipoFuncaoGratificada.valueOf(tipoFG), (VinculoFP) getEp(), getDataReferenciaCompleta());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("tetoRemuneratorio()")
    public ReferenciaFPFuncao tetoRemuneratorio() {
        String metodo = "tetoRemuneratorio";
        if (valorMetodo.containsKey(metodo)) {
            return (ReferenciaFPFuncao) valorMetodo.get(metodo);
        }
        ReferenciaFPFuncao valor =  funcoesFolhaFacade.tetoRemuneratorio(ep);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("enquadramentoPCCR('PLANO','CATEGORIA','PROGRESSÃO','NÍVEL','REFERÊNCIA')")
    public Double enquadramentoPCCR(String plano, String categoria, String progressao, String nivel, String referencia) {
        String metodo = "enquadramentoPCCR".concat(plano).concat(categoria).concat(progressao).concat(nivel).concat(referencia);
        if (valorMetodo.containsKey(metodo)) {
            return (Double) valorMetodo.get(metodo);
        }

        Double valor = funcoesFolhaFacade.enquadramentoPCCR(getEp(), plano, categoria, progressao, nivel, referencia);
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("progressaoServidor()")
    public String progressaoServidor() {
        String metodo = "progressaoServidor";
        if (valorMetodo.containsKey(metodo)) {
            return (String) valorMetodo.get(metodo);
        }

        String valor = funcoesFolhaFacade.progressaoServidor(getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    public enum TipoBuscaRetroacao {
        LER_BASE,
        COM_RETROACAO,
        SEM_RETROACAO;
    }
    public enum TipoBusca {
        VALOR,
        REFERENCIA;

        public static TipoBusca getTipoPorName(String descricao) {
            if (descricao != null) {
                for (TipoBusca modoBusca : TipoBusca.values()) {
                    if (descricao.equals(modoBusca.name())) {
                        return modoBusca;
                    }
                }
            }
            return null;
        }

    }

    public enum ModoBusca {
        ULTIMOSMESES,
        FERIAS,
        SALARIO_13,
        FERIASPROP,
        FERIASVENC,
        FERIASDOBR,
        FERIASAUT13;

        public static ModoBusca getTipoPorName(String descricao) {
            if (descricao != null) {
                for (ModoBusca modoBusca : ModoBusca.values()) {
                    if (descricao.equals(modoBusca.name())) {
                        return modoBusca;
                    }
                }
            }
            return null;
        }

    }

    public List<EventoTotalItemFicha> getItemPagosNaFolhaNormal() {
        return itemPagosNaFolhaNormal;
    }

    public void setItemPagosNaFolhaNormal(List<EventoTotalItemFicha> itemPagosNaFolhaNormal) {
        this.itemPagosNaFolhaNormal = itemPagosNaFolhaNormal;
    }

    public Map<String, List<LancamentoFP>> getLancamentosReferenciaJaCalculados() {
        return lancamentosReferenciaJaCalculados;
    }

    public void setLancamentosReferenciaJaCalculados(Map<String, List<LancamentoFP>> lancamentosReferenciaJaCalculados) {
        this.lancamentosReferenciaJaCalculados = lancamentosReferenciaJaCalculados;
    }

    public Map<EventoFP, LancamentoFP> getLancamentos() {
        return lancamentos;
    }

    public class EventoTipoCaclulo{
        private String codigo;
        private TipoCalculoFP tipoCalculoFP;

        public EventoTipoCaclulo() {
        }

        public EventoTipoCaclulo(String codigo, TipoCalculoFP tipoCalculoFP) {
            this.codigo = codigo;
            this.tipoCalculoFP = tipoCalculoFP;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public TipoCalculoFP getTipoCalculoFP() {
            return tipoCalculoFP;
        }

        public void setTipoCalculoFP(TipoCalculoFP tipoCalculoFP) {
            this.tipoCalculoFP = tipoCalculoFP;
        }
    }
}
