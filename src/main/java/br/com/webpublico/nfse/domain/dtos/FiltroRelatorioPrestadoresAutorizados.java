package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.TipoPorte;
import com.google.common.base.Strings;

public class FiltroRelatorioPrestadoresAutorizados {

    private CadastroEconomico cadastroEconomico;
    private TipoPorte porte;
    private SituacaoCadastralCadastroEconomico situacaoCadastral;
    private Pessoa pessoa;
    private String cnpjInicial;
    private String cnpjFinal;

    public FiltroRelatorioPrestadoresAutorizados() {
    }

    public String montarDescricaoFiltros() {
        StringBuilder retorno = new StringBuilder();
        if (cadastroEconomico != null) {
            retorno.append(" Cadastro Econômico: ").append(cadastroEconomico).append("; ");
        }
        if (!Strings.isNullOrEmpty(cnpjInicial)) {
            retorno.append(" CNPJ Inicial: ").append(cnpjInicial).append("; ");
        }
        if (!Strings.isNullOrEmpty(cnpjFinal)) {
            retorno.append(" CNPJ Final: ").append(cnpjFinal).append("; ");
        }
        if (pessoa != null) {
            retorno.append(" Pessoa: ").append(pessoa).append("; ");
        }
        if (porte != null) {
            retorno.append(" Porte: ").append(porte.getDescricao()).append("; ");
        }
        if (situacaoCadastral != null) {
            retorno.append(" Situação: ").append(situacaoCadastral.getDescricao()).append("; ");
        }
        return retorno.toString();
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public TipoPorte getPorte() {
        return porte;
    }

    public void setPorte(TipoPorte porte) {
        this.porte = porte;
    }

    public SituacaoCadastralCadastroEconomico getSituacaoCadastral() {
        return situacaoCadastral;
    }

    public void setSituacaoCadastral(SituacaoCadastralCadastroEconomico situacaoCadastral) {
        this.situacaoCadastral = situacaoCadastral;
    }
}
