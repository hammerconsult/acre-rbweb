package br.com.webpublico.entidades;

import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoClassificacaoFornecedor;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 16/07/14
 * Time: 11:43
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Table(name = "DOCUMENTOFORNECEDORDISP")
@Etiqueta("Documento Fornecedor Dispensa de Licitação")
public class DocumentoFornecedorDispensaDeLicitacao implements Serializable, ValidadorEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private FornecedorDispensaDeLicitacao fornecedorDispensa;

    @ManyToOne
    private DoctoHabilitacao documentoHabilitacao;

    @Obrigatorio
    @Etiqueta("Número do Documento")
    @Pesquisavel
    @Tabelavel
    private Integer numeroDoDocumento;

    @Obrigatorio
    @Etiqueta("Data de Emissão")
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date dataDeEmissao;

    @Obrigatorio
    @Etiqueta("Data de Validade")
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date dataDeValidade;

    @Etiqueta("Documento Vencido")
    @Pesquisavel
    @Tabelavel
    private Boolean documentoVencido;

    @Invisivel
    @Transient
    private Long criadoEm;

    public DocumentoFornecedorDispensaDeLicitacao() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FornecedorDispensaDeLicitacao getFornecedorDispensa() {
        return fornecedorDispensa;
    }

    public void setFornecedorDispensa(FornecedorDispensaDeLicitacao fornecedorDispensa) {
        this.fornecedorDispensa = fornecedorDispensa;
    }

    public DoctoHabilitacao getDocumentoHabilitacao() {
        return documentoHabilitacao;
    }

    public void setDocumentoHabilitacao(DoctoHabilitacao documentoHabilitacao) {
        this.documentoHabilitacao = documentoHabilitacao;
    }

    public Integer getNumeroDoDocumento() {
        return numeroDoDocumento;
    }

    public void setNumeroDoDocumento(Integer numeroDoDocumento) {
        this.numeroDoDocumento = numeroDoDocumento;
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

    public Boolean getDocumentoVencido() {
        return documentoVencido;
    }

    public void setDocumentoVencido(Boolean documentoVencido) {
        this.documentoVencido = documentoVencido;
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

    @Override
    public String toString() {
        return this.documentoHabilitacao.getDescricao();
    }

    public TipoClassificacaoFornecedor getSituacaoDeAcordoComEsteDocumento(Date dataOperacao){
        if (!isDocumentoVencido(dataOperacao)){
            this.setDocumentoVencido(Boolean.FALSE);
            getFornecedorDispensa().setJustificativaDaClassificacao("Habilitado pois está com documento ok.");
            return TipoClassificacaoFornecedor.HABILITADO;
        }

        Pessoa f = getFornecedorDispensa().getPessoa();

        this.setDocumentoVencido(Boolean.TRUE);
        if (f.isPessoaJuridica() && (f.isMicroEmpresa() || f.isPequenaEmpresa() || f.isTipoEmpresaIndefinido())){
            getFornecedorDispensa().setJustificativaDaClassificacao("Habilitado com ressalva pois é pessoa jurídica, está com o documento '" + this + "' vencido e o tipo da empresa é " + f.getTipoEmpresa().getDescricao().toUpperCase() + ". O fornecedor tem um prazo de 3(três) dias para regularizar o documento.");
            return TipoClassificacaoFornecedor.HABILITADOCOMRESSALVA;
        }

        if (f.isPessoaJuridica()){
            getFornecedorDispensa().setJustificativaDaClassificacao("Inabilitado pois está com documento vencido e não se enquadra para habilitação com ressalva pois o tipo da empresa é " + f.getTipoEmpresa().getDescricao().toUpperCase() + ".");
        } else if (f.isPessoaFisica()){
            getFornecedorDispensa().setJustificativaDaClassificacao("Inabilitado pois está com documento vencido e não se enquadra para habilitação com ressalva pois o fornecedor é uma pessoa física.");
        }
        return TipoClassificacaoFornecedor.INABILITADO;
    }

    public boolean isDocumentoVencido(Date dataOperacao) {
        try{
            return getDataDeValidade() != null && getDataDeValidade().compareTo(dataOperacao) < 0;
        }catch (NullPointerException npe){
            return true;
        }
    }

    public void validarCamposObrigatoriosDocumentosSelecionados() {
        ValidacaoException ve = new ValidacaoException();

        if (getDocumentoHabilitacao().getRequerNumero() && (this.getNumeroDoDocumento() == null || this.getNumeroDoDocumento().toString().trim().length() <= 0)) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "Por favor, informe o número.");
        }

        if (getDocumentoHabilitacao().getRequerEmissao() && this.getDataDeEmissao() == null) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "Por favor, informe a data de emissão.");
        }

        if (getDocumentoHabilitacao().getRequerValidade() && this.getDataDeValidade() == null) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "Por favor, informe a data de validade.");
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }
}
