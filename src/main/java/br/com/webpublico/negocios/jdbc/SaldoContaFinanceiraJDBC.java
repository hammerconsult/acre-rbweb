package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.MovimentacaoFinanceira;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by fernando on 30/01/15.
 */
public class SaldoContaFinanceiraJDBC extends ClassPatternJDBC {

    private static SaldoContaFinanceiraJDBC instancia;
    private PreparedStatement psSelectUltimoSaldoSubconta;
    private PreparedStatement psSelectSaldosSubcontaFuturos;
    private PreparedStatement psInsertSaldoSubconta;
    private PreparedStatement psUpdateSaldoSubconta;
    private PreparedStatement psInsertMovimentoContaFinanceira;
    private IdGenerator idGenerator = IdGenerator.createInstance(conexao);
    private HashMap<MapSaldoSubconta, List<SaldoSubConta>> saldosByMap = new HashMap<>();

    private SaldoContaFinanceiraJDBC(Connection conn) {
        this.conexao = conn;
    }

    public static synchronized SaldoContaFinanceiraJDBC createInstance(Connection conn) {
        if (instancia == null) {
            instancia = new SaldoContaFinanceiraJDBC(conn);
        }
        showConnection(instancia.getClass());
        return instancia;
    }

    public void createSaldoContaFinanceira(Pagamento pag, TipoOperacao tipo, MovimentacaoFinanceira movi) {
        if (allFieldsNotNull(pag, tipo)) {
            try {
                SaldoSubConta saldo = selectUltimoSaldoSubconta(pag.getSubConta(), pag.getLiquidacao().getEmpenho().getUnidadeOrganizacional(),
                        (ContaDeDestinacao) pag.getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos(),
                        pag.getDataPagamento());
                if (haveFunds(saldo, pag.getValorFinal(), tipo)) {
                    if (saldo.getId() != null) {
                        if (DataUtil.compararDatas(saldo.getDataSaldo(), pag.getDataPagamento())) {
                            atualizarValorSaldo(saldo, pag.getValorFinal(), tipo);
                            updateSaldoSubconta(saldo);
                        } else {
                            saldo.setId(idGenerator.createID());
                            saldo.setDataSaldo(pag.getDataPagamento());
                            atualizarValorSaldo(saldo, pag.getValorFinal(), tipo);
                            insertSaldoSubconta(saldo);
                        }
                    } else {
                        saldo.setId(idGenerator.createID());
                        atualizarValorSaldo(saldo, pag.getValorFinal(), tipo);
                        insertSaldoSubconta(saldo);
                    }
                } else {
                    throw new ExcecaoNegocioGenerica(" Saldo indisponível na Conta Financeira: " + pag.getSubConta() + " para essa operação. Saldo: " + "<b>" + Util.formataValor(saldo.getSaldoDoDia()) + "</b>.");
                }
                createMovimentoContaFinanceira(pag, tipo, movi);
                processaSaldosFuturos(pag.getSubConta(), pag.getLiquidacao().getEmpenho().getUnidadeOrganizacional(),
                        (ContaDeDestinacao) pag.getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos(),
                        pag.getDataPagamento(), pag.getValorFinal(), tipo);
            } catch (SQLException ex) {
                throw new ExcecaoNegocioGenerica("Erro ao gerar saldo da conta financeira: " + ex.getMessage(), ex);
            }
        }
    }

    private boolean haveFunds(SaldoSubConta saldo, BigDecimal valor, TipoOperacao tipo) {
        if (tipo.equals(TipoOperacao.DEBITO)) {
            return (saldo.getSaldoDoDia().subtract(valor)).compareTo(BigDecimal.ZERO) >= 0;
        }
        return true;
    }

    private void atualizarValorSaldo(SaldoSubConta saldo, BigDecimal valor, TipoOperacao tipo) {
        switch (tipo) {
            case CREDITO:
                saldo.setTotalCredito(saldo.getTotalCredito().add(valor));
                break;
            case DEBITO:
                saldo.setTotalDebito(saldo.getTotalDebito().add(valor));
                break;
            default:
                throw new ExcecaoNegocioGenerica("Erro ao manipular saldo! Nenhum tipo de operação foi informado!");
        }
    }

