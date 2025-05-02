package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.ProcessoCalculo;
import br.com.webpublico.entidades.ProcessoCalculoIPTU;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ProcessoCalculoSetter implements BatchPreparedStatementSetter {

    private ProcessoCalculo processo;
    private SingletonGeradorId geradorDeIds;

    public ProcessoCalculoSetter(ProcessoCalculo processo, SingletonGeradorId geradorDeIds) {
        this.processo = processo;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        processo.setId(geradorDeIds.getProximoId());
        ps.setLong(1, processo.getId());
        ps.setLong(2, processo.getExercicio().getId());
        ps.setLong(3, processo.getDivida().getId());
        ps.setDate(4, new java.sql.Date(new java.util.Date().getTime()));
        ps.setString(5, processo.getDescricao());
        if(processo.getUsuarioSistema() != null && processo.getUsuarioSistema().getId() != null){
            ps.setLong(6, processo.getUsuarioSistema().getId());
        } else {
            ps.setNull(6, Types.NUMERIC);
        }
        ps.setString(7, processo.getNumeroProtocolo());
        ps.setString(8, processo.getAnoProtocolo());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
