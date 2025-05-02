package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.FaceValor;
import br.com.webpublico.negocios.tributario.setter.FaceValorSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

/**
 * Created by William on 03/03/2017.
 */
@Repository(value = "persisteFaceValor")
public class JdbcFaceValor extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcFaceValor(DataSource dataSource) {
        setDataSource(dataSource);
    }


    public synchronized void salvar(FaceValor face) {
        String sql = "INSERT INTO FACEVALOR " +
            "(ID, EXERCICIO_ID, FACE_ID, VALOR, QUADRA_ID) " +
            "VALUES (?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new FaceValorSetter(face, geradorDeIds));

    }
}
