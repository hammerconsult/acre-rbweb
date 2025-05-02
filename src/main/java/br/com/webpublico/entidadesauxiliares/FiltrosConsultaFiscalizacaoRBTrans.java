package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.FiscalizacaoRBTrans;
import br.com.webpublico.enums.OrgaoAutuadorRBTrans;
import br.com.webpublico.enums.StatusFiscalizacaoRBTrans;

import java.util.Date;

/**
 * @author André Gustavo
 */
public class FiltrosConsultaFiscalizacaoRBTrans {

    private Long codigoAutuacao;
    private StatusFiscalizacaoRBTrans situacaoFiscalizacao;
    private Date dataAutuacao;
    private OrgaoAutuadorRBTrans orgaoAutuadorRBTrans;
    private CadastroEconomico cadastroEconomicoPermissionario;
    private Integer quantidadeMaximaRegistros;

    public FiltrosConsultaFiscalizacaoRBTrans() {
        codigoAutuacao = null;
        //System.out.println("CODIGO ---- " + codigoAutuacao);
        orgaoAutuadorRBTrans = null;
        dataAutuacao = null;
        situacaoFiscalizacao = null;
        cadastroEconomicoPermissionario = null;
        quantidadeMaximaRegistros = null;
    }

    public Integer getQuantidadeMaximaRegistros() {
        return quantidadeMaximaRegistros;
    }

    public void setQuantidadeMaximaRegistros(Integer quantidadeMaximaRegistros) {
        this.quantidadeMaximaRegistros = quantidadeMaximaRegistros;
    }

    public Long getCodigoAutuacao() {
        return codigoAutuacao;
    }

    public void setCodigoAutuacao(Long codigoAutuacao) {
        this.codigoAutuacao = codigoAutuacao;
    }

    public StatusFiscalizacaoRBTrans getSituacaoFiscalizacao() {
        return situacaoFiscalizacao;
    }

    public void setSituacaoFiscalizacao(StatusFiscalizacaoRBTrans situacaoFiscalizacao) {
        this.situacaoFiscalizacao = situacaoFiscalizacao;
    }

    public Date getDataAutuacao() {
        return dataAutuacao;
    }

    public void setDataAutuacao(Date dataAutuacao) {
        this.dataAutuacao = dataAutuacao;
    }

    public OrgaoAutuadorRBTrans getOrgaoAutuadorRBTrans() {
        return orgaoAutuadorRBTrans;
    }

    public void setOrgaoAutuadorRBTrans(OrgaoAutuadorRBTrans orgaoAutuadorRBTrans) {
        this.orgaoAutuadorRBTrans = orgaoAutuadorRBTrans;
    }

    public CadastroEconomico getCadastroEconomicoPermissionario() {
        return cadastroEconomicoPermissionario;
    }

    public void setCadastroEconomicoPermissionario(CadastroEconomico cadastroEconomicoPermissionario) {
        this.cadastroEconomicoPermissionario = cadastroEconomicoPermissionario;
    }

    private String adicionarClausulaWhere(String where, String clausula) {
        if (where.length() > 5) {
            return where + " and " + clausula;
        }

        return where + " " + clausula;
    }

    public String obtemQueryConsultaFiscalizacaoRBTrans() {
        String hql = "select distinct fiscalizacao from " + FiscalizacaoRBTrans.class.getSimpleName() + " fiscalizacao"
                + " inner join fiscalizacao.autuacaoFiscalizacao autuacao"
                + " inner join fiscalizacao.situacoesFiscalizacao situacaoFiscalizacao"
                + " inner join autuacao.cadastroEconomico permissionario ";

        String where = "where";

        if (codigoAutuacao != null) {
            where = adicionarClausulaWhere(where, "autuacao.codigo = :codigo");
        }
        if (dataAutuacao != null) {
            where = adicionarClausulaWhere(where, "autuacao.dataAutuacao = :dataAutuacao");
        }

        if (orgaoAutuadorRBTrans != null) {
            where = adicionarClausulaWhere(where, "fiscalizacao.orgaoAutuador = :orgaoAutuador");
        }

        if (situacaoFiscalizacao != null) {
            where = adicionarClausulaWhere(where, "situacaoFiscalizacao.statusFiscalizacao = :statusFiscalizacao  and situacaoFiscalizacao.finalizadoEM is null");
        }
        if (cadastroEconomicoPermissionario != null) {
            where = adicionarClausulaWhere(where, "permissionario = :permissionario");
        }

        if (where.length() > 5) {
            return hql + where;
        } else {
            return hql;
        }

    }
}
