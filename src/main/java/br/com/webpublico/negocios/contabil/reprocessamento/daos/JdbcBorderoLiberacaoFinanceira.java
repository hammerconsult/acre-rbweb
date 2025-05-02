package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.Bordero;
import br.com.webpublico.entidades.BorderoLiberacaoFinanceira;
import br.com.webpublico.entidades.BorderoTransferenciaMesmaUnidade;
import br.com.webpublico.enums.SituacaoItemBordero;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.BorderoLiberacaoFinanceiraSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.BorderoTransferenciaMesmaUnidadeSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

@Repository
public class JdbcBorderoLiberacaoFinanceira extends JdbcDaoSupport implements Serializable {

    @Autowired
    private JdbcLiberacaoFinanceira jdbcLiberacaoFinanceira;

    @Autowired
    public JdbcBorderoLiberacaoFinanceira(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Autowired
    private SingletonGeradorId geradorDeIds;

    public void salvarNovo(Bordero bordero, Long idRev) {
        if (Util.isNotNull(bordero)) {
            if (!bordero.getListaLiberacaoCotaFinanceira().isEmpty()) {
                for (BorderoLiberacaoFinanceira borderoLiberacaoFinanceira : bordero.getListaLiberacaoCotaFinanceira()) {
                    Long proximoId = geradorDeIds.getProximoId();
                    getJdbcTemplate().batchUpdate(BorderoLiberacaoFinanceiraSetter.SQL_INSERT, new BorderoLiberacaoFinanceiraSetter(borderoLiberacaoFinanceira, proximoId));
                    getJdbcTemplate().batchUpdate(BorderoLiberacaoFinanceiraSetter.SQL_INSERT_AUD, new BorderoLiberacaoFinanceiraSetter(borderoLiberacaoFinanceira, proximoId, idRev, 0));
                    jdbcLiberacaoFinanceira.atualizarStatus(borderoLiberacaoFinanceira.getLiberacaoCotaFinanceira(), StatusPagamento.BORDERO, idRev);
                }
            }
        }
    }

    public void atualizarTodosBorderoLiberacaoFinanceira(Bordero b, StatusPagamento statusPagamento, SituacaoItemBordero situacaoItemBordero, Long idRev) {
        Object[] objectos = new Object[4];
        if (!b.getListaLiberacaoCotaFinanceira().isEmpty()) {
            for (BorderoLiberacaoFinanceira bordero : b.getListaLiberacaoCotaFinanceira()) {
                objectos[0] = situacaoItemBordero.name();
                objectos[1] = bordero.getValor();
                objectos[2] = bordero.getLiberacaoCotaFinanceira().getTipoOperacaoPagto().name();
                objectos[3] = bordero.getId();
                getJdbcTemplate().update(BorderoLiberacaoFinanceiraSetter.SQL_UPDATE, objectos);
                getJdbcTemplate().batchUpdate(BorderoLiberacaoFinanceiraSetter.SQL_INSERT_AUD, new BorderoLiberacaoFinanceiraSetter(bordero, bordero.getId(), idRev, 1));
                jdbcLiberacaoFinanceira.atualizarStatus(bordero.getLiberacaoCotaFinanceira(), statusPagamento, idRev);
            }
        }
    }

    public void atualizarSituacao(BorderoLiberacaoFinanceira liberacao, Long idRev) {
        if (Util.isNotNull(liberacao)) {
            Object[] objetos = new Object[2];
            objetos[0] = liberacao.getSituacaoItemBordero().name();
            objetos[1] = liberacao.getId();
            getJdbcTemplate().update(BorderoLiberacaoFinanceiraSetter.SQL_UPDATE_SITUACAO, objetos);
            getJdbcTemplate().batchUpdate(BorderoLiberacaoFinanceiraSetter.SQL_INSERT_AUD, new BorderoLiberacaoFinanceiraSetter(liberacao, liberacao.getId(), idRev, 1));
        }
    }

    public void remove(BorderoLiberacaoFinanceira liberacao){
        if(Util.isNotNull(liberacao)){
            Object[] objetos = new Object[1];
            objetos[0] = liberacao.getId();
            getJdbcTemplate().update(BorderoLiberacaoFinanceiraSetter.SQL_DELETE, objetos);
            getJdbcTemplate().update(BorderoLiberacaoFinanceiraSetter.SQL_DELETE_AUD, objetos);
        }
    }
}
