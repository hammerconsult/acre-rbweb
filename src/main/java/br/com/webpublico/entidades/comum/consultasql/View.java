package br.com.webpublico.entidades.comum.consultasql;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by renatoromanini on 01/09/15.
 */
public class View {

    private static final Integer MAXIMO_REGISTROS_TABELA = 0;
    private List<HistoricoConsultarSql> historicos;
    private UsuarioSistema usuarioSistema;
    //consulta
    private String sql;
    private String motivo;
    private Integer quantidadeDeLinhasAlteradas;
    private List<ParametroView> parametros;
    private List<ObjetoView> objetos;
    private List<ColunaView> colunas;

    //totalizador
    private Integer inicio;
    private Integer totalDeRegistrosExistentes;
    private Integer maximoRegistrosTabela;

    //tempo
    private Long inicioExecucao;
    private Long fimExecucao;
    private String tempoExecucao;

    //relatorio
    private String nomeArquivo;
    private String titulo;

    public View() {
        this.parametros = Lists.newArrayList();
        this.objetos = Lists.newArrayList();
        this.colunas = Lists.newArrayList();
        this.historicos = Lists.newArrayList();
        this.inicio = 0;
        this.maximoRegistrosTabela = MAXIMO_REGISTROS_TABELA;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<ParametroView> getParametros() {
        return parametros;
    }

    public void setParametros(List<ParametroView> parametros) {
        this.parametros = parametros;
    }

    public List<ObjetoView> getObjetos() {
        return objetos;
    }

    public void setObjetos(List<ObjetoView> objetos) {
        this.objetos = objetos;
    }

    public List<ColunaView> getColunas() {
        return colunas;
    }

    public void setColunas(List<ColunaView> colunas) {
        this.colunas = colunas;
    }

    public Integer getInicio() {
        return inicio;
    }

    public void setInicio(Integer inicio) {
        this.inicio = inicio;
    }

    public Integer getTotalDeRegistrosExistentes() {
        return totalDeRegistrosExistentes;
    }

    public void setTotalDeRegistrosExistentes(Integer totalDeRegistrosExistentes) {
        this.totalDeRegistrosExistentes = totalDeRegistrosExistentes;
    }

    public Integer getMaximoRegistrosTabela() {
        return maximoRegistrosTabela;
    }

    public void setMaximoRegistrosTabela(Integer maximoRegistrosTabela) {
        this.maximoRegistrosTabela = maximoRegistrosTabela;
    }

    public Long getInicioExecucao() {
        return inicioExecucao;
    }

    public void setInicioExecucao(Long inicioExecucao) {
        this.inicioExecucao = inicioExecucao;
    }

    public Long getFimExecucao() {
        return fimExecucao;
    }

    public void setFimExecucao(Long fimExecucao) {
        this.fimExecucao = fimExecucao;
    }

    public String getTempoExecucao() {
        return tempoExecucao;
    }

    public void setTempoExecucao(String tempoExecucao) {
        this.tempoExecucao = tempoExecucao;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void limparVariaveis() {
        this.parametros = Lists.newArrayList();
        this.objetos = Lists.newArrayList();
        this.colunas = Lists.newArrayList();
        this.quantidadeDeLinhasAlteradas = null;
    }

    public String recuperarSomaColuna(ColunaView colunaView) {
        try {
            BigDecimal valor = BigDecimal.ZERO;
            for (ObjetoView objetoView : this.getObjetos()) {
                for (ColunaView c : objetoView.getColunas()) {
                    if (c.getNomeColuna().equals(colunaView.getNomeColuna())) {
                        valor = valor.add((BigDecimal) c.getObjeto());
                    }
                }
            }
            return "TOTAL " + colunaView.getNomeColuna() + ": " + Util.getValorRemovendoRS(valor);
        } catch (Exception e) {
            return "";
        }
    }

    public Integer getQuantidadeDeLinhasAlteradas() {
        return quantidadeDeLinhasAlteradas;
    }

    public void setQuantidadeDeLinhasAlteradas(Integer quantidadeDeLinhasAlteradas) {
        this.quantidadeDeLinhasAlteradas = quantidadeDeLinhasAlteradas;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public List<HistoricoConsultarSql> getHistoricos() {
        return historicos;
    }

    public void setHistoricos(List<HistoricoConsultarSql> historicos) {
        this.historicos = historicos;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getSqlRemovendoBarraNEBarraR() {
        return getSql().replace("\n", " ").replace("\r", " ");
    }

    public boolean isSqlPesquisar() {
        return sql != null && !sql.isEmpty() &&
            (sql.trim().toUpperCase().startsWith("SELECT") || sql.trim().toUpperCase().startsWith("WITH"));
    }
}
