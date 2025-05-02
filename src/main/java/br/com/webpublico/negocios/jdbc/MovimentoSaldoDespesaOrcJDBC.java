package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.enums.TipoOperacaoORC;
import br.com.webpublico.enums.TipoRestosProcessado;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fernando on 29/01/15.
 */
public final class MovimentoSaldoDespesaOrcJDBC extends ClassPatternJDBC {

    private static MovimentoSaldoDespesaOrcJDBC instancia;
    private PreparedStatement psInsertMovimentoDespesaOrc;
    private PreparedStatement psSelectSaldoFonteDespesaOrc;
    private PreparedStatement psSelectSaldosFonteDespesaOrcFuturo;
    private PreparedStatement psInsertSaldoFonteDespesaOrc;
    private PreparedStatement psUpdateSaldoFonteDespesaOrc;
    private IdGenerator idGenerator = IdGenerator.createInstance(conexao);

    private MovimentoSaldoDespesaOrcJDBC(Connection conn) {
        this.conexao = conn;
    }

    public static synchronized MovimentoSaldoDespesaOrcJDBC createInstance(Connection conn) {
        if (instancia == null) {
            instancia = new MovimentoSaldoDespesaOrcJDBC(conn);
        }
        showConnection(instancia.getClass());
        return instancia;
    }

    public void createMovimentoDespesaOrc(Pagamento pag) {
        if (allFieldsNotNull()) {
            if (pag.getCategoriaOrcamentaria().equals(CategoriaOrcamentaria.RESTO)) {
                if (pag.getLiquidacao().getEmpenho().getTipoRestosProcessados().equals(TipoRestosProcessado.PROCESSADOS)) {
                    MovimentoDespesaORC mdorc = insereMovimentoDespesaORC(pag.getLiquidacao().getEmpenho().getFonteDespesaORC(), OperacaoORC.PAGAMENTORESTO_PROCESSADO, TipoOperacaoORC.NORMAL, pag.getValor(), pag.getDataPagamento(), pag.getUnidadeOrganizacional());
                    pag.setMovimentoDespesaORC(mdorc);
                } else {
                    MovimentoDespesaORC mdorc = insereMovimentoDespesaORC(pag.getLiquidacao().getEmpenho().getFonteDespesaORC(), OperacaoORC.PAGAMENTORESTO_NAO_PROCESSADO, TipoOperacaoORC.NORMAL, pag.getValor(), pag.getDataPagamento(), pag.getUnidadeOrganizacional());
                    pag.setMovimentoDespesaORC(mdorc);
                }
            } else {
                MovimentoDespesaORC mdorc = insereMovimentoDespesaORC(pag.getLiquidacao().getEmpenho().getFonteDespesaORC(), OperacaoORC.PAGAMENTO, TipoOperacaoORC.NORMAL, pag.getValor(), pag.getDataPagamento(), pag.getUnidadeOrganizacional());
                pag.setMovimentoDespesaORC(mdorc);
            }
        }
        System.out.println("Movimento criado?");
    }

    private MovimentoDespesaORC insereMovimentoDespesaORC(FonteDespesaORC fonte, OperacaoORC op, TipoOperacaoORC top, BigDecimal valor, Date data, UnidadeOrganizacional unidadeOrganizacional) {
        if (allFieldsNotNull(fonte, op, top, valor, data, unidadeOrganizacional)) {
            MovimentoDespesaORC movi = new MovimentoDespesaORC(data, valor, op, top, fonte, unidadeOrganizacional);
            try {
                movi.setId(idGenerator.createID());
                preparaInsertMovimentoDespesaOrc();
                psInsertMovimentoDespesaOrc.clearParameters();
                psInsertMovimentoDespesaOrc.setLong(1, movi.getId());
                psInsertMovimentoDespesaOrc.setDate(2, new java.sql.Date(movi.getDataMovimento().getTime()));
                psInsertMovimentoDespesaOrc.setBigDecimal(3, movi.getValor());
                psInsertMovimentoDespesaOrc.setString(4, movi.getTipoOperacaoORC().name());
                psInsertMovimentoDespesaOrc.setString(5, movi.getOperacaoORC().name());
                psInsertMovimentoDespesaOrc.setLong(6, fonte.getId());
                psInsertMovimentoDespesaOrc.setLong(7, unidadeOrganizacional.getId());
                psInsertMovimentoDespesaOrc.executeUpdate();
                createSaldoFonteDespesaOrc(movi);
                return movi;
            } catch (SQLException ex) {
                throw new ExcecaoNegocioGenerica("Erro ao inserir movimento despesa orc: " + ex.getMessage(), ex);
            }
        }
        return null;
    }

