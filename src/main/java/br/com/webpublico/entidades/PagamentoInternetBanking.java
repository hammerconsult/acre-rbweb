package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class PagamentoInternetBanking extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private DAM dam;
    @Temporal(TemporalType.DATE)
    private Date dataPagamento;
    @Temporal(TemporalType.DATE)
    private Date dataCredito;
    private String codigoBanco;
    private String codigoAgencia;
    private String codigoConta;
    private String autenticacao;
    private BigDecimal valorPago;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DAM getDam() {
        return dam;
    }

    public void setDam(DAM dam) {
        this.dam = dam;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Date getDataCredito() {
        return dataCredito;
    }

    public void setDataCredito(Date dataCredito) {
        this.dataCredito = dataCredito;
    }

    public String getCodigoBanco() {
        return codigoBanco;
    }

    public void setCodigoBanco(String codigoBanco) {
        this.codigoBanco = codigoBanco;
    }

    public String getCodigoAgencia() {
        return codigoAgencia;
    }

    public void setCodigoAgencia(String codigoAgencia) {
        this.codigoAgencia = codigoAgencia;
    }

    public String getCodigoConta() {
        return codigoConta;
    }

    public void setCodigoConta(String codigoConta) {
        this.codigoConta = codigoConta;
    }

    public String getAutenticacao() {
        return autenticacao;
    }

    public void setAutenticacao(String autenticacao) {
        this.autenticacao = autenticacao;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }
}
