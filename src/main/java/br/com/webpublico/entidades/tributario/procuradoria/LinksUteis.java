package br.com.webpublico.entidades.tributario.procuradoria;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 19/08/15
 * Time: 10:47
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Procuradoria")
public class LinksUteis extends SuperEntidade{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String descricao;
    private String link;
    @ManyToOne
    private ParametroProcuradoria parametroProcuradoria;

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ParametroProcuradoria getParametroProcuradoria() {
        return parametroProcuradoria;
    }

    public void setParametroProcuradoria(ParametroProcuradoria parametroProcuradoria) {
        this.parametroProcuradoria = parametroProcuradoria;
    }
}
