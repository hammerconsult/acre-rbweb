package br.com.webpublico.entidades;

import br.com.webpublico.enums.CategoriaProdutoFeira;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Audited
@Etiqueta("Produto Feira")
public class ProdutoFeira extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Código")
    private Long codigo;

    @Obrigatorio
    @Etiqueta("Nome")
    private String nome;

    @Obrigatorio
    @Etiqueta("Valor Unitário")
    private BigDecimal valorUnitario;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Categoria")
    private CategoriaProdutoFeira categoria;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Unidade de Medida")
    private UnidadeMedida unidadeMedida;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valor) {
        this.valorUnitario = valor;
    }

    public CategoriaProdutoFeira getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaProdutoFeira categoria) {
        this.categoria = categoria;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    @Override
    public String toString() {
     return codigo + " - " + nome;
    }
}
