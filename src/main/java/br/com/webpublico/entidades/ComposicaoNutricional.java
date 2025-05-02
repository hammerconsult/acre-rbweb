package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Audited
@Etiqueta("Composição Nutricional")
public class ComposicaoNutricional extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Etiqueta("Energia (KCAL)")
    private BigDecimal energiaKCAL;

    @Obrigatorio
    @Etiqueta("CHO(g)")
    private BigDecimal CHOg;

    @Obrigatorio
    @Etiqueta("PTN(g)")
    private BigDecimal PTNg;

    @Obrigatorio
    @Etiqueta("LPD(g)")
    private BigDecimal LPDg;

    @Obrigatorio
    @Etiqueta("FIBRAS(g)")
    private BigDecimal FIBRASg;

    @Obrigatorio
    @Etiqueta("VIT. A(mcg)")
    private BigDecimal VITAmcg;

    @Obrigatorio
    @Etiqueta("VIT. C(mcg)")
    private BigDecimal VITCmcg;

    @Obrigatorio
    @Etiqueta("Ca(mg)")
    private BigDecimal CAmg;

    @Obrigatorio
    @Etiqueta("Fe(mg)")
    private BigDecimal FEmg;

    @Obrigatorio
    @Etiqueta("Zn(mg)")
    private BigDecimal ZNmg;

    @Obrigatorio
    @Etiqueta("Na(mg)")
    private BigDecimal NAmg;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getEnergiaKCAL() {
        return energiaKCAL;
    }

    public void setEnergiaKCAL(BigDecimal energiaKCAL) {
        this.energiaKCAL = energiaKCAL;
    }

    public BigDecimal getCHOg() {
        return CHOg;
    }

    public void setCHOg(BigDecimal CHOg) {
        this.CHOg = CHOg;
    }

    public BigDecimal getPTNg() {
        return PTNg;
    }

    public void setPTNg(BigDecimal PTNg) {
        this.PTNg = PTNg;
    }

    public BigDecimal getLPDg() {
        return LPDg;
    }

    public void setLPDg(BigDecimal LPDg) {
        this.LPDg = LPDg;
    }

    public BigDecimal getFIBRASg() {
        return FIBRASg;
    }

    public void setFIBRASg(BigDecimal FIBRASg) {
        this.FIBRASg = FIBRASg;
    }

    public BigDecimal getVITAmcg() {
        return VITAmcg;
    }

    public void setVITAmcg(BigDecimal VITAmcg) {
        this.VITAmcg = VITAmcg;
    }

    public BigDecimal getVITCmcg() {
        return VITCmcg;
    }

    public void setVITCmcg(BigDecimal VITCmcg) {
        this.VITCmcg = VITCmcg;
    }

    public BigDecimal getCAmg() {
        return CAmg;
    }

    public void setCAmg(BigDecimal CAmg) {
        this.CAmg = CAmg;
    }

    public BigDecimal getFEmg() {
        return FEmg;
    }

    public void setFEmg(BigDecimal FEmg) {
        this.FEmg = FEmg;
    }

    public BigDecimal getZNmg() {
        return ZNmg;
    }

    public void setZNmg(BigDecimal ZNmg) {
        this.ZNmg = ZNmg;
    }

    public BigDecimal getNAmg() {
        return NAmg;
    }

    public void setNAmg(BigDecimal NAmg) {
        this.NAmg = NAmg;
    }
}
