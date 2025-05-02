package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Cadastro;
import br.com.webpublico.enums.TipoCadastroTributario;

import java.io.Serializable;

public class FiltroRelacaoProcessoProtesto extends AbstractFiltroRelacaoLancamentoDebito implements Serializable {

    private Cadastro cadastro;
    private String numeroCDA;

    private TipoCadastroTributario tipoCadastro;

    public FiltroRelacaoProcessoProtesto() {
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public String getNumeroCDA() {
        return numeroCDA;
    }

    public void setNumeroCDA(String numeroCDA) {
        this.numeroCDA = numeroCDA;
    }

    public TipoCadastroTributario getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroTributario tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

}
