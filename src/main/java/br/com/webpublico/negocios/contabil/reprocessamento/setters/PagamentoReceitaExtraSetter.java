package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.PagamentoReceitaExtra;
import br.com.webpublico.util.Util;
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
public class PagamentoReceitaExtraSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT_AUD = "insert into pagamentoreceitaextra_aud (pagamentoextra_id, receitaextra_id, pagamentoestornorecextra_id, rev, revtype, id) " +
        "values (?, ?, ?, ?, ?, ?)";

    public static final String SQL_INSERT = "insert into pagamentoreceitaextra (pagamentoextra_id, receitaextra_id, pagamentoestornorecextra_id, id) " +
        "values (?, ?, ?, ?)";

    public static final String SQL_UPDATE_ALL = "update pagamentoreceitaextra " +
        "set pagamentoextra_id           = ?, " +
        "    receitaextra_id             = ?, " +
        "    pagamentoestornorecextra_id = ? " +
        "where id = ?";
    public static final String SQL_DELETE = "delete pagamentoreceitaextra where id = ?";

    private final PagamentoReceitaExtra pagamentoReceitaExtra;
    private final Long id;
    private Long idRev;
    private Integer typeRev;

    public PagamentoReceitaExtraSetter(PagamentoReceitaExtra pagamentoReceitaExtra, Long id) {
        this.pagamentoReceitaExtra = pagamentoReceitaExtra;
        this.id = id;
    }

    public PagamentoReceitaExtraSetter(PagamentoReceitaExtra pagamentoReceitaExtra, Long id, Long idRev, Integer typeRev) {
        this.pagamentoReceitaExtra = pagamentoReceitaExtra;
        this.id = id;
        this.idRev = idRev;
        this.typeRev = typeRev;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        if (Util.isNotNull(pagamentoReceitaExtra.getPagamentoExtra())) {
            ps.setLong(1, pagamentoReceitaExtra.getPagamentoExtra().getId());
        } else {
            ps.setNull(1, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(pagamentoReceitaExtra.getReceitaExtra())) {
            ps.setLong(2, pagamentoReceitaExtra.getReceitaExtra().getId());
        } else {
            ps.setNull(2, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(pagamentoReceitaExtra.getPagamentoEstornoRecExtra())) {
            ps.setLong(3, pagamentoReceitaExtra.getPagamentoEstornoRecExtra().getId());
        } else {
            ps.setNull(3, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(idRev)) {
            ps.setLong(4, idRev);
            ps.setInt(5, typeRev);
            ps.setLong(6, id);
        } else {
            ps.setLong(4, id);
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
