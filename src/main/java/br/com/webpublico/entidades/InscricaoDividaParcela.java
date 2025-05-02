/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Renato
 */
@Entity

@Audited
@Etiqueta("Inscrição Dívida Ativa")
@GrupoDiagrama(nome = "Divida Ativa")
public class InscricaoDividaParcela implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ItemInscricaoDividaAtiva itemInscricaoDividaAtiva;
    @ManyToOne
    private ParcelaValorDivida parcelaValorDivida;
    @Transient
    private Long criadoEm;
    @Transient
    private Long idParcela;
    @Transient
    private BigDecimal saldoParcela;
    @Transient
    private Calculo.TipoCalculo tipoCalculo;
    @Transient
    private String referenciaParcela;

    public InscricaoDividaParcela() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ItemInscricaoDividaAtiva getItemInscricaoDividaAtiva() {
        return itemInscricaoDividaAtiva;
    }

    public void setItemInscricaoDividaAtiva(ItemInscricaoDividaAtiva itemInscricaoDividaAtiva) {
        this.itemInscricaoDividaAtiva = itemInscricaoDividaAtiva;
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        this.parcelaValorDivida = parcelaValorDivida;
    }

    public Long getIdParcela() {
        return idParcela;
    }

    public void setIdParcela(Long idParcela) {
        this.idParcela = idParcela;
    }

    public BigDecimal getSaldoParcela() {
        return saldoParcela;
    }

    public void setSaldoParcela(BigDecimal saldoParcela) {
        this.saldoParcela = saldoParcela;
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
        return "br.com.webpublico.entidades.InscricaoParcela[ id=" + id + " ]";
    }

    public void setTipoCalculo(Calculo.TipoCalculo tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }

    public Calculo.TipoCalculo getTipoCalculo() {
        return tipoCalculo;
    }

    public String getReferenciaParcela() {
        return referenciaParcela;
    }

    public void setReferenciaParcela(String referenciaParcela) {
        this.referenciaParcela = referenciaParcela;
    }
}
