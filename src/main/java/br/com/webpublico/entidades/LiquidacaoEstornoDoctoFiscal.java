package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mga on 31/07/2017.
 */
@Entity
@Audited
@Table(name = "LIQUIDACAOESTDOCTOFISCAL")
public class LiquidacaoEstornoDoctoFiscal extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Estorno de Liquidação")
    @ManyToOne
    private LiquidacaoEstorno liquidacaoEstorno;
    @Etiqueta("Documento Fiscal")
    @ManyToOne
    private DoctoFiscalLiquidacao documentoFiscal;
    @Etiqueta("Valor")
    @Monetario
    private BigDecimal valor;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "liquidacaoEstDoctoFiscal")
    private List<EstornoLiquidacaoDoctoFiscalTransferenciaBens> transferenciasBens;

    public LiquidacaoEstornoDoctoFiscal() {
        valor = BigDecimal.ZERO;
        transferenciasBens = Lists.newArrayList();
    }

    public BigDecimal getValorTotalDasTransferencias() {
        BigDecimal retorno = BigDecimal.ZERO;
        for (EstornoLiquidacaoDoctoFiscalTransferenciaBens transferencia : transferenciasBens) {
            retorno = retorno.add(transferencia.getValor());
        }
        return retorno;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LiquidacaoEstorno getLiquidacaoEstorno() {
        return liquidacaoEstorno;
    }

    public void setLiquidacaoEstorno(LiquidacaoEstorno liquidacaoEstorno) {
        this.liquidacaoEstorno = liquidacaoEstorno;
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

    public List<EstornoLiquidacaoDoctoFiscalTransferenciaBens> getTransferenciasBens() {
        return transferenciasBens;
    }

    public void setTransferenciasBens(List<EstornoLiquidacaoDoctoFiscalTransferenciaBens> transferenciasBens) {
        this.transferenciasBens = transferenciasBens;
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
