package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.Bordero;
import br.com.webpublico.entidades.BorderoPagamento;
import br.com.webpublico.entidades.BorderoPagamentoExtra;
import br.com.webpublico.entidades.PagamentoExtra;
import br.com.webpublico.enums.SituacaoItemBordero;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.BorderoPagamentoExtraSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.BorderoPagamentoSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.PagamentoExtraSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.PagamentoSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

@Repository
public class JdbcBorderoPagamentoExtra extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorId;
    @Autowired
    private JdbcPagamentoExtra jdbcPagamentoExtra;

    @Autowired
    public JdbcBorderoPagamentoExtra(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void salvarNovo(Bordero bordero, Long idRev) {
        if (Util.isNotNull(bordero)) {
            if (!bordero.getListaPagamentosExtra().isEmpty()) {
                for (BorderoPagamentoExtra borderoPagamentoExtra : bordero.getListaPagamentosExtra()) {
                    Long proximoId = geradorId.getProximoId();
                    getJdbcTemplate().batchUpdate(BorderoPagamentoExtraSetter.SQL_INSERT, new BorderoPagamentoExtraSetter(borderoPagamentoExtra, proximoId));
                    getJdbcTemplate().batchUpdate(BorderoPagamentoExtraSetter.SQL_INSERT_AUD, new BorderoPagamentoExtraSetter(borderoPagamentoExtra, proximoId, idRev, 0));
                    jdbcPagamentoExtra.atualizarStatus(borderoPagamentoExtra.getPagamentoExtra(), StatusPagamento.BORDERO, idRev);
                }
            }
        }
    }

    public void atualizarTodosBorderoPagamentoExtra(Bordero b, StatusPagamento statusPagamento, SituacaoItemBordero situacaoItemBordero, Long idRev) {
        Object[] objectos = new Object[6];
        if (!b.getListaPagamentosExtra().isEmpty()) {
            for (BorderoPagamentoExtra pag : b.getListaPagamentosExtra()) {
                objectos[0] = situacaoItemBordero.name();
                objectos[1] = pag.getPagamentoExtra().getId();
                objectos[2] = pag.getValor();
                objectos[3] = pag.getContaCorrenteFavorecido() != null ? pag.getContaCorrenteFavorecido().getId() : null;
                objectos[4] = pag.getPagamentoExtra().getTipoOperacaoPagto().name();
                objectos[5] = pag.getId();
                getJdbcTemplate().update(BorderoPagamentoExtraSetter.SQL_UPDATE, objectos);
                getJdbcTemplate().batchUpdate(BorderoPagamentoExtraSetter.SQL_INSERT_AUD, new BorderoPagamentoExtraSetter(pag, pag.getId(), idRev, 1));
                jdbcPagamentoExtra.atualizarStatus(pag.getPagamentoExtra(), statusPagamento, idRev);
            }
        }
    }

    public void atualizarSituacao(BorderoPagamentoExtra borderoPag, Long idRev) {
        if (Util.isNotNull(borderoPag)) {
            Object[] objetos = new Object[2];
            objetos[0] = borderoPag.getSituacaoItemBordero().name();
            objetos[1] = borderoPag.getId();
            getJdbcTemplate().update(BorderoPagamentoExtraSetter.SQL_UPDATE_SITUACAO, objetos);
            getJdbcTemplate().batchUpdate(BorderoPagamentoExtraSetter.SQL_INSERT_AUD, new BorderoPagamentoExtraSetter(borderoPag, borderoPag.getId(), idRev, 1));
        }
    }

    public void remove(BorderoPagamentoExtra borderoPag){
        if(Util.isNotNull(borderoPag)){
            Object[] objetos = new Object[1];
            objetos[0] = borderoPag.getId();
            getJdbcTemplate().update(BorderoPagamentoExtraSetter.SQL_DELETE, objetos);
            getJdbcTemplate().update(BorderoPagamentoExtraSetter.SQL_DELETE_AUD, objetos);
        }
    }
}
