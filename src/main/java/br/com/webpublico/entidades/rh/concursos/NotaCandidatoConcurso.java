package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.CriterioDesempate;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Entity
@Audited
public class NotaCandidatoConcurso extends SuperEntidade implements Serializable, Comparable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Avaliação Prova")
    private AvaliacaoProvaConcurso avaliacaoProvaConcurso;
    @OneToOne
    @Obrigatorio
    @Etiqueta("Inscrição Concurso")
    private InscricaoConcurso inscricao;
    @Etiqueta("Observações")
    private String observacoes;
    @Etiqueta("Nota")
    @Obrigatorio
    private BigDecimal nota;
    @Transient
    @Invisivel
    private BigDecimal novaNota;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AvaliacaoProvaConcurso getAvaliacaoProvaConcurso() {
        return avaliacaoProvaConcurso;
    }

    public void setAvaliacaoProvaConcurso(AvaliacaoProvaConcurso avaliacaoProvaConcurso) {
        this.avaliacaoProvaConcurso = avaliacaoProvaConcurso;
    }

    public InscricaoConcurso getInscricao() {
        return inscricao;
    }

    public void setInscricao(InscricaoConcurso inscricao) {
        this.inscricao = inscricao;
    }

    public BigDecimal getNota() {
        return nota;
    }

    public void setNota(BigDecimal nota) {
        this.nota = nota;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    @Override
    public int compareTo(Object o) {
//        o = (NotaCandidatoConcurso) o;
        return this.getInscricao().getNumero().compareTo(((NotaCandidatoConcurso) o).getInscricao().getNumero());
    }

    public boolean isCandidatoSolicitanteDoRecurso(InscricaoConcurso candidato) {
        return candidato.equals(this.inscricao);
    }

    public String getCor(InscricaoConcurso candidato) {
        return isCandidatoSolicitanteDoRecurso(candidato) ? "green" : "black";
    }

    public BigDecimal getNovaNota() {
        return novaNota;
    }

    public void setNovaNota(BigDecimal novaNota) {
        this.novaNota = novaNota;
    }

    public boolean temNovaNota() {
        return this.novaNota != null;
    }
}
