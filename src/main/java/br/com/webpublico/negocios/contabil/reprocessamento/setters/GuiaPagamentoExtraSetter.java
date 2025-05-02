package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.GuiaPagamentoExtra;
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
public class GuiaPagamentoExtraSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT_AUD = "insert into guiapagamentoextra_aud (pagamentoextra_id, guiafatura_id, guiaconvenio_id, guiagps_id, guiadarf_id,    " +
        "                                guiadarfsimples_id, situacaoguiapagamento, numeroautenticacao, pessoa_id,    " +
        "                                tipoidentificacaoguia, codigoidentificacao, datapagamento, guiagru_id, rev, revtype, id)    " +
        "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE_ALL = "update guiapagamentoextra " +
        "set pagamentoextra_id = ?, " +
        "    guiafatura_id = ?, " +
        "    guiaconvenio_id = ?, " +
        "    guiagps_id = ?, " +
        "    guiadarf_id = ?, " +
        "    guiadarfsimples_id = ?, " +
        "    situacaoguiapagamento = ?, " +
        "    numeroautenticacao = ?, " +
        "    pessoa_id = ?, " +
        "    tipoidentificacaoguia = ?, " +
        "    codigoidentificacao = ?, " +
        "    datapagamento = ?, " +
        "    guiagru_id = ? " +
        "    where id = ?";

    public static final String SQL_INSERT = "insert into guiapagamentoextra (pagamentoextra_id, guiafatura_id, guiaconvenio_id, guiagps_id, guiadarf_id,     + " +
        "                                         guiadarfsimples_id, situacaoguiapagamento, numeroautenticacao, pessoa_id,     + " +
        "                                         tipoidentificacaoguia, codigoidentificacao, datapagamento, guiagru_id, id)     + " +
        "         values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_DELETE = "delete guiaPagamentoExtra where id = ?";
    private final GuiaPagamentoExtra guiaPagamentoExtra;
    private final Long id;
    private Long idRev;

    private Integer typeRev;

    public GuiaPagamentoExtraSetter(GuiaPagamentoExtra guiaPagamentoExtra, Long id) {
        this.guiaPagamentoExtra = guiaPagamentoExtra;
        this.id = id;
    }

    public GuiaPagamentoExtraSetter(GuiaPagamentoExtra guiaPagamentoExtra, Long id, Long idRev, Integer typeRev) {
        this.guiaPagamentoExtra = guiaPagamentoExtra;
        this.id = id;
        this.idRev = idRev;
        this.typeRev = typeRev;
    }


    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        if (Util.isNotNull(guiaPagamentoExtra.getPagamentoExtra())) {
            ps.setLong(1, guiaPagamentoExtra.getPagamentoExtra().getId());
        } else {
            ps.setNull(1, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(guiaPagamentoExtra.getGuiaFatura())) {
            ps.setLong(2, guiaPagamentoExtra.getGuiaFatura().getId());
        } else {
            ps.setNull(2, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(guiaPagamentoExtra.getGuiaConvenio())) {
            ps.setLong(3, guiaPagamentoExtra.getGuiaConvenio().getId());
        } else {
            ps.setNull(3, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(guiaPagamentoExtra.getGuiaGPS())) {
            ps.setLong(4, guiaPagamentoExtra.getGuiaGPS().getId());
        } else {
            ps.setNull(4, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(guiaPagamentoExtra.getGuiaDARF())) {
            ps.setLong(5, guiaPagamentoExtra.getGuiaDARF().getId());
        } else {
            ps.setNull(5, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(guiaPagamentoExtra.getGuiaDARFSimples())) {
            ps.setLong(6, guiaPagamentoExtra.getGuiaDARFSimples().getId());
        } else {
            ps.setNull(6, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(guiaPagamentoExtra.getSituacaoGuiaPagamento())) {
            ps.setString(7, guiaPagamentoExtra.getSituacaoGuiaPagamento().name());
        } else {
            ps.setNull(7, Types.VARCHAR);
        }
        if (Util.isNotNull(guiaPagamentoExtra.getNumeroAutenticacao())) {
            ps.setString(8, guiaPagamentoExtra.getNumeroAutenticacao());
        } else {
            ps.setNull(8, Types.VARCHAR);
        }
        if(Util.isNotNull(guiaPagamentoExtra.getPessoa())) {
            ps.setLong(9, guiaPagamentoExtra.getPessoa().getId());
        }else{
            ps.setNull(9, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(guiaPagamentoExtra.getTipoIdentificacaoGuia())) {
            ps.setString(10, guiaPagamentoExtra.getTipoIdentificacaoGuia().name());
        }else{
            ps.setNull(10, Types.VARCHAR);
        }
        if(Util.isNotNull(guiaPagamentoExtra.getCodigoIdentificacao())) {
            ps.setString(11, guiaPagamentoExtra.getCodigoIdentificacao());
        }else{
            ps.setNull(11, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(guiaPagamentoExtra.getDataPagamento())) {
            ps.setDate(12, new Date(guiaPagamentoExtra.getDataPagamento().getTime()));
        }else {
            ps.setNull(12, Types.DATE);
        }
        if (Util.isNotNull(guiaPagamentoExtra.getGuiaGRU())) {
            ps.setLong(13, guiaPagamentoExtra.getGuiaGRU().getId());
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
