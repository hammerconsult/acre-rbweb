package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoLicencaAmbiental;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@GrupoDiagrama(nome = "Alvara")
@Table(name = "PARAMETROVALORALVARAAMB")
@Audited
@Entity
public class ParametroValorAlvaraAmbiental extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private ConfiguracaoTributario configuracaoTributario;

    @Enumerated(EnumType.STRING)
    private TipoLicencaAmbiental tipoLicencaAmbiental;

    private BigDecimal valorUFMClasseI;
    private BigDecimal valorUFMClasseII;
    private BigDecimal valorUFMClasseIII;
    private BigDecimal valorUFMSemClasse;

    public ParametroValorAlvaraAmbiental() {
        valorUFMClasseI = BigDecimal.ZERO;
        valorUFMClasseII = BigDecimal.ZERO;
        valorUFMClasseIII = BigDecimal.ZERO;
        valorUFMSemClasse = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoTributario getConfiguracaoTributario() {
        return configuracaoTributario;
    }

    public void setConfiguracaoTributario(ConfiguracaoTributario configuracaoTributario) {
        this.configuracaoTributario = configuracaoTributario;
    }

    public TipoLicencaAmbiental getTipoLicencaAmbiental() {
        return tipoLicencaAmbiental;
    }

    public void setTipoLicencaAmbiental(TipoLicencaAmbiental tipoLicencaAmbiental) {
        this.tipoLicencaAmbiental = tipoLicencaAmbiental;
    }

    public BigDecimal getValorUFMClasseI() {
        return valorUFMClasseI;
    }

    public void setValorUFMClasseI(BigDecimal valorUFMClasseI) {
        this.valorUFMClasseI = valorUFMClasseI;
    }

    public BigDecimal getValorUFMClasseII() {
        return valorUFMClasseII;
    }

    public void setValorUFMClasseII(BigDecimal valorUFMClasseII) {
        this.valorUFMClasseII = valorUFMClasseII;
    }

    public BigDecimal getValorUFMClasseIII() {
        return valorUFMClasseIII;
    }

    public void setValorUFMClasseIII(BigDecimal valorUFMClasseIII) {
        this.valorUFMClasseIII = valorUFMClasseIII;
    }

    public BigDecimal getValorUFMSemClasse() {
        return valorUFMSemClasse;
    }

    public void setValorUFMSemClasse(BigDecimal valorUFMSemClasse) {
        this.valorUFMSemClasse = valorUFMSemClasse;
    }
}
