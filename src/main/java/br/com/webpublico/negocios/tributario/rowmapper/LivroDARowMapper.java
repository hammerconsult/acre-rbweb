package br.com.webpublico.negocios.tributario.rowmapper;


import br.com.webpublico.entidades.LivroDividaAtiva;
import br.com.webpublico.enums.TipoCadastroTributario;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LivroDARowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        LivroDividaAtiva livro = new LivroDividaAtiva();
        livro.setId(rs.getLong("ID"));
        livro.setIdExercicio(rs.getLong("EXERCICIO_ID"));
        livro.setNumero(rs.getLong("NUMERO"));
        livro.setSequencia(rs.getLong("SEQUENCIA"));
        livro.setTotalPaginas(rs.getLong("TOTALPAGINAS"));
        livro.setTipoCadastroTributario(TipoCadastroTributario.valueOf(rs.getString("TIPOCADASTROTRIBUTARIO")));
        livro.setTipoOrdenacao(LivroDividaAtiva.TipoOrdenacao.valueOf(rs.getString("TIPOORDENACAO")));

        return livro;
    }
}
