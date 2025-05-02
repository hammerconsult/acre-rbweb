package br.com.webpublico.negocios.tributario.setter;

import br.com.webpublico.entidades.Ocorrencia;
import br.com.webpublico.interfaces.DetentorDeOcorrencia;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 18/09/13
 * Time: 16:49
 * To change this template use File | Settings | File Templates.
 */
public class OcorrenciaSetter implements BatchPreparedStatementSetter {
    private List<? extends DetentorDeOcorrencia> ocorrencias;
    private SingletonGeradorId geradorDeIds;

    public OcorrenciaSetter(List<? extends DetentorDeOcorrencia> listaDeOcorrencias, SingletonGeradorId gerador) {
        this.ocorrencias = listaDeOcorrencias;
        this.geradorDeIds = gerador;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        Ocorrencia o = ocorrencias.get(i).getOcorrencia();
        o.setId(geradorDeIds.getProximoId());

        ps.setLong(1, o.getId());
        ps.setString(2, o.getConteudo());
        ps.setDate(3, new Date(o.getDataRegistro().getTime()));
        ps.setString(4, o.getNivelOcorrencia().name());
        ps.setString(5, o.getTipoOcorrencia().name());
        ps.setString(6, o.getDetalhesTecnicos());
    }

    @Override
    public int getBatchSize() {
        return ocorrencias.size();
    }
}
