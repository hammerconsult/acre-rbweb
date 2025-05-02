package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.ReducaoValorBem;
import br.com.webpublico.enums.SituacaoEventoBem;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateReducaoValorBemSetter implements BatchPreparedStatementSetter {

    public static final String SQL_UPDATE_REDUCAO_VALOR_BEM = " UPDATE EVENTOBEM SET SITUACAOEVENTOBEM = ? WHERE ID = ? ";

    private final Long idReducao;

    public UpdateReducaoValorBemSetter(Long idReducao) {
        this.idReducao = idReducao;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setString(1, SituacaoEventoBem.FINALIZADO.name());
        ps.setLong(2, idReducao);
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
