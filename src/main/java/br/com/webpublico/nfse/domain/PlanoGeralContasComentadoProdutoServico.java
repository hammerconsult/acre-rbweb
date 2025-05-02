package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "PGCCPRODUTOSERVICO")
public class PlanoGeralContasComentadoProdutoServico extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProdutoServicoBancario produtoServicoBancario;
    private String descricaoComplementar;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProdutoServicoBancario getProdutoServicoBancario() {
        return produtoServicoBancario;
    }

    public void setProdutoServicoBancario(ProdutoServicoBancario produtoServicoBancario) {
        this.produtoServicoBancario = produtoServicoBancario;
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }
}
