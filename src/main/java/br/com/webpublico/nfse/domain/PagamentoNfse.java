package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.enums.CondicaoPagamento;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A Pagamento.
 */
@Entity
@Audited
public class PagamentoNfse extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipopagamento")
    private CondicaoPagamento tipoPagamento;

    @Column(name = "quantidadeparcelas", precision = 10, scale = 2)
    private BigDecimal quantidadeParcelas;

    @Column(name = "valor", precision = 10, scale = 2)
    private BigDecimal valor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CondicaoPagamento getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(CondicaoPagamento tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public BigDecimal getQuantidadeParcelas() {
        return quantidadeParcelas;
    }

    public void setQuantidadeParcelas(BigDecimal quantidadeParcelas) {
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Pagamento{" +
            "id=" + id +
            ", tipoPagamento='" + tipoPagamento + "'" +
            ", quantidadeParcelas='" + quantidadeParcelas + "'" +
            ", valor='" + valor + "'" +
            '}';
    }
}
