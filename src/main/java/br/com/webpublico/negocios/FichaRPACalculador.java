/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EntidadePagavelRH;
import br.com.webpublico.interfaces.NomeObjetosConstantesScriptFP;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.util.anotacoes.DescricaoMetodo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


public class FichaRPACalculador implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(FichaRPACalculador.class);

    private ScriptEngine engine;
    private EntidadePagavelRH ep;
    private FuncoesFolhaFacade funcoesFolhaFacade;
    private FuncoesFichaRPA funcoesFichaRPA;
    private Integer mes;
    private Integer ano;
    private FichaRPA fichaRPA;
    private Map<String, BigDecimal> baseValor;
    private Map<String, Object> valorMetodo;

    public FichaRPACalculador(EntidadePagavelRH ep, FolhaDePagamentoNovoCalculador folhaDePagamentoNovoCalculador, FuncoesFolhaFacade funcoesFolhaFacade, FichaRPA fichaRPA, java.time.LocalDate mesAno, FuncoesFichaRPA funcoesFichaRPA) {
        this.ep = ep;
        this.ep.setMes(mesAno.getMonthValue());
        this.ep.setAno(mesAno.getYear());
        this.ep.setFolha(folhaDePagamentoNovoCalculador.getFolhaDePagamento());
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("JavaScript");
        engine.put(NomeObjetosConstantesScriptFP.NOME_OBJETO_JAVA, folhaDePagamentoNovoCalculador);
        engine.put("funcao", funcoesFolhaFacade);
        engine.put("ep", ep);
        engine.put(NomeObjetosConstantesScriptFP.NOME_OBJETO_JAVA_FICHA_RPA, this);
        engine.put(NomeObjetosConstantesScriptFP.NOME_FICHARPA, fichaRPA);
        engine.put(NomeObjetosConstantesScriptFP.NOME_OBJETO_MES_CALCULO, mesAno.getMonthValue());
        engine.put(NomeObjetosConstantesScriptFP.NOME_OBJETO_ANO_CALCULO, mesAno.getYear());
        engine.put(NomeObjetosConstantesScriptFP.MAP_CALCULO_RETROATIVO, null);
        this.funcoesFolhaFacade = funcoesFolhaFacade;
        this.funcoesFichaRPA = funcoesFichaRPA;
        this.mes = mesAno.getMonthValue();
        this.ano = mesAno.getYear();
        this.fichaRPA = fichaRPA;
        this.baseValor = new HashMap<>();
        this.valorMetodo = new HashMap<>();
    }

    public ScriptEngine getEngine() {
        return engine;
    }

    public void setEngine(ScriptEngine engine) {
        this.engine = engine;
    }

    @DescricaoMetodo("buscarValorBaseOutrosVinculos('codigoBase')")
    public double buscarValorBaseOutrosVinculos(String codigoBase) throws Exception {
        String metodo = "buscarValorBaseOutrosVinculos" + codigoBase + ep.getIdCalculo();
        if (valorMetodo.containsKey(metodo)) {
            return (double) valorMetodo.get(metodo);
        }
        double valor = funcoesFichaRPA.buscarValorBaseOutrosVinculos(codigoBase, getEp(), getMes(), getAno(), getFichaRPA());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("valorTotalEventoOutrosVinculos('codigoEventoFP')")
    public Double valorTotalEventoOutrosVinculos(String codigo) {
        String metodo = "valorTotalEventoOutrosVinculos" + codigo + ep.getIdCalculo();
        if (valorMetodo.containsKey(metodo)) {
            return (double) valorMetodo.get(metodo);
        }
        double valor = funcoesFichaRPA.valorTotalEventoOutrosVinculos(codigo, getEp(), getMes(), getAno());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    @DescricaoMetodo("calculaBase('codigoBase')")
    public double calculaBase(String codigoBase) throws Exception {
        if (baseValor.containsKey(codigoBase)) {
            logger.debug("valor da base: " + codigoBase + " : " + baseValor.get(codigoBase).doubleValue());
            return baseValor.get(codigoBase).doubleValue();
        }
        BigDecimal soma = BigDecimal.ZERO;
        List<ItemBaseFP> itensBaseFP = funcoesFichaRPA.buscarItensBase(codigoBase);
        for (ItemBaseFP item : itensBaseFP) {
            EventoFP eventoFP = item.getEventoFP();
            BigDecimal valor = BigDecimal.ZERO;

            if (eventoFP.getAutomatico() && eventoFP.getAtivo() && avaliaRegra(eventoFP)) { // São calculadas apenas as fórmulas de eventos automáticos
                valor = avaliaFormulaDoEventoFP(eventoFP);
            } else {
                continue;
            }

            OperacaoFormula opItem = item.getOperacaoFormula();
            if (opItem.equals(OperacaoFormula.ADICAO)) {
                soma = soma.add(valor);
            } else {
                soma = soma.subtract(valor);
            }
        }

        if (Util.isValorNegativo(soma)) {
            soma = BigDecimal.ZERO;
        }

        soma = soma.setScale(10, RoundingMode.HALF_UP);
        baseValor.put(codigoBase, soma);
        return soma.doubleValue();
    }

    @DescricaoMetodo("avaliaReferencia('codigoEvento')")
    public double avaliaReferencia(String codigoEvento) throws Exception {
        String metodo = "avaliaReferencia" + codigoEvento;
        if (valorMetodo.containsKey(metodo)) {
            return (double) valorMetodo.get(metodo);
        }
        EventoFP evento = funcoesFichaRPA.recuperaEventoPorCodigo(codigoEvento);
        if (avaliaRegra(evento)) {
            double valor = avaliaReferenciaDoEventoFP(evento).doubleValue();
            valorMetodo.put(metodo, valor);
            return valor;
        }
        return 0;
    }

    @DescricaoMetodo("obterCategoriaeSocial()")
    public int obterCategoriaeSocial() {
        String metodo = "obterCategoriaeSocial" + getEp().getIdCalculo();
        if (valorMetodo.containsKey(metodo)) {
            return (int) valorMetodo.get(metodo);
        }
        Integer valor = funcoesFichaRPA.recuperaCategoriaeSocial(getEp());
        if (valor != null) {
            valorMetodo.put(metodo, valor);
            return valor;
        }
        return 0;
    }

    @DescricaoMetodo("avaliaEvento(codigoEvento)")
    public double avaliaEvento(String codigoEvento) throws Exception {
        EventoFP evento = funcoesFolhaFacade.recuperaEvento(codigoEvento, TipoExecucaoEP.RPA);
        if (avaliaRegra(evento)) {
            return avaliaFormulaDoEventoFP(evento).doubleValue();
        }
        return 0;
    }

    @DescricaoMetodo("obterNumeroDependentes(codigoTipoDepend)")
    public int obterNumeroDependentes(String codigoTipoDepend) {
        String metodo = "obterNumeroDependentes" + codigoTipoDepend + getEp().getIdCalculo();
        if (valorMetodo.containsKey(metodo)) {
            return (int) valorMetodo.get(metodo);
        }
        Integer valor = funcoesFichaRPA.obterNumeroDependentes(codigoTipoDepend, getEp());
        valorMetodo.put(metodo, valor);
        return valor;
    }

    public BigDecimal avaliaReferenciaDoEventoFP(EventoFP eventoFP) {
        BigDecimal valor = new BigDecimal(avaliaFormulaReferencia(eventoFP));
        return valor;
    }

    private double avaliaFormulaReferencia(EventoFP evento) {
        double resultado = 0;
        try {
            String formula = "function formula(){\n" + evento.getReferenciaCalculo() + "\n}\nformula();";
            resultado = ((Number) engine.eval(formula)).doubleValue();
            if (Double.isNaN(resultado)) {
                logger.error("Problemas no Evento " + evento + " \nReferencia\n" + evento.getReferencia() + "\nex:NumberFormatException");
            }
        } catch (Exception ex) {
            logger.error("Problemas no Evento " + evento + " \nReferencia\n" + evento.getReferencia() + "\nex:", ex);
        }
        return resultado;
    }

    public boolean avaliaRegra(EventoFP evento) {
        boolean resultado = false;
        try {
            String regra = "function formula(){\n" + evento.getRegraCalculo() + "\n}\nformula();";
            resultado = (boolean) engine.eval(regra);
        } catch (Exception ex) {
            logger.error("Problemas no Evento " + evento + " \nRegra\n" + evento.getRegra() + "\nex:", ex);
        }
        return resultado;
    }

    public BigDecimal avaliaFormulaDoEventoFP(EventoFP eventoFP) {
        BigDecimal valor;
        try {
            if (eventoFP.getArredondarValor()) {
                valor = BigDecimal.valueOf(avaliaFormula(eventoFP)).setScale(2, BigDecimal.ROUND_HALF_UP);
            } else {
                valor = UtilRH.truncate(String.valueOf(avaliaFormula(eventoFP)));
            }
        } catch (NumberFormatException nfe) {
            logger.error("NumberFormatException ao executar o método avaliaFormula(evento) erro: ", nfe);
            valor = BigDecimal.ZERO;
        }
        return valor;
    }

    private double avaliaFormula(EventoFP evento) {
        double resultado = 0;
        try {

            String formula = "function formula(){\n" + evento.getFormulaCalculo() + "\n}\nformula();";

            resultado = ((Number) engine.eval(formula)).doubleValue();
            if (Double.isNaN(resultado)) {
                logger.error("Problemas no Evento " + evento + " \nFormula\n" + evento.getFormula() + "\nex: NumberFormatException");
                resultado = 0;
            }

        } catch (Exception ex) {
            logger.error("Problemas no Evento " + evento + " \nFormula\n" + evento.getFormula() + "\nex:", ex);
            return 0;
        }

        return resultado;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public FichaRPA getFichaRPA() {
        return fichaRPA;
    }

    public EntidadePagavelRH getEp() {
        return ep;
    }
}
