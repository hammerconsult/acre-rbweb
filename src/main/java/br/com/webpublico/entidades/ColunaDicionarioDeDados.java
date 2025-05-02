package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Etiqueta("Coluna do Dicionário de Dados")
public class ColunaDicionarioDeDados extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Ordem")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Integer ordem;
    @Etiqueta("Coluna")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private String coluna;
    @Etiqueta("Descrição")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private String descricao;
    @Etiqueta("Tipo")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private String tipo;
    @ManyToOne
    private DicionarioDeDados dicionarioDeDados;

    public ColunaDicionarioDeDados() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getColuna() {
        return coluna;
    }

    public void setColuna(String coluna) {
        this.coluna = coluna;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public DicionarioDeDados getDicionarioDeDados() {
        return dicionarioDeDados;
    }

    public void setDicionarioDeDados(DicionarioDeDados dicionarioDeDados) {
        this.dicionarioDeDados = dicionarioDeDados;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
