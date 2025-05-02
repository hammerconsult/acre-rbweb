package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TiposCredito;
import com.google.common.base.Strings;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

/**
 * Created by rodolfo on 01/09/17.
 */
public class LinhaRelatorioCreditoReceberTributario implements Comparable<LinhaRelatorioCreditoReceberTributario> {

    private String agrupadorPrincial = "CRÉDITO BRUTO (I)";
    private TiposCredito tiposCredito;
    private Boolean acrescrimo = false;
    private PrazoCreditoReceber prazo;
    private String descricaoTributo;
    private String entidade;
    private String codigoTributo;
    private String descricaoConta;
    private String codigoConta;
    private BigDecimal valor;
    private LocalDate emissao;


    public TiposCredito getTiposCredito() {
        return tiposCredito;
    }

    public void setTiposCredito(TiposCredito tiposCredito) {
        this.tiposCredito = tiposCredito;
    }

    public Boolean getAcrescrimo() {
        return acrescrimo;
    }

    public void setAcrescrimo(Boolean acrescrimo) {
        this.acrescrimo = acrescrimo;
    }

    public PrazoCreditoReceber getPrazo() {
        return prazo;
    }

    public void setPrazo(PrazoCreditoReceber prazo) {
        this.prazo = prazo;
    }

    public String getDescricaoTributo() {
        return descricaoTributo;
    }

    public void setDescricaoTributo(String descricaoTributo) {
        this.descricaoTributo = descricaoTributo;
    }

    public String getCodigoTributo() {
        return codigoTributo;
    }

    public void setCodigoTributo(String codigoTributo) {
        this.codigoTributo = codigoTributo;
    }

    public String getDescricaoConta() {
        return descricaoConta;
    }

    public void setDescricaoConta(String descricaoConta) {
        this.descricaoConta = descricaoConta;
    }

    public String getCodigoConta() {
        return codigoConta;
    }

    public void setCodigoConta(String codigoConta) {
        this.codigoConta = codigoConta;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void somaValor(BigDecimal valor) {
        this.valor = this.valor.add(valor);
    }

    public String getEntidade() {
        return entidade;
    }

    public void setEntidade(String entidade) {
        this.entidade = entidade;
    }

    public String getAgrupadorPrincial() {
        return agrupadorPrincial;
    }

    public void setAgrupadorPrincial(String agrupadorPrincial) {
        this.agrupadorPrincial = agrupadorPrincial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LinhaRelatorioCreditoReceberTributario that = (LinhaRelatorioCreditoReceberTributario) o;

        if (agrupadorPrincial != null ? !agrupadorPrincial.equals(that.agrupadorPrincial) : that.agrupadorPrincial != null)
            return false;
        if (tiposCredito != that.tiposCredito) return false;
        if (prazo != null ? !prazo.equals(that.prazo) : that.prazo != null) return false;
        if (descricaoTributo != null ? !descricaoTributo.equals(that.descricaoTributo) : that.descricaoTributo != null)
            return false;
        if (entidade != null ? !entidade.equals(that.entidade) : that.entidade != null) return false;
        if (codigoTributo != null ? !codigoTributo.equals(that.codigoTributo) : that.codigoTributo != null)
            return false;
        if (descricaoConta != null ? !descricaoConta.equals(that.descricaoConta) : that.descricaoConta != null)
            return false;
        return codigoConta != null ? codigoConta.equals(that.codigoConta) : that.codigoConta == null;
    }

    @Override
    public int hashCode() {
        int result = agrupadorPrincial != null ? agrupadorPrincial.hashCode() : 0;
        result = 31 * result + (tiposCredito != null ? tiposCredito.hashCode() : 0);
        result = 31 * result + (prazo != null ? prazo.hashCode() : 0);
        result = 31 * result + (descricaoTributo != null ? descricaoTributo.hashCode() : 0);
        result = 31 * result + (entidade != null ? entidade.hashCode() : 0);
        result = 31 * result + (codigoTributo != null ? codigoTributo.hashCode() : 0);
        result = 31 * result + (descricaoConta != null ? descricaoConta.hashCode() : 0);
        result = 31 * result + (codigoConta != null ? codigoConta.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(LinhaRelatorioCreditoReceberTributario linha) {
        int i = Strings.isNullOrEmpty(this.getEntidade()) || Strings.isNullOrEmpty(linha.getEntidade()) ? 0
            : this.getEntidade().compareTo(linha.getEntidade());
        if (i != 0) return i;
        i = this.getTiposCredito() == null || linha.getTiposCredito() == null ? 0 : this.getTiposCredito().compareTo(linha.getTiposCredito());
        if (i != 0) return i;
        i = Integer.compare(linha.getPrazo().getFim().getYear(), this.getPrazo().getFim().getYear());
        if (i != 0) return i;
        i = Days.daysBetween(this.getPrazo().getInicio(), this.getPrazo().getFim()).compareTo(Days.daysBetween(linha.getPrazo().getInicio(), linha.getPrazo().getFim()));
        if (i != 0) return i;
        return this.getDescricaoConta().compareTo(linha.getDescricaoConta());
    }


    public void setEmissao(LocalDate emissao) {
        this.emissao = emissao;
    }

    public LocalDate getEmissao() {
        return emissao;
    }
}
