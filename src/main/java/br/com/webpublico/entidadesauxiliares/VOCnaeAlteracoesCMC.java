package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.EconomicoCNAE;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import com.google.common.base.Objects;

import java.io.Serializable;

public class VOCnaeAlteracoesCMC implements Serializable {
    private final String codigoCnae;
    private final GrauDeRiscoDTO grauDeRisco;
    private final EconomicoCNAE.TipoCnae tipoCnae;

    public VOCnaeAlteracoesCMC(String codigoCnae, GrauDeRiscoDTO grauDeRisco, EconomicoCNAE.TipoCnae tipoCnae) {
        this.codigoCnae = codigoCnae;
        this.grauDeRisco = grauDeRisco;
        this.tipoCnae = tipoCnae;
    }

    public String getCodigoCnae() {
        return codigoCnae;
    }

    public GrauDeRiscoDTO getGrauDeRisco() {
        return grauDeRisco;
    }

    public EconomicoCNAE.TipoCnae getTipoCnae() {
        return tipoCnae;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VOCnaeAlteracoesCMC that = (VOCnaeAlteracoesCMC) o;
        return Objects.equal(codigoCnae, that.codigoCnae) && grauDeRisco == that.grauDeRisco && tipoCnae == that.tipoCnae;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigoCnae, grauDeRisco, tipoCnae);
    }
}
