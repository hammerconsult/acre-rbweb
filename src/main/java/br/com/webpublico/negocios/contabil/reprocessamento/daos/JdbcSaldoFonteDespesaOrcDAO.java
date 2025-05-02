package br.com.webpublico.negocios.contabil.reprocessamento.daos;

import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.entidades.MovimentoDespesaORC;
import br.com.webpublico.entidades.SaldoFonteDespesaORC;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.SaldoFonteDespesaORCSetter;
import br.com.webpublico.negocios.contabil.reprocessamento.setters.SaldoFonteDespesaOrcRowMapper;
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
public class JdbcSaldoFonteDespesaOrcDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcSaldoFonteDespesaOrcDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void persistirSaldo(SaldoFonteDespesaORC saldo) {
        List<SaldoFonteDespesaORC> saldos = Lists.newArrayList();
        saldos.add(saldo);
        getJdbcTemplate().batchUpdate(SaldoFonteDespesaORCSetter.SQL_INSERT, new SaldoFonteDespesaORCSetter(saldos, geradorDeIds));
    }

    public SaldoFonteDespesaORC buscarUltimoSaldoPorData(FonteDespesaORC fonteDespesaOrc, Date data, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = "select " +
            " f.ID, f.DOTACAO, f.EMPENHADO, f.LIQUIDADO, f.PAGO, f.DATASALDO, f.FONTEDESPESAORC_ID, f.ALTERACAO, f.RESERVADO," +
            " f.UNIDADEORGANIZACIONAL_ID, f.SUPLEMENTADO, f.REDUZIDO, f.EMPENHADOPROCESSADO, f.EMPENHADONAOPROCESSADO, " +
            " f.LIQUIDADOPROCESSADO, f.LIQUIDADONAOPROCESSADO, f.PAGOPROCESSADO, f.PAGONAOPROCESSADO, f.RESERVADOPORLICITACAO, f.VERSAO " +
            " from saldofontedespesaorc f "
            + " WHERE f.fonteDespesaORC_id = ? "
            + " AND f.unidadeOrganizacional_id = ? "
            + " AND trunc(f.dataSaldo) <= to_date(?, 'dd/MM/yyyy')"
            + " ORDER BY trunc(f.dataSaldo) desc ";
        List<SaldoFonteDespesaORC> saldos = getJdbcTemplate().query(sql, new Object[]{fonteDespesaOrc.getId(), unidadeOrganizacional.getId(), DataUtil.getDataFormatada(data)}, new SaldoFonteDespesaOrcRowMapper());
        if (!saldos.isEmpty()) {
            return saldos.get(0);
        } else {
            return null;
        }
    }

    public List<SaldoFonteDespesaORC> buscarSaldosFuturos(MovimentoDespesaORC movimento) {
        String sql = " select  " +
            " f.ID, f.DOTACAO, f.EMPENHADO, f.LIQUIDADO, f.PAGO, f.DATASALDO, f.FONTEDESPESAORC_ID, f.ALTERACAO, f.RESERVADO," +
            " f.UNIDADEORGANIZACIONAL_ID, f.SUPLEMENTADO, f.REDUZIDO, f.EMPENHADOPROCESSADO, f.EMPENHADONAOPROCESSADO, " +
            " f.LIQUIDADOPROCESSADO, f.LIQUIDADONAOPROCESSADO, f.PAGOPROCESSADO, f.PAGONAOPROCESSADO, f.RESERVADOPORLICITACAO, f.VERSAO " +
            "from SaldoFonteDespesaORC f "
            + " where f.fonteDespesaORC_id = ?"
            + " and f.unidadeOrganizacional_id = ?"
            + " and trunc(f.dataSaldo) > to_date(?, 'dd/MM/yyyy')"
            + " ORDER BY trunc(f.dataSaldo) ";
        List<SaldoFonteDespesaORC> saldos = getJdbcTemplate().query(sql, new Object[]{movimento.getFonteDespesaORC().getId(), movimento.getUnidadeOrganizacional().getId(), DataUtil.getDataFormatada(movimento.getDataMovimento())}, new SaldoFonteDespesaOrcRowMapper());
        if (!saldos.isEmpty()) {
            return saldos;
        } else {
            return null;
        }
    }

    public void atualizarSaldo(SaldoFonteDespesaORC saldo) {
        Object[] objetos = new Object[20];
        objetos[0] = saldo.getDotacao();
        objetos[1] = saldo.getEmpenhado();
        objetos[2] = saldo.getLiquidado();
        objetos[3] = saldo.getPago();
        objetos[4] = saldo.getDataSaldo();
        objetos[5] = saldo.getFonteDespesaORC().getId();
        objetos[6] = saldo.getAlteracao();
        objetos[7] = saldo.getReservado();
        objetos[8] = saldo.getUnidadeOrganizacional().getId();
        objetos[9] = saldo.getSuplementado();
        objetos[10] = saldo.getReduzido();
        objetos[11] = saldo.getEmpenhadoProcessado();
        objetos[12] = saldo.getEmpenhadoNaoProcessado();
        objetos[13] = saldo.getLiquidadoProcessado();
        objetos[14] = saldo.getLiquidadoNaoProcessado();
        objetos[15] = saldo.getPagoProcessado();
        objetos[16] = saldo.getPagoNaoProcessado();
        objetos[17] = saldo.getReservadoPorLicitacao();
        saldo.setVersao(saldo.getVersao() + 1l);
        objetos[18] = saldo.getVersao();
        objetos[19] = saldo.getId();
        getJdbcTemplate().update(SaldoFonteDespesaORCSetter.SQL_UPDATE, objetos);
    }
}
