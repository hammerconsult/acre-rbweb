package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.RetencaoPgto;
import br.com.webpublico.entidades.contabil.execucao.DesdobramentoPagamento;
import br.com.webpublico.util.Util;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
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
public class DesdobramentoPagamentoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT_AUD = "insert into desdobramentopagamento_aud (pagamento_id, desdobramento_id, valor, saldo, rev, revtype, id) \n" +
        "values (?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE_ALL = "update desdobramentopagamento\n" +
        "set pagamento_id     = ?,\n" +
        "    desdobramento_id = ?,\n" +
        "    valor            = ?,\n" +
        "    saldo            = ?\n" +
        "where id = ?";

    public static final String SQL_INSERT = "insert into desdobramentopagamento (pagamento_id, desdobramento_id, valor, saldo, id) \n" +
        "values (?, ?, ?, ?, ?)";
    public static final String SQL_DELETE = "delete desdobramentopagamento where id = ?";
    private final DesdobramentoPagamento desdobramentoPagamento;
    private final Long id;
    private Long idRev;

    private Integer typeRev;

    public DesdobramentoPagamentoSetter(DesdobramentoPagamento desdobramentoPagamento, Long id) {
        this.desdobramentoPagamento = desdobramentoPagamento;
        this.id = id;
    }

    public DesdobramentoPagamentoSetter(DesdobramentoPagamento desdobramentoPagamento, Long id, Long idRev, Integer typeRev) {
        this.desdobramentoPagamento = desdobramentoPagamento;
        this.id = id;
        this.idRev = idRev;
        this.typeRev = typeRev;
    }


    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, desdobramentoPagamento.getPagamento().getId());
        ps.setLong(2, desdobramentoPagamento.getDesdobramento().getId());
        if(Util.isNotNull(desdobramentoPagamento.getValor())){
            ps.setBigDecimal(3, desdobramentoPagamento.getValor());
        }else{
            ps.setNull(3, Types.DECIMAL);
        }
        if(Util.isNotNull(desdobramentoPagamento.getSaldo())){
            ps.setBigDecimal(4, desdobramentoPagamento.getSaldo());
        }else{
            ps.setNull(4, Types.DECIMAL);
        }
        if (Util.isNotNull(idRev)) {
            ps.setLong(5, idRev);
            ps.setInt(6, typeRev);
            ps.setLong(7, id);
        }else {
            ps.setLong(5, id);
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
