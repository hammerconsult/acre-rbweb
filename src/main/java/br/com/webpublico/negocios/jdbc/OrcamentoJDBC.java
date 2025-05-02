package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoLancamentoContabil;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by venom on 23/02/15.
 */
public final class OrcamentoJDBC extends ClassPatternJDBC {

    private static OrcamentoJDBC instancia;
    private IdGenerator idGenerator = IdGenerator.createInstance(conexao);
    private CommonsJDBC commonsJDBC = CommonsJDBC.createInstance(conexao);
    private String listaIds;
    private PreparedStatement psSelectContaById;
    private PreparedStatement psSelectContaByListaId;
    private PreparedStatement psSelectTipoContaAuxiliar;
    private PreparedStatement psSelectContaAuxiliar;
    private PreparedStatement psSelectContaDespesa;
    private PreparedStatement psSelectPlanoDeContasAuxiliar;
    private PreparedStatement psInsertConta;
    private PreparedStatement psInsertContaAuxiliar;
    private PreparedStatement psInsertPlanoDeContas;
    private PreparedStatement psInsertLancamentoContabil;
    private PreparedStatement psUpdatePlanoDeContasExercicio;
    private PreparedStatement psSelectSaldoContaContabil;
    private PreparedStatement psInsertSaldoContaContabil;
    private PreparedStatement psUpdateSaldoContaContabil;
    private Map<Long, Conta> contas = new HashMap<>();
    private Map<Long, PlanoDeContas> planosDeContas = new HashMap<>();
    private Map<Integer, PlanoDeContas> planosAuxiliares = new HashMap<>();
    private Map<Long, TipoContaAuxiliar> tiposContaAuxiliar = new HashMap<>();
    private Map<MapContaAuxiliar, ContaAuxiliar> contasAuxiliares = new HashMap<>();
    private Map<MapSaldoContaContabil, List<SaldoContaContabil>> saldos = new HashMap<>();

    private OrcamentoJDBC(Connection conn) {
        this.conexao = conn;
    }

    public static synchronized OrcamentoJDBC createInstance(Connection conn) {
        if (instancia == null) {
            instancia = new OrcamentoJDBC(conn);
        }
        showConnection(instancia.getClass());
        return instancia;
    }

    public void preparaListaContas(Long... ids) {
        listaIds = "";
        for (Long id : ids) {
            if (id != null) {
                listaIds += "'" + id + "',";
            }
        }
        listaIds = listaIds.substring(0, listaIds.length() - 1);
        try {
            loadContasByListaIds(listaIds);
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar contas: " + ex.getMessage(), ex);
        }
    }

