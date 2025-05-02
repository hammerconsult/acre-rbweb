package br.com.webpublico.negocios.tributario.setter;

import br.com.webpublico.entidades.CalculoISS;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 18/09/13
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
public class CalculoIssSetter implements BatchPreparedStatementSetter {
    private List<CalculoISS> calculos;

    public CalculoIssSetter(List<CalculoISS> calculos) {
        this.calculos = calculos;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        CalculoISS c = calculos.get(i);

        ps.setLong(1, c.getId());
        ps.setLong(2, c.getProcessoCalculo().getId());
        ps.setLong(3, c.getCadastroEconomico().getId());
        ps.setString(4, c.getTipoCalculoISS().name());
        ps.setBigDecimal(5, c.getAliquota());
        ps.setBigDecimal(6, c.getBaseCalculo());
        ps.setBigDecimal(7, c.getValorCalculado());
        ps.setBigDecimal(8, c.getFaturamento());
        ps.setLong(9, c.getSequenciaLancamento());
        ps.setBigDecimal(10, c.getTaxaSobreIss());
        ps.setBoolean(11, c.getAusenciaMovimento());
        ps.setString(12, c.getTipoSituacaoCalculoISS().name());
        if (c.getUsuarioLancamento() != null) {
            ps.setLong(13, c.getUsuarioLancamento().getId());
        } else {
            ps.setNull(13, Types.NULL);
        }
        ps.setBoolean(14, c.getNotaEletronica());
        if (c.getIssqnFmTipoLancamentoNfse() != null) {
            ps.setString(15, c.getIssqnFmTipoLancamentoNfse().name());
        } else {
            ps.setNull(15, Types.NULL);
        }
    }

    @Override
    public int getBatchSize() {
        return calculos.size();
    }
}
