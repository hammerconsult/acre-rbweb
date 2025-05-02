package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.entidades.Agencia;
import br.com.webpublico.entidades.Banco;
import br.com.webpublico.entidades.ContaBancariaEntidade;
import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by venom on 23/02/15.
 */
public final class BancarioJDBC extends ClassPatternJDBC {

    private static BancarioJDBC instancia;
    private PreparedStatement psSelectBanco;
    private PreparedStatement psSelectAgencia;
    private PreparedStatement psSelectSubconta;
    private PreparedStatement psSelectContaBancariaEntidade;
    private Map<Long, Banco> bancos = new HashMap<>();
    private Map<Long, Agencia> agencias = new HashMap<>();
    private Map<Long, SubConta> contasFinanceiras = new HashMap<>();
    private Map<Long, ContaBancariaEntidade> contasBancarias = new HashMap<>();

    private BancarioJDBC(Connection conn) {
        this.conexao = conn;
    }

    public static synchronized BancarioJDBC createInstance(Connection conn) {
        if (instancia == null) {
            instancia = new BancarioJDBC(conn);
        }
        showConnection(instancia.getClass());
        return instancia;
    }

    public Banco getBancoById(Long id) throws SQLException {
        if (bancos.isEmpty()) {
            loadBancos();
        }
        return bancos.get(id);
    }

    private void loadBancos() throws SQLException {
        preparaSelectBancos();
        try (ResultSet rs = psSelectBanco.executeQuery()) {
            while (rs.next()) {
                Banco b = new Banco();
                b.setId(rs.getLong("id"));
                b.setDescricao(rs.getString("descricao"));
                b.setNumeroBanco(rs.getString("numero"));
                bancos.put(b.getId(), b);
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao carregar bancos: " + ex.getMessage(), ex);
        }
    }

    private void preparaSelectBancos() throws SQLException {
        if (psSelectBanco == null) {
            psSelectBanco = this.conexao.prepareStatement(Queries.selectBanco());
        }
    }

    public Agencia getAgenciaById(Long id) throws SQLException {
        if (!agencias.containsKey(id)) {
            preparaSelectAgencia();
            psSelectAgencia.clearParameters();
            psSelectAgencia.setLong(1, id);
            try (ResultSet rs = psSelectAgencia.executeQuery()) {
                if (rs.next()) {
                    Agencia ag = new Agencia();
                    ag.setId(rs.getLong("id"));
                    ag.setNomeAgencia(rs.getString("nome"));
                    ag.setNumeroAgencia(rs.getString("numero"));
                    ag.setDigitoVerificador(rs.getString("digito"));
                    ag.setBanco(getBancoById(rs.getLong("banco")));
                    agencias.put(ag.getId(), ag);
                }
            } catch (SQLException ex) {
                throw new ExcecaoNegocioGenerica("Erro ao buscar agência: " + ex.getMessage(), ex);
            }
        }
        return agencias.get(id);
    }

    private void preparaSelectAgencia() throws SQLException {
        if (psSelectAgencia == null) {
            psSelectAgencia = this.conexao.prepareStatement(Queries.selectAgencia());
        }
    }

    public ContaBancariaEntidade getContaBancariaEntidadeById(Long id) throws SQLException {
        if (!contasBancarias.containsKey(id)) {
            preparaSelectContaBancariaEntidade();
            psSelectContaBancariaEntidade.clearParameters();
            psSelectContaBancariaEntidade.setLong(1, id);
            try (ResultSet rs = psSelectContaBancariaEntidade.executeQuery()) {
                if (rs.next()) {
                    ContaBancariaEntidade cbe = new ContaBancariaEntidade();
                    cbe.setId(rs.getLong("id"));
                    cbe.setNumeroConta(rs.getString("numero"));
                    cbe.setDigitoVerificador(rs.getString("digito"));
                    cbe.setAgencia(getAgenciaById(rs.getLong("agencia")));
                    contasBancarias.put(cbe.getId(), cbe);
                }
            } catch (SQLException ex) {
                throw new ExcecaoNegocioGenerica("Erro ao buscar conta bancária: " + ex.getMessage(), ex);
            }
        }
        return contasBancarias.get(id);
    }

    private void preparaSelectContaBancariaEntidade() throws SQLException {
        if (psSelectContaBancariaEntidade == null) {
            psSelectContaBancariaEntidade = this.conexao.prepareStatement(Queries.selectContaBancariaEntidade());
        }
    }

    public SubConta getSubContaById(Long id) throws SQLException {
        if (!contasFinanceiras.containsKey(id)) {
            preparaSelectSubconta();
            psSelectSubconta.clearParameters();
            psSelectSubconta.setLong(1, id);
            try (ResultSet rs = psSelectSubconta.executeQuery()) {
                if (rs.next()) {
                    SubConta subconta = new SubConta();
                    subconta.setId(rs.getLong("id"));
                    subconta.setCodigo(rs.getString("codigo"));
                    subconta.setContaBancariaEntidade(getContaBancariaEntidadeById(rs.getLong("contabancaria")));
                    contasFinanceiras.put(id, subconta);
                }
            } catch (SQLException ex) {
                throw new ExcecaoNegocioGenerica("Erro ao recuperar Conta Financeira do objeto parâmetro: " + ex.getMessage(), ex);
            }
        }
        return contasFinanceiras.get(id);
    }

    private void preparaSelectSubconta() throws SQLException {
        if (psSelectSubconta == null) {
            psSelectSubconta = this.conexao.prepareStatement(Queries.selectSubconta());
        }
    }
}
