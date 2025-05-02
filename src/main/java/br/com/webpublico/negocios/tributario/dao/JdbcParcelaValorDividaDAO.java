package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.DAM;
import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.entidades.SituacaoParcelaValorDivida;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoSituacaoCalculoISS;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Repository
public class JdbcParcelaValorDividaDAO extends JdbcDaoSupport implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(JdbcParcelaValorDividaDAO.class);

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcParcelaValorDividaDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public static JdbcParcelaValorDividaDAO getInstance() {
        return (JdbcParcelaValorDividaDAO) Util.getSpringBeanPeloNome("jdbcParcelaValorDividaDAO");
    }

    public void inserirSituacaoParcelaValorDivida(ParcelaValorDivida parcela, SituacaoParcelaValorDivida situacaoAtual, SituacaoParcela situacaoParcela) {
        inserirSituacaoParcelaValorDivida(parcela.getId(), situacaoAtual.getReferencia(), situacaoAtual.getSaldo(), situacaoParcela);
    }

    public void inserirSituacaoParcelaValorDivida(Long idParcela, String referencia, BigDecimal saldo, SituacaoParcela situacaoParcela) {
        String sql = "INSERT INTO SITUACAOPARCELAVALORDIVIDA" +
            "(ID, DATALANCAMENTO, SITUACAOPARCELA, PARCELA_ID, SALDO, REFERENCIA ) " +
            " VALUES(?,?,?,?,?,?) ";

        getJdbcTemplate().update(sql, new Object[]
            {
                geradorDeIds.getProximoId(),
                new Date(),
                situacaoParcela.name(),
                idParcela,
                saldo,
                referencia
            });
    }

    public void abrirOutraOpcaoPagamentoBaixado(ParcelaValorDivida parcelaValorDivida) {
        String sql = "insert into situacaoparcelavalordivida (id, datalancamento, situacaoparcela, parcela_id, saldo, inconsistente, referencia) " +
            "select hibernate_sequence.nextval, current_date, ?, parcela.id, 0, 0, situacao.referencia from ParcelaValorDivida parcela " +
            "inner join SituacaoParcelaValorDivida situacao on parcela.situacaoAtual_id = situacao.id " +
            "where parcela.valorDivida_id = ? and parcela.opcaoPagamento_id <> ? and situacao.situacaoParcela = ?";

        getJdbcTemplate().update(sql, new Object[] {SituacaoParcela.EM_ABERTO.name(),
                parcelaValorDivida.getValorDivida().getId(),
                parcelaValorDivida.getOpcaoPagamento().getId(),
                SituacaoParcela.BAIXADO_OUTRA_OPCAO.name()});
    }

    public void baixarOutraOpcaoPagamentoEmAberto(ParcelaValorDivida parcelaValorDivida) {
        String sql = "insert into situacaoparcelavalordivida (id, datalancamento, situacaoparcela, parcela_id, saldo, inconsistente, referencia) " +
            "select hibernate_sequence.nextval, current_date, ?, parcela.id, 0, 0, situacao.referencia from ParcelaValorDivida parcela " +
            "inner join SituacaoParcelaValorDivida situacao on parcela.situacaoAtual_id = situacao.id " +
            "where parcela.valorDivida_id = ? and parcela.opcaoPagamento_id <> ? and situacao.situacaoParcela = ?";

        getJdbcTemplate().update(sql, new Object[] {SituacaoParcela.BAIXADO_OUTRA_OPCAO.name(),
            parcelaValorDivida.getValorDivida().getId(),
            parcelaValorDivida.getOpcaoPagamento().getId(),
            SituacaoParcela.EM_ABERTO.name()});
    }

    public void estornarLancamentoDeIss(ParcelaValorDivida parcela) {
        String sql = "update CalculoIss set tipoSituacaoCalculoISS = ? where id in " +
            "(select vd.calculo_id from ValorDivida vd inner join parcelaValorDivida pvd on pvd.valorDivida_id = vd.id where pvd.id = ?) ";
        getJdbcTemplate().update(sql, new Object[] {TipoSituacaoCalculoISS.ESTORNADO.name(), parcela.getId()});
    }

    public boolean hasOutraOpcaoPagamento(ParcelaValorDivida parcelaValorDivida) {
       return getJdbcTemplate().queryForObject(" select count(1) " +
                "   from parcelavalordivida pvd " +
                " where pvd.valordivida_id = ? and pvd.opcaopagamento_id <> ? ",
            new Object[]{parcelaValorDivida.getValorDivida().getId(),
                parcelaValorDivida.getOpcaoPagamento().getId()}, Integer.class) > 0;
    }

    public boolean hasOutraOpcaoPagamentoPago(ParcelaValorDivida parcelaValorDivida) {
        return getJdbcTemplate().queryForObject(" select count(1) " +
                "   from parcelavalordivida pvd " +
                "  inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
                " where pvd.valordivida_id = ? " +
                "   and pvd.opcaopagamento_id <> ?" +
                "   and spvd.situacaoparcela = ? ",
            new Object[]{parcelaValorDivida.getValorDivida().getId(),
                parcelaValorDivida.getOpcaoPagamento().getId(),
            SituacaoParcela.PAGO.name()}, Integer.class) > 0;
    }
}
