package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Pagamento Avulso")
public class PagamentoAvulso implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParcelaValorDivida parcelaValorDivida;
    private BigDecimal valorPago;
    @Temporal(value = TemporalType.DATE)
    private Date dataPagamento;
    private Boolean ativo;
    @Enumerated(EnumType.STRING)
    private Tributo.TipoTributo tipoTributo;
    @ManyToOne
    private SubvencaoProcesso subvencao;

    public PagamentoAvulso() {
    }

    public PagamentoAvulso(ParcelaValorDivida parcelaValorDivida, BigDecimal valorPago, Date dataPagamento) {
        this.parcelaValorDivida = parcelaValorDivida;
        this.valorPago = valorPago;
        this.dataPagamento = dataPagamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Tributo.TipoTributo getTipoTributo() {
        return tipoTributo;
    }

    public void setTipoTributo(Tributo.TipoTributo tipoTributo) {
        this.tipoTributo = tipoTributo;
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        this.parcelaValorDivida = parcelaValorDivida;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PagamentoAvulso that = (PagamentoAvulso) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public SubvencaoProcesso getSubvencao() {
        return subvencao;
    }

    public void setSubvencao(SubvencaoProcesso subvencao) {
        this.subvencao = subvencao;
    }
}
