package br.com.webpublico.entidadesauxiliares.rh;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RelatorioCertidaoTempoServicoSubReport3 implements Comparable<RelatorioCertidaoTempoServicoSubReport3> {

    private Date inicio;
    private String orgaoEmpresa;
    private String periodo;
    private BigDecimal total;
    private Date fim;

    public static List<RelatorioCertidaoTempoServicoSubReport3> preencherDados(List<Object[]> objs) {
        List<RelatorioCertidaoTempoServicoSubReport3> retorno = Lists.newArrayList();
        for (Object[] obj : objs) {
            RelatorioCertidaoTempoServicoSubReport3 rel = new RelatorioCertidaoTempoServicoSubReport3();
            rel.setInicio((Date) obj[0]);
            rel.setOrgaoEmpresa((String) obj[1]);
            rel.setPeriodo((String) obj[2]);
            rel.setTotal((BigDecimal) obj[3]);
            rel.setFim((Date) obj[4]);
            retorno.add(rel);
        }
        return retorno;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public String getOrgaoEmpresa() {
        return orgaoEmpresa;
    }

    public void setOrgaoEmpresa(String orgaoEmpresa) {
        this.orgaoEmpresa = orgaoEmpresa;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public DateTime getInicioJoda() {
        if (inicio != null) {
            return new DateTime(inicio);
        }
        return null;
    }

    public DateTime getFimJoda() {
        if (fim != null) {
            return new DateTime(fim);
        }
        return null;
    }

    @Override
    public int compareTo(RelatorioCertidaoTempoServicoSubReport3 o) {
        return getInicio().compareTo(o.getInicio());
    }
}
