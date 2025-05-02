package br.com.webpublico.entidades;

import br.com.webpublico.enums.administrativo.SituacaoDocumentoFiscalEntradaMaterial;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Audited
@Etiqueta("Documento Fiscal Entrada por Compra")
public class DoctoFiscalEntradaCompra extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Entrada por Compra")
    private EntradaCompraMaterial entradaCompraMaterial;

    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Documento Fiscal")
    private DoctoFiscalLiquidacao doctoFiscalLiquidacao;

    @Enumerated(EnumType.STRING)
    private SituacaoDocumentoFiscalEntradaMaterial situacao;

    @Invisivel
    @OneToMany(mappedBy = "doctoFiscalEntradaCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemDoctoItemEntrada> itens;

    public DoctoFiscalEntradaCompra() {
        itens = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EntradaCompraMaterial getEntradaCompraMaterial() {
        return entradaCompraMaterial;
    }

    public void setEntradaCompraMaterial(EntradaCompraMaterial entradaCompraMaterial) {
        this.entradaCompraMaterial = entradaCompraMaterial;
    }

    public DoctoFiscalLiquidacao getDoctoFiscalLiquidacao() {
        return doctoFiscalLiquidacao;
    }

    public void setDoctoFiscalLiquidacao(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        this.doctoFiscalLiquidacao = doctoFiscalLiquidacao;
    }

    public List<ItemDoctoItemEntrada> getItens() {
        return itens;
    }

    public void setItens(List<ItemDoctoItemEntrada> itens) {
        this.itens = itens;
    }

    public SituacaoDocumentoFiscalEntradaMaterial getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoDocumentoFiscalEntradaMaterial situacao) {
        this.situacao = situacao;
    }

    public BigDecimal getValorTotalDocumento() {
        BigDecimal total = BigDecimal.ZERO;
        try {
            for (ItemDoctoItemEntrada item : getItens()) {
                total = total.add(item.getValorTotal());
            }
            return total;
        } catch (Exception ex) {
            return total;
        }
    }
}
