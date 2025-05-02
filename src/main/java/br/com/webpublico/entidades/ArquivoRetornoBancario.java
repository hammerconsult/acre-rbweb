/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fabio
 */
@Entity

public class ArquivoRetornoBancario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal valorTotalArquivo;
//    private BigDecimal valorTotalpago;
//    private int quantidadeDePgtoArquivo;
//    private int quantidadePgtoCompensado;
//    @ManyToOne
//    private Banco banco;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataPagamento;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Arquivo")
    private Arquivo arquivo;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    private SituacaoArquivo situacaoArquivo;

    public ArquivoRetornoBancario() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public SituacaoArquivo getSituacaoArquivo() {
        return situacaoArquivo;
    }

    public void setSituacaoArquivo(SituacaoArquivo situacaoArquivo) {
        this.situacaoArquivo = situacaoArquivo;
    }

    public BigDecimal getValorTotalArquivo() {
        return valorTotalArquivo;
    }

    public void setValorTotalArquivo(BigDecimal valorTotalArquivo) {
        this.valorTotalArquivo = valorTotalArquivo;
    }



    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ArquivoRetornoBancario)) {
            return false;
        }
        ArquivoRetornoBancario other = (ArquivoRetornoBancario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ArquivoLoteBaixa[ id=" + id + " ]";
    }
}
