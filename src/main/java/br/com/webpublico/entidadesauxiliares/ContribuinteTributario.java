package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoCadastroTributario;

public class ContribuinteTributario {

    private Long id;
    private Long idPessoa;
    private TipoCadastroTributario tipoCadastroTributario;

    public ContribuinteTributario(Long id, Long idPessoa, TipoCadastroTributario tipoCadastroTributario) {
        this.id = id;
        this.idPessoa = idPessoa;
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContribuinteTributario that = (ContribuinteTributario) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return tipoCadastroTributario == that.tipoCadastroTributario;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (tipoCadastroTributario != null ? tipoCadastroTributario.hashCode() : 0);
        return result;
    }
}
