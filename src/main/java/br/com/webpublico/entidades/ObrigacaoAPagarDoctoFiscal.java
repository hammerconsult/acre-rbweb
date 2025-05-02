package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.negocios.TipoContaAuxiliarFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.TreeMap;

/**
 * Created by mga on 22/06/2017.
 */
@Entity
@Audited
@Etiqueta("Documento Fiscal de Obrigação a Pagar")
public class ObrigacaoAPagarDoctoFiscal extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Obrigação a Pagar")
    private ObrigacaoAPagar obrigacaoAPagar;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Etiqueta("Documento Fiscal")
    private DoctoFiscalLiquidacao documentoFiscal;

    @Monetario
    @Etiqueta("Valor")
    private BigDecimal valor;

    @Monetario
    @Etiqueta("Saldo")
    private BigDecimal saldo;

    public ObrigacaoAPagarDoctoFiscal() {
        valor = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ObrigacaoAPagar getObrigacaoAPagar() {
        return obrigacaoAPagar;
    }

    public void setObrigacaoAPagar(ObrigacaoAPagar obrigacaoAPagar) {
        this.obrigacaoAPagar = obrigacaoAPagar;
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

    public BigDecimal getSaldo() {
        if (saldo == null) {
            return BigDecimal.ZERO;
        }
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
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
        if (documentoFiscal.getTipoDocumentoFiscal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo Documento deve ser informado.");
        } else if (documentoFiscal.getTipoDocumentoFiscal().getObrigarChaveDeAcesso() && documentoFiscal.getChaveDeAcesso().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Chave de Acesso deve ser informado.");
        }
        if (documentoFiscal.getDataDocto() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Emissão deve ser informado.");
        }
        if (documentoFiscal.getNumero() == null || documentoFiscal.getNumero().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número do Documento deve ser informado.");
        }
        if (documentoFiscal.getValor() == null || documentoFiscal.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Valor deve ser maior que zero (0). ");
        }
        ve.lancarException();
    }
}

