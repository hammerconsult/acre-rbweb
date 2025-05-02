package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.ItemMalaDiretaGeral;
import br.com.webpublico.entidades.ParcelaMalaDiretaGeral;
import br.com.webpublico.negocios.tributario.setter.ItemMalaDiretaGeralSetter;
import br.com.webpublico.negocios.tributario.setter.ParcelaMalaDiretaGeralSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;

@Repository("malaDiretaGeralDAO")
public class JdbcMalaDiretaGeral extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcMalaDiretaGeral(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void persisteCadastroMalaDiretaIptu(ItemMalaDiretaGeral itemMalaDiretaGeral) {
        String sql = " INSERT INTO ITEMMALADIRETAGERAL (ID, MALADIRETAGERAL_ID, DAM_ID, CADASTRO_ID, PESSOA_ID, TEXTO) VALUES (?,?,?,?,?,?) ";
        Long idCadastroMalaDireta = geradorDeIds.getProximoId();
        getJdbcTemplate().batchUpdate(sql, new ItemMalaDiretaGeralSetter(itemMalaDiretaGeral, idCadastroMalaDireta));

        for (ParcelaMalaDiretaGeral parcelaMalaDiretaGeral : itemMalaDiretaGeral.getParcelas()) {
            parcelaMalaDiretaGeral.getItemMalaDiretaGeral().setId(idCadastroMalaDireta);
            persisteParcelaMalaDiretaIptu(parcelaMalaDiretaGeral);
        }
    }

    public void persisteParcelaMalaDiretaIptu(ParcelaMalaDiretaGeral parcelaMalaDiretaGeral) {
        String sql = " INSERT INTO PARCELAMALADIRETAGERAL (ID, PARCELA_ID, ITEMMALADIRETAGERAL_ID) VALUES (?,?,?) ";
        getJdbcTemplate().batchUpdate(sql, new ParcelaMalaDiretaGeralSetter(parcelaMalaDiretaGeral, geradorDeIds.getProximoId()));
    }
}
