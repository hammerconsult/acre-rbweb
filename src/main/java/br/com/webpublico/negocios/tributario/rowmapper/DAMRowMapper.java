package br.com.webpublico.negocios.tributario.rowmapper;

import br.com.webpublico.entidades.DAM;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 24/09/13
 * Time: 08:52
 * To change this template use File | Settings | File Templates.
 */
public class DAMRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        DAM dam = new DAM();
        dam.setId(rs.getLong("ID"));
        dam.setNumeroDAM(rs.getString("NUMERODAM"));
        dam.setCodigoBarras(rs.getString("CODIGOBARRAS"));
        dam.setEmissao(rs.getDate("EMISSAO"));
        dam.setVencimento(rs.getDate("VENCIMENTO"));
        dam.setSituacao(DAM.Situacao.valueOf(rs.getString("SITUACAO")));
        dam.setTipo(DAM.Tipo.valueOf(rs.getString("TIPO")));
        dam.setValorOriginal(rs.getBigDecimal("VALORORIGINAL"));
        dam.setDesconto(rs.getBigDecimal("DESCONTO"));
        dam.setMulta(rs.getBigDecimal("MULTA"));
        dam.setJuros(rs.getBigDecimal("JUROS"));
        dam.setCorrecaoMonetaria(rs.getBigDecimal("CORRECAOMONETARIA"));
        return dam;
    }
}
