package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by William on 16/03/2017.
 */
@Entity
@Audited
public class PagamentoCartaoItem extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PagamentoCartao pagamentoCartao;
    private Integer parcela;
    @Temporal(TemporalType.DATE)
    private Date vencimento;
    private BigDecimal valor;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PagamentoCartao getPagamentoCartao() {
        return pagamentoCartao;
    }

    public void setPagamentoCartao(PagamentoCartao pagamentoCartao) {
        this.pagamentoCartao = pagamentoCartao;
    }

    public Integer getParcela() {
        return parcela;
    }

    public void setParcela(Integer parcela) {
        this.parcela = parcela;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

}
