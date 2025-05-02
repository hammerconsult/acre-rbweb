package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.ParcelaMalaDiretaIPTU;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ParcelaMalaDiretaIptuSetter implements BatchPreparedStatementSetter {

    ParcelaMalaDiretaIPTU parcelaMalaDiretaIPTU;
    Long id;

    public ParcelaMalaDiretaIptuSetter(ParcelaMalaDiretaIPTU parcelaMalaDiretaIPTU, Long id) {
        this.id = id;
        this.parcelaMalaDiretaIPTU = parcelaMalaDiretaIPTU;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        parcelaMalaDiretaIPTU.setId(id);
        ps.setLong(1, parcelaMalaDiretaIPTU.getId());
        ps.setLong(2, parcelaMalaDiretaIPTU.getParcela().getId());
        ps.setLong(3, parcelaMalaDiretaIPTU.getCadastroMalaDiretaIPTU().getId());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
