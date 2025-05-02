package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.ReducaoValorBemContabil;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class UpdateReducaoValorBemContabilSetter implements BatchPreparedStatementSetter {

    public static final String SQL_UPDATE_REDUCAO_VALOR_BEM_CONTABIL = " UPDATE REDUCAOVALORBEMCONTABIL SET BENSMOVEIS_ID = ?, SITUACAO = ? WHERE ID = ? ";

    private final ReducaoValorBemContabil reducaoValorBemContabil;

    public UpdateReducaoValorBemContabilSetter(ReducaoValorBemContabil reducaoValorBemContabil) {
        this.reducaoValorBemContabil = reducaoValorBemContabil;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        if (reducaoValorBemContabil.getBensMoveis() != null && reducaoValorBemContabil.getBensMoveis().getId() != null) {
            ps.setLong(1, reducaoValorBemContabil.getBensMoveis().getId());
        } else {
            ps.setNull(1, Types.NULL);
        }
        ps.setString(2, reducaoValorBemContabil.getSituacao().name());
        ps.setLong(3, reducaoValorBemContabil.getId());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
