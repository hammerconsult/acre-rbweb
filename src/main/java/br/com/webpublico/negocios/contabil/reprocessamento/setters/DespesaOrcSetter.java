package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.DespesaORC;
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
public class DespesaOrcSetter implements BatchPreparedStatementSetter {


    public static final String SQL_INSERT = "INSERT INTO DESPESAORC " +
        " (ID, CODIGO, CODIGOREDUZIDO, TIPODESPESAORC, EXERCICIO_ID, PROVISAOPPADESPESA_ID) " +
        " VALUES " +
        "(?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE = "UPDATE FONTEDESPESAORC " +
        " SET CODIGO = ?, CODIGOREDUZIDO = ?, TIPODESPESAORC = ?, EXERCICIO_ID = ?, PROVISAOPPADESPESA_ID = ?) " +
        " WHERE ID = ? ";

    private final List<DespesaORC> fontes;
    private final SingletonGeradorId geradorDeIds;

    public DespesaOrcSetter(List<DespesaORC> fontes, SingletonGeradorId geradorDeIds) {
        this.fontes = fontes;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        DespesaORC fonte = fontes.get(i);
        fonte.setId(geradorDeIds.getProximoId());

        ps.setLong(1, fonte.getId());
        ps.setString(2, fonte.getCodigo());
        ps.setString(3, fonte.getCodigoReduzido());
        ps.setString(4, fonte.getTipoDespesaORC().name());
        ps.setLong(5, fonte.getExercicio().getId());
        ps.setLong(6, fonte.getProvisaoPPADespesa().getId());
    }

    @Override
    public int getBatchSize() {
        return fontes.size();
    }
}
