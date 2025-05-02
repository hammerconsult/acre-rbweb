package br.com.webpublico.consultaentidade;

import br.com.webpublico.enums.Operador;
import br.com.webpublico.enums.OperadorLogico;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.DocumentoOficialFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import org.apache.commons.io.IOUtils;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
public class ConsultaEntidadeFacade {
    protected static final Logger logger = LoggerFactory.getLogger(ConsultaEntidadeFacade.class);


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public ConsultaEntidade recuperarConsultaEntidade(String chave) throws IOException, SQLException {
        List<Object[]> result = em.createNativeQuery("select c.chave, c.JSON from CONSULTAENTIDADE c where c.chave = :chave")
            .setParameter("chave", chave)
            .getResultList();
        if (result == null || result.isEmpty()) {
            return null;
        }
        java.sql.Clob clob = (java.sql.Clob) result.get(0)[1];

        return new ObjectMapper().readValue(clob.getSubString(1, (int) clob.length()), ConsultaEntidade.class);
    }

    public List<Object[]> executarConsulta(String sql) {
        Query q = em.createNativeQuery(sql);
        return q.getResultList();
    }

    public void salvarConsultaEntidade(ConsultaEntidade consultaEntidade) throws JsonProcessingException {
        em.createNativeQuery("delete from CONSULTAENTIDADE where chave = :chave")
            .setParameter("chave", consultaEntidade.getChave())
            .executeUpdate();

        String json = new ObjectMapper().writeValueAsString(consultaEntidade);

        em.createNativeQuery("insert into CONSULTAENTIDADE values(:chave, :json)")
            .setParameter("chave", consultaEntidade.getChave())
            .setParameter("json", json)
            .executeUpdate();
    }

