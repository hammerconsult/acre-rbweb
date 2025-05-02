package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.ContaContabil;
import br.com.webpublico.entidades.SaldoContaContabil;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoBalancete;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SaldoContaContabilRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        SaldoContaContabil saldoContaContabil = new SaldoContaContabil();
        saldoContaContabil.setId(rs.getLong("ID"));
        saldoContaContabil.setDataSaldo(rs.getDate("DATASALDO"));
        ContaContabil contaContabil = new ContaContabil();
        contaContabil.setId(rs.getLong("CONTACONTABIL_ID"));
        saldoContaContabil.setContaContabil(contaContabil);
        saldoContaContabil.setTipoBalancete(TipoBalancete.valueOf(rs.getString("TIPOBALANCETE")));
        UnidadeOrganizacional unidadeOrganizacional = new UnidadeOrganizacional();
        unidadeOrganizacional.setId(rs.getLong("UNIDADEORGANIZACIONAL_ID"));
        saldoContaContabil.setUnidadeOrganizacional(unidadeOrganizacional);
        saldoContaContabil.setTotalCredito(rs.getBigDecimal("TOTALCREDITO"));
        saldoContaContabil.setTotalDebito(rs.getBigDecimal("TOTALDEBITO"));
        return saldoContaContabil;
    }
}
