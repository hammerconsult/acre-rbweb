package br.com.webpublico.negocios.tributario.setter;

import br.com.webpublico.entidades.FaceServico;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by William on 03/03/2017.
 */
public class FaceServicoSetter implements BatchPreparedStatementSetter {
    private FaceServico faceServico;
    private SingletonGeradorId geradorDeIds;


    public FaceServicoSetter(FaceServico face, SingletonGeradorId gerador) {
        this.faceServico = face;
        this.geradorDeIds = gerador;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        faceServico.setId(geradorDeIds.getProximoId());
        ps.setLong(1, faceServico.getId());
        ps.setInt(2, faceServico.getAno());
        ps.setDate(3, new java.sql.Date(faceServico.getDataRegistro().getTime()));
        ps.setLong(4, faceServico.getFace().getId());
        ps.setLong(5, faceServico.getServicoUrbano().getId());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}

