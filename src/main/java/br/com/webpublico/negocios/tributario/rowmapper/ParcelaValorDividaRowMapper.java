package br.com.webpublico.negocios.tributario.rowmapper;

import br.com.webpublico.entidades.OpcaoPagamento;
import br.com.webpublico.entidades.ParcelaValorDivida;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 23/09/13
 * Time: 09:51
 * To change this template use File | Settings | File Templates.
 */
public class ParcelaValorDividaRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        ParcelaValorDivida pc = new ParcelaValorDivida();
        pc.setId(resultSet.getLong("ID"));
        pc.setSequenciaParcela(resultSet.getString("SEQUENCIAPARCELA"));
        pc.setVencimento(resultSet.getDate("VENCIMENTO"));
        pc.setValor(resultSet.getBigDecimal("VALOR"));
        OpcaoPagamento opc = new OpcaoPagamento();
        opc.setId(resultSet.getLong("OPCAOPAGAMENTO_ID"));
        opc.setDescricao(resultSet.getString("OPCAOPAGAMENTO_DESCRICAO"));
        pc.setOpcaoPagamento(opc);
        pc.setDividaAtiva(resultSet.getBoolean("DIVIDAATIVA"));
        pc.setDividaAtivaAjuizada(resultSet.getBoolean("DIVIDAATIVAAJUIZADA"));
        return pc;
    }
}
