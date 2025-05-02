package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.SaldoSubConta;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.SaldoSubContaSetter;
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
public class JdbcSaldoSubContaDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcSaldoSubContaDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void persistirSaldo(SaldoSubConta saldo) {
        List<SaldoSubConta> saldos = Lists.newArrayList();
        saldos.add(saldo);
        getJdbcTemplate().batchUpdate(SaldoSubContaSetter.SQL_INSERT, new SaldoSubContaSetter(saldos, geradorDeIds));
    }

    public void atualizarSaldo(SaldoSubConta saldo) {
        Object[] objetos = new Object[10];
        objetos[0] = saldo.getDataSaldo();
        objetos[1] = saldo.getValor();
        objetos[2] = saldo.getSubConta().getId();
        objetos[3] = saldo.getUnidadeOrganizacional().getId();
        objetos[4] = saldo.getFonteDeRecursos().getId();
        objetos[5] = saldo.getTotalCredito();
        objetos[6] = saldo.getTotalDebito();
        saldo.setVersao(saldo.getVersao() + 1l);
        objetos[7] = saldo.getVersao();
        objetos[8] = saldo.getContaDeDestinacao().getId();

        objetos[9] = saldo.getId();
        getJdbcTemplate().update(SaldoSubContaSetter.SQL_UPDATE, objetos);
    }
}
