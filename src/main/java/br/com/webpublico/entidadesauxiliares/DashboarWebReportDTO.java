package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class DashboarWebReportDTO implements Serializable {

    private Integer quantidadeUsuarios;
    private Integer quantidadeRelatorios;
    private String tempoMedioAsString;
    private String tempoMaiorAsString;

    private String usuario;
    private String nomeRelatorio;
    private String status;
    private Integer pagina;
    private Integer quantidade;
    private Integer totalPaginas;
    private List<WebReportRelatorioDTO> relatorios;

    private List<ReportRelatoriosDTO> relatoriosPorRelatorio;
    private List<ReportUsuariosDTO> relatoriosPorUsuario;

    private GraficoAcompanhamentoDiarioDTO graficoAcompanhamentoDiario;

    public DashboarWebReportDTO() {
        this.quantidadeUsuarios = 0;
        this.quantidadeRelatorios = 0;
        this.tempoMedioAsString = "00:00";
        this.tempoMaiorAsString = "00:00";
        this.pagina = 1;
        this.quantidade = 10;
        this.totalPaginas = 0;
        this.relatorios = Lists.newArrayList();
        this.relatoriosPorRelatorio = Lists.newArrayList();
        this.relatoriosPorUsuario = Lists.newArrayList();
    }

    public Integer getQuantidadeUsuarios() {
        return quantidadeUsuarios;
    }

    public void setQuantidadeUsuarios(Integer quantidadeUsuarios) {
        this.quantidadeUsuarios = quantidadeUsuarios;
    }

    public Integer getQuantidadeRelatorios() {
        return quantidadeRelatorios;
    }

    public void setQuantidadeRelatorios(Integer quantidadeRelatorios) {
        this.quantidadeRelatorios = quantidadeRelatorios;
    }

    public String getTempoMedioAsString() {
        return tempoMedioAsString;
    }

    public void setTempoMedioAsString(String tempoMedioAsString) {
        this.tempoMedioAsString = tempoMedioAsString;
    }

    public String getTempoMaiorAsString() {
        return tempoMaiorAsString;
    }

    public void setTempoMaiorAsString(String tempoMaiorAsString) {
        this.tempoMaiorAsString = tempoMaiorAsString;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNomeRelatorio() {
        return nomeRelatorio;
    }

    public void setNomeRelatorio(String nomeRelatorio) {
        this.nomeRelatorio = nomeRelatorio;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPagina() {
        return pagina;
    }

    public void setPagina(Integer pagina) {
        this.pagina = pagina;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getTotalPaginas() {
        return totalPaginas;
    }

    public void setTotalPaginas(Integer totalPaginas) {
        this.totalPaginas = totalPaginas;
    }

    public List<WebReportRelatorioDTO> getRelatorios() {
        return relatorios;
    }

    public void setRelatorios(List<WebReportRelatorioDTO> relatorios) {
        this.relatorios = relatorios;
    }

    public List<ReportRelatoriosDTO> getRelatoriosPorRelatorio() {
        return relatoriosPorRelatorio;
    }

    public void setRelatoriosPorRelatorio(List<ReportRelatoriosDTO> relatoriosPorRelatorio) {
        this.relatoriosPorRelatorio = relatoriosPorRelatorio;
    }

    public List<ReportUsuariosDTO> getRelatoriosPorUsuario() {
        return relatoriosPorUsuario;
    }

    public void setRelatoriosPorUsuario(List<ReportUsuariosDTO> relatoriosPorUsuario) {
        this.relatoriosPorUsuario = relatoriosPorUsuario;
    }

    public GraficoAcompanhamentoDiarioDTO getGraficoAcompanhamentoDiario() {
        return graficoAcompanhamentoDiario;
    }

    public void setGraficoAcompanhamentoDiario(GraficoAcompanhamentoDiarioDTO graficoAcompanhamentoDiario) {
        this.graficoAcompanhamentoDiario = graficoAcompanhamentoDiario;
    }
}


