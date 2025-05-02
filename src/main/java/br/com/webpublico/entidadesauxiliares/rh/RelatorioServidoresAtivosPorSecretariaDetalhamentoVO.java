package br.com.webpublico.entidadesauxiliares.rh;

import java.math.BigDecimal;

/**
 * @author octavio
 */
public class RelatorioServidoresAtivosPorSecretariaDetalhamentoVO {

    private String descricaoMes;
    private Integer anoFolha;
    private Integer totalServidores;
    private BigDecimal totalBrutoMes;
    private BigDecimal totalLiquidoMes;
    private BigDecimal totalLiquidoAno;
    private BigDecimal totalBrutoAno;

    public RelatorioServidoresAtivosPorSecretariaDetalhamentoVO() {
        totalBrutoMes = BigDecimal.ZERO;
        totalLiquidoMes = BigDecimal.ZERO;
        totalLiquidoAno = BigDecimal.ZERO;
        totalBrutoAno = BigDecimal.ZERO;
    }

    public String getDescricaoMes() {
        return descricaoMes;
    }

    public void setDescricaoMes(String descricaoMes) {
        this.descricaoMes = descricaoMes;
    }

    public Integer getAnoFolha() {
        return anoFolha;
    }

    public void setAnoFolha(Integer anoFolha) {
        this.anoFolha = anoFolha;
    }

    public Integer getTotalServidores() {
        return totalServidores;
    }

    public void setTotalServidores(Integer totalServidores) {
        this.totalServidores = totalServidores;
    }

    public BigDecimal getTotalBrutoMes() {
        return totalBrutoMes;
    }

    public void setTotalBrutoMes(BigDecimal totalBrutoMes) {
        this.totalBrutoMes = totalBrutoMes;
    }

    public BigDecimal getTotalLiquidoMes() {
        return totalLiquidoMes;
    }

    public void setTotalLiquidoMes(BigDecimal totalLiquidoMes) {
        this.totalLiquidoMes = totalLiquidoMes;
    }

    public BigDecimal getTotalLiquidoAno() {
        return totalLiquidoAno;
    }

    public void setTotalLiquidoAno(BigDecimal totalLiquidoAno) {
        this.totalLiquidoAno = totalLiquidoAno;
    }

    public BigDecimal getTotalBrutoAno() {
        return totalBrutoAno;
    }

    public void setTotalBrutoAno(BigDecimal totalBrutoAno) {
        this.totalBrutoAno = totalBrutoAno;
    }
}
