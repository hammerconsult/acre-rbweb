package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by AndreGustavo on 16/10/2014.
 */
@Entity
@Audited
@Etiqueta("Inscrição Capacitação")
public class InscricaoCapacitacao extends SuperEntidade implements Comparable<InscricaoCapacitacao> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long numero;
    @Temporal(TemporalType.DATE)
    private Date dataInscricao;
    @ManyToOne
    private MatriculaFP matriculaFP;
    @ManyToOne
    private Capacitacao capacitacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(Date dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    public MatriculaFP getMatriculaFP() {
        return matriculaFP;
    }

    public void setMatriculaFP(MatriculaFP matriculaFP) {
        this.matriculaFP = matriculaFP;
    }

    public Capacitacao getCapacitacao() {
        return capacitacao;
    }

    public void setCapacitacao(Capacitacao capacitacao) {
        this.capacitacao = capacitacao;
    }

    @Override
    public int compareTo(InscricaoCapacitacao o) {
        return this.numero.compareTo(o.numero);
    }
}
