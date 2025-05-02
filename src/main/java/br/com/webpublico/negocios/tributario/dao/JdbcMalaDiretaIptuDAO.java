package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.CadastroMalaDiretaIPTU;
import br.com.webpublico.entidades.ParcelaMalaDiretaIPTU;
import br.com.webpublico.negocios.tributario.setter.CadastroMalaDiretaIptuSetter;
import br.com.webpublico.negocios.tributario.setter.ParcelaMalaDiretaIptuSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

@Repository("malaDiretaIptuDAO")
public class JdbcMalaDiretaIptuDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcMalaDiretaIptuDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void persisteCadastroMalaDiretaIptu(CadastroMalaDiretaIPTU cadastroMalaDiretaIPTU) {
        String sql = " INSERT INTO CADASTROMALADIRETAIPTU (ID, MALADIRETAIPTU_ID, CADASTROIMOBILIARIO_ID) VALUES (?,?,?) ";
        Long idCadastroMalaDireta = geradorDeIds.getProximoId();
        getJdbcTemplate().batchUpdate(sql, new CadastroMalaDiretaIptuSetter(cadastroMalaDiretaIPTU, idCadastroMalaDireta));

        for(ParcelaMalaDiretaIPTU parcelaMalaDiretaIPTU : cadastroMalaDiretaIPTU.getParcelaMalaDiretaIPTU()){
            parcelaMalaDiretaIPTU.getCadastroMalaDiretaIPTU().setId(idCadastroMalaDireta);
            persisteParcelaMalaDiretaIptu(parcelaMalaDiretaIPTU);
        }
    }

    public void persisteParcelaMalaDiretaIptu(ParcelaMalaDiretaIPTU parcelaMalaDiretaIPTU) {
        String sql = " INSERT INTO PARCELAMALADIRETAIPTU (ID, PARCELA_ID, CADASTROMALADIRETAIPTU_ID) VALUES (?,?,?) ";
        getJdbcTemplate().batchUpdate(sql, new ParcelaMalaDiretaIptuSetter(parcelaMalaDiretaIPTU, geradorDeIds.getProximoId()));
    }
}
