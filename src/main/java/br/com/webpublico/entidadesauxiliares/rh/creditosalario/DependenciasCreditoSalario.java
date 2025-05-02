package br.com.webpublico.entidadesauxiliares.rh.creditosalario;

import br.com.webpublico.entidades.GrupoRecursoFP;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.rh.creditodesalario.CreditoSalario;
import br.com.webpublico.entidadesauxiliares.DependenciasDirf;
import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DependenciasCreditoSalario implements Serializable {


    protected static final Logger logger = LoggerFactory.getLogger(DependenciasDirf.class);
    private Integer totalCadastros;
    private Integer contadorProcessos;
    private Integer contadorLinha;
    private Map<GrupoRecursoFP, Set<VinculoCreditoSalario>> vinculos;
    private List<String> logIncosistencia;
    private List<String> logIncosistenciaGeral;
    private StringBuilder logDetalhado;
    private Long iniciadoEm;
    private Boolean parado;
    private Boolean gerarLogDetalhado;
    private HashMap<TipoLog, List<String>> logGeral;
    private UsuarioSistema usuarioSistema;
    private String caminhoRelatorio;
    private String caminhoImagem;
    private String processoAtual;
    Future<DependenciasCreditoSalario> future;
    private CreditoSalario creditoSalario;


    public void iniciarProcesso() {
        this.logIncosistencia = Lists.newArrayList();
        this.logIncosistenciaGeral = Lists.newArrayList();
        this.contadorProcessos = 0;
        this.contadorLinha = 1;
        this.totalCadastros = 0;
        this.iniciadoEm = System.currentTimeMillis();
        this.parado = Boolean.FALSE;
        this.vinculos = Maps.newLinkedHashMap();
    }

    public void reiniciarLogs() {
        this.logIncosistencia = Lists.newArrayList();
        this.logDetalhado = new StringBuilder();
    }

    public synchronized void incrementarContadorProcesso() {
        contadorProcessos++;
    }

    public synchronized void incrementarContadorLinha() {
        contadorLinha++;
    }


    public void pararProcessamento() {
        this.parado = Boolean.TRUE;

        if (future != null) {
            future.cancel(true);
        }
    }


    public DependenciasCreditoSalario() {
        this.vinculos = Maps.newLinkedHashMap();
        logDetalhado = new StringBuilder();
    }

    public Integer getContadorLinha() {
        return contadorLinha;
    }

    public void setContadorLinha(Integer contadorLinha) {
        this.contadorLinha = contadorLinha;
    }

    public StringBuilder getLogDetalhado() {
        return logDetalhado;
    }

    public String getTodosErros() {
        StringBuilder sb = new StringBuilder();
        for (String s : logIncosistencia) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }

    public void setLogDetalhado(StringBuilder logDetalhado) {
        this.logDetalhado = logDetalhado;
    }

    public CreditoSalario getCreditoSalario() {
        return creditoSalario;
    }

    public void setCreditoSalario(CreditoSalario creditoSalario) {
        this.creditoSalario = creditoSalario;
    }

    public Map<GrupoRecursoFP, Set<VinculoCreditoSalario>> getVinculos() {
        return vinculos;
    }

    public void setVinculos(Map<GrupoRecursoFP, Set<VinculoCreditoSalario>> vinculos) {
        this.vinculos = vinculos;
    }

    public static Logger getLogger() {
        return logger;
    }

    public Integer getTotalCadastros() {
        return totalCadastros;
    }

    public void setTotalCadastros(Integer totalCadastros) {
        this.totalCadastros = totalCadastros;
    }

    public synchronized Integer getContadorProcessos() {
        return contadorProcessos;
    }

    public synchronized void setContadorProcessos(Integer contadorProcessos) {
        this.contadorProcessos = contadorProcessos;
    }


    public List<String> getLogIncosistencia() {
        return logIncosistencia;
    }

    public void setLogIncosistencia(List<String> logIncosistencia) {
        this.logIncosistencia = logIncosistencia;
    }

    public List<String> getLogIncosistenciaGeral() {
        return logIncosistenciaGeral;
    }

    public void setLogIncosistenciaGeral(List<String> logIncosistenciaGeral) {
        this.logIncosistenciaGeral = logIncosistenciaGeral;
    }

    public Long getIniciadoEm() {
        return iniciadoEm;
    }

    public void setIniciadoEm(Long iniciadoEm) {
        this.iniciadoEm = iniciadoEm;
    }

    public Boolean getParado() {
        return parado;
    }

    public void setParado(Boolean parado) {
        this.parado = parado;
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

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
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

    public String getProcessoAtual() {
        return processoAtual;
    }

    public void setProcessoAtual(String processoAtual) {
        this.processoAtual = processoAtual;
    }

    public Future<DependenciasCreditoSalario> getFuture() {
        return future;
    }

    public void setFuture(Future<DependenciasCreditoSalario> future) {
        this.future = future;
    }

    public Boolean getGerarLogDetalhado() {
        return gerarLogDetalhado != null && gerarLogDetalhado;
    }

    public void setGerarLogDetalhado(Boolean gerarLogDetalhado) {
        this.gerarLogDetalhado = gerarLogDetalhado;
    }

    public Double getPorcentagemDoCalculo() {
        if (contadorProcessos == null || totalCadastros == null) {
            return 0d;
        }
        return (contadorProcessos.doubleValue() / totalCadastros) * 100;
    }

    public String getTempoDecorrido() {
        long HOUR = TimeUnit.HOURS.toMillis(1);
        long decorrido = (System.currentTimeMillis() - this.iniciadoEm);
        if (decorrido < HOUR) {
            return String.format("%1$TM:%1$TS%n", decorrido);
        } else {
            return String.format("%d:%2$TM:%2$TS%n", decorrido / HOUR, decorrido % HOUR);
        }
    }

    public String getTempoEstimado() {
        long HOUR = TimeUnit.HOURS.toMillis(1);
        long unitario = (System.currentTimeMillis() - this.iniciadoEm) / (this.contadorProcessos + 1);
        Double qntoFalta = Double.parseDouble("" + unitario * (this.totalCadastros - this.contadorProcessos));
        if (qntoFalta < HOUR) {
            return String.format("%1$TM:%1$TS%n", qntoFalta.longValue());
        } else {
            return String.format("%d:%2$TM:%2$TS%n", qntoFalta.longValue() / HOUR, qntoFalta.longValue() % HOUR);
        }
    }

    public enum TipoLog {
        TODOS("Todos", ""),
        SUCESSO("Sucesso", "green"),
        ALERTA("Alerta", "#f89406"),
        ERRO("Erro", "red");
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
