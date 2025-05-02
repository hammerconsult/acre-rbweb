package br.com.webpublico.negocios.tributario.rowmapper;


import br.com.webpublico.entidades.Tributo;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ValorPorTributoRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        ValorPorTributo valor = new ValorPorTributo(rs.getBigDecimal("VALOR"), rs.getLong("ID"));
        return valor;
    }

    public class ValorPorTributo {
        private BigDecimal valor;
        private Tributo tributo;

        public ValorPorTributo(BigDecimal valor, Long id) {
            this.valor = valor;
            this.tributo = new Tributo();
            tributo.setId(id);
        }

        public BigDecimal getValor() {
            return valor;
        }

        public Tributo getTributo() {
            return tributo;
        }
    }
}
