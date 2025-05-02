package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.MemoriaCaluloIPTU;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import com.sun.btrace.org.objectweb.asm.Type;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MemoriaCalculoSetter implements BatchPreparedStatementSetter {
    private List<MemoriaCaluloIPTU> memorias;
    private SingletonGeradorId geradorDeIds;

    public MemoriaCalculoSetter(List<MemoriaCaluloIPTU> memorias, SingletonGeradorId geradorDeIds) {
        this.memorias = memorias;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        MemoriaCaluloIPTU memoria = memorias.get(i);
        memoria.setId(geradorDeIds.getProximoId());
        ps.setLong(1, memoria.getId());
        ps.setLong(2, memoria.getCalculoIPTU().getId());
        ps.setLong(3, memoria.getEvento().getId());
        ps.setBigDecimal(4, memoria.getValor());
        if (memoria.getConstrucao().isDummy()) {
            ps.setNull(5, Type.LONG);
        } else {
            ps.setLong(5, memoria.getConstrucao().getId());
        }
    }

    @Override
    public int getBatchSize() {
        return memorias.size();
    }
}
