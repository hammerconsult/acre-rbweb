package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 08/06/15
 * Time: 16:32
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Tipo de Vínculo")
public class TipoVinculoBem extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    @Length(maximo = 255)
    private String descricao;

    public TipoVinculoBem() {
        super();
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
}
