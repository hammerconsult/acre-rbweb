package br.com.webpublico.entidadesauxiliares.rh.relatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class RelatorioAcompanhamentoAtualizacaoCadastral implements Serializable {
    private Integer ano;
    private String situacao;
    private Date inicio;
    private Date fim;
    private String orgao;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    private List<RelatorioAcompanhamentoAtualizacaoCadastralServidores> servidores;
    private List<RelatorioAcompanhamentoAtualizacaoCadastralEstatistica> estatisticas;
    private List<RelatorioAcompanhamentoAtualizacaoCadastralDadosGerais> dadosGerais;

    public RelatorioAcompanhamentoAtualizacaoCadastral() {
        servidores = Lists.newArrayList();
        estatisticas = Lists.newArrayList();
        dadosGerais = Lists.newArrayList();
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public List<RelatorioAcompanhamentoAtualizacaoCadastralServidores> getServidores() {
        return servidores;
    }

    public void setServidores(List<RelatorioAcompanhamentoAtualizacaoCadastralServidores> servidores) {
        this.servidores = servidores;
    }

    public List<RelatorioAcompanhamentoAtualizacaoCadastralEstatistica> getEstatisticas() {
        return estatisticas;
    }

    public void setEstatisticas(List<RelatorioAcompanhamentoAtualizacaoCadastralEstatistica> estatisticas) {
        this.estatisticas = estatisticas;
    }

    public List<RelatorioAcompanhamentoAtualizacaoCadastralDadosGerais> getDadosGerais() {
        return dadosGerais;
    }

    public void setDadosGerais(List<RelatorioAcompanhamentoAtualizacaoCadastralDadosGerais> dadosGerais) {
        this.dadosGerais = dadosGerais;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
