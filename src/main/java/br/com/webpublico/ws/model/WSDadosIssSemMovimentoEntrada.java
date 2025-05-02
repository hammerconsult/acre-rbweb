package br.com.webpublico.ws.model;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DMSArrecadacao", namespace = "NotaFiscalEletronica")
@XmlRootElement(name = "DMSArrecadacao")
public class WSDadosIssSemMovimentoEntrada {

    /*DMSCTCID*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private Integer DMSCTCID;
    /*DMSMESREF*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private Integer DMSMESREF;
    /*DMSANOREF*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private Integer DMSANOREF;
    /*DMSTIPOMOV
     1 - INCLUSÃO
     2 - ATUALIZAÇÃO
     3 - BAIXA MANUAL
     9 - EXCLUSÃO
    */
    @XmlElement(namespace = "NotaFiscalEletronica")
    private Integer DMSTIPOMOV;
    /*DMSDTMOV*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private Date DMSDTMOV;
    /*DMSTRIB
     1 - ISSQN VARÍAVEL
     2 - ISSQN SUBSTITUTO
     3 - ISSQN RESPONSÁVEL
    */
    @XmlElement(namespace = "NotaFiscalEletronica")
    private Integer DMSTRIB;
    /*DMSSIT
     S - SEM MOVIMENTO
     J - SEM RETENÇÃO
     M - RETIDO NA FONTE
     H - RECOLHIMENTO FORA
    */
    @XmlElement(namespace = "NotaFiscalEletronica")
    private String DMSSIT;
    /*DMSVLRDEC*/
    @XmlElement(namespace = "NotaFiscalEletronica")
    private BigDecimal DMSVLRDEC;

    public Integer getDMSCTCID() {
        return DMSCTCID;
    }

    public void setDMSCTCID(Integer DMSCTCID) {
        this.DMSCTCID = DMSCTCID;
    }

    public Integer getDMSMESREF() {
        return DMSMESREF;
    }

    public void setDMSMESREF(Integer DMSMESREF) {
        this.DMSMESREF = DMSMESREF;
    }

    public Integer getDMSANOREF() {
        return DMSANOREF;
    }

    public void setDMSANOREF(Integer DMSANOREF) {
        this.DMSANOREF = DMSANOREF;
    }

    public Integer getDMSTIPOMOV() {
        return DMSTIPOMOV;
    }

    public void setDMSTIPOMOV(Integer DMSTIPOMOV) {
        this.DMSTIPOMOV = DMSTIPOMOV;
    }

    public Date getDMSDTMOV() {
        return DMSDTMOV;
    }

    public void setDMSDTMOV(Date DMSDTMOV) {
        this.DMSDTMOV = DMSDTMOV;
    }

    public Integer getDMSTRIB() {
        return DMSTRIB;
    }

    public void setDMSTRIB(Integer DMSTRIB) {
        this.DMSTRIB = DMSTRIB;
    }

    public String getDMSSIT() {
        return DMSSIT;
    }

    public void setDMSSIT(String DMSSIT) {
        this.DMSSIT = DMSSIT;
    }

    public BigDecimal getDMSVLRDEC() {
        return DMSVLRDEC;
    }

    public void setDMSVLRDEC(BigDecimal DMSVLRDEC) {
        this.DMSVLRDEC = DMSVLRDEC;
    }
}
