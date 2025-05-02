package br.com.webpublico.entidades;

import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: MGA
 * Date: 03/09/14
 * Time: 10:09
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta(value = "Empenho de Bem Imóvel", genero = "M")
public class EmpenhoLevantamentoImovel extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número do Empenho")
    @Obrigatorio
    private String numeroEmpenho;

    @Etiqueta("Data do Empenho")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    private Date dataEmpenho;

    @Etiqueta("Valor Referente ao Bem")
    @Obrigatorio
    private BigDecimal valorReferenteAoBem;

    @Etiqueta("Número da Liquidação")
    @Obrigatorio
    private String numeroLiquidacao;

    @Etiqueta("Data da Liquidação")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    private Date dataLiquidacao;

    @Obrigatorio
    @ManyToOne
    private DocumentoComprobatorioLevantamentoBemImovel documentoComprobatorio;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroLiquidacao() {
        return numeroLiquidacao;
    }

    public void setNumeroLiquidacao(String numeroLiquidacao) {
        this.numeroLiquidacao = numeroLiquidacao;
    }

    public Date getDataLiquidacao() {
        return dataLiquidacao;
    }

    public void setDataLiquidacao(Date dataLiquidacao) {
        this.dataLiquidacao = dataLiquidacao;
    }

    public String getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(String numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
    }

    public Date getDataEmpenho() {
        return dataEmpenho;
    }

    public void setDataEmpenho(Date dataEmpenho) {
        this.dataEmpenho = dataEmpenho;
    }

    public BigDecimal getValorReferenteAoBem() {
        return valorReferenteAoBem;
    }

    public void setValorReferenteAoBem(BigDecimal valorReferenteAoBem) {
        this.valorReferenteAoBem = valorReferenteAoBem;
    }

    public DocumentoComprobatorioLevantamentoBemImovel getDocumentoComprobatorio() {
        return documentoComprobatorio;
    }

    public void setDocumentoComprobatorio(DocumentoComprobatorioLevantamentoBemImovel documentoComprobatorio) {
        this.documentoComprobatorio = documentoComprobatorio;
    }

    public void validarRegrasDeNegocio() {
        ValidacaoException ve = new ValidacaoException();

        if (dataEmpenho != null && dataLiquidacao != null) {
            if (dataLiquidacao.before(dataEmpenho)) {
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A data da liquidação deve ser igual ou posterior a data do empenho.");
            }
        }
        if (valorReferenteAoBem != null && valorReferenteAoBem.compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "O valor referente ao bem deve ser maior que 0 (zero).");
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }
}
