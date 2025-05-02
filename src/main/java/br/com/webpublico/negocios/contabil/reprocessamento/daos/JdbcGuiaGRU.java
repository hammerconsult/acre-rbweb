package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.GuiaGRU;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.GuiaGRURowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

@Repository
public class JdbcGuiaGRU extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcGuiaGRU(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public GuiaGRU buscarGuiaGRUPorId(Long idGuiaGRU) {
        String sql = "select id, codigorecolhimento, numeroreferencia, competencia, vencimento, uggestao, valorprincipal, codigobarra from guiagru where id = ?";
        List<GuiaGRU> guias = getJdbcTemplate().query(sql, new Object[]{idGuiaGRU}, new GuiaGRURowMapper());
        if (!guias.isEmpty()) {
            return guias.get(0);
        } else {
            return null;
        }
    }
}
