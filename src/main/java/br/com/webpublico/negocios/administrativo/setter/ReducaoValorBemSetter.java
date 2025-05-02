package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.ReducaoValorBem;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 16/10/14
 * Time: 14:20
 * To change this template use File | Settings | File Templates.
 */
public class ReducaoValorBemSetter implements BatchPreparedStatementSetter {
    public static final String SQL_INSERT_REDUCAO_VALOR_BEM = "INSERT INTO REDUCAOVALORBEM (ID, LOTEREDUCAOVALORBEM_ID) VALUES (?, ?)";

    private final List<ReducaoValorBem> reducoes;

    public ReducaoValorBemSetter(List<ReducaoValorBem> reducoes) {
        this.reducoes = reducoes;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ReducaoValorBem reducaoValorBem = reducoes.get(i);

        ps.setLong(1, reducaoValorBem.getId());
        ps.setLong(2, reducaoValorBem.getLoteReducaoValorBem().getId());
    }

    @Override
    public int getBatchSize() {
        return reducoes.size();
    }
}
