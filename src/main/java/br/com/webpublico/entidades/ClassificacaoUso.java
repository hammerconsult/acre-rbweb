package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
@Etiqueta("Classificação de Uso")
public class ClassificacaoUso extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Código")
    private Integer codigo;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Categoria")
    private String categoria;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @OneToMany(mappedBy = "classificacaoUso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassificacaoUsoItem> itemList;
    private Boolean exibirPortalContribuinte;

    public ClassificacaoUso() {
        itemList = Lists.newArrayList();
        exibirPortalContribuinte = Boolean.FALSE;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<ClassificacaoUsoItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<ClassificacaoUsoItem> itemList) {
        this.itemList = itemList;
    }

    public Boolean getExibirPortalContribuinte() {
        return exibirPortalContribuinte;
    }

    public void setExibirPortalContribuinte(Boolean exibirPortalContribuinte) {
        this.exibirPortalContribuinte = exibirPortalContribuinte;
    }

    public boolean hasItem(ClassificacaoUsoItem classificacaoUsoItem) {
        if (itemList != null) {
            for (ClassificacaoUsoItem usoItem : itemList) {
                if (usoItem.getCodigo().equals(classificacaoUsoItem.getCodigo()))
                    return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return codigo + " - " + categoria + " - " + descricao;
    }
}