    public void loadContasByListaIds(String listaIds) throws SQLException {
        preparaSelectContaByListaId();
        psSelectContaByListaId.clearParameters();
        psSelectContaByListaId.setString(1, listaIds);
        try (ResultSet rs = psSelectContaByListaId.executeQuery()) {
            while (rs.next()) {
                Conta con = new Conta();
                con.setId(rs.getLong("id"));
                con.setCodigo(rs.getString("codigo"));
                con.setPlanoDeContas(getPlanoDeContasById(rs.getLong("planodecontasid")));
                con.setSuperior(getContaById(rs.getLong("superior")));
                if (!contas.containsKey(con.getId())) {
                    contas.put(con.getId(), con);
                }
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar contas: " + ex.getMessage(), ex);
        }
    }

    public Conta getContaById(Long id) {
        if (id == null) {
            return null;
        }
        if (!contas.containsKey(id)) {
            try {
                loadContaById(id);
            } catch (SQLException ex) {
                throw new ExcecaoNegocioGenerica("Erro ao buscar conta por id: " + ex.getMessage(), ex);
            }
        }
        return contas.get(id);
    }

    private void loadContaById(Long id) throws SQLException {
        preparaSelectContaById();
        psSelectContaById.clearParameters();
        psSelectContaById.setLong(1, id);
        try (ResultSet rs = psSelectContaById.executeQuery()) {
            if (rs.next()) {
                Conta con = new Conta();
                con.setId(rs.getLong("id"));
                con.setCodigo(rs.getString("codigo"));
                con.setPlanoDeContas(getPlanoDeContasById(rs.getLong("planodecontasid")));
                con.setSuperior(getContaById(rs.getLong("superior")));
                if (!contas.containsKey(con.getId())) {
                    contas.put(con.getId(), con);
                }
            } else {
                throw new ExcecaoNegocioGenerica("Conta não encontrada? " + id);
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar conta: " + ex.getMessage(), ex);
        }
    }

    public Conta getContaDespesaById(Long id) throws SQLException {
        if (!contas.containsKey(id)) {
            preparaSelectContaDespesa();
            psSelectContaDespesa.clearParameters();
            psSelectContaDespesa.setLong(1, id);
            try (ResultSet rs = psSelectContaDespesa.executeQuery()) {
                if (rs.next()) {
                    ContaDespesa cd = new ContaDespesa();
                    cd.setId(rs.getLong("id"));
                    cd.setCodigo(rs.getString("conta"));
                    cd.setSuperior((Conta) getContaDespesaById(rs.getLong("superior")));
                    contas.put(id, cd);
                }
            } catch (SQLException ex) {
                throw new ExcecaoNegocioGenerica("Erro ao recuperar Conta de Despesa do objeto parâmetro: " + ex.getMessage(), ex);
            }
        }
        return contas.get(id);
    }

    private void preparaSelectContaDespesa() throws SQLException {
        if (psSelectContaDespesa == null) {
            psSelectContaDespesa = this.conexao.prepareStatement(Queries.selectContaDespesa());
        }
    }

    public PlanoDeContas getPlanoDeContasById(Long id) {
        if (!planosDeContas.containsKey(id)) {
            PlanoDeContas pdc = new PlanoDeContas();
            pdc.setId(id);
            planosDeContas.put(id, pdc);
        }
        return planosDeContas.get(id);
    }

    private void preparaSelectContaById() throws SQLException {
        if (psSelectContaById == null) {
            psSelectContaById = this.conexao.prepareStatement(Queries.selectContaById());
        }
    }

    private void preparaSelectContaByListaId() throws SQLException {
        if (psSelectContaByListaId == null) {
            psSelectContaByListaId = this.conexao.prepareStatement(Queries.selectContaByListId());
        }
    }

    public TipoContaAuxiliar getTipoContaAuxiliar(Long id) {
        if (id != null) {
            if (tiposContaAuxiliar.isEmpty()) {
                try {
                    loadTiposContaAuxiliar();
                } catch (SQLException ex) {
                    throw new ExcecaoNegocioGenerica("Erro ao buscar tipo de conta auxiliar: " + ex.getMessage(), ex);
                }
            }
            return tiposContaAuxiliar.get(id);
        } else {
            return null;
        }
    }

    private void loadTiposContaAuxiliar() throws SQLException {
        preparaSelectTipoContaAuxiliar();
        try (ResultSet rs = psSelectTipoContaAuxiliar.executeQuery()) {
            while (rs.next()) {
                TipoContaAuxiliar tca = new TipoContaAuxiliar();
                tca.setId(rs.getLong("id"));
                tca.setCodigo(rs.getString("codigo"));
                tca.setDescricao(rs.getString("descricao"));
                tca.setChave(rs.getString("chave"));
                tca.setMascara(rs.getString("mascara"));
                tca.setItens(rs.getString("itens"));
                if (!tiposContaAuxiliar.containsKey(tca.getId())) {
                    tiposContaAuxiliar.put(tca.getId(), tca);
                }
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao carregar tipos de conta auxiliar: " + ex.getMessage(), ex);
        }
    }

    private void preparaSelectTipoContaAuxiliar() throws SQLException {
        if (psSelectTipoContaAuxiliar == null) {
            psSelectTipoContaAuxiliar = this.conexao.prepareStatement(Queries.selectTipoContaAuxiliar());
        }
    }

    public ContaAuxiliar getContaAuxiliarByMap(MapContaAuxiliar map) {
        return contasAuxiliares.get(map);
    }

    public void loadContasAuxiliaresByCodigoTipoContaAuxiliar(MapContaAuxiliar map, Conta superior, String descricao, Exercicio exercicio) throws SQLException {
        if (!contasAuxiliares.containsKey(map)) {
            preparaSelectContaAuxiliar();
            psSelectContaAuxiliar.clearParameters();
            psSelectContaAuxiliar.setString(1, map.getCodigo() + "%");
            psSelectContaAuxiliar.setLong(2, map.getPlanoDeContas());
            psSelectContaAuxiliar.setLong(3, map.getContaContabil());
            try (ResultSet rs = psSelectContaAuxiliar.executeQuery()) {
                while (rs.next()) {
                    ContaAuxiliar ca = new ContaAuxiliar();
                    ca.setId(rs.getLong("id"));
                    ca.setContaContabil((ContaContabil) getContaById(rs.getLong("contacontabil")));
                    ca.setCodigo(rs.getString("codigo"));
                    ca.setDescricao(rs.getString("descricao"));
                    MapContaAuxiliar mca = new MapContaAuxiliar(ca.getCodigo(), ca.getContaContabil().getId(), map.getPlanoDeContas());
                    if (!contasAuxiliares.containsKey(mca)) {
                        contasAuxiliares.put(mca, ca);
                    } else {
                        System.out.println("Tentou adicionar conta auxiliar que já existia? " + map.toString());
                    }
                }
                if (!contasAuxiliares.containsKey(map)) {
                    insereContaAuxiliar(map, superior, descricao, exercicio);
                }
            } catch (SQLException ex) {
                throw new ExcecaoNegocioGenerica("Erro ao buscar contas auxiliares: " + ex.getMessage(), ex);
            }
        }
    }

    private void preparaSelectContaAuxiliar() throws SQLException {
        if (psSelectContaAuxiliar == null) {
            psSelectContaAuxiliar = this.conexao.prepareStatement(Queries.selectContaAuxiliar());
        }
    }

    public void insereContaAuxiliar(MapContaAuxiliar map, Conta superior, String descricao, Exercicio ex) {
        ContaAuxiliar ca = new ContaAuxiliar();
        ca.setCodigo(map.getCodigo());
        ca.setContaContabil((ContaContabil) getContaById(map.getContaContabil()));
        ca.setPlanoDeContas(getPlanoDeContasById(map.getPlanoDeContas()));
        ca.setAtiva(true);
        ca.setDescricao(descricao);
        ca.setSuperior(superior);
        ca.setdType("ContaAuxiliar");
        ca.setExercicio(ex);
        ca.setDataRegistro(new Date());
        ca.setTemp(".");
        insertContaAuxiliar(ca);
        contasAuxiliares.put(map, ca);
    }

    private void insertContaAuxiliar(ContaAuxiliar ca) {
        ca.setId(idGenerator.createID());
        try {
            preparaInsertConta();
            psInsertConta.clearParameters();
            psInsertConta.setLong(1, ca.getId());
            psInsertConta.setString(2, ca.getCodigo());
            psInsertConta.setBoolean(3, ca.getAtiva());
            psInsertConta.setString(4, ca.getDescricao());
            psInsertConta.setLong(5, ca.getPlanoDeContas().getId());
            psInsertConta.setLong(6, ca.getSuperior().getId());
            psInsertConta.setString(7, ca.getdType());
            psInsertConta.setLong(8, ca.getExercicio().getId());
            psInsertConta.setDate(9, (java.sql.Date) ca.getDataRegistro());
            psInsertConta.executeUpdate();
            preparaInsertContaAuxiliar();
            psInsertContaAuxiliar.clearParameters();
            psInsertContaAuxiliar.setLong(1, ca.getId());
            psInsertContaAuxiliar.setString(2, ca.getTemp());
            psInsertContaAuxiliar.setLong(3, ca.getContaContabil().getId());
            psInsertContaAuxiliar.executeUpdate();
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao inserir conta auxiliar: " + ex.getMessage(), ex);
        }
    }

    private void preparaInsertConta() throws SQLException {
        if (psInsertConta == null) {
            psInsertConta = this.conexao.prepareStatement(Queries.insertConta());
        }
    }

    private void preparaInsertContaAuxiliar() throws SQLException {
        if (psInsertContaAuxiliar == null) {
            psInsertContaAuxiliar = this.conexao.prepareStatement(Queries.insertContaAuxiliar());
        }
    }

    public PlanoDeContas getPlanoDeContasAuxiliarByAno(Integer ano) throws SQLException {
        if (!planosAuxiliares.containsKey(ano)) {
            preparaSelectPlanoDeContas();
            psSelectPlanoDeContasAuxiliar.clearParameters();
            psSelectPlanoDeContasAuxiliar.setString(1, ano.toString());
            try (ResultSet rs = psSelectPlanoDeContasAuxiliar.executeQuery()) {
                if (rs.next()) {
                    PlanoDeContasExercicio pdce = new PlanoDeContasExercicio();
                    pdce.setId(rs.getLong("id"));
                    pdce.setExercicio(commonsJDBC.getExercicioByAno(ano));
                    PlanoDeContas pdc = new PlanoDeContas();
                    pdc.setId(rs.getLong("auxiliar"));
                    if (pdc.getId() != null) {
                        planosAuxiliares.put(ano, pdc);
                    } else {
                        pdc.setId(idGenerator.createID());
                        pdc.setDataRegistro(new Date());
                        pdc.setDescricao("Plano de Contas Auxiliar " + ano);
                        pdc.setExercicio(pdce.getExercicio());
                        pdce.setPlanoAuxiliar(pdc);
                        insertPlanoDeContas(pdc);
                        updatePlanoDeContasExercicio(pdce);
                    }
                } else {
                    throw new ExcecaoNegocioGenerica("Nenhum plano de contas exercício encontrado em " + ano);
                }
            } catch (SQLException ex) {
                throw new ExcecaoNegocioGenerica("Erro ao buscar plano de contas: " + ex.getMessage(), ex);
            }
        }
        return planosAuxiliares.get(ano);
    }

    private void preparaSelectPlanoDeContas() throws SQLException {
        if (psSelectPlanoDeContasAuxiliar == null) {
            psSelectPlanoDeContasAuxiliar = this.conexao.prepareStatement(Queries.selectPlanoDeContasAuxiliar());
        }
    }

    private void insertPlanoDeContas(PlanoDeContas pdc) {
        try {
            preparaInsertPlanoDeContas();
            psInsertPlanoDeContas.clearParameters();
            psInsertPlanoDeContas.setLong(1, pdc.getId());
            psInsertPlanoDeContas.setDate(2, new java.sql.Date(pdc.getDataRegistro().getTime()));
            psInsertPlanoDeContas.setString(3, pdc.getDescricao());
            setLong(psInsertPlanoDeContas, 4, null);
            psInsertPlanoDeContas.setLong(5, pdc.getExercicio().getId());
            psInsertPlanoDeContas.executeUpdate();
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao inserir plano de contas: " + ex.getMessage(), ex);
        }
    }

    private void updatePlanoDeContasExercicio(PlanoDeContasExercicio pdce) {
        try {
            preparaUpdatePlanoDeContasExercicio();
            psUpdatePlanoDeContasExercicio.clearParameters();
            psUpdatePlanoDeContasExercicio.setLong(1, pdce.getPlanoAuxiliar().getId());
            psUpdatePlanoDeContasExercicio.setLong(2, pdce.getId());
            psUpdatePlanoDeContasExercicio.executeUpdate();
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao atualizar plano de contas exercício: " + ex.getMessage(), ex);
        }
    }

    private void preparaUpdatePlanoDeContasExercicio() throws SQLException {
        if (psUpdatePlanoDeContasExercicio == null) {
            psUpdatePlanoDeContasExercicio = this.conexao.prepareStatement(Queries.updatePlanoDeContasExercicioSetPlanoAuxiliarId());
        }
    }

    private void preparaInsertPlanoDeContas() throws SQLException {
        if (psInsertPlanoDeContas == null) {
            psInsertPlanoDeContas = this.conexao.prepareStatement(Queries.insertPlanoDeContas());
        }
    }

    public void insertLancamentoContabil(LancamentoContabil lc) {
        lc.setId(idGenerator.createID());
        try {
            preparaInsertLancamentoContabil();
            psInsertLancamentoContabil.clearParameters();
            psInsertLancamentoContabil.setLong(1, lc.getId());
            psInsertLancamentoContabil.setDate(2, (java.sql.Date) lc.getDataLancamento());
            psInsertLancamentoContabil.setBigDecimal(3, lc.getValor());
            psInsertLancamentoContabil.setString(4, lc.getComplementoHistorico());
            psInsertLancamentoContabil.setString(5, lc.getNumero().toString());
            psInsertLancamentoContabil.setLong(6, lc.getContaCredito().getId());
            psInsertLancamentoContabil.setLong(7, lc.getContaDebito().getId());
            setLong(psInsertLancamentoContabil, 8, lc.getClpHistoricoContabil().getId());
            psInsertLancamentoContabil.setLong(9, lc.getUnidadeOrganizacional().getId());
            psInsertLancamentoContabil.setLong(10, lc.getLcp().getId());
            psInsertLancamentoContabil.setLong(11, lc.getItemParametroEvento().getId());
            setLong(psInsertLancamentoContabil, 12, lc.getContaAuxiliarCredito().getId());
            setLong(psInsertLancamentoContabil, 13, lc.getContaAuxiliarDebito().getId());
            psInsertLancamentoContabil.executeUpdate();
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao inserir lançamento contábil");
        }
        geraSaldosContaContabilByLancamentoContabil(lc);
    }

    private void preparaInsertLancamentoContabil() throws SQLException {
        if (psInsertLancamentoContabil == null) {
            psInsertLancamentoContabil = this.conexao.prepareStatement(Queries.insertLancamentoContabil());
        }
    }

    private void geraSaldosContaContabilByLancamentoContabil(LancamentoContabil lc) {
        MapSaldoContaContabil mapCredito = new MapSaldoContaContabil(lc.getContaCredito(), lc.getUnidadeOrganizacional(), lc.getTipoBalancete());
        geraSaldoContaContabil(mapCredito, lc.getDataLancamento(), TipoLancamentoContabil.CREDITO, lc.getValor());

        MapSaldoContaContabil mapDebito = new MapSaldoContaContabil(lc.getContaDebito(), lc.getUnidadeOrganizacional(), lc.getTipoBalancete());
        geraSaldoContaContabil(mapDebito, lc.getDataLancamento(), TipoLancamentoContabil.DEBITO, lc.getValor());

        if (lc.getContaAuxiliarCredito() != null) {
            MapSaldoContaContabil mapAuxCredito = new MapSaldoContaContabil(lc.getContaAuxiliarCredito(), lc.getUnidadeOrganizacional(), lc.getTipoBalancete());
            geraSaldoContaContabil(mapAuxCredito, lc.getDataLancamento(), TipoLancamentoContabil.CREDITO, lc.getValor());
        }

        if (lc.getContaAuxiliarDebito() != null) {
            MapSaldoContaContabil mapAuxDebito = new MapSaldoContaContabil(lc.getContaAuxiliarDebito(), lc.getUnidadeOrganizacional(), lc.getTipoBalancete());
            geraSaldoContaContabil(mapAuxDebito, lc.getDataLancamento(), TipoLancamentoContabil.DEBITO, lc.getValor());
        }
    }

    private void geraSaldoContaContabil(MapSaldoContaContabil map, Date data, TipoLancamentoContabil tlc, BigDecimal valor) {
        SaldoContaContabil saldo = getUltimoSaldoContaContabilPorData(map, data);
        setaValorCreditoOuDebito(saldo, tlc, valor);
        if (saldo.getId() == null) {
            insertSaldoContaContabil(saldo);
        } else {
            if (saldo.getDataSaldo().equals(data)) {
                updateSaldoContaContabil(saldo);
            } else {
                insertSaldoContaContabil(saldo);
            }
        }
        reprocessaSaldosFuturos(map, data, tlc, valor);
    }

    private void reprocessaSaldosFuturos(MapSaldoContaContabil map, Date data, TipoLancamentoContabil tlc, BigDecimal valor) {
        for (SaldoContaContabil saldo : getSaldosPosteriores(map, data)) {
            setaValorCreditoOuDebito(saldo, tlc, valor);
            updateSaldoContaContabil(saldo);
        }
    }

    private List<SaldoContaContabil> getSaldosPosteriores(MapSaldoContaContabil map, Date data) {
        List<SaldoContaContabil> lista = new ArrayList<>();
        for (SaldoContaContabil saldo : saldos.get(map)) {
            if (saldo.getDataSaldo().after(data)) {
                lista.add(saldo);
            }
        }
        return lista;
    }

    private void setaValorCreditoOuDebito(SaldoContaContabil saldo, TipoLancamentoContabil tlc, BigDecimal valor) {
        switch (tlc) {
            case CREDITO:
                saldo.setTotalCredito(saldo.getTotalCredito().add(valor));
                break;
            case DEBITO:
                saldo.setTotalDebito(saldo.getTotalDebito().add(valor));
                break;
            default:
                throw new ExcecaoNegocioGenerica("Nenhum tipo de lançamento contábil foi identificado para a operação de saldo da conta contábil: " + tlc.toString());
        }
    }

    private SaldoContaContabil getUltimoSaldoContaContabilPorData(MapSaldoContaContabil map, Date data) {
        loadSaldosContaContabilByMap(map);
        if (saldos.get(map).isEmpty()) {
            SaldoContaContabil saldo = new SaldoContaContabil();
            saldo.setContaContabil(map.getContaContabil());
            saldo.setUnidadeOrganizacional(map.getUnidadeOrganizacional());
            saldo.setTipoBalancete(map.getTipoBalancete());
            saldo.setDataSaldo(data);
            return saldo;
        }
        SaldoContaContabil ultimoSaldo = null;
        for (SaldoContaContabil s : saldos.get(map)) {
            if (s.getDataSaldo().equals(data)) {
                return s;
            } else {
                if (s.getDataSaldo().before(data) && ultimoSaldo == null) {
                    ultimoSaldo = s;
                } else {
                    if (s.getDataSaldo().before(data) && s.getDataSaldo().after(ultimoSaldo.getDataSaldo())) {
                        ultimoSaldo = s;
                    }
                }
            }
        }
        return ultimoSaldo;
    }

    private void loadSaldosContaContabilByMap(MapSaldoContaContabil map) {
        if (!saldos.containsKey(map)) {
            List<SaldoContaContabil> lista = new ArrayList<>();
            try {
                preparaSelectSaldoContaContabil();
                psSelectSaldoContaContabil.clearParameters();
                psSelectSaldoContaContabil.setLong(1, map.getContaContabil().getId());
                psSelectSaldoContaContabil.setLong(2, map.getUnidadeOrganizacional().getId());
                psSelectSaldoContaContabil.setString(3, map.getTipoBalancete().name());
                ResultSet rs = psSelectSaldoContaContabil.executeQuery();
                while (rs.next()) {
                    SaldoContaContabil saldo = new SaldoContaContabil();
                    saldo.setId(rs.getLong("id"));
                    saldo.setDataSaldo(rs.getDate("datasaldo"));
                    saldo.setTotalCredito(rs.getBigDecimal("totalcredito"));
                    saldo.setTotalDebito(rs.getBigDecimal("totaldebito"));
                    saldo.setContaContabil(map.getContaContabil());
                    saldo.setUnidadeOrganizacional(map.getUnidadeOrganizacional());
                    saldo.setTipoBalancete(map.getTipoBalancete());
                    lista.add(saldo);
                }
            } catch (SQLException ex) {
                throw new ExcecaoNegocioGenerica("Erro ao buscar saldos contábeis: " + ex.getMessage(), ex);
            }
            saldos.put(map, lista);
        }
    }

    private void preparaSelectSaldoContaContabil() throws SQLException {
        if (psSelectSaldoContaContabil == null) {
            psSelectSaldoContaContabil = this.conexao.prepareStatement(Queries.selectSaldoContaContabil());
        }
    }

    private void insertSaldoContaContabil(SaldoContaContabil saldo) {
        saldo.setId(idGenerator.createID());
        try {
            preparaInsertSaldoContaContabil();
            psInsertSaldoContaContabil.clearParameters();
            psInsertSaldoContaContabil.setLong(1, saldo.getId());
            psInsertSaldoContaContabil.setDate(2, (java.sql.Date) saldo.getDataSaldo());
            psInsertSaldoContaContabil.setBigDecimal(3, saldo.getTotalCredito());
            psInsertSaldoContaContabil.setBigDecimal(4, saldo.getTotalDebito());
            psInsertSaldoContaContabil.setString(5, saldo.getTipoBalancete().name());
            psInsertSaldoContaContabil.setLong(6, saldo.getUnidadeOrganizacional().getId());
            psInsertSaldoContaContabil.setLong(7, saldo.getContaContabil().getId());
            psInsertSaldoContaContabil.executeUpdate();
            addSaldoContaContabilOnMap(saldo);
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao inserir saldo conta contábil: " + ex.getMessage(), ex);
        }
    }

    private void preparaInsertSaldoContaContabil() throws SQLException {
        if (psInsertSaldoContaContabil == null) {
            psInsertSaldoContaContabil = this.conexao.prepareStatement(Queries.insertSaldoContaContabil());
        }
    }

    private void addSaldoContaContabilOnMap(SaldoContaContabil saldo) {
        MapSaldoContaContabil map = new MapSaldoContaContabil(saldo.getContaContabil(), saldo.getUnidadeOrganizacional(), saldo.getTipoBalancete());
        if (saldos.containsKey(map)) {
            saldos.get(map).add(saldo);
        } else {
            List<SaldoContaContabil> lista = new ArrayList<>();
            lista.add(saldo);
            saldos.put(map, lista);
        }
    }

    private void updateSaldoContaContabil(SaldoContaContabil saldo) {
        try {
            preparaUpdateSaldoContaContabil();
            psUpdateSaldoContaContabil.clearParameters();
            psUpdateSaldoContaContabil.setBigDecimal(1, saldo.getTotalCredito());
            psUpdateSaldoContaContabil.setBigDecimal(2, saldo.getTotalDebito());
            psUpdateSaldoContaContabil.setLong(3, saldo.getId());
            psUpdateSaldoContaContabil.executeUpdate();
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao atualizar saldo de conta contábil: " + ex.getMessage(), ex);
        }
    }

    private void preparaUpdateSaldoContaContabil() throws SQLException {
        if (psUpdateSaldoContaContabil == null) {
            psUpdateSaldoContaContabil = this.conexao.prepareStatement(Queries.updateSaldoContaContabil());
        }
    }
}
