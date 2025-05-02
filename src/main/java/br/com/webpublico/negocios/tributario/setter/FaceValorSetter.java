package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.FaceValor;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FaceValorSetter implements BatchPreparedStatementSetter {
    private FaceValor faceValor;
    private SingletonGeradorId geradorDeIds;


    public FaceValorSetter(FaceValor face, SingletonGeradorId gerador) {
        this.faceValor = face;
        this.geradorDeIds = gerador;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        faceValor.setId(geradorDeIds.getProximoId());
        ps.setLong(1, faceValor.getId());
        ps.setLong(2, faceValor.getExercicio().getId());
        ps.setLong(3, faceValor.getFace().getId());
        ps.setBigDecimal(4, faceValor.getValor());
        ps.setLong(5, faceValor.getQuadra().getId());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
