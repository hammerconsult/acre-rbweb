package br.com.webpublico.negocios.tributario.rowmapper;

import br.com.webpublico.entidades.DescontoItemParcela;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 23/09/13
 * Time: 16:45
 * To change this template use File | Settings | File Templates.
 */
public class DescontoItemParcelaRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        DescontoItemParcela descontoItemParcela = new DescontoItemParcela();
        descontoItemParcela.setId(rs.getLong("ID"));
        descontoItemParcela.setInicio(rs.getDate("INICIO"));
        descontoItemParcela.setFim(rs.getDate("FIM"));
        descontoItemParcela.setDesconto(rs.getBigDecimal("DESCONTO"));
        descontoItemParcela.setMotivo(rs.getString("MOTIVO"));
        descontoItemParcela.setTipo(rs.getString("TIPO") != null ? DescontoItemParcela.Tipo.valueOf(rs.getString("TIPO")) : null);
        descontoItemParcela.setOrigem(rs.getString("ORIGEM") != null ? DescontoItemParcela.Origem.valueOf(rs.getString("ORIGEM")) : null);
        return descontoItemParcela;
    }
}
