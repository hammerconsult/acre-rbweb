package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.EventoCalculoConstrucao;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class EventoCalculoConstrucaoSetter implements BatchPreparedStatementSetter {

    SingletonGeradorId geradorDeIds;
    private List<EventoCalculoConstrucao> eventos;

    public EventoCalculoConstrucaoSetter(List<EventoCalculoConstrucao> eventos, SingletonGeradorId geradorDeIds) {
        this.eventos = eventos;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        EventoCalculoConstrucao evento = eventos.get(i);
        evento.setId(geradorDeIds.getProximoId());
        ps.setLong(1, evento.getId());
        ps.setLong(2, evento.getConstrucao().getId());
        ps.setLong(3, evento.getEventoCalculo().getId());
        ps.setBigDecimal(4, evento.getValor());
    }

    @Override
    public int getBatchSize() {
        return eventos.size();
    }
}
