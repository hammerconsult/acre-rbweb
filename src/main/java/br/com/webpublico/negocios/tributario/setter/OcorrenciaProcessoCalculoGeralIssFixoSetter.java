package br.com.webpublico.negocios.tributario.setter;

import br.com.webpublico.entidades.OcorrenciaProcessoCalculoGeralIssFixo;
import br.com.webpublico.negocios.tributario.singletons.SingletonLancamentoGeralISSFixo;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 18/09/13
 * Time: 17:02
 * To change this template use File | Settings | File Templates.
 */
public class OcorrenciaProcessoCalculoGeralIssFixoSetter implements BatchPreparedStatementSetter {
    private List<OcorrenciaProcessoCalculoGeralIssFixo> ocorrencias;

    public OcorrenciaProcessoCalculoGeralIssFixoSetter(List<OcorrenciaProcessoCalculoGeralIssFixo> ocorrenciasProcesso) {
        this.ocorrencias = ocorrenciasProcesso;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        OcorrenciaProcessoCalculoGeralIssFixo opc = ocorrencias.get(i);
        opc.setId(SingletonLancamentoGeralISSFixo.getInstance().getProximoId());

        ps.setLong(1, opc.getId());
        ps.setLong(2, opc.getProcessoCalculoGeral().getId());

        if (opc.getCadastroEconomico() != null) {
            ps.setLong(3, opc.getCadastroEconomico().getId());
            ps.setNull(4, Types.NUMERIC);
        } else {
            ps.setNull(3, Types.NUMERIC);
            ps.setLong(4, opc.getCalculoISS().getId());
        }

        ps.setLong(5, opc.getOcorrencia().getId());
    }

    @Override
    public int getBatchSize() {
        return ocorrencias.size();
    }
}
