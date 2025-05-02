package br.com.webpublico.entidades.contabil;

import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Audited
@Entity
public class NotaExplicativaItem extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Nota Explicativa")
    private NotaExplicativa notaExplicativa;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Item Demonstrativo")
    private ItemDemonstrativo itemDemonstrativo;
    @Obrigatorio
    @Etiqueta("Número da Nota")
    private Integer numero;
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;

    public NotaExplicativaItem() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotaExplicativa getNotaExplicativa() {
        return notaExplicativa;
    }

    public void setNotaExplicativa(NotaExplicativa notaExplicativa) {
        this.notaExplicativa = notaExplicativa;
    }

    public ItemDemonstrativo getItemDemonstrativo() {
        return itemDemonstrativo;
    }

    public void setItemDemonstrativo(ItemDemonstrativo itemDemonstrativo) {
        this.itemDemonstrativo = itemDemonstrativo;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
