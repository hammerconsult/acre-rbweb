package br.com.webpublico.ws.model;

import br.com.webpublico.enums.SituacaoParcela;

import javax.persistence.Column;
import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GuiaArrecadacao", namespace = "NotaFiscalEletronica")
@XmlRootElement(name = "GuiaArrecadacao")
public class WSDadosArrecadacaoEntrada {

    /*GRCID*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private Integer GRCID;
    /*GRCCTCID*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private Integer GRCCTCID;
    /*GRCTRIBUTO
      1	I.S.S.Q.N
      2	SUBSTITUIÇÃO
      3	I.S.S.Q.N.RETIDO
      8	MULTA ACESSÓRIA*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private Integer GRCTRIBUTO;
    /*GRCMESREF*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private Integer GRCMESREF;
    /*GRCANOREF*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private Integer GRCANOREF;
    /*GRCDATAVNC*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private Date GRCDATAVNC;
    /*GRCVLRDEB*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private BigDecimal GRCVLRDEB;
    /*GRCVLRMULTA*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private BigDecimal GRCVLRMULTA;
    /*GRCVLRJUROS*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private BigDecimal GRCVLRJUROS;
    /*GRCVLRCORRECAO*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private BigDecimal GRCVLRCORRECAO;
    /*GRCTIPOMOVIMENTO
     01	Inclusão
     02	Alteração
     03	Pagamento
     04	(Re)Parcelamento
     05	Ajuizamento
     06	Estorno
     07	Reabilitação
     08	Inscrição em Divida Ativa
     09	Cancelamento
     */
    @XmlElement(namespace = "NotaFiscalEletronica")
    private Integer GRCTIPOMOVIMENTO;
    /*GRCDTMOVIMENTO*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private Date GRCDTMOVIMENTO;
    /*GRCVLRPAGO
      Quando GRCTIPOMOVIMENTO  for 03*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private BigDecimal GRCVLRPAGO;
    /*GRCCHAVE*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private String GRCCHAVE;
    /*GRCNOSSONUMERO*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private String GRCNOSSONUMERO;
    /*GRCDATAVNCDAM*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private Date GRCDATAVNCDAM;

    public Integer getGRCID() {
        return GRCID;
    }

    public void setGRCID(Integer GRCID) {
        this.GRCID = GRCID;
    }

    public Integer getGRCCTCID() {
        return GRCCTCID;
    }

    public void setGRCCTCID(Integer GRCCTCID) {
        this.GRCCTCID = GRCCTCID;
    }

    public Integer getGRCTRIBUTO() {
        return GRCTRIBUTO;
    }

    public void setGRCTRIBUTO(Integer GRCTRIBUTO) {
        this.GRCTRIBUTO = GRCTRIBUTO;
    }

    public Integer getGRCMESREF() {
        return GRCMESREF;
    }

    public void setGRCMESREF(Integer GRCMESREF) {
        this.GRCMESREF = GRCMESREF;
    }

    public Integer getGRCANOREF() {
        return GRCANOREF;
    }

    public void setGRCANOREF(Integer GRCANOREF) {
        this.GRCANOREF = GRCANOREF;
    }

    public Date getGRCDATAVNC() {
        return GRCDATAVNC;
    }

    public void setGRCDATAVNC(Date GRCDATAVNC) {
        this.GRCDATAVNC = GRCDATAVNC;
    }

    public BigDecimal getGRCVLRDEB() {
        return GRCVLRDEB;
    }

    public void setGRCVLRDEB(BigDecimal GRCVLRDEB) {
        this.GRCVLRDEB = GRCVLRDEB;
    }

    public BigDecimal getGRCVLRMULTA() {
        return GRCVLRMULTA;
    }

    public void setGRCVLRMULTA(BigDecimal GRCVLRMULTA) {
        this.GRCVLRMULTA = GRCVLRMULTA;
    }

    public BigDecimal getGRCVLRJUROS() {
        return GRCVLRJUROS;
    }

    public void setGRCVLRJUROS(BigDecimal GRCVLRJUROS) {
        this.GRCVLRJUROS = GRCVLRJUROS;
    }

    public BigDecimal getGRCVLRCORRECAO() {
        return GRCVLRCORRECAO;
    }

    public void setGRCVLRCORRECAO(BigDecimal GRCVLRCORRECAO) {
        this.GRCVLRCORRECAO = GRCVLRCORRECAO;
    }

    public Integer getGRCTIPOMOVIMENTO() {
        return GRCTIPOMOVIMENTO;
    }

    public void setGRCTIPOMOVIMENTO(Integer GRCTIPOMOVIMENTO) {
        this.GRCTIPOMOVIMENTO = GRCTIPOMOVIMENTO;
    }

    public Date getGRCDTMOVIMENTO() {
        return GRCDTMOVIMENTO;
    }

    public void setGRCDTMOVIMENTO(Date GRCDTMOVIMENTO) {
        this.GRCDTMOVIMENTO = GRCDTMOVIMENTO;
    }

    public BigDecimal getGRCVLRPAGO() {
        return GRCVLRPAGO;
    }

    public void setGRCVLRPAGO(BigDecimal GRCVLRPAGO) {
        this.GRCVLRPAGO = GRCVLRPAGO;
    }

    public String getGRCCHAVE() {
        return GRCCHAVE;
    }

    public void setGRCCHAVE(String GRCCHAVE) {
        this.GRCCHAVE = GRCCHAVE;
    }

    public String getGRCNOSSONUMERO() {
        return GRCNOSSONUMERO;
    }

    public void setGRCNOSSONUMERO(String GRCNOSSONUMERO) {
        this.GRCNOSSONUMERO = GRCNOSSONUMERO;
    }

    public Date getGRCDATAVNCDAM() {
        return GRCDATAVNCDAM;
    }

    public void setGRCDATAVNCDAM(Date GRCDATAVNCDAM) {
        this.GRCDATAVNCDAM = GRCDATAVNCDAM;
    }

    public static Integer getTipoPorsituacaoParcela(SituacaoParcela situacaoParcela) {
        if (situacaoParcela.isPago()) {
            return TipoIntegracao.PAGAMENTO.valor;
        }
        if (SituacaoParcela.INSCRITA_EM_DIVIDA_ATIVA.equals(situacaoParcela)) {
            return TipoIntegracao.INSCRICAO_EM_DIVIDA_ATIVA.valor;
        }
        if (SituacaoParcela.EM_ABERTO.equals(situacaoParcela)) {
            return TipoIntegracao.ESTORNO.valor;
        }
        return TipoIntegracao.CANCELAMENTO.valor;
    }

    public enum TipoIntegracao {
        INCLUSAO(1),
        ALTERACAO(2),
        PAGAMENTO(3),
        RE_PARCELAMENTO(4),
        AJUIZAMENTO(5),
        ESTORNO(6),
        REABILITACAO(7),
        INSCRICAO_EM_DIVIDA_ATIVA(8),
        CANCELAMENTO(9);
        public int valor;

        private TipoIntegracao(int valor) {
            this.valor = valor;
        }
    }

    public enum TipoMovimento {
        INCLUSAO(1, "Inclusão"),
        ALTERACAO(2, "Alteração"),
        PAGAMENTO(3, "Pagamento"),
        RE_PARCELAMENTO(4, "(Re)Parcelamento"),
        AJUIZAMENTO(5, "Ajuizamento"),
        ESTORNO(6, "Estorno"),
        REABILITAÇÃO(7, "Reabilitação"),
        INSCRICAO_DA(8, "Inscrição em Divida Ativa"),
        CANCELAMENTO(9, "Cancelamento");
        private Integer codigo;
        private String descricao;

        TipoMovimento(Integer codigo, String descricao) {
            this.codigo = codigo;
            this.descricao = descricao;
        }

        public Integer getCodigo() {
            return codigo;
        }

        public String getDescricao() {
            return descricao;
        }
    }

}
