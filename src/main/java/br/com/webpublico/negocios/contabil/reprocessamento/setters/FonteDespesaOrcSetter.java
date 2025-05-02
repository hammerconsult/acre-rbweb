package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.FonteDespesaORC;
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
public class FonteDespesaOrcSetter implements BatchPreparedStatementSetter {


    public static final String SQL_INSERT = "INSERT INTO FONTEDESPESAORC " +
        " (ID, DESPESAORC_ID, PROVISAOPPAFONTE_ID, GRUPOORCAMENTARIO_ID) " +
        " VALUES " +
        "(?, ?, ?, ?)";

    public static final String SQL_UPDATE = "UPDATE FONTEDESPESAORC " +
        " SET DESPESAORC_ID = ?, PROVISAOPPAFONTE_ID = ?, GRUPOORCAMENTARIO_ID = ? " +
        " WHERE ID = ? ";

    private final List<FonteDespesaORC> fontes;
    private final SingletonGeradorId geradorDeIds;

    public FonteDespesaOrcSetter(List<FonteDespesaORC> fontes, SingletonGeradorId geradorDeIds) {
        this.fontes = fontes;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        FonteDespesaORC fonte = fontes.get(i);
        fonte.setId(geradorDeIds.getProximoId());

        ps.setLong(1, fonte.getId());
        ps.setLong(2, fonte.getDespesaORC().getId());
        ps.setLong(3, fonte.getProvisaoPPAFonte().getId());
        if (fonte.getGrupoOrcamentario() != null) {
            ps.setLong(4, fonte.getGrupoOrcamentario().getId());
        } else {
            ps.setNull(4, Types.NUMERIC);
        }
    }

    @Override
    public int getBatchSize() {
        return fontes.size();
    }
}
