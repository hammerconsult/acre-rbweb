/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DependenciasDirf extends AssistenteBarraProgresso {

    protected static final Logger logger = LoggerFactory.getLogger(DependenciasDirf.class);
    @Deprecated
    private List<String> log;
    private List<String> logIncosistencia;
    private Boolean parado;
    private HashMap<TipoLog, List<String>> logGeral;
    private String caminhoRelatorio;
    private String caminhoImagem;

    public DependenciasDirf() {
        super();
    }

    public HashMap<TipoLog, List<String>> getLogGeral() {
        if (logGeral == null) {
            logGeral = new HashMap<>();
        }
        return logGeral;
    }

    public void setLogGeral(HashMap<TipoLog, List<String>> logGeral) {
        this.logGeral = logGeral;
    }

    public void iniciarProcesso() {
        super.zerarContadoresProcesso();
        this.log = new ArrayList<>();
        this.logIncosistencia = new ArrayList<>();
        this.parado = Boolean.FALSE;
    }

    @Deprecated
    public List<String> getLog() {
        return log;
    }

    @Deprecated
    public void setLog(List<String> log) {
        this.log = log;
    }

    public List<String> getLogIncosistencia() {
        return logIncosistencia;
    }

    public void setLogIncosistencia(List<String> logIncosistencia) {
        this.logIncosistencia = logIncosistencia;
    }


    public void pararProcessamento() {
        this.parado = Boolean.TRUE;
        super.removerProcessoDoAcompanhamento();
    }

    public Boolean getParado() {
        return parado;
    }

    public void setParado(Boolean parado) {
        this.parado = parado;
    }

    public String getSomenteStringDoLog() {
        try {
            return this.log.toString().replace("[", "").replace("]", "").replace(",", "");
        } catch (ConcurrentModificationException cme) {
            corrigeLog();
            return "";
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public String recuperarSomenteStringDoLog(TipoLog tipoLog) {
        try {
            return getLogGeral().get(tipoLog).toString().replace("[", "").replace("]", "").replace(",", "").replace("<br/> ", "<br/>");
        } catch (ConcurrentModificationException cme) {
            return "";
        } catch (NullPointerException npe) {
            return "Nenhuma mensagem de log encontrada.";
        }
    }

    public String recuperarSomenteStringDoLogSemMsgErro(TipoLog tipoLog) {
        try {
            return getLogGeral().get(tipoLog).toString().replace("[", "").replace("]", "").replace(",", "").replace("<br/> ", "<br/>");
        } catch (ConcurrentModificationException cme) {
            return "";
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public String recuperarSomenteStringDoLogEstruturado() {
        return recuperarSomenteStringDoLogSemMsgErro(DependenciasDirf.TipoLog.CABECALHO) +
            recuperarSomenteStringDoLog(DependenciasDirf.TipoLog.CORPO) +
            recuperarSomenteStringDoLogSemMsgErro(DependenciasDirf.TipoLog.RODAPE);
    }

    public String getStringLogIncosistencia() {
        try {
            return this.logIncosistencia.toString().replace("[", "").replace("]", "").replace(",", "");
        } catch (ConcurrentModificationException cme) {
            corrigirLogIncosistencia();
            return "";
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public void corrigeLog() {
        List<String> copia = new ArrayList<String>();
        copia.addAll(log);
        log = new ArrayList<String>();
        log.addAll(copia);
    }

    public void corrigirLogIncosistencia() {
        List<String> copia = new ArrayList<String>();
        copia.addAll(logIncosistencia);
        logIncosistencia = new ArrayList<String>();
        logIncosistencia.addAll(copia);
    }

    public void adicionarLog(TipoLog tipo, String conteudo) {
        if (getLogGeral().get(tipo) == null) {
            getLogGeral().put(tipo, new ArrayList<String>());
        }

        if (getLogGeral().get(TipoLog.TODOS) == null) {
            getLogGeral().put(TipoLog.TODOS, new ArrayList<String>());
        }

        String agora = Util.dateHourToString(new Date());
        conteudo = "<span>" + agora + " - <font style='color : " + tipo.getCor() + "'>" + conteudo + "</font></span><br/>";

        getLogGeral().get(tipo).add(conteudo);
        if (!tipo.equals(TipoLog.TODOS)) {
            getLogGeral().get(TipoLog.TODOS).add(conteudo);
        }
    }

    public String getCaminhoRelatorio() {
        return caminhoRelatorio;
    }

    public void setCaminhoRelatorio(String caminhoRelatorio) {
        this.caminhoRelatorio = caminhoRelatorio;
    }

    public String getCaminhoImagem() {
        return caminhoImagem;
    }

    public void setCaminhoImagem(String caminhoImagem) {
        this.caminhoImagem = caminhoImagem;
    }

    public void setTotalCadastros(int i) {
        setTotal(i);
    }

    public Integer getTotalCadastros() {
        return getTotal();
    }

    public void setContadorProcessos(int i) {
        setCalculados(i);
    }

    public Integer getContadorProcessos() {
        return getCalculados();
    }

    public enum TipoLog {
        TODOS("Todos", ""),
        SUCESSO("Sucesso", "green"),
        ALERTA("Alerta", "#f89406"),
        ERRO("Erro", "red"),
        CABECALHO("Cabeçalho", ""),
        CORPO("Corpo", ""),
        RODAPE("Rodapé", "");
        private String descricao;
        private String cor;

        TipoLog(String descricao, String cor) {
            this.descricao = descricao;
            this.cor = cor;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getCor() {
            return cor;
        }

        @Override
        public String toString() {
            return descricao;
        }

        public boolean isSucesso() {
            return SUCESSO.equals(name());
        }

        public boolean isAlerta() {
            return ALERTA.equals(name());
        }

        public boolean isErro() {
            return ERRO.equals(name());
        }

        public boolean isTodos() {
            return TODOS.equals(name());
        }
    }
}
