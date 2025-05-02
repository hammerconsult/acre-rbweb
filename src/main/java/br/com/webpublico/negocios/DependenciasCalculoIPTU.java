/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.math.BigDecimal;
import java.util.*;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 *
 * @author Gustavo
 */
public class DependenciasCalculoIPTU {

    private ProcessoCalculoIPTU processoCalculoIPTU;


    private BigDecimal ufmVigente;

    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");

    private Exercicio exercicio;
    private List<Pontuacao> pontuacoes;
    List<ServicoUrbano> servicoUrbanos;
    List<Construcao> construcoes;
    List<CalculoIPTU> calculos;
    Map<Lote, Map<Pontuacao, BigDecimal>> pontuacaoLote;
    Map<Construcao, Map<Pontuacao, BigDecimal>> pontuacaoConstrucao;
    Map<Lote, BigDecimal> areaConstruidaLote;

    public List<ServicoUrbano> getServicoUrbanos() {
        return servicoUrbanos;
    }

    public void setServicoUrbanos(List<ServicoUrbano> servicoUrbanos) {
        this.servicoUrbanos = servicoUrbanos;
    }

    public List<Pontuacao> getPontuacoes() {
        return pontuacoes;
    }

    public void setPontuacoes(List<Pontuacao> pontuacoes) {
        this.pontuacoes = pontuacoes;
    }

    public ProcessoCalculoIPTU getProcessoCalculoIPTU() {
        return processoCalculoIPTU;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public void setProcessoCalculoIPTU(ProcessoCalculoIPTU processoCalculoIPTU) {
        this.processoCalculoIPTU = processoCalculoIPTU;
    }




    public BigDecimal getUfmVigente() {
        return ufmVigente;
    }

    public void setUfmVigente(BigDecimal ufmVigente) {
        this.ufmVigente = ufmVigente;
    }


    public List<CalculoIPTU> getCalculos() {
        if (calculos == null) {
            calculos = Lists.newArrayList();
        }
        return calculos;
    }

    public ScriptEngine getEngine() {
        return engine;
    }

    public void setEngine(ScriptEngine engine) {
        this.engine = engine;
    }

    public void limpaTudo() {
        processoCalculoIPTU = new ProcessoCalculoIPTU();
        calculos = Lists.newArrayList();
//        configuracaoIPTU = new ConfiguracaoIPTU();
        engine = new ScriptEngineManager().getEngineByName("javascript");
        pontuacaoLote = Maps.newHashMap();
        pontuacaoConstrucao = Maps.newHashMap();
        areaConstruidaLote = Maps.newHashMap();
    }

    public Map<Lote, Map<Pontuacao, BigDecimal>> getPontuacaoLote() {
        if(pontuacaoLote == null){
            pontuacaoLote = Maps.newHashMap();
        }
        return pontuacaoLote;
    }

    public Map<Lote, BigDecimal> getAreaConstruidaLote() {
        if(areaConstruidaLote == null){
            areaConstruidaLote = Maps.newHashMap();
        }
        return areaConstruidaLote;
    }

    public Map<Construcao, Map<Pontuacao, BigDecimal>> getPontuacaoConstrucao() {
        if(pontuacaoConstrucao == null){
            pontuacaoConstrucao = Maps.newHashMap();
        }
        return pontuacaoConstrucao;
    }

    public List<Construcao> getConstrucoes() {
        try {
            Collections.sort(construcoes);
        } catch (Exception e) {
        }
        return construcoes;
    }

    public void setConstrucoes(List<Construcao> construcoes) {
        this.construcoes = construcoes;
    }

    void limpaMapas() {
        pontuacaoLote.clear();
        pontuacaoConstrucao.clear();
        areaConstruidaLote.clear();
        calculos.clear();
    }
}
