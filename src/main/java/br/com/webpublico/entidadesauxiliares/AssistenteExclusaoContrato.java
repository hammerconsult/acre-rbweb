package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import com.google.common.collect.Lists;

import java.util.List;

public class AssistenteExclusaoContrato {

    private BarraProgressoItens barraProgressoItens;
    private ExclusaoContrato entity;
    private Contrato contrato;
    private ExecucaoContrato execucaoContrato;
    private AlteracaoContratual alteracaoContratual;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private List<RelacionamentoContrato> relacionamentos;

    public AssistenteExclusaoContrato() {
        relacionamentos = Lists.newArrayList();
    }

    public void iniciarBarraProgresso(Integer tamanho) {
        BarraProgressoItens barraProgressoItens = this.getBarraProgressoItens();
        barraProgressoItens.inicializa();
        barraProgressoItens.setTotal(tamanho);
    }

    public void finalizar() {
        getBarraProgressoItens().finaliza();
    }

    public BarraProgressoItens getBarraProgressoItens() {
        return barraProgressoItens;
    }

    public void setBarraProgressoItens(BarraProgressoItens barraProgressoItens) {
        this.barraProgressoItens = barraProgressoItens;
    }

    public ExclusaoContrato getEntity() {
        return entity;
    }

    public void setEntity(ExclusaoContrato entity) {
        this.entity = entity;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<RelacionamentoContrato> getRelacionamentos() {
        return relacionamentos;
    }

    public void setRelacionamentos(List<RelacionamentoContrato> relacionamentos) {
        this.relacionamentos = relacionamentos;
    }

    public ExecucaoContrato getExecucaoContrato() {
        return execucaoContrato;
    }

    public void setExecucaoContrato(ExecucaoContrato execucaoContrato) {
        this.execucaoContrato = execucaoContrato;
    }

    public AlteracaoContratual getAlteracaoContratual() {
        return alteracaoContratual;
    }

    public void setAlteracaoContratual(AlteracaoContratual alteracaoContratual) {
        this.alteracaoContratual = alteracaoContratual;
    }
}
