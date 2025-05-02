package br.com.webpublico.entidades;

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
 * Date: 25/06/14
 * Time: 15:52
 * To change this template use File | Settings | File Templates.
 */
@Entity

@Audited
@Etiqueta("Documento Fornecedor")
public class DocumentoFornecedor implements Serializable, ValidadorEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Fornecedor fornecedor;

    @Etiqueta("Documento")
    @Obrigatorio
    @ManyToOne
    private DoctoHabilitacao documentoHabilitacao;

    @Etiqueta("Número")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Integer numero;

    @Etiqueta("Data de Emissão")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date dataDeEmissao;

    @Etiqueta("Data de Validade")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date dataDeValidade;

    @Invisivel
    @Transient
    private Long criadoEm;

    public DocumentoFornecedor() {
        this.criadoEm = System.nanoTime();
    }

    public DocumentoFornecedor(Fornecedor fornecedor, DoctoHabilitacao documentoHabilitacao, Integer numero, Date dataDeEmissao, Date dataDeValidade) {
        this.fornecedor = fornecedor;
        this.documentoHabilitacao = documentoHabilitacao;
        this.numero = numero;
        this.dataDeEmissao = dataDeEmissao;
        this.dataDeValidade = dataDeValidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public DoctoHabilitacao getDocumentoHabilitacao() {
        return documentoHabilitacao;
    }

    public void setDocumentoHabilitacao(DoctoHabilitacao documentoHabilitacao) {
        this.documentoHabilitacao = documentoHabilitacao;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Date getDataDeEmissao() {
        return dataDeEmissao;
    }

    public void setDataDeEmissao(Date dataDeEmissao) {
        this.dataDeEmissao = dataDeEmissao;
    }

    public Date getDataDeValidade() {
        return dataDeValidade;
    }

    public void setDataDeValidade(Date dataDeValidade) {
        this.dataDeValidade = dataDeValidade;
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
