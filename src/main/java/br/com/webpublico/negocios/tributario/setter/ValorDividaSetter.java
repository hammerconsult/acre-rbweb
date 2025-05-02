package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.ValorDivida;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ValorDividaSetter implements BatchPreparedStatementSetter {

    private final ValorDivida valorDivida;
    private final Long id;
    private Long idRevisao;

    public ValorDividaSetter(ValorDivida valorDivida, Long id) {
        this.valorDivida = valorDivida;
        this.id = id;
    }

    public ValorDividaSetter(ValorDivida valorDivida, Long id, Long idRevisao) {
        this.valorDivida = valorDivida;
        this.id = id;
        this.idRevisao = idRevisao;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        valorDivida.setId(id);
        ps.setLong(1, valorDivida.getId());
        ps.setDate(2, new Date(valorDivida.getEmissao().getTime()));
        ps.setBigDecimal(3, valorDivida.getValor());
        ps.setLong(4, valorDivida.getDivida().getId());
        ps.setLong(5, valorDivida.getExercicio().getId());
        ps.setLong(6, valorDivida.getCalculo().getId());
        if (idRevisao != null) {
            ps.setLong(7, idRevisao);
            ps.setLong(8, 0L);
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
