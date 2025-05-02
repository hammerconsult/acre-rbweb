package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.LoteBaixa;
import br.com.webpublico.entidades.Tributo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class RelatorioReceitaDiariaArrecadacao implements Comparable<RelatorioReceitaDiariaArrecadacao> {

    private Date dataFinanceira;
    private Tributo tributo;
    private LoteBaixa lote;
    private BigDecimal valor;

    public RelatorioReceitaDiariaArrecadacao(Date dataFinanceira, Tributo tributo, LoteBaixa lote) {
        this.dataFinanceira = dataFinanceira;
        this.tributo = tributo;
        this.lote = lote;
        this.valor = BigDecimal.ZERO;
    }

    public RelatorioReceitaDiariaArrecadacao(Date dataFinanceira, Tributo tributo) {
        this.dataFinanceira = dataFinanceira;
        this.tributo = tributo;
        this.valor = BigDecimal.ZERO;
    }

    public Date getDataFinanceira() {
        return dataFinanceira;
    }

    public void setDataFinanceira(Date dataFinanceira) {
        this.dataFinanceira = dataFinanceira;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public LoteBaixa getLote() {
        return lote;
    }

    public void setLote(LoteBaixa lote) {
        this.lote = lote;
    }

    public BigDecimal getValor() {
        if (valor == null) {
            valor = BigDecimal.ZERO;
        }
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public int compareTo(RelatorioReceitaDiariaArrecadacao o) {
        if (dataFinanceira.compareTo(o.getDataFinanceira()) == 0) {
            return tributo.getDescricao().compareTo(o.getTributo().getDescricao());
        }
        return dataFinanceira.compareTo(o.getDataFinanceira());
    }

    public void somar(BigDecimal valor) {
        setValor(getValor().add(valor));
    }

    public void subtrair(BigDecimal valor) {
        setValor(getValor().subtract(valor));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelatorioReceitaDiariaArrecadacao that = (RelatorioReceitaDiariaArrecadacao) o;

        if (dataFinanceira != null ? !dataFinanceira.equals(that.dataFinanceira) : that.dataFinanceira != null)
            return false;
        if (tributo != null ? !tributo.equals(that.tributo) : that.tributo != null) return false;
        return !(lote != null ? !lote.equals(that.lote) : that.lote != null);

    }

    @Override
    public int hashCode() {
        int result = dataFinanceira != null ? dataFinanceira.hashCode() : 0;
        result = 31 * result + (tributo != null ? tributo.hashCode() : 0);
        result = 31 * result + (lote != null ? lote.hashCode() : 0);
        return result;
    }
}
