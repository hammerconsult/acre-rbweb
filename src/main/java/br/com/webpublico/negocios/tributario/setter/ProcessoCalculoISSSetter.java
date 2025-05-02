package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.ProcessoCalculoISS;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProcessoCalculoISSSetter implements BatchPreparedStatementSetter {

    private ProcessoCalculoISS processo;

    public ProcessoCalculoISSSetter(ProcessoCalculoISS processo) {
        this.processo = processo;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, processo.getId());
        ps.setLong(2, processo.getMesReferencia());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
