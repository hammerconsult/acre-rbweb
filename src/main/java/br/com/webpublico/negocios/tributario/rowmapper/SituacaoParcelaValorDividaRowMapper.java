package br.com.webpublico.negocios.tributario.rowmapper;

import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.entidades.SituacaoParcelaValorDivida;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 23/09/13
 * Time: 13:47
 * To change this template use File | Settings | File Templates.
 */
public class SituacaoParcelaValorDividaRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        SituacaoParcelaValorDivida spc = new SituacaoParcelaValorDivida();
        spc.setId(resultSet.getLong("ID"));
        spc.setSituacaoParcela(SituacaoParcela.valueOf(resultSet.getString("SITUACAOPARCELA")));
        spc.setDataLancamento(resultSet.getDate("DATALANCAMENTO"));
        spc.setSaldo(resultSet.getBigDecimal("SALDO"));
        return spc;
    }
}
