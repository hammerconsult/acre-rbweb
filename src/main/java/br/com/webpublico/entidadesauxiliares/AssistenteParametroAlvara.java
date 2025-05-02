package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.ParametroValorAlvaraAmbiental;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.enums.TipoAlvara;
import br.com.webpublico.enums.TipoCalculoAlvara;
import br.com.webpublico.enums.TipoLicencaAmbiental;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.math.BigDecimal;

public class AssistenteParametroAlvara implements Serializable {

    private Divida dividaSelecionada;
    private Tributo tributoSelecionado;
    private TipoAlvara tipoAlvara;
    private TipoLicencaAmbiental licencaAmbiental;
    private TipoCalculoAlvara tipoCalculoAlvaraSelecionado;
    private TipoCalculoAlvara tipoCalculoRenovAlvaraSelecionado;
    private TipoCalculoAlvara tipoPrimeiroCalculo;
    private BigDecimal valorUFMClasseI;
    private BigDecimal valorUFMClasseII;
    private BigDecimal valorUFMClasseIII;
    private BigDecimal valorUFMSemClasse;
    private BigDecimal valorUFMFixoCalculoSelecionado;
    private BigDecimal valorUFMFixoCalcRenovacaoSelecionado;
    private BigDecimal valorUFMFixoPrimeiroCalculo;
    private ParametroValorAlvaraAmbiental parametroAlvara;
    private Integer diasVencDebito;
    private Long criadoEm;

    public AssistenteParametroAlvara() {
        valorUFMClasseI = BigDecimal.ZERO;
        valorUFMClasseII = BigDecimal.ZERO;
        valorUFMClasseIII = BigDecimal.ZERO;
        valorUFMSemClasse = BigDecimal.ZERO;
        valorUFMFixoCalculoSelecionado = BigDecimal.ZERO;
        valorUFMFixoCalcRenovacaoSelecionado = BigDecimal.ZERO;
        valorUFMFixoPrimeiroCalculo = BigDecimal.ZERO;
        diasVencDebito = 0;
        parametroAlvara = new ParametroValorAlvaraAmbiental();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Divida getDividaSelecionada() {
        return dividaSelecionada;
    }

    public void setDividaSelecionada(Divida dividaSelecionada) {
        this.dividaSelecionada = dividaSelecionada;
    }

    public Tributo getTributoSelecionado() {
        return tributoSelecionado;
    }

    public void setTributoSelecionado(Tributo tributoSelecionado) {
        this.tributoSelecionado = tributoSelecionado;
    }

    public TipoAlvara getTipoAlvara() {
        return tipoAlvara;
    }

    public void setTipoAlvara(TipoAlvara tipoAlvara) {
        this.tipoAlvara = tipoAlvara;
    }

    public TipoLicencaAmbiental getLicencaAmbiental() {
        return licencaAmbiental;
    }

    public void setLicencaAmbiental(TipoLicencaAmbiental licencaAmbiental) {
        this.licencaAmbiental = licencaAmbiental;
    }

    public TipoCalculoAlvara getTipoCalculoAlvaraSelecionado() {
        return tipoCalculoAlvaraSelecionado;
    }

    public void setTipoCalculoAlvaraSelecionado(TipoCalculoAlvara tipoCalculoAlvaraSelecionado) {
        this.tipoCalculoAlvaraSelecionado = tipoCalculoAlvaraSelecionado;
    }

    public TipoCalculoAlvara getTipoCalculoRenovAlvaraSelecionado() {
        return tipoCalculoRenovAlvaraSelecionado;
    }

    public void setTipoCalculoRenovAlvaraSelecionado(TipoCalculoAlvara tipoCalculoRenovAlvaraSelecionado) {
        this.tipoCalculoRenovAlvaraSelecionado = tipoCalculoRenovAlvaraSelecionado;
    }

    public TipoCalculoAlvara getTipoPrimeiroCalculo() {
        return tipoPrimeiroCalculo;
    }

    public void setTipoPrimeiroCalculo(TipoCalculoAlvara tipoPrimeiroCalculo) {
        this.tipoPrimeiroCalculo = tipoPrimeiroCalculo;
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

    public ParametroValorAlvaraAmbiental getParametroAlvara() {
        return parametroAlvara;
    }

    public void setParametroAlvara(ParametroValorAlvaraAmbiental parametroAlvara) {
        this.parametroAlvara = parametroAlvara;
    }

    public BigDecimal getValorUFMFixoCalculoSelecionado() {
        return valorUFMFixoCalculoSelecionado;
    }

    public void setValorUFMFixoCalculoSelecionado(BigDecimal valorUFMFixoCalculoSelecionado) {
        this.valorUFMFixoCalculoSelecionado = valorUFMFixoCalculoSelecionado;
    }

    public BigDecimal getValorUFMFixoCalcRenovacaoSelecionado() {
        return valorUFMFixoCalcRenovacaoSelecionado;
    }

    public void setValorUFMFixoCalcRenovacaoSelecionado(BigDecimal valorUFMFixoCalcRenovacaoSelecionado) {
        this.valorUFMFixoCalcRenovacaoSelecionado = valorUFMFixoCalcRenovacaoSelecionado;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssistenteParametroAlvara that = (AssistenteParametroAlvara) o;
        if(criadoEm != null) {
            return Objects.equal(criadoEm, that.criadoEm);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(criadoEm);
    }
}
