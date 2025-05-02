/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Concursos")
@Etiqueta("Publicação")
public class PublicacaoConcurso extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Concurso")
    @Tabelavel
    @Pesquisavel
    private Concurso concurso;
    @Etiqueta("Data de Cadastro")
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Date cadastradaEm;
    @OneToOne
    @Etiqueta("Ato Legal")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private AtoLegal atoLegal;

    public Concurso getConcurso() {
        return concurso;
    }

    public void setConcurso(Concurso concurso) {
        this.concurso = concurso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCadastradaEm() {
        return cadastradaEm;
    }

    public void setCadastradaEm(Date cadastradaEm) {
        this.cadastradaEm = cadastradaEm;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }
}
