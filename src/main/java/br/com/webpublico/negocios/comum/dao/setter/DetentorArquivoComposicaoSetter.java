package br.com.webpublico.negocios.comum.dao.setter;

import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by wellington on 27/06/17.
 */
public class DetentorArquivoComposicaoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT_DETENTOR_ARQUIVO_COMPOSICAO = "INSERT INTO DETENTORARQUIVOCOMPOSICAO (ID) VALUES (?)";

    private final List<DetentorArquivoComposicao> detentoresArquivoComposicao;
    private final SingletonGeradorId geradorDeIds;

    public DetentorArquivoComposicaoSetter(List<DetentorArquivoComposicao> detentoresArquivoComposicao, SingletonGeradorId geradorDeIds) {
        this.detentoresArquivoComposicao = detentoresArquivoComposicao;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        DetentorArquivoComposicao detentorArquivoComposicao = detentoresArquivoComposicao.get(i);
        detentorArquivoComposicao.setId(geradorDeIds.getProximoId());
        ps.setLong(1, detentorArquivoComposicao.getId());
    }

    @Override
    public int getBatchSize() {
        return detentoresArquivoComposicao.size();
    }
}
