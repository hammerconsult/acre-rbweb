/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Monetario;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Wellington
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Reserva de Dotação de Solicitação de Compra - Itens")
@Table(name = "DOTACAOSOLMATITEM")
public class DotacaoSolicitacaoMaterialItem extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Dotação Solicitação Material")
    private DotacaoSolicitacaoMaterial dotacaoSolicitacaoMaterial;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Objeto de Compra")
    private TipoObjetoCompra tipoObjetoCompra;

    @Monetario
    @Etiqueta("Valor")
    private BigDecimal valor;

    @Invisivel
    @OneToMany(mappedBy = "dotacaoSolMatItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DotacaoSolicitacaoMaterialItemFonte> fontes;

    public DotacaoSolicitacaoMaterialItem() {
        valor = BigDecimal.ZERO;
        fontes = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DotacaoSolicitacaoMaterial getDotacaoSolicitacaoMaterial() {
        return dotacaoSolicitacaoMaterial;
    }

    public void setDotacaoSolicitacaoMaterial(DotacaoSolicitacaoMaterial dotacaoSolicitacaoMaterial) {
        this.dotacaoSolicitacaoMaterial = dotacaoSolicitacaoMaterial;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<DotacaoSolicitacaoMaterialItemFonte> getFontes() {
        return fontes;
    }

    public void setFontes(List<DotacaoSolicitacaoMaterialItemFonte> fontes) {
        this.fontes = fontes;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }
}
