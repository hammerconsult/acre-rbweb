package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.nfse.domain.SituacaoAidfe;

public class FiltroSolicitacaoEmissaoNota {

    private CadastroEconomico cadastroEconomico;
    private SituacaoAidfe situacao;

    public FiltroSolicitacaoEmissaoNota() {
    }

    public String montarDescricaoFiltros() {
        StringBuilder retorno = new StringBuilder();
        if (cadastroEconomico != null) {
            retorno.append(" Cadastro Econômico: ").append(cadastroEconomico).append("; ");
        }
        if (situacao != null) {
            retorno.append(" Situação: ").append(situacao.getDescricao()).append("; ");
        }
        return retorno.toString();
    }

    public String montarClausulaWhere() {
        StringBuilder sql = new StringBuilder();
        String clausula = " where ";
        if (cadastroEconomico != null) {
            sql.append(clausula).append(" cad.id = ").append(cadastroEconomico.getId());
            clausula = " and ";
        }
        if (situacao != null) {
            sql.append(clausula).append(" aidfe.situacao = '").append(situacao.name()).append("'");
        }
        return sql.toString();
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public SituacaoAidfe getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoAidfe situacao) {
        this.situacao = situacao;
    }
}
