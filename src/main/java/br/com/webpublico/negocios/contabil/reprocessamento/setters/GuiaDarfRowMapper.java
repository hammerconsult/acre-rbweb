package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.GuiaDARF;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuiaDarfRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        GuiaDARF guiaDARF = new GuiaDARF();
        guiaDARF.setId(rs.getLong("ID"));
        guiaDARF.setCodigoReceitaTributo(rs.getString("CODIGORECEITATRIBUTO"));
        guiaDARF.setCodigoIdentificacaoTributo(rs.getString("CODIGOIDENTIFICACAOTRIBUTO"));
        guiaDARF.setPeriodoApuracao(rs.getDate("PERIODOAPURACAO"));
        guiaDARF.setNumeroReferencia(rs.getString("NUMEROREFERENCIA"));
        guiaDARF.setValorPrincipal(rs.getBigDecimal("VALORPRINCIPAL"));
        guiaDARF.setValorMulta(rs.getBigDecimal("VALORMULTA"));
        guiaDARF.setValorJuros(rs.getBigDecimal("VALORJUROS"));
        guiaDARF.setDataVencimento(rs.getDate("DATAVENCIMENTO"));
        return guiaDARF;
    }
}
