package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.BorderoTransferenciaFinanceira;
import br.com.webpublico.entidades.TransferenciaContaFinanceira;
import br.com.webpublico.enums.SituacaoItemBordero;
import br.com.webpublico.util.Util;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BorderoTransferenciaFinanceiraSetter implements BatchPreparedStatementSetter {

    public static final String SQL_UPDATE = "update borderotransffinanceira\n" +
        "set situacaoitembordero = ?,\n" +
        "    transffinanceira_id = ?,\n" +
        "    valor               = ?,\n" +
        "    tipooperacaopagto   = ?\n" +
        "where id = ?";

    public static final String SQL_INSERT = "insert into borderotransffinanceira (bordero_id, transffinanceira_id, id, situacaoitembordero,\n" +
        "                              tipooperacaopagto, valor)\n" +
        "values (?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE_SITUACAO = "update borderotransffinanceira set  situacaoitembordero = ? where id = ?";
    public static final String SQL_INSERT_AUD = "insert into borderotransffinanceira_aud (bordero_id, transffinanceira_id, id, situacaoitembordero, tipooperacaopagto, valor, rev, revtype)\n" +
        "values (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String SQL_DELETE = "DELETE FROM borderotransffinanceira WHERE ID = ?";
    public static final String SQL_DELETE_AUD = "DELETE FROM borderotransffinanceira_aud WHERE ID = ?";
    private final TransferenciaContaFinanceira transferenciaFinanceira;
    private final Long id;
    private final Long idBordero;

    private Long idRev;
    private Integer typeRev;

    public BorderoTransferenciaFinanceiraSetter(BorderoTransferenciaFinanceira borderoTransferenciaFinanceira, Long id) {
        this.transferenciaFinanceira = borderoTransferenciaFinanceira.getTransferenciaContaFinanceira();
        this.id = id;
        this.idBordero = borderoTransferenciaFinanceira.getBordero().getId();
    }

    public BorderoTransferenciaFinanceiraSetter(BorderoTransferenciaFinanceira borderoTransferenciaFinanceira, Long id, Long idRev, Integer typeRev) {
        this.transferenciaFinanceira = borderoTransferenciaFinanceira.getTransferenciaContaFinanceira();
        this.id = id;
        this.idBordero = borderoTransferenciaFinanceira.getBordero().getId();
        this.idRev = idRev;
        this.typeRev = typeRev;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {

        ps.setLong(1, idBordero);
        ps.setLong(2, transferenciaFinanceira.getId());
        ps.setLong(3, id);
        ps.setString(4, SituacaoItemBordero.BORDERO.name());
        if(Util.isNotNull(transferenciaFinanceira.getTipoOperacaoPagto())) {
            ps.setString(5, transferenciaFinanceira.getTipoOperacaoPagto().name());
        }else{
            ps.setNull(5, Types.VARCHAR);
        }
        ps.setBigDecimal(6, transferenciaFinanceira.getValor());
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
