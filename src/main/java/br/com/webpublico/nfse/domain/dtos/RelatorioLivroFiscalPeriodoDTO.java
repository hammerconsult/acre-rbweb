package br.com.webpublico.nfse.domain.dtos;

import java.util.Date;
import java.util.List;

public class RelatorioLivroFiscalPeriodoDTO implements Comparable<RelatorioLivroFiscalPeriodoDTO> {

    private Date periodoInicial;
    private Date periodoFinal;
    private Boolean retencao;
    private List<RelatorioLivroFiscalNotaDTO> notas;

    public RelatorioLivroFiscalPeriodoDTO() {
    }

    public List<RelatorioLivroFiscalNotaDTO> getNotas() {
        return notas;
    }

    public void setNotas(List<RelatorioLivroFiscalNotaDTO> notas) {
        this.notas = notas;
    }

    public Date getPeriodoInicial() {
        return periodoInicial;
    }

    public void setPeriodoInicial(Date periodoInicial) {
        this.periodoInicial = periodoInicial;
    }

    public Date getPeriodoFinal() {
        return periodoFinal;
    }

    public void setPeriodoFinal(Date periodoFinal) {
        this.periodoFinal = periodoFinal;
    }

    public Boolean getRetencao() {
        return retencao != null ? retencao : false;
    }

    public void setRetencao(Boolean retencao) {
        this.retencao = retencao;
    }

    @Override
    public int compareTo(RelatorioLivroFiscalPeriodoDTO o) {
        int i = this.periodoInicial.compareTo(o.getPeriodoInicial());
        if (i == 0) i = this.getRetencao().compareTo(o.getRetencao());
        return i;
    }
}
