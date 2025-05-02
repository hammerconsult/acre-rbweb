package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.OcorrenciaCalculoIPTU;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class OcorrenciaIPTUSetter implements BatchPreparedStatementSetter {
    private List<OcorrenciaCalculoIPTU> ocorrencias;
    private SingletonGeradorId geradorDeIds;

    public OcorrenciaIPTUSetter(List<OcorrenciaCalculoIPTU> ocorrencias, SingletonGeradorId geradorDeIds) {
        this.ocorrencias = ocorrencias;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        OcorrenciaCalculoIPTU ocorrencia = ocorrencias.get(i);
        ocorrencia.setId(geradorDeIds.getProximoId());
        ps.setLong(1, ocorrencia.getId());
        ps.setLong(2, ocorrencia.getOcorrencia().getId());
        if (ocorrencia.getCadastroImobiliario() != null) {
            ps.setLong(3, ocorrencia.getCadastroImobiliario().getId());
        } else {
            ps.setNull(3, Types.NUMERIC);
        }
        if (ocorrencia.getConstrucao() != null) {
            ps.setLong(4, ocorrencia.getConstrucao().getId());
        } else {
            ps.setNull(4, Types.NUMERIC);
        }
        ps.setLong(5, ocorrencia.getCalculoIptu().getId());

    }

    @Override
    public int getBatchSize() {
        return ocorrencias.size();
    }
}
