package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Cadastro;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.TipoCadastroTributario;

/**
 * Created by HardRock on 02/03/2017.
 */
public class VOConsultaDebito {

    private Pessoa pessoa;
    private Cadastro cadastro;
    private TipoCadastroTributario tipoCadastroTributario;

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public boolean isContribuinteGeral() {
        return this.tipoCadastroTributario != null && TipoCadastroTributario.PESSOA.equals(this.tipoCadastroTributario);
    }

}
