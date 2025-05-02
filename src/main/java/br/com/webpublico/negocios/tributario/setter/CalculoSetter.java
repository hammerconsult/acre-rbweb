package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.negocios.tributario.auxiliares.GeradorDeIds;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import javax.lang.model.type.NullType;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class CalculoSetter implements BatchPreparedStatementSetter {
    private List<? extends Calculo> calculos;
    private SingletonGeradorId geradorDeIds;


    public CalculoSetter(List<? extends Calculo> calculos, SingletonGeradorId gerador) {
        this.calculos = calculos;
        this.geradorDeIds = gerador;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        Calculo calculo = calculos.get(i);
        calculo.setId(geradorDeIds.getProximoId());

        ps.setLong(1, calculo.getId());
        ps.setDate(2, new java.sql.Date(calculo.getDataCalculo().getTime()));
        ps.setBoolean(3, calculo.getSimulacao());
        ps.setBigDecimal(4, calculo.getValorReal());
        ps.setBigDecimal(5, calculo.getValorEfetivo());
        ps.setBoolean(6, calculo.getIsento());
        if(calculo.getCadastro() != null){
            ps.setLong(7, calculo.getCadastro().getId());
        }else{
            ps.setNull(7, Types.NUMERIC);
        }
        if(calculo.getSubDivida() != null){
            ps.setLong(8, calculo.getSubDivida());
        }else{
            ps.setNull(8, Types.NUMERIC);
        }
        ps.setString(9, calculo.getTipoCalculo().name());
        ps.setBoolean(10, calculo.getConsistente());
        ps.setLong(11, calculo.getProcessoCalculo().getId());
        ps.setString(12, calculo.getReferencia());
        ps.setBoolean(13, calculo.isIsentaAcrescimos());
    }

    @Override
    public int getBatchSize() {
        return calculos.size();
    }
}
