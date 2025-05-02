package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.entidades.DividaPublica;
import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.entidades.SaldoDividaPublica;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.OperacaoDiarioDividaPublica;
import br.com.webpublico.enums.OperacaoMovimentoDividaPublica;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoORC;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.DataUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fernando on 30/01/15.
 */
public class SaldoDividaPublicaJDBC extends ClassPatternJDBC {

    private static SaldoDividaPublicaJDBC instancia;
    private PreparedStatement psSelectUltimoSaldoDividaPublica;
    private PreparedStatement psSelectSaldosDividaPublicaFuturos;
    private PreparedStatement psInsertSaldoDividaPublica;
    private PreparedStatement psUpdateSaldoDividaPublica;
    private PreparedStatement psInsertMovimentoDiarioDividaPublica;
    private IdGenerator idGenerator = IdGenerator.createInstance(conexao);

    private SaldoDividaPublicaJDBC(Connection conn) {
        this.conexao = conn;
    }

    public static synchronized SaldoDividaPublicaJDBC createInstance(Connection conn) {
        if (instancia == null) {
            instancia = new SaldoDividaPublicaJDBC(conn);
        }
        showConnection(instancia.getClass());
        return instancia;
    }

    public void createSaldoDividaPublica(Pagamento pag, TipoLancamento tipo, OperacaoMovimentoDividaPublica op, OperacaoDiarioDividaPublica opDiario) {
        if (allFieldsNotNull(pag, tipo, op, pag.getLiquidacao().getEmpenho().getDividaPublica())) {
            try {
                SaldoDividaPublica saldo = selectUltimoSaldoDividaPublica(pag.getDataPagamento(), pag.getUnidadeOrganizacional(), pag.getLiquidacao().getEmpenho().getDividaPublica());
                if (saldo.getId() != null) {
                    if (DataUtil.compararDatas(pag.getDataPagamento(), saldo.getData())) {
                        atualizaValorSaldo(saldo, pag.getValor(), op, tipo);
                        updateSaldoDividaPublica(saldo);
                    } else {
                        saldo.setData(pag.getDataPagamento());
                        atualizaValorSaldo(saldo, pag.getValor(), op, tipo);
                        insertSaldoDividaPublica(saldo);
                    }
                } else {
                    saldo.setId(idGenerator.createID());
                    saldo.setData(pag.getDataPagamento());
                    atualizaValorSaldo(saldo, pag.getValor(), op, tipo);
                    insertSaldoDividaPublica(saldo);
                }
                processaSaldosFuturos(pag, tipo, op);
                insertMovimentoDiarioDividaPublica(saldo, opDiario, tipo, pag.getValor());
            } catch (SQLException ex) {
                throw new ExcecaoNegocioGenerica("Erro ao gerar saldo da dívida pública: " + ex.getMessage(), ex);
            }
        }
    }

    private void atualizaValorSaldo(SaldoDividaPublica saldo, BigDecimal valor, OperacaoMovimentoDividaPublica op, TipoLancamento tipo) {
        switch (op) {
            case INSCRICAO_PRINCIPAL:
                somaOuSubtrai(saldo.getInscricao(), valor, tipo);
                break;
            case ATUALIZACAO_JUROS:
                somaOuSubtrai(saldo.getAtualizacao(), valor, tipo);
                break;
            case ATUALIZACAO_ENCARGOS:
                somaOuSubtrai(saldo.getAtualizacao(), valor, tipo);
                break;
            case APROPRIACAO_JUROS:
                somaOuSubtrai(saldo.getApropriacao(), valor, tipo);
                break;
            case APROPRIACAO_ENCARGOS:
                somaOuSubtrai(saldo.getApropriacao(), valor, tipo);
                break;
            case CANCELAMENTO_APROPRIACAO_ENCARGOS:
                somaOuSubtrai(saldo.getCancelamento(), valor, tipo);
                break;
            case CANCELAMENTO_APROPRIACAO_JUROS:
                somaOuSubtrai(saldo.getCancelamento(), valor, tipo);
                break;
            case CANCELAMENTO_ENCARGOS:
                somaOuSubtrai(saldo.getCancelamento(), valor, tipo);
                break;
            case CANCELAMENTO_JUROS:
                somaOuSubtrai(saldo.getCancelamento(), valor, tipo);
                break;
            case CANCELAMENTO_PRINCIPAL:
                somaOuSubtrai(saldo.getCancelamento(), valor, tipo);
                break;
            case PAGAMENTO_AMORTIZACAO:
                somaOuSubtrai(saldo.getPagamento(), valor, tipo);
                break;
            case RECEITA_OPERACAO_CREDITO:
                somaOuSubtrai(saldo.getReceita(), valor, tipo);
                break;
            default:
                throw new ExcecaoNegocioGenerica("Operação de movimentação inválida: " + op.getDescricao());
        }
    }

