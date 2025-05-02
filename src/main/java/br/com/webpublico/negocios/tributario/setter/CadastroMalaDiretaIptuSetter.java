package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.CadastroMalaDiretaIPTU;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CadastroMalaDiretaIptuSetter implements BatchPreparedStatementSetter {

    CadastroMalaDiretaIPTU cadastroMalaDiretaIPTU;
    Long id;

    public CadastroMalaDiretaIptuSetter(CadastroMalaDiretaIPTU cadastroMalaDiretaIPTU, Long id) {
        this.id = id;
        this.cadastroMalaDiretaIPTU = cadastroMalaDiretaIPTU;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        cadastroMalaDiretaIPTU.setId(id);
        ps.setLong(1, cadastroMalaDiretaIPTU.getId());
        ps.setLong(2, cadastroMalaDiretaIPTU.getMalaDiretaIPTU().getId());
        ps.setLong(3, cadastroMalaDiretaIPTU.getCadastroImobiliario().getId());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
