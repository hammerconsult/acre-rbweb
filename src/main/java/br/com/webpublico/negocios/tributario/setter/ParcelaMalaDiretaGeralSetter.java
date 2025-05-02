package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.ParcelaMalaDiretaGeral;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ParcelaMalaDiretaGeralSetter implements BatchPreparedStatementSetter {

    ParcelaMalaDiretaGeral parcelaMalaDiretaGeral;
    Long id;

    public ParcelaMalaDiretaGeralSetter(ParcelaMalaDiretaGeral parcelaMalaDiretaGeral, Long id) {
        this.id = id;
        this.parcelaMalaDiretaGeral = parcelaMalaDiretaGeral;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        parcelaMalaDiretaGeral.setId(id);
        ps.setLong(1, parcelaMalaDiretaGeral.getId());
        ps.setLong(2, parcelaMalaDiretaGeral.getParcela().getId());
        ps.setLong(3, parcelaMalaDiretaGeral.getItemMalaDiretaGeral().getId());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