    private SaldoSubConta selectUltimoSaldoSubconta(SubConta subConta, UnidadeOrganizacional unidade, ContaDeDestinacao contaDeDestinacao, Date dataSaldo) throws SQLException {
        if (allFieldsNotNull(subConta, unidade, contaDeDestinacao, dataSaldo)) {
            MapSaldoSubconta map = new MapSaldoSubconta(subConta, unidade, contaDeDestinacao);
            if (!saldosByMap.containsKey(map)) {
                loadSaldosSubcontaByMap(map);
            }
            if (saldosByMap.get(map).isEmpty()) {
                return new SaldoSubConta(dataSaldo, contaDeDestinacao, unidade, subConta);
            } else {
                return getSaldoByData(map, dataSaldo);
            }
        } else {
            throw new ExcecaoNegocioGenerica("Atributos nulos para buscar saldo de subconta: " + subConta + "/" + unidade + "/" + contaDeDestinacao + "/" + dataSaldo);
        }
    }

    private SaldoSubConta getSaldoByData(MapSaldoSubconta map, Date data) {
        SaldoSubConta temp = new SaldoSubConta(data, map.getContaDeDestinacao(), map.getUnidadeOrganizacional(), map.getSubConta());
        for(SaldoSubConta saldo : saldosByMap.get(map)) {
            if(saldo.getDataSaldo().equals(data)) {
                return saldo;
            }
            if(saldo.getDataSaldo().before(data) && saldo.getDataSaldo().after(temp.getDataSaldo())) {
                temp = saldo;
            }
        }
        return temp;
    }

