package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;

public class FiltroContratoRequisicaoCompra {
    HierarquiaOrganizacional hierarquiaOrganizacional;
    Exercicio exercicio;
    Pessoa gestor;
    Pessoa fiscal;
    Licitacao licitacao;
    Contrato contrato;
    ObjetoCompra objetoCompra;

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Pessoa getGestor() {
        return gestor;
    }

    public void setGestor(Pessoa gestor) {
        this.gestor = gestor;
    }

    public Pessoa getFiscal() {
        return fiscal;
    }

    public void setFiscal(Pessoa fiscal) {
        this.fiscal = fiscal;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }
}
