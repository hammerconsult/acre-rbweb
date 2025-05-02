package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.GuiaFatura;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.GuiaFaturaRowMapper;
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
public class JdbcGuiaFatura extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcGuiaFatura(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public GuiaFatura buscarGuiaConvenioPorId(Long guiaFatura_id) {
        String sql = "select id, codigobarra, datavencimento, valornominal, valordescontos, valorjuros " +
            "from guiafatura " +
            "where id = ?";

        List<GuiaFatura> guias = getJdbcTemplate().query(sql, new Object[]{guiaFatura_id}, new GuiaFaturaRowMapper());
        if (!guias.isEmpty()) {
            return guias.get(0);
        } else {
            return null;
        }
    }
}
