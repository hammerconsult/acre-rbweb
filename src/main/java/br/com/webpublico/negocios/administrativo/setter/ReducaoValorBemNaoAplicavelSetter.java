package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.ReducaoValorBemNaoAplicavel;
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
public class ReducaoValorBemNaoAplicavelSetter implements BatchPreparedStatementSetter {
    public static final String SQL_INSERT_REDUCAO_VALOR_BEM_NAO_APLICAVEL = "INSERT INTO REDUCAOVALORBEMNAOAPLICAVE (ID, LOTEREDUCAOVALORBEM_ID, " +
        "GRUPOBEM_ID, BEM_ID, VALORORIGINAL) VALUES (?, ?, ?, ?, ?)";

    private final List<ReducaoValorBemNaoAplicavel> bensNaoAplicavel;
    private final SingletonGeradorId geradorDeIds;

    public ReducaoValorBemNaoAplicavelSetter(List<ReducaoValorBemNaoAplicavel> bensNaoAplicavel, SingletonGeradorId geradorDeIds) {
        this.bensNaoAplicavel = bensNaoAplicavel;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ReducaoValorBemNaoAplicavel reducaoValorBemNaoAplicavel = bensNaoAplicavel.get(i);
        reducaoValorBemNaoAplicavel.setId(geradorDeIds.getProximoId());

        ps.setLong(1, reducaoValorBemNaoAplicavel.getId());
        ps.setLong(2, reducaoValorBemNaoAplicavel.getLoteReducaoValorBem().getId());
        ps.setLong(3, reducaoValorBemNaoAplicavel.getGrupoBem().getId());
        ps.setLong(4, reducaoValorBemNaoAplicavel.getBem().getId());
        ps.setBigDecimal(5, reducaoValorBemNaoAplicavel.getValorOriginal());
    }

    @Override
    public int getBatchSize() {
        return bensNaoAplicavel.size();
    }
}
