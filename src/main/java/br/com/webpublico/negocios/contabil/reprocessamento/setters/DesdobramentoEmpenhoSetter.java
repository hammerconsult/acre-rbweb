package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.DesdobramentoEmpenho;
import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.repositorios.jdbc.util.JDBCUtil;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public class DesdobramentoEmpenhoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = " INSERT INTO DESDOBRAMENTOEMPENHO " +
        " (ID, EMPENHO_ID, CONTA_ID, EVENTOCONTABIL_ID, VALOR, SALDO, DESDOBRAMENTOOBRIGACAOPAGAR_ID, DESDOBRAMENTOEMPENHO_ID) " +
        " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE = " UPDATE DESDOBRAMENTOEMPENHO " +
        " SET EMPENHO_ID = ?, CONTA_ID = ?, EVENTOCONTABIL_ID = ?, VALOR = ?, SALDO = ?, DESDOBRAMENTOOBRIGACAOPAGAR_ID = ?, DESDOBRAMENTOEMPENHO_ID = ?" +
        "  WHERE ID = ?";

    private List<DesdobramentoEmpenho> objs;
    private final SingletonGeradorId geradorDeIds;

    public DesdobramentoEmpenhoSetter(List<DesdobramentoEmpenho> objs, SingletonGeradorId geradorDeIds) {
        this.objs = objs;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        DesdobramentoEmpenho obj = objs.get(i);
        obj.setId(geradorDeIds.getProximoId());

        ps.setLong(1, obj.getId());
        ps.setLong(2, obj.getEmpenho().getId());
        JDBCUtil.atribuirLong(ps, 3, obj.getConta() != null ? obj.getConta().getId() : null);
        JDBCUtil.atribuirLong(ps, 4, obj.getEventoContabil() != null ? obj.getEventoContabil().getId() : null);
        ps.setBigDecimal(5, obj.getValor());
        ps.setBigDecimal(6, obj.getSaldo());
        JDBCUtil.atribuirLong(ps, 7, obj.getDesdobramentoObrigacaoPagar() != null ? obj.getDesdobramentoObrigacaoPagar().getId() : null);
        JDBCUtil.atribuirLong(ps, 8, obj.getDesdobramentoEmpenho() != null ? obj.getDesdobramentoEmpenho().getId() : null);
    }

    @Override
    public int getBatchSize() {
        return objs.size();
    }
}
