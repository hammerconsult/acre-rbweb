package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.ProcessoCalculoIPTU;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProcessoCalculoIPTUSetter implements BatchPreparedStatementSetter {

    private ProcessoCalculoIPTU processo;

    public ProcessoCalculoIPTUSetter(ProcessoCalculoIPTU processo) {
        this.processo = processo;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {

        ps.setLong(1, processo.getId());
        ps.setLong(2, processo.getConfiguracaoEventoIPTU().getId());
        ps.setString(3, processo.getCadastroInicial());
        ps.setString(4, processo.getCadastroFinal());

    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
