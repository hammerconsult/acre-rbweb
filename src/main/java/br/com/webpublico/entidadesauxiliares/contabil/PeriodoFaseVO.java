package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.PeriodoFase;

/**
 * Created by carlos on 16/05/17.
 */
public class PeriodoFaseVO implements Comparable<PeriodoFaseVO> {
    private PeriodoFase periodoFase;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public PeriodoFaseVO() {
    }

    public PeriodoFase getPeriodoFase() {
        return periodoFase;
    }

    public void setPeriodoFase(PeriodoFase periodoFase) {
        this.periodoFase = periodoFase;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }


    @Override
    public int compareTo(PeriodoFaseVO o) {
        if (this.getHierarquiaOrganizacional() != null && o.getHierarquiaOrganizacional() != null) {
            return this.getHierarquiaOrganizacional().getCodigo().compareTo(o.getHierarquiaOrganizacional().getCodigo());
        }
        return 0;
    }
}
