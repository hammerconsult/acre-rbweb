package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoBalancete;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 20/07/17
 * Time: 17:38
 * To change this template use File | Settings | File Templates.
 */
public class DeleteLancamentoContabilSetter implements PreparedStatementSetter {


    private Date dataInicial;
    private Date dataFinal;
    private TipoBalancete tipoBalancete;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public DeleteLancamentoContabilSetter(Date dataInicio, Date dataFim, TipoBalancete mensal, HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.dataInicial = dataInicio;
        this.dataFinal = dataFim;
        this.tipoBalancete = mensal;
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        ps.setDate(1, new java.sql.Date(dataInicial.getTime()));
        ps.setDate(2, new java.sql.Date(dataFinal.getTime()));
        ps.setString(3, tipoBalancete.name());
        if (hierarquiaOrganizacional != null) {
            ps.setLong(4, hierarquiaOrganizacional.getSubordinada().getId());
        }
    }

    public String getSql() {
        return " delete LANCAMENTOCONTABIL where id in ( " +
            " SELECT l.id FROM LANCAMENTOCONTABIL L " +
            " INNER JOIN ITEMPARAMETROEVENTO IT ON L.ITEMPARAMETROEVENTO_ID = IT.ID " +
            " INNER JOIN PARAMETROEVENTO P ON IT.PARAMETROEVENTO_ID = P.ID " +
            " inner join EVENTOCONTABIL e on P.EVENTOCONTABIL_ID = e.id " +
            " WHERE trunc(L.DATALANCAMENTO) BETWEEN ? AND ?" +
            " and e.tipobalancete = ? " + (hierarquiaOrganizacional != null ? " and l.unidadeorganizacional_id = ? " : "") +
            " )";
    }
}
