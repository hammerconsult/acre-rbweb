package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.entidades.RetencaoPgto;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.RetencaoPagamentoRowMapper;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.RetencaoPagamentoSetter;
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
public class JdbcRetencaoPagamento extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcRetencaoPagamento(DataSource dataSource) {
        setDataSource(dataSource);
    }
    @Autowired
    private SingletonGeradorId geradorDeIds;

    public RetencaoPgto salvar(RetencaoPgto retencaoPgto, Long idRev) {
        if (Util.isNotNull(retencaoPgto)) {
            retencaoPgto.setId(geradorDeIds.getProximoId());
            getJdbcTemplate().batchUpdate(RetencaoPagamentoSetter.SQL_INSERT, new RetencaoPagamentoSetter(retencaoPgto, retencaoPgto.getId()));
            getJdbcTemplate().batchUpdate(RetencaoPagamentoSetter.SQL_INSERT_AUD, new RetencaoPagamentoSetter(retencaoPgto, retencaoPgto.getId(), idRev, 0));
        }
        return retencaoPgto;
    }

    public RetencaoPgto atualizar(RetencaoPgto retencaoPgto, Long idRev) {
        if (Util.isNotNull(retencaoPgto)) {
            getJdbcTemplate().batchUpdate(RetencaoPagamentoSetter.SQL_UPDATE_ALL, new RetencaoPagamentoSetter(retencaoPgto, retencaoPgto.getId()));
            getJdbcTemplate().batchUpdate(RetencaoPagamentoSetter.SQL_INSERT_AUD, new RetencaoPagamentoSetter(retencaoPgto, retencaoPgto.getId(), idRev, 1));
        }
        return retencaoPgto;
    }

    public List<RetencaoPgto> buscarRetencaoPagamentosPorPagamento(Pagamento pagamento) {
        String sql = "select rt.id,  " +
            "       rt.numero,  " +
            "       rt.valor,  " +
            "       rt.dataretencao,  " +
            "       rt.saldo,  " +
            "       rt.complementohistorico,  " +
            "       rt.subconta_id,  " +
            "       rt.fontederecursos_id,  " +
            "       rt.contaextraorcamentaria_id,  " +
            "       rt.usuariosistema_id,  " +
            "       rt.pagamento_id,  " +
            "       rt.unidadeorganizacional_id,  " +
            "       rt.efetivado,  " +
            "       rt.estornado,  " +
            "       rt.pessoa_id,  " +
            "       rt.classecredor_id,  " +
            "       rt.tipoconsignacao,  " +
            "       rt.pagamentoestorno_id  " +
            "from retencaopgto rt where rt.pagamento_id = ?";
        List<RetencaoPgto> retencoes = getJdbcTemplate().query(sql, new Object[]{pagamento.getId()}, new RetencaoPagamentoRowMapper());
        if (!retencoes.isEmpty()) {
            return retencoes;
        } else {
            return null;
        }
    }

    public void delete(RetencaoPgto retencaoPgto, Long idRev){
        Object retencaoPgtoId = retencaoPgto.getId();
        getJdbcTemplate().update(RetencaoPagamentoSetter.SQL_DELETE, retencaoPgtoId);
        getJdbcTemplate().batchUpdate(RetencaoPagamentoSetter.SQL_INSERT_AUD, new RetencaoPagamentoSetter(retencaoPgto, retencaoPgto.getId(), idRev, 2));
    }

}
