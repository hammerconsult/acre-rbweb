package br.com.webpublico.consultaentidade;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsultaEntidade {
    String chave;
    String nomeTela;
    String from;
    String groupBy;
    Boolean distinct;
    List<FieldConsultaEntidade> pesquisaveis;
    List<FieldConsultaEntidade> tabelaveis;
    List<Map<String, Object>> resultados;
    List<FiltroConsultaEntidade> filtros;
    List<ActionConsultaEntidade> actionsTabela;
    List<ActionConsultaEntidade> actionsHeader;
    FieldConsultaEntidade identificador;
    FieldConsultaEntidade estiloLinha;
    Map<String, BigDecimal> totalizadores;
    @JsonIgnore
    UsuarioSistema usuarioCorrente;
    @JsonIgnore
    Exercicio exercicioCorrente;
    @JsonIgnore
    UnidadeOrganizacional unidadeOrganizacionalAdministrativaCorrente;
    @JsonIgnore
    UnidadeOrganizacional unidadeOrganizacionalOrcamentariaCorrente;
    @JsonIgnore
    Date dataOperacao;

    int totalRegistros = 0;
    int paginaAtual = 0;
    int registroPorPagina = 10;

    public ConsultaEntidade() {
        pesquisaveis = Lists.newArrayList();
        tabelaveis = Lists.newArrayList();
        resultados = Lists.newArrayList();
        filtros = Lists.newArrayList();
        actionsTabela = Lists.newArrayList();
        actionsHeader = Lists.newArrayList();
        totalizadores = Maps.newHashMap();
        distinct = false;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<FieldConsultaEntidade> getPesquisaveis() {
        return pesquisaveis;
    }

    public void setPesquisaveis(List<FieldConsultaEntidade> pesquisaveis) {
        this.pesquisaveis = pesquisaveis;
    }

    public List<FieldConsultaEntidade> getTabelaveis() {
        return tabelaveis;
    }

    public void setTabelaveis(List<FieldConsultaEntidade> tabelaveis) {
        this.tabelaveis = tabelaveis;
    }

    public List<Map<String, Object>> getResultados() {
        return resultados;
    }

    public void setResultados(List<Map<String, Object>> resultados) {
        this.resultados = resultados;
    }

    public List<FiltroConsultaEntidade> getFiltros() {
        return filtros;
    }

    public void setFiltros(List<FiltroConsultaEntidade> filtros) {
        this.filtros = filtros;
    }

    public int getTotalRegistros() {
        return totalRegistros;
    }

    @JsonIgnore
    public int getTotalPagina() {
        return totalRegistros / registroPorPagina;
    }

    public void setTotalRegistros(int totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public int getPaginaAtual() {
        return paginaAtual;
    }

    public void setPaginaAtual(int paginaAtual) {
        this.paginaAtual = paginaAtual;
    }

    public int getRegistroPorPagina() {
        return registroPorPagina;
    }

    public void setRegistroPorPagina(int registroPorPagina) {
        this.registroPorPagina = registroPorPagina;
    }

    public String getNomeTela() {
        return nomeTela;
    }

    public void setNomeTela(String nomeTela) {
        this.nomeTela = nomeTela;
    }

    public FieldConsultaEntidade getIdentificador() {
        return identificador;
    }

    public void setIdentificador(FieldConsultaEntidade identificador) {
        this.identificador = identificador;
    }

    public FieldConsultaEntidade getEstiloLinha() {
        return estiloLinha;
    }

    public void setEstiloLinha(FieldConsultaEntidade estiloLinha) {
        this.estiloLinha = estiloLinha;
    }

    public List<ActionConsultaEntidade> getActionsTabela() {
        return actionsTabela;
    }

    public List<ActionConsultaEntidade> actionsTabelaPorAlinhamento(TipoAlinhamento tipoAlinhamento) {
        List<ActionConsultaEntidade> poAlinhamento = Lists.newArrayList();
        for (ActionConsultaEntidade actionConsultaEntidade : actionsTabela) {
            if (tipoAlinhamento.equals(actionConsultaEntidade.getAlinhamento())) {
                poAlinhamento.add(actionConsultaEntidade);
            }
        }
        return poAlinhamento;
    }

    public void setActionsTabela(List<ActionConsultaEntidade> actionsTabela) {
        this.actionsTabela = actionsTabela;
    }

    public List<ActionConsultaEntidade> getActionsHeader() {
        return actionsHeader;
    }

    public void setActionsHeader(List<ActionConsultaEntidade> actionsHeader) {
        this.actionsHeader = actionsHeader;
    }

    public String getGroupBy() {
        return groupBy == null ? "" : groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public Map<String, BigDecimal> getTotalizadores() {
        return totalizadores;
    }

    public void setTotalizadores(Map<String, BigDecimal> totalizadores) {
        this.totalizadores = totalizadores;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @JsonIgnore
    public UsuarioSistema getUsuarioCorrente() {
        return usuarioCorrente;
    }

    @JsonIgnore
    public void setUsuarioCorrente(UsuarioSistema usuarioCorrente) {
        this.usuarioCorrente = usuarioCorrente;
    }

    @JsonIgnore
    public Exercicio getExercicioCorrente() {
        return exercicioCorrente;
    }

    @JsonIgnore
    public void setExercicioCorrente(Exercicio exercicioCorrente) {
        this.exercicioCorrente = exercicioCorrente;
    }

    @JsonIgnore
    public UnidadeOrganizacional getUnidadeOrganizacionalAdministrativaCorrente() {
        return unidadeOrganizacionalAdministrativaCorrente;
    }

    @JsonIgnore
    public void setUnidadeOrganizacionalAdministrativaCorrente(UnidadeOrganizacional unidadeOrganizacionalAdministrativaCorrente) {
        this.unidadeOrganizacionalAdministrativaCorrente = unidadeOrganizacionalAdministrativaCorrente;
    }

    @JsonIgnore
    public UnidadeOrganizacional getUnidadeOrganizacionalOrcamentariaCorrente() {
        return unidadeOrganizacionalOrcamentariaCorrente;
    }

    @JsonIgnore
    public void setUnidadeOrganizacionalOrcamentariaCorrente(UnidadeOrganizacional unidadeOrganizacionalOrcamentariaCorrente) {
        this.unidadeOrganizacionalOrcamentariaCorrente = unidadeOrganizacionalOrcamentariaCorrente;
    }

    @JsonIgnore
    public Date getDataOperacao() {
        return dataOperacao;
    }

    @JsonIgnore
    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public Object getValorPesquisavelPorOrdem(Integer ordem) {
        for (FiltroConsultaEntidade filtro : filtros) {
            if(filtro.getValor() != null && filtro.getField().getOrdem().equals(ordem)){
                return filtro.getValorParaQuery();
            }
        }

        return " ";
    }
}
