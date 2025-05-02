/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

/**
 * @author juggernaut
 */
public class RelatorioBalancoOrcamentarioLRF implements Serializable {
    private VoPrevisaoAtualizadaReceita voPrevisaoAtualizadaReceita;
    private VoPrevisaoInicialReceita voPrevisaoInicialReceita;
    private VoReceitaRealizadaAteBimestreReceita voReceitaRealizadaAteBimestreReceita;
    private VoReceitaRealizadaNoBimestreReceita voReceitaRealizadaNoBimestreReceita;

    public VoPrevisaoAtualizadaReceita getVoPrevisaoAtualizadaReceita() {
        return voPrevisaoAtualizadaReceita;
    }

    public void setVoPrevisaoAtualizadaReceita(VoPrevisaoAtualizadaReceita voPrevisaoAtualizadaReceita) {
        this.voPrevisaoAtualizadaReceita = voPrevisaoAtualizadaReceita;
    }

    public VoPrevisaoInicialReceita getVoPrevisaoInicialReceita() {
        return voPrevisaoInicialReceita;
    }

    public void setVoPrevisaoInicialReceita(VoPrevisaoInicialReceita voPrevisaoInicialReceita) {
        this.voPrevisaoInicialReceita = voPrevisaoInicialReceita;
    }

    public VoReceitaRealizadaAteBimestreReceita getVoReceitaRealizadaAteBimestreReceita() {
        return voReceitaRealizadaAteBimestreReceita;
    }

    public void setVoReceitaRealizadaAteBimestreReceita(VoReceitaRealizadaAteBimestreReceita voReceitaRealizadaAteBimestreReceita) {
        this.voReceitaRealizadaAteBimestreReceita = voReceitaRealizadaAteBimestreReceita;
    }

    public VoReceitaRealizadaNoBimestreReceita getVoReceitaRealizadaNoBimestreReceita() {
        return voReceitaRealizadaNoBimestreReceita;
    }

    public void setVoReceitaRealizadaNoBimestreReceita(VoReceitaRealizadaNoBimestreReceita voReceitaRealizadaNoBimestreReceita) {
        this.voReceitaRealizadaNoBimestreReceita = voReceitaRealizadaNoBimestreReceita;
    }
}
