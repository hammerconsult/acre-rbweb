package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.GuiaGPS;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.GuiaGpsRowMapper;
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
public class JdbcGuiaGps extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcGuiaGps(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public GuiaGPS buscarGuiaGpsPorId(Long guiaGps_id) {
        String sql = "select id, " +
            "       codigoreceitatributo, " +
            "       codigoidentificacaotributo, " +
            "       periodocompetencia, " +
            "       valorprevistoinss, " +
            "       valoroutrasentidades, " +
            "       atualizacaomonetaria " +
            "from guiagps where id = ?";

        List<GuiaGPS> guias = getJdbcTemplate().query(sql, new Object[]{guiaGps_id}, new GuiaGpsRowMapper());
        if (!guias.isEmpty()) {
            return guias.get(0);
        } else {
            return null;
        }
    }
}
