package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.BorderoPagamento;
import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.enums.SituacaoItemBordero;
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
public class BorderoPagamentoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_UPDATE = "update borderopagamento set  situacaoitembordero = ?, \n" +
        "pagamento_id = ?, valor = ?, \n" +
        "contacorrentefavorecido_id = ?,\n" +
        "tipooperacaopagto = ?\n" +
        "where id = ?";

    public static final String SQL_INSERT = "insert into borderopagamento (bordero_id, pagamento_id, id, situacaoitembordero, contacorrentefavorecido_id,\n" +
        "                              tipooperacaopagto, valor)\n" +
        "values (?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE_SITUACAO = "update borderopagamento set  situacaoitembordero = ? where id = ?";

    public static final String SQL_INSERT_REV = "insert into borderopagamento_aud (bordero_id, pagamento_id, id, situacaoitembordero, contacorrentefavorecido_id,\n" +
        "                                                            tipooperacaopagto, valor, rev, revtype)\n" +
        "                              values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_DELETE = "DELETE FROM borderopagamento WHERE ID = ?";
    public static final String SQL_DELETE_AUD = "DELETE FROM borderopagamento_aud WHERE ID = ?";

    private final Pagamento pagamento;
    private final Long id;
    private final Long idBordero;
    private Long idRev;

    private Integer typeRev;

    public BorderoPagamentoSetter(BorderoPagamento borderoPagamento, Long id) {
        this.pagamento = borderoPagamento.getPagamento();
        this.id = id;
        this.idBordero = borderoPagamento.getBordero().getId();
    }

    public BorderoPagamentoSetter(BorderoPagamento borderoPagamento, Long id, Long idRev, Integer typeRev) {
        this.pagamento = borderoPagamento.getPagamento();
        this.id = id;
        this.idBordero = borderoPagamento.getBordero().getId();
        this.idRev = idRev;
        this.typeRev = typeRev;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, idBordero);
        ps.setLong(2, pagamento.getId());
        ps.setLong(3, id);
        ps.setString(4, SituacaoItemBordero.BORDERO.name());
        if (Util.isNotNull(pagamento.getContaPgto())) {
            ps.setLong(5, pagamento.getContaPgto().getId());
        } else {
            ps.setNull(5, Types.LONGNVARCHAR);
        }
        ps.setString(6, pagamento.getTipoOperacaoPagto().name());
        ps.setBigDecimal(7, pagamento.getValorFinal());
        if (Util.isNotNull(idRev)) {
            ps.setLong(8, idRev);
            ps.setInt(9, typeRev);
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
