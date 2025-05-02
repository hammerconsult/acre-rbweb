package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.MovimentoContaFinanceira;
import br.com.webpublico.entidades.MovimentoDespesaORC;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.MovimentoContaFinanceiraSetter;
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
public class JdbcMovimentoContaFinanceiraOrcDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcMovimentoContaFinanceiraOrcDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void persistirSaldo(MovimentoContaFinanceira saldo) {
        List<MovimentoContaFinanceira> saldos = Lists.newArrayList();
        saldos.add(saldo);
        getJdbcTemplate().batchUpdate(MovimentoContaFinanceiraSetter.SQL_INSERT, new MovimentoContaFinanceiraSetter(saldos, geradorDeIds));
    }

    public void atualizar(MovimentoContaFinanceira mov) {
        Object[] objetos = new Object[10];
        objetos[0] = mov.getDataSaldo();
        objetos[1] = mov.getUnidadeOrganizacional().getId();
        objetos[2] = mov.getSubConta().getId();
        objetos[3] = mov.getFonteDeRecursos().getId();
        objetos[4] = mov.getEventoContabil().getId();
        objetos[5] = mov.getHistorico();
        objetos[6] = mov.getMovimentacaoFinanceira().name();
        objetos[7] = mov.getTotalDebito();
        objetos[8] = mov.getTotalCredito();
        objetos[9] = mov.getUuid();
        objetos[10] = mov.getContaDeDestinacao().getId();
        objetos[11] = mov.getId();
        getJdbcTemplate().update(MovimentoContaFinanceiraSetter.SQL_UPDATE, objetos);
    }
}
