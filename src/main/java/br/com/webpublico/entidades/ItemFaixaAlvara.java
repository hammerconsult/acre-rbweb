/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.enums.TipoAlvara;
import br.com.webpublico.enums.TipoLocalizacao;
import br.com.webpublico.enums.TipoMateriaPrima;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Heinz
 */
@Audited
@Entity

@GrupoDiagrama(nome = "Alvara")
public class ItemFaixaAlvara extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoAlvara tipoAlvara;
    private BigDecimal areaMetroInicial;
    private BigDecimal areaMetroFinal;
    private BigDecimal valorTaxaUFMAno;
    private BigDecimal valorTaxaUFMMes;
    @Enumerated(EnumType.STRING)
    private GrauDeRiscoDTO grauDeRisco;
    @Enumerated(EnumType.STRING)
    private TipoLocalizacao tipoLocalizacao;
    @Enumerated(EnumType.STRING)
    private TipoMateriaPrima tipoMateriaPrima;
    @ManyToOne
    private FaixaAlvara faixaAlvara;

    public ItemFaixaAlvara() {
        this.areaMetroInicial = BigDecimal.ZERO;
        this.areaMetroFinal = BigDecimal.ZERO;

    }

    public FaixaAlvara getFaixaAlvara() {
        return faixaAlvara;
    }

    public void setFaixaAlvara(FaixaAlvara faixaAlvara) {
        this.faixaAlvara = faixaAlvara;
    }

    public BigDecimal getAreaMetroFinal() {
        return areaMetroFinal;
    }

    public void setAreaMetroFinal(BigDecimal areaMetroFinal) {
        this.areaMetroFinal = areaMetroFinal;
    }

    public BigDecimal getAreaMetroInicial() {
        return areaMetroInicial;
    }

    public void setAreaMetroInicial(BigDecimal areaMetroInicial) {
        this.areaMetroInicial = areaMetroInicial;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public GrauDeRiscoDTO getGrauDeRisco() {
        return grauDeRisco;
    }

    public void setGrauDeRisco(GrauDeRiscoDTO grauDeRisco) {
        this.grauDeRisco = grauDeRisco;
    }

    public TipoAlvara getTipoAlvara() {
        return tipoAlvara;
    }

    public void setTipoAlvara(TipoAlvara tipoAlvara) {
        this.tipoAlvara = tipoAlvara;
    }

    public BigDecimal getValorTaxaUFMAno() {
        return valorTaxaUFMAno;
    }

    public void setValorTaxaUFMAno(BigDecimal valorTaxaUFM) {
        this.valorTaxaUFMAno = valorTaxaUFM;
    }

    public BigDecimal getValorTaxaUFMMes() {
        return valorTaxaUFMMes;
    }

    public void setValorTaxaUFMMes(BigDecimal valorTaxaUFMMes) {
        this.valorTaxaUFMMes = valorTaxaUFMMes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.enums.TipoAlvara[ id=" + id + " ]";
    }

    public TipoLocalizacao getTipoLocalizacao() {
        return tipoLocalizacao;
    }

    public void setTipoLocalizacao(TipoLocalizacao tipoLocalizacao) {
        this.tipoLocalizacao = tipoLocalizacao;
    }

    public TipoMateriaPrima getTipoMateriaPrima() {
        return tipoMateriaPrima;
    }

    public void setTipoMateriaPrima(TipoMateriaPrima tipoMateriaPrima) {
        this.tipoMateriaPrima = tipoMateriaPrima;
    }
}
