/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Renato doidão
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Documento Habiltação da Licitação")
public class DoctoHabLicitacao extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Documento")
    private DoctoHabilitacao doctoHabilitacao;

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Licitação")
    private Licitacao licitacao;

    public DoctoHabLicitacao() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DoctoHabilitacao getDoctoHabilitacao() {
        return doctoHabilitacao;
    }

    public void setDoctoHabilitacao(DoctoHabilitacao doctoHabilitacao) {
        this.doctoHabilitacao = doctoHabilitacao;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    @Override
    public String toString() {
        return doctoHabilitacao.getDescricao();
    }
}
