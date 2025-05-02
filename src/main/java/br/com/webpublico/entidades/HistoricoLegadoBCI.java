/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Alex
 */

@Entity
public class HistoricoLegadoBCI extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date dtaimvhst;
    private String nmrinshst;
    private String nmrprchst;
    private Integer anoprchst;
    private String dtaprchst;
    private String mtvaltimv1;
    private String mtvaltimv2;
    private String mtvaltimv3;
    private String hraaltimv;
    private String usrhstimv;
    private String stsemtetq;
    private String stsprcalt;
    private String flgemtetq;
    private String cltdadhst;
    private String mtvarbhst;
    private String sitatvhst;
    private Integer tpodscedih;
    private String dscedihst;
    private Integer cdgsethst;
    private String cdgqdrhst;
    private String fcequahst;
    private Integer cdglechst;
    private String cdglothst;
    private String cdgltehst;
    private String cdgqlthst;
    private String cdgarrhst;
    private Integer tpoimvhst;
    private String cdgreghst;
    private String sitpgrhst;
    private Integer anopgrhst;
    private Integer cdglgrhst;
    private String nmrlothst;
    private String cepedihst;
    private Integer cdgpeshst;
    private Integer cdgdsthst;
    private Integer cdgmtrhst;
    private Integer metfrachst;
    private Integer metfrenhst;
    private Integer metlddirhs;
    private Integer metldesqhs;
    private Integer metfunhst;
    private Integer areterhst;
    private Integer medtsthst;
    private Integer lnctrbhst;
    private Integer flginshst;
    private Integer qtdcrihhst;
    private Integer qtdcrimhst;
    private Integer qtdadthhst;
    private Integer qtdadtmhst;
    private Integer qtdvelhhst;
    private Integer qtdvelmhst;
    private Integer qtdanfhst;
    private Integer qtddeshst;
    private String lncipthst;
    private String lnctsuhst;
    private Integer cdglgrhs2;
    private Integer cdglgrhs3;
    private Integer cdglgrhs4;
    private String fceqdrhs2;
    private String fceqdrhs3;
    private String fceqdrhs4;
    private String sitorihst;
    private Integer prcegbhst;
    private String arqfothst;
    private String cdggeohst;
    private Integer vlrvnlhst;
    private Integer vlrterhst;
    private Integer aretothst;
    private Integer lgrcorhst;
    private String endcorhst;
    private Integer brrcorhst;
    private String brrdeshst;
    private Integer tpocmpcorh;
    private String cmpcorhst;
    private String dstcorhst;
    private Integer muncorhst;
    private String cepcorhst;
    private String nmrcorhst;
    private Integer cdgltmchst;
    private String dscltmchst;
    private String cdgltechst;
    private String cdgqltchst;
    private Integer cdgprchst;
    private String nmoprchst;
    private String tpoprchst;
    private String obsimvhst;
    private String insatuhst;
    private String matrimvhst;
    private Integer qtdfnthst;
    private Integer qtdundmhst;
    private Integer bseclchst;
    private Integer cdgleccorh;
    private String dscleccorh;
    private Integer tpoentcarh;
    private Integer ultcfrhst;
    private String loccmthst;
    private String sitpenhst;
    private Integer prcpenhst;
    private Integer anopenhst;
    private Integer tpoalthst;
    private Integer nmrprcuhst;
    private String dtanatjhst;
    private Integer qtdtotmulh;
    private Integer qtdtothomh;
    private Integer qtdtotvlhh;
    private Integer qtdtotcrih;
    private Integer anoreshst;
    private Integer nmrfamhst;
    private String imglivregh;
    private String emailcorhs;
    private String foncelcorh;
    private String faxcomcorh;
    private String foncomcorh;
    private String fonrescorh;
    private String qrtimvhst;
    private String flgpenhst;
    private String pdaflag;
    private Integer rotid;
    private Integer rotusrid;
    private String statusrot;
    private String provemde;
    private Integer cdgwebphst;
    private Integer cdgpespchs;
    private Integer nmrserloth;
    private String idnimvloth;
    private String espolioimh;
    private Integer nmrtitdefh;
    private Integer anotitdefh;
    private Integer nmrescpubh;
    private Integer anoescpubh;
    private Integer flhescpubh;
    private Integer cdgcarthst;
    private Integer lvrescpubh;
    @ManyToOne
    @Etiqueta("Cadastro Imobiliário")
    private CadastroImobiliario cadastroImobiliario;

    public HistoricoLegadoBCI() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDtaimvhst() {
        return dtaimvhst;
    }

    public void setDtaimvhst(Date dtaimvhst) {
        this.dtaimvhst = dtaimvhst;
    }

    public String getNmrinshst() {
        return nmrinshst;
    }

    public void setNmrinshst(String nmrinshst) {
        this.nmrinshst = nmrinshst;
    }

    public String getNmrprchst() {
        return nmrprchst;
    }

    public void setNmrprchst(String nmrprchst) {
        this.nmrprchst = nmrprchst;
    }

    public Integer getAnoprchst() {
        return anoprchst;
    }

    public void setAnoprchst(Integer anoprchst) {
        this.anoprchst = anoprchst;
    }

    public String getDtaprchst() {
        return dtaprchst;
    }

    public void setDtaprchst(String dtaprchst) {
        this.dtaprchst = dtaprchst;
    }

    public String getMtvaltimv1() {
        return mtvaltimv1;
    }

    public void setMtvaltimv1(String mtvaltimv1) {
        this.mtvaltimv1 = mtvaltimv1;
    }

    public String getMtvaltimv2() {
        return mtvaltimv2;
    }

    public void setMtvaltimv2(String mtvaltimv2) {
        this.mtvaltimv2 = mtvaltimv2;
    }

    public String getMtvaltimv3() {
        return mtvaltimv3;
    }

    public void setMtvaltimv3(String mtvaltimv3) {
        this.mtvaltimv3 = mtvaltimv3;
    }

    public String getHraaltimv() {
        return hraaltimv;
    }

    public void setHraaltimv(String hraaltimv) {
        this.hraaltimv = hraaltimv;
    }

    public String getUsrhstimv() {
        return usrhstimv;
    }

    public void setUsrhstimv(String usrhstimv) {
        this.usrhstimv = usrhstimv;
    }

    public String getStsemtetq() {
        return stsemtetq;
    }

    public void setStsemtetq(String stsemtetq) {
        this.stsemtetq = stsemtetq;
    }

    public String getStsprcalt() {
        return stsprcalt;
    }

    public void setStsprcalt(String stsprcalt) {
        this.stsprcalt = stsprcalt;
    }

    public String getFlgemtetq() {
        return flgemtetq;
    }

    public void setFlgemtetq(String flgemtetq) {
        this.flgemtetq = flgemtetq;
    }

    public String getCltdadhst() {
        return cltdadhst;
    }

    public void setCltdadhst(String cltdadhst) {
        this.cltdadhst = cltdadhst;
    }

    public String getMtvarbhst() {
        return mtvarbhst;
    }

    public void setMtvarbhst(String mtvarbhst) {
        this.mtvarbhst = mtvarbhst;
    }

    public String getSitatvhst() {
        return sitatvhst;
    }

    public void setSitatvhst(String sitatvhst) {
        this.sitatvhst = sitatvhst;
    }

    public Integer getTpodscedih() {
        return tpodscedih;
    }

    public void setTpodscedih(Integer tpodscedih) {
        this.tpodscedih = tpodscedih;
    }

    public String getDscedihst() {
        return dscedihst;
    }

    public void setDscedihst(String dscedihst) {
        this.dscedihst = dscedihst;
    }

    public Integer getCdgsethst() {
        return cdgsethst;
    }

    public void setCdgsethst(Integer cdgsethst) {
        this.cdgsethst = cdgsethst;
    }

    public String getCdgqdrhst() {
        return cdgqdrhst;
    }

    public void setCdgqdrhst(String cdgqdrhst) {
        this.cdgqdrhst = cdgqdrhst;
    }

    public String getFcequahst() {
        return fcequahst;
    }

    public void setFcequahst(String fcequahst) {
        this.fcequahst = fcequahst;
    }

    public Integer getCdglechst() {
        return cdglechst;
    }

    public void setCdglechst(Integer cdglechst) {
        this.cdglechst = cdglechst;
    }

    public String getCdglothst() {
        return cdglothst;
    }

    public void setCdglothst(String cdglothst) {
        this.cdglothst = cdglothst;
    }

    public String getCdgltehst() {
        return cdgltehst;
    }

    public void setCdgltehst(String cdgltehst) {
        this.cdgltehst = cdgltehst;
    }

    public String getCdgqlthst() {
        return cdgqlthst;
    }

    public void setCdgqlthst(String cdgqlthst) {
        this.cdgqlthst = cdgqlthst;
    }

    public String getCdgarrhst() {
        return cdgarrhst;
    }

    public void setCdgarrhst(String cdgarrhst) {
        this.cdgarrhst = cdgarrhst;
    }

    public Integer getTpoimvhst() {
        return tpoimvhst;
    }

    public void setTpoimvhst(Integer tpoimvhst) {
        this.tpoimvhst = tpoimvhst;
    }

    public String getCdgreghst() {
        return cdgreghst;
    }

    public void setCdgreghst(String cdgreghst) {
        this.cdgreghst = cdgreghst;
    }

    public String getSitpgrhst() {
        return sitpgrhst;
    }

    public void setSitpgrhst(String sitpgrhst) {
        this.sitpgrhst = sitpgrhst;
    }

    public Integer getAnopgrhst() {
        return anopgrhst;
    }

    public void setAnopgrhst(Integer anopgrhst) {
        this.anopgrhst = anopgrhst;
    }

    public Integer getCdglgrhst() {
        return cdglgrhst;
    }

    public void setCdglgrhst(Integer cdglgrhst) {
        this.cdglgrhst = cdglgrhst;
    }

    public String getNmrlothst() {
        return nmrlothst;
    }

    public void setNmrlothst(String nmrlothst) {
        this.nmrlothst = nmrlothst;
    }

    public String getCepedihst() {
        return cepedihst;
    }

    public void setCepedihst(String cepedihst) {
        this.cepedihst = cepedihst;
    }

    public Integer getCdgpeshst() {
        return cdgpeshst;
    }

    public void setCdgpeshst(Integer cdgpeshst) {
        this.cdgpeshst = cdgpeshst;
    }

    public Integer getCdgdsthst() {
        return cdgdsthst;
    }

    public void setCdgdsthst(Integer cdgdsthst) {
        this.cdgdsthst = cdgdsthst;
    }

    public Integer getCdgmtrhst() {
        return cdgmtrhst;
    }

    public void setCdgmtrhst(Integer cdgmtrhst) {
        this.cdgmtrhst = cdgmtrhst;
    }

    public Integer getMetfrachst() {
        return metfrachst;
    }

    public void setMetfrachst(Integer metfrachst) {
        this.metfrachst = metfrachst;
    }

    public Integer getMetfrenhst() {
        return metfrenhst;
    }

    public void setMetfrenhst(Integer metfrenhst) {
        this.metfrenhst = metfrenhst;
    }

    public Integer getMetlddirhs() {
        return metlddirhs;
    }

    public void setMetlddirhs(Integer metlddirhs) {
        this.metlddirhs = metlddirhs;
    }

    public Integer getMetldesqhs() {
        return metldesqhs;
    }

    public void setMetldesqhs(Integer metldesqhs) {
        this.metldesqhs = metldesqhs;
    }

    public Integer getMetfunhst() {
        return metfunhst;
    }

    public void setMetfunhst(Integer metfunhst) {
        this.metfunhst = metfunhst;
    }

    public Integer getAreterhst() {
        return areterhst;
    }

    public void setAreterhst(Integer areterhst) {
        this.areterhst = areterhst;
    }

    public Integer getMedtsthst() {
        return medtsthst;
    }

    public void setMedtsthst(Integer medtsthst) {
        this.medtsthst = medtsthst;
    }

    public Integer getLnctrbhst() {
        return lnctrbhst;
    }

    public void setLnctrbhst(Integer lnctrbhst) {
        this.lnctrbhst = lnctrbhst;
    }

    public Integer getFlginshst() {
        return flginshst;
    }

    public void setFlginshst(Integer flginshst) {
        this.flginshst = flginshst;
    }

    public Integer getQtdcrihhst() {
        return qtdcrihhst;
    }

    public void setQtdcrihhst(Integer qtdcrihhst) {
        this.qtdcrihhst = qtdcrihhst;
    }

    public Integer getQtdcrimhst() {
        return qtdcrimhst;
    }

    public void setQtdcrimhst(Integer qtdcrimhst) {
        this.qtdcrimhst = qtdcrimhst;
    }

    public Integer getQtdadthhst() {
        return qtdadthhst;
    }

    public void setQtdadthhst(Integer qtdadthhst) {
        this.qtdadthhst = qtdadthhst;
    }

    public Integer getQtdadtmhst() {
        return qtdadtmhst;
    }

    public void setQtdadtmhst(Integer qtdadtmhst) {
        this.qtdadtmhst = qtdadtmhst;
    }

    public Integer getQtdvelhhst() {
        return qtdvelhhst;
    }

    public void setQtdvelhhst(Integer qtdvelhhst) {
        this.qtdvelhhst = qtdvelhhst;
    }

    public Integer getQtdvelmhst() {
        return qtdvelmhst;
    }

    public void setQtdvelmhst(Integer qtdvelmhst) {
        this.qtdvelmhst = qtdvelmhst;
    }

    public Integer getQtdanfhst() {
        return qtdanfhst;
    }

    public void setQtdanfhst(Integer qtdanfhst) {
        this.qtdanfhst = qtdanfhst;
    }

    public Integer getQtddeshst() {
        return qtddeshst;
    }

    public void setQtddeshst(Integer qtddeshst) {
        this.qtddeshst = qtddeshst;
    }

    public String getLncipthst() {
        return lncipthst;
    }

    public void setLncipthst(String lncipthst) {
        this.lncipthst = lncipthst;
    }

    public String getLnctsuhst() {
        return lnctsuhst;
    }

    public void setLnctsuhst(String lnctsuhst) {
        this.lnctsuhst = lnctsuhst;
    }

    public Integer getCdglgrhs2() {
        return cdglgrhs2;
    }

    public void setCdglgrhs2(Integer cdglgrhs2) {
        this.cdglgrhs2 = cdglgrhs2;
    }

    public Integer getCdglgrhs3() {
        return cdglgrhs3;
    }

    public void setCdglgrhs3(Integer cdglgrhs3) {
        this.cdglgrhs3 = cdglgrhs3;
    }

    public Integer getCdglgrhs4() {
        return cdglgrhs4;
    }

    public void setCdglgrhs4(Integer cdglgrhs4) {
        this.cdglgrhs4 = cdglgrhs4;
    }

    public String getFceqdrhs2() {
        return fceqdrhs2;
    }

    public void setFceqdrhs2(String fceqdrhs2) {
        this.fceqdrhs2 = fceqdrhs2;
    }

    public String getFceqdrhs3() {
        return fceqdrhs3;
    }

    public void setFceqdrhs3(String fceqdrhs3) {
        this.fceqdrhs3 = fceqdrhs3;
    }

    public String getFceqdrhs4() {
        return fceqdrhs4;
    }

    public void setFceqdrhs4(String fceqdrhs4) {
        this.fceqdrhs4 = fceqdrhs4;
    }

    public String getSitorihst() {
        return sitorihst;
    }

    public void setSitorihst(String sitorihst) {
        this.sitorihst = sitorihst;
    }

    public Integer getPrcegbhst() {
        return prcegbhst;
    }

    public void setPrcegbhst(Integer prcegbhst) {
        this.prcegbhst = prcegbhst;
    }

    public String getArqfothst() {
        return arqfothst;
    }

    public void setArqfothst(String arqfothst) {
        this.arqfothst = arqfothst;
    }

    public String getCdggeohst() {
        return cdggeohst;
    }

    public void setCdggeohst(String cdggeohst) {
        this.cdggeohst = cdggeohst;
    }

    public Integer getVlrvnlhst() {
        return vlrvnlhst;
    }

    public void setVlrvnlhst(Integer vlrvnlhst) {
        this.vlrvnlhst = vlrvnlhst;
    }

    public Integer getVlrterhst() {
        return vlrterhst;
    }

    public void setVlrterhst(Integer vlrterhst) {
        this.vlrterhst = vlrterhst;
    }

    public Integer getAretothst() {
        return aretothst;
    }

    public void setAretothst(Integer aretothst) {
        this.aretothst = aretothst;
    }

    public Integer getLgrcorhst() {
        return lgrcorhst;
    }

    public void setLgrcorhst(Integer lgrcorhst) {
        this.lgrcorhst = lgrcorhst;
    }

    public String getEndcorhst() {
        return endcorhst;
    }

    public void setEndcorhst(String endcorhst) {
        this.endcorhst = endcorhst;
    }

    public Integer getBrrcorhst() {
        return brrcorhst;
    }

    public void setBrrcorhst(Integer brrcorhst) {
        this.brrcorhst = brrcorhst;
    }

    public String getBrrdeshst() {
        return brrdeshst;
    }

    public void setBrrdeshst(String brrdeshst) {
        this.brrdeshst = brrdeshst;
    }

    public Integer getTpocmpcorh() {
        return tpocmpcorh;
    }

    public void setTpocmpcorh(Integer tpocmpcorh) {
        this.tpocmpcorh = tpocmpcorh;
    }

    public String getCmpcorhst() {
        return cmpcorhst;
    }

    public void setCmpcorhst(String cmpcorhst) {
        this.cmpcorhst = cmpcorhst;
    }

    public String getDstcorhst() {
        return dstcorhst;
    }

    public void setDstcorhst(String dstcorhst) {
        this.dstcorhst = dstcorhst;
    }

    public Integer getMuncorhst() {
        return muncorhst;
    }

    public void setMuncorhst(Integer muncorhst) {
        this.muncorhst = muncorhst;
    }

    public String getCepcorhst() {
        return cepcorhst;
    }

    public void setCepcorhst(String cepcorhst) {
        this.cepcorhst = cepcorhst;
    }

    public String getNmrcorhst() {
        return nmrcorhst;
    }

    public void setNmrcorhst(String nmrcorhst) {
        this.nmrcorhst = nmrcorhst;
    }

    public Integer getCdgltmchst() {
        return cdgltmchst;
    }

    public void setCdgltmchst(Integer cdgltmchst) {
        this.cdgltmchst = cdgltmchst;
    }

    public String getDscltmchst() {
        return dscltmchst;
    }

    public void setDscltmchst(String dscltmchst) {
        this.dscltmchst = dscltmchst;
    }

    public String getCdgltechst() {
        return cdgltechst;
    }

    public void setCdgltechst(String cdgltechst) {
        this.cdgltechst = cdgltechst;
    }

    public String getCdgqltchst() {
        return cdgqltchst;
    }

    public void setCdgqltchst(String cdgqltchst) {
        this.cdgqltchst = cdgqltchst;
    }

    public Integer getCdgprchst() {
        return cdgprchst;
    }

    public void setCdgprchst(Integer cdgprchst) {
        this.cdgprchst = cdgprchst;
    }

    public String getNmoprchst() {
        return nmoprchst;
    }

    public void setNmoprchst(String nmoprchst) {
        this.nmoprchst = nmoprchst;
    }

    public String getTpoprchst() {
        return tpoprchst;
    }

    public void setTpoprchst(String tpoprchst) {
        this.tpoprchst = tpoprchst;
    }

    public String getObsimvhst() {
        return obsimvhst;
    }

    public void setObsimvhst(String obsimvhst) {
        this.obsimvhst = obsimvhst;
    }

    public String getInsatuhst() {
        return insatuhst;
    }

    public void setInsatuhst(String insatuhst) {
        this.insatuhst = insatuhst;
    }

    public String getMatrimvhst() {
        return matrimvhst;
    }

    public void setMatrimvhst(String matrimvhst) {
        this.matrimvhst = matrimvhst;
    }

    public Integer getQtdfnthst() {
        return qtdfnthst;
    }

    public void setQtdfnthst(Integer qtdfnthst) {
        this.qtdfnthst = qtdfnthst;
    }

    public Integer getQtdundmhst() {
        return qtdundmhst;
    }

    public void setQtdundmhst(Integer qtdundmhst) {
        this.qtdundmhst = qtdundmhst;
    }

    public Integer getBseclchst() {
        return bseclchst;
    }

    public void setBseclchst(Integer bseclchst) {
        this.bseclchst = bseclchst;
    }

    public Integer getCdgleccorh() {
        return cdgleccorh;
    }

    public void setCdgleccorh(Integer cdgleccorh) {
        this.cdgleccorh = cdgleccorh;
    }

    public String getDscleccorh() {
        return dscleccorh;
    }

    public void setDscleccorh(String dscleccorh) {
        this.dscleccorh = dscleccorh;
    }

    public Integer getTpoentcarh() {
        return tpoentcarh;
    }

    public void setTpoentcarh(Integer tpoentcarh) {
        this.tpoentcarh = tpoentcarh;
    }

    public Integer getUltcfrhst() {
        return ultcfrhst;
    }

    public void setUltcfrhst(Integer ultcfrhst) {
        this.ultcfrhst = ultcfrhst;
    }

    public String getLoccmthst() {
        return loccmthst;
    }

    public void setLoccmthst(String loccmthst) {
        this.loccmthst = loccmthst;
    }

    public String getSitpenhst() {
        return sitpenhst;
    }

    public void setSitpenhst(String sitpenhst) {
        this.sitpenhst = sitpenhst;
    }

    public Integer getPrcpenhst() {
        return prcpenhst;
    }

    public void setPrcpenhst(Integer prcpenhst) {
        this.prcpenhst = prcpenhst;
    }

    public Integer getAnopenhst() {
        return anopenhst;
    }

    public void setAnopenhst(Integer anopenhst) {
        this.anopenhst = anopenhst;
    }

    public Integer getTpoalthst() {
        return tpoalthst;
    }

    public void setTpoalthst(Integer tpoalthst) {
        this.tpoalthst = tpoalthst;
    }

    public Integer getNmrprcuhst() {
        return nmrprcuhst;
    }

    public void setNmrprcuhst(Integer nmrprcuhst) {
        this.nmrprcuhst = nmrprcuhst;
    }

    public String getDtanatjhst() {
        return dtanatjhst;
    }

    public void setDtanatjhst(String dtanatjhst) {
        this.dtanatjhst = dtanatjhst;
    }

    public Integer getQtdtotmulh() {
        return qtdtotmulh;
    }

    public void setQtdtotmulh(Integer qtdtotmulh) {
        this.qtdtotmulh = qtdtotmulh;
    }

    public Integer getQtdtothomh() {
        return qtdtothomh;
    }

    public void setQtdtothomh(Integer qtdtothomh) {
        this.qtdtothomh = qtdtothomh;
    }

    public Integer getQtdtotvlhh() {
        return qtdtotvlhh;
    }

    public void setQtdtotvlhh(Integer qtdtotvlhh) {
        this.qtdtotvlhh = qtdtotvlhh;
    }

    public Integer getQtdtotcrih() {
        return qtdtotcrih;
    }

    public void setQtdtotcrih(Integer qtdtotcrih) {
        this.qtdtotcrih = qtdtotcrih;
    }

    public Integer getAnoreshst() {
        return anoreshst;
    }

    public void setAnoreshst(Integer anoreshst) {
        this.anoreshst = anoreshst;
    }

    public Integer getNmrfamhst() {
        return nmrfamhst;
    }

    public void setNmrfamhst(Integer nmrfamhst) {
        this.nmrfamhst = nmrfamhst;
    }

    public String getImglivregh() {
        return imglivregh;
    }

    public void setImglivregh(String imglivregh) {
        this.imglivregh = imglivregh;
    }

    public String getEmailcorhs() {
        return emailcorhs;
    }

    public void setEmailcorhs(String emailcorhs) {
        this.emailcorhs = emailcorhs;
    }

    public String getFoncelcorh() {
        return foncelcorh;
    }

    public void setFoncelcorh(String foncelcorh) {
        this.foncelcorh = foncelcorh;
    }

    public String getFaxcomcorh() {
        return faxcomcorh;
    }

    public void setFaxcomcorh(String faxcomcorh) {
        this.faxcomcorh = faxcomcorh;
    }

    public String getFoncomcorh() {
        return foncomcorh;
    }

    public void setFoncomcorh(String foncomcorh) {
        this.foncomcorh = foncomcorh;
    }

    public String getFonrescorh() {
        return fonrescorh;
    }

    public void setFonrescorh(String fonrescorh) {
        this.fonrescorh = fonrescorh;
    }

    public String getQrtimvhst() {
        return qrtimvhst;
    }

    public void setQrtimvhst(String qrtimvhst) {
        this.qrtimvhst = qrtimvhst;
    }

    public String getFlgpenhst() {
        return flgpenhst;
    }

    public void setFlgpenhst(String flgpenhst) {
        this.flgpenhst = flgpenhst;
    }

    public String getPdaflag() {
        return pdaflag;
    }

    public void setPdaflag(String pdaflag) {
        this.pdaflag = pdaflag;
    }

    public Integer getRotid() {
        return rotid;
    }

    public void setRotid(Integer rotid) {
        this.rotid = rotid;
    }

    public Integer getRotusrid() {
        return rotusrid;
    }

    public void setRotusrid(Integer rotusrid) {
        this.rotusrid = rotusrid;
    }

    public String getStatusrot() {
        return statusrot;
    }

    public void setStatusrot(String statusrot) {
        this.statusrot = statusrot;
    }

    public String getProvemde() {
        return provemde;
    }

    public void setProvemde(String provemde) {
        this.provemde = provemde;
    }

    public Integer getCdgwebphst() {
        return cdgwebphst;
    }

    public void setCdgwebphst(Integer cdgwebphst) {
        this.cdgwebphst = cdgwebphst;
    }

    public Integer getCdgpespchs() {
        return cdgpespchs;
    }

    public void setCdgpespchs(Integer cdgpespchs) {
        this.cdgpespchs = cdgpespchs;
    }

    public Integer getNmrserloth() {
        return nmrserloth;
    }

    public void setNmrserloth(Integer nmrserloth) {
        this.nmrserloth = nmrserloth;
    }

    public String getIdnimvloth() {
        return idnimvloth;
    }

    public void setIdnimvloth(String idnimvloth) {
        this.idnimvloth = idnimvloth;
    }

    public String getEspolioimh() {
        return espolioimh;
    }

    public void setEspolioimh(String espolioimh) {
        this.espolioimh = espolioimh;
    }

    public Integer getNmrtitdefh() {
        return nmrtitdefh;
    }

    public void setNmrtitdefh(Integer nmrtitdefh) {
        this.nmrtitdefh = nmrtitdefh;
    }

    public Integer getAnotitdefh() {
        return anotitdefh;
    }

    public void setAnotitdefh(Integer anotitdefh) {
        this.anotitdefh = anotitdefh;
    }

    public Integer getNmrescpubh() {
        return nmrescpubh;
    }

    public void setNmrescpubh(Integer nmrescpubh) {
        this.nmrescpubh = nmrescpubh;
    }

    public Integer getAnoescpubh() {
        return anoescpubh;
    }

    public void setAnoescpubh(Integer anoescpubh) {
        this.anoescpubh = anoescpubh;
    }

    public Integer getFlhescpubh() {
        return flhescpubh;
    }

    public void setFlhescpubh(Integer flhescpubh) {
        this.flhescpubh = flhescpubh;
    }

    public Integer getCdgcarthst() {
        return cdgcarthst;
    }

    public void setCdgcarthst(Integer cdgcarthst) {
        this.cdgcarthst = cdgcarthst;
    }

    public Integer getLvrescpubh() {
        return lvrescpubh;
    }

    public void setLvrescpubh(Integer lvrescpubh) {
        this.lvrescpubh = lvrescpubh;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }
}
