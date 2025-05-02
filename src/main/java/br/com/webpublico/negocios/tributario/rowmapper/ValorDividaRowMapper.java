package br.com.webpublico.negocios.tributario.rowmapper;

import br.com.webpublico.entidadesauxiliares.ResultadoValorDivida;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 19/09/13
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */
public class ValorDividaRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        ResultadoValorDivida resultadoValorDivida = new ResultadoValorDivida();
        resultadoValorDivida.setId(rs.getLong("ID_VALORDIVIDA"));
        resultadoValorDivida.setDividaId(rs.getLong("ID_DIVIDA"));
        resultadoValorDivida.setDividaDescricao(rs.getString("DESCRICAO_DIVIDA"));
        resultadoValorDivida.setExercicioAno(rs.getInt("ANO_EXERCICIO"));
        resultadoValorDivida.setSubDivida(rs.getLong("SD"));
        resultadoValorDivida.setTipoCadastro(rs.getString("TIPOCADASTRO"));
        resultadoValorDivida.setInscricao(rs.getString("INSCRICAO"));
        resultadoValorDivida.setValor(rs.getBigDecimal("VALOR"));
        resultadoValorDivida.setEmissao(rs.getDate("EMISSAO"));
        resultadoValorDivida.setCalculoId(rs.getLong("CALCULO_ID"));
        resultadoValorDivida.setTipoCalculo(rs.getString("TIPOCALCULO"));
        return resultadoValorDivida;
    }
}
