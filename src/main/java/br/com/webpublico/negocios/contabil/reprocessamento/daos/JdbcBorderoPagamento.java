package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoItemBordero;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.BorderoLiberacaoFinanceiraSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.BorderoPagamentoSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.PagamentoSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 29/10/14
 * Time: 09:34
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class JdbcBorderoPagamento extends JdbcDaoSupport implements Serializable {
    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    private JdbcPagamento jdbcpagamento;

    @Autowired
    public JdbcBorderoPagamento(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void salvarNovo(Bordero bordero, Long idRev ) {
        if (Util.isNotNull(bordero)) {
            if (!bordero.getListaPagamentos().isEmpty()) {
                for (BorderoPagamento borderoPagamento : bordero.getListaPagamentos()) {
                    Long proximoId = geradorDeIds.getProximoId();
                    getJdbcTemplate().batchUpdate(BorderoPagamentoSetter.SQL_INSERT, new BorderoPagamentoSetter(borderoPagamento, proximoId));
                    getJdbcTemplate().batchUpdate(BorderoPagamentoSetter.SQL_INSERT_REV, new BorderoPagamentoSetter(borderoPagamento, proximoId, idRev, 0));
                    borderoPagamento.getPagamento().setStatus(StatusPagamento.BORDERO);
                    jdbcpagamento.atualizarStatus(borderoPagamento.getPagamento(), idRev);
                }
            }
        }
    }
    public void atualizarTodosBorderoPagamento(Bordero b, StatusPagamento statusPagamento, SituacaoItemBordero situacaoItemBordero, Long idRev) {
        Object[] objetos = new Object[6];

        if (!b.getListaPagamentos().isEmpty()) {
            for (BorderoPagamento pag : b.getListaPagamentos()) {
                objetos[0] = situacaoItemBordero.name();
                objetos[1] = pag.getPagamento().getId();
                jdbcpagamento.atualizarPagamento(statusPagamento, pag.getPagamento(), idRev);
                objetos[2] = pag.getValor();
                objetos[3] = pag.getPagamento().getContaPgto() != null ? pag.getPagamento().getContaPgto().getId() : null;
                objetos[4] = pag.getPagamento().getTipoOperacaoPagto().name();
                objetos[5] = pag.getId();
                getJdbcTemplate().update(BorderoPagamentoSetter.SQL_UPDATE, objetos);
                getJdbcTemplate().batchUpdate(BorderoPagamentoSetter.SQL_INSERT_REV, new BorderoPagamentoSetter(pag, pag.getId(), idRev, 1));
            }
        }
    }
    public void atualizarSituacao(BorderoPagamento borderoPagamento, Long idRev){
        if(Util.isNotNull(borderoPagamento)){
            Object[] objetos = new Object[2];
            objetos[0] = borderoPagamento.getSituacaoItemBordero().name();
            objetos[1] = borderoPagamento.getId();
            getJdbcTemplate().update(BorderoPagamentoSetter.SQL_UPDATE_SITUACAO, objetos);
            getJdbcTemplate().batchUpdate(BorderoPagamentoSetter.SQL_INSERT_REV, new BorderoPagamentoSetter(borderoPagamento, borderoPagamento.getId(), idRev, 1));
        }
    }

    public void remove(BorderoPagamento borderoPagamento){
        if(Util.isNotNull(borderoPagamento)){
            Object[] objetos = new Object[1];
            objetos[0] = borderoPagamento.getId();
            getJdbcTemplate().update(BorderoPagamentoSetter.SQL_DELETE, objetos);
            getJdbcTemplate().update(BorderoPagamentoSetter.SQL_DELETE_AUD, objetos);
        }
    }
}
