package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.rh.concursos.Abrangencia;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Buzatto on 02/09/2015.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Concursos")
@Etiqueta(value = "Avaliacao Recurso")
public class AvaliacaoRecurso extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta(value = "Data")
    private Date data;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    @Etiqueta(value = "Concurso")
    private Concurso concurso;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @OneToOne(cascade = CascadeType.ALL)
    @Etiqueta(value = "Recurso")
    private RecursoConcurso recursoConcurso;

    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta(value = "Fase")
    private FaseConcurso faseConcurso;

    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta(value = "Prova")
    private ProvaConcurso provaConcurso;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta(value = "Recurso Aceito?")
    private Boolean recursoAceito;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta(value = "Observação/Motivo")
    private String observacao;

    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Abrangência")
    private Abrangencia abrangencia;

    public AvaliacaoRecurso() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Concurso getConcurso() {
        return concurso;
    }

    public void setConcurso(Concurso concurso) {
        this.concurso = concurso;
    }

    public RecursoConcurso getRecursoConcurso() {
        return recursoConcurso;
    }

    public void setRecursoConcurso(RecursoConcurso recursoConcurso) {
        this.recursoConcurso = recursoConcurso;
    }

    public FaseConcurso getFaseConcurso() {
        return faseConcurso;
    }

    public void setFaseConcurso(FaseConcurso faseConcurso) {
        this.faseConcurso = faseConcurso;
    }

    public ProvaConcurso getProvaConcurso() {
        return provaConcurso;
    }

    public void setProvaConcurso(ProvaConcurso provaConcurso) {
        this.provaConcurso = provaConcurso;
    }

    public Boolean getRecursoAceito() {
        return recursoAceito;
    }

    public void setRecursoAceito(Boolean recursoAceito) {
        this.recursoAceito = recursoAceito;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Abrangencia getAbrangencia() {
        return abrangencia;
    }

    public void setAbrangencia(Abrangencia abrangencia) {
        this.abrangencia = abrangencia;
    }

    public boolean temConcurso() {
        return this.concurso != null;
    }

    public boolean temFase() {
        return this.faseConcurso != null;
    }

    public boolean temRecurso() {
        return this.recursoConcurso != null;
    }

    public boolean podeGerarNovaAvaliacao() {
        return temRecurso() && this.recursoAceito != null && this.recursoAceito;
    }

    public boolean isAceito() {
        try {
            return getRecursoAceito();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean isAbrangenciaIndividual() {
        return Abrangencia.INDIVIDUAL.equals(this.abrangencia);
    }

    public boolean isAbrangenciaGeral() {
        return Abrangencia.GERAL.equals(this.abrangencia);
    }

    public boolean isAbrangenciaInformada() {
        return this.abrangencia != null;
    }
}
