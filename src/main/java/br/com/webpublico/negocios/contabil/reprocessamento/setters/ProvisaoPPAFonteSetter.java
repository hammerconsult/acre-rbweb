package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.ProvisaoPPAFonte;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 23/10/14
 * Time: 17:38
 * To change this template use File | Settings | File Templates.
 */
public class ProvisaoPPAFonteSetter implements BatchPreparedStatementSetter {


    public static final String SQL_INSERT = "INSERT INTO PROVISAOPPAFONTE " +
        " (ID, SOMENTELEITURA, VALOR, DESTINACAODERECURSOS_ID, ORIGEM_ID, PROVISAOPPADESPESA_ID, ESFERAORCAMENTARIA, HISTORICORAZAO, HISTORICONOTA, EVENTOCONTABIL_ID, EXTENSAOFONTERECURSO_ID) " +
        " VALUES " +
        "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE = "UPDATE PROVISAOPPAFONTE " +
        " SET SOMENTELEITURA = ?, VALOR = ?, DESTINACAODERECURSOS_ID = ?, ORIGEM_ID = ?, PROVISAOPPADESPESA_ID = ?, ESFERAORCAMENTARIA = ?, HISTORICORAZAO = ?, HISTORICONOTA = ?, EVENTOCONTABIL_ID = ?, EXTENSAOFONTERECURSO_ID = ?" +
        " WHERE ID = ? ";

    public static final String SQL_SELECT_PROVISAO = "SELECT ID, SOMENTELEITURA, VALOR, DESTINACAODERECURSOS_ID, ORIGEM_ID, PROVISAOPPADESPESA_ID, ESFERAORCAMENTARIA, HISTORICORAZAO,HISTORICONOTA,  EVENTOCONTABIL_ID, EXTENSAOFONTERECURSO_ID FROM PROVISAOPPAFONTE " +
        " WHERE PROVISAOPPADESPESA_ID = ? ";

    private final List<ProvisaoPPAFonte> fontes;
    private final SingletonGeradorId geradorDeIds;

    public ProvisaoPPAFonteSetter(List<ProvisaoPPAFonte> fontes, SingletonGeradorId geradorDeIds) {
        this.fontes = fontes;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ProvisaoPPAFonte fonte = fontes.get(i);
        fonte.setId(geradorDeIds.getProximoId());

        ps.setLong(1, fonte.getId());
        ps.setBoolean(2, fonte.getSomenteLeitura());
        ps.setBigDecimal(3, fonte.getValor());
        ps.setLong(4, fonte.getDestinacaoDeRecursos().getId());
        if (fonte.getOrigem() != null) {
            ps.setLong(5, fonte.getOrigem().getId());
        } else {
            ps.setNull(5, Types.NUMERIC);
        }
        ps.setLong(6, fonte.getProvisaoPPADespesa().getId());
        ps.setString(7, fonte.getEsferaOrcamentaria().name());
        ps.setString(8, fonte.getHistoricoRazao());
        ps.setString(9, fonte.getHistoricoNota());
        if (fonte.getEventoContabil() != null) {
            ps.setLong(10, fonte.getEventoContabil().getId());
        } else {
            ps.setNull(10, Types.NUMERIC);
        }

        if (fonte.getExtensaoFonteRecurso() != null) {
            ps.setLong(11, fonte.getExtensaoFonteRecurso().getId());
        } else {
            ps.setNull(11, Types.NUMERIC);
        }

    }

    @Override
    public int getBatchSize() {
        return fontes.size();
    }
}
