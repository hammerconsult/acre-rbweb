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
 * Date: 23/10/14
 * Time: 17:38
 * To change this template use File | Settings | File Templates.
 */
public class DeleteSaldoContaContabilSetter implements PreparedStatementSetter {

    private Date dataInicial;
    private Date dataFinal;
    private TipoBalancete tipoBalancete;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public DeleteSaldoContaContabilSetter(java.util.Date dataInicio, java.util.Date dataFim, TipoBalancete tipoBalancete, HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.dataInicial = dataInicio;
        this.dataFinal = dataFim;
        this.tipoBalancete = tipoBalancete;
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
        return "delete SALDOCONTACONTABIL " +
            " WHERE trunc(DATASALDO) BETWEEN ? AND ?" +
            " and tipobalancete = ? " + (hierarquiaOrganizacional != null ? " and unidadeorganizacional_id = ? " : "");
    }
}
