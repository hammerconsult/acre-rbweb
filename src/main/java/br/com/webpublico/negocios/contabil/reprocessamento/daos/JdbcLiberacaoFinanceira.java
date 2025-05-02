package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.Bordero;
import br.com.webpublico.entidades.BorderoPagamento;
import br.com.webpublico.entidades.LiberacaoCotaFinanceira;
import br.com.webpublico.entidades.TransferenciaMesmaUnidade;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.BorderoPagamentoSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.BorderoSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.LiberacaoFinanceiraSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.TransferenciaMesmaUnidadeSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

@Repository
public class JdbcLiberacaoFinanceira extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcLiberacaoFinanceira(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void atualizarStatus(LiberacaoCotaFinanceira liberacao, StatusPagamento statusPagamento, Long idRev) {
        Object[] objectos = new Object[2];
        if (Util.isNotNull(statusPagamento) && Util.isNotNull(liberacao)) {
            objectos[0] = statusPagamento.name();
            objectos[1] = liberacao.getId();
            getJdbcTemplate().update(LiberacaoFinanceiraSetter.SQL_UPDATE_STATUS, objectos);
            getJdbcTemplate().batchUpdate(LiberacaoFinanceiraSetter.SQL_INSERT_AUD, new LiberacaoFinanceiraSetter(liberacao, liberacao.getId(), idRev, 1));
        }
    }

    public void indefir(LiberacaoCotaFinanceira liberacao, Long idRev){
        if(Util.isNotNull(liberacao)){
            Object[] objetos = new Object[4];
            objetos[0] = liberacao.getStatusPagamento().name();
            objetos[1] = liberacao.getDataConciliacao();
            objetos[2] = liberacao.getDataLiberacao();
            objetos[3] = liberacao.getId();
            getJdbcTemplate().update(LiberacaoFinanceiraSetter.SQL_UPDATE_INDEFERIR, objetos);
            getJdbcTemplate().batchUpdate(LiberacaoFinanceiraSetter.SQL_INSERT_AUD, new LiberacaoFinanceiraSetter(liberacao, liberacao.getId(), idRev, 1));
        }
    }
}
