package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "DERIVACAOOBJCOMPRACOMP")
@Etiqueta("Derivação do Objeto de Compra Componente")
public class DerivacaoObjetoCompraComponente extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;

    @ManyToOne
    @Etiqueta("Derivação do Objeto de Compra")
    private DerivacaoObjetoCompra derivacaoObjetoCompra;

    public DerivacaoObjetoCompraComponente() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public DerivacaoObjetoCompra getDerivacaoObjetoCompra() {
        return derivacaoObjetoCompra;
    }

    public void setDerivacaoObjetoCompra(DerivacaoObjetoCompra derivacaoObjetoCompra) {
        this.derivacaoObjetoCompra = derivacaoObjetoCompra;
    }

    public String getDescricaoDerivacaoComponente() {
        return derivacaoObjetoCompra.getDescricao() + " / " + descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
