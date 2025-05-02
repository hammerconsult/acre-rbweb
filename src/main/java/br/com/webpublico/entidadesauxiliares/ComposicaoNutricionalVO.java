package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

public class ComposicaoNutricionalVO {

    private BigDecimal energiaKCALRefeicao;
    private BigDecimal energiaKCALPrograma;

    private BigDecimal CHOgRefeicao;
    private BigDecimal CHOgPrograma;

    private BigDecimal PTNgRefeicao;
    private BigDecimal PTNgPrograma;

    private BigDecimal LPDgRefeicao;
    private BigDecimal LPDgPrograma;

    private BigDecimal FIBRASgRefeicao;
    private BigDecimal FIBRASgPrograma;

    private BigDecimal VITAmcgRefeicao;
    private BigDecimal VITAmcgPrograma;

    private BigDecimal VITCmcgRefeicao;
    private BigDecimal VITCmcgPrograma;

    private BigDecimal CAmgRefeicao;
    private BigDecimal CAmgPrograma;

    private BigDecimal FEmgRefeicao;
    private BigDecimal FEmgPrograma;

    private BigDecimal ZNmgRefeicao;
    private BigDecimal ZNmgPrograma;

    private BigDecimal NAmgRefeicao;
    private BigDecimal NAmgPrograma;

    public ComposicaoNutricionalVO() {
        inicializarValoresNutricionaisRefeicao();
        inicializarValoresNutricionaisPrograma();
    }

    public BigDecimal getEnergiaKCALRefeicao() {
        return energiaKCALRefeicao;
    }

    public void setEnergiaKCALRefeicao(BigDecimal energiaKCALRefeicao) {
        this.energiaKCALRefeicao = energiaKCALRefeicao;
    }

    public BigDecimal getEnergiaKCALPrograma() {
        return energiaKCALPrograma;
    }

    public void setEnergiaKCALPrograma(BigDecimal energiaKCALPrograma) {
        this.energiaKCALPrograma = energiaKCALPrograma;
    }

    public BigDecimal getCHOgRefeicao() {
        return CHOgRefeicao;
    }

    public void setCHOgRefeicao(BigDecimal CHOgRefeicao) {
        this.CHOgRefeicao = CHOgRefeicao;
    }

    public BigDecimal getCHOgPrograma() {
        return CHOgPrograma;
    }

    public void setCHOgPrograma(BigDecimal CHOgPrograma) {
        this.CHOgPrograma = CHOgPrograma;
    }

    public BigDecimal getPTNgRefeicao() {
        return PTNgRefeicao;
    }

    public void setPTNgRefeicao(BigDecimal PTNgRefeicao) {
        this.PTNgRefeicao = PTNgRefeicao;
    }

    public BigDecimal getPTNgPrograma() {
        return PTNgPrograma;
    }

    public void setPTNgPrograma(BigDecimal PTNgPrograma) {
        this.PTNgPrograma = PTNgPrograma;
    }

    public BigDecimal getLPDgRefeicao() {
        return LPDgRefeicao;
    }

    public void setLPDgRefeicao(BigDecimal LPDgRefeicao) {
        this.LPDgRefeicao = LPDgRefeicao;
    }

    public BigDecimal getLPDgPrograma() {
        return LPDgPrograma;
    }

    public void setLPDgPrograma(BigDecimal LPDgPrograma) {
        this.LPDgPrograma = LPDgPrograma;
    }

    public BigDecimal getFIBRASgRefeicao() {
        return FIBRASgRefeicao;
    }

    public void setFIBRASgRefeicao(BigDecimal FIBRASgRefeicao) {
        this.FIBRASgRefeicao = FIBRASgRefeicao;
    }

    public BigDecimal getFIBRASgPrograma() {
        return FIBRASgPrograma;
    }

    public void setFIBRASgPrograma(BigDecimal FIBRASgPrograma) {
        this.FIBRASgPrograma = FIBRASgPrograma;
    }

    public BigDecimal getVITAmcgRefeicao() {
        return VITAmcgRefeicao;
    }

    public void setVITAmcgRefeicao(BigDecimal VITAmcgRefeicao) {
        this.VITAmcgRefeicao = VITAmcgRefeicao;
    }

    public BigDecimal getVITAmcgPrograma() {
        return VITAmcgPrograma;
    }

    public void setVITAmcgPrograma(BigDecimal VITAmcgPrograma) {
        this.VITAmcgPrograma = VITAmcgPrograma;
    }

    public BigDecimal getVITCmcgRefeicao() {
        return VITCmcgRefeicao;
    }

    public void setVITCmcgRefeicao(BigDecimal VITCmcgRefeicao) {
        this.VITCmcgRefeicao = VITCmcgRefeicao;
    }

    public BigDecimal getVITCmcgPrograma() {
        return VITCmcgPrograma;
    }

    public void setVITCmcgPrograma(BigDecimal VITCmcgPrograma) {
        this.VITCmcgPrograma = VITCmcgPrograma;
    }

