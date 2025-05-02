/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoRequisicaoMaterial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author cheles
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Item Requisição Material")
public class ItemRequisicaoMaterial extends SuperEntidade implements Comparable<ItemRequisicaoMaterial> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Requisição")
    @ManyToOne
    private RequisicaoMaterial requisicaoMaterial;

    @Etiqueta("Material Requisitado")
    @ManyToOne
    @Obrigatorio
    private Material materialRequisitado;

    @Etiqueta("Material Aprovado")
    @ManyToOne
    @Obrigatorio
    private Material materialAprovado;

    @Obrigatorio
    @Etiqueta("Quantidade Solicitada")
    private BigDecimal quantidade;

    @Etiqueta("Quantidade Autorizada")
    private BigDecimal quantidadeAutorizada;

    @Etiqueta("Quantidade Atendida")
    private BigDecimal quantidadeAtendida;

    @Etiqueta("Quantidade em Trânsito")
    private BigDecimal quantidadeEmTransito;

    @Transient
    private BigDecimal quantidadeSaida;

    public ItemRequisicaoMaterial() {
        quantidade = BigDecimal.ZERO;
        quantidadeAutorizada = BigDecimal.ZERO;
        quantidadeAtendida = BigDecimal.ZERO;
        quantidadeEmTransito = BigDecimal.ZERO;
        quantidadeSaida = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Material getMaterialRequisitado() {
        return materialRequisitado;
    }

    public void setMaterialRequisitado(Material materialRequisitado) {
        this.materialRequisitado = materialRequisitado;
    }

    public Material getMaterialAprovado() {
        return materialAprovado;
    }

    public void setMaterialAprovado(Material materialAprovado) {
        this.materialAprovado = materialAprovado;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getQuantidadeAutorizada() {
        return quantidadeAutorizada;
    }

    public void setQuantidadeAutorizada(BigDecimal quantidadeAutorizada) {
        this.quantidadeAutorizada = quantidadeAutorizada;
    }

    public RequisicaoMaterial getRequisicaoMaterial() {
        return requisicaoMaterial;
    }

    public void setRequisicaoMaterial(RequisicaoMaterial requisicaoMaterial) {
        this.requisicaoMaterial = requisicaoMaterial;
    }

    public BigDecimal getQuantidadeAtendida() {
        return quantidadeAtendida;
    }

    public void setQuantidadeAtendida(BigDecimal quantidadeAtendida) {
        this.quantidadeAtendida = quantidadeAtendida;
    }

    public BigDecimal getQuantidadeEmTransito() {
        return quantidadeEmTransito;
    }

    public void setQuantidadeEmTransito(BigDecimal quantidadeEmTransito) {
        this.quantidadeEmTransito = quantidadeEmTransito;
    }

    public BigDecimal getQuantidadeSaida() {
        return quantidadeSaida;
    }

    public void setQuantidadeSaida(BigDecimal quantidadeSaida) {
        this.quantidadeSaida = quantidadeSaida;
    }

    public BigDecimal getQuantidadeAAtender() {
        return this.quantidadeAutorizada.subtract(this.quantidadeAtendida.add(this.quantidadeEmTransito));
    }

    public boolean isAtendidoTotalmente() {
        return this.getQuantidadeAAtender().compareTo(BigDecimal.ZERO) == 0;
    }

    public Material getMaterialDisponibilizado() {
        if (this.materialAprovado == null) {
            return this.materialRequisitado;
        }
        return this.materialAprovado;
    }

    @Override
    public String toString() {
        try {
            return this.quantidade + " " + this.materialRequisitado.getUnidadeMedida().getSigla() + " - " + this.materialRequisitado;
        } catch (Exception e) {
            return this.quantidade + " - " + this.materialRequisitado;
        }
    }

    public BigDecimal getQuantidadeAtendidaOuEmTransito() {
        return TipoRequisicaoMaterial.TRANSFERENCIA.equals(requisicaoMaterial.getTipoRequisicao()) ? quantidadeEmTransito : quantidadeAtendida;
    }

    public String getQtdeSolicitadaFormatada() {
        return Util.formataQuandoDecimal(quantidade, getMaterialDisponibilizado().getMascaraQuantidade());
    }

    public String getQtdeAutorizadaFormatada() {
        return Util.formataQuandoDecimal(quantidadeAutorizada, getMaterialDisponibilizado().getMascaraQuantidade());
    }

    public String getQtdeAtenderFormatada() {
        return Util.formataQuandoDecimal(getQuantidadeAAtender(), getMaterialDisponibilizado().getMascaraQuantidade());
    }

    public String getQtdeAtendidaOuEmTransitoFormatada() {
        return Util.formataQuandoDecimal(getQuantidadeAtendidaOuEmTransito(), getMaterialDisponibilizado().getMascaraQuantidade());
    }

    @Override
    public int compareTo(ItemRequisicaoMaterial o) {
        if (o.getMaterialRequisitado() != null
            && !Util.isStringNulaOuVazia(o.getMaterialRequisitado().getDescricao())
            && getMaterialRequisitado() != null && !Util.isStringNulaOuVazia(getMaterialRequisitado().getDescricao())) {
            return StringUtil.removeAcentuacao(getMaterialRequisitado().getDescricao())
                .compareToIgnoreCase(StringUtil.removeAcentuacao(o.getMaterialRequisitado().getDescricao()));
        }
        return 0;
    }
}
