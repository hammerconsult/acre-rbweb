package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.ReducaoValorBemResidual;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 16/10/14
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
public class ReducaoValorBemResidualSetter implements BatchPreparedStatementSetter {
    public static final String SQL_INSERT_REDUCAO_VALOR_BEM_RESIDUAL = "INSERT INTO REDUCAOVALORBEMRESIDUAL (ID, LOTEREDUCAOVALORBEM_ID, " +
        "GRUPOBEM_ID, BEM_ID, VALORORIGINAL, VALORLIQUIDO) VALUES (?, ?, ?, ?, ?, ?)";

    private final List<ReducaoValorBemResidual> bensResidual;
    private final SingletonGeradorId geradorDeIds;

    public ReducaoValorBemResidualSetter(List<ReducaoValorBemResidual> bensResidual, SingletonGeradorId geradorDeIds) {
        this.bensResidual = bensResidual;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ReducaoValorBemResidual reducaoValorBemResidual = bensResidual.get(i);
        reducaoValorBemResidual.setId(geradorDeIds.getProximoId());

        ps.setLong(1, reducaoValorBemResidual.getId());
        ps.setLong(2, reducaoValorBemResidual.getLoteReducaoValorBem().getId());
        ps.setLong(3, reducaoValorBemResidual.getGrupoBem().getId());
        ps.setLong(4, reducaoValorBemResidual.getBem().getId());
        ps.setBigDecimal(5, reducaoValorBemResidual.getValorOriginal());
        ps.setBigDecimal(6, reducaoValorBemResidual.getValorLiquido());
    }

    @Override
    public int getBatchSize() {
        return bensResidual.size();
    }
}
