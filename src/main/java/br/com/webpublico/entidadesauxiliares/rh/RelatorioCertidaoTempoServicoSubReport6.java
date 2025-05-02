package br.com.webpublico.entidadesauxiliares.rh;

import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class RelatorioCertidaoTempoServicoSubReport6 {

    private Date inicio;
    private Date termino;
    private String descricao;
    private Integer quantidadeDias;

    public static List<RelatorioCertidaoTempoServicoSubReport6> preencherDados(List<Object[]> objs) {
        List<RelatorioCertidaoTempoServicoSubReport6> retorno = Lists.newArrayList();
        for (Object[] obj : objs) {
            RelatorioCertidaoTempoServicoSubReport6 rel = new RelatorioCertidaoTempoServicoSubReport6();
            rel.setInicio((Date) obj[0]);
            rel.setTermino((Date) obj[1]);
            rel.setDescricao((String) obj[2]);
            rel.setQuantidadeDias(Integer.parseInt(obj[3].toString()));
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

    public Date getTermino() {
        return termino;
    }

    public void setTermino(Date termino) {
        this.termino = termino;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQuantidadeDias() {
        return quantidadeDias;
    }

    public void setQuantidadeDias(Integer quantidadeDias) {
        this.quantidadeDias = quantidadeDias;
    }
}
