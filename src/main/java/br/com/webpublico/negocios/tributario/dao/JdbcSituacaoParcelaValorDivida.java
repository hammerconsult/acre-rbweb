package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.SituacaoParcelaValorDivida;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

/**
 * Created by Buzatto on 24/08/2016.
 */
@Repository
public class JdbcSituacaoParcelaValorDivida extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcSituacaoParcelaValorDivida(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void atualizar(SituacaoParcelaValorDivida situacao) {
        String sql = "UPDATE situacaoparcelavalordivida "
            + "          SET processada = ?             "
            + "         WHERE id =?                     ";

        getJdbcTemplate().update(sql, true, situacao.getId());
    }
}
