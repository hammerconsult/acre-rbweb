package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.EnderecoAlvara;
import br.com.webpublico.entidades.EnderecoCadastroEconomico;
import br.com.webpublico.entidades.EnderecoCalculoAlvara;
import br.com.webpublico.entidades.EnderecoCorreio;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class VOConfirmaTaxaDeLocalizacao implements Serializable {
    private Boolean hasAlteracaoEndereco;
    private EnderecoAlvara enderecoAlvara;
    private EnderecoCadastroEconomico enderecoCadastroEconomico;
    private Boolean hasAlteracaoCnae;
    private List<VOCnae> cnaesDiferentes;
    private List<VOCnae> cnaesComMesmoCodigo;

    public VOConfirmaTaxaDeLocalizacao() {
        this.hasAlteracaoEndereco = Boolean.FALSE;
        this.hasAlteracaoCnae = Boolean.FALSE;
        this.cnaesDiferentes = Lists.newArrayList();
        this.cnaesComMesmoCodigo = Lists.newArrayList();
    }

    public Boolean getHasAlteracaoEndereco() {
        return hasAlteracaoEndereco;
    }

    public void setHasAlteracaoEndereco(Boolean hasAlteracaoEndereco) {
        this.hasAlteracaoEndereco = hasAlteracaoEndereco;
    }

    public EnderecoAlvara getEnderecoAlvara() {
        return enderecoAlvara;
    }

    public void setEnderecoAlvara(EnderecoAlvara enderecoAlvara) {
        this.enderecoAlvara = enderecoAlvara;
    }

    public EnderecoCadastroEconomico getEnderecoCadastroEconomico() {
        return enderecoCadastroEconomico;
    }

    public void setEnderecoCadastroEconomico(EnderecoCadastroEconomico enderecoCadastroEconomico) {
        this.enderecoCadastroEconomico = enderecoCadastroEconomico;
    }

    public Boolean getHasAlteracaoCnae() {
        return hasAlteracaoCnae;
    }

    public void setHasAlteracaoCnae(Boolean hasAlteracaoCnae) {
        this.hasAlteracaoCnae = hasAlteracaoCnae;
    }

    public List<VOCnae> getCnaesDiferentes() {
        return cnaesDiferentes;
    }

    public void setCnaesDiferentes(List<VOCnae> cnaesDiferentes) {
        this.cnaesDiferentes = cnaesDiferentes;
    }

    public List<VOCnae> getCnaesComMesmoCodigo() {
        return cnaesComMesmoCodigo;
    }

    public void setCnaesComMesmoCodigo(List<VOCnae> cnaesComMesmoCodigo) {
        this.cnaesComMesmoCodigo = cnaesComMesmoCodigo;
    }

    public boolean hasAlteracao() {
        return this.hasAlteracaoEndereco || this.hasAlteracaoCnae;
    }
}
