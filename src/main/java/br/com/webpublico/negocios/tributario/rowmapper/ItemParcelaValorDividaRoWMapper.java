package br.com.webpublico.negocios.tributario.rowmapper;

import br.com.webpublico.entidades.ItemParcelaValorDivida;
import br.com.webpublico.entidades.ItemValorDivida;
import br.com.webpublico.entidades.Tributo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 23/09/13
 * Time: 13:46
 * To change this template use File | Settings | File Templates.
 */
public class ItemParcelaValorDividaRoWMapper implements RowMapper {

    private final boolean preencherTributosAcrescimos;

    public ItemParcelaValorDividaRoWMapper() {
        this.preencherTributosAcrescimos = false;
    }

    public ItemParcelaValorDividaRoWMapper(boolean preencherTributosAcrescimos) {
        this.preencherTributosAcrescimos = preencherTributosAcrescimos;
    }

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        ItemParcelaValorDivida item = new ItemParcelaValorDivida();
        item.setId(resultSet.getLong("ID"));
        Tributo tributo = criarTributo(resultSet, "TRIBUTO_ID", "TRIBUTO_DESCRICAO", "TIPOTRIBUTO");
        preencherTributosAcrescimos(tributo, resultSet);
        ItemValorDivida itemVD = new ItemValorDivida();
        itemVD.setTributo(tributo);
        item.setItemValorDivida(itemVD);
        item.setValor(resultSet.getBigDecimal("VALOR"));
        return item;
    }

    private void preencherTributosAcrescimos(Tributo tributo, ResultSet rs) throws SQLException{
        if (preencherTributosAcrescimos) {
            tributo.setTributoHonorarios(criarTributo(rs, "TRIBUTO_HON_ID", "TRIBUTO_HN_DESCRICAO", "TIPOTRIBUTO_HN"));
            tributo.setTributoCorrecaoMonetaria(criarTributo(rs, "TRIBUTO_CM_ID", "TRIBUTO_CM_DESCRICAO", "TIPOTRIBUTO_CM"));
            tributo.setTributoJuros(criarTributo(rs, "TRIBUTO_JUROS_ID", "TRIBUTO_JUROS_DESCRICAO", "TIPOTRIBUTO_JUROS"));
            tributo.setTributoMulta(criarTributo(rs, "TRIBUTO_MULTA_ID", "TRIBUTO_MULTA_DESCRICAO", "TIPOTRIBUTO_MULTA"));
        }
    }

    private Tributo criarTributo(ResultSet rs, String labelIdTributo, String labelTributoDescricao, String labelTipoTributo) throws SQLException {
        Tributo tributo = new Tributo();
        tributo.setId(rs.getLong(labelIdTributo));
        tributo.setDescricao(rs.getString(labelTributoDescricao));
        String tipotributo = rs.getString(labelTipoTributo);
        tributo.setTipoTributo(tipotributo != null ? Tributo.TipoTributo.valueOf(tipotributo) : null);

        return tributo;
    }
}
