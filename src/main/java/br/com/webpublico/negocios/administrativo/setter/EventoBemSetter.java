package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.EventoBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 16/10/14
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
public class EventoBemSetter implements BatchPreparedStatementSetter {
    public static final String SQL_INSERT_EVENTO_BEM = "INSERT INTO EVENTOBEM (ID, ESTADOINICIAL_ID, ESTADORESULTANTE_ID, BEM_ID, DATAOPERACAO, DATALANCAMENTO, SITUACAOEVENTOBEM, VALORDOLANCAMENTO, TIPOEVENTOBEM, TIPOOPERACAO) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final List<? extends EventoBem> eventos;
    private final SingletonGeradorId geradorDeIds;

    public EventoBemSetter(List<? extends EventoBem> eventos, SingletonGeradorId geradorDeIds) {
        this.eventos = eventos;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        EventoBem eventoBem = eventos.get(i);
        eventoBem.setId(geradorDeIds.getProximoId());

        ps.setLong(1, eventoBem.getId());
        ps.setLong(2, eventoBem.getEstadoInicial().getId());
        ps.setLong(3, eventoBem.getEstadoResultante().getId());
        ps.setLong(4, eventoBem.getBem().getId());
        ps.setTimestamp(5, new Timestamp(eventoBem.getDataOperacao().getTime()));
        ps.setTimestamp(6, new Timestamp(eventoBem.getDataLancamento().getTime()));
        ps.setString(7, eventoBem.getSituacaoEventoBem().name().toUpperCase());
        ps.setBigDecimal(8, eventoBem.getValorDoLancamento());
        ps.setString(9, eventoBem.getTipoEventoBem().name());
        ps.setString(10, eventoBem.getTipoOperacao().name());
    }

    @Override
    public int getBatchSize() {
        return eventos.size();
    }
}
