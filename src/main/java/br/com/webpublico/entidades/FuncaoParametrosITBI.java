package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoFuncaoParametrosITBI;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Audited
@Entity
public class FuncaoParametrosITBI extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String descricao;
    @Enumerated(EnumType.STRING)
    private TipoFuncaoParametrosITBI funcao;

    public FuncaoParametrosITBI() {
        super();
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

    public TipoFuncaoParametrosITBI getFuncao() {
        return funcao;
    }

    public void setFuncao(TipoFuncaoParametrosITBI funcao) {
        this.funcao = funcao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
