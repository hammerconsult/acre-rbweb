package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Audited
@Etiqueta("Romaneio Produtos")
@Table(name = "ROMANEIOFEIRAFEIRANTEPROD")
public class RomaneioFeiraFeiranteProduto extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private RomaneioFeiraFeirante romaneioFeiraFeirante;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Produto")
    private ProdutoFeira produtoFeira;

    @Obrigatorio
    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    public RomaneioFeiraFeiranteProduto() {
        quantidade = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RomaneioFeiraFeirante getRomaneioFeiraFeirante() {
        return romaneioFeiraFeirante;
    }

    public void setRomaneioFeiraFeirante(RomaneioFeiraFeirante romaneioFeiraFeirante) {
        this.romaneioFeiraFeirante = romaneioFeiraFeirante;
    }

    public ProdutoFeira getProdutoFeira() {
        return produtoFeira;
    }

    public void setProdutoFeira(ProdutoFeira produtoFeira) {
        this.produtoFeira = produtoFeira;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorTotal() {
        try {
            return quantidade.multiply(produtoFeira.getValorUnitario()).setScale(2, RoundingMode.HALF_EVEN);
        } catch (NullPointerException npe) {
            return BigDecimal.ZERO;
        }
    }
}
