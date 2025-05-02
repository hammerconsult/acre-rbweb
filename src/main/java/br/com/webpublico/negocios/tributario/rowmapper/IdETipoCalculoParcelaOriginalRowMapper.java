package br.com.webpublico.negocios.tributario.rowmapper;


import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.entidades.Lote;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IdETipoCalculoParcelaOriginalRowMapper implements RowMapper {
    private Long idParcelaOriginal;
    private Calculo.TipoCalculo tipoCalculoOriginal;

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        idParcelaOriginal  = rs.getLong("id");
        tipoCalculoOriginal = Calculo.TipoCalculo.valueOf(rs.getString("tipocalculo"));
        return this;
    }

    public Long getIdParcelaOriginal() {
        return idParcelaOriginal;
    }

    public Calculo.TipoCalculo getTipoCalculoOriginal() {
        return tipoCalculoOriginal;
    }
}
