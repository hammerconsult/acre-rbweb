package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.LoteEstornoReducaoValorBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * User: Wellington
 * Date: 27/06/17
 */
public class LoteEstornoReducaoValorBemSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT_LOTE_ESTORNO_REDUCAO_VALOR_BEM = "INSERT INTO LOTEESTORNOREDUCAOVALORBEM (ID, LOGREDUCAOOUESTORNO_ID, " +
        "LOTEREDUCAOVALORBEM_ID, DATAESTORNO, USUARIODOESTORNO_ID) VALUES (?, ?, ?, ?, ?)";
    private final LoteEstornoReducaoValorBem loteEstornoReducaoValorBem;
    private final SingletonGeradorId geradorDeIds;

    public LoteEstornoReducaoValorBemSetter(LoteEstornoReducaoValorBem loteEstornoReducaoValorBem, SingletonGeradorId geradorDeIds) {
        this.loteEstornoReducaoValorBem = loteEstornoReducaoValorBem;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        loteEstornoReducaoValorBem.setId(geradorDeIds.getProximoId());

        ps.setLong(1, loteEstornoReducaoValorBem.getId());
        if (loteEstornoReducaoValorBem.getLogReducaoOuEstorno() != null && loteEstornoReducaoValorBem.getLogReducaoOuEstorno().getId() != null) {
            ps.setLong(2, loteEstornoReducaoValorBem.getLogReducaoOuEstorno().getId());
        } else {
            ps.setNull(2, Types.NULL);
        }
        ps.setLong(3, loteEstornoReducaoValorBem.getLoteReducaoValorBem().getId());
        ps.setDate(4, new java.sql.Date(loteEstornoReducaoValorBem.getDataEstorno().getTime()));
        ps.setLong(5, loteEstornoReducaoValorBem.getUsuarioDoEstorno().getId());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
