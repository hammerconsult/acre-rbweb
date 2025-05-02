package br.com.webpublico.entidadesauxiliares.softplan.dto;

import br.com.webpublico.entidadesauxiliares.softplan.enums.NaturezaCDASoftPlan;
import com.google.common.collect.Lists;

import java.util.List;

public class PessoaCDASoftPlanDTO {

    private String nuTipoPessoa;
    private String nmPessoa;
    private NaturezaCDASoftPlan deNaturezaPessoa;
    private String nuCPFCNPJ;
    private String nuCNAE;
    private String nuRGIE;
    private String deOrgaoExpedidor;
    private List<EnderecoCDASoftPlanDTO> listaDeEnderecos;
    private List<TelefoneCDASoftPlanDTO> listaDeTelefones;
    private String seq;

    public PessoaCDASoftPlanDTO() {
        listaDeEnderecos = Lists.newArrayList();
        listaDeTelefones = Lists.newArrayList();
    }

    public String getNuTipoPessoa() {
        return nuTipoPessoa;
    }

    public void setNuTipoPessoa(String nuTipoPessoa) {
        this.nuTipoPessoa = nuTipoPessoa;
    }

    public String getNmPessoa() {
        return nmPessoa;
    }

    public void setNmPessoa(String nmPessoa) {
        this.nmPessoa = nmPessoa;
    }

    public NaturezaCDASoftPlan getDeNaturezaPessoa() {
        return deNaturezaPessoa;
    }

    public void setDeNaturezaPessoa(NaturezaCDASoftPlan deNaturezaPessoa) {
        this.deNaturezaPessoa = deNaturezaPessoa;
    }

    public String getNuCPFCNPJ() {
        return nuCPFCNPJ;
    }

    public void setNuCPFCNPJ(String nuCPFCNPJ) {
        this.nuCPFCNPJ = nuCPFCNPJ;
    }

    public String getNuCNAE() {
        return nuCNAE;
    }

    public void setNuCNAE(String nuCNAE) {
        this.nuCNAE = nuCNAE;
    }

    public String getNuRGIE() {
        return nuRGIE;
    }

    public void setNuRGIE(String nuRGIE) {
        this.nuRGIE = nuRGIE;
    }

    public String getDeOrgaoExpedidor() {
        return deOrgaoExpedidor;
    }

    public void setDeOrgaoExpedidor(String deOrgaoExpedidor) {
        this.deOrgaoExpedidor = deOrgaoExpedidor;
    }

    public List<EnderecoCDASoftPlanDTO> getListaDeEnderecos() {
        return listaDeEnderecos;
    }

    public void setListaDeEnderecos(List<EnderecoCDASoftPlanDTO> listaDeEnderecos) {
        this.listaDeEnderecos = listaDeEnderecos;
    }

    public List<TelefoneCDASoftPlanDTO> getListaDeTelefones() {
        return listaDeTelefones;
    }

    public void setListaDeTelefones(List<TelefoneCDASoftPlanDTO> listaDeTelefones) {
        this.listaDeTelefones = listaDeTelefones;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }
}
