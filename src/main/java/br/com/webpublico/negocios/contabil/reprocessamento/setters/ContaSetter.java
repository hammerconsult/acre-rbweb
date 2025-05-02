package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.Conta;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 29/10/14
 * Time: 10:14
 * To change this template use File | Settings | File Templates.
 */
public class ContaSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = "INSERT INTO CONTA " +
            "( ID, ATIVA, CODIGO, DATAREGISTRO, DESCRICAO, FUNCAO, " +
            " PERMITIRDESDOBRAMENTO, RUBRICA, TIPOCONTACONTABIL, " +
            " PLANODECONTAS_ID, SUPERIOR_ID, DTYPE, EXERCICIO_ID, " +
            " CATEGORIA, CODIGOTCE, codigoSICONFI)" +
            " VALUES " +
            "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final Conta conta;
    private final Long id;

    public ContaSetter(Conta conta, Long id) {
        this.conta = conta;
        this.id = id;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, id);
        if (conta.getAtiva() != null) {
            ps.setBoolean(2, conta.getAtiva());
        } else {
            ps.setBoolean(2, Boolean.TRUE);
        }
        ps.setString(3, conta.getCodigo());
        ps.setDate(4, new java.sql.Date(conta.getDataRegistro().getTime()));
        ps.setString(5, conta.getDescricao());
        ps.setString(6, conta.getFuncao());
        ps.setBoolean(7, conta.getPermitirDesdobramento());
        ps.setString(8, conta.getRubrica());
        if (conta.getTipoContaContabil() != null) {
            ps.setString(9, conta.getTipoContaContabil().name());
        } else {
            ps.setNull(9, Types.VARCHAR);
        }
        if (conta.getPlanoDeContas() != null) {
            ps.setLong(10, conta.getPlanoDeContas().getId());
        } else {
            ps.setLong(10, java.sql.Types.NUMERIC);
        }
        if (conta.getSuperior() != null && conta.getSuperior().getId() != null) {
            ps.setLong(11, conta.getSuperior().getId());
        } else {
            ps.setLong(11, java.sql.Types.NUMERIC);
        }
        ps.setString(12, conta.getdType());
        ps.setLong(13, conta.getExercicio().getId());
        if (conta.getCategoria() != null) {
            ps.setString(14, conta.getCategoria().name());
        } else {
            ps.setNull(14, Types.VARCHAR);
        }
        if (conta.getCodigoTCE() != null) {
            ps.setString(15, conta.getCodigoTCE());
        } else {
            ps.setNull(15, Types.VARCHAR);
        }
        if (conta.getCodigoSICONFI() != null) {
            ps.setString(16, conta.getCodigoSICONFI());
        } else {
            ps.setNull(16, Types.VARCHAR);
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
