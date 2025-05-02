package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.AcompanhamentoLiberacaoFinanc;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.TipoMovimento;
import br.com.webpublico.negocios.FonteDeRecursosFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.contabil.financeiro.UnidadeAcompanhamentoFinanceiroFacade;

import javax.ejb.EJB;
import java.io.Serializable;
import java.util.List;

/**
 * Created by mateus on 11/07/17.
 */

public abstract class RelatorioAcompanhamentoLiberacaoFinanceiraSuperFacade extends RelatorioContabilSuperFacade implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private UnidadeAcompanhamentoFinanceiroFacade unidadeAcompanhamentoFinanceiroFacade;

    public abstract List<AcompanhamentoLiberacaoFinanc> recuperarRelatorio(List<ParametrosRelatorios> parametros, Integer mesSelecionado, TipoMovimento tipoMovimento, Boolean exibirContasBancarias);

    protected StringBuilder buscarQuerySaldoFonteDespesaOrc() {
        StringBuilder sql = new StringBuilder();
        sql.append("  FROM SALDOFONTEDESPESAORC A ")
            .append(" INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID ")
            .append(" INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID ")
            .append(" inner join PROVISAOPPAFONTE ppf on b.PROVISAOPPAFONTE_ID = ppf.id ")
            .append(" inner join contadedestinacao cd on ppf.DESTINACAODERECURSOS_ID = cd.id ")
            .append(" inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_id = fr.id ")
            .append(" INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID ")
            .append(" INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID ")
            .append(buscarJoinVwUnidade("a.unidadeorganizacional_id"))
            .append(" INNER JOIN ")
            .append("  (SELECT A.FONTEDESPESAORC_ID AS FONTE, a.unidadeorganizacional_id as unidade,MAX(A.DATASALDO) AS MAXDATE FROM SALDOFONTEDESPESAORC A ")
            .append("   where a.datasaldo <= to_date(:MESFINAL, 'dd/mm/yyyy') ")
            .append("  GROUP BY A.FONTEDESPESAORC_ID, A.unidadeorganizacional_id) FONTES ON FONTES.FONTE = A.FONTEDESPESAORC_ID AND A.DATASALDO = FONTES.MAXDATE and a.unidadeorganizacional_id = fontes.unidade ");
        return sql;
    }

    protected StringBuilder buscarAndConfiguracaoRetirada() {
        StringBuilder sql = new StringBuilder();
        sql.append("   AND (CAST(RETIRADA.CODIGO AS NUMBER) >= CAST(CAF.INTERVALOINICIAL AS NUMBER) ")
            .append("   AND CAST(RETIRADA.CODIGO AS NUMBER) <= CAST(CAF.INTERVALOFINAL AS NUMBER)) ")
            .append("   AND CAF.TIPOCONTAACOMPANHAMENTO = 'RETIRADA' ");
        return sql;
    }

    protected StringBuilder buscarAndConfiguracaoRecebida() {
        StringBuilder sql = new StringBuilder();
        sql.append("   AND (CAST(RECEBIDA.CODIGO AS NUMBER) >= CAST(CAF.INTERVALOINICIAL AS NUMBER) ")
            .append("   AND CAST(RECEBIDA.CODIGO AS  NUMBER) <= CAST(CAF.INTERVALOFINAL AS NUMBER)) ")
            .append("   AND CAF.TIPOCONTAACOMPANHAMENTO = 'RECEBIDA' ");
        return sql;
    }

    protected StringBuilder buscarFromEstornoTransferenciaConcedida() {
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM EstornoTransferencia estorno ")
            .append(" INNER JOIN TRANSFERENCIACONTAFINANC transferencia ON estorno.TRANSFERENCIA_ID = transferencia.id ")
            .append(" INNER JOIN FONTEDERECURSOS fr ON transferencia.FONTEDERECURSOSRETIRADA_ID = fr.id ")
            .append(" inner join contadedestinacao cd on transferencia.contaDeDestinacaoRetirada_ID = cd.id ")
            .append(" INNER JOIN exercicio ex ON fr.exercicio_id = ex.id ")
            .append(buscarJoinVwUnidade("transferencia.UNIDADEORGCONCEDIDA_ID"))
            .append(" INNER JOIN UNIDADEACOMPAFINANCEIRO conf  ON transferencia.UNIDADEORGCONCEDIDA_ID = conf.UNIDADEORGANIZACIONAL_ID ")
            .append(" INNER JOIN FILTROACOMPAFINANCEIRO filtro  ON conf.id                                    = filtro.UNIDADE_ID ")
            .append("   AND transferencia.tipoTransferenciaFinanceira = filtro.TIPOLIBERACAOFINANCEIRA ")
            .append(" INNER JOIN SUBCONTA RETIRADA  ON RETIRADA.ID = transferencia.SUBCONTARETIRADA_ID ")
            .append(" INNER JOIN CONTAACOMPAFINANCEIRO CAF  ON CAF.UNIDADE_ID                     = CONF.ID ");
        return sql;
    }

    protected StringBuilder buscarFromTransferenciaRecebida() {
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM TRANSFERENCIACONTAFINANC transferencia ")
            .append(" INNER JOIN FONTEDERECURSOS fr ON transferencia.fonteDeRecursosDeposito_ID = fr.id ")
            .append(" inner join contadedestinacao cd on transferencia.contaDeDestinacaoDeposito_ID = cd.id ")
            .append(" INNER JOIN exercicio ex ON fr.exercicio_id = ex.id ")
            .append(buscarJoinVwUnidade("transferencia.unidadeOrganizacional_id"))
            .append(" INNER join UNIDADEACOMPAFINANCEIRO conf on transferencia.unidadeOrganizacional_id = conf.UNIDADEORGANIZACIONAL_ID ")
            .append(" INNER join FILTROACOMPAFINANCEIRO filtro on conf.id = filtro.UNIDADE_ID and transferencia.tipoTransferenciaFinanceira = filtro.TIPOLIBERACAOFINANCEIRA")
            .append(" INNER JOIN SUBCONTA RECEBIDA ON RECEBIDA.ID = TRANSFERENCIA.SUBCONTADEPOSITO_ID ")
            .append(" INNER JOIN CONTAACOMPAFINANCEIRO CAF ON CAF.UNIDADE_ID = CONF.ID ");
        return sql;
    }

    protected StringBuilder buscarFromLiberacaoRetirada() {
        StringBuilder sql = new StringBuilder();
        sql.append(" from liberacaocotafinanceira liberacao ")
            .append(" inner join FONTEDERECURSOS fr on liberacao.FONTEDERECURSOS_id = fr.id ")
            .append(" inner join contadedestinacao cd on liberacao.contaDeDestinacao_id = cd.id ")
            .append(" inner join EVENTOCONTABIL evento on liberacao.eventoContabilRetirada_id = evento.id")
            .append(" inner join exercicio ex on fr.exercicio_id = ex.id ")
            .append(buscarJoinVwUnidade("liberacao.unidadeorganizacional_id"))
            .append(" inner JOIN SUBCONTA RETIRADA ON RETIRADA.ID = LIBERACAO.SUBCONTA_ID ")
            .append(" inner JOIN UNIDADEACOMPAFINANCEIRO conf on liberacao.UNIDADEORGANIZACIONAL_ID = conf.UNIDADEORGANIZACIONAL_ID ")
            .append(" inner join FILTROACOMPAFINANCEIRO filtro on conf.id = filtro.UNIDADE_ID and liberacao.TIPOLIBERACAOFINANCEIRA = filtro.TIPOLIBERACAOFINANCEIRA")
            .append(" inner JOIN CONTAACOMPAFINANCEIRO CAF ON CAF.UNIDADE_ID = CONF.ID ");
        return sql;
    }

    protected StringBuilder buscarFromEstornoTransferenciaRecebida() {
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM EstornoTransferencia estorno ")
            .append(" INNER JOIN TRANSFERENCIACONTAFINANC transferencia ON estorno.TRANSFERENCIA_ID = transferencia.id ")
            .append(" INNER JOIN FONTEDERECURSOS fr ON transferencia.FONTEDERECURSOSRETIRADA_ID = fr.id ")
            .append(" inner join contadedestinacao cd on transferencia.contaDeDestinacaoRetirada_ID = cd.id ")
            .append(" INNER JOIN exercicio ex ON fr.exercicio_id = ex.id ")
            .append(buscarJoinVwUnidade("transferencia.unidadeOrganizacional_id"))
            .append(" inner JOIN UNIDADEACOMPAFINANCEIRO conf  ON transferencia.unidadeOrganizacional_id = conf.UNIDADEORGANIZACIONAL_ID ")
            .append(" INNER JOIN FILTROACOMPAFINANCEIRO filtro  ON conf.id                                    = filtro.UNIDADE_ID ")
            .append("   AND transferencia.tipoTransferenciaFinanceira = filtro.TIPOLIBERACAOFINANCEIRA ")
            .append(" INNER JOIN SUBCONTA RECEBIDA  ON RECEBIDA.ID = transferencia.SUBCONTADEPOSITO_ID ")
            .append(" INNER JOIN CONTAACOMPAFINANCEIRO CAF  ON CAF.UNIDADE_ID                     = CONF.ID ");
        return sql;
    }

    protected StringBuilder buscarFromEstornoLiberacaoRecebida() {
        StringBuilder sql = new StringBuilder();
        sql.append(" from ESTORNOLIBCOTAFINANCEIRA est ")
            .append(" INNER JOIN LIBERACAOCOTAFINANCEIRA liberacao ON EST.LIBERACAO_ID = liberacao.ID ")
            .append(" inner join solicitacaocotafinanceira solicitacao on liberacao.solicitacaocotafinanceira_id = solicitacao.id ")
            .append(" inner join FONTEDERECURSOS fr on solicitacao.FONTEDERECURSOS_id = fr.id ")
            .append(" inner join contadedestinacao cd on solicitacao.contaDeDestinacao_ID = cd.id ")
            .append(" inner join exercicio ex on fr.exercicio_id = ex.id ")
            .append(buscarJoinVwUnidade("solicitacao.unidadeorganizacional_id"))
            .append(" inner JOIN UNIDADEACOMPAFINANCEIRO conf on solicitacao.UNIDADEORGANIZACIONAL_ID = conf.UNIDADEORGANIZACIONAL_ID ")
            .append(" inner join FILTROACOMPAFINANCEIRO filtro on conf.id = filtro.UNIDADE_ID and liberacao.TIPOLIBERACAOFINANCEIRA = filtro.TIPOLIBERACAOFINANCEIRA")
            .append(" inner JOIN SUBCONTA RECEBIDA ON RECEBIDA.ID = SOLICITACAO.CONTAFINANCEIRA_ID ")
            .append(" inner JOIN CONTAACOMPAFINANCEIRO CAF ON CAF.UNIDADE_ID = CONF.ID ");
        return sql;
    }

    protected StringBuilder buscarFromEstornoLiberacaoRetirada() {
        StringBuilder sql = new StringBuilder();
        sql.append(" from ESTORNOLIBCOTAFINANCEIRA est ")
            .append(" INNER JOIN LIBERACAOCOTAFINANCEIRA liberacao ON EST.LIBERACAO_ID = liberacao.ID ")
            .append(" inner join FONTEDERECURSOS fr on liberacao.FONTEDERECURSOS_id = fr.id ")
            .append(" inner join contadedestinacao cd on liberacao.contaDeDestinacao_ID = cd.id ")
            .append(" inner join exercicio ex on fr.exercicio_id = ex.id ")
            .append(buscarJoinVwUnidade("liberacao.unidadeorganizacional_id"))
            .append(" inner JOIN UNIDADEACOMPAFINANCEIRO conf on liberacao.UNIDADEORGANIZACIONAL_ID = conf.UNIDADEORGANIZACIONAL_ID ")
            .append(" inner join FILTROACOMPAFINANCEIRO filtro on conf.id = filtro.UNIDADE_ID and liberacao.TIPOLIBERACAOFINANCEIRA = filtro.TIPOLIBERACAOFINANCEIRA")
            .append(" inner JOIN SUBCONTA RETIRADA ON RETIRADA.ID = LIBERACAO.SUBCONTA_ID ")
            .append(" inner JOIN CONTAACOMPAFINANCEIRO CAF ON CAF.UNIDADE_ID = CONF.ID ");
        return sql;
    }

    protected StringBuilder buscarFromLiberacaoRecebida() {
        StringBuilder sql = new StringBuilder();
        sql.append(" from liberacaocotafinanceira liberacao ")
            .append(" inner join solicitacaocotafinanceira solicitacao on liberacao.solicitacaocotafinanceira_id = solicitacao.id ")
            .append(" inner join FONTEDERECURSOS fr on solicitacao.FONTEDERECURSOS_id = fr.id ")
            .append(" inner join contadedestinacao cd on solicitacao.contaDeDestinacao_ID = cd.id ")
            .append(" inner join EVENTOCONTABIL evento on liberacao.eventoContabilDeposito_id = evento.id")
            .append(" inner join exercicio ex on fr.exercicio_id = ex.id ")
            .append(buscarJoinVwUnidade("solicitacao.unidadeorganizacional_id"))
            .append(" inner JOIN UNIDADEACOMPAFINANCEIRO conf on solicitacao.unidadeorganizacional_id = conf.UNIDADEORGANIZACIONAL_ID ")
            .append(" inner join FILTROACOMPAFINANCEIRO filtro on conf.id = filtro.UNIDADE_ID and liberacao.TIPOLIBERACAOFINANCEIRA = filtro.TIPOLIBERACAOFINANCEIRA")
            .append(" inner JOIN SUBCONTA RECEBIDA ON RECEBIDA.ID = SOLICITACAO.CONTAFINANCEIRA_ID ")
            .append(" inner JOIN CONTAACOMPAFINANCEIRO CAF ON CAF.UNIDADE_ID = CONF.ID ");
        return sql;
    }

    protected StringBuilder buscarFromTransferenciaConcedida() {
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM TRANSFERENCIACONTAFINANC transferencia ")
            .append(" INNER JOIN FONTEDERECURSOS fr ON transferencia.FONTEDERECURSOSRETIRADA_ID = fr.id ")
            .append(" inner join contadedestinacao cd on transferencia.contaDeDestinacaoRetirada_ID = cd.id ")
            .append(" INNER JOIN exercicio ex ON fr.exercicio_id = ex.id ")
            .append(buscarJoinVwUnidade("transferencia.unidadeOrgConcedida_id"))
            .append(" inner JOIN UNIDADEACOMPAFINANCEIRO conf on transferencia.unidadeOrgConcedida_id = conf.UNIDADEORGANIZACIONAL_ID ")
            .append(" inner join FILTROACOMPAFINANCEIRO filtro on conf.id = filtro.UNIDADE_ID and transferencia.tipoTransferenciaFinanceira = filtro.TIPOLIBERACAOFINANCEIRA")
            .append(" inner JOIN SUBCONTA RETIRADA ON RETIRADA.ID = TRANSFERENCIA.SUBCONTARETIRADA_ID ")
            .append(" inner JOIN CONTAACOMPAFINANCEIRO CAF ON CAF.UNIDADE_ID = CONF.ID ");
        return sql;
    }

    protected StringBuilder buscarJoinContaBancariaRetirada() {
        return buscarJoinContaBancaria("RETIRADA");
    }

    protected StringBuilder buscarJoinContaBancariaRecebida() {
        return buscarJoinContaBancaria("RECEBIDA");
    }

    private StringBuilder buscarJoinContaBancaria(String aliasContaBancaria) {
        StringBuilder sql = new StringBuilder();
        sql.append(" inner join contabancariaentidade cbe on ").append(aliasContaBancaria).append(".contabancariaentidade_id = cbe.id ")
            .append(" inner join agencia ag on cbe.agencia_id = ag.id ")
            .append(" inner join banco b on ag.banco_id = b.id ");
        return sql;
    }

    @Override
    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    @Override
    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public UnidadeAcompanhamentoFinanceiroFacade getUnidadeAcompanhamentoFinanceiroFacade() {
        return unidadeAcompanhamentoFinanceiroFacade;
    }
}
