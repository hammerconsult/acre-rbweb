package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.TransferenciaMesmaUnidade;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.TransferenciaMesmaUnidadeSetter;
import br.com.webpublico.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

@Repository
public class JdbcTransferenciaMesmaUnidade extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcTransferenciaMesmaUnidade(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void atualizarStatusTransferenciaMesmaUnidade(TransferenciaMesmaUnidade trans, StatusPagamento statusPagamento, Long idRev) {
        Object[] objectos = new Object[2];
        if (Util.isNotNull(statusPagamento) && Util.isNotNull(trans)) {
            objectos[0] = statusPagamento.name();
            objectos[1] = trans.getId();
            getJdbcTemplate().update(TransferenciaMesmaUnidadeSetter.SQL_UPDATE_STATUS, objectos);
            getJdbcTemplate().batchUpdate(TransferenciaMesmaUnidadeSetter.SQL_INSERT_AUD, new TransferenciaMesmaUnidadeSetter(trans, trans.getId(), idRev, 1));
        }
    }

    public void indeferir(TransferenciaMesmaUnidade trans, Long idRev) {
        if (Util.isNotNull(trans)) {
            Object[] objetos = new Object[4];
            objetos[0] = trans.getStatusPagamento().name();
            objetos[1] = trans.getDataConciliacao();
            objetos[2] = trans.getRecebida();
            objetos[3] = trans.getId();
            getJdbcTemplate().update(TransferenciaMesmaUnidadeSetter.SQL_UPDATE_INDEFERIR, objetos);
            getJdbcTemplate().batchUpdate(TransferenciaMesmaUnidadeSetter.SQL_INSERT_AUD, new TransferenciaMesmaUnidadeSetter(trans, trans.getId(), idRev, 1));
        }
    }
}


