package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.GuiaPagamento;
import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.GuiaPagamentoRowMapper;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.GuiaPagamentoSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 29/10/14
 * Time: 09:34
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class JdbcGuiaPagamento extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcGuiaPagamento(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Autowired
    private SingletonGeradorId geradorDeIds;

    public GuiaPagamento salvar(GuiaPagamento guiaPagamento, Long idRev) {
        if (Util.isNotNull(guiaPagamento)) {
            guiaPagamento.setId(geradorDeIds.getProximoId());
            getJdbcTemplate().batchUpdate(GuiaPagamentoSetter.SQL_INSERT, new GuiaPagamentoSetter(guiaPagamento, guiaPagamento.getId()));
            getJdbcTemplate().batchUpdate(GuiaPagamentoSetter.SQL_INSERT_AUD, new GuiaPagamentoSetter(guiaPagamento, guiaPagamento.getId(), idRev, 0));
        }
        return guiaPagamento;
    }

    public GuiaPagamento atualizar(GuiaPagamento guiaPagamento, Long idRev) {
        if (Util.isNotNull(guiaPagamento)) {
            getJdbcTemplate().batchUpdate(GuiaPagamentoSetter.SQL_UPDATE_ALL, new GuiaPagamentoSetter(guiaPagamento, guiaPagamento.getId()));
            getJdbcTemplate().batchUpdate(GuiaPagamentoSetter.SQL_INSERT_AUD, new GuiaPagamentoSetter(guiaPagamento, guiaPagamento.getId(), idRev, 1));
        }
        return guiaPagamento;
    }

    public List<GuiaPagamento> buscarGuiaPagamentoPorPagamento(Pagamento pagamento) {
        String sql = "select gp.id, " +
            "       gp.pagamento_id, " +
            "       gp.guiafatura_id, " +
            "       gp.guiaconvenio_id, " +
            "       gp.guiagps_id, " +
            "       gp.guiadarf_id, " +
            "       gp.guiadarfsimples_id, " +
            "       gp.situacaoguiapagamento, " +
            "       gp.numeroautenticacao, " +
            "       gp.pessoa_id, " +
            "       gp.tipoidentificacaoguia, " +
            "       gp.codigoidentificacao, " +
            "       gp.datapagamento, " +
            "       gp.guiagru_id " +
            "from guiapagamento gp where gp.pagamento_id = ?";
        List<GuiaPagamento> guias = getJdbcTemplate().query(sql, new Object[]{pagamento.getId()}, new GuiaPagamentoRowMapper());
        if (!guias.isEmpty()) {
            return guias;
        } else {
            return null;
        }
    }

    public void delete(GuiaPagamento guiaPagamento, Long idRev) {
        Object guiaPagamentoId = guiaPagamento.getId();
        getJdbcTemplate().update(GuiaPagamentoSetter.SQL_DELETE, guiaPagamentoId);
        getJdbcTemplate().batchUpdate(GuiaPagamentoSetter.SQL_INSERT_AUD, new GuiaPagamentoSetter(guiaPagamento, guiaPagamento.getId(), idRev, 2));
    }
}
