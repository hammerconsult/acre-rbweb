package br.com.webpublico.negocios.tributario.rowmapper;


import br.com.webpublico.entidades.Divida;
import br.com.webpublico.enums.TipoCadastroTributario;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DividaRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        Divida divida = new Divida();
        divida.setId(rs.getLong("ID"));
        divida.setNrLivroDividaAtiva(rs.getString("NRLIVRODIVIDAATIVA"));
        divida.setDescricao(rs.getString("DESCRICAO"));
        divida.setIsDividaAtiva(rs.getBoolean("ISDIVIDAATIVA"));
        divida.setIsParcelamento(rs.getBoolean("ISPARCELAMENTO"));
        divida.setCodigo(rs.getInt("CODIGO"));
        divida.setOrdemApresentacao(rs.getInt("ORDEMAPRESENTACAO"));
        divida.setTipoCadastro(TipoCadastroTributario.valueOf(rs.getString("TIPOCADASTRO")));
        return divida;
    }
}
