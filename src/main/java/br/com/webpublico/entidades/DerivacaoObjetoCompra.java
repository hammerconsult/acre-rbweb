package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
@Etiqueta("Derivação do Objeto de Compra")
public class DerivacaoObjetoCompra extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "derivacaoObjetoCompra", orphanRemoval = true)
    private List<DerivacaoObjetoCompraComponente> derivacaoComponentes;

    public DerivacaoObjetoCompra() {
        derivacaoComponentes = Lists.newArrayList();
    }

    @Override
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

    public List<DerivacaoObjetoCompraComponente> getDerivacaoComponentes() {
        return derivacaoComponentes;
    }

    public void setDerivacaoComponentes(List<DerivacaoObjetoCompraComponente> derivacaoComponentes) {
        this.derivacaoComponentes = derivacaoComponentes;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
