package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 20/08/15
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class Edital extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Exercício")
    @ManyToOne
    private Exercicio exercicio;
    @Tabelavel
    @Etiqueta("Número")
    private Integer numero;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Data de Publicação")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataPublicacao;
    @Tabelavel
    @Etiqueta("Data de Validade")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataValidade;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Título")
    private String titulo;
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public Date getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(Date dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }
}
