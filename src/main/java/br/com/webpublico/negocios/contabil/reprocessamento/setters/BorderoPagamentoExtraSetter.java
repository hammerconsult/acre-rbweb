package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.BorderoPagamentoExtra;
import br.com.webpublico.entidades.PagamentoExtra;
import br.com.webpublico.enums.SituacaoItemBordero;
import br.com.webpublico.util.Util;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BorderoPagamentoExtraSetter implements BatchPreparedStatementSetter {

    public static final String SQL_UPDATE = "update borderopagamentoextra\n" +
        "set situacaoitembordero        = ?,\n" +
        "    pagamentoextra_id          = ?,\n" +
        "    valor                      = ?,\n" +
        "    contacorrentefavorecido_id = ?,\n" +
        "    tipooperacaopagto          = ?\n" +
        "where id = ?";

    public static final String SQL_INSERT = "insert into borderopagamentoextra (bordero_id, pagamentoextra_id, id, situacaoitembordero, contacorrentefavorecido_id,\n" +
        "                              tipooperacaopagto, valor)\n" +
        "values (?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE_SITUACAO = "update borderopagamentoextra set situacaoitembordero = ? where id = ?";

    public static final String SQL_INSERT_AUD = "insert into borderopagamentoextra_aud (bordero_id, pagamentoextra_id, id, situacaoitembordero, contacorrentefavorecido_id,\n" +
        "                                   tipooperacaopagto, valor, rev, revtype)\n" +
        "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_DELETE = "DELETE FROM borderopagamentoextra WHERE ID = ?";
    public static final String SQL_DELETE_AUD = "DELETE FROM borderopagamentoextra_aud WHERE ID = ?";

    private final PagamentoExtra pagamentoExtra;
    private final Long id;
    private final Long idBordero;
    private Long idRev;
    private Integer typeRev;

    public BorderoPagamentoExtraSetter(BorderoPagamentoExtra borderoPagamentoExtra, Long id) {
        this.pagamentoExtra = borderoPagamentoExtra.getPagamentoExtra();
        this.id = id;
        this.idBordero = borderoPagamentoExtra.getBordero().getId();
    }

    public BorderoPagamentoExtraSetter(BorderoPagamentoExtra borderoPagamentoExtra, Long id, Long idRev, Integer typeRev) {
        this.pagamentoExtra = borderoPagamentoExtra.getPagamentoExtra();
        this.id = id;
        this.idBordero = borderoPagamentoExtra.getBordero().getId();
        this.idRev = idRev;
        this.typeRev = typeRev;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, idBordero);
        ps.setLong(2, pagamentoExtra.getId());
        ps.setLong(3, id);
        ps.setString(4, SituacaoItemBordero.BORDERO.name());
        if(Util.isNotNull(pagamentoExtra.getContaCorrenteBancaria())) {
            ps.setLong(5, pagamentoExtra.getContaCorrenteBancaria().getId());
        }else {
            ps.setNull(5, Types.LONGNVARCHAR);
        }
        ps.setString(6, pagamentoExtra.getTipoOperacaoPagto().name());
        ps.setBigDecimal(7, pagamentoExtra.getValor());
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
