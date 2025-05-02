package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class TipoInfracaoSaud extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date dataCadastro;
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Obrigatorio
    @Etiqueta("Controle de Quantidade")
    private Integer controleQuantidade;

    public TipoInfracaoSaud() {
        super();
        this.dataCadastro = new Date();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getControleQuantidade() {
        return controleQuantidade;
    }

    public void setControleQuantidade(Integer controleQuantidade) {
        this.controleQuantidade = controleQuantidade;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
