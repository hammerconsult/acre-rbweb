package br.com.webpublico.negocios.tributario.rowmapper;


import br.com.webpublico.entidades.ItemLivroDividaAtiva;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemLivroDARowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        ItemLivroDividaAtiva item = new ItemLivroDividaAtiva();
        item.setId(rs.getLong("ID"));
        item.setIdInscricaoDividaAtiva(rs.getLong("INSCRICAODIVIDAATIVA_ID"));
        item.setProcessado(rs.getBoolean("PROCESSADO"));
        return item;
    }
}
