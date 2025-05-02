package br.com.webpublico.negocios.tributario.rowmapper;


import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.Lote;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CadastroImobiliarioRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        CadastroImobiliario cadastro = new CadastroImobiliario();
        cadastro.setId(rs.getLong("ID"));
        cadastro.setCodigo(rs.getString("CODIGO"));
        cadastro.setInscricaoCadastral(rs.getString("INSCRICAOCADASTRAL"));
        Lote lote = new Lote();
        lote.setId(rs.getLong("LOTE_ID"));
        cadastro.setLote(lote);
        cadastro.setAreaTotalConstruida(rs.getBigDecimal("AREATOTALCONSTRUIDA"));
        cadastro.setAtivo(rs.getBoolean("ATIVO"));
        cadastro.setIdMaiorProprietario(rs.getLong("PESSOA_ID"));
        cadastro.setAutonoma(rs.getLong("AUTONOMA"));
        return cadastro;
    }
}
