package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class ConsultaLocalEstoqueOrcamentario implements Comparable<ConsultaLocalEstoqueOrcamentario> {

    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private List<ConsultaLocalEstoqueMaterial> materiais;

    public ConsultaLocalEstoqueOrcamentario(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
        materiais = Lists.newArrayList();
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<ConsultaLocalEstoqueMaterial> getMateriais() {
        return materiais;
    }

    public void setMateriais(List<ConsultaLocalEstoqueMaterial> materiais) {
        this.materiais = materiais;
    }

    public BigDecimal getValorTotalMateriais() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (ConsultaLocalEstoqueMaterial mp : this.materiais) {
            total = total.add(mp.getValorEstoque());
        }
        return total;
    }

    @Override
    public int compareTo(ConsultaLocalEstoqueOrcamentario o) {
        if (hierarquiaOrganizacional != null && o.getHierarquiaOrganizacional() != null) {
            return hierarquiaOrganizacional.getCodigo().compareTo(o.getHierarquiaOrganizacional().getCodigo());
        }
        return 0;
    }
}
