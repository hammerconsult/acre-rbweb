package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.MovimentoDespesaORC;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.MovimentoDespesaOrcSetter;
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
public class JdbcMovimentoFonteDespesaOrcDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcMovimentoFonteDespesaOrcDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void persistirSaldo(MovimentoDespesaORC saldo) {
        List<MovimentoDespesaORC> saldos = Lists.newArrayList();
        saldos.add(saldo);
        getJdbcTemplate().batchUpdate(MovimentoDespesaOrcSetter.SQL_INSERT, new MovimentoDespesaOrcSetter(saldos, geradorDeIds));
    }

    public void atualizar(MovimentoDespesaORC mov) {
        Object[] objetos = new Object[10];

        objetos[0] = mov.getDataMovimento();
        objetos[1] = mov.getTipoOperacaoORC().name();
        objetos[2] = mov.getOperacaoORC().name();
        objetos[3] = mov.getFonteDespesaORC().getId();
        objetos[4] = mov.getUnidadeOrganizacional().getId();
        objetos[5] = mov.getClasseOrigem();
        objetos[6] = mov.getNumeroMovimento();
        objetos[7] = mov.getHistorico();
        objetos[8] = mov.getIdOrigem();
        objetos[9] = mov.getId();
        getJdbcTemplate().update(MovimentoDespesaOrcSetter.SQL_UPDATE, objetos);
    }
}
