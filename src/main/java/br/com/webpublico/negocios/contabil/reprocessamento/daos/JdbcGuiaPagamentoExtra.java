package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.GuiaPagamentoExtra;
import br.com.webpublico.entidades.PagamentoExtra;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.GuiaPagamentoExtraRowMapper;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.GuiaPagamentoExtraSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

@Repository
public class JdbcGuiaPagamentoExtra extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcGuiaPagamentoExtra(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Autowired
    private SingletonGeradorId geradorDeIds;

    public GuiaPagamentoExtra atualizar(GuiaPagamentoExtra guiaPagamentoExtra, Long idRev) {
        if (Util.isNotNull(guiaPagamentoExtra)) {
            getJdbcTemplate().batchUpdate(GuiaPagamentoExtraSetter.SQL_UPDATE_ALL, new GuiaPagamentoExtraSetter(guiaPagamentoExtra, guiaPagamentoExtra.getId()));
            getJdbcTemplate().batchUpdate(GuiaPagamentoExtraSetter.SQL_INSERT_AUD, new GuiaPagamentoExtraSetter(guiaPagamentoExtra, guiaPagamentoExtra.getId(), idRev, 1));
        }
        return guiaPagamentoExtra;
    }

    public GuiaPagamentoExtra salvar(GuiaPagamentoExtra guiaPagamentoExtra, Long idRev) {
        if (Util.isNotNull(guiaPagamentoExtra)) {
            guiaPagamentoExtra.setId(geradorDeIds.getProximoId());
            getJdbcTemplate().batchUpdate(GuiaPagamentoExtraSetter.SQL_INSERT, new GuiaPagamentoExtraSetter(guiaPagamentoExtra, guiaPagamentoExtra.getId()));
            getJdbcTemplate().batchUpdate(GuiaPagamentoExtraSetter.SQL_INSERT_AUD, new GuiaPagamentoExtraSetter(guiaPagamentoExtra, guiaPagamentoExtra.getId(), idRev, 0));
        }
        return guiaPagamentoExtra;
    }

    public List<GuiaPagamentoExtra> buscarGuiaPagamentoExtraPorPagamentoExtra(PagamentoExtra pagamentoExtra) {
        String sql = "select id , " +
            "       pagamentoextra_id , " +
            "       guiafatura_id  , " +
            "       guiaconvenio_id , " +
            "       guiagps_id , " +
            "       guiadarf_id , " +
            "       guiadarfsimples_id , " +
            "       situacaoguiapagamento , " +
            "       numeroautenticacao , " +
            "       pessoa_id , " +
            "       tipoidentificacaoguia , " +
            "       codigoidentificacao , " +
            "       datapagamento , " +
            "       guiagru_id  from guiapagamentoextra where pagamentoextra_id = ?";

        List<GuiaPagamentoExtra> guias = getJdbcTemplate().query(sql, new Object[]{pagamentoExtra.getId()}, new GuiaPagamentoExtraRowMapper());
        if (!guias.isEmpty()) {
            return guias;
        } else {
            return null;
        }
    }

    public void delete(GuiaPagamentoExtra guiaPagamentoExtra, Long idRev) {
        if (Util.isNotNull(guiaPagamentoExtra)) {
            Object idPagamentoReceitaExtra = guiaPagamentoExtra.getId();
            getJdbcTemplate().update(GuiaPagamentoExtraSetter.SQL_DELETE, idPagamentoReceitaExtra);
            getJdbcTemplate().batchUpdate(GuiaPagamentoExtraSetter.SQL_INSERT_AUD, new GuiaPagamentoExtraSetter(guiaPagamentoExtra, guiaPagamentoExtra.getId(), idRev, 2));
        }
    }
}
