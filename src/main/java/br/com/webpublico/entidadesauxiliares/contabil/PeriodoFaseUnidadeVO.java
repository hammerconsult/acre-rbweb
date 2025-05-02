package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.PeriodoFaseUnidade;

/**
 * Created by carlos on 15/05/17.
 */
public class PeriodoFaseUnidadeVO implements Comparable<PeriodoFaseUnidadeVO> {
    private PeriodoFaseUnidade periodoFaseUnidade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public PeriodoFaseUnidadeVO() {
    }

    public PeriodoFaseUnidade getPeriodoFaseUnidade() {
        return periodoFaseUnidade;
    }

    public void setPeriodoFaseUnidade(PeriodoFaseUnidade periodoFaseUnidade) {
        this.periodoFaseUnidade = periodoFaseUnidade;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    @Override
    public int compareTo(PeriodoFaseUnidadeVO o) {
        if (this.getHierarquiaOrganizacional() != null && o.getHierarquiaOrganizacional() != null) {
            return this.getHierarquiaOrganizacional().getCodigo().compareTo(o.getHierarquiaOrganizacional().getCodigo());
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getHierarquiaOrganizacional().equals(((PeriodoFaseUnidadeVO) obj).getHierarquiaOrganizacional());
    }
}