    private void preparaInsertMovimentoDespesaOrc() throws SQLException {
        if (psInsertMovimentoDespesaOrc == null) {
            String sql = "insert into movimentodespesaorc(ID, DATAMOVIMENTO, VALOR "
                    + " , TIPOOPERACAOORC, OPERACAOORC, FONTEDESPESAORC_ID, UNIDADEORGANIZACIONAL_ID) "
                    + "VALUES (?,?,?,?,?,?,?)";
            psInsertMovimentoDespesaOrc = getConexao().prepareStatement(sql);
        }
    }

    private void createSaldoFonteDespesaOrc(MovimentoDespesaORC mov) {
        try {
            SaldoFonteDespesaORC saldo = recuperaUltimoSaldo(mov.getDataMovimento(), mov.getUnidadeOrganizacional(), mov.getFonteDespesaORC());
            if (saldo != null) {
                if (DataUtil.compararDatas(saldo.getDataSaldo(), mov.getDataMovimento())) {
                    if (validaValorDaOperacao(saldo, mov)) {
                        atualizaValorSaldo(saldo, mov.getValor(), mov.getOperacaoORC(), mov.getTipoOperacaoORC());
                        salvaSaldoFonteDespesaOrc(saldo);
                        processaSaldosFuturos(saldo, mov);
                    }
                } else {
                    SaldoFonteDespesaORC novoSaldo = (SaldoFonteDespesaORC) Util.clonarObjeto(saldo);
                    novoSaldo.setId(null);
                    novoSaldo.setDataSaldo(mov.getDataMovimento());
                    if (validaValorDaOperacao(novoSaldo, mov)) {
                        atualizaValorSaldo(novoSaldo, mov.getValor(), mov.getOperacaoORC(), mov.getTipoOperacaoORC());
                        salvaSaldoFonteDespesaOrc(novoSaldo);
                        processaSaldosFuturos(novoSaldo, mov);
                    }
                }
            } else {
                SaldoFonteDespesaORC s = new SaldoFonteDespesaORC(mov.getDataMovimento(), mov.getFonteDespesaORC(), mov.getUnidadeOrganizacional());
                if (validaValorDaOperacao(s, mov)) {
                    atualizaValorSaldo(s, mov.getValor(), mov.getOperacaoORC(), mov.getTipoOperacaoORC());
                    salvaSaldoFonteDespesaOrc(s);
                    processaSaldosFuturos(s, mov);
                }
            }
//            processaSaldosFuturos(saldo, mov);
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao gerar saldo: " + ex.getMessage(), ex);
        }
    }

