package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.GuiaPagamento;
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
public class GuiaPagamentoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT_AUD = "insert into guiapagamento_aud (pagamento_id, guiafatura_id, guiaconvenio_id, guiagps_id, guiadarf_id, guiadarfsimples_id,\n" +
        "                          situacaoguiapagamento, numeroautenticacao, pessoa_id, tipoidentificacaoguia,\n" +
        "                          codigoidentificacao, datapagamento, guiagru_id, rev, revtype, id)\n" +
        "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE_ALL = "update guiapagamento\n" +
        "set pagamento_id = ?,\n" +
        "    guiafatura_id = ?,\n" +
        "    guiaconvenio_id = ?,\n" +
        "    guiagps_id = ?,\n" +
        "    guiadarf_id = ?,\n" +
        "    guiadarfsimples_id = ?,\n" +
        "    situacaoguiapagamento = ?,\n" +
        "    numeroautenticacao = ?,\n" +
        "    pessoa_id = ?,\n" +
        "    tipoidentificacaoguia = ?,\n" +
        "    codigoidentificacao = ?,\n" +
        "    datapagamento = ?,\n" +
        "    guiagru_id = ?\n" +
        "where id = ?";

    public static final String SQL_INSERT = "insert into guiapagamento(pagamento_id, guiafatura_id, guiaconvenio_id, guiagps_id, guiadarf_id, guiadarfsimples_id,\n" +
        "                          situacaoguiapagamento, numeroautenticacao, pessoa_id, tipoidentificacaoguia,\n" +
        "                          codigoidentificacao, datapagamento, guiagru_id, id)\n" +
        "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String SQL_DELETE = "delete guiapagamento where id = ?";
    private final GuiaPagamento guiaPagamento;
    private final Long id;
    private Long idRev;

    private Integer typeRev;

    public GuiaPagamentoSetter(GuiaPagamento guiaPagamento, Long id) {
        this.guiaPagamento = guiaPagamento;
        this.id = id;
    }

    public GuiaPagamentoSetter(GuiaPagamento guiaPagamento, Long id, Long idRev, Integer typeRev) {
        this.guiaPagamento = guiaPagamento;
        this.id = id;
        this.idRev = idRev;
        this.typeRev = typeRev;
    }


    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        if (Util.isNotNull(guiaPagamento.getPagamento())) {
            ps.setLong(1, guiaPagamento.getPagamento().getId());
        } else {
            ps.setNull(1, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(guiaPagamento.getGuiaFatura())) {
            ps.setLong(2, guiaPagamento.getGuiaFatura().getId());
        } else {
            ps.setNull(2, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(guiaPagamento.getGuiaConvenio())) {
            ps.setLong(3, guiaPagamento.getGuiaConvenio().getId());
        } else {
            ps.setNull(3, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(guiaPagamento.getGuiaGPS())) {
            ps.setLong(4, guiaPagamento.getGuiaGPS().getId());
        } else {
            ps.setNull(4, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(guiaPagamento.getGuiaDARF())) {
            ps.setLong(5, guiaPagamento.getGuiaDARF().getId());
        } else {
            ps.setNull(5, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(guiaPagamento.getGuiaDARFSimples())) {
            ps.setLong(6, guiaPagamento.getGuiaDARFSimples().getId());
        } else {
            ps.setNull(6, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(guiaPagamento.getSituacaoGuiaPagamento())) {
            ps.setString(7, guiaPagamento.getSituacaoGuiaPagamento().name());
        } else {
            ps.setNull(7, Types.VARCHAR);
        }
        if (Util.isNotNull(guiaPagamento.getNumeroAutenticacao())) {
            ps.setString(8, guiaPagamento.getNumeroAutenticacao());
        } else {
            ps.setNull(8, Types.VARCHAR);
        }
        if (Util.isNotNull(guiaPagamento.getPessoa())) {
            ps.setLong(9, guiaPagamento.getPessoa().getId());
        } else {
            ps.setNull(9, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(guiaPagamento.getTipoIdentificacaoGuia())) {
            ps.setString(10, guiaPagamento.getTipoIdentificacaoGuia().name());
        } else {
            ps.setNull(10, Types.VARCHAR);
        }
        if (Util.isNotNull(guiaPagamento.getCodigoIdentificacao())) {
            ps.setString(11, guiaPagamento.getCodigoIdentificacao());
        } else {
            ps.setNull(11, Types.VARCHAR);
        }
        if (Util.isNotNull(guiaPagamento.getDataPagamento())) {
            ps.setDate(12, new Date(guiaPagamento.getDataPagamento().getTime()));
        } else {
            ps.setNull(12, Types.DATE);
        }
        if (Util.isNotNull(guiaPagamento.getGuiaGRU())) {
            ps.setLong(13, guiaPagamento.getGuiaGRU().getId());
        } else {
            ps.setNull(13, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(idRev)) {
            ps.setLong(14, idRev);
            ps.setInt(15, typeRev);
            ps.setLong(16, id);
        } else {
            ps.setLong(14, id);
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
