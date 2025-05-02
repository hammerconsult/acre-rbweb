package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.CarneIPTU;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import com.sun.btrace.org.objectweb.asm.Type;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CarneIPTUSetter implements BatchPreparedStatementSetter {
    private List<CarneIPTU> carnes;
    private SingletonGeradorId geradorDeIds;

    public CarneIPTUSetter(List<CarneIPTU> carnes, SingletonGeradorId geradorDeIds) {
        this.carnes = carnes;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        CarneIPTU carne = carnes.get(i);
        ps.setLong(1, geradorDeIds.getProximoId());
        ps.setLong(2, carne.getCalculo().getId());
        ps.setBigDecimal(3, carne.getAreaConstruida());
        ps.setBigDecimal(4, carne.getFatorCorrecao());
        ps.setBigDecimal(5, carne.getAreaExcedente());
        ps.setBigDecimal(6, carne.getFatorPeso());
        ps.setBigDecimal(7, carne.getFracaoIdeal());
        ps.setBigDecimal(8, carne.getUfmrb());
        ps.setBigDecimal(9, carne.getVlrM2Construido());
        ps.setBigDecimal(10, carne.getVlrM2Excedente());
        ps.setBigDecimal(11, carne.getVlrM2Terreno());
        ps.setBigDecimal(12, carne.getVlrVenalEdificacao());
        ps.setBigDecimal(13, carne.getVlrVenalExcedente());
        ps.setBigDecimal(14, carne.getVlrVenalTerreno());
        ps.setBigDecimal(15, carne.getAliquota());
        if (carne.getConstrucao() == null || carne.getConstrucao().isDummy()) {
            ps.setNull(16, Type.LONG);
        } else {
            ps.setLong(16, carne.getConstrucao().getId());
        }
    }

    @Override
    public int getBatchSize() {
        return carnes.size();
    }
}
