package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Audited
@Etiqueta("Classificação de Verbas")
public class ClassificacaoVerba extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Código")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private Integer codigo;
    @Etiqueta("Descrição")
    @Length(maximo = 70)
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String descricao;

    public ClassificacaoVerba() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
