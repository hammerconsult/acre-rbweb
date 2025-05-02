package br.com.webpublico.negocios.tributario.rowmapper;


import br.com.webpublico.entidades.IsencaoCadastroImobiliario;
import br.com.webpublico.enums.TipoLancamentoIsencaoIPTU;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IsencaoRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        IsencaoCadastroImobiliario isencao = new IsencaoCadastroImobiliario();
        isencao.setId(rs.getLong("ID"));
        isencao.setSequencia(rs.getLong("SEQUENCIA"));
        isencao.setInicioVigencia(rs.getDate("INICIOVIGENCIA"));
        isencao.setFinalVigencia(rs.getDate("FINALVIGENCIA"));
        isencao.setTipoLancamentoIsencao(TipoLancamentoIsencaoIPTU.valueOf(rs.getString("TIPOLANCAMENTOISENCAO")));
        isencao.setLancaImposto(rs.getBoolean("LANCAIMPOSTO"));
        isencao.setLancaTaxa(rs.getBoolean("LANCATAXA"));
        isencao.setPercentual(rs.getBigDecimal("PERCENTUAL"));
        return isencao;
    }
}
