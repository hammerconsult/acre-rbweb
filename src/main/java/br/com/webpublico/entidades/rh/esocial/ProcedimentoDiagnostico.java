package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Audited
public class ProcedimentoDiagnostico extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    private String codigo;

    @Pesquisavel
    private String descricao;

    private Boolean observacaoObrigatoria;

    @Override
    public Long getId() {
        return id;
    }

    public Boolean getObservacaoObrigatoria() {
        return observacaoObrigatoria != null ? observacaoObrigatoria : false;
    }

    public void setObservacaoObrigatoria(Boolean observacaoObrigatoria) {
        this.observacaoObrigatoria = observacaoObrigatoria;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
