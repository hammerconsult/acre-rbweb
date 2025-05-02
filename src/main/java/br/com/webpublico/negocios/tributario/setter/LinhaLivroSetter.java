package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.LinhaDoLivroDividaAtiva;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class LinhaLivroSetter implements BatchPreparedStatementSetter {

    private final List<LinhaDoLivroDividaAtiva> linhas;
    private final List<Long> ids;

    public LinhaLivroSetter(List<LinhaDoLivroDividaAtiva> linhas, List<Long> ids) {
        this.linhas = linhas;
        this.ids = ids;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        LinhaDoLivroDividaAtiva linha = linhas.get(i);

        linha.setId(ids.get(i));
        ps.setLong(1, linha.getId());
        ps.setLong(2, linha.getSequencia());
        ps.setLong(3, linha.getPagina());
        ps.setLong(4, linha.getNumeroDaLinha());
        ps.setLong(5, linha.getItemInscricaoDividaAtiva().getId());
        ps.setLong(6, linha.getItemLivroDividaAtiva().getId());
        ps.setLong(7, linha.getTermoInscricaoDividaAtiva().getId());
    }

    @Override
    public int getBatchSize() {
        return linhas.size();
    }
}
