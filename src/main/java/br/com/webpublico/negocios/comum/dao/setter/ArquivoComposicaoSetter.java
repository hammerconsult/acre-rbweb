package br.com.webpublico.negocios.comum.dao.setter;

import br.com.webpublico.entidades.ArquivoComposicao;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by wellington on 27/06/17.
 */
public class ArquivoComposicaoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT_ARQUIVO_COMPOSICAO = " INSERT INTO ARQUIVOCOMPOSICAO (ID, DETENTORARQUIVOCOMPOSICAO_ID," +
        "ARQUIVO_ID, DATAUPLOAD) VALUES (?, ?, ?, ?) ";

    private final List<ArquivoComposicao> arquivosComposicao;
    private final SingletonGeradorId geradorDeIds;

    public ArquivoComposicaoSetter(List<ArquivoComposicao> arquivosComposicao, SingletonGeradorId geradorDeIds) {
        this.arquivosComposicao = arquivosComposicao;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ArquivoComposicao arquivoComposicao = arquivosComposicao.get(i);
        arquivoComposicao.setId(geradorDeIds.getProximoId());
        ps.setLong(1, arquivoComposicao.getId());
        ps.setLong(2, arquivoComposicao.getDetentorArquivoComposicao().getId());
        ps.setLong(3, arquivoComposicao.getArquivo().getId());
        ps.setDate(4, new java.sql.Date(arquivoComposicao.getDataUpload().getTime()));
    }

    @Override
    public int getBatchSize() {
        return arquivosComposicao.size();
    }
}
