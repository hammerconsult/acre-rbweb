/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Alex
 * @since 30/03/2017 14:30
 */
@Etiqueta("Multas")
@Entity
@Audited
@Table(name = "ITEMLANCMULTAACESSORIA")
public class ItemLancamentoMultaAcessoria extends SuperEntidade implements Serializable, Comparable<ItemLancamentoMultaAcessoria> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    private MultaFiscalizacao multaFiscalizacao;
    @Length(maximo = 255)
    private String descricao;
    @Length(maximo = 255)
    private String observacao;
    @ManyToOne
    private LancamentoMultaAcessoria lancamentoMultaAcessoria;
    private BigDecimal valorMulta;
    private Integer quantidadeUFMRB;

    public ItemLancamentoMultaAcessoria() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MultaFiscalizacao getMultaFiscalizacao() {
        return multaFiscalizacao;
    }

    public void setMultaFiscalizacao(MultaFiscalizacao multaFiscalizacao) {
        this.multaFiscalizacao = multaFiscalizacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public LancamentoMultaAcessoria getLancamentoMultaAcessoria() {
        return lancamentoMultaAcessoria;
    }

    public void setLancamentoMultaAcessoria(LancamentoMultaAcessoria lancamentoMultaAcessoria) {
        this.lancamentoMultaAcessoria = lancamentoMultaAcessoria;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public Integer getQuantidadeUFMRB() {
        return quantidadeUFMRB;
    }

    public void setQuantidadeUFMRB(Integer quantidadeUFMRB) {
        this.quantidadeUFMRB = quantidadeUFMRB;
    }

    @Override
    public int compareTo(ItemLancamentoMultaAcessoria o) {
        try {
            int i =  this.getMultaFiscalizacao().getCodigo().compareTo(o.getMultaFiscalizacao().getCodigo());
            if (i == 0) {
                i =  this.getMultaFiscalizacao().getArtigo().compareTo(o.getMultaFiscalizacao().getArtigo());
            }
            return i;

        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }
}
