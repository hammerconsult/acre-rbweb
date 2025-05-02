/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.nfse.domain.dtos.NFSAvulsaItemNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Gustavo
 */
@Entity
@Audited
public class NFSAvulsaItem implements Serializable, NfseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer quantidade;
    private String unidade;
    private String descricao;
    private BigDecimal valorUnitario;
    private BigDecimal aliquotaIss;
    private BigDecimal valorIss;
    @Transient
    private Long criadoEm;
    @ManyToOne
    private NFSAvulsa NFSAvulsa;
    @ManyToOne
    private Servico servico;

    private String placa;

    public NFSAvulsaItem() {
        aliquotaIss = BigDecimal.ZERO;
        valorIss = BigDecimal.ZERO;
        valorUnitario = BigDecimal.ZERO;
        quantidade = 1;
        criadoEm = System.nanoTime();
    }

    public br.com.webpublico.entidades.NFSAvulsa getNFSAvulsa() {
        return NFSAvulsa;
    }

    public void setNFSAvulsa(br.com.webpublico.entidades.NFSAvulsa NFSAvulsa) {
        this.NFSAvulsa = NFSAvulsa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAliquotaIss() {
        return aliquotaIss;
    }

    public void setAliquotaIss(BigDecimal aliquotaIss) {
        this.aliquotaIss = aliquotaIss;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getValorIss() {
        return valorIss;
    }

    public BigDecimal getValorTotal() {
        return valorUnitario.multiply(new BigDecimal(quantidade));
    }

    public void setValorIss(BigDecimal valorIss) {
        this.valorIss = valorIss;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
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
        return quantidade + " " + descricao + ", no valor de: " + getValorTotal();
    }

    @Override
    public NFSAvulsaItemNfseDTO toNfseDto() {
        NFSAvulsaItemNfseDTO nfsAvulsaItemNfseDTO = new NFSAvulsaItemNfseDTO();
        nfsAvulsaItemNfseDTO.setServico(servico.toNfseDto());
        nfsAvulsaItemNfseDTO.setAliquotaIss(aliquotaIss);
        nfsAvulsaItemNfseDTO.setDescricao(descricao);
        nfsAvulsaItemNfseDTO.setQuantidade(quantidade);
        nfsAvulsaItemNfseDTO.setValorUnitario(valorUnitario);
        nfsAvulsaItemNfseDTO.setValorIss(valorIss);
        nfsAvulsaItemNfseDTO.setValorTotal(valorUnitario.multiply(new BigDecimal(quantidade)));
        return nfsAvulsaItemNfseDTO;
    }
}
