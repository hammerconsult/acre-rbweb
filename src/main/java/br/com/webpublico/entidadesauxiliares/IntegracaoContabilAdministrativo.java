package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.entidades.SolicitacaoEmpenho;

public class IntegracaoContabilAdministrativo {
    private Empenho empenho;
    private SolicitacaoEmpenho solicitacaoEmpenho;
    private String execucaoSolicitacaoEmpenho;
    private String linkRedirecionarExecucaoSolicitacaoEmp;

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public SolicitacaoEmpenho getSolicitacaoEmpenho() {
        return solicitacaoEmpenho;
    }

    public void setSolicitacaoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho) {
        this.solicitacaoEmpenho = solicitacaoEmpenho;
    }

    public String getExecucaoSolicitacaoEmpenho() {
        return execucaoSolicitacaoEmpenho;
    }

    public void setExecucaoSolicitacaoEmpenho(String execucaoSolicitacaoEmpenho) {
        this.execucaoSolicitacaoEmpenho = execucaoSolicitacaoEmpenho;
    }

    public String getLinkRedirecionarExecucaoSolicitacaoEmp() {
        return linkRedirecionarExecucaoSolicitacaoEmp;
    }

    public void setLinkRedirecionarExecucaoSolicitacaoEmp(String linkRedirecionarExecucaoSolicitacaoEmp) {
        this.linkRedirecionarExecucaoSolicitacaoEmp = linkRedirecionarExecucaoSolicitacaoEmp;
    }
}
