package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.Bordero;
import br.com.webpublico.entidades.BorderoPagamentoExtra;
import br.com.webpublico.entidades.BorderoTransferenciaMesmaUnidade;
import br.com.webpublico.enums.SituacaoItemBordero;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.BorderoPagamentoSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.BorderoTransferenciaMesmaUnidadeSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

@Repository
public class JdbcBorderoTransferenciaMesmaUnidade extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorId;
    @Autowired
    private JdbcTransferenciaMesmaUnidade jdbcTransferenciaMesmaUnidade;

    @Autowired
    public JdbcBorderoTransferenciaMesmaUnidade(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void salvarNovo(Bordero bordero, Long idRev) {
        if (Util.isNotNull(bordero)) {
            if (!bordero.getListaTransferenciaMesmaUnidade().isEmpty()) {
                for (BorderoTransferenciaMesmaUnidade transf : bordero.getListaTransferenciaMesmaUnidade()) {
                    Long proximoId = geradorId.getProximoId();
                    getJdbcTemplate().batchUpdate(BorderoTransferenciaMesmaUnidadeSetter.SQL_INSERT, new BorderoTransferenciaMesmaUnidadeSetter(transf, proximoId));
                    getJdbcTemplate().batchUpdate(BorderoTransferenciaMesmaUnidadeSetter.SQL_INSERT_AUD, new BorderoTransferenciaMesmaUnidadeSetter(transf, proximoId, idRev, 0));
                    jdbcTransferenciaMesmaUnidade.atualizarStatusTransferenciaMesmaUnidade(transf.getTransferenciaMesmaUnidade(), StatusPagamento.BORDERO, idRev);
                }
            }
        }
    }

    public void atualizarTodosBorderoTransferenciaMesmaUnidade(Bordero b, StatusPagamento statusPagamento, SituacaoItemBordero situacaoItemBordero, Long idRev) {
        Object[] objectos = new Object[5];
        if (!b.getListaTransferenciaMesmaUnidade().isEmpty()) {
            for (BorderoTransferenciaMesmaUnidade transf : b.getListaTransferenciaMesmaUnidade()) {
                objectos[0] = situacaoItemBordero.name();
                objectos[1] = transf.getTransferenciaMesmaUnidade().getId();
                objectos[2] = transf.getValor();
                objectos[3] = transf.getTransferenciaMesmaUnidade().getTipoOperacaoPagto().name();
                objectos[4] = transf.getId();
                getJdbcTemplate().update(BorderoTransferenciaMesmaUnidadeSetter.SQL_UPDATE, objectos);
                getJdbcTemplate().batchUpdate(BorderoTransferenciaMesmaUnidadeSetter.SQL_INSERT_AUD, new BorderoTransferenciaMesmaUnidadeSetter(transf, transf.getId(), idRev, 1));
                jdbcTransferenciaMesmaUnidade.atualizarStatusTransferenciaMesmaUnidade(transf.getTransferenciaMesmaUnidade(), statusPagamento, idRev);
            }
        }
    }

    public void atualizarSituacao(BorderoTransferenciaMesmaUnidade transf, Long idRev) {
        if (Util.isNotNull(transf)) {
            Object[] objetos = new Object[2];
            objetos[0] = transf.getSituacaoItemBordero().name();
            objetos[1] = transf.getId();
            getJdbcTemplate().update(BorderoTransferenciaMesmaUnidadeSetter.SQL_UPDATE_SITUACAO, objetos);
            getJdbcTemplate().batchUpdate(BorderoTransferenciaMesmaUnidadeSetter.SQL_INSERT_AUD, new BorderoTransferenciaMesmaUnidadeSetter(transf, transf.getId(), idRev, 1));
        }
    }

    public void remove(BorderoTransferenciaMesmaUnidade transf){
        if(Util.isNotNull(transf)){
            Object[] objetos = new Object[1];
            objetos[0] = transf.getId();
            getJdbcTemplate().update(BorderoTransferenciaMesmaUnidadeSetter.SQL_DELETE, objetos);
            getJdbcTemplate().update(BorderoTransferenciaMesmaUnidadeSetter.SQL_DELETE_AUD, objetos);
        }
    }
}
