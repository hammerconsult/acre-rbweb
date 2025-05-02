package br.com.webpublico.entidadesauxiliares;

import net.sf.jasperreports.engine.export.JRExportProgressMonitor;

public class ExportPDFMonitor implements JRExportProgressMonitor {

    private Estado estado;
    private Integer totalRegistros;
    private Integer registrosGerados;
    private Integer totalPaginas;
    private Integer paginasGeradas;


    public ExportPDFMonitor() {
        totalPaginas = 0;
        paginasGeradas = 0;
        totalRegistros = 0;
        registrosGerados = 0;
        estado = Estado.CONSULTANDO_REGISTROS;
    }

    public void gerouLinha() {
        registrosGerados++;
    }

    @Override
    public void afterPageExport() {
        paginasGeradas++;
    }

    public Integer getTotalPaginas() {
        return totalPaginas;
    }

    public void setTotalPaginas(Integer totalPaginas) {
        this.totalPaginas = totalPaginas;
    }

    public Integer getPaginasGeradas() {
        return paginasGeradas;
    }

    public void setPaginasGeradas(Integer paginasGeradas) {
        this.paginasGeradas = paginasGeradas;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Integer getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Integer totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public Integer getRegistrosGerados() {
        return registrosGerados;
    }

    public void setRegistrosGerados(Integer registrosGerados) {
        this.registrosGerados = registrosGerados;
    }

    public enum Estado {
        CONSULTANDO_REGISTROS,
        GERANDO_REGISTROS,
        GERANDO_PDF
    }
}
