package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.Bordero;
import br.com.webpublico.entidades.BorderoLiberacaoFinanceira;
import br.com.webpublico.entidades.BorderoPagamentoExtra;
import br.com.webpublico.entidades.BorderoTransferenciaFinanceira;
import br.com.webpublico.enums.SituacaoItemBordero;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

@Repository
public class JdbcBorderoTransferenciaFinanceira extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorId;

    @Autowired
    private JdbcTransferenciaFinanceira jdbcTransferenciaFinanceira;

    @Autowired
    public JdbcBorderoTransferenciaFinanceira(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void salvarNovo(Bordero bordero, Long idRev) {
        if (Util.isNotNull(bordero)) {
            if (!bordero.getListaTransferenciaFinanceira().isEmpty()) {
                for (BorderoTransferenciaFinanceira transf : bordero.getListaTransferenciaFinanceira()) {
                    Long proximoId = geradorId.getProximoId();
                    getJdbcTemplate().batchUpdate(BorderoTransferenciaFinanceiraSetter.SQL_INSERT, new BorderoTransferenciaFinanceiraSetter(transf, proximoId));
                    getJdbcTemplate().batchUpdate(BorderoTransferenciaFinanceiraSetter.SQL_INSERT_AUD, new BorderoTransferenciaFinanceiraSetter(transf, proximoId, idRev, 0));
                    jdbcTransferenciaFinanceira.atualizarStatusTransferenciaContaFinanceira(transf.getTransferenciaContaFinanceira(), StatusPagamento.BORDERO, idRev);
                }
            }
        }
    }
    public void atualizarTodosBorderoTranferenciaFinanceira(Bordero b, StatusPagamento statusPagamento, SituacaoItemBordero situacaoItemBordero, Long idRev) {
        Object[] objectos = new Object[5];
        if (!b.getListaTransferenciaFinanceira().isEmpty()) {
            for (BorderoTransferenciaFinanceira trans : b.getListaTransferenciaFinanceira()) {
                objectos[0] = situacaoItemBordero.name();
                objectos[1] = trans.getTransferenciaContaFinanceira().getId();
                objectos[2] = trans.getValor();
                objectos[3] = trans.getTransferenciaContaFinanceira().getTipoOperacaoPagto().name();
                objectos[4] = trans.getId();
                getJdbcTemplate().update(BorderoTransferenciaFinanceiraSetter.SQL_UPDATE, objectos);
                getJdbcTemplate().batchUpdate(BorderoTransferenciaFinanceiraSetter.SQL_INSERT_AUD, new BorderoTransferenciaFinanceiraSetter(trans, trans.getId(), idRev, 1));
                jdbcTransferenciaFinanceira.atualizarStatusTransferenciaContaFinanceira(trans.getTransferenciaContaFinanceira(), statusPagamento, idRev);
            }
        }
    }
    public void atualizarSituacao(BorderoTransferenciaFinanceira borderoTransferenciaFinanceira, Long idRev){
        if(Util.isNotNull(borderoTransferenciaFinanceira)){
            Object[] objetos = new Object[2];
            objetos[0] = borderoTransferenciaFinanceira.getSituacaoItemBordero().name();
            objetos[1] = borderoTransferenciaFinanceira.getId();
            getJdbcTemplate().update(BorderoTransferenciaFinanceiraSetter.SQL_UPDATE_SITUACAO, objetos);
            getJdbcTemplate().batchUpdate(BorderoTransferenciaFinanceiraSetter.SQL_INSERT_AUD, new BorderoTransferenciaFinanceiraSetter(borderoTransferenciaFinanceira, borderoTransferenciaFinanceira.getId(), idRev, 1));
        }
    }

    public void remove(BorderoTransferenciaFinanceira borderoTransferenciaFinanceira){
        if(Util.isNotNull(borderoTransferenciaFinanceira)){
            Object[] objetos = new Object[1];
            objetos[0] = borderoTransferenciaFinanceira.getId();
            getJdbcTemplate().update(BorderoTransferenciaFinanceiraSetter.SQL_DELETE, objetos);
            getJdbcTemplate().update(BorderoTransferenciaFinanceiraSetter.SQL_DELETE_AUD, objetos);
        }
    }
}
