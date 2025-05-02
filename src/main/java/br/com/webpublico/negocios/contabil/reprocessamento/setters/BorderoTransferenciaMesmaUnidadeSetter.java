package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.BorderoTransferenciaMesmaUnidade;
import br.com.webpublico.entidades.TransferenciaMesmaUnidade;
import br.com.webpublico.enums.SituacaoItemBordero;
import br.com.webpublico.util.Util;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BorderoTransferenciaMesmaUnidadeSetter implements BatchPreparedStatementSetter {

    public static final String SQL_UPDATE = "update borderotransfmesmaunidade\n" +
        "set situacaoitembordero = ?,\n" +
        "    transfmesmaunidade_id = ?,\n" +
        "    valor               = ?,\n" +
        "    tipooperacaopagto   = ?\n" +
        "where id = ?";

    public static final String SQL_INSERT_AUD = "insert into borderotransfmesmaunidade_aud (bordero_id, transfmesmaunidade_id, id, situacaoitembordero,\n" +
        "                                           tipooperacaopagto, valor, rev, revtype)\n" +
        "values (?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_INSERT = "insert into borderotransfmesmaunidade (bordero_id, transfmesmaunidade_id, id, situacaoitembordero,\n" +
        "                              tipooperacaopagto, valor)\n" +
        "values (?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE_SITUACAO = "update borderotransfmesmaunidade set  situacaoitembordero = ? where id = ?";
    public static final String SQL_DELETE = "DELETE FROM borderotransfmesmaunidade WHERE ID = ?";
    public static final String SQL_DELETE_AUD = "DELETE FROM borderotransfmesmaunidade_aud WHERE ID = ?";
    private final TransferenciaMesmaUnidade transferenciaMesmaUnidade;
    private final Long id;
    private final Long idBordero;
    private Long idRev;
    private Integer typeRev;

    public BorderoTransferenciaMesmaUnidadeSetter(BorderoTransferenciaMesmaUnidade transferenciaMesmaUnidade, Long id) {
        this.transferenciaMesmaUnidade = transferenciaMesmaUnidade.getTransferenciaMesmaUnidade();
        this.id = id;
        this.idBordero = transferenciaMesmaUnidade.getBordero().getId();
    }

    public BorderoTransferenciaMesmaUnidadeSetter(BorderoTransferenciaMesmaUnidade transferenciaMesmaUnidade, Long id, Long idRev, Integer typeRev) {
        this.transferenciaMesmaUnidade = transferenciaMesmaUnidade.getTransferenciaMesmaUnidade();
        this.id = id;
        this.idBordero = transferenciaMesmaUnidade.getBordero().getId();
        this.idRev = idRev;
        this.typeRev = typeRev;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, idBordero);
        ps.setLong(2, transferenciaMesmaUnidade.getId());
        ps.setLong(3, id);
        ps.setString(4, SituacaoItemBordero.BORDERO.name());
        if(Util.isNotNull(transferenciaMesmaUnidade.getTipoOperacaoPagto())) {
            ps.setString(5, transferenciaMesmaUnidade.getTipoOperacaoPagto().name());
        }else{
            ps.setNull(5, Types.VARCHAR);
        }
        ps.setBigDecimal(6, transferenciaMesmaUnidade.getValor());
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
