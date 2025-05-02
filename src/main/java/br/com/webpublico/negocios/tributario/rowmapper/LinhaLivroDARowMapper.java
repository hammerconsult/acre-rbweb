package br.com.webpublico.negocios.tributario.rowmapper;


import br.com.webpublico.util.UltimoLinhaDaPaginaDoLivroDividaAtiva;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LinhaLivroDARowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        UltimoLinhaDaPaginaDoLivroDividaAtiva ultimo = new UltimoLinhaDaPaginaDoLivroDividaAtiva();
        ultimo.setSequencia(rs.getLong("SEQUENCIA"));
        ultimo.setPagina(rs.getLong("PAGINA"));
        ultimo.setLinha(rs.getLong("LINHA"));
        ultimo.setNumeroLivro(rs.getLong("NUMEROLIVRO"));

        return ultimo;
    }
}
