/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author venon
 */
@Entity

@GrupoDiagrama(nome = "ReservaDeDotacao")
@Table(name = "DIARIACIVILSOLICEMP")
@Audited
public class DiariaCivilSolicitacaoEmpenho implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PropostaConcessaoDiaria propostaConcessaoDiaria;
    @ManyToOne
    private SolicitacaoEmpenho solicitacaoEmpenho;
    @Transient
    private Long criadoEm;

    public DiariaCivilSolicitacaoEmpenho() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public PropostaConcessaoDiaria getPropostaConcessaoDiaria() {
        return propostaConcessaoDiaria;
    }

    public void setPropostaConcessaoDiaria(PropostaConcessaoDiaria propostaConcessaoDiaria) {
        this.propostaConcessaoDiaria = propostaConcessaoDiaria;
    }

    public SolicitacaoEmpenho getSolicitacaoEmpenho() {
        return solicitacaoEmpenho;
    }

    public void setSolicitacaoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho) {
        this.solicitacaoEmpenho = solicitacaoEmpenho;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.DiariaCivilSolicitacaoEmpenho[ id=" + id + " ]";
    }
}
