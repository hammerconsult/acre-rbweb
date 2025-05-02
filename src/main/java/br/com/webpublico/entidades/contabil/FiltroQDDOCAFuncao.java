package br.com.webpublico.entidades.contabil;

import br.com.webpublico.entidades.Funcao;
import br.com.webpublico.entidades.SubFuncao;
import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class FiltroQDDOCAFuncao extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private FiltroQDDOCA filtroQDDOCA;
    @ManyToOne
    private Funcao funcao;
    @ManyToOne
    private SubFuncao subFuncao;

    public FiltroQDDOCAFuncao() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FiltroQDDOCA getFiltroQDDOCA() {
        return filtroQDDOCA;
    }

    public void setFiltroQDDOCA(FiltroQDDOCA filtroQDDOCA) {
        this.filtroQDDOCA = filtroQDDOCA;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public SubFuncao getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(SubFuncao subFuncao) {
        this.subFuncao = subFuncao;
    }

    @Override
    public String toString() {
        return funcao.toString() + " - " + subFuncao.toString();
    }
}
