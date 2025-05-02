package br.com.webpublico.negocios.tributario.rowmapper;

import br.com.webpublico.entidades.ItemDAM;
import br.com.webpublico.entidades.OpcaoPagamento;
import br.com.webpublico.entidades.ParcelaValorDivida;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 24/09/13
 * Time: 09:26
 * To change this template use File | Settings | File Templates.
 */
public class ItemDAMRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        ItemDAM itemDAM = new ItemDAM();
        itemDAM.setId(rs.getLong("ID"));
        ParcelaValorDivida parcelaValorDivida = new ParcelaValorDivida();
        parcelaValorDivida.setOpcaoPagamento(new OpcaoPagamento());
        parcelaValorDivida.setId(rs.getLong("PARCELA_ID"));
        parcelaValorDivida.setSequenciaParcela(rs.getString("PARCELA_SEQUENCIA"));
        parcelaValorDivida.setVencimento(rs.getDate("PARCELA_VENCIMENTO"));
        parcelaValorDivida.setValor(rs.getBigDecimal("PARCELA_VALOR"));
        itemDAM.setParcela(parcelaValorDivida);
        itemDAM.setValorOriginalDevido(rs.getBigDecimal("VALORORIGINALDEVIDO"));
        itemDAM.setDesconto(rs.getBigDecimal("DESCONTO"));
        itemDAM.setMulta(rs.getBigDecimal("MULTA"));
        itemDAM.setJuros(rs.getBigDecimal("JUROS"));
        itemDAM.setCorrecaoMonetaria(rs.getBigDecimal("CORRECAOMONETARIA"));
        return itemDAM;
    }
}
