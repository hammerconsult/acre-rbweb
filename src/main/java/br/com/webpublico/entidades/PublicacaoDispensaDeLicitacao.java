package br.com.webpublico.entidades;

import br.com.webpublico.controle.GrupoObjetoCompraControlador;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 10/07/14
 * Time: 08:24
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Table(name = "PUBLICACAODISPLIC")
@Etiqueta("Publicação Dispensa de Licitação")
public class PublicacaoDispensaDeLicitacao implements Serializable, ValidadorEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Dispensa de Licitação")
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private DispensaDeLicitacao dispensaDeLicitacao;

    @Obrigatorio
    @Etiqueta("Veículo de Publicação")
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private VeiculoDePublicacao veiculoDePublicacao;

    @Obrigatorio
    @Etiqueta("Data da Publicação")
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date dataDaPublicacao;

    @Etiqueta("Número da Edição da Publicação")
    @Tabelavel
    @Pesquisavel
    private String numeroDaEdicaoDaPublicacao;

    @Etiqueta("Número da Página")
    @Tabelavel
    @Pesquisavel
    private String numeroDaPagina;

    @Etiqueta("Observações")
    @Tabelavel
    @Pesquisavel
    private String observacoes;

    @Invisivel
    @Transient
    private Long criadoEm;

    public PublicacaoDispensaDeLicitacao() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DispensaDeLicitacao getDispensaDeLicitacao() {
        return dispensaDeLicitacao;
    }

    public void setDispensaDeLicitacao(DispensaDeLicitacao dispensaDeLicitacao) {
        this.dispensaDeLicitacao = dispensaDeLicitacao;
    }

    public VeiculoDePublicacao getVeiculoDePublicacao() {
        return veiculoDePublicacao;
    }

    public void setVeiculoDePublicacao(VeiculoDePublicacao veiculoDePublicacao) {
        this.veiculoDePublicacao = veiculoDePublicacao;
    }

    public Date getDataDaPublicacao() {
        return dataDaPublicacao;
    }

    public void setDataDaPublicacao(Date dataDaPublicacao) {
        this.dataDaPublicacao = dataDaPublicacao;
    }

    public String getNumeroDaEdicaoDaPublicacao() {
        return numeroDaEdicaoDaPublicacao;
    }

    public void setNumeroDaEdicaoDaPublicacao(String numeroDaEdicaoDaPublicacao) {
        this.numeroDaEdicaoDaPublicacao = numeroDaEdicaoDaPublicacao;
    }

    public String getNumeroDaPagina() {
        return numeroDaPagina;
    }

    public void setNumeroDaPagina(String numeroDaPagina) {
        this.numeroDaPagina = numeroDaPagina;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }
}
