package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.CalculoIPTU;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class CalculoIPTUSetter implements BatchPreparedStatementSetter {
    private List<CalculoIPTU> calculos;

    public CalculoIPTUSetter(List<CalculoIPTU> calculos) {
        this.calculos = calculos;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        CalculoIPTU calculo = calculos.get(i);
        ps.setLong(1, calculo.getId());
        if (calculo.getConstrucao() != null) {
            ps.setLong(2, calculo.getConstrucao().getId());
        } else {
            ps.setNull(2, Types.NUMERIC);
        }
        if (calculo.getProcessoCalculo() != null) {
            ps.setLong(3, calculo.getProcessoCalculo().getId());
        } else {
            ps.setNull(3, Types.NUMERIC);
        }
        ps.setLong(4, calculo.getCadastroImobiliario().getId());
        if (calculo.getIsencaoCadastroImobiliario() != null) {
            ps.setLong(5, calculo.getIsencaoCadastroImobiliario().getId());
        } else {
            ps.setNull(5, Types.NUMERIC);
        }
    }

    @Override
    public int getBatchSize() {
        return calculos.size();
    }
}