    private void loadSaldosSubcontaByMap(MapSaldoSubconta map) throws SQLException {
        List<SaldoSubConta> saldos = new ArrayList<>();
        preparaSelectUltimoSaldoSubconta();
        psSelectUltimoSaldoSubconta.clearParameters();
        psSelectUltimoSaldoSubconta.setLong(1, map.getSubConta().getId());
        psSelectUltimoSaldoSubconta.setLong(2, map.getUnidadeOrganizacional().getId());
        psSelectUltimoSaldoSubconta.setLong(3, map.getContaDeDestinacao().getId());
        try (ResultSet rs = psSelectUltimoSaldoSubconta.executeQuery()) {
            while (rs.next()) {
                SaldoSubConta saldo = new SaldoSubConta();
                saldo.setId(rs.getLong("id"));
                saldo.setDataSaldo(rs.getDate("datasaldo"));
                saldo.setValor(rs.getBigDecimal("valor"));
                saldo.setTotalCredito(rs.getBigDecimal("totalcredito"));
                saldo.setTotalDebito(rs.getBigDecimal("totaldebito"));
                saldo.setSubConta(map.getSubConta());
                saldo.setUnidadeOrganizacional(map.getUnidadeOrganizacional());
                saldo.setContaDeDestinacao(map.getContaDeDestinacao());
                saldo.setFonteDeRecursos(map.getContaDeDestinacao().getFonteDeRecursos());
                saldos.add(saldo);
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar último saldo: " + ex.getMessage(), ex);
        }
        saldosByMap.put(map, saldos);
    }

    private void preparaSelectUltimoSaldoSubconta() throws SQLException {
        if (psSelectUltimoSaldoSubconta == null) {
            String sql = "select " +
                    "  s.id as id " +
                    "  , s.datasaldo as datasaldo " +
                    "  , s.valor as valor " +
                    "  , s.totalcredito as totalcredito " +
                    "  , s.totaldebito as totaldebito " +
                    "from saldosubconta s " +
                    "where s.subconta_id = ? " +
                    "and s.unidadeorganizacional_id = ? " +
                    "and s.contadedestinacao_id = ? " +
//                    "and s.datasaldo <= to_date(?, 'dd/MM/yyyy') " +
                    "order by s.datasaldo desc";
            psSelectUltimoSaldoSubconta = this.conexao.prepareStatement(sql);
        }
    }

    private void updateSaldoSubconta(SaldoSubConta saldo) {
        try {
            preparaUpdateSaldoSubconta();
            psUpdateSaldoSubconta.clearParameters();
            psUpdateSaldoSubconta.setBigDecimal(1, saldo.getValor());
            psUpdateSaldoSubconta.setBigDecimal(2, saldo.getTotalCredito());
            psUpdateSaldoSubconta.setBigDecimal(3, saldo.getTotalDebito());
            psUpdateSaldoSubconta.setLong(4, saldo.getId());
            psUpdateSaldoSubconta.executeUpdate();
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao atualizar saldo: " + ex.getMessage(), ex);
        }
    }

    private void preparaUpdateSaldoSubconta() throws SQLException {
        if (psUpdateSaldoSubconta == null) {
            String sql = "update saldosubconta set valor = ?, totalcredito = ?, totaldebito = ? where id = ?";
            psUpdateSaldoSubconta = this.conexao.prepareStatement(sql);
        }
    }

    private void insertSaldoSubconta(SaldoSubConta saldo) {
        try {
            preparaInsertSaldoSubconta();
            psInsertSaldoSubconta.clearParameters();
            psInsertSaldoSubconta.setLong(1, saldo.getId());
            psInsertSaldoSubconta.setDate(2, new java.sql.Date(saldo.getDataSaldo().getTime()));
            psInsertSaldoSubconta.setBigDecimal(3, saldo.getValor());
            psInsertSaldoSubconta.setBigDecimal(4, saldo.getTotalCredito());
            psInsertSaldoSubconta.setBigDecimal(5, saldo.getTotalDebito());
            psInsertSaldoSubconta.setLong(6, saldo.getSubConta().getId());
            psInsertSaldoSubconta.setLong(7, saldo.getUnidadeOrganizacional().getId());
            psInsertSaldoSubconta.setLong(8, saldo.getContaDeDestinacao().getId());
            psInsertSaldoSubconta.setLong(9, saldo.getFonteDeRecursos().getId());
            psInsertSaldoSubconta.executeUpdate();
            addSaldoSubcontaInMap(saldo);
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao inserir novo saldo da conta financeira: " + ex.getMessage(), ex);
        }
    }

    private void addSaldoSubcontaInMap(SaldoSubConta saldo) {
        MapSaldoSubconta map = new MapSaldoSubconta(saldo.getSubConta(), saldo.getUnidadeOrganizacional(), saldo.getContaDeDestinacao());
        if(!saldosByMap.containsKey(map)) {
            List<SaldoSubConta> lista = new ArrayList<>();
            lista.add(saldo);
            saldosByMap.put(map, lista);
        } else {
            saldosByMap.get(map).add(saldo);
        }
    }

    private void preparaInsertSaldoSubconta() throws SQLException {
        if (psInsertSaldoSubconta == null) {
            String sql = "insert into saldosubconta (id, datasaldo, valor, totalcredito, totaldebito, subconta_id "
                + ", unidadeorganizacional_id, contadedestinacao_id, fontederecursos_id) values (?,?,?,?,?,?,?,?, ?)";
            psInsertSaldoSubconta = this.conexao.prepareStatement(sql);
        }
    }

    private void processaSaldosFuturos(SubConta subconta, UnidadeOrganizacional unidade, ContaDeDestinacao contaDeDestinacao, Date data, BigDecimal valor, TipoOperacao tipo) {
        try {
            List<SaldoSubConta> saldos = selectSaldosSubcontaFuturos(subconta, unidade, contaDeDestinacao, data);
            for (SaldoSubConta s : saldos) {
                if (haveFunds(s, valor, tipo)) {
                    atualizarValorSaldo(s, valor, tipo);
                    updateSaldoSubconta(s);
                } else {
                    throw new ExcecaoNegocioGenerica(" Saldo indisponível na conta financeira " + subconta + " em " + s.getDataSaldo().toString() + " para lançamento retroativo." + "<b>" + Util.formataValor(s.getSaldoDoDia()) + "</b>.");
                }
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao processar saldos futuros: " + ex.getMessage(), ex);
        }
    }

    private List<SaldoSubConta> selectSaldosSubcontaFuturos(SubConta subconta, UnidadeOrganizacional unidade, ContaDeDestinacao contaDeDestinacao, Date data) throws SQLException {
        List<SaldoSubConta> saldos = new ArrayList<>();
        preparaSelectSaldosSubcontaFuturos();
        psSelectSaldosSubcontaFuturos.clearParameters();
        psSelectSaldosSubcontaFuturos.setLong(1, subconta.getId());
        psSelectSaldosSubcontaFuturos.setLong(2, unidade.getId());
        psSelectSaldosSubcontaFuturos.setLong(3, contaDeDestinacao.getId());
        psSelectSaldosSubcontaFuturos.setDate(4, new java.sql.Date(data.getTime()));
        try (ResultSet rs = psSelectSaldosSubcontaFuturos.executeQuery()) {
            while (rs.next()) {
                SaldoSubConta saldo = new SaldoSubConta();
                saldo.setId(rs.getLong("id"));
                saldo.setDataSaldo(rs.getDate("datasaldo"));
                saldo.setValor(rs.getBigDecimal("valor"));
                saldo.setTotalCredito(rs.getBigDecimal("totalcredito"));
                saldo.setTotalDebito(rs.getBigDecimal("totaldebito"));
                saldo.setSubConta(subconta);
                saldo.setUnidadeOrganizacional(unidade);
                saldo.setContaDeDestinacao(contaDeDestinacao);
                saldo.setFonteDeRecursos(contaDeDestinacao.getFonteDeRecursos());
                saldos.add(saldo);
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar saldos futuros: " + ex.getMessage(), ex);
        }
        return saldos;
    }

    private void preparaSelectSaldosSubcontaFuturos() throws SQLException {
        if (psSelectSaldosSubcontaFuturos == null) {
            String sql = "select " +
                    "  s.id as id " +
                    "  , s.datasaldo as datasaldo " +
                    "  , s.valor as valor " +
                    "  , s.totalcredito as totalcredito " +
                    "  , s.totaldebito as totaldebito " +
                    "from saldosubconta s " +
                    "where subconta_id = ? " +
                    "and unidadeorganizacional_id = ? " +
                    "and contadedestinacao_id = ? " +
                    "and s.datasaldo > to_date(?, 'dd/MM/yyyy'))";
            psSelectSaldosSubcontaFuturos = this.conexao.prepareStatement(sql);
        }
    }

    private void createMovimentoContaFinanceira(Pagamento pag, TipoOperacao tipo, MovimentacaoFinanceira mov) {
        if (allFieldsNotNull(pag, tipo, mov)) {
            try {
                preparaInsertMovimentoContaFinanceira();
                psInsertMovimentoContaFinanceira.clearParameters();
                psInsertMovimentoContaFinanceira.setLong(1, idGenerator.createID());
                psInsertMovimentoContaFinanceira.setDate(2, new java.sql.Date(pag.getDataPagamento().getTime()));
                psInsertMovimentoContaFinanceira.setString(3, pag.getHistoricoRazao());
                psInsertMovimentoContaFinanceira.setString(4, mov.name());
                if (tipo.equals(TipoOperacao.DEBITO)) {
                    psInsertMovimentoContaFinanceira.setBigDecimal(5, pag.getValorFinal());
                    psInsertMovimentoContaFinanceira.setBigDecimal(6, BigDecimal.ZERO);
                } else {
                    psInsertMovimentoContaFinanceira.setBigDecimal(5, BigDecimal.ZERO);
                    psInsertMovimentoContaFinanceira.setBigDecimal(6, pag.getValorFinal());
                }
                psInsertMovimentoContaFinanceira.setLong(7, pag.getLiquidacao().getEmpenho().getUnidadeOrganizacional().getId());
                psInsertMovimentoContaFinanceira.setLong(8, pag.getSubConta().getId());
                psInsertMovimentoContaFinanceira.setLong(9, pag.getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos().getId());
                psInsertMovimentoContaFinanceira.setLong(10, ((ContaDeDestinacao) pag.getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos().getId());
                psInsertMovimentoContaFinanceira.setLong(11, pag.getEventoContabil().getId());
                psInsertMovimentoContaFinanceira.executeUpdate();
            } catch (SQLException ex) {
                throw new ExcecaoNegocioGenerica("Erro ao inserir movimento de conta financeira: " + ex.getMessage(), ex);
            }
        }
    }

    private void preparaInsertMovimentoContaFinanceira() throws SQLException {
        if (psInsertMovimentoContaFinanceira == null) {
            String sql = "insert into movimentocontafinanceira(id, datasaldo, historico, movimentacaofinanceira, totaldebito, totalcredito " +
                    ", unidadeorganizacional_id, subconta_id, contadedestinacao_id, fontederecursos_id, eventocontabil_id) values (?,?,?,?,?,?,?,?,?,?,?)";
            psInsertMovimentoContaFinanceira = this.conexao.prepareStatement(sql);
        }
    }
}
