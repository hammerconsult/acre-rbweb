package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.SaldoContaContabil;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoBalancete;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.SaldoContaContabilRowMapper;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.SaldoContaContabilSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 23/10/14
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class JdbcSaldoContaContabilDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcSaldoContaContabilDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void persistirSaldo(SaldoContaContabil saldoContaContabil) {
        List<SaldoContaContabil> saldos = Lists.newArrayList();
        saldos.add(saldoContaContabil);
        getJdbcTemplate().batchUpdate(SaldoContaContabilSetter.SQL_INSERT, new SaldoContaContabilSetter(saldos, geradorDeIds));
    }

    public SaldoContaContabil buscarUltimoSaldoPorData(Date dataSaldo, Conta conta, UnidadeOrganizacional unidade, TipoBalancete tipoBalancete) {
        String sql = "select s.id," +
            "                s.DATASALDO," +
            "                s.CONTACONTABIL_ID, " +
            "                s.TIPOBALANCETE," +
            "                s.UNIDADEORGANIZACIONAL_ID," +
            "                s.TOTALCREDITO," +
            "                s.TOTALDEBITO  " +
            "   from SaldoContaContabil s "
            + " where trunc(s.dataSaldo) <= to_date(?,'dd/MM/yyyy') "
            + " and s.contaContabil_id  = ? "
            + " and s.unidadeOrganizacional_id = ? "
            + " and s.tipoBalancete = ? "
            + " order by s.dataSaldo desc";
        List<SaldoContaContabil> saldos = getJdbcTemplate().query(sql, new Object[]{DataUtil.getDataFormatada(dataSaldo), conta.getId(), unidade.getId(), tipoBalancete.name()}, new SaldoContaContabilRowMapper());
        if (!saldos.isEmpty()) {
            return saldos.get(0);
        } else {
            SaldoContaContabil ultimoSaldo = new SaldoContaContabil();
            ultimoSaldo.setContaContabil(conta);
            ultimoSaldo.setUnidadeOrganizacional(unidade);
            ultimoSaldo.setTipoBalancete(tipoBalancete);
            ultimoSaldo.setDataSaldo(dataSaldo);
            return ultimoSaldo;
        }
    }

    public List<SaldoContaContabil> buscarSaldosPosterioresAData(Date dataSaldo, Conta conta, UnidadeOrganizacional unidade, TipoBalancete tipoBalancete) {
        String sql = "select s.id," +
            "                s.DATASALDO," +
            "                s.CONTACONTABIL_ID, " +
            "                s.TIPOBALANCETE," +
            "                s.UNIDADEORGANIZACIONAL_ID," +
            "                s.TOTALCREDITO," +
            "                s.TOTALDEBITO  " +
            "    from SaldoContaContabil s "
            + " where trunc(s.dataSaldo) > to_date(?,'dd/MM/yyyy') "
            + " and s.contaContabil_id  = ? "
            + " and s.unidadeOrganizacional_id = ? "
            + " and s.tipoBalancete = ? "
            + " order by s.dataSaldo desc";
        return getJdbcTemplate().query(sql, new Object[]{DataUtil.getDataFormatada(dataSaldo), conta.getId(), unidade.getId(), tipoBalancete.name()}, new SaldoContaContabilRowMapper());
    }

    public void atualizarSaldo(SaldoContaContabil saldoContaContabil) {
        Object[] objetos = new Object[7];
        objetos[0] = saldoContaContabil.getDataSaldo();
        objetos[1] = saldoContaContabil.getTotalCredito();
        objetos[2] = saldoContaContabil.getTotalDebito();
        objetos[3] = saldoContaContabil.getContaContabil().getId();
        objetos[4] = saldoContaContabil.getUnidadeOrganizacional().getId();
        objetos[5] = saldoContaContabil.getTipoBalancete().name();
        objetos[6] = saldoContaContabil.getId();
        getJdbcTemplate().update(SaldoContaContabilSetter.SQL_UPDATE, objetos);
    }
}