    public String trocarTags(ConsultaEntidade consultaEntidade, String from) {
        from = from.replace("$" + TAG.USUARIO_ID.name(), consultaEntidade.getUsuarioCorrente().getId().toString());
        from = from.replace("$" + TAG.EXERCICIO_ID.name(), consultaEntidade.getExercicioCorrente().getId().toString());
        from = from.replace("$" + TAG.EXERCICIO_ANO.name(), consultaEntidade.getExercicioCorrente().getAno().toString());
        from = from.replace("$" + TAG.USUARIO_LOGIN.name(), consultaEntidade.getUsuarioCorrente().getLogin());
        from = from.replace("$" + TAG.UNIDADE_ADM_ID.name(), consultaEntidade.getUnidadeOrganizacionalAdministrativaCorrente().getId().toString());
        from = from.replace("$" + TAG.UNIDADE_ORC_ID.name(), consultaEntidade.getUnidadeOrganizacionalOrcamentariaCorrente().getId().toString());
        from = from.replace("$" + TAG.TIPO_HIERARQUIA_ADM.name(), "'" + TipoHierarquiaOrganizacional.ADMINISTRATIVA.name() + "'");
        from = from.replace("$" + TAG.TIPO_HIERARQUIA_ORC.name(), "'" + TipoHierarquiaOrganizacional.ORCAMENTARIA.name() + "'");
        from = from.replace("$" + TAG.DATA_OPERACAO.name(), "to_date('" + DataUtil.getDataFormatada(consultaEntidade.getDataOperacao()) + "', 'dd/MM/yyyy')");
        return from;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public void consultarEntidades(ConsultaEntidade consulta) {

        StringBuilder sql = new StringBuilder("select ");
        if (consulta.getDistinct()) {
            sql.append(" distinct ");
        }
        StringBuilder ordemByExistente = new StringBuilder();
        StringBuilder ordemBy = new StringBuilder();
        String from = trocarTags(consulta, consulta.getFrom());

        if (consulta.getIdentificador() != null
            && consulta.getIdentificador().getValor() != null && !consulta.getIdentificador().getValor().isEmpty()) {
            sql.append(consulta.getIdentificador().getValor()).append(" as identificador").append(", ");
        }

        sql.append(consulta.getEstiloLinha() != null &&
            !Strings.isNullOrEmpty(consulta.getEstiloLinha().getValor()) ?
            consulta.getEstiloLinha().getValor() : "null ").append(" as estilo").append(", ");

        if (from.contains("order by")) {
            ordemByExistente.append(from.substring(from.indexOf("order by")));
            from = from.substring(0, from.indexOf("order by"));
        }

        for (int i = 0; i < consulta.getTabelaveis().size(); i++) {
            FieldConsultaEntidade tabelavel = consulta.getTabelaveis().get(i);
            String valor = tabelavel.getValor();
            if (valor.contains("$pesquisavel")) {
                int posicaoString = valor.indexOf("$pesquisavel_");
                String posicao = valor.substring(posicaoString + 13, valor.length() < posicaoString + 15 ? posicaoString + 14 : posicaoString + 15);
                posicao = posicao.replaceAll("[^0-9]", "");
                valor = valor.replace("$pesquisavel_" + posicao, consulta.getValorPesquisavelPorOrdem(Integer.valueOf(posicao)) instanceof Date ? ("'" + DataUtil.getDataFormatada((Date) consulta.getValorPesquisavelPorOrdem(Integer.valueOf(posicao))) + "'") : ("'" + consulta.getValorPesquisavelPorOrdem(Integer.valueOf(posicao)) + "'"));
            }
            sql.append(valor).append(" as field_").append(i);
            if (i + 1 != consulta.getTabelaveis().size()) {
                sql.append(",");
            }
            if (tabelavel.getTipoOrdenacao() != null && !TipoCampo.ENUM.equals(tabelavel.getTipo())) {
                ordemBy.append(ordemBy.length() > 0 || from.contains("order by") ? ", " : " order by ")
                    .append(Strings.isNullOrEmpty(tabelavel.getValorOrdenacao()) ? valor : tabelavel.getValorOrdenacao())
                    .append(tabelavel.getTipoOrdenacao().equals(TipoOrdenacao.ASC) ? " asc " : " desc ");
            } else if (tabelavel.getTipoOrdenacao() != null && TipoCampo.ENUM.equals(tabelavel.getTipo())) {
                try {
                    Class enumClass = Class.forName(tabelavel.getTipoEnum());
                    ordemBy.append(ordemBy.length() > 0 || from.contains("order by") ? ", " : " order by ");
                    ordemBy.append(montarOrderByPelaDescricaoDoEnum(enumClass, tabelavel));
                } catch (Exception e) {
                    logger.error("Erro ao tentar recuperar enum [{}]", tabelavel);
                }
            }
        }

        sql.append(" ").append(from);

        StringBuilder where = new StringBuilder();
        where.append(agruparCondicoesPeloOperador(consulta, from));


        if (!ordemBy.toString().contains("order by")) {
            String orderByPadrao = ordemByExistente.length() == 0 ? "order by " + consulta.getIdentificador().getValor() + " desc" : ordemByExistente.toString();
            ordemBy.append(orderByPadrao);
        }

        sql.append(where).append(" ").append(consulta.getGroupBy()).append(" ").append(ordemBy);
        String select, count;

        if (!Strings.isNullOrEmpty(consulta.getGroupBy())) {
            select = "select * from (" + sql + ") dados ";
            count = "select count(" + (consulta.getDistinct() ? "distinct " : "") + " dados.identificador " + ") from (" + sql + ") dados ";
        } else {
            count = "select count(" + (consulta.getDistinct() ? "distinct " : "") + consulta.getIdentificador().getValor() + ") " + from + where;
            select = sql.toString();
        }

        select += consulta.getPaginaAtual() >= 0 ? " offset :offset rows fetch first :limit rows only " : " ";

        Query query = em.createNativeQuery(select);
        Query queryCount = em.createNativeQuery(count);

        for (int i = 0; i < consulta.getFiltros().size(); i++) {
            FiltroConsultaEntidade filtro = consulta.getFiltros().get(i);
            if (filtro.getValor() != null && filtro.getOperacao() != null && !filtro.operadorIsNull() && filtro.getValorParaQuery() != null) {
                if (!Operador.IS_NULL.equals(filtro.getOperacao()) && !Operador.IS_NOT_NULL.equals(filtro.getOperacao())) {
                    query.setParameter("param_" + i, filtro.getValorParaQuery());
                    queryCount.setParameter("param_" + i, filtro.getValorParaQuery());
                }
            }
        }

        if (consulta.getPaginaAtual() >= 0) {
            query.setParameter("offset", consulta.getPaginaAtual() * consulta.getRegistroPorPagina());
            query.setParameter("limit", consulta.getRegistroPorPagina());
        }

        List<Object[]> resultados = query
            .getResultList();

        Number totalRegistros = (Number) queryCount.getSingleResult();
        consulta.setTotalRegistros(totalRegistros.intValue());

        consulta.getResultados().clear();
        consulta.getTotalizadores().clear();

        for (Object[] resultado : resultados) {
            Map<String, Object> objeto = Maps.newHashMap();
            objeto.put("identificador", resultado[0]);
            objeto.put("estiloLinha", resultado[1]);
            for (int i = 0; i < consulta.getTabelaveis().size(); i++) {
                FieldConsultaEntidade tabelavel = consulta.getTabelaveis().get(i);
                int index = i + 2;
                try {
                    if (TipoCampo.ENUM.equals(tabelavel.getTipo()) && !Strings.isNullOrEmpty(tabelavel.getTipoEnum())) {
                        try {
                            Class enumClass = Class.forName(tabelavel.getTipoEnum());
                            Enum anEnum = Enum.valueOf(enumClass, (String) resultado[index]);
                            if (anEnum instanceof EnumComDescricao) {
                                objeto.put(tabelavel.getValor(), ((EnumComDescricao) anEnum).getDescricao());
                            } else {
                                objeto.put(tabelavel.getValor(), anEnum.toString());
                            }
                        } catch (Exception e) {
                            objeto.put(tabelavel.getValor(), "");
                        }
                    } else if (TipoCampo.DATE.equals(tabelavel.getTipo())) {
                        try {
                            objeto.put(tabelavel.getValor(), DataUtil.getDataFormatada((Date) resultado[index]));
                        } catch (Exception e) {
                            defineValorPadrao(objeto, resultado, tabelavel.getValor(), index);
                        }
                    } else if (TipoCampo.DATE_TIME.equals(tabelavel.getTipo())) {
                        try {
                            objeto.put(tabelavel.getValor(), DataUtil.getDataFormatadaDiaHora((Date) resultado[index]));
                        } catch (Exception e) {
                            defineValorPadrao(objeto, resultado, tabelavel.getValor(), index);
                        }
                    } else if (TipoCampo.DECIMAL.equals(tabelavel.getTipo())) {
                        try {
                            objeto.put(tabelavel.getValor(), Util.formatarValor((BigDecimal) resultado[index], tabelavel.getFormato(), false));
                        } catch (Exception e) {
                            defineValorPadrao(objeto, resultado, tabelavel.getValor(), index);
                        }
                    } else if (TipoCampo.MONETARIO.equals(tabelavel.getTipo())) {
                        try {
                            objeto.put(tabelavel.getValor(), Util.formataValor((BigDecimal) resultado[index]));
                        } catch (Exception e) {
                            defineValorPadrao(objeto, resultado, tabelavel.getValor(), index);
                        }
                    } else if (TipoCampo.BOOLEAN.equals(tabelavel.getTipo())) {
                        try {
                            Boolean aBoolean = ((BigDecimal) resultado[index]).equals(BigDecimal.ONE) ? Boolean.TRUE : Boolean.FALSE;
                            objeto.put(tabelavel.getValor(), Util.converterBooleanSimOuNao(aBoolean));
                        } catch (Exception e) {
                            defineValorPadrao(objeto, resultado, tabelavel.getValor(), index);
                        }
                    } else if (TipoCampo.LINK.equals(tabelavel.getTipo())) {
                        objeto.put(tabelavel.getValor(), "<a href='" + resultado[index] + "'>" + resultado[index] + "</a>");
                    } else if (TipoCampo.IMAGEM.equals(tabelavel.getTipo())) {
                        if (resultado[index] != null) {
                            String identificador = "img" + ((BigDecimal) resultado[0]).longValue();
                            Long idArquivo = ((BigDecimal) resultado[index]).longValue();
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            arquivoFacade.recupera(idArquivo, outputStream);
                            String base64 = Util.getBase64Encode(outputStream.toByteArray());
                            String html = "<div style=\"display: flex; flex-direction: column; \"> " +
                                "            <div style=\"display: flex; justify-content: center; padding: 3px;\"> " +
                                "                <a style=\"padding-right: 2px\" onclick=\"aumentarTamanho('#" + identificador + "', 20, 20, 200, 200)\"><i class=\"fa fa-plus-circle fa-lg\"></i></a> " +
                                "                <a onclick=\"diminuirTamanho('#" + identificador + "', 20, 20, 40, 40)\"><i class=\"fa fa-minus-circle fa-lg\"></i></a> " +
                                "            </div> " +
                                "            <div style=\"display: flex; justify-content: center;\"> " +
                                "                <img id=\"" + identificador + "\" width=\"40\" height=\"40\" " +
                                "                     src=\"data:image/png;base64, " + base64 + "\" " +
                                "                     alt=\"\">" +
                                "                </img>" +
                                "            </div>" +
                                "        </div>";
                            objeto.put(tabelavel.getValor(), html);

                        } else {
                            objeto.put(tabelavel.getValor(), "");
                        }
                    } else {
                        defineValorPadrao(objeto, resultado, tabelavel.getValor(), index);
                    }
                    if (tabelavel.getTotalizar()) {
                        if (!consulta.getTotalizadores().containsKey(tabelavel.getValor())) {
                            consulta.getTotalizadores().put(tabelavel.getValor(), BigDecimal.ZERO);
                        }
                        consulta.getTotalizadores().put(tabelavel.getValor(), consulta.getTotalizadores().get(tabelavel.getValor()).add(new BigDecimal(resultado[index].toString())));
                    }
                } catch (Exception e) {
                    logger.error("Erro ao tentar carregar o tabelavel [{}] Erro [{}]", tabelavel, e);
                }
            }
            consulta.getResultados().add(objeto);
        }
    }

    private <T extends Enum<T>> String montarOrderByPelaDescricaoDoEnum(Class<T> enumClass, FieldConsultaEntidade tabelavel) {
        T[] enumConstants = enumClass.getEnumConstants();
        ordenarEnumPelaDescricao(enumConstants, tabelavel.getTipoOrdenacao());

        StringBuilder caseWhen = new StringBuilder();
        String when = " when ";
        caseWhen.append(" case ").append(tabelavel.getValor()).append(when);

        for (int i = 0; i < enumConstants.length; i++) {
            caseWhen.append("'").append(enumConstants[i].name()).append("'").append(" then ").append(i + 1)
                .append(i == (enumConstants.length - 1) ? "" : when);
        }
        caseWhen.append("end");
        return caseWhen.toString();
    }

    private <T> void ordenarEnumPelaDescricao(T[] enumConstants, TipoOrdenacao tipoOrdenacao) {
        final Ordering<Comparable> ordem = TipoOrdenacao.ASC.equals(tipoOrdenacao) ? Ordering.natural() : Ordering.natural().reverse();
        Arrays.sort(enumConstants, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return ComparisonChain.start().compare(
                    (o1 instanceof EnumComDescricao ? ((EnumComDescricao) o1).getDescricao() : o1.toString()),
                    (o2 instanceof EnumComDescricao ? ((EnumComDescricao) o2).getDescricao() : o2.toString()),
                    ordem
                ).result();
            }
        });
    }

    public StringBuilder agruparCondicoesPeloOperador(ConsultaEntidade consulta, String sql) {
        List<Map<OperadorLogico, String>> condicoes = adicionarCondicoes(consulta);

        StringBuilder agrupadorCondicoes = new StringBuilder();
        int indexProximaCondicao = 0;
        for (int i = 0; i < condicoes.size(); i++) {
            Map<OperadorLogico, String> mapCondicao = condicoes.get(i);

            for (Map.Entry<OperadorLogico, String> entryAnd : mapCondicao.entrySet()) {
                if (OperadorLogico.AND.equals(entryAnd.getKey()) || entryAnd.getKey() == null) {
                    indexProximaCondicao++;
                    if (i == 0) {
                        agrupadorCondicoes.append((sql.toLowerCase().contains("where")) ? " and " : " where ");
                    } else {
                        agrupadorCondicoes.append(" and ");
                    }
                    agrupadorCondicoes.append("(");
                    agrupadorCondicoes.append(entryAnd.getValue());
                }

                indexProximaCondicao = montarCondicaoOr(condicoes, agrupadorCondicoes, indexProximaCondicao);
                i = (indexProximaCondicao - 1);

                agrupadorCondicoes.append(")");
            }
        }
        return agrupadorCondicoes;
    }

    private List<Map<OperadorLogico, String>> adicionarCondicoes(ConsultaEntidade consulta) {
        List<Map<OperadorLogico, String>> condicoes = Lists.newLinkedList();
        for (int i = 0; i < consulta.getFiltros().size(); i++) {
            FiltroConsultaEntidade filtro = consulta.getFiltros().get(i);
            if (filtro.getOperacao() != null) {
                String condicao = "";
                String valor = filtro.getCampoParaQuery();
                if (valor.contains("$pesquisavel")) {
                    int posicaoString = valor.indexOf("$pesquisavel_");
                    String posicao = valor.substring(posicaoString + 13, valor.length() < posicaoString + 15 ? posicaoString + 14 : posicaoString + 15);
                    posicao = posicao.replaceAll("[^0-9]", "");
                    valor = valor.replace("$pesquisavel_" + posicao, consulta.getValorPesquisavelPorOrdem(Integer.valueOf(posicao)) instanceof Date ? ("'" + DataUtil.getDataFormatada((Date) consulta.getValorPesquisavelPorOrdem(Integer.valueOf(posicao))) + "'") : ("'" + consulta.getValorPesquisavelPorOrdem(Integer.valueOf(posicao)) + "'"));
                }
                if (filtro.getField().getDinamico()) {
                    condicao += filtro.getField().getValor().replace("${OPERADOR}", filtro.getOperacao().getOperador())
                        .replace("${VALOR_DIGITADO}", (filtro.getOperacao().equals(Operador.IS_NULL) || filtro.getOperacao().equals(Operador.IS_NOT_NULL)
                            ? ""
                            : filtro.operadorIsNull()
                            ? "''"
                            : ":param_" + i));
                } else {
                    if (Operador.DIFERENTE.equals(filtro.getOperacao()) && !filtro.operadorIsNull()) {
                        condicao += ("(" + valor + " " + Operador.IS_NULL.getOperador() + " or ");
                    }
                    condicao += valor + " " + (filtro.operadorIsNull()
                        ? getOperadorIsNull(filtro)
                        : (Operador.IS_NULL.equals(filtro.getOperacao()) || Operador.IS_NOT_NULL.equals(filtro.getOperacao())) && !filtro.operadorIsNull()
                        ? filtro.getOperacao().getOperador()
                        : (filtro.getOperacao().getOperador() + " " + ":param_" + i));
                    if (Operador.DIFERENTE.equals(filtro.getOperacao()) && !filtro.operadorIsNull()) {
                        condicao += ")";
                    }
                }
                Map<OperadorLogico, String> mapCondicao = Maps.newHashMap();
                mapCondicao.put(filtro.getOperadorLogico(), trocarTags(consulta, condicao));
                condicoes.add(mapCondicao);
            }
        }
        return condicoes;
    }

    private int montarCondicaoOr(List<Map<OperadorLogico, String>> condicoes, StringBuilder agrupadorCondicoes, int indexProximaCondicao) {
        if (indexProximaCondicao < condicoes.size()) {
            for (Map.Entry<OperadorLogico, String> entryOr : condicoes.get(indexProximaCondicao).entrySet()) {
                if (OperadorLogico.OR.equals(entryOr.getKey())) {
                    agrupadorCondicoes.append(" or ").append(entryOr.getValue());
                    return montarCondicaoOr(condicoes, agrupadorCondicoes, (indexProximaCondicao + 1));
                }
            }
        }
        return indexProximaCondicao;
    }

    private void defineValorPadrao(Map<String, Object> objeto, Object[] resultado, String valor, int i2) {
        objeto.put(valor, resultado[i2]);
    }

    private String getOperadorIsNull(FiltroConsultaEntidade filtro) {
        if (!Operador.IS_NOT_NULL.equals(filtro.getOperacao()) && !Operador.IS_NULL.equals(filtro.getOperacao())) {
            return Operador.DIFERENTE.equals(filtro.getOperacao()) ? Operador.IS_NOT_NULL.getOperador() : Operador.IS_NULL.getOperador();
        }
        return filtro.getOperacao().getOperador();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public void gerarExcel(ConsultaEntidade consulta, Boolean todosRegistros) {
        ExcelUtil excelUtil = new ExcelUtil();
        excelUtil.setAjustarTamanhoColuna(!todosRegistros);
        try {
            ResultadoConsultaEmLinhas result = montarRelatorioEmLinhas(consulta, todosRegistros, TipoRelatorioDTO.XLS);
            adicionarTotalizadorExcel(consulta, result.valores);
            ReportService.getInstance().porcentagemRelatorio(result.uuidRelatorio, BigDecimal.valueOf(95));
            excelUtil
                .gerarExcel("Relatório de " + consulta.getNomeTela(),
                    "relatorio_" + consulta.getChave(),
                    result.colunas, result.valores, "", consulta.getUsuarioCorrente().getPessoaFisica().toString(),
                    consulta.getDataOperacao());
            byte[] conteudo = null;
            if (excelUtil.getFile() != null) {
                conteudo = IOUtils.toByteArray(excelUtil.fileDownload().getStream());
            }
            Thread.sleep(3000);
            ReportService.getInstance().finalizarRelatorio(result.uuidRelatorio, consulta.getUsuarioCorrente(), conteudo);
        } catch (Exception e) {
            logger.error("Erro ao gerar Excel. {}", e);
        }
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public void gerarCSV(ConsultaEntidade consulta, Boolean todosRegistros) {
        ExcelUtil excelUtil = new ExcelUtil();
        try {
            ResultadoConsultaEmLinhas result = montarRelatorioEmLinhas(consulta, todosRegistros, TipoRelatorioDTO.CSV);
            adicionarTotalizadorExcel(consulta, result.valores);
            ReportService.getInstance().porcentagemRelatorio(result.uuidRelatorio, BigDecimal.valueOf(95));
            excelUtil
                .gerarCSV("Relatório de " + consulta.getNomeTela(),
                    "relatorio_" + consulta.getChave(),
                    result.colunas, result.valores, false);
            byte[] conteudo = null;
            if (excelUtil.getFile() != null) {
                conteudo = IOUtils.toByteArray(excelUtil.fileDownload().getStream());
            }
            Thread.sleep(3000);
            ReportService.getInstance().finalizarRelatorio(result.uuidRelatorio, consulta.getUsuarioCorrente(), conteudo);
        } catch (Exception e) {
            logger.error("Erro ao gerar Excel. {}", e);
        }
    }

    private ResultadoConsultaEmLinhas montarRelatorioEmLinhas(ConsultaEntidade consulta, Boolean todosRegistros, TipoRelatorioDTO tipo) {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setTipoRelatorio(tipo);
        dto.setNomeRelatorio("Exportaçao de " + consulta.getNomeTela());
        String uuidRelatorio = ReportService.getInstance().addRelatorio(consulta.getUsuarioCorrente(), dto);
        ReportService.getInstance().porcentagemRelatorio(uuidRelatorio, BigDecimal.valueOf(10));
        List<String> colunas = Lists.newArrayList();
        List<Object[]> valores = Lists.newArrayList();
        List<Map<String, Object>> resultados;
        if (todosRegistros) {
            ConsultaEntidade consultaEntidadeTodos = (ConsultaEntidade) Util.clonarObjeto(consulta);
            consultaEntidadeTodos.setPaginaAtual(-1);
            consultarEntidades(consultaEntidadeTodos);
            resultados = consultaEntidadeTodos.getResultados();
        } else {
            consultarEntidades(consulta);
            resultados = consulta.getResultados();
        }
        ReportService.getInstance().porcentagemRelatorio(uuidRelatorio, BigDecimal.valueOf(60));
        for (FieldConsultaEntidade tabelavel : consulta.getTabelaveis()) {
            colunas.add(tabelavel.getDescricao());
        }
        for (Map<String, Object> resultado : resultados) {
            Object[] obj = new Object[resultado.size()];
            int i = 0;
            for (FieldConsultaEntidade tabelavel : consulta.getTabelaveis()) {
                obj[i] = resultado.get(tabelavel.getValor());
                i++;
            }
            valores.add(obj);
        }
        return new ResultadoConsultaEmLinhas(uuidRelatorio, colunas, valores);
    }

    private static class ResultadoConsultaEmLinhas {
        public final String uuidRelatorio;
        public final List<String> colunas;
        public final List<Object[]> valores;

        public ResultadoConsultaEmLinhas(String uuidRelatorio, List<String> colunas, List<Object[]> valores) {
            this.uuidRelatorio = uuidRelatorio;
            this.colunas = colunas;
            this.valores = valores;
        }
    }

    private void adicionarTotalizadorExcel(ConsultaEntidade consulta, List<Object[]> valores) {
        Object[] obj = new Object[consulta.getTabelaveis().size()];
        int i = 0;
        for (FieldConsultaEntidade tabelavel : consulta.getTabelaveis()) {
            obj[i] = consulta.getTotalizadores().containsKey(tabelavel.getValor()) ?
                "Total - " + tabelavel.getDescricao() :
                "";
            i++;
        }
        valores.add(obj);
        obj = new Object[consulta.getTabelaveis().size()];
        i = 0;
        for (FieldConsultaEntidade tabelavel : consulta.getTabelaveis()) {
            obj[i] = consulta.getTotalizadores().containsKey(tabelavel.getValor()) ?
                Util.formataValor(consulta.getTotalizadores().get(tabelavel.getValor())) :
                "";
            i++;
        }
        valores.add(obj);
    }
}
