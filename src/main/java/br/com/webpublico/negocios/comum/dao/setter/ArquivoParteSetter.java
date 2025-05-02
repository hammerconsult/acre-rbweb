package br.com.webpublico.negocios.comum.dao.setter;

import br.com.webpublico.entidades.ArquivoParte;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by wellington on 27/06/17.
 */
public class ArquivoParteSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT_ARQUIVO_PARTE = " INSERT INTO ARQUIVOPARTE (ID, ARQUIVO_ID," +
        "DADOS) VALUES (?, ?, ?) ";

    private final List<ArquivoParte> arquivosParte;
    private final SingletonGeradorId geradorDeIds;

    public ArquivoParteSetter(List<ArquivoParte> arquivosParte, SingletonGeradorId geradorDeIds) {
        this.arquivosParte = arquivosParte;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ArquivoParte arquivoParte = arquivosParte.get(i);
        arquivoParte.setId(geradorDeIds.getProximoId());
        ps.setLong(1, arquivoParte.getId());
        ps.setLong(2, arquivoParte.getArquivo().getId());
        ps.setBytes(3, arquivoParte.getDados());
    }

    @Override
    public int getBatchSize() {
        return arquivosParte.size();
    }
}