    public BigDecimal getCAmgRefeicao() {
        return CAmgRefeicao;
    }

    public void setCAmgRefeicao(BigDecimal CAmgRefeicao) {
        this.CAmgRefeicao = CAmgRefeicao;
    }

    public BigDecimal getCAmgPrograma() {
        return CAmgPrograma;
    }

    public void setCAmgPrograma(BigDecimal CAmgPrograma) {
        this.CAmgPrograma = CAmgPrograma;
    }

    public BigDecimal getFEmgRefeicao() {
        return FEmgRefeicao;
    }

    public void setFEmgRefeicao(BigDecimal FEmgRefeicao) {
        this.FEmgRefeicao = FEmgRefeicao;
    }

    public BigDecimal getFEmgPrograma() {
        return FEmgPrograma;
    }

    public void setFEmgPrograma(BigDecimal FEmgPrograma) {
        this.FEmgPrograma = FEmgPrograma;
    }

    public BigDecimal getZNmgRefeicao() {
        return ZNmgRefeicao;
    }

    public void setZNmgRefeicao(BigDecimal ZNmgRefeicao) {
        this.ZNmgRefeicao = ZNmgRefeicao;
    }

    public BigDecimal getZNmgPrograma() {
        return ZNmgPrograma;
    }

    public void setZNmgPrograma(BigDecimal ZNmgPrograma) {
        this.ZNmgPrograma = ZNmgPrograma;
    }

    public BigDecimal getNAmgRefeicao() {
        return NAmgRefeicao;
    }

    public void setNAmgRefeicao(BigDecimal NAmgRefeicao) {
        this.NAmgRefeicao = NAmgRefeicao;
    }

    public BigDecimal getNAmgPrograma() {
        return NAmgPrograma;
    }

    public void setNAmgPrograma(BigDecimal NAmgPrograma) {
        this.NAmgPrograma = NAmgPrograma;
    }

    public boolean isMetaAtingidaEnergiaKCAL() {
        return energiaKCALRefeicao.compareTo(energiaKCALPrograma) >= 0;
    }

    public boolean isMetaAtingidaCHO() {
        return CHOgRefeicao.compareTo(CHOgPrograma) >= 0;
    }

    public boolean isMetaAtingidaPTN() {
        return PTNgRefeicao.compareTo(PTNgPrograma) >= 0;
    }

    public boolean isMetaAtingidaLPD() {
        return LPDgRefeicao.compareTo(LPDgPrograma) >= 0;
    }

    public boolean isMetaAtingidaFibras() {
        return FIBRASgRefeicao.compareTo(FIBRASgPrograma) >= 0;
    }

    public boolean isMetaAtingidaVitA() {
        return VITAmcgRefeicao.compareTo(VITAmcgPrograma) >= 0;
    }

    public boolean isMetaAtingidaVitC() {
        return VITCmcgRefeicao.compareTo(VITCmcgPrograma) >= 0;
    }

    public boolean isMetaAtingidaCa() {
        return CAmgRefeicao.compareTo(CAmgPrograma) >= 0;
    }

    public boolean isMetaAtingidaFe() {
        return FEmgRefeicao.compareTo(FEmgPrograma) >= 0;
    }

    public boolean isMetaAtingidaZn() {
        return ZNmgRefeicao.compareTo(ZNmgPrograma) >= 0;
    }

    public boolean isMetaAtingidaNa() {
        return NAmgRefeicao.compareTo(NAmgPrograma) >= 0;
    }

    public void inicializarValoresNutricionaisRefeicao() {
        energiaKCALRefeicao = BigDecimal.ZERO;
        CHOgRefeicao = BigDecimal.ZERO;
        PTNgRefeicao = BigDecimal.ZERO;
        LPDgRefeicao = BigDecimal.ZERO;
        FIBRASgRefeicao = BigDecimal.ZERO;
        VITAmcgRefeicao = BigDecimal.ZERO;
        VITCmcgRefeicao = BigDecimal.ZERO;
        CAmgRefeicao = BigDecimal.ZERO;
        FEmgRefeicao = BigDecimal.ZERO;
        ZNmgRefeicao = BigDecimal.ZERO;
        NAmgRefeicao = BigDecimal.ZERO;
    }

    public void inicializarValoresNutricionaisPrograma() {
        energiaKCALPrograma = BigDecimal.ZERO;
        CHOgPrograma = BigDecimal.ZERO;
        PTNgPrograma = BigDecimal.ZERO;
        LPDgPrograma = BigDecimal.ZERO;
        FIBRASgPrograma = BigDecimal.ZERO;
        VITAmcgPrograma = BigDecimal.ZERO;
        VITCmcgPrograma = BigDecimal.ZERO;
        CAmgPrograma = BigDecimal.ZERO;
        FEmgPrograma = BigDecimal.ZERO;
        ZNmgPrograma = BigDecimal.ZERO;
        NAmgPrograma = BigDecimal.ZERO;
    }

}
