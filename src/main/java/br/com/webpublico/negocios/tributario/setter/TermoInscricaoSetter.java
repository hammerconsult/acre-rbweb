package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.LinhaDoLivroDividaAtiva;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TermoInscricaoSetter implements BatchPreparedStatementSetter {

    private final List<LinhaDoLivroDividaAtiva> linhas;
    private final List<Long> ids;

    public TermoInscricaoSetter(List<LinhaDoLivroDividaAtiva> linhas, List<Long> ids) {
        this.linhas = linhas;
        this.ids = ids;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        LinhaDoLivroDividaAtiva linha = linhas.get(i);

        linha.getTermoInscricaoDividaAtiva().setId(ids.get(i));
        ps.setLong(1, linha.getTermoInscricaoDividaAtiva().getId());
        ps.setString(2, linha.getTermoInscricaoDividaAtiva().getNumero());
    }

    @Override
    public int getBatchSize() {
        return linhas.size();
    }
}
