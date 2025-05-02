package br.com.webpublico.entidadesauxiliares.rh;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RelatorioCertidaoTempoServicoSubReport4 implements Comparable {

    private BigDecimal ano;
    private BigDecimal tempoAverbado;
    private List<RelatorioCertidaoTempoServicoSubReport4SubReport1> totalPorExtenso;

    public RelatorioCertidaoTempoServicoSubReport4() {
        totalPorExtenso = Lists.newArrayList();
    }

    public static List<RelatorioCertidaoTempoServicoSubReport4> preencherDados(List<Object[]> objs, List<RelatorioCertidaoTempoServicoSubReport4SubReport1> totalPorExteso, Map<Integer, Set<Date>> anoDias) {
        List<RelatorioCertidaoTempoServicoSubReport4> retorno = Lists.newArrayList();
        for (Map.Entry<Integer, Set<Date>> param : anoDias.entrySet()) {
            RelatorioCertidaoTempoServicoSubReport4 rel = new RelatorioCertidaoTempoServicoSubReport4();
            rel.setAno(new BigDecimal(param.getKey()));
            rel.setTempoAverbado(new BigDecimal(param.getValue().size()));
            rel.setTotalPorExtenso(totalPorExteso);
            retorno.add(rel);
        }
        return retorno;
    }

    public BigDecimal getAno() {
        return ano;
    }

    public void setAno(BigDecimal ano) {
        this.ano = ano;
    }

    public BigDecimal getTempoAverbado() {
        return tempoAverbado;
    }

    public void setTempoAverbado(BigDecimal tempoAverbado) {
        this.tempoAverbado = tempoAverbado;
    }

    public List<RelatorioCertidaoTempoServicoSubReport4SubReport1> getTotalPorExtenso() {
        return totalPorExtenso;
    }

    public void setTotalPorExtenso(List<RelatorioCertidaoTempoServicoSubReport4SubReport1> totalPorExtenso) {
        this.totalPorExtenso = totalPorExtenso;
    }

    @Override
    public int compareTo(Object o) {
        return this.getAno().compareTo(((RelatorioCertidaoTempoServicoSubReport4) o).getAno());
    }
}
