package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.TransferenciaContaFinanceira;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.TransferenciaFinanceiraSetter;
import br.com.webpublico.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

@Repository
public class JdbcTransferenciaFinanceira extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcTransferenciaFinanceira(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void atualizarStatusTransferenciaContaFinanceira(TransferenciaContaFinanceira transf, StatusPagamento statusPagamento, Long idRev) {
        Object[] objectos = new Object[2];
        if (Util.isNotNull(statusPagamento) && Util.isNotNull(transf)) {
            objectos[0] = statusPagamento.name();
            objectos[1] = transf.getId();
            getJdbcTemplate().update(TransferenciaFinanceiraSetter.SQL_UPDATE_STATUS, objectos);
            getJdbcTemplate().batchUpdate(TransferenciaFinanceiraSetter.SQL_INSERT_REV, new TransferenciaFinanceiraSetter(transf, transf.getId(), idRev, 1));
        }
    }

    public void indeferir(TransferenciaContaFinanceira transf, Long idRev) {
        if (Util.isNotNull(transf)) {
            Object[] objetos = new Object[4];
            objetos[0] = transf.getStatusPagamento().name();
            objetos[1] = transf.getDataConciliacao();
            objetos[2] = transf.getRecebida();
            objetos[3] = transf.getId();
            getJdbcTemplate().update(TransferenciaFinanceiraSetter.SQL_UPDATE_INDEFERIR, objetos);
            getJdbcTemplate().batchUpdate(TransferenciaFinanceiraSetter.SQL_INSERT_REV, new TransferenciaFinanceiraSetter(transf, transf.getId(), idRev, 1));
        }
    }
}
