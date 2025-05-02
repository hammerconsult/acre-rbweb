/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPublicacaoLicitacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Publicação do Reconhecimento da Dívida do Exercício")
@Table(name = "PUBLICACAORECONHECDIVIDA")
public class PublicacaoReconhecimentoDivida extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Reconehcimento de Dívida do Exercício")
    private ReconhecimentoDivida reconhecimentoDivida;

    @ManyToOne
    @Etiqueta("Veículo de Publicação")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private VeiculoDePublicacao veiculoDePublicacao;

    @Etiqueta("Data Publicação")
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    private Date dataPublicacao;

    @Etiqueta("Tipo de Publicação da Licitação")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoPublicacaoLicitacao tipoPublicacaoLicitacao;

    @Etiqueta("Observações")
    @Pesquisavel
    @Tabelavel
    @Length(maximo = 3000)
    private String observacoes;

    @Etiqueta("Número Edição Publicação")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Length(maximo = 20)
    private String numeroEdicaoPublicacao;

    @Etiqueta("Número Página")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Length(maximo = 20)
    private String numeroPagina;

    public PublicacaoReconhecimentoDivida() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ReconhecimentoDivida getReconhecimentoDivida() {
        return reconhecimentoDivida;
    }

    public void setReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida) {
        this.reconhecimentoDivida = reconhecimentoDivida;
    }
}
