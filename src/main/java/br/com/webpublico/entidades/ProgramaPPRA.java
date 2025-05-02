package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by carlos on 05/09/15.
 */
@Entity
@Audited
@Etiqueta("Programa de Prevenção de Riscos Ambientais")
public class ProgramaPPRA extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Nome do Programa")
    @Tabelavel
    @Pesquisavel
    private String nomePrograma;
    @Obrigatorio
    @Etiqueta("Descrição")
    @Tabelavel
    @Pesquisavel
    private String descricao;
    @Obrigatorio
    @Etiqueta("Objetivo")
    @Tabelavel
    @Pesquisavel
    private String objetivo;
    @Obrigatorio
    @Etiqueta("Prazo")
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date prazo;
    @Obrigatorio
    @Etiqueta("Responsável")
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private PessoaFisica pessoaFisica;
    @Obrigatorio
    @Etiqueta("Conclusão")
    @Tabelavel
    @Pesquisavel
    private String conclusao;
    @Obrigatorio
    @Etiqueta("PPRA")
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private PPRA ppra;

    public ProgramaPPRA() {
    }

    public String getConclusao() {
        return conclusao;
    }

    public void setConclusao(String conclusao) {
        this.conclusao = conclusao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomePrograma() {
        return nomePrograma;
    }

    public void setNomePrograma(String nomePrograma) {
        this.nomePrograma = nomePrograma;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public Date getPrazo() {
        return prazo;
    }

    public void setPrazo(Date prazo) {
        this.prazo = prazo;
    }

    public PPRA getPpra() {
        return ppra;
    }

    public void setPpra(PPRA ppra) {
        this.ppra = ppra;
    }

    @Override
    public String toString() {
        return nomePrograma;
    }
}
