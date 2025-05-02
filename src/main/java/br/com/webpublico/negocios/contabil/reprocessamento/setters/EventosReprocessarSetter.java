package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.EventosReprocessar;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 28/10/14
 * Time: 17:19
 * To change this template use File | Settings | File Templates.
 */
public class EventosReprocessarSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = "INSERT INTO EVENTOSREPROCESSAR " +
            "( ID, DATAINICIAL, DATAFINAL, DATAREGISTRO, EVENTOCONTABIL_ID, CLASSEORIGEM, IDORIGEM, STATUSEVENTOREPROCESSAR)" +
            " VALUES " +
            "( ?, ?, ?, ?, ?, ?, ?, ?)";
    private final List<EventosReprocessar> itens;
    private final SingletonGeradorId geradorDeIds;

    public EventosReprocessarSetter(List<EventosReprocessar> itens, SingletonGeradorId geradorDeIds) {
        this.itens = itens;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        EventosReprocessar eventosReprocessar = itens.get(i);
        eventosReprocessar.setId(geradorDeIds.getProximoId());

        ps.setLong(1, eventosReprocessar.getId());
//        if (eventosReprocessar.getDataInicial() != null) {
//            ps.setDate(2, new java.sql.Date(eventosReprocessar.getDataInicial().getTime()));
//        } else {
//            ps.setNull(2, Types.DATE);
//        }
//        if (eventosReprocessar.getDataFinal() != null) {
//            ps.setDate(3, new java.sql.Date(eventosReprocessar.getDataFinal().getTime()));
//        } else {
//            ps.setNull(3, Types.DATE);
//        }
//        if (eventosReprocessar.getDataRegistro() != null) {
//            ps.setDate(4, new java.sql.Date(eventosReprocessar.getDataRegistro().getTime()));
//        } else {
//            ps.setNull(4, Types.DATE);
//        }
//        if (eventosReprocessar.getEventoContabil() != null && eventosReprocessar.getEventoContabil().getId() != null) {
//            ps.setLong(5, eventosReprocessar.getEventoContabil().getId());
//        } else {
//            ps.setNull(5, Types.NUMERIC);
//        }
//        ps.setString(6, eventosReprocessar.getClasseOrigem());
//
//        if (eventosReprocessar.getIdOrigem() != null) {
//            ps.setLong(7, eventosReprocessar.getIdOrigem());
//        } else {
//            ps.setLong(7, Types.NUMERIC);
//        }
//        if (eventosReprocessar.getStatusEventoReprocessar() != null) {
//            ps.setString(8, eventosReprocessar.getStatusEventoReprocessar().name());
//        } else {
//            ps.setNull(8, Types.VARCHAR);
//        }
    }

    @Override
    public int getBatchSize() {
        return itens.size();
    }
}
