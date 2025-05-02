package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.ProvisaoPPADespesa;
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
public class ProvisaoPPADespesaSetter implements BatchPreparedStatementSetter {


    public static final String SQL_INSERT = "INSERT INTO PROVISAOPPADESPESA " +
        " (ID, CODIGO, DATAREGISTRO, SOMENTELEITURA, TIPODESPESAORC, VALOR, CONTADEDESPESA_ID, ORIGEM_ID, UNIDADEORGANIZACIONAL_ID, SUBACAOPPA_ID) " +
        " VALUES " +
        "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE = "UPDATE PROVISAOPPADESPESA " +
        " SET CODIGO = ?, DATAREGISTRO = ?, SOMENTELEITURA = ?, TIPODESPESAORC = ?, VALOR = ?, CONTADEDESPESA_ID = ?, ORIGEM_ID = ?, UNIDADEORGANIZACIONAL_ID = ?, SUBACAOPPA_ID = ? " +
        " WHERE ID = ? ";

    private final List<ProvisaoPPADespesa> fontes;
    private final SingletonGeradorId geradorDeIds;

    public ProvisaoPPADespesaSetter(List<ProvisaoPPADespesa> fontes, SingletonGeradorId geradorDeIds) {
        this.fontes = fontes;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ProvisaoPPADespesa fonte = fontes.get(i);
        fonte.setId(geradorDeIds.getProximoId());

        ps.setLong(1, fonte.getId());
        ps.setString(2, fonte.getCodigo());
        ps.setTimestamp(3, new java.sql.Timestamp(fonte.getDataRegistro().getTime()));
        ps.setBoolean(4, fonte.getSomenteLeitura());
        ps.setString(5, fonte.getTipoDespesaORC().name());
        ps.setBigDecimal(6, fonte.getValor());
        ps.setLong(7, fonte.getContaDeDespesa().getId());

        if (fonte.getOrigem() != null) {
            ps.setLong(8, fonte.getOrigem().getId());
        } else {
            ps.setNull(8, Types.NUMERIC);
        }
        ps.setLong(9, fonte.getUnidadeOrganizacional().getId());
        ps.setLong(10, fonte.getSubAcaoPPA().getId());

    }

    @Override
    public int getBatchSize() {
        return fontes.size();
    }
}
