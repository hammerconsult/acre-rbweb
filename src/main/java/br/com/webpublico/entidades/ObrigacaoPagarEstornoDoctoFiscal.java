package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by mga on 26/07/2017.
 */
@Entity
@Audited
@Etiqueta("Estorno Obrigação a Pagar Documento Fiscal")
@Table(name = "OBRIGACAOPAGESTDOCTOFISCAL")
public class ObrigacaoPagarEstornoDoctoFiscal extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Estorno de Obrigação a Pagar")
    private ObrigacaoAPagarEstorno obrigacaoAPagarEstorno;

    @ManyToOne
    @Etiqueta("Documento Fiscal")
    private DoctoFiscalLiquidacao documentoFiscal;

    @Monetario
    @Etiqueta("Valor Liquidado")
    private BigDecimal valor;

    public ObrigacaoPagarEstornoDoctoFiscal() {
        valor = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ObrigacaoAPagarEstorno getObrigacaoAPagarEstorno() {
        return obrigacaoAPagarEstorno;
    }

    public void setObrigacaoAPagarEstorno(ObrigacaoAPagarEstorno obrigacaoAPagarEstorno) {
        this.obrigacaoAPagarEstorno = obrigacaoAPagarEstorno;
    }

    public DoctoFiscalLiquidacao getDocumentoFiscal() {
        return documentoFiscal;
    }

    public void setDocumentoFiscal(DoctoFiscalLiquidacao documentoFiscal) {
        this.documentoFiscal = documentoFiscal;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        try {
            return this.documentoFiscal.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public void realizarValidacoes() {
        ValidacaoException ve = new ValidacaoException();
        if (documentoFiscal == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Documento Fiscal deve ser informado.");
        }
        if (documentoFiscal.getValor() == null || documentoFiscal.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor deve ser maior que zero (0). ");
        }
        ve.lancarException();
    }
}
