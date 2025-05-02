package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoAlvara;
import br.com.webpublico.enums.TipoCalculoAlvara;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@GrupoDiagrama(nome = "Alvara")
@Entity
@Audited
public class ConfiguracaoAlvara extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfiguracaoTributario configuracaoTributario;
    @ManyToOne
    private Divida divida;
    @ManyToOne
    private Tributo tributo;
    @Enumerated(EnumType.STRING)
    private TipoAlvara tipoAlvara;
    @Enumerated(EnumType.STRING)
    private TipoCalculoAlvara tipoCalculoAlvara;
    @Enumerated(EnumType.STRING)
    private TipoCalculoAlvara tipoCalculoRenovacaoAlvara;
    @Enumerated(EnumType.STRING)
    private TipoCalculoAlvara tipoPrimeiroCalculo;
    private BigDecimal valorUFMFixoCalculoAlvara;
    private BigDecimal valorUFMFixoCalcRenovAlvara;
    private BigDecimal valorUFMFixoPrimeiroCalculo;
    private Integer diasVencDebito;

    public ConfiguracaoAlvara() {
        valorUFMFixoCalculoAlvara = BigDecimal.ZERO;
        valorUFMFixoCalcRenovAlvara = BigDecimal.ZERO;
        valorUFMFixoPrimeiroCalculo = BigDecimal.ZERO;
        diasVencDebito = 0;
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

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public TipoAlvara getTipoAlvara() {
        return tipoAlvara;
    }

    public void setTipoAlvara(TipoAlvara tipoAlvara) {
        this.tipoAlvara = tipoAlvara;
    }

    public TipoCalculoAlvara getTipoCalculoAlvara() {
        return tipoCalculoAlvara;
    }

    public void setTipoCalculoAlvara(TipoCalculoAlvara tipoCalculoAlvara) {
        this.tipoCalculoAlvara = tipoCalculoAlvara;
    }

    public TipoCalculoAlvara getTipoCalculoRenovacaoAlvara() {
        return tipoCalculoRenovacaoAlvara;
    }

    public void setTipoCalculoRenovacaoAlvara(TipoCalculoAlvara tipoCalculoRenovacaoAlvara) {
        this.tipoCalculoRenovacaoAlvara = tipoCalculoRenovacaoAlvara;
    }

    public TipoCalculoAlvara getTipoPrimeiroCalculo() {
        return tipoPrimeiroCalculo;
    }

    public void setTipoPrimeiroCalculo(TipoCalculoAlvara tipoPrimeiroCalculo) {
        this.tipoPrimeiroCalculo = tipoPrimeiroCalculo;
    }

    public BigDecimal getValorUFMFixoCalculoAlvara() {
        return valorUFMFixoCalculoAlvara;
    }

    public void setValorUFMFixoCalculoAlvara(BigDecimal valorUFMFixoCalculoAlvara) {
        this.valorUFMFixoCalculoAlvara = valorUFMFixoCalculoAlvara;
    }

    public BigDecimal getValorUFMFixoCalcRenovAlvara() {
        return valorUFMFixoCalcRenovAlvara;
    }

    public void setValorUFMFixoCalcRenovAlvara(BigDecimal valorUFMFixoCalcRenovAlvara) {
        this.valorUFMFixoCalcRenovAlvara = valorUFMFixoCalcRenovAlvara;
    }

    public BigDecimal getValorUFMFixoPrimeiroCalculo() {
        return valorUFMFixoPrimeiroCalculo;
    }

    public void setValorUFMFixoPrimeiroCalculo(BigDecimal valorUFMFixoPrimeiroCalculo) {
        this.valorUFMFixoPrimeiroCalculo = valorUFMFixoPrimeiroCalculo;
    }

    public Integer getDiasVencDebito() {
        return diasVencDebito;
    }

    public void setDiasVencDebito(Integer diasVencDebito) {
        this.diasVencDebito = diasVencDebito;
    }
}
