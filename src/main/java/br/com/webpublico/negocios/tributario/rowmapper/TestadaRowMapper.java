package br.com.webpublico.negocios.tributario.rowmapper;

import br.com.webpublico.entidades.*;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 10/06/14
 * Time: 11:42
 * To change this template use File | Settings | File Templates.
 */
public class TestadaRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        Testada testada = new Testada();
        testada.setId(rs.getLong("TESTADA_ID"));
        Face face = new Face();
        face.setId(rs.getLong("FACE_ID"));
        LogradouroBairro logradouroBairro = new LogradouroBairro();
        logradouroBairro.setId(rs.getLong("LOGRADOUROBAIRRO_ID"));
        logradouroBairro.setCep(rs.getString("LOGRADOUROBAIRRO_CEP"));
        Bairro bairro = new Bairro();
        bairro.setId(rs.getLong("BAIRRO_ID"));
        bairro.setDescricao(rs.getString("BAIRRO_DESCRICAO"));
        Logradouro logradouro = new Logradouro();
        logradouro.setId(rs.getLong("LOGRADOURO_ID"));
        logradouro.setNome(rs.getString("LOGRADOURO_NOME"));
        TipoLogradouro tipoLogradouro = new TipoLogradouro();
        tipoLogradouro.setId(rs.getLong("TIPOLOGRADOURO_ID"));
        tipoLogradouro.setDescricao(rs.getString("TIPOLOGRADOURO_DESCRICAO"));
        logradouro.setTipoLogradouro(tipoLogradouro);
        logradouroBairro.setBairro(bairro);
        logradouroBairro.setLogradouro(logradouro);
        face.setLogradouroBairro(logradouroBairro);
        testada.setFace(face);
        return testada;
    }
}
