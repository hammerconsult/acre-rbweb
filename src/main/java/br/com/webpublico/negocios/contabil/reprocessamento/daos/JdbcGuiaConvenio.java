package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.GuiaConvenio;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.GuiaConvenioRowMapper;
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
public class JdbcGuiaConvenio extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcGuiaConvenio(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public GuiaConvenio buscarGuiaConvenioPorId(Long guiaConvenio_id) {
        String sql = "select id, codigobarra, valor from guiaconvenio where id = ?";
        List<GuiaConvenio> guias = getJdbcTemplate().query(sql, new Object[]{guiaConvenio_id}, new GuiaConvenioRowMapper());
        if (!guias.isEmpty()) {
            return guias.get(0);
        } else {
            return null;
        }
    }
}
