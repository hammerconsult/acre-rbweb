/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author jaimaum
 */
@Entity

@Audited
public class CLPConfiguracaoParametro implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal valor;
    @ManyToOne
    private Conta contaCredito;
    @ManyToOne
    private Conta contaDebito;

    @ManyToOne
    private CLPRealizado cLPRealizado;
    private String complementoHistorico;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    private TagOCC cLPTag;
    @Transient
    private Long criadoEm;

    /**
     * Método de criação de parâmetros da CLP
     *
     * @param valor
     * @param contaCredito
     * @param contaDebito
     * @param tag
     * @param cLPRealizado
     * @param complementoHistorico
     */
    public CLPConfiguracaoParametro(UnidadeOrganizacional unidadeOrganizacional, BigDecimal valor, Conta contaCredito, Conta contaDebito, TagOCC cLPTag, CLPRealizado cLPRealizado, String complementoHistorico) {
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.valor = valor;
        this.contaCredito = contaCredito;
        this.contaDebito = contaDebito;
        this.cLPTag = cLPTag;
        this.cLPRealizado = cLPRealizado;
        this.complementoHistorico = complementoHistorico;
        this.criadoEm = System.nanoTime();
    }

    public CLPConfiguracaoParametro() {
        criadoEm = System.nanoTime();
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CLPRealizado getcLPRealizado() {
        return cLPRealizado;
    }

    public void setcLPRealizado(CLPRealizado cLPRealizado) {
        this.cLPRealizado = cLPRealizado;
    }


    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Conta getContaCredito() {
        return contaCredito;
    }

    public void setContaCredito(Conta contaCredito) {
        this.contaCredito = contaCredito;
    }

    public Conta getContaDebito() {
        return contaDebito;
    }

    public void setContaDebito(Conta contaDebito) {
        this.contaDebito = contaDebito;
    }

    public String getComplementoHistorico() {
        return complementoHistorico;
    }

    public void setComplementoHistorico(String complementoHistorico) {
        this.complementoHistorico = complementoHistorico;
    }

    public TagOCC getcLPTag() {
        return cLPTag;
    }

    public void setcLPTag(TagOCC cLPTag) {
        this.cLPTag = cLPTag;
    }


    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.CLPConfiguracaoParametro[ id=" + id + " ]";
    }
}
