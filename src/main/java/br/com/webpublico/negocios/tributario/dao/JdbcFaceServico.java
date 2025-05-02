package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.FaceServico;
import br.com.webpublico.negocios.tributario.setter.FaceServicoSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

/**
 * Created by William on 03/03/2017.
 */
@Repository(value = "persisteFaceServico")
public class JdbcFaceServico extends JdbcDaoSupport implements Serializable {


    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcFaceServico(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public synchronized void salvar(FaceServico face) {
        String sql = "INSERT INTO FACESERVICO " +
            "(ID, ANO, DATAREGISTRO, FACE_ID, SERVICOURBANO_ID) " +
            "VALUES (?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new FaceServicoSetter(face, geradorDeIds));

    }
}
