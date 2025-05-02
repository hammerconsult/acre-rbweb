package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoReceitaExtra;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by fernando on 31/01/15.
 */
public class ConsignacaoJDBC extends ClassPatternJDBC {

    private static ConsignacaoJDBC instancia;
    private static Connection conexao;
    private IdGenerator idGenerator = IdGenerator.createInstance(conexao);
    private PreparedStatement psInsertReceitaExtra;
    private PreparedStatement psSelectUltimoNumeroReceitaExtra;

    public ConsignacaoJDBC(Connection conn) {
        this.conexao = conn;
    }

    public static synchronized ConsignacaoJDBC createInstance(Connection conn) {
        if (instancia == null) {
            instancia = new ConsignacaoJDBC(conn);
        }
        showConnection(instancia.getClass());
        return instancia;
    }

    public void createReceitaExtra(Pagamento pag) {
        if (allFieldsNotNull(pag)) {
            for (RetencaoPgto rp : pag.getRetencaoPgtos()) {
                ReceitaExtra re = new ReceitaExtra();
                re.setId(idGenerator.createID());
                re.setComplementoHistorico(rp.getComplementoHistorico());
                re.setContaExtraorcamentaria(rp.getContaExtraorcamentaria());
                re.setDataReceita(pag.getDataPagamento());
                re.setFonteDeRecursos(rp.getFonteDeRecursos());
                re.setValor(rp.getValor());
                re.setSaldo(re.getValor());
                re.setUnidadeOrganizacional(pag.getLiquidacao().getEmpenho().getUnidadeOrganizacional());
                re.setUnidadeOrganizacionalAdm(pag.getLiquidacao().getEmpenho().getUnidadeOrganizacionalAdm());
                re.setExercicio(pag.getExercicio());
                re.setSubConta(rp.getSubConta());
                re.setSituacaoReceitaExtra(SituacaoReceitaExtra.ABERTO);
                re.setRetencaoPgto(rp);
                re.setUsuarioSistema(rp.getUsuarioSistema());
                re.setPessoa(rp.getPessoa());
                re.setClasseCredor(rp.getClasseCredor());
                re.setTipoConsignacao(rp.getTipoConsignacao());
                re.setNumero(getUltimoNumeroReceitaExtra(re.getExercicio(), re.getUnidadeOrganizacional()));
                insertReceitaExtra(re);
            }
        }
    }

    private String getUltimoNumeroReceitaExtra(Exercicio ex, UnidadeOrganizacional unidade) {
        try {
            BigDecimal maior = BigDecimal.ONE;
            preparaSelectUltimoNumeroReceitaExtra();
            psSelectUltimoNumeroReceitaExtra.clearParameters();
            psSelectUltimoNumeroReceitaExtra.setLong(1, ex.getId());
            psSelectUltimoNumeroReceitaExtra.setLong(2, unidade.getId());
            psSelectUltimoNumeroReceitaExtra.setLong(3, ex.getId());
            psSelectUltimoNumeroReceitaExtra.setLong(4, unidade.getId());
            ResultSet rs = psSelectUltimoNumeroReceitaExtra.executeQuery();
            if (rs.next()) {
                BigDecimal encontrado = rs.getBigDecimal("maior");
                maior = maior.add(encontrado);
            }
            return maior.toString();
        } catch (SQLException e) {
            throw new ExcecaoNegocioGenerica("Erro ao buscar último número de receita extra: " + e.getMessage(), e);
        }
    }

    private void preparaSelectUltimoNumeroReceitaExtra() throws SQLException {
        if (psSelectUltimoNumeroReceitaExtra == null) {
            String sql = "select max(maior) as maior from ( " +
                    "select " +
                    "  max(to_number(re.numero)) as maior " +
                    "from receitaextra re " +
                    "where re.exercicio_id = ? " +
                    "and re.unidadeorganizacional_id = ? " +
                    "having max(to_number(re.numero)) is not null " +
                    "union all " +
                    "select " +
                    "  max(to_number(est.numero)) as maior " +
                    "from receitaextraestorno est " +
                    "where est.exercicio_id = ? " +
                    "and est.unidadeorganizacional_id = ? " +
                    "having max(to_number(est.numero)) is not null)";
            psSelectUltimoNumeroReceitaExtra = this.conexao.prepareStatement(sql);
        }
    }

    private void insertReceitaExtra(ReceitaExtra re) {
        try {
            preparaInsertReceitaExtra();
            psInsertReceitaExtra.clearParameters();
            psInsertReceitaExtra.setLong(1, re.getId());
            psInsertReceitaExtra.setString(2, re.getNumero());
            psInsertReceitaExtra.setDate(3, new java.sql.Date(re.getDataReceita().getTime()));
            if (re.getDataConciliacao() == null) {
                setDate(psInsertReceitaExtra, 4, null);
            } else {
                psInsertReceitaExtra.setDate(4, new java.sql.Date(re.getDataConciliacao().getTime()));
            }
            psInsertReceitaExtra.setString(5, re.getComplementoHistorico());
            psInsertReceitaExtra.setBigDecimal(6, re.getValor());
            psInsertReceitaExtra.setBigDecimal(7, re.getSaldo());
            psInsertReceitaExtra.setString(8, re.getSituacaoReceitaExtra().name());
            psInsertReceitaExtra.setString(9, re.getTipoConsignacao().name());
            psInsertReceitaExtra.setLong(10, re.getFonteDeRecursos().getId());
            psInsertReceitaExtra.setLong(11, re.getContaExtraorcamentaria().getId());
            psInsertReceitaExtra.setLong(12, re.getUnidadeOrganizacional().getId());
            psInsertReceitaExtra.setLong(13, re.getSubConta().getId());
            psInsertReceitaExtra.setLong(14, re.getExercicio().getId());
            psInsertReceitaExtra.setLong(15, re.getPessoa().getId());
            psInsertReceitaExtra.setLong(16, re.getClasseCredor().getId());
            psInsertReceitaExtra.executeUpdate();
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao inserir receita extraorçamentária: " + ex.getMessage(), ex);
        }
    }

    private void preparaInsertReceitaExtra() throws SQLException {
        if (psInsertReceitaExtra == null) {
            String sql = "insert into receitaextra (ID, numero, datareceita, dataconciliacao"
                    + ", complementohistorico, valor, saldo, situacaoreceitaextra, tipoconsignacao "
                    + ", fontederecursos_id, contaextraorcamentaria_id, unidadeorganizacional_id"
                    + ", subconta_id, exercicio_id, pessoa_id, classecredor_id) "
                    + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            psInsertReceitaExtra = this.conexao.prepareStatement(sql);
        }
    }
}
