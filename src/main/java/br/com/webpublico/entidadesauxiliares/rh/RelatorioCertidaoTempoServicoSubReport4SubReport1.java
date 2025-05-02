package br.com.webpublico.entidadesauxiliares.rh;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class RelatorioCertidaoTempoServicoSubReport4SubReport1 {

    private BigDecimal total;
    private String totalExtenso;

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getTotalExtenso() {
        return totalExtenso;
    }

    public void setTotalExtenso(String totalExtenso) {
        this.totalExtenso = totalExtenso;
    }

    public static List<RelatorioCertidaoTempoServicoSubReport4SubReport1> preencherDados(List<Object[]> objs) {
        List<RelatorioCertidaoTempoServicoSubReport4SubReport1> retorno = Lists.newArrayList();
        for (Object[] obj : objs) {
            RelatorioCertidaoTempoServicoSubReport4SubReport1 rel = new RelatorioCertidaoTempoServicoSubReport4SubReport1();
            rel.setTotal((BigDecimal) obj[0]);
            rel.setTotalExtenso((String) obj[1]);
            retorno.add(rel);
        }
        return retorno;
    }
}
