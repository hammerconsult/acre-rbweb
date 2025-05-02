package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.SaldoExtraorcamentario;
import br.com.webpublico.entidades.SaldoSubConta;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.SaldoExtraSetter;
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
public class JdbcSaldoExtraDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcSaldoExtraDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void persistirSaldo(SaldoExtraorcamentario saldo) {
        List<SaldoExtraorcamentario> saldos = Lists.newArrayList();
        saldos.add(saldo);
        getJdbcTemplate().batchUpdate(SaldoExtraSetter.SQL_INSERT, new SaldoExtraSetter(saldos, geradorDeIds));
    }

    public void atualizarSaldo(SaldoExtraorcamentario saldo) {
        Object[] objetos = new Object[7];
        objetos[0] = saldo.getDataSaldo();
        objetos[1] = saldo.getValor();
        objetos[2] = saldo.getContaExtraorcamentaria().getId();
        objetos[3] = saldo.getFonteDeRecursos().getId();
        objetos[4] = saldo.getUnidadeOrganizacional().getId();
        objetos[5] = saldo.getContaDeDestinacao().getId();
        objetos[6] = saldo.getId();
        getJdbcTemplate().update(SaldoExtraSetter.SQL_UPDATE, objetos);
    }
}
