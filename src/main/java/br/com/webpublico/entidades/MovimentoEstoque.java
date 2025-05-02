/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ItemMovimentoEstoque;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@GrupoDiagrama(nome = "Materiais")
@Audited
@Entity

/**
 * MovimentoEstoque representa toda entrada e saída de material em determinado LocalEstoque em
 * determinado dia.
 *
 * @author cheles
 */
public class MovimentoEstoque extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoOperacao tipoOperacao;

    @Tabelavel
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataMovimento;

    @Tabelavel
    private BigDecimal fisico;

    @Tabelavel
    @Monetario
    private BigDecimal financeiro;

    /**
     * Campo utilizado para exibir o valor unitário do item Entrada/Saída no extrato de estoque.
     */
    @Monetario
    private BigDecimal valorUnitario;

    @Invisivel
    @Monetario
    private BigDecimal financeiroReajustado;

    @ManyToOne
    @Tabelavel
    private Material material;

    @ManyToOne
    @Tabelavel
    private LocalEstoqueOrcamentario localEstoqueOrcamentario;

    /**
     * Opcional, de acordo com o atributo Material.controleDeLote
     */
    @ManyToOne
    private LoteMaterial loteMaterial;

    @ManyToOne
    private Estoque estoque;

    private String descricaoDaOperacao;

    private Long idOrigem;
    private Integer numeroItem;

    public MovimentoEstoque() {
        super();
    }

    public MovimentoEstoque(ItemMovimentoEstoque item, Estoque estoque) {
        super();
        this.dataMovimento = item.getDataMovimento();
        this.fisico = item.getQuantidade();
        this.financeiro = item.getValorTotal();
        this.material = item.getMaterial();
        this.localEstoqueOrcamentario = item.getLocalEstoqueOrcamentario();
        this.tipoOperacao = item.getTipoOperacao();
        this.loteMaterial = item.getLoteMaterial();
        this.estoque = estoque;
        this.descricaoDaOperacao = item.getDescricaoDaOperacao();
        this.valorUnitario = item.getValorUnitario();
        this.idOrigem = item.getIdOrigem();
        this.numeroItem = item.getNumeroItem();
    }

    public Integer getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Integer numeroItem) {
        this.numeroItem = numeroItem;
    }

    public Long getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(Long idOrigem) {
        this.idOrigem = idOrigem;
    }

    public TipoOperacao getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacao tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public BigDecimal getFinanceiro() {
        return financeiro;
    }

    public void setFinanceiro(BigDecimal financeiro) {
        this.financeiro = financeiro;
    }

    public BigDecimal getFinanceiroReajustado() {
        return financeiroReajustado;
    }

    public void setFinanceiroReajustado(BigDecimal financeiroReajustado) {
        this.financeiroReajustado = financeiroReajustado;
    }

    public BigDecimal getFisico() {
        return fisico;
    }

    public void setFisico(BigDecimal fisico) {
        this.fisico = fisico;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalEstoqueOrcamentario getLocalEstoqueOrcamentario() {
        return localEstoqueOrcamentario;
    }

    public void setLocalEstoqueOrcamentario(LocalEstoqueOrcamentario localEstoqueOrcamentario) {
        this.localEstoqueOrcamentario = localEstoqueOrcamentario;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public LoteMaterial getLoteMaterial() {
        return loteMaterial;
    }

    public void setLoteMaterial(LoteMaterial loteMaterial) {
        this.loteMaterial = loteMaterial;
    }

    public Estoque getEstoque() {
        return estoque;
    }

    public void setEstoque(Estoque estoque) {
        this.estoque = estoque;
    }

    public String getDescricaoDaOperacao() {
        return descricaoDaOperacao;
    }

    public void setDescricaoDaOperacao(String descricaoDaOperacao) {
        this.descricaoDaOperacao = descricaoDaOperacao;
    }

    @Override
    public String toString() {
        return dataMovimento + " -- " + financeiro + " -- " + material;
    }
}
