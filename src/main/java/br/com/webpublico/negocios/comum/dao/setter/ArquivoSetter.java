package br.com.webpublico.negocios.comum.dao.setter;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by wellington on 27/06/17.
 */
public class ArquivoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT_ARQUIVO = " INSERT INTO ARQUIVO (ID, DESCRICAO," +
        "MIMETYPE, NOME, TAMANHO) VALUES (?, ?, ?, ?, ?) ";

    private final List<Arquivo> arquivos;
    private final SingletonGeradorId geradorDeIds;

    public ArquivoSetter(List<Arquivo> arquivos, SingletonGeradorId geradorDeIds) {
        this.arquivos = arquivos;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        Arquivo arquivo = arquivos.get(i);
        arquivo.setId(geradorDeIds.getProximoId());
        ps.setLong(1, arquivo.getId());
        ps.setString(2, arquivo.getDescricao());
        ps.setString(3, arquivo.getMimeType());
        ps.setString(4, arquivo.getNome());
        ps.setLong(5, arquivo.getTamanho());
    }

    @Override
    public int getBatchSize() {
        return arquivos.size();
    }
}
