/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.enums.TipoParecerLicitacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeDetendorDocumentoLicitacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lucas
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Parecer Licitacao")
public class ParecerLicitacao extends SuperEntidade implements EntidadeDetendorDocumentoLicitacao {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Data do Parecer")
    private Date dataParecer;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Integer numero;

    @Obrigatorio
    @Etiqueta("Observações")
    private String observacoes;

    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Licitação")
    private Licitacao licitacao;

    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tipo de Parecer")
    private TipoParecerLicitacao tipoParecerLicitacao;

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Parecerista")
    private PessoaFisica pessoa;


    @Etiqueta("Deferido ou Indeferido")
    private Boolean deferido;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorDocumentoLicitacao detentorDocumentoLicitacao;

    public ParecerLicitacao() {

    }

    public PessoaFisica getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaFisica pessoa) {
        this.pessoa = pessoa;
    }

    public Date getDataParecer() {
        return dataParecer;
    }

    public void setDataParecer(Date dataParecer) {
        this.dataParecer = dataParecer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public TipoParecerLicitacao getTipoParecerLicitacao() {
        return tipoParecerLicitacao;
    }

    public void setTipoParecerLicitacao(TipoParecerLicitacao tipoParecerLicitacao) {
        this.tipoParecerLicitacao = tipoParecerLicitacao;
    }

    public Boolean getDeferido() {
        return deferido;
    }

    public void setDeferido(Boolean deferido) {
        this.deferido = deferido;
    }

    @Override
    public DetentorDocumentoLicitacao getDetentorDocumentoLicitacao() {
        return detentorDocumentoLicitacao;
    }

    @Override
    public void setDetentorDocumentoLicitacao(DetentorDocumentoLicitacao detentorDocumentoLicitacao) {
        this.detentorDocumentoLicitacao = detentorDocumentoLicitacao;
    }

    @Override
    public TipoMovimentoProcessoLicitatorio getTipoAnexo() {
        return TipoMovimentoProcessoLicitatorio.PARECER_LICITACAO;
    }

    @Override
    public String toString() {
        return this.getNumero() + " - " + this.getTipoParecerLicitacao().getDescricao();
    }
}
