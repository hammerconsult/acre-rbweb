package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.BorderoLiberacaoFinanceira;
import br.com.webpublico.entidades.LiberacaoCotaFinanceira;
import br.com.webpublico.enums.SituacaoItemBordero;
import br.com.webpublico.util.Util;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BorderoLiberacaoFinanceiraSetter implements BatchPreparedStatementSetter {

    public static final String SQL_UPDATE = "update borderolibcotafinanceira\n" +
        "set situacaoitembordero = ?,\n" +
        "    valor               = ?,\n" +
        "    tipooperacaopagto   = ?\n" +
        "where id = ?";

    public static final String SQL_INSERT = "insert into borderolibcotafinanceira (bordero_id, libcotafinanceira_id, id, situacaoitembordero, tipooperacaopagto, valor)\n" +
        "    values (?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE_SITUACAO = "update borderolibcotafinanceira set situacaoitembordero = ? where id = ?";

    public static final String SQL_INSERT_AUD = "insert into borderolibcotafinanceira_aud(bordero_id, libcotafinanceira_id, id, situacaoitembordero, tipooperacaopagto,\n" +
        "                                     valor, rev, revtype)\n" +
        "values (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String SQL_DELETE = "DELETE FROM borderolibcotafinanceira WHERE ID = ?";
    public static final String SQL_DELETE_AUD = "DELETE FROM borderolibcotafinanceira_aud WHERE ID = ?";
    private final LiberacaoCotaFinanceira liberacaoCotaFinanceira;
    private final Long id;
    private Long idRev;
    private Integer typeRev;
    private final Long idBordero;

    public BorderoLiberacaoFinanceiraSetter(BorderoLiberacaoFinanceira borderoLiberacaoFinanceira, Long id) {
        this.liberacaoCotaFinanceira = borderoLiberacaoFinanceira.getLiberacaoCotaFinanceira();
        this.id = id;
        this.idBordero = borderoLiberacaoFinanceira.getBordero().getId();
    }

    public BorderoLiberacaoFinanceiraSetter(BorderoLiberacaoFinanceira borderoLiberacaoFinanceira, Long id, Long idRev, Integer typeRev) {
        this.liberacaoCotaFinanceira = borderoLiberacaoFinanceira.getLiberacaoCotaFinanceira();
        this.id = id;
        this.idBordero = borderoLiberacaoFinanceira.getBordero().getId();
        this.idRev = idRev;
        this.typeRev = typeRev;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, idBordero);
        ps.setLong(2, liberacaoCotaFinanceira.getId());
        ps.setLong(3, id);
        ps.setString(4, SituacaoItemBordero.BORDERO.name());
        ps.setString(5, liberacaoCotaFinanceira.getTipoOperacaoPagto().name());
        ps.setBigDecimal(6, liberacaoCotaFinanceira.getValor());
        if (Util.isNotNull(idRev)) {
            ps.setLong(7, idRev);
            ps.setInt(8, typeRev);
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
