package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.GuiaDARFSimples;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.GuiaDarfSimplesRowMapper;
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
public class JdbcGuiaDARFSimples extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcGuiaDARFSimples(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public GuiaDARFSimples buscarGuiaDARFSimplesPorId(Long guiaDARF_id) {
        String sql = "select id, " +
            "       codigoreceitatributo, " +
            "       codigoidentificacaotributo, " +
            "       periodoapuracao, " +
            "       valorreceitabruta, " +
            "       percentualreceitabruta, " +
            "       valorprincipal, " +
            "       valormulta, " +
            "       valorjuros " +
            "from guiadarfsimples where id = ? ";

        List<GuiaDARFSimples> guias = getJdbcTemplate().query(sql, new Object[]{guiaDARF_id}, new GuiaDarfSimplesRowMapper());
        if (!guias.isEmpty()) {
            return guias.get(0);
        } else {
            return null;
        }
    }
}
