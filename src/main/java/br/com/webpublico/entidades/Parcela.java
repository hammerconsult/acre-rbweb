/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Terminal-2
 */
@GrupoDiagrama(nome = "Tributario")
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
public class Parcela implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private OpcaoPagamento opcaoPagamento;
    @Invisivel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    private String mensagem;
    private BigDecimal percentualValorTotal;
    private String sequenciaParcela;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vencimento;
    @Transient
    private Long criadoEm;

    public Parcela() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OpcaoPagamento getOpcaoPagamento() {
        return opcaoPagamento;
    }

    public void setOpcaoPagamento(OpcaoPagamento opcaoPagamento) {
        this.opcaoPagamento = opcaoPagamento;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public BigDecimal getPercentualValorTotal() {
        return percentualValorTotal;
    }

    public void setPercentualValorTotal(BigDecimal percentualValorTotal) {
        this.percentualValorTotal = percentualValorTotal;
    }

    public String getSequenciaParcela() {
        return sequenciaParcela;
    }

    public void setSequenciaParcela(String numeroParcelas) {
        this.sequenciaParcela = numeroParcelas;
    }

    public String getLabelVencimento() {
        return "Não encontrado";
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public String toString() {
        return getLabelVencimento();
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }
}
