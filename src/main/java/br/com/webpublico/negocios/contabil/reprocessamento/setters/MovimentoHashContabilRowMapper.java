package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.contabil.conciliacaocontabil.MovimentoHashContabil;
import br.com.webpublico.enums.conciliacaocontabil.TipoMovimentoHashContabil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MovimentoHashContabilRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        MovimentoHashContabil obj = new MovimentoHashContabil();
        obj.setId(rs.getLong("ID"));
        obj.setValor(rs.getBigDecimal("VALOR"));
        obj.setData(rs.getDate("DATA"));
        obj.setHash(rs.getString("HASH"));
        obj.setTipo(rs.getString("TIPO") != null ? TipoMovimentoHashContabil.valueOf(rs.getString("TIPO")) : null);
        return obj;
    }
}
