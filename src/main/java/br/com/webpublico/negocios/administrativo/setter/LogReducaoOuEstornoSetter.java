package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.LogReducaoOuEstorno;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * User: Wellington
 * Date: 27/06/17
 */
public class LogReducaoOuEstornoSetter implements BatchPreparedStatementSetter {
    public static final String SQL_INSERT_LOG_REDUCAO_OU_ESTORNO = "INSERT INTO LOGREDUCAOOUESTORNO (ID) VALUES (?) ";

    private final LogReducaoOuEstorno logReducaoOuEstorno;
    private final SingletonGeradorId geradorDeIds;


    public LogReducaoOuEstornoSetter(LogReducaoOuEstorno logReducaoOuEstorno, SingletonGeradorId geradorDeIds) {
        this.logReducaoOuEstorno = logReducaoOuEstorno;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        logReducaoOuEstorno.setId(geradorDeIds.getProximoId());

        ps.setLong(1, logReducaoOuEstorno.getId());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
