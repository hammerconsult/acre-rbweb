package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.ReducaoValorBemEstornoContabil;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class UpdateReducaoValorBemEstornoContabilSetter implements BatchPreparedStatementSetter {

    public static final String SQL_UPDATE_REDUCAO_VALOR_BEM_ESTORNO_CONTABIL = " UPDATE REDUCAOVALORBEMESTORNOCONTABIL SET BENSMOVEIS_ID = ?, SITUACAO = ? WHERE ID = ? ";

    private final ReducaoValorBemEstornoContabil reducaoValorBemEstornoContabil;

    public UpdateReducaoValorBemEstornoContabilSetter(ReducaoValorBemEstornoContabil reducaoValorBemEstornoContabil) {
        this.reducaoValorBemEstornoContabil = reducaoValorBemEstornoContabil;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        if (reducaoValorBemEstornoContabil.getBensMoveis() != null && reducaoValorBemEstornoContabil.getBensMoveis().getId() != null) {
            ps.setLong(1, reducaoValorBemEstornoContabil.getBensMoveis().getId());
        } else {
            ps.setNull(1, Types.NULL);
        }
        ps.setString(2, reducaoValorBemEstornoContabil.getSituacao().name());
        ps.setLong(3, reducaoValorBemEstornoContabil.getId());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
