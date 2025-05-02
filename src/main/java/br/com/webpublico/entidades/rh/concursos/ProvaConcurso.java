package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.rh.concursos.MetodoAvaliacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by venom on 13/11/14.
 */
@Entity
@Audited
@Etiqueta(value = "Prova Concurso")
public class ProvaConcurso extends SuperEntidade implements Serializable, ValidadorEntidade, Comparable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @Obrigatorio
    @Etiqueta("Cargo")
    private CargoConcurso cargoConcurso;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Fase")
    private FaseConcurso faseConcurso;
    @Obrigatorio
    @Etiqueta("Nome")
    @Pesquisavel
    private String nome;
    @Etiqueta("Método de Avaliação")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    private MetodoAvaliacao metodoAvaliacao;
    @Etiqueta("Observações")
    private String observacoes;
    @Etiqueta("Endereço")
    @ManyToOne(cascade = CascadeType.ALL)
    @Pesquisavel
    private EnderecoCorreio enderecoCorreio;
    @Etiqueta("Data e horário inicial")
    @Temporal(TemporalType.TIMESTAMP)
    @Obrigatorio
    private Date inicio;
    @Etiqueta("Data e horário final")
    @Temporal(TemporalType.TIMESTAMP)
    @Obrigatorio
    private Date fim;
    @OneToOne(mappedBy = "prova", cascade = CascadeType.ALL, orphanRemoval = true)
    private AvaliacaoProvaConcurso avaliacaoProvaConcurso;
    @Invisivel
    @Etiqueta("Nota Mínima")
    private BigDecimal notaMinima;

    public BigDecimal getNotaMinima() {
        return notaMinima;
    }

    public void setNotaMinima(BigDecimal notaMinima) {
        this.notaMinima = notaMinima;
    }

    public ProvaConcurso() {
//        this.avaliacoes = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CargoConcurso getCargoConcurso() {
        return cargoConcurso;
    }

    public void setCargoConcurso(CargoConcurso cargoConcurso) {
        this.cargoConcurso = cargoConcurso;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public EnderecoCorreio getEnderecoCorreio() {
        return enderecoCorreio;
    }

    public void setEnderecoCorreio(EnderecoCorreio enderecoCorreio) {
        this.enderecoCorreio = enderecoCorreio;
    }

    public MetodoAvaliacao getMetodoAvaliacao() {
        return metodoAvaliacao;
    }

    public void setMetodoAvaliacao(MetodoAvaliacao metodoAvaliacao) {
        this.metodoAvaliacao = metodoAvaliacao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public FaseConcurso getFaseConcurso() {
        return faseConcurso;
    }

    public void setFaseConcurso(FaseConcurso faseConcurso) {
        this.faseConcurso = faseConcurso;
    }

    public AvaliacaoProvaConcurso getAvaliacaoProvaConcurso() {
        return avaliacaoProvaConcurso;
    }

    public void setAvaliacaoProvaConcurso(AvaliacaoProvaConcurso avaliacaoProvaConcurso) {
        this.avaliacaoProvaConcurso = avaliacaoProvaConcurso;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        ValidacaoException exception = new ValidacaoException();

        if (fim.compareTo(inicio) <= 0){
            exception.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A data final deve ser posterior a data inicial.");
        }

        if (exception.temMensagens()) {
            throw exception;
        }
    }

    @Override
    public int compareTo(Object o) {
        return this.inicio.compareTo(((ProvaConcurso) o).getInicio());
    }

    @Override
    public String toString() {
        return this.getNome();
    }

    public boolean temAvaliacao() {
        return this.getAvaliacaoProvaConcurso() != null;
    }
}
