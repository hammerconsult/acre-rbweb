package br.com.webpublico.entidades;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class PIX extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long validadeEmSegundos;

    private String qrCode;
    private String txid;
    private String endToEndId;
    private String infoPagador;

    private BigDecimal valor;
    private BigDecimal valorPago;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataSolicitacao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataPagamento;

    @OneToMany(mappedBy = "pix", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StatusPIX> statusSolicitacao;

    public PIX() {
        this.valor = BigDecimal.ZERO;
        this.valorPago = BigDecimal.ZERO;
        this.statusSolicitacao = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getValidadeEmSegundos() {
        return validadeEmSegundos;
    }

    public void setValidadeEmSegundos(Long validadeEmSegundos) {
        this.validadeEmSegundos = validadeEmSegundos;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getEndToEndId() {
        return endToEndId;
    }

    public void setEndToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
    }

    public String getInfoPagador() {
        return infoPagador;
    }

    public void setInfoPagador(String infoPagador) {
        this.infoPagador = infoPagador;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public List<StatusPIX> getStatusSolicitacao() {
        return statusSolicitacao;
    }

    public void setStatusSolicitacao(List<StatusPIX> statusSolicitacao) {
        this.statusSolicitacao = statusSolicitacao;
    }

    public StatusPIX getStatusAtual() {
        return Collections.max(statusSolicitacao, comparadorDataStatus());
    }

    private Comparator<StatusPIX> comparadorDataStatus() {
        return new Comparator<StatusPIX>() {
            @Override
            public int compare(StatusPIX o1, StatusPIX o2) {
                return ComparisonChain.start().compare(o1.getDataSituacao(), o2.getDataSituacao()).result();
            }
        };
    }
}
