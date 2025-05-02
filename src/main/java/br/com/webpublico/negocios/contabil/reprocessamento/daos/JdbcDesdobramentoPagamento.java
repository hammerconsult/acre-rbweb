package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.entidades.contabil.execucao.DesdobramentoPagamento;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.DesdobramentoPagamentoRowMapper;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.DesdobramentoPagamentoSetter;
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
public class JdbcDesdobramentoPagamento extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcDesdobramentoPagamento(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Autowired
    private SingletonGeradorId geradorDeIds;

    public DesdobramentoPagamento salvar(DesdobramentoPagamento desdobramentoPagamento, Long idRev) {
        if (Util.isNotNull(desdobramentoPagamento)) {
            desdobramentoPagamento.setId(geradorDeIds.getProximoId());
            getJdbcTemplate().batchUpdate(DesdobramentoPagamentoSetter.SQL_INSERT, new DesdobramentoPagamentoSetter(desdobramentoPagamento, desdobramentoPagamento.getId()));
            getJdbcTemplate().batchUpdate(DesdobramentoPagamentoSetter.SQL_INSERT_AUD, new DesdobramentoPagamentoSetter(desdobramentoPagamento, desdobramentoPagamento.getId(), idRev, 0));
        }
        return desdobramentoPagamento;
    }

    public DesdobramentoPagamento atualizar(DesdobramentoPagamento desdobramentoPagamento, Long idRev) {
        if (Util.isNotNull(desdobramentoPagamento)) {
            getJdbcTemplate().batchUpdate(DesdobramentoPagamentoSetter.SQL_UPDATE_ALL, new DesdobramentoPagamentoSetter(desdobramentoPagamento, desdobramentoPagamento.getId()));
            getJdbcTemplate().batchUpdate(DesdobramentoPagamentoSetter.SQL_INSERT_AUD, new DesdobramentoPagamentoSetter(desdobramentoPagamento, desdobramentoPagamento.getId(), idRev, 1));
        }
        return desdobramentoPagamento;
    }

    public List<DesdobramentoPagamento> buscarDesdobramentoPorPagamento(Pagamento pagamento) {
        String sql = "select dp.id, dp.pagamento_id, dp.desdobramento_id, dp.valor, dp.saldo  from desdobramentopagamento dp where dp.pagamento_id = ?";
        List<DesdobramentoPagamento> desdobramentos = getJdbcTemplate().query(sql, new Object[]{pagamento.getId()}, new DesdobramentoPagamentoRowMapper());
        if (!desdobramentos.isEmpty()) {
            return desdobramentos;
        } else {
            return null;
        }
    }

    public void delete(DesdobramentoPagamento desdobramentoPagamento, Long idRev) {
        Object desdobramentoId = desdobramentoPagamento.getId();
        getJdbcTemplate().update(DesdobramentoPagamentoSetter.SQL_DELETE, desdobramentoId);
        getJdbcTemplate().batchUpdate(DesdobramentoPagamentoSetter.SQL_INSERT_AUD, new DesdobramentoPagamentoSetter(desdobramentoPagamento, desdobramentoPagamento.getId(), idRev, 2));
    }

}
