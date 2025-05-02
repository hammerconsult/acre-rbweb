package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.GuiaDARF;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.GuiaDarfRowMapper;
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
public class JdbcGuiaDARF extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcGuiaDARF(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public GuiaDARF buscarGuiaDARFPorId(Long guiaDARF_id) {
        String sql = "select id,  " +
            "       codigoreceitatributo,  " +
            "       codigoidentificacaotributo,  " +
            "       periodoapuracao,  " +
            "       numeroreferencia,  " +
            "       valorprincipal,  " +
            "       valormulta,  " +
            "       valorjuros,  " +
            "       datavencimento  " +
            "from guiadarf where id = ? ";

        List<GuiaDARF> guias = getJdbcTemplate().query(sql, new Object[]{guiaDARF_id}, new GuiaDarfRowMapper());
        if (!guias.isEmpty()) {
            return guias.get(0);
        } else {
            return null;
        }
    }
}
