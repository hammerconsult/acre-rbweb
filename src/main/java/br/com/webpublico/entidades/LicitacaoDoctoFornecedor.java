/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoClassificacaoFornecedor;
import br.com.webpublico.enums.TipoDocumentoHabilitacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author lucas
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Documentos do Fornecedor")
public class LicitacaoDoctoFornecedor extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private LicitacaoFornecedor licitacaoFornecedor;

    @ManyToOne
    private DoctoHabilitacao doctoHabilitacao;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataDeEmissao;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataDeValidade;

    private Boolean documentoVencido;

    private Integer numeroDaCertidao;

    public LicitacaoDoctoFornecedor() {
        super();
    }

    public LicitacaoFornecedor getLicitacaoFornecedor() {
        return licitacaoFornecedor;
    }

    public void setLicitacaoFornecedor(LicitacaoFornecedor licitacaoFornecedor) {
        this.licitacaoFornecedor = licitacaoFornecedor;
    }

    public Date getDataDeEmissao() {
        return dataDeEmissao;
    }

    public void setDataDeEmissao(Date dataDeEmissao) {
        this.dataDeEmissao = dataDeEmissao;
    }

    public DoctoHabilitacao getDoctoHabilitacao() {
        return doctoHabilitacao;
    }

    public void setDoctoHabilitacao(DoctoHabilitacao doctoHabilitacao) {
        this.doctoHabilitacao = doctoHabilitacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroDaCertidao() {
        return numeroDaCertidao;
    }

    public void setNumeroDaCertidao(Integer numeroDaCertidao) {
        this.numeroDaCertidao = numeroDaCertidao;
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

    @Override
    public String toString() {
        return this.doctoHabilitacao.getDescricao();
    }

    public boolean isDocumentoVencido() {
        try {
            return getDataDeValidade() != null
                && DataUtil.dataSemHorario(getDataDeValidade()).compareTo(DataUtil.dataSemHorario(getLicitacaoFornecedor().getLicitacao().getAbertaEm())) < 0;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public void setDocumentoVencido(Boolean documentoVencido) {
        this.documentoVencido = documentoVencido;
    }

    public TipoClassificacaoFornecedor getSituacaoDeAcordoComEsteDocumento() {
        Pessoa f = getLicitacaoFornecedor().getEmpresa();
        getLicitacaoFornecedor().setMensagemDeJustificativa("");
        this.setDocumentoVencido(Boolean.TRUE);
        if (TipoDocumentoHabilitacao.FISCAL.equals(getDoctoHabilitacao().getTipoDoctoHabilitacao().getTipoDocumentoHabilitacao())) {
            if (isDocumentoVencido()) {
                getLicitacaoFornecedor().setJustificativaClassificacao("Habilitado com ressalva, o documento '" + this + "' está vencido. O fornecedor tem um prazo de 3(três) dias para regularizar o ducumento");
                getLicitacaoFornecedor().setMensagemDeJustificativa(getLicitacaoFornecedor().getJustificativaClassificacao());
                return TipoClassificacaoFornecedor.HABILITADOCOMRESSALVA;
            }
        } else {
            if (isDocumentoVencido()) {
                String justificativa = "Inabilitado pois está com documento '" + this + "' vencido.";
                getLicitacaoFornecedor().setJustificativaClassificacao(justificativa);
                getLicitacaoFornecedor().setMensagemDeJustificativa(justificativa);
                return TipoClassificacaoFornecedor.INABILITADO;
            }
        }

        return TipoClassificacaoFornecedor.HABILITADO;
    }

    public void validarCamposObrigatoriosDocumentosSelecionados() {
        ValidacaoException ve = new ValidacaoException();
        if (getDoctoHabilitacao().getRequerNumero() && (this.getNumeroDaCertidao() == null || this.getNumeroDaCertidao().toString().trim().length() <= 0)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo número deve ser informado.");
        }
        if (getDoctoHabilitacao().getRequerEmissao() && this.getDataDeEmissao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de emissão deve ser informado.");
        }
        if (getDoctoHabilitacao().getRequerValidade() && this.getDataDeValidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de validade deve ser informado.");
        }
        ve.lancarException();
    }
}
