package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.ItemInscricaoDividaAtiva;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class CalculoDividaAtivaSetter implements BatchPreparedStatementSetter {

    private final List<ItemInscricaoDividaAtiva> calculos;
    private final List<Long> ids;


    public CalculoDividaAtivaSetter(List<ItemInscricaoDividaAtiva> calculos, List<Long> ids) {
        this.calculos = calculos;
        this.ids = ids;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ItemInscricaoDividaAtiva calculo = calculos.get(i);
        calculo.setId(ids.get(i));
        ps.setLong(1, calculo.getId());
        ps.setDate(2, new java.sql.Date(calculo.getDataCalculo().getTime()));
        ps.setBoolean(3, calculo.getSimulacao());
        ps.setBigDecimal(4, calculo.getValorReal());
        ps.setBigDecimal(5, calculo.getValorEfetivo());
        ps.setBoolean(6, calculo.getIsento());
        if (calculo.getResultadoParcela() != null && calculo.getResultadoParcela().getIdCadastro() != null) {
            ps.setLong(7, calculo.getResultadoParcela().getIdCadastro());
        } else {
            ps.setNull(7, Types.NUMERIC);
        }
        ps.setLong(8, calculo.getSubDivida());
        ps.setString(9, calculo.getTipoCalculo().name());
        ps.setBoolean(10, calculo.getConsistente());
        ps.setLong(11, calculo.getProcessoCalculo().getId());
        ps.setString(12, calculo.getReferencia());
    }

    @Override
    public int getBatchSize() {
        return calculos.size();
    }
}
