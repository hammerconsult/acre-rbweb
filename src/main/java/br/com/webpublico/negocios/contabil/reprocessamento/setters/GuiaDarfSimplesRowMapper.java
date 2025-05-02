package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.GuiaDARFSimples;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuiaDarfSimplesRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        GuiaDARFSimples guiaDARFSimples = new GuiaDARFSimples();
        guiaDARFSimples.setId(rs.getLong("ID"));
        guiaDARFSimples.setCodigoReceitaTributo(rs.getString("CODIGORECEITATRIBUTO"));
        guiaDARFSimples.setCodigoIdentificacaoTributo(rs.getString("CODIGOIDENTIFICACAOTRIBUTO"));
        guiaDARFSimples.setPeriodoApuracao(rs.getDate("PERIODOAPURACAO"));
        guiaDARFSimples.setValorReceitaBruta(rs.getBigDecimal("VALORRECEITABRUTA"));
        guiaDARFSimples.setPercentualReceitaBruta(rs.getBigDecimal("PERCENTUALRECEITABRUTA"));
        guiaDARFSimples.setValorPrincipal(rs.getBigDecimal("VALORPRINCIPAL"));
        guiaDARFSimples.setValorMulta(rs.getBigDecimal("VALORMULTA"));
        guiaDARFSimples.setValorJuros(rs.getBigDecimal("VALORJUROS"));
        return guiaDARFSimples;
    }
}
