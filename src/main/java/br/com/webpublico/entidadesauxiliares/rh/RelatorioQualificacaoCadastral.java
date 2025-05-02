package br.com.webpublico.entidadesauxiliares.rh;

import java.util.Date;

/**
 * Created by mateus on 20/07/17.
 */
public class RelatorioQualificacaoCadastral {

    private String cpf;
    private String nis;
    private String nomeFuncionario;
    private Date dataNascimento;
    private String processadoRejeitado;
    private String observacao;

    public RelatorioQualificacaoCadastral() {
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNis() {
        return nis;
    }

    public void setNis(String nis) {
        this.nis = nis;
    }

    public String getNomeFuncionario() {
        return nomeFuncionario;
    }

    public void setNomeFuncionario(String nomeFuncionario) {
        this.nomeFuncionario = nomeFuncionario;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getProcessadoRejeitado() {
        return processadoRejeitado;
    }

    public void setProcessadoRejeitado(String processadoRejeitado) {
        this.processadoRejeitado = processadoRejeitado;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
