package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.MalaDiretaRBTransParcela;
import br.com.webpublico.entidades.MalaDiretaRBTransPermissao;
import br.com.webpublico.negocios.tributario.setter.MalaDiretaRBTransParcelaSetter;
import br.com.webpublico.negocios.tributario.setter.MalaDiretaRBTransPermissaoSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

@Repository("malaDiretaRBTransDAO")
public class JdbcMalaDiretaRBTransDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcMalaDiretaRBTransDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void persistirMalaDiretaRBTransPermissao(MalaDiretaRBTransPermissao malaDiretaRBTransPermissao) {
        String sql = " INSERT INTO MALADIRETARBTRANSPERMISSAO (ID, MALADIRETARBTRANS_ID, PERMISSAOTRANSPORTE_ID) VALUES (?,?,?) ";
        Long idMalaDiretaRBTransPermissao = geradorDeIds.getProximoId();
        getJdbcTemplate().batchUpdate(sql, new MalaDiretaRBTransPermissaoSetter(malaDiretaRBTransPermissao, idMalaDiretaRBTransPermissao));

        for (MalaDiretaRBTransParcela malaDiretaRBTransParcela : malaDiretaRBTransPermissao.getParcelas()) {
            malaDiretaRBTransParcela.getMalaDiretaRBTransPermissao().setId(idMalaDiretaRBTransPermissao);
            persistirMalaDiretaRBTransParcela(malaDiretaRBTransParcela);
        }
    }

    public void persistirMalaDiretaRBTransParcela(MalaDiretaRBTransParcela malaDiretaRBTransParcela) {
        String sql = " INSERT INTO MALADIRETARBTRANSPARCELA (ID, MALADIRETARBTRANSPERMISSAO_ID, PARCELAVALORDIVIDA_ID, DAM_ID) VALUES (?,?,?,?) ";
        getJdbcTemplate().batchUpdate(sql, new MalaDiretaRBTransParcelaSetter(malaDiretaRBTransParcela, geradorDeIds.getProximoId()));
    }
}
