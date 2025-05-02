package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class DfeNacional extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    private String idNfse;
    private String idDec;
    private String ambGer;
    private String versao;
    private String cLocEmi;
    private String xLocEmi;
    private String cLocPrestacao;
    private String xLocPrestacao;
    private String nnfse;
    private String cLocIncid;
    private String xLocIncid;
    private String xTribNac;
    private String xTribMun;
    private String xnbs;
    private String tpEmis;
    private String procEmi;
    private String cStat;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dhProc;
    private String ndfse;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dhEmi;
    private String serie;
    private String ndps;
    @Temporal(TemporalType.DATE)
    private Date dCompet;
    private String tpEmit;
    private String cPaisPrestacao;
    private String opConsumServ;
    private String cTribNac;
    private String cTribMun;
    private String xDescServ;
    private String cnbs;
    private String cIntContrib;
    private BigDecimal vCalcDR;
    private String tpBM;
    private BigDecimal vCalcBM;
    private BigDecimal vbc;
    private BigDecimal pAliqAplic;
    private BigDecimal vissqn;
    private BigDecimal vTotalRet;
    private BigDecimal vLiq;
    private BigDecimal vServ;
    private String xOutInf;
    private String chaveAcesso;
    @OneToOne(cascade = CascadeType.ALL)
    private DfeNacionalPessoa prestador;
    @OneToOne(cascade = CascadeType.ALL)
    private DfeNacionalPessoa tomador;
    @OneToOne(cascade = CascadeType.ALL)
    private DfeNacionalPessoa intermediario;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdNfse() {
        return idNfse;
    }

    public void setIdNfse(String idNfse) {
        this.idNfse = idNfse;
    }

    public String getIdDec() {
        return idDec;
    }

    public void setIdDec(String idDec) {
        this.idDec = idDec;
    }

    public String getAmbGer() {
        return ambGer;
    }

    public void setAmbGer(String ambGer) {
        this.ambGer = ambGer;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public String getcLocEmi() {
        return cLocEmi;
    }

    public void setcLocEmi(String cLocEmi) {
        this.cLocEmi = cLocEmi;
    }

    public String getxLocEmi() {
        return xLocEmi;
    }

    public void setxLocEmi(String xLocEmi) {
        this.xLocEmi = xLocEmi;
    }

    public String getcLocPrestacao() {
        return cLocPrestacao;
    }

    public void setcLocPrestacao(String cLocPrestacao) {
        this.cLocPrestacao = cLocPrestacao;
    }

    public String getxLocPrestacao() {
        return xLocPrestacao;
    }

    public void setxLocPrestacao(String xLocPrestacao) {
        this.xLocPrestacao = xLocPrestacao;
    }

    public String getNnfse() {
        return nnfse;
    }

    public void setNnfse(String nnfse) {
        this.nnfse = nnfse;
    }

    public String getcLocIncid() {
        return cLocIncid;
    }

    public void setcLocIncid(String cLocIncid) {
        this.cLocIncid = cLocIncid;
    }

    public String getxLocIncid() {
        return xLocIncid;
    }

    public void setxLocIncid(String xLocIncid) {
        this.xLocIncid = xLocIncid;
    }

    public String getxTribNac() {
        return xTribNac;
    }

    public void setxTribNac(String xTribNac) {
        this.xTribNac = xTribNac;
    }

    public String getxTribMun() {
        return xTribMun;
    }

    public void setxTribMun(String xTribMun) {
        this.xTribMun = xTribMun;
    }

    public String getXnbs() {
        return xnbs;
    }

    public void setXnbs(String xnbs) {
        this.xnbs = xnbs;
    }

    public String getTpEmis() {
        return tpEmis;
    }

    public void setTpEmis(String tpEmis) {
        this.tpEmis = tpEmis;
    }

    public String getProcEmi() {
        return procEmi;
    }

    public void setProcEmi(String procEmi) {
        this.procEmi = procEmi;
    }

    public String getcStat() {
        return cStat;
    }

    public String getxStat() {
        switch (cStat) {
            case "100":
                return "Emitida";
            default:
                return "Não Identificado";
        }
    }

    public void setcStat(String cStat) {
        this.cStat = cStat;
    }

    public Date getDhProc() {
        return dhProc;
    }

    public void setDhProc(Date dhProc) {
        this.dhProc = dhProc;
    }

    public String getNdfse() {
        return ndfse;
    }

    public void setNdfse(String ndfse) {
        this.ndfse = ndfse;
    }

    public Date getDhEmi() {
        return dhEmi;
    }

    public void setDhEmi(Date dhEmi) {
        this.dhEmi = dhEmi;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getNdps() {
        return ndps;
    }

    public void setNdps(String ndps) {
        this.ndps = ndps;
    }

    public Date getdCompet() {
        return dCompet;
    }

    public void setdCompet(Date dCompet) {
        this.dCompet = dCompet;
    }

    public String getTpEmit() {
        return tpEmit;
    }

    public void setTpEmit(String tpEmit) {
        this.tpEmit = tpEmit;
    }

    public String getcPaisPrestacao() {
        return cPaisPrestacao;
    }

    public void setcPaisPrestacao(String cPaisPrestacao) {
        this.cPaisPrestacao = cPaisPrestacao;
    }

    public String getOpConsumServ() {
        return opConsumServ;
    }

    public void setOpConsumServ(String opConsumServ) {
        this.opConsumServ = opConsumServ;
    }

    public String getcTribNac() {
        return cTribNac;
    }

    public void setcTribNac(String cTribNac) {
        this.cTribNac = cTribNac;
    }

    public String getcTribMun() {
        return cTribMun;
    }

    public void setcTribMun(String cTribMun) {
        this.cTribMun = cTribMun;
    }

    public String getxDescServ() {
        return xDescServ;
    }

    public void setxDescServ(String xDescServ) {
        this.xDescServ = xDescServ;
    }

    public String getCnbs() {
        return cnbs;
    }

    public void setCnbs(String cnbs) {
        this.cnbs = cnbs;
    }

    public String getcIntContrib() {
        return cIntContrib;
    }

    public void setcIntContrib(String cIntContrib) {
        this.cIntContrib = cIntContrib;
    }

    public BigDecimal getvCalcDR() {
        return vCalcDR;
    }

    public void setvCalcDR(BigDecimal vCalcDR) {
        this.vCalcDR = vCalcDR;
    }

    public String getTpBM() {
        return tpBM;
    }

    public void setTpBM(String tpBM) {
        this.tpBM = tpBM;
    }

    public BigDecimal getvCalcBM() {
        return vCalcBM;
    }

    public void setvCalcBM(BigDecimal vCalcBM) {
        this.vCalcBM = vCalcBM;
    }

    public BigDecimal getVbc() {
        return vbc;
    }

    public void setVbc(BigDecimal vbc) {
        this.vbc = vbc;
    }

    public BigDecimal getpAliqAplic() {
        return pAliqAplic;
    }

    public void setpAliqAplic(BigDecimal pAliqAplic) {
        this.pAliqAplic = pAliqAplic;
    }

    public BigDecimal getVissqn() {
        return vissqn;
    }

    public void setVissqn(BigDecimal vissqn) {
        this.vissqn = vissqn;
    }

    public BigDecimal getvTotalRet() {
        return vTotalRet;
    }

    public void setvTotalRet(BigDecimal vTotalRet) {
        this.vTotalRet = vTotalRet;
    }

    public BigDecimal getvLiq() {
        return vLiq;
    }

    public void setvLiq(BigDecimal vLiq) {
        this.vLiq = vLiq;
    }

    public BigDecimal getvServ() {
        return vServ;
    }

    public void setvServ(BigDecimal vServ) {
        this.vServ = vServ;
    }

    public String getxOutInf() {
        return xOutInf;
    }

    public void setxOutInf(String xOutInf) {
        this.xOutInf = xOutInf;
    }

    public String getChaveAcesso() {
        return chaveAcesso;
    }

    public void setChaveAcesso(String chaveAcesso) {
        this.chaveAcesso = chaveAcesso;
    }

    public DfeNacionalPessoa getPrestador() {
        return prestador;
    }

    public void setPrestador(DfeNacionalPessoa prestador) {
        this.prestador = prestador;
    }

    public DfeNacionalPessoa getTomador() {
        return tomador;
    }

    public void setTomador(DfeNacionalPessoa tomador) {
        this.tomador = tomador;
    }

    public DfeNacionalPessoa getIntermediario() {
        return intermediario;
    }

    public void setIntermediario(DfeNacionalPessoa intermediario) {
        this.intermediario = intermediario;
    }

    public String getCodigoDescricaoServico() {
        return cTribNac + "." + cTribMun + " - " + xDescServ;
    }
}
