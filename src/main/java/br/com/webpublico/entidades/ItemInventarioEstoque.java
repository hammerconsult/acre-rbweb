/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@GrupoDiagrama(nome = "Materiais")
@Audited
@Entity

@Etiqueta("Itens de Inventário-Estoque")
/**
 * Representa as quantidades esperada (estado do estoque no início do inventário)
 * e encontrada (constatada durante a realização do inventário). Eventuais diferenças
 * deverão gerar ajustes.
 *
 * @author arthur
 */
public class ItemInventarioEstoque extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Inventário de Estoque")
    private InventarioEstoque inventarioEstoque;

    @Tabelavel
    @ManyToOne
    @Etiqueta("Material")
    private Material material;

    @Etiqueta("Quantidade Esperada")
    private BigDecimal quantidadeEsperada;

    @Etiqueta("Quantidade Constatada")
    private BigDecimal quantidadeConstatada;

    @Etiqueta("Quantidade Ajustada")
    @Transient
    private BigDecimal quantidadeAjustada;

    @Etiqueta("Valor Financeiro")
    private BigDecimal financeiro;
    /*
     * Quando a quantidade esperada (do saldo do local de estoque)
    for diferente da encontrada.
     */
    @Etiqueta("Houve Divergência")
    private Boolean divergente;
    /**
     * Materiais que não constavam em um determinado local de estoque e que foram
     * encontrados e registrados no inventário.
     */
    private Boolean encontrado;
    /**
     * Determina que o ajuste de um item divergente foi executado.
     */
    @Etiqueta("Foi Ajustado")
    private Boolean ajustado;

    @Invisivel
    @OneToMany(mappedBy = "itemInventarioEstoque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemInventarioItemEntrada> itensInventarioItemEntrada;

    @Invisivel
    @OneToMany(mappedBy = "itemInventarioEstoque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemInventarioItemSaida> itensInventarioItemSaida;

    @Invisivel
    @OneToMany(mappedBy = "itemInventarioEstoque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoteItemInventario> lotesItemInventario;

    public ItemInventarioEstoque() {
        itensInventarioItemEntrada = Lists.newArrayList();
        itensInventarioItemSaida = Lists.newArrayList();
        lotesItemInventario = Lists.newArrayList();
        this.quantidadeEsperada = BigDecimal.ZERO;
        this.quantidadeAjustada = BigDecimal.ZERO;
    }

    public List<LoteItemInventario> getLotesItemInventario() {
        return lotesItemInventario;
    }

    public void setLotesItemInventario(List<LoteItemInventario> lotesItemInventario) {
        this.lotesItemInventario = lotesItemInventario;
    }

    public BigDecimal getQuantidadeAjustada() {
        return quantidadeAjustada;
    }

    public void setQuantidadeAjustada(BigDecimal quantidadeAjustada) {
        this.quantidadeAjustada = quantidadeAjustada;
    }

    public Boolean getAjustado() {
        return ajustado;
    }

    public void setAjustado(Boolean ajustado) {
        this.ajustado = ajustado;
    }

    public Boolean getDivergente() {
        return divergente;
    }

    public void setDivergente(Boolean divergente) {
        this.divergente = divergente;
    }

    public Boolean getEncontrado() {
        return encontrado;
    }

    public void setEncontrado(Boolean encontrado) {
        this.encontrado = encontrado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InventarioEstoque getInventarioEstoque() {
        return inventarioEstoque;
    }

    public void setInventarioEstoque(InventarioEstoque inventarioEstoque) {
        this.inventarioEstoque = inventarioEstoque;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public BigDecimal getQuantidadeConstatada() {
        return quantidadeConstatada;
    }

    public void setQuantidadeConstatada(BigDecimal quantidadeConstatada) {
        this.quantidadeConstatada = quantidadeConstatada;
    }

    public BigDecimal getQuantidadeEsperada() {
        return quantidadeEsperada;
    }

    public void setQuantidadeEsperada(BigDecimal quantidadeEsperada) {
        this.quantidadeEsperada = quantidadeEsperada;
    }

    public List<ItemInventarioItemEntrada> getItensInventarioItemEntrada() {
        return itensInventarioItemEntrada;
    }

    public void setItensInventarioItemEntrada(List<ItemInventarioItemEntrada> itensInventarioItemEntrada) {
        this.itensInventarioItemEntrada = itensInventarioItemEntrada;
    }

    public List<ItemInventarioItemSaida> getItensInventarioItemSaida() {
        return itensInventarioItemSaida;
    }

    public void setItensInventarioItemSaida(List<ItemInventarioItemSaida> itensInventarioItemSaida) {
        this.itensInventarioItemSaida = itensInventarioItemSaida;
    }

    public BigDecimal getFinanceiro() {
        return financeiro;
    }

    public void setFinanceiro(BigDecimal financeiro) {
        this.financeiro = financeiro;
    }

    @Override
    public String toString() {
        try {
            return material + " - " + quantidadeConstatada + " - " + quantidadeEsperada + " - " + divergente;
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String toStringAutoComplete() {
        return material + " - " + quantidadeConstatada + " - " + quantidadeEsperada + " - " + divergente;
    }
}
