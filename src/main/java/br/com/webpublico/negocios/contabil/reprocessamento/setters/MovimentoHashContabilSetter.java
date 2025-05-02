package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.contabil.conciliacaocontabil.MovimentoHashContabil;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MovimentoHashContabilSetter implements BatchPreparedStatementSetter {


    public static final String SQL_INSERT = "insert into MovimentoHashContabil (id, hash, data, valor, tipo)\n" +
        "values (?,?,?,?,?)";
    private final MovimentoHashContabil movimento;


    public MovimentoHashContabilSetter(MovimentoHashContabil movimento) {
        this.movimento = movimento;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, movimento.getId());
        ps.setString(2, movimento.getHash());
        ps.setDate(3, new Date(movimento.getData().getTime()));
        ps.setBigDecimal(4, movimento.getValor());
        ps.setString(5, movimento.getTipo().name());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
