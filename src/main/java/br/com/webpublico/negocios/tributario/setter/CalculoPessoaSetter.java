package br.com.webpublico.negocios.tributario.setter;

import br.com.webpublico.entidades.CalculoPessoa;
import br.com.webpublico.interfaces.GeradorDeID;
import br.com.webpublico.negocios.tributario.auxiliares.GeradorDeIds;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class CalculoPessoaSetter implements BatchPreparedStatementSetter {
    private final List<CalculoPessoa> calculos;
    private final List<Long> ids;

    public CalculoPessoaSetter(List<CalculoPessoa> calculos, List<Long> ids) {
        this.calculos = calculos;
        this.ids = ids;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        CalculoPessoa item = calculos.get(i);
        ps.setLong(1, ids.get(i));
        ps.setLong(2, item.getCalculo().getId());
        if (item.getIdPessoa() != null) {
            ps.setLong(3, item.getIdPessoa());
        } else {
            ps.setNull(3, Types.NUMERIC);
        }
    }

    @Override
    public int getBatchSize() {
        return calculos.size();
    }
}
