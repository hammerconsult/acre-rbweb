package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.PagamentoExtra;
import br.com.webpublico.entidades.PagamentoReceitaExtra;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.PagamentoReceitaExtraRowMapper;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.PagamentoReceitaExtraSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

@Repository
public class JdbcPagamentoReceitaExtra extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcPagamentoReceitaExtra(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Autowired
    private SingletonGeradorId geradorDeIds;

    public PagamentoReceitaExtra atualizar(PagamentoReceitaExtra pagamentoReceitaExtra, Long idRev) {
        if (Util.isNotNull(pagamentoReceitaExtra)) {
            getJdbcTemplate().batchUpdate(PagamentoReceitaExtraSetter.SQL_UPDATE_ALL, new PagamentoReceitaExtraSetter(pagamentoReceitaExtra, pagamentoReceitaExtra.getId()));
            getJdbcTemplate().batchUpdate(PagamentoReceitaExtraSetter.SQL_INSERT_AUD, new PagamentoReceitaExtraSetter(pagamentoReceitaExtra, pagamentoReceitaExtra.getId(), idRev, 1));
        }
        return pagamentoReceitaExtra;
    }

    public PagamentoReceitaExtra salvar(PagamentoReceitaExtra pagamentoReceitaExtra, Long idRev) {
        if (Util.isNotNull(pagamentoReceitaExtra)) {
            pagamentoReceitaExtra.setId(geradorDeIds.getProximoId());
            getJdbcTemplate().batchUpdate(PagamentoReceitaExtraSetter.SQL_INSERT, new PagamentoReceitaExtraSetter(pagamentoReceitaExtra, pagamentoReceitaExtra.getId()));
            getJdbcTemplate().batchUpdate(PagamentoReceitaExtraSetter.SQL_INSERT_AUD, new PagamentoReceitaExtraSetter(pagamentoReceitaExtra, pagamentoReceitaExtra.getId(), idRev, 0));
        }
        return pagamentoReceitaExtra;
    }

    public List<PagamentoReceitaExtra> buscarPagamentosReceitaExtraPorPagamentoExtra(PagamentoExtra pagamentoExtra) {
        String sql = "select id, pagamentoextra_id, receitaextra_id, pagamentoestornorecextra_id  " +
            "from pagamentoreceitaextra where pagamentoextra_id = ?";

        List<PagamentoReceitaExtra> pagamentos = getJdbcTemplate().query(sql, new Object[]{pagamentoExtra.getId()}, new PagamentoReceitaExtraRowMapper());
        if (!pagamentos.isEmpty()) {
            return pagamentos;
        } else {
            return null;
        }
    }

    public void delete(PagamentoReceitaExtra pagamentoReceitaExtra, Long idRev) {
        if (Util.isNotNull(pagamentoReceitaExtra)) {
            Object idPagamentoReceitaExtra = pagamentoReceitaExtra.getId();
            getJdbcTemplate().update(PagamentoReceitaExtraSetter.SQL_DELETE, idPagamentoReceitaExtra);
            getJdbcTemplate().batchUpdate(PagamentoReceitaExtraSetter.SQL_INSERT_AUD, new PagamentoReceitaExtraSetter(pagamentoReceitaExtra, pagamentoReceitaExtra.getId(), idRev, 2));
        }
    }
}
