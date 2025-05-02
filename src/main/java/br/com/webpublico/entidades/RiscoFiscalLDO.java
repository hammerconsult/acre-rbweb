/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoRiscoFiscalLDO;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Mapeia um risco fiscal da LDO com a respectiva providência que será tomada.
 *
 * @author arthur
 */
@Etiqueta("Risco Fiscal LDO")
@GrupoDiagrama(nome = "PPA")
@Audited
@Entity

public class RiscoFiscalLDO extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tipo de Risco")
    private TipoRiscoFiscalLDO tipoRiscoFiscalLDO;
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("LDO")
    private LDO ldo;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição do Risco")
    private String risco;
    @Tabelavel
    @Pesquisavel
    @Monetario
    @Obrigatorio
    @Etiqueta("Valor do Risco")
    private BigDecimal valorDoRisco;
    @Tabelavel
    @Obrigatorio
    @Etiqueta(value = "Providência")
    private String providencia;
    @Tabelavel
    @Pesquisavel
    @Monetario
    @Obrigatorio
    @Etiqueta("Valor da Providencia")
    private BigDecimal valorDaProvidencia;

    public RiscoFiscalLDO() {
        this.valorDaProvidencia = BigDecimal.ZERO;
        this.valorDoRisco= BigDecimal.ZERO;
    }

    public RiscoFiscalLDO(TipoRiscoFiscalLDO tipoRiscoFiscalLDO, LDO ldo, String risco, BigDecimal valorDoRisco, String providencia, BigDecimal valorDaProvidencia) {
        this.tipoRiscoFiscalLDO = tipoRiscoFiscalLDO;
        this.ldo = ldo;
        this.risco = risco;
        this.valorDoRisco = valorDoRisco;
        this.providencia = providencia;
        this.valorDaProvidencia = valorDaProvidencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LDO getLdo() {
        return ldo;
    }

    public void setLdo(LDO ldo) {
        this.ldo = ldo;
    }

    public String getProvidencia() {
        return providencia;
    }

    public void setProvidencia(String providencia) {
        this.providencia = providencia;
    }

    public String getRisco() {
        return risco;
    }

    public void setRisco(String risco) {
        this.risco = risco;
    }

    public TipoRiscoFiscalLDO getTipoRiscoFiscalLDO() {
        return tipoRiscoFiscalLDO;
    }

    public void setTipoRiscoFiscalLDO(TipoRiscoFiscalLDO tipoRiscoFiscalLDO) {
        this.tipoRiscoFiscalLDO = tipoRiscoFiscalLDO;
    }

    public BigDecimal getValorDaProvidencia() {
        return valorDaProvidencia;
    }

    public void setValorDaProvidencia(BigDecimal valorDaProvidencia) {
        this.valorDaProvidencia = valorDaProvidencia;
    }

    public BigDecimal getValorDoRisco() {
        return valorDoRisco;
    }

    public void setValorDoRisco(BigDecimal valorDoRisco) {
        this.valorDoRisco = valorDoRisco;
    }

    @Override
    public String toString() {
        return risco;
    }
}
