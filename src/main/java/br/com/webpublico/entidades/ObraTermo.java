package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by William on 27/04/2017.
 */
@Entity
@Audited
@Etiqueta("Termo da Obra")
public class ObraTermo extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Nome")
    private String nome;
    @Etiqueta("Conteúdo")
    private String conteudo;
    @Etiqueta("Definitivo")
    private Boolean definitivo;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data de Emissão")
    private Date dataEmissao;

    @ManyToOne
    @Etiqueta("Obra")
    private Obra obra;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Boolean getDefinitivo() {
        return definitivo;
    }

    public void setDefinitivo(Boolean definitivo) {
        this.definitivo = definitivo;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Obra getObra() {
        return obra;
    }

    public void setObra(Obra obra) {
        this.obra = obra;
    }

}
