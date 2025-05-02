package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

/**
 * Created by william on 07/11/17.
 */
public class RelatorioAtualizacaoServidoresPortal implements Serializable {
    private String matricula;
    private String nomeServidor;
    private String modalidade;

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNomeServidor() {
        return nomeServidor;
    }

    public void setNomeServidor(String nomeServidor) {
        this.nomeServidor = nomeServidor;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }
}
