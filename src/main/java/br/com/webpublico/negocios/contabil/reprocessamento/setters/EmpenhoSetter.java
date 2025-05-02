package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.repositorios.jdbc.util.JDBCUtil;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;


public class EmpenhoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = " INSERT INTO EMPENHO (ID, DATAEMPENHO, VALOR, SALDO, DESPESAORC_ID, FONTEDESPESAORC_ID, USUARIOSISTEMA_ID, UNIDADEORGANIZACIONAL_ID, HISTORICOCONTABIL_ID, FORNECEDOR_ID, MOVIMENTODESPESAORC_ID, NUMERO, COMPLEMENTOHISTORICO, TIPOEMPENHO, SOLICITACAOEMPENHO_ID, CATEGORIAORCAMENTARIA, EMPENHO_ID, EXERCICIO_ID, NUMEROORIGINAL, EXERCICIOORIGINAL_ID, IMPORTADO, DIVIDAPUBLICA_ID, CONVENIODESPESA_ID, CONVENIORECEITA_ID, TIPOEMPENHOINTEGRACAO, CLASSECREDOR_ID, EVENTOCONTABIL_ID, FOLHADEPAGAMENTO_ID, PROPOSTACONCESSAODIARIA_ID, UNIDADEORGANIZACIONALADM_ID, SALDODISPONIVEL, CONTRATO_ID, TIPOCONTADESPESA, HISTORICONOTA, HISTORICORAZAO, TIPORESTOSPROCESSADOS, TIPORESTOSINSCRITOS, SUBTIPODESPESA, SEMDISPONIBILIDADEFINANCEIRA, TRANSPORTADO, MODALIDADELICITACAO, PARCELADIVIDAPUBLICA_ID, TIPORECONHECIMENTO, SALDOOBRIGACAOPAGARANTESEMP, SALDOOBRIGACAOPAGARDEPOISEMP, OPERACAODECREDITO_ID, EXTENSAOFONTERECURSO_ID, CONTADESPESA_ID, FONTEDERECURSOS_ID, CONTADEDESTINACAO_ID, CODIGOCONTATCE, VERSAO, RECONHECIMENTODIVIDA_ID, EXECUCAOPROCESSO_ID) " +
        " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE = "UPDATE EMPENHO " +
        " SET DATAEMPENHO = ?, VALOR = ?, SALDO = ?, DESPESAORC_ID = ?, FONTEDESPESAORC_ID = ?, USUARIOSISTEMA_ID = ?, UNIDADEORGANIZACIONAL_ID = ?, HISTORICOCONTABIL_ID = ?, " +
        " FORNECEDOR_ID = ?, MOVIMENTODESPESAORC_ID = ?, NUMERO = ?, COMPLEMENTOHISTORICO = ?, TIPOEMPENHO = ?, SOLICITACAOEMPENHO_ID = ?, CATEGORIAORCAMENTARIA = ?, EMPENHO_ID = ?," +
        " EXERCICIO_ID = ?, NUMEROORIGINAL = ?, EXERCICIOORIGINAL_ID = ?, IMPORTADO = ?, DIVIDAPUBLICA_ID = ?, CONVENIODESPESA_ID = ?, CONVENIORECEITA_ID = ?, TIPOEMPENHOINTEGRACAO = ?, " +
        " CLASSECREDOR_ID = ?, EVENTOCONTABIL_ID = ?, FOLHADEPAGAMENTO_ID = ?, PROPOSTACONCESSAODIARIA_ID = ?, UNIDADEORGANIZACIONALADM_ID = ?, SALDODISPONIVEL = ?, CONTRATO_ID = ?, " +
        " TIPOCONTADESPESA = ?, HISTORICONOTA = ?, HISTORICORAZAO = ?, TIPORESTOSPROCESSADOS = ?, TIPORESTOSINSCRITOS = ?, SUBTIPODESPESA = ?, SEMDISPONIBILIDADEFINANCEIRA = ?, TRANSPORTADO = ?," +
        " MODALIDADELICITACAO = ?, PARCELADIVIDAPUBLICA_ID = ?, TIPORECONHECIMENTO = ?, SALDOOBRIGACAOPAGARANTESEMP = ?, SALDOOBRIGACAOPAGARDEPOISEMP = ?, OPERACAODECREDITO_ID = ?, " +
        " EXTENSAOFONTERECURSO_ID = ?, CONTADESPESA_ID = ?, FONTEDERECURSOS_ID = ?, CONTADEDESTINACAO_ID = ?, CODIGOCONTATCE = ?, VERSAO = ?, RECONHECIMENTODIVIDA_ID = ?, EXECUCAOPROCESSO_ID = ? " +
        " WHERE ID = ?";

    private List<Empenho> empenhos;
    private final SingletonGeradorId geradorDeIds;

    public EmpenhoSetter(List<Empenho> empenhos, SingletonGeradorId geradorDeIds) {
        this.empenhos = empenhos;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        Empenho emp = empenhos.get(i);
        emp.setId(geradorDeIds.getProximoId());

        ps.setLong(1, emp.getId());
        ps.setDate(2, new Date(emp.getDataEmpenho().getTime()));
        ps.setBigDecimal(3, emp.getValor());
        ps.setBigDecimal(4, emp.getSaldo());
        ps.setLong(5, emp.getDespesaORC().getId());
        ps.setLong(6, emp.getFonteDespesaORC().getId());
        JDBCUtil.atribuirLong(ps, 7, emp.getUsuarioSistema() != null ? emp.getUsuarioSistema().getId() : null);
        JDBCUtil.atribuirLong(ps, 8, emp.getUnidadeOrganizacional() != null ? emp.getUnidadeOrganizacional().getId() : null);
        JDBCUtil.atribuirLong(ps, 9, emp.getHistoricoContabil() != null ? emp.getHistoricoContabil().getId() : null);
        JDBCUtil.atribuirLong(ps, 10, emp.getFornecedor() != null ? emp.getFornecedor().getId() : null);
        JDBCUtil.atribuirLong(ps, 11, emp.getMovimentoDespesaORC() != null ? emp.getMovimentoDespesaORC().getId() : null);
        ps.setString(12, emp.getNumero());
        ps.setString(13, emp.getComplementoHistorico());
        ps.setString(14, emp.getTipoEmpenho().name());
        JDBCUtil.atribuirLong(ps, 15, emp.getSolicitacaoEmpenho() != null ? emp.getSolicitacaoEmpenho().getId() : null);
        ps.setString(16, emp.getCategoriaOrcamentaria().name());
        JDBCUtil.atribuirLong(ps, 17, emp.getEmpenho() != null ? emp.getEmpenho().getId() : null);
        JDBCUtil.atribuirLong(ps, 18, emp.getExercicio() != null ? emp.getExercicio().getId() : null);
        ps.setString(19, emp.getNumeroOriginal());
        JDBCUtil.atribuirLong(ps, 20, emp.getExercicioOriginal() != null ? emp.getExercicioOriginal().getId() : null);
        ps.setBoolean(21, emp.getImportado());
        JDBCUtil.atribuirLong(ps, 22, emp.getDividaPublica() != null ? emp.getDividaPublica().getId() : null);
        JDBCUtil.atribuirLong(ps, 23, emp.getConvenioDespesa() != null ? emp.getConvenioDespesa().getId() : null);
        JDBCUtil.atribuirLong(ps, 24, emp.getConvenioReceita() != null ? emp.getConvenioReceita().getId() : null);
        JDBCUtil.atribuirString(ps, 25, emp.getTipoEmpenhoIntegracao() != null ? emp.getTipoEmpenhoIntegracao().name() : null);
        JDBCUtil.atribuirLong(ps, 26, emp.getClasseCredor() != null ? emp.getClasseCredor().getId() : null);
        JDBCUtil.atribuirLong(ps, 27, emp.getEventoContabil() != null ? emp.getEventoContabil().getId() : null);
        JDBCUtil.atribuirLong(ps, 28, emp.getFolhaDePagamento() != null ? emp.getFolhaDePagamento().getId() : null);
        JDBCUtil.atribuirLong(ps, 29, emp.getPropostaConcessaoDiaria() != null ? emp.getPropostaConcessaoDiaria().getId() : null);
        JDBCUtil.atribuirLong(ps, 30, emp.getUnidadeOrganizacionalAdm() != null ? emp.getUnidadeOrganizacionalAdm().getId() : null);
        ps.setBigDecimal(31, emp.getSaldoDisponivel());
        JDBCUtil.atribuirLong(ps, 32, emp.getContrato() != null ? emp.getContrato().getId() : null);
        JDBCUtil.atribuirString(ps, 33, emp.getTipoContaDespesa() != null ? emp.getTipoContaDespesa().name() : null);
        JDBCUtil.atribuirString(ps, 34, emp.getHistoricoNota());
        JDBCUtil.atribuirString(ps, 35, emp.getHistoricoRazao());
        JDBCUtil.atribuirString(ps, 36, emp.getTipoRestosProcessados() != null ? emp.getTipoRestosProcessados().name() : null);
        JDBCUtil.atribuirString(ps, 37, emp.getTipoRestosInscritos() != null ? emp.getTipoRestosInscritos().name() : null);
        JDBCUtil.atribuirString(ps, 38, emp.getSubTipoDespesa() != null ? emp.getSubTipoDespesa().name() : null);
        ps.setBoolean(39, emp.getSemDisponibilidadeFinanceira());
        ps.setBoolean(40, emp.getTransportado());
        JDBCUtil.atribuirString(ps, 41, emp.getModalidadeLicitacao() != null ? emp.getModalidadeLicitacao().name() : null);
        JDBCUtil.atribuirLong(ps, 42, emp.getParcelaDividaPublica() != null ? emp.getParcelaDividaPublica().getId() : null);
        JDBCUtil.atribuirString(ps, 43, emp.getTipoReconhecimento() != null ? emp.getTipoReconhecimento().name() : null);
        ps.setBigDecimal(44, emp.getSaldoObrigacaoPagarAntesEmp());
        ps.setBigDecimal(45, emp.getSaldoObrigacaoPagarDepoisEmp());
        JDBCUtil.atribuirLong(ps, 46, emp.getOperacaoDeCredito() != null ? emp.getOperacaoDeCredito().getId() : null);
        JDBCUtil.atribuirLong(ps, 47, emp.getExtensaoFonteRecurso() != null ? emp.getExtensaoFonteRecurso().getId() : null);
        JDBCUtil.atribuirLong(ps, 48, emp.getContaDespesa() != null ? emp.getContaDespesa().getId() : null);
        JDBCUtil.atribuirLong(ps, 49, emp.getFonteDeRecursos() != null ? emp.getFonteDeRecursos().getId() : null);
        JDBCUtil.atribuirLong(ps, 50, emp.getContaDeDestinacao() != null ? emp.getContaDeDestinacao().getId() : null);
        JDBCUtil.atribuirString(ps, 51, emp.getCodigoContaTCE());
        ps.setLong(52, emp.getVersao());
        JDBCUtil.atribuirLong(ps, 53, emp.getReconhecimentoDivida() != null ? emp.getReconhecimentoDivida().getId() : null);
        JDBCUtil.atribuirLong(ps, 54, emp.getExecucaoProcesso() != null ? emp.getExecucaoProcesso().getId() : null);
    }

    @Override
    public int getBatchSize() {
        return empenhos.size();
    }
}
