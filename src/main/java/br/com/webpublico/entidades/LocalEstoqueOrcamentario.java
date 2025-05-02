package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 29/07/14
 * Time: 10:39
 * To change this template use File | Settings | File Templates.
 */
@Audited
@Entity
public class LocalEstoqueOrcamentario extends SuperEntidade implements Comparable<LocalEstoqueOrcamentario> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Local de Estoque")
    @Obrigatorio
    @ManyToOne
    private LocalEstoque localEstoque;

    @Etiqueta("Unidade Orçamentária")
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidadeOrcamentaria;

    @OneToMany(mappedBy = "localEstoqueOrcamentario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Estoque> estoques;

    public LocalEstoqueOrcamentario() {
        super();
    }

    public LocalEstoqueOrcamentario(LocalEstoque le, UnidadeOrganizacional unidadeOrcamentaria) {
        super();
        this.localEstoque = le;
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(UnidadeOrganizacional unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public List<Estoque> getEstoques() {
        return estoques;
    }

    public void setEstoques(List<Estoque> estoques) {
        this.estoques = estoques;
    }

    @Override
    public int compareTo(LocalEstoqueOrcamentario leo) {
        return this.id.compareTo(leo.getId());
    }
}