    private SaldoFonteDespesaORC recuperaUltimoSaldo(Date data, UnidadeOrganizacional unidadeOrganizacional, FonteDespesaORC fonteDespesaORC) throws SQLException {
        preparaSelectSaldoFonteDespesaOrc();
        psSelectSaldoFonteDespesaOrc.clearParameters();
        psSelectSaldoFonteDespesaOrc.setLong(1, fonteDespesaORC.getId());
        psSelectSaldoFonteDespesaOrc.setDate(2, new java.sql.Date(data.getTime()));
        psSelectSaldoFonteDespesaOrc.setLong(3, unidadeOrganizacional.getId());
        try (ResultSet rs = psSelectSaldoFonteDespesaOrc.executeQuery()) {
            if (rs.next()) {
                SaldoFonteDespesaORC saldo = new SaldoFonteDespesaORC();
                saldo.setId(rs.getLong("id"));
                saldo.setDotacao(rs.getBigDecimal("dotacao"));
                saldo.setEmpenhado(rs.getBigDecimal("empenhado"));
                saldo.setLiquidado(rs.getBigDecimal("liquidado"));
                saldo.setPago(rs.getBigDecimal("pago"));
                saldo.setDataSaldo(rs.getDate("datasaldo"));
                saldo.setAlteracao(rs.getBigDecimal("alteracao"));
                saldo.setReservado(rs.getBigDecimal("reservado"));
                saldo.setSuplementado(rs.getBigDecimal("suplementado"));
                saldo.setReduzido(rs.getBigDecimal("reduzido"));
                saldo.setEmpenhadoProcessado(rs.getBigDecimal("empenhadoprocessado"));
                saldo.setLiquidadoProcessado(rs.getBigDecimal("liquidadoprocessado"));
                saldo.setPagoProcessado(rs.getBigDecimal("pagoprocessado"));
                saldo.setEmpenhadoNaoProcessado(rs.getBigDecimal("empenhadonaoprocessado"));
                saldo.setLiquidadoNaoProcessado(rs.getBigDecimal("liquidadonaoprocessado"));
                saldo.setPagoNaoProcessado(rs.getBigDecimal("pagonaoprocessado"));
                saldo.setReservadoPorLicitacao(rs.getBigDecimal("reservadoporlicitacao"));
                saldo.setUnidadeOrganizacional(unidadeOrganizacional);
                saldo.setFonteDespesaORC(fonteDespesaORC);
                return saldo;
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar último saldo: " + ex.getMessage(), ex);
        }
        return null;
    }

    private void preparaSelectSaldoFonteDespesaOrc() throws SQLException {
        if (psSelectSaldoFonteDespesaOrc == null) {
            String sql = " select "
                    + "    saldo.id as id "
                    + "    , saldo.dotacao as dotacao "
                    + "    , saldo.empenhado as empenhado "
                    + "    , saldo.liquidado as liquidado "
                    + "    , saldo.pago as pago "
                    + "    , saldo.datasaldo as datasaldo "
                    + "    , saldo.alteracao as alteracao "
                    + "    , saldo.reservado as reservado "
                    + "    , saldo.suplementado as suplementado "
                    + "    , saldo.reduzido as reduzido "
                    + "    , saldo.empenhadoprocessado as empenhadoprocessado "
                    + "    , saldo.liquidadoprocessado as liquidadoprocessado "
                    + "    , saldo.pagoprocessado as pagoprocessado "
                    + "    , saldo.empenhadonaoprocessado as empenhadonaoprocessado "
                    + "    , saldo.liquidadonaoprocessado as liquidadonaoprocessado "
                    + "    , saldo.pagonaoprocessado as pagonaoprocessado "
                    + "    , saldo.reservadoporlicitacao as reservadoporlicitacao "
                    + "from saldofontedespesaorc saldo "
                    + "where saldo.fontedespesaorc_id = ? "
                    + "and saldo.datasaldo = (select max(sal.datasaldo) "
                    + "                     from saldofontedespesaorc sal "
                    + "                     where sal.fontedespesaorc_id = saldo.fontedespesaorc_id "
                    + "                     and sal.unidadeorganizacional_id = saldo.unidadeorganizacional_id "
                    + "                     and sal.datasaldo <= to_date(?, 'dd/MM/yyyy')) "
                    + "and saldo.unidadeorganizacional_id = ? ";
            psSelectSaldoFonteDespesaOrc = getConexao().prepareStatement(sql);
        }
    }

    private void salvaSaldoFonteDespesaOrc(SaldoFonteDespesaORC saldo) {
        if (saldo.getId() != null) {
            updateSaldoFonteDespesaorc(saldo);
        } else {
            saldo.setId(idGenerator.createID());
            insertSaldoFonteDespesaOrc(saldo);
        }
    }

    private void updateSaldoFonteDespesaorc(SaldoFonteDespesaORC saldo) {
        try {
            preparaUpdateSaldoFonteDespesaOrc();
            psUpdateSaldoFonteDespesaOrc.clearParameters();
            psUpdateSaldoFonteDespesaOrc.setBigDecimal(1, saldo.getDotacao());
            psUpdateSaldoFonteDespesaOrc.setBigDecimal(2, saldo.getEmpenhado());
            psUpdateSaldoFonteDespesaOrc.setBigDecimal(3, saldo.getLiquidado());
            psUpdateSaldoFonteDespesaOrc.setBigDecimal(4, saldo.getPago());
            psUpdateSaldoFonteDespesaOrc.setBigDecimal(5, saldo.getAlteracao());
            psUpdateSaldoFonteDespesaOrc.setBigDecimal(6, saldo.getSuplementado());
            psUpdateSaldoFonteDespesaOrc.setBigDecimal(7, saldo.getReduzido());
            psUpdateSaldoFonteDespesaOrc.setBigDecimal(8, saldo.getReservado());
            psUpdateSaldoFonteDespesaOrc.setBigDecimal(9, saldo.getEmpenhadoProcessado());
            psUpdateSaldoFonteDespesaOrc.setBigDecimal(10, saldo.getEmpenhadoNaoProcessado());
            psUpdateSaldoFonteDespesaOrc.setBigDecimal(11, saldo.getLiquidadoProcessado());
            psUpdateSaldoFonteDespesaOrc.setBigDecimal(12, saldo.getLiquidadoNaoProcessado());
            psUpdateSaldoFonteDespesaOrc.setBigDecimal(13, saldo.getPagoProcessado());
            psUpdateSaldoFonteDespesaOrc.setBigDecimal(14, saldo.getPagoNaoProcessado());
            psUpdateSaldoFonteDespesaOrc.setBigDecimal(15, saldo.getReservadoPorLicitacao());
            psUpdateSaldoFonteDespesaOrc.setDate(16, new java.sql.Date(saldo.getDataSaldo().getTime()));
            psUpdateSaldoFonteDespesaOrc.setLong(17, saldo.getFonteDespesaORC().getId());
            psUpdateSaldoFonteDespesaOrc.setLong(18, saldo.getUnidadeOrganizacional().getId());
            psUpdateSaldoFonteDespesaOrc.executeUpdate();
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao atualizar saldo: " + ex.getMessage(), ex);
        }
    }

    private void preparaUpdateSaldoFonteDespesaOrc() throws SQLException {
        if (psUpdateSaldoFonteDespesaOrc == null) {
            String sql = "update saldofontedespesaorc set dotacao = ?, empenhado = ?, liquidado = ?, pago = ? "
                    + ", alteracao = ?, suplementado = ?, reduzido = ?, reservado = ?, empenhadoprocessado = ? "
                    + ", empenhadonaoprocessado = ?, liquidadoprocessado = ?, liquidadonaoprocessado = ? "
                    + ", pagoprocessado = ?, pagonaoprocessado = ?, reservadoporlicitacao = ?, datasaldo = ? "
                    + ", fontedespesaorc_id = ?, unidadeorganizacional_id = ? where id = ? ";
            psUpdateSaldoFonteDespesaOrc = getConexao().prepareStatement(sql);
        }
    }

    private void atualizaValorSaldo(SaldoFonteDespesaORC saldo, BigDecimal valor, OperacaoORC operacao, TipoOperacaoORC tipo) {
        switch (operacao) {
            case EMPENHO:
                saldo.setEmpenhado(somaOuSubtrai(saldo.getEmpenhado(), valor, tipo));
                break;
            case LIQUIDACAO:
                saldo.setLiquidado(somaOuSubtrai(saldo.getLiquidado(), valor, tipo));
                break;
            case PAGAMENTO:
                saldo.setPago(somaOuSubtrai(saldo.getPago(), valor, tipo));
                break;
            case DOTACAO:
                saldo.setDotacao(somaOuSubtrai(saldo.getDotacao(), valor, tipo));
                break;
            case ANULACAO:
                saldo.setAlteracao(somaOuSubtrai(saldo.getAlteracao(), valor, tipo));
                break;
            case SUPLEMENTACAO:
                saldo.setSuplementado(somaOuSubtrai(saldo.getSuplementado(), valor, tipo));
                break;
            case RESERVADO:
                saldo.setReservado(somaOuSubtrai(saldo.getReservado(), valor, tipo));
                break;
            case RESERVADO_POR_LICITACAO:
                saldo.setReservadoPorLicitacao(somaOuSubtrai(saldo.getReservadoPorLicitacao(), valor, tipo));
                break;
            case EMPENHORESTO_PROCESSADO:
                saldo.setEmpenhadoProcessado(somaOuSubtrai(saldo.getEmpenhadoProcessado(), valor, tipo));
                break;
            case EMPENHORESTO_NAO_PROCESSADO:
                saldo.setEmpenhadoNaoProcessado(somaOuSubtrai(saldo.getEmpenhadoNaoProcessado(), valor, tipo));
                break;
            case LIQUIDACAORESTO_PROCESSADO:
                saldo.setLiquidadoProcessado(somaOuSubtrai(saldo.getLiquidadoProcessado(), valor, tipo));
                break;
            case LIQUIDACAORESTO_NAO_PROCESSADO:
                saldo.setLiquidadoNaoProcessado(somaOuSubtrai(saldo.getLiquidadoNaoProcessado(), valor, tipo));
                break;
            case PAGAMENTORESTO_PROCESSADO:
                saldo.setPagoProcessado(somaOuSubtrai(saldo.getPagoProcessado(), valor, tipo));
                break;
            case PAGAMENTORESTO_NAO_PROCESSADO:
                saldo.setPagoNaoProcessado(somaOuSubtrai(saldo.getPagoNaoProcessado(), valor, tipo));
                break;
            default:
                System.out.println("Não veio nada!");
                break;
        }
    }

    private BigDecimal calculaSaldoDoDia(SaldoFonteDespesaORC saldo, BigDecimal valor, OperacaoORC operacao, TipoOperacaoORC tipo) {
        switch (operacao) {
            case EMPENHO:
                return (somaOuSubtrai(saldo.getEmpenhado(), valor, tipo));
            case LIQUIDACAO:
                return (somaOuSubtrai(saldo.getLiquidado(), valor, tipo));
            case PAGAMENTO:
                return (somaOuSubtrai(saldo.getPago(), valor, tipo));
            case DOTACAO:
                return (somaOuSubtrai(saldo.getDotacao(), valor, tipo));
            case ANULACAO:
                return (somaOuSubtrai(saldo.getAlteracao(), valor, tipo));
            case SUPLEMENTACAO:
                return (somaOuSubtrai(saldo.getSuplementado(), valor, tipo));
            case RESERVADO:
                return (somaOuSubtrai(saldo.getReservado(), valor, tipo));
            case RESERVADO_POR_LICITACAO:
                return (somaOuSubtrai(saldo.getReservadoPorLicitacao(), valor, tipo));
            case EMPENHORESTO_PROCESSADO:
                return (somaOuSubtrai(saldo.getEmpenhadoProcessado(), valor, tipo));
            case EMPENHORESTO_NAO_PROCESSADO:
                return (somaOuSubtrai(saldo.getEmpenhadoNaoProcessado(), valor, tipo));
            case LIQUIDACAORESTO_PROCESSADO:
                return (somaOuSubtrai(saldo.getLiquidadoProcessado(), valor, tipo));
            case LIQUIDACAORESTO_NAO_PROCESSADO:
                return (somaOuSubtrai(saldo.getLiquidadoNaoProcessado(), valor, tipo));
            case PAGAMENTORESTO_PROCESSADO:
                return (somaOuSubtrai(saldo.getPagoProcessado(), valor, tipo));
            case PAGAMENTORESTO_NAO_PROCESSADO:
                return (somaOuSubtrai(saldo.getPagoNaoProcessado(), valor, tipo));
            default:
                System.out.println("Não veio nada!");
                return BigDecimal.ZERO;
        }
    }


    private void processaSaldosFuturos(SaldoFonteDespesaORC saldo, MovimentoDespesaORC movi) {
        try {
            if(saldo == null) {
                System.out.println("Saldo nulo!");
            }
            if(movi == null) {
                System.out.println("Movimento nulo!");
            }
            List<SaldoFonteDespesaORC> saldos = recuperaSaldosFuturos(saldo.getDataSaldo(), saldo.getUnidadeOrganizacional(), saldo.getFonteDespesaORC());
            if (!saldos.isEmpty()) {
                if (validaValorDaOperacao(saldo, movi)) {
                    atualizaValorSaldo(saldo, movi.getValor(), movi.getOperacaoORC(), movi.getTipoOperacaoORC());
                    salvaSaldoFonteDespesaOrc(saldo);
                }
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao processar saldos futuros: " + ex.getMessage(), ex);
        }
    }

    private List<SaldoFonteDespesaORC> recuperaSaldosFuturos(Date data, UnidadeOrganizacional unidade, FonteDespesaORC fonte) throws SQLException {
        List<SaldoFonteDespesaORC> saldos = new ArrayList<>();
        preparaSelectSaldosFonteDespesaOrcFuturos();
        psSelectSaldosFonteDespesaOrcFuturo.clearParameters();
        psSelectSaldosFonteDespesaOrcFuturo.setLong(1, fonte.getId());
        psSelectSaldosFonteDespesaOrcFuturo.setDate(2, new java.sql.Date(data.getTime()));
        psSelectSaldosFonteDespesaOrcFuturo.setLong(3, unidade.getId());
        try (ResultSet rs = psSelectSaldosFonteDespesaOrcFuturo.executeQuery()) {
            while (rs.next()) {
                SaldoFonteDespesaORC saldo = new SaldoFonteDespesaORC();
                saldo.setId(rs.getLong("id"));
                saldo.setDotacao(rs.getBigDecimal("dotacao"));
                saldo.setEmpenhado(rs.getBigDecimal("empenhado"));
                saldo.setLiquidado(rs.getBigDecimal("liquidado"));
                saldo.setPago(rs.getBigDecimal("pago"));
                saldo.setDataSaldo(rs.getDate("datasaldo"));
                saldo.setAlteracao(rs.getBigDecimal("alteracao"));
                saldo.setReservado(rs.getBigDecimal("reservado"));
                saldo.setSuplementado(rs.getBigDecimal("suplementado"));
                saldo.setReduzido(rs.getBigDecimal("reduzido"));
                saldo.setEmpenhadoProcessado(rs.getBigDecimal("empenhadoprocessado"));
                saldo.setLiquidadoProcessado(rs.getBigDecimal("liquidadoprocessado"));
                saldo.setPagoProcessado(rs.getBigDecimal("pagoprocessado"));
                saldo.setEmpenhadoNaoProcessado(rs.getBigDecimal("empenhadonaoprocessado"));
                saldo.setLiquidadoNaoProcessado(rs.getBigDecimal("liquidadonaoprocessado"));
                saldo.setPagoNaoProcessado(rs.getBigDecimal("pagonaoprocessado"));
                saldo.setReservadoPorLicitacao(rs.getBigDecimal("reservadoporlicitacao"));
                saldos.add(saldo);
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar saldos futuros: " + ex.getMessage(), ex);
        }
        return saldos;
    }

    private void preparaSelectSaldosFonteDespesaOrcFuturos() throws SQLException {
        if (psSelectSaldosFonteDespesaOrcFuturo == null) {
            String sql = " select "
                    + "    saldo.id as id "
                    + "    , saldo.dotacao as dotacao "
                    + "    , saldo.empenhado as empenhado "
                    + "    , saldo.liquidado as liquidado "
                    + "    , saldo.pago as pago "
                    + "    , saldo.datasaldo as datasaldo "
                    + "    , saldo.alteracao as alteracao "
                    + "    , saldo.reservado as reservado "
                    + "    , saldo.suplementado as suplementado "
                    + "    , saldo.reduzido as reduzido "
                    + "    , saldo.empenhadoprocessado as empenhadoprocessado "
                    + "    , saldo.liquidadoprocessado as liquidadoprocessado "
                    + "    , saldo.pagoprocessado as pagoprocessado "
                    + "    , saldo.empenhadonaoprocessado as empenhadonaoprocessado "
                    + "    , saldo.liquidadonaoprocessado as liquidadonaoprocessado "
                    + "    , saldo.pagonaoprocessado as pagonaoprocessado "
                    + "    , saldo.reservadoporlicitacao as reservadoporlicitacao "
                    + "from saldofontedespesaorc saldo "
                    + "where saldo.fontedespesaorc_id = ? "
                    + "and saldo.datasaldo > ? "
                    + "and saldo.unidadeorganizacional_id = ? "
                    + "order by saldo.datasaldo ";
            psSelectSaldosFonteDespesaOrcFuturo = getConexao().prepareStatement(sql);
        }
    }

    private void insertSaldoFonteDespesaOrc(SaldoFonteDespesaORC saldo) {
        try {
            preparaInsertSaldoFonteDespesaOrc();
            psInsertSaldoFonteDespesaOrc.clearParameters();
            psInsertSaldoFonteDespesaOrc.setLong(1, saldo.getId());
            psInsertSaldoFonteDespesaOrc.setBigDecimal(2, saldo.getDotacao());
            psInsertSaldoFonteDespesaOrc.setBigDecimal(3, saldo.getEmpenhado());
            psInsertSaldoFonteDespesaOrc.setBigDecimal(4, saldo.getLiquidado());
            psInsertSaldoFonteDespesaOrc.setBigDecimal(5, saldo.getPago());
            psInsertSaldoFonteDespesaOrc.setBigDecimal(6, saldo.getAlteracao());
            psInsertSaldoFonteDespesaOrc.setBigDecimal(7, saldo.getSuplementado());
            psInsertSaldoFonteDespesaOrc.setBigDecimal(8, saldo.getReduzido());
            psInsertSaldoFonteDespesaOrc.setBigDecimal(9, saldo.getReservado());
            psInsertSaldoFonteDespesaOrc.setBigDecimal(10, saldo.getEmpenhadoProcessado());
            psInsertSaldoFonteDespesaOrc.setBigDecimal(11, saldo.getEmpenhadoNaoProcessado());
            psInsertSaldoFonteDespesaOrc.setBigDecimal(12, saldo.getLiquidadoProcessado());
            psInsertSaldoFonteDespesaOrc.setBigDecimal(13, saldo.getLiquidadoNaoProcessado());
            psInsertSaldoFonteDespesaOrc.setBigDecimal(14, saldo.getPagoProcessado());
            psInsertSaldoFonteDespesaOrc.setBigDecimal(15, saldo.getPagoNaoProcessado());
            psInsertSaldoFonteDespesaOrc.setBigDecimal(16, saldo.getReservadoPorLicitacao());
            psInsertSaldoFonteDespesaOrc.setDate(17, new java.sql.Date(saldo.getDataSaldo().getTime()));
            psInsertSaldoFonteDespesaOrc.setLong(18, saldo.getFonteDespesaORC().getId());
            psInsertSaldoFonteDespesaOrc.setLong(19, saldo.getUnidadeOrganizacional().getId());
            psInsertSaldoFonteDespesaOrc.executeUpdate();
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao inserir saldo: " + ex.getMessage(), ex);
        }
    }

    private void preparaInsertSaldoFonteDespesaOrc() throws SQLException {
        if (psInsertSaldoFonteDespesaOrc == null) {
            String sql = "insert into saldofontedespesaorc(id, dotacao, empenhado, liquidado, pago "
                    + ", alteracao, suplementado, reduzido, reservado, empenhadoprocessado"
                    + ", empenhadonaoprocessado, liquidadoprocessado, liquidadonaoprocessado"
                    + ", pagoprocessado, pagonaoprocessado, reservadoporlicitacao, datasaldo"
                    + ", fontedespesaorc_id, unidadeorganizacional_id) "
                    + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            psInsertSaldoFonteDespesaOrc = getConexao().prepareStatement(sql);
        }
    }

    private BigDecimal somaOuSubtrai(BigDecimal valorOriginal, BigDecimal valor, TipoOperacaoORC tipo) {
        if (tipo.equals(TipoOperacaoORC.NORMAL)) {
            return valorOriginal.add(valor);
        } else {
            return valorOriginal.subtract(valor);
        }
    }

    private boolean validaValorDaOperacao(SaldoFonteDespesaORC saldo, MovimentoDespesaORC mov) {
        if (!mov.getOperacaoORC().equals(OperacaoORC.SUPLEMENTACAO) && !mov.getOperacaoORC().equals(OperacaoORC.ANULACAO)) {
            BigDecimal valor = calculaSaldoDoDia(saldo, mov.getValor(), mov.getOperacaoORC(), mov.getTipoOperacaoORC());
            if (valor.compareTo(BigDecimal.ZERO) < 0) {
                throw new ExcecaoNegocioGenerica("O Valor " + Util.formataValor(valor) + " do Saldo da Fonte de Despesa Orçamentária para Operação: " + mov.getOperacaoORC().toString() + "(" + mov.getTipoOperacaoORC().getDescricao() + ") ficou negativo na data " + new SimpleDateFormat("dd/MM/yyyy").format(saldo.getDataSaldo()) + ". Valor: " + new DecimalFormat("#,###,##0.00").format(valor) + " - " + mov.getFonteDespesaORC().getDescricaoContaDespesa());
            }
        }
        return true;
    }

    public Connection getConexao() {
        return conexao;
    }
}
