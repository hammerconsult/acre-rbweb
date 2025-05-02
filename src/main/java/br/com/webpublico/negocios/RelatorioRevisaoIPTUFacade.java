package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioImoveisComRevisaoIptu;
import br.com.webpublico.entidadesauxiliares.RelatorioRevisaoIPTU;
import br.com.webpublico.enums.SituacaoLoteBaixa;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoImovel;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class RelatorioRevisaoIPTUFacade {
    private final Logger logger = LoggerFactory.getLogger(RelatorioRevisaoIPTUFacade.class);
    @EJB
    private SistemaFacade sistemaFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 10)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<ByteArrayOutputStream> gerarRelatorio(List<RelatorioRevisaoIPTU> dados, String caminhoImagem, String municipio, String caminhoSubReport, String usuarioCorrente, String filtroUtilizados, Boolean detalhado, UnidadeOrganizacional unidadeOrganizacional) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            String arquivoJasper = "RelatorioRevisaoIPTU.jasper";
            AbstractReport report = AbstractReport.getAbstractReport();
            HashMap parameters = new HashMap();
            parameters.put("MODULO", "Tributário");
            parameters.put("MUNICIPIO", municipio);
            parameters.put("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS E DESENVOLVIMENTO ECONÔMICO");
            parameters.put("NOMERELATORIO", "RELATÓRIO DE IMÓVEIS COM REVISÃO DE IPTU");
            parameters.put("BRASAO", caminhoImagem);
            parameters.put("USUARIO", usuarioCorrente);
            parameters.put("FILTROS", filtroUtilizados);
            parameters.put("DETALHADO", detalhado);
            JasperPrint jasperPrint = report.gerarBytesDeRelatorioComDadosEmCollectionView(caminhoSubReport, arquivoJasper, parameters, new JRBeanCollectionDataSource(dados), unidadeOrganizacional);
            byteArrayOutputStream = report.exportarJasperParaPDF(jasperPrint);
        } catch (ValidacaoException vl) {
            FacesUtil.printAllFacesMessages(vl.getMensagens());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new AsyncResult<>(byteArrayOutputStream);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<List<RelatorioRevisaoIPTU>> buscarRevisoesCalculoIPTU(List<BigDecimal> idsParte, FiltroRelatorioImoveisComRevisaoIptu filtroRelatorio, AssistenteBarraProgresso assistenteBarraProgresso) {
        List<RelatorioRevisaoIPTU> retorno = new ArrayList<>();
        List<List<BigDecimal>> partition = Lists.partition(idsParte, idsParte.size() > 100 ? idsParte.size() / 10 : idsParte.size());
        for (List<BigDecimal> loteIds : partition) {
            if (filtroRelatorio.getCancelarProcesso()) {
                return new AsyncResult<>(retorno);
            }
            StringBuilder sql = new StringBuilder();
            sql.append(getSql(filtroRelatorio));
            sql.append(getCondicaoBci(loteIds));
            sql.append(getCondicaoFiltros(filtroRelatorio));
            sql.append(getMontarCondicaoDataPagamento(filtroRelatorio));
            sql.append(getSqlOrdenacao());
            try {
                Query q = em.createNativeQuery(sql.toString());
                List<Object[]> lista = q.getResultList();
                retorno.addAll(RelatorioRevisaoIPTU.preencherDados(lista));
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
            assistenteBarraProgresso.contar(loteIds.size());
        }
        return new AsyncResult<>(retorno);
    }

    public List<BigDecimal> buscarBciPorFiltros(FiltroRelatorioImoveisComRevisaoIptu filtro) {
        String sql =
            " SELECT distinct ci.id " +
                "  FROM  CADASTROIMOBILIARIO CI " +
                " INNER JOIN CALCULOIPTU IPTU ON IPTU.CADASTROIMOBILIARIO_ID = CI.ID" +
                " INNER JOIN CALCULO CALCULO ON CALCULO.ID = IPTU.ID" +
                " INNER JOIN PROCESSOCALCULO PROCESSO ON PROCESSO.ID = IPTU.PROCESSOCALCULOIPTU_ID" +
                " INNER JOIN VALORDIVIDA VD ON VD.CALCULO_ID = IPTU.ID" +
                " INNER JOIN EXERCICIO EX ON EX.ID = VD.EXERCICIO_ID";
        sql += filtro.getBairro() != null ||
            filtro.getLogradouro() != null ||
            (filtro.getSetorInicial() != null && !filtro.getSetorInicial().isEmpty()) ||
            (filtro.getSetorFinal() != null && !filtro.getSetorFinal().isEmpty()) ?
            " INNER JOIN VWENDERECOBCI ENDERECO ON ENDERECO.CADASTROIMOBILIARIO_ID = CI.ID " : "";
        String juncao = " Where ";
        if (filtro.getBairro() != null) {
            sql += juncao + " ENDERECO.ID_BAIRRO = :bairro";
            juncao = " and ";
        }
        if (filtro.getLogradouro() != null) {
            sql += juncao + " ENDERECO.ID_LOGRADOURO = :logradouro";
            juncao = " and ";
        }
        if (filtro.getSetorInicial() != null && !filtro.getSetorInicial().trim().isEmpty()) {
            sql += juncao + " to_number(ENDERECO.SETOR) >= :setorInicial";
            juncao = " and ";
        }
        if (filtro.getSetorFinal() != null && !filtro.getSetorFinal().trim().isEmpty()) {
            sql += juncao + " to_number(ENDERECO.SETOR) <= :setorFinal";
            juncao = " and ";
        }
        if (filtro.getQuadraInicial() != null && !filtro.getQuadraInicial().trim().isEmpty()) {
            sql += juncao + " to_number(ENDERECO.QUADRA) >= :quadraInicial";
            juncao = " and ";
        }
        if (filtro.getQuadraFinal() != null && !filtro.getQuadraFinal().trim().isEmpty()) {
            sql += juncao + " to_number(ENDERECO.QUADRA) <= :quadraFinal";
            juncao = " and ";
        }
        if (filtro.getCadastroInicial() != null && !filtro.getCadastroInicial().trim().isEmpty()) {
            sql += juncao + " CI.INSCRICAOCADASTRAL >= :cadastroInicial ";
            juncao = " and ";
        }
        if (filtro.getCadastroFinal() != null && !filtro.getCadastroFinal().trim().isEmpty()) {
            sql += juncao + " CI.INSCRICAOCADASTRAL <= :cadastroFinal";
            juncao = " and ";
        }
        if (filtro.getExercicioInicial() != null) {
            sql += juncao + " EX.ANO >= :exercicioInicial";
            juncao = " and ";
        }
        if (filtro.getExercicioFinal() != null) {
            sql += juncao + " EX.ANO <= :exercicioFinal";
            juncao = " and ";
        }
        if (filtro.getValorInicial() != null) {
            sql += juncao + " calculo.valorefetivo >= :valorInicial";
            juncao = " and ";
        }
        if (filtro.getValorFinal() != null) {
            sql += juncao + " calculo.valorefetivo <= :valorFinal";
            juncao = " and ";
        }
        if (filtro.getProtocolo() != null && !filtro.getProtocolo().isEmpty()) {
            sql += juncao + " trim(processo.numeroprotocolo) = :protocolo";
            juncao = " and ";
        }
        if (filtro.getDataLancamentoInicial() != null) {
            sql += juncao + " to_date(to_char(PROCESSO.DATALANCAMENTO, 'dd/MM/yyyy'), 'dd/MM/yyyy') >= to_date(:dataInicial, 'dd/MM/yyyy') ";
            juncao = " and ";
        }
        if (filtro.getDataLancamentoFinal() != null) {
            sql += juncao + " to_date(to_char(PROCESSO.DATALANCAMENTO, 'dd/MM/yyyy'), 'dd/MM/yyyy') <= to_date(:dataFinal, 'dd/MM/yyyy') ";
        }
        Query q = em.createNativeQuery(sql);
        if (filtro.getBairro() != null) {
            q.setParameter("bairro", filtro.getBairro().getId());
        }
        if (filtro.getLogradouro() != null) {
            q.setParameter("logradouro", filtro.getLogradouro().getId());
        }
        if (filtro.getSetorInicial() != null && !filtro.getSetorInicial().trim().isEmpty()) {
            q.setParameter("setorInicial", Integer.parseInt(filtro.getSetorInicial().trim()));
        }
        if (filtro.getSetorFinal() != null && !filtro.getSetorFinal().trim().isEmpty()) {
            q.setParameter("setorFinal", Integer.parseInt(filtro.getSetorFinal().trim()));
        }
        if (filtro.getQuadraInicial() != null && !filtro.getQuadraInicial().trim().isEmpty()) {
            q.setParameter("quadraInicial", Integer.parseInt(filtro.getQuadraInicial().trim()));
        }
        if (filtro.getQuadraFinal() != null && !filtro.getQuadraFinal().trim().isEmpty()) {
            q.setParameter("quadraFinal", Integer.parseInt(filtro.getQuadraFinal().trim()));
        }
        if (filtro.getCadastroInicial() != null && !filtro.getCadastroInicial().trim().isEmpty()) {
            q.setParameter("cadastroInicial", filtro.getCadastroInicial().trim());
        }
        if (filtro.getCadastroFinal() != null && !filtro.getCadastroFinal().trim().isEmpty()) {
            q.setParameter("cadastroFinal", filtro.getCadastroFinal().trim());
        }
        if (filtro.getExercicioInicial() != null) {
            q.setParameter("exercicioInicial", filtro.getExercicioInicial().getAno());
        }
        if (filtro.getExercicioFinal() != null) {
            q.setParameter("exercicioFinal", filtro.getExercicioFinal().getAno());
        }
        if (filtro.getDataLancamentoInicial() != null) {
            q.setParameter("dataInicial", DataUtil.getDataFormatada(filtro.getDataLancamentoInicial()));
        }
        if (filtro.getDataLancamentoFinal() != null) {
            q.setParameter("dataFinal", DataUtil.getDataFormatada(filtro.getDataLancamentoFinal()));
        }
        if (filtro.getValorInicial() != null) {
            q.setParameter("valorInicial", filtro.getValorInicial());
        }
        if (filtro.getValorFinal() != null) {
            q.setParameter("valorFinal", filtro.getValorFinal());
        }
        if (filtro.getProtocolo() != null && !filtro.getProtocolo().isEmpty()) {
            q.setParameter("protocolo", filtro.getProtocolo().trim());
        }
        return q.getResultList();
    }

    public String getCondicaoBci(List<BigDecimal> ids) {
        StringBuilder toReturn = new StringBuilder();
        if (ids != null && !ids.isEmpty()) {
            String juncao = " and ( ci.id  in (";
            Integer count = 0;
            for (BigDecimal id : ids) {
                toReturn.append(juncao);
                toReturn.append(id);
                juncao = ", ";
                count++;
                if (count == 1000) {
                    juncao = ") or ci.id in (";
                    count = 0;
                }
            }
            if (toReturn.length() > 0) {
                toReturn.append(")) ");
            }
        }
        return toReturn.toString();
    }

    public String getCondicaoFiltros(FiltroRelatorioImoveisComRevisaoIptu filtro) {
        String condicao = "";
        if (filtro.getExercicioInicial() != null) {
            condicao += " AND EX.ANO >= " + filtro.getExercicioInicial().getAno();
        }
        if (filtro.getExercicioFinal() != null) {
            condicao += " AND EX.ANO <= " + filtro.getExercicioFinal().getAno();
        }
        if (filtro.getProcessoCalculoIPTU() != null) {
            condicao += " AND PROCESSO.ID = " + filtro.getProcessoCalculoIPTU().getId();
        }

        if (filtro.getTipoImovel() != null) {
            if (TipoImovel.PREDIAL.equals(filtro.getTipoImovel())) {
                condicao += " and exists (select 1 from memoriacalculoiptu m where m.calculoiptu_id = IPTU.id and m.construcao_id is not null) ";
            } else {
                condicao += " and exists (select 1 from memoriacalculoiptu m where m.calculoiptu_id = IPTU.id and m.construcao_id is null) ";
            }
        }

        if (filtro.getProprietario() != null) {
            condicao += " and PESSOAPROP.ID = " + filtro.getProprietario().getId();
        }

        if (filtro.getCompromissario() != null) {
            condicao += " and PESSOACOMP.ID = " + filtro.getCompromissario().getId();
        }

        if (filtro.getDataLancamentoInicial() != null) {
            condicao += " and to_date(to_char(PROCESSO.DATALANCAMENTO, 'dd/MM/yyyy'), 'dd/MM/yyyy') >= to_date('" + DataUtil.getDataFormatada(filtro.getDataLancamentoInicial()) + "', 'dd/MM/yyyy') ";
        }

        if (filtro.getDataLancamentoFinal() != null) {
            condicao += " and to_date(to_char(PROCESSO.DATALANCAMENTO, 'dd/MM/yyyy'), 'dd/MM/yyyy') <= to_date('" + DataUtil.getDataFormatada(filtro.getDataLancamentoFinal()) + "', 'dd/MM/yyyy') ";
        }

        if (filtro.getDataVencimentoInicial() != null) {
            condicao += " and to_date(to_char(PARCELA.VENCIMENTO, 'dd/MM/yyyy'), 'dd/MM/yyyy') >= to_date('" + DataUtil.getDataFormatada(filtro.getDataVencimentoInicial()) + "', 'dd/MM/yyyy') ";
        }

        if (filtro.getDataVencimentoFinal() != null) {
            condicao += " and to_date(to_date(PARCELA.VENCIMENTO, 'dd/MM/yyyy'), 'dd/MM/yyyy') <= to_date('" + DataUtil.getDataFormatada(filtro.getDataVencimentoFinal()) + "', 'dd/MM/yyyy') ";
        }

        return condicao;
    }

    public String getSql(FiltroRelatorioImoveisComRevisaoIptu filtro) {
        String sql =
            " SELECT    PROCESSO.NUMEROPROTOCOLO, " +
                "       PROCESSO.ANOPROTOCOLO, " +
                "       COALESCE(PFPROP.CPF, PJPROP.CNPJ) AS CPFCNPJ, " +
                "       COALESCE(PFPROP.NOME, PJPROP.RAZAOSOCIAL) AS NOME, " +
                "       CI.INSCRICAOCADASTRAL, " +
                "       TIPOLOGRADOURO.SIGLA ||' - ' ||LOGRADOURO.NOME ||' N°: ' || CI.NUMERO AS LOGRADOURO, " +
                "       CI.NUMERO AS NUMEROENDERECO, " +
                "       BAIRRO.DESCRICAO BAIRRO, " +
                "       PROCESSO.DATALANCAMENTO, " +
                " " +
                "  (SELECT SUM(IT.VALOR) " +
                "   FROM ITEMVALORDIVIDA IT " +
                "   INNER JOIN TRIBUTO TR ON TR.ID = IT.TRIBUTO_ID " +
                "   AND TR.TIPOTRIBUTO = 'IMPOSTO' " +
                "   WHERE IT.VALORDIVIDA_ID = VD.ID) AS IMPOSTO, " +
                " " +
                "  (SELECT SUM(IT.VALOR) " +
                "   FROM ITEMVALORDIVIDA IT " +
                "   INNER JOIN TRIBUTO TR ON TR.ID = IT.TRIBUTO_ID " +
                "   AND TR.TIPOTRIBUTO = 'TAXA' " +
                "   WHERE IT.VALORDIVIDA_ID = VD.ID) AS TAXA, " +
                "       CALCULO.VALOREFETIVO, " +
                "       COALESCE((SELECT SUM(CASE " +
                "                            WHEN DESCONTO.TIPO = 'PERCENTUAL' THEN COALESCE(ROUND(ITEM.VALOR * (DESCONTO.DESCONTO / 100),2),0) " +
                "                            WHEN DESCONTO.TIPO = 'VALOR' THEN COALESCE(DESCONTO.DESCONTO,0) " +
                "                            END) AS VALOR " +
                "                   FROM DESCONTOITEMPARCELA DESCONTO " +
                "                   INNER JOIN ITEMPARCELAVALORDIVIDA ITEM ON ITEM.ID = DESCONTO.ITEMPARCELAVALORDIVIDA_ID " +
                "                   INNER JOIN PARCELAVALORDIVIDA PVD ON PVD.ID = ITEM.PARCELAVALORDIVIDA_ID " +
                "                   INNER JOIN OPCAOPAGAMENTO OP ON OP.ID = PVD.OPCAOPAGAMENTO_ID " +
                "                   AND OP.PROMOCIONAL = 1 " +
                "                   INNER JOIN ITEMVALORDIVIDA IVD ON IVD.ID = ITEM.ITEMVALORDIVIDA_ID " +
                "                   WHERE IVD.VALORDIVIDA_ID = VD.ID " +
                "                     AND DESCONTO.DESCONTO > 0 ),0) AS DESCONTO, " +
                "  (SELECT PVD.VENCIMENTO " +
                "   FROM PARCELAVALORDIVIDA PVD " +
                "   INNER JOIN OPCAOPAGAMENTO OP ON OP.ID = PVD.OPCAOPAGAMENTO_ID " +
                "   AND OP.PROMOCIONAL = 1 " +
                "   WHERE PVD.VALORDIVIDA_ID = VD.ID) AS VENCIMENTO_CU," +
                "   IPTU.ID, " +
                "   CASE WHEN (SELECT COUNT(pvd.id) FROM PARCELAVALORDIVIDA PVD " +
                "               INNER JOIN SITUACAOPARCELAVALORDIVIDA SPVD ON SPVD.ID = PVD.SITUACAOATUAL_ID " +
                "               WHERE PVD.VALORDIVIDA_ID = VD.ID " +
                "                 AND SPVD.SITUACAOPARCELA = 'CANCELADO_RECALCULO') > 1 then 0 else 1 END AS ATUAL " +
                " FROM CALCULOIPTU IPTU " +
                " INNER JOIN CALCULO CALCULO ON CALCULO.ID = IPTU.ID " +
                " INNER JOIN PROCESSOCALCULO PROCESSO ON PROCESSO.ID = IPTU.PROCESSOCALCULOIPTU_ID " +
                " INNER JOIN CADASTROIMOBILIARIO CI ON CI.ID = IPTU.CADASTROIMOBILIARIO_ID " +
                " INNER JOIN VALORDIVIDA VD ON VD.CALCULO_ID = IPTU.ID " +
                " INNER JOIN EXERCICIO EX ON EX.ID = VD.EXERCICIO_ID " +
                " INNER JOIN PROPRIEDADE ON (PROPRIEDADE.IMOVEL_ID = CI.ID " +
                "                           AND PROPRIEDADE.ID = " +
                "                             (SELECT MAX(ID) " +
                "                              FROM PROPRIEDADE " +
                "                              WHERE IMOVEL_ID = CI.ID " +
                "                                AND FINALVIGENCIA IS NULL)) " +
                " INNER JOIN PESSOA PESSOAPROP ON PESSOAPROP.ID = PROPRIEDADE.PESSOA_ID " +
                " LEFT  JOIN PESSOAFISICA PFPROP ON PFPROP.ID = PESSOAPROP.ID " +
                " LEFT JOIN PESSOAJURIDICA PJPROP ON PJPROP.ID = PESSOAPROP.ID " +
                " INNER JOIN LOTE ON LOTE.ID = CI.LOTE_ID " +
                " INNER JOIN SETOR ON SETOR.ID = LOTE.SETOR_ID ";
        sql += filtro.getCompromissario() != null ? " LEFT JOIN COMPROMISSARIO ON COMPROMISSARIO.CADASTROIMOBILIARIO_ID = CI.ID AND COMPROMISSARIO.FIMVIGENCIA IS NULL " : "";
        sql += filtro.getCompromissario() != null ? " LEFT JOIN PESSOA PESSOACOMP ON PESSOACOMP.ID = COMPROMISSARIO.COMPROMISSARIO_ID " : "";
        sql += " LEFT JOIN TESTADA ON TESTADA.LOTE_ID = CI.LOTE_ID " +
            " LEFT JOIN FACE ON TESTADA.FACE_ID = FACE.ID " +
            " LEFT JOIN LOGRADOUROBAIRRO ON FACE.LOGRADOUROBAIRRO_ID = LOGRADOUROBAIRRO.ID " +
            " LEFT JOIN BAIRRO ON BAIRRO.ID = LOGRADOUROBAIRRO.BAIRRO_ID " +
            " LEFT JOIN LOGRADOURO ON LOGRADOURO.ID = LOGRADOUROBAIRRO.LOGRADOURO_ID " +
            " LEFT JOIN TIPOLOGRADOURO ON TIPOLOGRADOURO.ID = LOGRADOURO.TIPOLOGRADOURO_ID " +
            " WHERE (TESTADA.ID = COALESCE((SELECT MAX(S_TESTADA.ID) " +
            "                                FROM TESTADA S_TESTADA " +
            "                                WHERE S_TESTADA.LOTE_ID = CI.LOTE_ID " +
            "                                  AND S_TESTADA.PRINCIPAL = 1 ), " +
            "                               (SELECT MAX(S_TESTADA.ID) " +
            "                                FROM TESTADA S_TESTADA " +
            "                                WHERE S_TESTADA.LOTE_ID = CI.LOTE_ID )) OR TESTADA.ID IS NULL) ";
        sql += filtro.getCompromissario() != null ? " AND (COMPROMISSARIO.ID = " +
            "         (SELECT MAX(COMP.ID) " +
            "          FROM COMPROMISSARIO COMP " +
            "          WHERE COMP.CADASTROIMOBILIARIO_ID = CI.ID " +
            "            AND COMP.FIMVIGENCIA IS NULL) " +
            "       OR NOT EXISTS " +
            "         (SELECT COMP.ID " +
            "          FROM COMPROMISSARIO COMP " +
            "          WHERE COMP.CADASTROIMOBILIARIO_ID = CI.ID " +
            "            AND COMP.FIMVIGENCIA IS NULL)) " : "";
        sql += filtro.getSituacoes() != null && filtro.getSituacoes().length > 0 ?
            "     and exists( select 1 from PARCELAVALORDIVIDA PARCELA " +
            "              INNER JOIN SITUACAOPARCELAVALORDIVIDA SITUACAO ON SITUACAO.ID = PARCELA.SITUACAOATUAL_ID " +
            "              where PARCELA.VALORDIVIDA_ID = VD.ID" +
            "                AND SITUACAO.SITUACAOPARCELA IN (" + montarSituacoes(filtro) + ")) " : "";
        return sql;
    }

    public String getSqlOrdenacao() {
        return " ORDER BY PROCESSO.DATALANCAMENTO, PROCESSO.ID";
    }

    public String montarSituacoes(FiltroRelatorioImoveisComRevisaoIptu filtro) {
        String retorno = "";
        for (SituacaoParcela sit : filtro.getSituacoes()) {
            retorno += "'" + sit.name() + "', ";
        }
        return retorno.substring(0, retorno.length() - 2);
    }

    public String getMontarCondicaoDataPagamento(FiltroRelatorioImoveisComRevisaoIptu filtro) {
        String retorno = "";
        if (filtro.getDataPagamentoInicial() != null && filtro.getDataPagamentoFinal() != null) {
            retorno = " AND EXISTS  (SELECT 1 " +
                "      FROM LOTEBAIXA LB " +
                "     INNER JOIN ITEMLOTEBAIXA ILB ON ILB.LOTEBAIXA_ID = LB.ID " +
                "     INNER JOIN ITEMLOTEBAIXAPARCELA ILBP ON ILBP.ITEMLOTEBAIXA_ID = ILB.ID " +
                "     INNER JOIN ITEMDAM IDAM ON IDAM.ID = ILBP.ITEMDAM_ID" +
                "     INNER JOIN PARCELAVALORDIVIDA PVD ON PVD.ID = IDAM.PARCELA_ID" +
                "   WHERE PVD.VALORDIVIDA_ID = VD.ID " +
                "     AND LB.SITUACAOLOTEBAIXA IN ('" + SituacaoLoteBaixa.BAIXADO.name() + "', '" + SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name() + "')" +
                "     AND LB.DATAPAGAMENTO BETWEEN TO_DATE('" + Util.dateToString(filtro.getDataPagamentoInicial()) + "' , 'dd/MM/yyyy') " +
                "                          AND     TO_DATE('" + Util.dateToString(filtro.getDataPagamentoFinal()) + "' , 'dd/MM/yyyy') ) ";
        } else if (filtro.getDataPagamentoInicial() != null) {
            retorno = " AND EXISTS (SELECT 1 " +
                "      FROM LOTEBAIXA LB " +
                "     INNER JOIN ITEMLOTEBAIXA ILB ON ILB.LOTEBAIXA_ID = LB.ID " +
                "     INNER JOIN ITEMLOTEBAIXAPARCELA ILBP ON ILBP.ITEMLOTEBAIXA_ID = ILB.ID " +
                "     INNER JOIN ITEMDAM IDAM ON IDAM.ID = ILBP.ITEMDAM_ID " +
                "     INNER JOIN PARCELAVALORDIVIDA PVD ON PVD.ID = IDAM.PARCELA_ID" +
                "   WHERE PVD.VALORDIVIDA_ID = VD.ID " +
                "     AND LB.SITUACAOLOTEBAIXA IN ('" + SituacaoLoteBaixa.BAIXADO.name() + "', '" + SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name() + "')" +
                "     AND LB.DATAPAGAMENTO >= TO_DATE('" +
                Util.dateToString(filtro.getDataPagamentoInicial()) +
                "' , 'dd/MM/yyyy')) ";
        } else if (filtro.getDataPagamentoFinal() != null) {
            retorno = " AND EXISTS  (SELECT 1 " +
                "      FROM LOTEBAIXA LB " +
                "     INNER JOIN ITEMLOTEBAIXA ILB ON ILB.LOTEBAIXA_ID = LB.ID " +
                "     INNER JOIN ITEMLOTEBAIXAPARCELA ILBP ON ILBP.ITEMLOTEBAIXA_ID = ILB.ID " +
                "     INNER JOIN ITEMDAM IDAM ON IDAM.ID = ILBP.ITEMDAM_ID " +
                "     INNER JOIN PARCELAVALORDIVIDA PVD ON PVD.ID = IDAM.PARCELA_ID" +
                "   WHERE PVD.VALORDIVIDA_ID = VD.ID" +
                "     and lb.situacaolotebaixa in ('" + SituacaoLoteBaixa.BAIXADO.name() + "', '" + SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name() + "')" +
                "     and lb.datapagamento <= to_date('" +
                Util.dateToString(filtro.getDataPagamentoFinal()) +
                "' , 'dd/MM/yyyy')) ";
        }

        return retorno;
    }
}

