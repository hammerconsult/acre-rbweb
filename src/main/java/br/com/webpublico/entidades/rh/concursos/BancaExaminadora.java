/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Concursos")
@Etiqueta("Banca Examinadora")
public class BancaExaminadora extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Concurso")
    private Concurso concurso;
    @ManyToOne
    private Contrato contrato;
    @OneToMany(mappedBy = "bancaExaminadora", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MembroBancaExaminadora> membros;

    public List<MembroBancaExaminadora> getMembros() {
        return membros;
    }

    public void setMembros(List<MembroBancaExaminadora> membros) {
        this.membros = membros;
    }

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

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }
}
