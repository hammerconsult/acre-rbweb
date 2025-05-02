package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.entidades.DiariaContabilizacao;
import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.entidades.PropostaConcessaoDiaria;
import br.com.webpublico.enums.OperacaoDiariaContabilizacao;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoProposta;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by fernando on 31/01/15.
 */
public class InscricaoDiariaJDBC extends ClassPatternJDBC {

    private static InscricaoDiariaJDBC instancia;
    private PreparedStatement psInsertDiariaContabilizacao;

    private InscricaoDiariaJDBC(Connection conn) {
        this.conexao = conn;
    }

    public static synchronized InscricaoDiariaJDBC createInstance(Connection conn) {
        if (instancia == null) {
            instancia = new InscricaoDiariaJDBC(conn);
        }
        showConnection(instancia.getClass());
        return instancia;
    }

    public void createDiariaContabilizacao(Pagamento pgto) {
        if (allFieldsNotNull(pgto)) {
            TipoContaDespesa tipoContaDespesa = pgto.getLiquidacao().getEmpenho().getTipoContaDespesa();
            if (pgto.getLiquidacao().getEmpenho().getPropostaConcessaoDiaria() != null) {
                DiariaContabilizacao dc = new DiariaContabilizacao();
                PropostaConcessaoDiaria pcd = pgto.getLiquidacao().getEmpenho().getPropostaConcessaoDiaria();

                if (tipoContaDespesa.equals(TipoContaDespesa.DIARIA_CIVIL)) {
                    dc.setOperacaoDiariaContabilizacao(OperacaoDiariaContabilizacao.INSCRICAO);
                    dc.setTipoLancamento(TipoLancamento.NORMAL);
                    dc.setTipoProposta(TipoProposta.CONCESSAO_DIARIA);
                    dc.setPropostaConcessaoDiaria(pcd);
                    dc.setValor(pgto.getValor());
                    dc.setHistorico(pgto.getComplementoHistorico());
                    dc.setUnidadeOrganizacional(pgto.getUnidadeOrganizacional());
                    dc.setDataDiaria(pgto.getDataPagamento());
                    insertDiariaContabilizacao(dc);
                }
                if (tipoContaDespesa.equals(TipoContaDespesa.DIARIA_CAMPO)) {
                    dc.setOperacaoDiariaContabilizacao(OperacaoDiariaContabilizacao.INSCRICAO);
                    dc.setTipoLancamento(TipoLancamento.NORMAL);
                    dc.setTipoProposta(TipoProposta.CONCESSAO_DIARIACAMPO);
                    dc.setPropostaConcessaoDiaria(pcd);
                    dc.setValor(pgto.getValor());
                    dc.setHistorico(pgto.getComplementoHistorico());
                    dc.setUnidadeOrganizacional(pgto.getUnidadeOrganizacional());
                    dc.setDataDiaria(pgto.getDataPagamento());
                    insertDiariaContabilizacao(dc);
                }
                if (tipoContaDespesa.equals(TipoContaDespesa.SUPRIMENTO_FUNDO)) {
                    dc.setOperacaoDiariaContabilizacao(OperacaoDiariaContabilizacao.INSCRICAO);
                    dc.setTipoLancamento(TipoLancamento.NORMAL);
                    dc.setTipoProposta(TipoProposta.SUPRIMENTO_FUNDO);
                    dc.setPropostaConcessaoDiaria(pcd);
                    dc.setValor(pgto.getValor());
                    dc.setHistorico(pgto.getComplementoHistorico());
                    dc.setUnidadeOrganizacional(pgto.getUnidadeOrganizacional());
                    dc.setDataDiaria(pgto.getDataPagamento());
                    insertDiariaContabilizacao(dc);
                }
            }
        }
    }

    private void insertDiariaContabilizacao(DiariaContabilizacao dc) {
        try {
            preparaInsertDiariaContabilizacao();
            psInsertDiariaContabilizacao.clearParameters();
            psInsertDiariaContabilizacao.setLong(1, dc.getId());
            psInsertDiariaContabilizacao.setDate(2, new java.sql.Date(dc.getDataDiaria().getTime()));
            psInsertDiariaContabilizacao.setString(3, dc.getOperacaoDiariaContabilizacao().name());
            psInsertDiariaContabilizacao.setString(4, dc.getHistorico());
            psInsertDiariaContabilizacao.setBigDecimal(5, dc.getValor());
            psInsertDiariaContabilizacao.setString(6, dc.getTipoLancamento().name());
            psInsertDiariaContabilizacao.setLong(7, dc.getContaDespesa().getId());
            psInsertDiariaContabilizacao.setLong(8, dc.getPropostaConcessaoDiaria().getId());
            psInsertDiariaContabilizacao.setLong(9, dc.getUnidadeOrganizacional().getId());
            psInsertDiariaContabilizacao.executeUpdate();
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao inserir diária contabilização: " + ex.getMessage(), ex);
        }
    }

    private void preparaInsertDiariaContabilizacao() throws SQLException {
        if (psInsertDiariaContabilizacao == null) {
            String sql = "insert into diariacontabilizacao (ID, datadiaria, operacaodiariacontabilizacao" +
                    ", historico, valor, tipolancamento, contadespesa_id, propostaconcessaodiaria_id" +
                    ", unidadeorganizacional_id) values (?,?,?,?,?,?,?,?,?) ";
            psInsertDiariaContabilizacao = this.conexao.prepareStatement(sql);
        }
    }
}
