package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.ProcessoCalculoIPTU;
import br.com.webpublico.entidades.ProcessoCalculoNfse;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProcessoCalculoNfseSetter implements BatchPreparedStatementSetter {

    private ProcessoCalculoNfse processo;

    public ProcessoCalculoNfseSetter(ProcessoCalculoNfse processo) {
        this.processo = processo;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, processo.getId());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
