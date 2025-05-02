package br.com.webpublico.entidadesauxiliares.rh.esocial;

import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.interfaces.IHistoricoEsocial;

import java.util.Date;

public class ReativacaoBeneficio implements IHistoricoEsocial {
    private Long id;
    private VinculoFP vinculoFP;
    private String nrBeneficio;
    private Date dtCessaoBeneficio;
    private Date dtEfetReativ;
    private Date dtEfeito;

    public ReativacaoBeneficio(Long id, VinculoFP vinculoFP, String nrBeneficio, Date dtCessaoBeneficio, Date dtEfetReativ, Date dtEfeito) {
        this.id = id;
        this.vinculoFP = vinculoFP;
        this.nrBeneficio = nrBeneficio;
        this.dtCessaoBeneficio = dtCessaoBeneficio;
        this.dtEfetReativ = dtEfetReativ;
        this.dtEfeito = dtEfeito;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public String getNrBeneficio() {
        return nrBeneficio;
    }

    public void setNrBeneficio(String nrBeneficio) {
        this.nrBeneficio = nrBeneficio;
    }

    public Date getDtCessaoBeneficio() {
        return dtCessaoBeneficio;
    }

    public void setDtCessaoBeneficio(Date dtCessaoBeneficio) {
        this.dtCessaoBeneficio = dtCessaoBeneficio;
    }

    public Date getDtEfetReativ() {
        return dtEfetReativ;
    }

    public void setDtEfetReativ(Date dtEfetReativ) {
        this.dtEfetReativ = dtEfetReativ;
    }

    public Date getDtEfeito() {
        return dtEfeito;
    }

    public void setDtEfeito(Date dtEfeito) {
        this.dtEfeito = dtEfeito;
    }

    @Override
    public String getDescricaoCompleta() {
        return vinculoFP.toString();
    }

    @Override
    public String getIdentificador() {
        return this.id.toString();
    }
}
