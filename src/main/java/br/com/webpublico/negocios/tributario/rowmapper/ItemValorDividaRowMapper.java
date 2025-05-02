package br.com.webpublico.negocios.tributario.rowmapper;

import br.com.webpublico.entidades.ItemValorDivida;
import br.com.webpublico.entidades.Tributo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 23/09/13
 * Time: 17:04
 * To change this template use File | Settings | File Templates.
 */
public class ItemValorDividaRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        ItemValorDivida itemValorDivida = new ItemValorDivida();
        itemValorDivida.setId(rs.getLong("ID"));
        itemValorDivida.setIsento(rs.getBoolean("ISENTO"));
        itemValorDivida.setValor(rs.getBigDecimal("VALOR"));
        Tributo tributo = new Tributo();
        tributo.setId(rs.getLong("TRIBUTO_ID"));
        tributo.setDescricao(rs.getString("TRIBUTO_DESCRICAO"));
        itemValorDivida.setTributo(tributo);
        return itemValorDivida;
    }
}