    private BigDecimal somaOuSubtrai(BigDecimal valorOriginal, BigDecimal valor, TipoLancamento tipo) {
        if (tipo.equals(TipoOperacaoORC.NORMAL)) {
            return valorOriginal.add(valor);
        } else {
            return valorOriginal.subtract(valor);
        }
    }

    private void processaSaldosFuturos(Pagamento pag, TipoLancamento tipo, OperacaoMovimentoDividaPublica op) {
        try {
            List<SaldoDividaPublica> saldos = selectSaldosFuturos(pag.getDataPagamento(), pag.getUnidadeOrganizacional(), pag.getLiquidacao().getEmpenho().getDividaPublica());
            for (SaldoDividaPublica s : saldos) {
                atualizaValorSaldo(s, pag.getValor(), op, tipo);
                updateSaldoDividaPublica(s);
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao processar saldos futuros: " + ex.getMessage(), ex);
        }
    }

    private List<SaldoDividaPublica> selectSaldosFuturos(Date data, UnidadeOrganizacional unidade, DividaPublica divida) throws SQLException {
        List<SaldoDividaPublica> saldos = new ArrayList<>();
        preparaSelectSaldosDividaPublicaFuturos();
        psSelectSaldosDividaPublicaFuturos.clearParameters();
        psSelectSaldosDividaPublicaFuturos.setLong(1, unidade.getId());
        psSelectSaldosDividaPublicaFuturos.setLong(2, divida.getId());
        psSelectSaldosDividaPublicaFuturos.setDate(3, new java.sql.Date(data.getTime()));
        try (ResultSet rs = psSelectSaldosDividaPublicaFuturos.executeQuery()) {
            while (rs.next()) {
                SaldoDividaPublica saldo = new SaldoDividaPublica();
                saldo.setId(rs.getLong("id"));
                saldo.setData(rs.getDate("datasaldo"));
                saldo.setInscricao(rs.getBigDecimal("inscricao"));
                saldo.setAtualizacao(rs.getBigDecimal("atualizacao"));
                saldo.setApropriacao(rs.getBigDecimal("apropriacao"));
                saldo.setPagamento(rs.getBigDecimal("pagamento"));
                saldo.setReceita(rs.getBigDecimal("receita"));
                saldo.setCancelamento(rs.getBigDecimal("cancelamento"));
                saldo.setUnidadeOrganizacional(unidade);
                saldo.setDividapublica(divida);
                saldos.add(saldo);
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar saldos futuros: " + ex.getMessage(), ex);
        }
        return saldos;
    }

    private void preparaSelectSaldosDividaPublicaFuturos() throws SQLException {
        if (psSelectSaldosDividaPublicaFuturos == null) {
            String sql = "select " +
                    "  s.id as id " +
                    "  , s.data as datasaldo " +
                    "  , s.inscricao as inscricao " +
                    "  , s.atualizacao as atualizacao " +
                    "  , s.apropriacao as apropriacao " +
                    "  , s.pagamento as pagamento " +
                    "  , s.receita as receita " +
                    "  , s.cancelamento as cancelamento " +
                    "  , s.unidadeorganizacional_id as unidade " +
                    "  , s.dividapublica_id as dividapublica " +
                    "from saldodividapublica s " +
                    "where s.unidadeorganizacional_id = ? " +
                    "and s.dividapublica_id = ? " +
                    "and s.data < to_date(?, 'dd/MM/yyyy'))";
            psSelectSaldosDividaPublicaFuturos = this.conexao.prepareStatement(sql);
        }
    }

    private SaldoDividaPublica selectUltimoSaldoDividaPublica(Date data, UnidadeOrganizacional unidade, DividaPublica divida) throws SQLException {
        preparaSelectUltimoSaldoDividaPublica();
        psSelectUltimoSaldoDividaPublica.clearParameters();
        psSelectUltimoSaldoDividaPublica.setLong(1, unidade.getId());
        psSelectUltimoSaldoDividaPublica.setLong(2, divida.getId());
        psSelectUltimoSaldoDividaPublica.setDate(3, new java.sql.Date(data.getTime()));
        try (ResultSet rs = psSelectUltimoSaldoDividaPublica.executeQuery()) {
            if (rs.next()) {
                SaldoDividaPublica saldo = new SaldoDividaPublica();
                saldo.setId(rs.getLong("id"));
                saldo.setData(rs.getDate("datasaldo"));
                saldo.setInscricao(rs.getBigDecimal("inscricao"));
                saldo.setAtualizacao(rs.getBigDecimal("atualizacao"));
                saldo.setApropriacao(rs.getBigDecimal("apropriacao"));
                saldo.setPagamento(rs.getBigDecimal("pagamento"));
                saldo.setReceita(rs.getBigDecimal("receita"));
                saldo.setCancelamento(rs.getBigDecimal("cancelamento"));
                saldo.setUnidadeOrganizacional(unidade);
                saldo.setDividapublica(divida);
                return saldo;
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar último saldo da dívida pública: " + ex.getMessage(), ex);
        }
        return new SaldoDividaPublica();
    }

    private void preparaSelectUltimoSaldoDividaPublica() throws SQLException {
        if (psSelectUltimoSaldoDividaPublica == null) {
            String sql = "select " +
                    "  s.id as id " +
                    "  , s.data as datasaldo " +
                    "  , s.inscricao as inscricao " +
                    "  , s.atualizacao as atualizacao " +
                    "  , s.apropriacao as apropriacao " +
                    "  , s.pagamento as pagamento " +
                    "  , s.receita as receita " +
                    "  , s.cancelamento as cancelamento " +
                    "  , s.unidadeorganizacional_id as unidade " +
                    "  , s.dividapublica_id as dividapublica " +
                    "from saldodividapublica s " +
                    "where s.unidadeorganizacional_id = ? " +
                    "and s.dividapublica_id = ? " +
                    "and s.data = ( " +
                    "  select max(sd.data) from saldodividapublica sd " +
                    "  where sd.unidadeorganizacional_id = s.unidadeorganizacional_id " +
                    "  and sd.dividapublica_id = s.dividapublica_id " +
                    "  and sd.data <= to_date(?, 'dd/MM/yyyy'))";
            psSelectUltimoSaldoDividaPublica = this.conexao.prepareStatement(sql);
        }
    }

    private void insertSaldoDividaPublica(SaldoDividaPublica saldo) {
        try {
            preparaInsertSaldoDividaPublica();
            psInsertSaldoDividaPublica.clearParameters();
            psInsertSaldoDividaPublica.setLong(1, saldo.getId());
            psInsertSaldoDividaPublica.setDate(2, new java.sql.Date(saldo.getData().getTime()));
            psInsertSaldoDividaPublica.setBigDecimal(3, saldo.getInscricao());
            psInsertSaldoDividaPublica.setBigDecimal(4, saldo.getAtualizacao());
            psInsertSaldoDividaPublica.setBigDecimal(5, saldo.getApropriacao());
            psInsertSaldoDividaPublica.setBigDecimal(6, saldo.getPagamento());
            psInsertSaldoDividaPublica.setBigDecimal(7, saldo.getReceita());
            psInsertSaldoDividaPublica.setBigDecimal(8, saldo.getCancelamento());
            psInsertSaldoDividaPublica.setLong(9, saldo.getUnidadeOrganizacional().getId());
            psInsertSaldoDividaPublica.setLong(10, saldo.getDividapublica().getId());
            psInsertSaldoDividaPublica.executeUpdate();
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao inserir saldo da dívida pública: " + ex.getMessage(), ex);
        }
    }

    private void preparaInsertSaldoDividaPublica() throws SQLException {
        if (psInsertSaldoDividaPublica == null) {
            String sql = "insert into saldodividapublica(id, data" +
                    ", inscricao, atualizacao, apropriacao, pagamento, receita, cancelamento" +
                    ", unidadeorganizacional_id, dividapublica_id) values (?,?,?,?,?,?,?,?,?,?)";
            psInsertSaldoDividaPublica = this.conexao.prepareStatement(sql);
        }
    }

    private void updateSaldoDividaPublica(SaldoDividaPublica saldo) {
        try {
            preparaUpdateSaldoDividaPublica();
            psUpdateSaldoDividaPublica.clearParameters();
            psUpdateSaldoDividaPublica.setBigDecimal(1, saldo.getInscricao());
            psUpdateSaldoDividaPublica.setBigDecimal(2, saldo.getAtualizacao());
            psUpdateSaldoDividaPublica.setBigDecimal(3, saldo.getApropriacao());
            psUpdateSaldoDividaPublica.setBigDecimal(4, saldo.getPagamento());
            psUpdateSaldoDividaPublica.setBigDecimal(5, saldo.getReceita());
            psUpdateSaldoDividaPublica.setBigDecimal(6, saldo.getCancelamento());
            psUpdateSaldoDividaPublica.setLong(7, saldo.getId());
            psUpdateSaldoDividaPublica.executeUpdate();
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao atualizar saldo: " + ex.getMessage(), ex);
        }
    }

    private void preparaUpdateSaldoDividaPublica() throws SQLException {
        if (psUpdateSaldoDividaPublica == null) {
            String sql = "update saldodividapublica set  " +
                    " inscricao = ?, atualizacao = ?, apropriacao = ?, pagamento = ?, receita = ?" +
                    ", cancelamento ? where id = ? ";
            psUpdateSaldoDividaPublica = this.conexao.prepareStatement(sql);
        }
    }

    private void insertMovimentoDiarioDividaPublica(SaldoDividaPublica saldo, OperacaoDiarioDividaPublica op, TipoLancamento tipo, BigDecimal valor) {
        try {
            preparaInsertMovimentoDiarioDividaPublica();
            psInsertMovimentoDiarioDividaPublica.clearParameters();
            psInsertMovimentoDiarioDividaPublica.setLong(1, idGenerator.createID());
            psInsertMovimentoDiarioDividaPublica.setDate(2, new java.sql.Date(saldo.getData().getTime()));
            psInsertMovimentoDiarioDividaPublica.setString(3, tipo.name());
            psInsertMovimentoDiarioDividaPublica.setBigDecimal(4, valor);
            psInsertMovimentoDiarioDividaPublica.setLong(5, saldo.getUnidadeOrganizacional().getId());
            psInsertMovimentoDiarioDividaPublica.setLong(6, saldo.getDividapublica().getId());
            psInsertMovimentoDiarioDividaPublica.executeUpdate();
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao inserir movimento diário: " + ex.getMessage(), ex);
        }
    }

    private void preparaInsertMovimentoDiarioDividaPublica() throws SQLException {
        if (psInsertMovimentoDiarioDividaPublica == null) {
            String sql = "insert into movimentodiariodividapub(id, data, tipolancamento " +
                    ",  valor, unidadeorganizacional_id " +
                    ", dividapublica_id) values (?,?,?,?,?,?)";
            psInsertMovimentoDiarioDividaPublica = this.conexao.prepareStatement(sql);
        }
    }
}
