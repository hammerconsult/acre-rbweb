package br.com.webpublico.entidadesauxiliares.rh;

import com.google.common.collect.Lists;

import java.util.List;

public class RelatorioCertidaoTempoServico {

    private String nomePessoa;
    private String cargoDescricao;
    private String unidadeDescricao;
    private String matricula;
    private String numero;
    private String letra;
    private String periodo;
    private String textoAverbacaoDataContratoTemporario;
    private Boolean averbacaoDataContratoTemporario;
    private boolean detalhado;
    private List<RelatorioCertidaoTempoServicoSubReport1> subreport1;
    private List<RelatorioCertidaoTempoServicoSubReport2> subreport2;
    private List<RelatorioCertidaoTempoServicoSubReport3> subreport3;
    private List<RelatorioCertidaoTempoServicoSubReport4> subreport4;
    private List<RelatorioCertidaoTempoServicoSubReport5> subreport5;
    private List<RelatorioCertidaoTempoServicoSubReport6> subreport6;

    public RelatorioCertidaoTempoServico() {
        subreport1 = Lists.newArrayList();
        subreport2 = Lists.newArrayList();
        subreport3 = Lists.newArrayList();
        subreport4 = Lists.newArrayList();
        subreport5 = Lists.newArrayList();
    }


    public static List<RelatorioCertidaoTempoServico> preencherDados(List<Object[]> objs,
                                                                     List<RelatorioCertidaoTempoServicoSubReport1> subreport1,
                                                                     List<RelatorioCertidaoTempoServicoSubReport2> subreport2,
                                                                     List<RelatorioCertidaoTempoServicoSubReport3> subreport3,
                                                                     List<RelatorioCertidaoTempoServicoSubReport4> subreport4,
                                                                     List<RelatorioCertidaoTempoServicoSubReport5> subreport5,
                                                                     List<RelatorioCertidaoTempoServicoSubReport6> subreport6,
                                                                     boolean detalhado,
                                                                     Boolean averbacaoDataContratoTemporario,
                                                                     String textoAverbacaoDataContratoTemporario) {
        List<RelatorioCertidaoTempoServico> retorno = Lists.newArrayList();
        for (Object[] obj : objs) {
            RelatorioCertidaoTempoServico rel = new RelatorioCertidaoTempoServico();
            rel.setNomePessoa((String) obj[0]);
            rel.setCargoDescricao((String) obj[1]);
            rel.setUnidadeDescricao((String) obj[2]);
            rel.setMatricula((String) obj[3]);
            rel.setNumero((String) obj[4]);
            rel.setLetra((String) obj[5]);
            rel.setPeriodo((String) obj[6]);
            rel.setDetalhado(detalhado);
            rel.setAverbacaoDataContratoTemporario(averbacaoDataContratoTemporario);
            rel.setTextoAverbacaoDataContratoTemporario(textoAverbacaoDataContratoTemporario);
            rel.setSubreport1(subreport1);
            rel.setSubreport2(subreport2);
            rel.setSubreport3(subreport3);
            rel.setSubreport4(subreport4);
            rel.setSubreport5(subreport5);
            rel.setSubreport6(subreport6);
            retorno.add(rel);
        }
        return retorno;
    }

    public List<RelatorioCertidaoTempoServicoSubReport6> getSubreport6() {
        return subreport6;
    }

    public void setSubreport6(List<RelatorioCertidaoTempoServicoSubReport6> subreport6) {
        this.subreport6 = subreport6;
    }

    public boolean isDetalhado() {
        return detalhado;
    }

    public void setDetalhado(boolean detalhado) {
        this.detalhado = detalhado;
    }

    public String getNomePessoa() {
        return nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public String getCargoDescricao() {
        return cargoDescricao;
    }

    public void setCargoDescricao(String cargoDescricao) {
        this.cargoDescricao = cargoDescricao;
    }

    public String getUnidadeDescricao() {
        return unidadeDescricao;
    }

    public void setUnidadeDescricao(String unidadeDescricao) {
        this.unidadeDescricao = unidadeDescricao;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public Boolean getAverbacaoDataContratoTemporario() {
        return averbacaoDataContratoTemporario != null ? averbacaoDataContratoTemporario : false;
    }

    public void setAverbacaoDataContratoTemporario(Boolean averbacaoDataContratoTemporario) {
        this.averbacaoDataContratoTemporario = averbacaoDataContratoTemporario;
    }

    public String getTextoAverbacaoDataContratoTemporario() {
        return textoAverbacaoDataContratoTemporario;
    }

    public void setTextoAverbacaoDataContratoTemporario(String textoAverbacaoDataContratoTemporario) {
        this.textoAverbacaoDataContratoTemporario = textoAverbacaoDataContratoTemporario;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public List<RelatorioCertidaoTempoServicoSubReport1> getSubreport1() {
        return subreport1;
    }

    public void setSubreport1(List<RelatorioCertidaoTempoServicoSubReport1> subreport1) {
        this.subreport1 = subreport1;
    }

    public List<RelatorioCertidaoTempoServicoSubReport2> getSubreport2() {
        return subreport2;
    }

    public void setSubreport2(List<RelatorioCertidaoTempoServicoSubReport2> subreport2) {
        this.subreport2 = subreport2;
    }

    public List<RelatorioCertidaoTempoServicoSubReport3> getSubreport3() {
        return subreport3;
    }

    public void setSubreport3(List<RelatorioCertidaoTempoServicoSubReport3> subreport3) {
        this.subreport3 = subreport3;
    }

    public List<RelatorioCertidaoTempoServicoSubReport4> getSubreport4() {
        return subreport4;
    }

    public void setSubreport4(List<RelatorioCertidaoTempoServicoSubReport4> subreport4) {
        this.subreport4 = subreport4;
    }

    public List<RelatorioCertidaoTempoServicoSubReport5> getSubreport5() {
        return subreport5;
    }

    public void setSubreport5(List<RelatorioCertidaoTempoServicoSubReport5> subreport5) {
        this.subreport5 = subreport5;
    }
}
