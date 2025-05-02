package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoParecerLicitacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 10/07/14
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Etiqueta("Parecer")
public class Parecer implements Serializable, ValidadorEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Data do Parecer")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date dataDoParecer;

    @Etiqueta("Número")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Integer numero;

    @Etiqueta("Tipo do Parecer")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoParecerLicitacao tipoDoParecer;

    @Etiqueta("Deferido")
    @Tabelavel
    @Pesquisavel
    private Boolean deferido;

    @Etiqueta("Parecerista")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private PessoaFisica pessoaFisica;

    @Etiqueta("Obervações")
    @Tabelavel
    @Pesquisavel
    @Length(max = 3000)
    private String observacoes;

    @Invisivel
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Invisivel
    @Transient
    private Long criadoEm;

    public Parecer() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataDoParecer() {
        return dataDoParecer;
    }

    public void setDataDoParecer(Date dataDoParecer) {
        this.dataDoParecer = dataDoParecer;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public TipoParecerLicitacao getTipoDoParecer() {
        return tipoDoParecer;
    }

    public void setTipoDoParecer(TipoParecerLicitacao tipoDoParecer) {
        this.tipoDoParecer = tipoDoParecer;
    }

    public Boolean getDeferido() {
        return deferido;
    }

    public void setDeferido(Boolean deferido) {
        this.deferido = deferido;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }
}
