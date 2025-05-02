package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.LivroDividaAtiva;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class LivroSetter implements BatchPreparedStatementSetter {

    private final LivroDividaAtiva livro;
    private final List<Long> ids;

    public LivroSetter(LivroDividaAtiva livro, List<Long> ids) {
        this.livro = livro;
        this.ids = ids;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        livro.setId(ids.get(i));
        ps.setLong(1, livro.getId());
        ps.setLong(2, livro.getNumero());
        ps.setLong(3, livro.getTotalPaginas());
        ps.setLong(4, livro.getIdExercicio());
        ps.setString(5, livro.getTipoCadastroTributario().name());
        ps.setLong(6, livro.getSequencia());
        ps.setString(7, livro.getTipoOrdenacao().name());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
