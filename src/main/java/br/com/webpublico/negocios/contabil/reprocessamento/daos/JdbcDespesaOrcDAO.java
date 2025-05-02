package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.DespesaORC;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.DespesaOrcSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 23/10/14
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class JdbcDespesaOrcDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcDespesaOrcDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void persistir(DespesaORC fonte) {
        List<DespesaORC> fontes = Lists.newArrayList();
        fontes.add(fonte);
        getJdbcTemplate().batchUpdate(DespesaOrcSetter.SQL_INSERT, new DespesaOrcSetter(fontes, geradorDeIds));
    }

    public void atualizar(DespesaORC mov) {
        Object[] objetos = new Object[6];

        objetos[0] = mov.getCodigo();
        objetos[1] = mov.getCodigoReduzido();
        objetos[2] = mov.getTipoDespesaORC().name();
        objetos[3] = mov.getExercicio().getId();
        objetos[4] = mov.getProvisaoPPADespesa().getId();

        objetos[5] = mov.getId();
        getJdbcTemplate().update(DespesaOrcSetter.SQL_UPDATE, objetos);
    }
}
