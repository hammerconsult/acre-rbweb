package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by carlos on 17/06/15.
 */
@Entity
@Audited
@Etiqueta("Fator de Risco")
public class FatorDeRisco extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Código")
    private Integer codigo;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @ManyToOne
    private Risco risco;

    public FatorDeRisco() {
    }

    public FatorDeRisco(Integer codigo, String descricao, Risco risco) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.risco = risco;
    }

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

    public Risco getRisco() {
        return risco;
    }

    public void setRisco(Risco risco) {
        this.risco = risco;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
