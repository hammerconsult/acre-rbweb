/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPublicacaoLicitacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lucas
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Publicação Licitação")
public class PublicacaoLicitacao extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Licitação")
    private Licitacao licitacao;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Veículo de Publicação")
    private VeiculoDePublicacao veiculoDePublicacao;

    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data Publicação")
    private Date dataPublicacao;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Publicação da Licitação")
    private TipoPublicacaoLicitacao tipoPublicacaoLicitacao;

    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Length(maximo = 3000)
    @Etiqueta("Observações")
    private String observacoes;

    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    @Length(maximo = 20)
    @Etiqueta("Número Edição Publicação")
    private String numeroEdicaoPublicacao;

    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    @Length(maximo = 20)
    @Etiqueta("Número Página")
    private String numeroPagina;


    public PublicacaoLicitacao() {
        super();
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public TipoPublicacaoLicitacao getTipoPublicacaoLicitacao() {
        return tipoPublicacaoLicitacao;
    }

    public void setTipoPublicacaoLicitacao(TipoPublicacaoLicitacao tipoPublicacaoLicitacao) {
        this.tipoPublicacaoLicitacao = tipoPublicacaoLicitacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public VeiculoDePublicacao getVeiculoDePublicacao() {
        return veiculoDePublicacao;
    }

    public void setVeiculoDePublicacao(VeiculoDePublicacao veiculoDePublicacao) {
        this.veiculoDePublicacao = veiculoDePublicacao;
    }

    public String getNumeroPagina() {
        return numeroPagina;
    }

    public void setNumeroPagina(String numeroPagina) {
        this.numeroPagina = numeroPagina;
    }

    public String getNumeroEdicaoPublicacao() {
        return numeroEdicaoPublicacao;
    }

    public void setNumeroEdicaoPublicacao(String numeroEdicaoPublicacao) {
        this.numeroEdicaoPublicacao = numeroEdicaoPublicacao;
    }

    @Override
    public String toString() {
        return this.veiculoDePublicacao.getNome();
    }

    public boolean isEdital() {
        return TipoPublicacaoLicitacao.EDITAL.equals(this.getTipoPublicacaoLicitacao());
    }

    public boolean isAdjudicacao() {
        return TipoPublicacaoLicitacao.ADJUDICACAO.equals(this.getTipoPublicacaoLicitacao());
    }

    public boolean isAviso() {
        return TipoPublicacaoLicitacao.AVISO.equals(this.getTipoPublicacaoLicitacao());
    }

    public boolean isHomolocacao() {
        return TipoPublicacaoLicitacao.HOMOLOGACAO.equals(this.getTipoPublicacaoLicitacao());
    }

    public boolean isOutros() {
        return TipoPublicacaoLicitacao.OUTROS.equals(this.getTipoPublicacaoLicitacao());
    }

}
