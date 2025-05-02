/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoAprovacaoMaterial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Item Aprovação Material")
public class ItemAprovacaoMaterial extends SuperEntidade implements Comparable<ItemAprovacaoMaterial> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Obrigatorio
    private BigDecimal quantidadeAprovada;

    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Obrigatorio
    private TipoAprovacaoMaterial tipoAprovacao;

    @OneToOne
    private ItemRequisicaoMaterial itemRequisicaoMaterial;

    @ManyToOne
    private AprovacaoMaterial aprovacaoMaterial;

    public ItemAprovacaoMaterial() {
        super();
    }

    public ItemAprovacaoMaterial(AprovacaoMaterial aprovacaoMaterial, ItemRequisicaoMaterial itemRequisicaoMaterial) {
        super();
        this.aprovacaoMaterial = aprovacaoMaterial;
        this.itemRequisicaoMaterial = itemRequisicaoMaterial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemRequisicaoMaterial getItemRequisicaoMaterial() {
        return itemRequisicaoMaterial;
    }

    public void setItemRequisicaoMaterial(ItemRequisicaoMaterial itemRequisicaoMaterial) {
        this.itemRequisicaoMaterial = itemRequisicaoMaterial;
    }

    public BigDecimal getQuantidadeAprovada() {
        return quantidadeAprovada;
    }

    public void setQuantidadeAprovada(BigDecimal quantidadeAprovada) {
        this.quantidadeAprovada = quantidadeAprovada;
    }

    public TipoAprovacaoMaterial getTipoAprovacao() {
        return tipoAprovacao;
    }

    public void setTipoAprovacao(TipoAprovacaoMaterial tipoAprovacaoMaterial) {
        this.tipoAprovacao = tipoAprovacaoMaterial;
    }

    public AprovacaoMaterial getAprovacaoMaterial() {
        return aprovacaoMaterial;
    }

    public void setAprovacaoMaterial(AprovacaoMaterial aprovacaoMaterial) {
        this.aprovacaoMaterial = aprovacaoMaterial;
    }

    @Override
    public String toString() {
        return itemRequisicaoMaterial.getMaterialRequisitado().getDescricao();
    }

    public boolean isAprovadoTotalmente() {
        return TipoAprovacaoMaterial.APROVADO_TOTAL.equals(tipoAprovacao);
    }

    public boolean isAprovadoParcialmente() {
        return TipoAprovacaoMaterial.APROVADO_PARCIAL.equals(tipoAprovacao);
    }

    public boolean isAprovado() {
        return isAprovadoParcialmente() || isAprovadoTotalmente();
    }

    public boolean isRejeitado() {
        return TipoAprovacaoMaterial.REJEITADO.equals(tipoAprovacao);
    }

    public Material getMaterial() {
        return itemRequisicaoMaterial.getMaterialAprovado();
    }

    public boolean isMaterialRequisitadoFoiTrocado() {
        return !itemRequisicaoMaterial.getMaterialRequisitado().equals(itemRequisicaoMaterial.getMaterialAprovado());
    }

    public String getQtdeAprovadaFormatada() {
        return Util.formataQuandoDecimal(quantidadeAprovada, itemRequisicaoMaterial.getMaterialDisponibilizado().getMascaraQuantidade());
    }

    public BigDecimal getQuantidadeReservada() {
        BigDecimal quantidade = BigDecimal.ZERO;
        if (getQuantidadeAprovada() != null && getQuantidadeAprovada().compareTo(BigDecimal.ZERO) != 0) {
            if (getItemRequisicaoMaterial().getQuantidadeAtendida() != null) {
                quantidade = getQuantidadeAprovada().subtract(getItemRequisicaoMaterial().getQuantidadeAtendida());

            } else if (getItemRequisicaoMaterial().getQuantidadeEmTransito() != null) {
                quantidade = getQuantidadeAprovada().subtract(getItemRequisicaoMaterial().getQuantidadeEmTransito());
            }
        }
        return quantidade;
    }

    @Override
    public int compareTo(ItemAprovacaoMaterial o) {
        if (o.getItemRequisicaoMaterial().getMaterialRequisitado() != null
            && !Util.isStringNulaOuVazia(o.getItemRequisicaoMaterial().getMaterialRequisitado().getDescricao())
            && getItemRequisicaoMaterial().getMaterialRequisitado() != null && !Util.isStringNulaOuVazia(getItemRequisicaoMaterial().getMaterialRequisitado().getDescricao())) {
            return StringUtil.removeAcentuacao(getItemRequisicaoMaterial().getMaterialRequisitado().getDescricao())
                .compareToIgnoreCase(StringUtil.removeAcentuacao(o.getItemRequisicaoMaterial().getMaterialRequisitado().getDescricao()));
        }
        return 0;
    }
}
