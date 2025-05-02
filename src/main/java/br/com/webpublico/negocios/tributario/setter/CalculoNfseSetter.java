package br.com.webpublico.negocios.tributario.setter;

import br.com.webpublico.entidades.CalculoISS;
import br.com.webpublico.entidades.CalculoNfse;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 18/09/13
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
public class CalculoNfseSetter implements BatchPreparedStatementSetter {
    private List<CalculoNfse> calculos;

    public CalculoNfseSetter(List<CalculoNfse> calculos) {
        this.calculos = calculos;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        CalculoNfse c = calculos.get(i);
        ps.setLong(1, c.getId());
        ps.setLong(2, c.getProcessoCalculo().getId());
        ps.setInt(3, c.getIdentificacaoDaGuia());
        ps.setInt(4, c.getMesDeReferencia());
        ps.setInt(5, c.getAnoDeReferencia());
        ps.setTimestamp(6, new java.sql.Timestamp(c.getDataVencimentoDebito().getTime()));
        ps.setBigDecimal(7, c.getValorTotalDoDebito());
        ps.setBigDecimal(8, c.getValorDaMulta());
        ps.setBigDecimal(9, c.getValorDosJuros());
        ps.setBigDecimal(10, c.getValorDaCorrecao());
        ps.setInt(11, c.getTipoDoMovimento());
        ps.setTimestamp(12, new java.sql.Timestamp(c.getDataDoMovimento().getTime()));
        ps.setBigDecimal(13, c.getValorTotalPago());
        ps.setString(14, c.getNossoNumero());
        ps.setTimestamp(15, new java.sql.Timestamp(c.getVencimentoDam().getTime()));
    }

    @Override
    public int getBatchSize() {
        return calculos.size();
    }
}
