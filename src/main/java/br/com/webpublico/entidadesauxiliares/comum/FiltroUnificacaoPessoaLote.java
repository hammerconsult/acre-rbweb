package br.com.webpublico.entidadesauxiliares.comum;

import br.com.webpublico.enums.TipoPessoa;

import java.io.Serializable;
import java.util.Date;

public class FiltroUnificacaoPessoaLote implements Serializable {

    private TipoPessoa tipoPessoa;
    private String cpfCnpj;
    private Date dataCadastro;

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoa tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
