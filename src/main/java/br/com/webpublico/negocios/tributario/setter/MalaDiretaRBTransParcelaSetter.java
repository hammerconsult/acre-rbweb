package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.MalaDiretaRBTransParcela;
import br.com.webpublico.entidades.ParcelaMalaDiretaIPTU;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MalaDiretaRBTransParcelaSetter implements BatchPreparedStatementSetter {

    MalaDiretaRBTransParcela malaDiretaRBTransParcela;
    Long id;

    public MalaDiretaRBTransParcelaSetter(MalaDiretaRBTransParcela malaDiretaRBTransParcela, Long id) {
        this.id = id;
        this.malaDiretaRBTransParcela = malaDiretaRBTransParcela;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        malaDiretaRBTransParcela.setId(id);
        ps.setLong(1, malaDiretaRBTransParcela.getId());
        ps.setLong(2, malaDiretaRBTransParcela.getMalaDiretaRBTransPermissao().getId());
        ps.setLong(3, malaDiretaRBTransParcela.getParcelaValorDivida().getId());
        ps.setLong(4, malaDiretaRBTransParcela.getDam().getId());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
