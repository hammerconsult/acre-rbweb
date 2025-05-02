package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.entidades.PagamentoEstorno;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.PagamentoEstornoRowMapper;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.PagamentoEstornoSetter;
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
public class JdbcPagamentoEstorno extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcPagamentoEstorno(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Autowired
    private SingletonGeradorId geradorDeIds;

    public PagamentoEstorno salvar(PagamentoEstorno pagamentoEstorno, Long idRev) {
        if (Util.isNotNull(pagamentoEstorno)) {
            pagamentoEstorno.setId(geradorDeIds.getProximoId());
            getJdbcTemplate().batchUpdate(PagamentoEstornoSetter.SQL_INSERT, new PagamentoEstornoSetter(pagamentoEstorno, pagamentoEstorno.getId()));
            getJdbcTemplate().batchUpdate(PagamentoEstornoSetter.SQL_INSERT_AUD, new PagamentoEstornoSetter(pagamentoEstorno, pagamentoEstorno.getId(), idRev, 0));
        }
        return pagamentoEstorno;
    }

    public PagamentoEstorno atualizar(PagamentoEstorno pagamentoEstorno, Long idRev) {
        if (Util.isNotNull(pagamentoEstorno)) {
            getJdbcTemplate().batchUpdate(PagamentoEstornoSetter.SQL_UPDATE_ALL, new PagamentoEstornoSetter(pagamentoEstorno, pagamentoEstorno.getId()));
            getJdbcTemplate().batchUpdate(PagamentoEstornoSetter.SQL_INSERT_AUD, new PagamentoEstornoSetter(pagamentoEstorno, pagamentoEstorno.getId(), idRev, 1));
        }
        return pagamentoEstorno;
    }


    public List<PagamentoEstorno> buscarPagamentosEstornoPorPagamento(Pagamento pagamento) {
        String sql = "select id, " +
            "       dataestorno, " +
            "       valor, " +
            "       numero, " +
            "       pagamento_id, " +
            "       movimentodespesaorc_id, " +
            "       categoriaorcamentaria, " +
            "       historicocontabil_id, " +
            "       complementohistorico, " +
            "       unidadeorganizacionaladm_id, " +
            "       unidadeorganizacional_id, " +
            "       dataconciliacao, " +
            "       historicorazao, " +
            "       historiconota, " +
            "       eventocontabil_id, " +
            "       usuariosistema_id, " +
            "       valorfinal, " +
            "       exercicio_id, " +
            "       uuid, " +
            "       identificador_id " +
            "from pagamentoestorno where pagamento_id = ?";
        List<PagamentoEstorno> estornos = getJdbcTemplate().query(sql, new Object[]{pagamento.getId()}, new PagamentoEstornoRowMapper());
        if (!estornos.isEmpty()) {
            return estornos;
        } else {
            return null;
        }

    }
}
