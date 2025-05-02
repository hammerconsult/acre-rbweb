package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.ProvisaoPPADespesa;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.ProvisaoPPADespesaSetter;
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
public class JdbcProvisaoPPADespesaDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcProvisaoPPADespesaDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void persistir(ProvisaoPPADespesa fonte) {
        List<ProvisaoPPADespesa> fontes = Lists.newArrayList();
        fontes.add(fonte);
        getJdbcTemplate().batchUpdate(ProvisaoPPADespesaSetter.SQL_INSERT, new ProvisaoPPADespesaSetter(fontes, geradorDeIds));
    }

    public void atualizar(ProvisaoPPADespesa mov) {
        Object[] objetos = new Object[10];

        objetos[0] = mov.getCodigo();
        objetos[1] = mov.getDataRegistro();
        objetos[2] = mov.getSomenteLeitura();
        objetos[3] = mov.getTipoDespesaORC().name();
        objetos[4] = mov.getValor();
        objetos[5] = mov.getContaDeDespesa().getId();
        if (mov.getOrigem() != null) {
            objetos[6] = mov.getOrigem().getId();
        } else {
            objetos[6] = null;
        }
        objetos[7] = mov.getUnidadeOrganizacional().getId();
        objetos[8] = mov.getSubAcaoPPA().getId();

        objetos[9] = mov.getId();
        getJdbcTemplate().update(ProvisaoPPADespesaSetter.SQL_UPDATE, objetos);
    }
}
