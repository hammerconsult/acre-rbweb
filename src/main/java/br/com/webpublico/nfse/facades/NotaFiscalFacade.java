package br.com.webpublico.nfse.facades;


import br.com.webpublico.BigDecimalUtils;
import br.com.webpublico.consultaentidade.FiltroConsultaEntidade;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.comum.UsuarioWebFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcNotaFiscalDao;
import br.com.webpublico.negocios.tributario.rowmapper.NotaFiscalRowMapper;
import br.com.webpublico.nfse.domain.*;
import br.com.webpublico.nfse.domain.dtos.Operador;
import br.com.webpublico.nfse.domain.dtos.*;
import br.com.webpublico.nfse.domain.dtos.enums.SituacaoDeclaracaoNfseDTO;
import br.com.webpublico.nfse.enums.*;
import br.com.webpublico.nfse.util.GeradorQuery;
import br.com.webpublico.singletons.CacheTributario;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.joda.time.LocalDate;
import org.springframework.web.client.RestTemplate;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


@Stateless
public class NotaFiscalFacade extends AbstractFacade<NotaFiscal> {


    private final String SQL_BASE_NOTA = " from NotaFiscal nota " +
        " inner join DeclaracaoPrestacaoServico  dec on dec.id = nota.declaracaoPrestacaoServico_id " +
        " left join CANCDECLAPRESTACAOSERVICO ca on dec.cancelamento_id = ca.id " +
        " left join DadosPessoaisNfse dadosPessoais on dadosPessoais.id = dec.dadosPessoaisTomador_id " +
        " left join Rps rps on rps.id = nota.rps_id " +
        " where nota.prestador_id = :empresaId ";
    private final String CAMPOS_SQL_RELATORIO_NOTA = " select " +
        "       nota.id, " +
        "       nota.numero, " +
        "       nota.emissao, " +
        "       rps.numero as numero_rps, " +
        "       lote_rps.numero as numero_lote_rps, " +
        "       lote_rps.datarecebimento as data_emissao_lote_rps, " +
        "       dpp.nomerazaosocial as prestador_nome, " +
        "       dpp.cpfcnpj as prestador_cpfcnpj, " +
        "       dpt.nomerazaosocial as tomador_nome, " +
        "       dpt.cpfcnpj as tomador_cpfcnpj, " +
        "       dec.situacao, " +
        "       dec.modalidade, " +
        "       nota.descriminacaoservico, " +
        "       dec.issretido, " +
        "       dec.totalservicos, " +
        "       dec.deducoeslegais as deducoes, " +
        "       dec.descontosincondicionais + dec.descontoscondicionais as descontos, " +
        "       dec.retencoesfederais as retencoes, " +
        "       dec.basecalculo, " +
        "       ids.quantidade, " +
        "       ids.aliquotaservico, " +
        "       dec.isscalculado, " +
        "       dec.isspagar, " +
        "       dec.naturezaoperacao, " +
        "       dec.competencia, " +
        "       ce.classificacaoatividade, " +
        "       s.codigo as codigo_servico, " +
        "       s.nome as nome_servico ";
    private final String SQL_RELATORIO_NOTA = " from declaracaoprestacaoservico  dec " +
        "  inner join itemdeclaracaoservico ids on ids.id = (select max(s.id) " +
        "                                                       from itemdeclaracaoservico s " +
        "                                                    where s.declaracaoprestacaoservico_id = dec.id) " +
        "  inner join servico s on s.id = ids.servico_id " +
        "  inner join notafiscal nota on nota.declaracaoprestacaoservico_id = dec.id " +
        "  left join rps on rps.id = nota.rps_id " +
        "  left join loterps lote_rps on lote_rps.id = rps.loterps_id " +
        "  inner join cadastroeconomico ce on ce.id = nota.prestador_id " +
        "  inner join enquadramentofiscal enq on enq.cadastroeconomico_id = ce.id " +
        "                                    and enq.fimvigencia is null " +
        "  left join dadospessoaisnfse dpp on dpp.id = dec.dadospessoaisprestador_id " +
        "  left join dadospessoaisnfse dpt on dpt.id = dec.dadospessoaistomador_id ";

    private final static String SELECT_NOTA_FISCAL = "select nf.* ";
    private final static String FROM_NOTA_FISCAL = "   from notafiscal nf " +
        "  inner join declaracaoprestacaoservico dps on dps.id = nf.declaracaoprestacaoservico_id " +
        "  left join intermediarioservico i on i.id = dps.intermediario_id " +
        "  left join rps rps on rps.id = nf.rps_id " +
        "  left join tomadorserviconfse tnf on tnf.id = nf.tomador_id " +
        "  left join dadospessoaisnfse dptnf on dptnf.id = dps.dadospessoaistomador_id " +
        "  left join dadospessoaisnfse dppnf on dppnf.id = dps.dadospessoaisprestador_id " +
        "  left join cadastroeconomico cep on cep.id = nf.prestador_id ";

    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private SorteioNfseFacade campanhaNfseService;
    @EJB
    private ConfiguracaoNfseFacade configuracaoNfseFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private DeclaracaoMensalServicoFacade declaracaoMensalServicoFacade;
    @EJB
    private FeriadoFacade feriadoFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    private JdbcNotaFiscalDao notaFiscalDao;
    @EJB
    private CacheTributario cacheTributario;

    public ConfiguracaoNfseFacade getConfiguracaoNfseFacade() {
        return configuracaoNfseFacade;
    }

    public JdbcNotaFiscalDao getNotaFiscalDao() {
        if (notaFiscalDao == null) {
            notaFiscalDao = (JdbcNotaFiscalDao) Util.getSpringBeanPeloNome("notaFiscalDao");
        }
        return notaFiscalDao;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    public NotaFiscalFacade() {
        super(NotaFiscal.class);
    }

    public DeclaracaoMensalServicoFacade getDeclaracaoMensalServicoFacade() {
        return declaracaoMensalServicoFacade;
    }

    public DAMFacade getDamFacade() {
        return damFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ConfiguracaoNfse getConfiguracaoNfse() {
        if (cacheTributario.getConfiguracaoNfse() == null) {
            cacheTributario.setConfiguracaoNfse(configuracaoNfseFacade.recuperarConfiguracao());
        }
        return cacheTributario.getConfiguracaoNfse();
    }

    public static void popularSearchDto(List<Object[]> resultado, List<NotaFiscalSearchDTO> retorno) {
        for (Object[] obj : resultado) {
            NotaFiscalSearchDTO nota = new NotaFiscalSearchDTO();
            nota.setId(((BigDecimal) obj[0]).longValue());
            if (obj[1] != null) {
                nota.setNumero(((BigDecimal) obj[1]).longValue());
            }
            nota.setEmissao((Date) obj[2]);
            nota.setNomeTomador((String) obj[3]);
            nota.setCpfCnpjTomador((String) obj[4]);
            nota.setNomePrestador((String) obj[5]);
            nota.setCpfCnpjPrestador((String) obj[6]);
            nota.setSituacao((String) obj[7]);
            nota.setTotalServicos((BigDecimal) obj[8]);
            nota.setIss((BigDecimal) obj[9]);
            nota.setBaseCalculo((BigDecimal) obj[10]);
            nota.setIssRetido(((BigDecimal) obj[11]).compareTo(BigDecimal.ZERO) > 0);
            nota.setTipoDocumentoNfse((String) obj[12]);
            nota.setIssCalculado((BigDecimal) obj[13]);
            nota.setIdPrestador(((BigDecimal) obj[14]).longValue());
            nota.setNaturezaOperacao((String) obj[15]);
            retorno.add(nota);
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public List<RelatorioNotasFiscaisDTO> buscarNotasFiscais(AbstractFiltroNotaFiscal filtro) {
        String whereOrAnd = " where ";
        String sql = CAMPOS_SQL_RELATORIO_NOTA +
            SQL_RELATORIO_NOTA;
        if (filtro.getDataInicial() != null) {
            sql += whereOrAnd + " nota.emissao >= to_date(:dataInicial, 'dd/mm/yyyy') ";
            whereOrAnd = " and ";
        }
        if (filtro.getDataFinal() != null) {
            sql += whereOrAnd + " nota.emissao <= to_date(:dataFinal, 'dd/mm/yyyy') ";
            whereOrAnd = " and ";
        }
        if (filtro.getExercicioInicial() != null && filtro.getMesInicial() != null) {
            sql += whereOrAnd + " (extract(year from dec.competencia) > :exercicioInicial or " +
                " (extract(year from dec.competencia) = :exercicioInicial and extract(month from dec.competencia) >= :mesInicial)) ";
            whereOrAnd = " and ";
        }
        if (filtro.getExercicioFinal() != null && filtro.getMesFinal() != null) {
            sql += whereOrAnd + " (extract(year from dec.competencia) < :exercicioFinal or " +
                " (extract(year from dec.competencia) = :exercicioFinal and extract(month from dec.competencia) <= :mesFinal)) ";
            whereOrAnd = " and ";
        }
        if (filtro.getNumero() != null && !filtro.getNumero().isEmpty()) {
            sql += whereOrAnd + " nota.numero = cast(:numero as number) ";
            whereOrAnd = " and ";
        }
        if (!Strings.isNullOrEmpty(filtro.getCnpjInicial())) {
            sql += whereOrAnd + " dpp.cpfcnpj >= :cnpjInicial ";
            whereOrAnd = " and ";
        }
        if (!Strings.isNullOrEmpty(filtro.getCnpjFinal())) {
            sql += whereOrAnd + " dpp.cpfcnpj <= :cnpjFinal ";
            whereOrAnd = " and ";
        }
        if (filtro.getTipoContribuinte() != null) {
            sql += whereOrAnd + " case when length(dpt.cpfcnpj) = 14 then 'JURIDICA' else 'FISICA' end = :tipoTomador ";
            whereOrAnd = " and ";
        }
        if (filtro.getContribuinte() != null) {
            sql += whereOrAnd + " dpt.cpfcnpj = :tomador ";
            whereOrAnd = " and ";
        }
        if (filtro.getCadastroEconomico() != null) {
            sql += whereOrAnd + " ce.id = :prestador ";
            whereOrAnd = " and ";
        }
        if (filtro.getExigibilidades() != null && !filtro.getExigibilidades().isEmpty()) {
            sql += whereOrAnd + " dec.naturezaOperacao in (:naturezas) ";
            whereOrAnd = " and ";
        }
        if (filtro.getSituacoes() != null && !filtro.getSituacoes().isEmpty()) {
            sql += whereOrAnd + " dec.situacao in (:situacoes) ";
            whereOrAnd = " and ";
        }
        if (!Strings.isNullOrEmpty(filtro.getCpfCnpjTomadorInicial())) {
            sql += whereOrAnd + " dpt.cpfcnpj >= :cpfCnpjTomadorInicial ";
            whereOrAnd = " and ";
        }
        if (!Strings.isNullOrEmpty(filtro.getCpfCnpjTomadorFinal())) {
            sql += whereOrAnd + " dpt.cpfcnpj <= :cpfCnpjTomadorFinal ";
            whereOrAnd = " and ";
        }
        if (filtro.getServicos() != null && !filtro.getServicos().isEmpty()) {
            sql += whereOrAnd + " ids.servico_id in (:servicos) ";
            whereOrAnd = " and ";
        }
        sql += " order by extract(year from dec.competencia) desc, extract(month from dec.competencia), nota.numero ";

        Query q = em.createNativeQuery(sql);
        if (filtro.getNumero() != null && !filtro.getNumero().isEmpty()) {
            q.setParameter("numero", filtro.getNumero());
        }
        if (!Strings.isNullOrEmpty(filtro.getCnpjInicial())) {
            q.setParameter("cnpjInicial", StringUtil.retornaApenasNumeros(filtro.getCnpjInicial()));
        }
        if (!Strings.isNullOrEmpty(filtro.getCnpjFinal())) {
            q.setParameter("cnpjFinal", StringUtil.retornaApenasNumeros(filtro.getCnpjFinal()));
        }
        if (filtro.getTipoContribuinte() != null) {
            q.setParameter("tipoTomador", filtro.getTipoContribuinte().name());
        }
        if (filtro.getContribuinte() != null) {
            q.setParameter("tomador", StringUtil.retornaApenasNumeros(filtro.getContribuinte().getCpf_Cnpj()));
        }
        if (!Strings.isNullOrEmpty(filtro.getCpfCnpjTomadorInicial())) {
            q.setParameter("cpfCnpjTomadorInicial", StringUtil.retornaApenasNumeros(filtro.getCpfCnpjTomadorInicial()));
        }
        if (!Strings.isNullOrEmpty(filtro.getCpfCnpjTomadorFinal())) {
            q.setParameter("cpfCnpjTomadorFinal", StringUtil.retornaApenasNumeros(filtro.getCpfCnpjTomadorFinal()));
        }
        if (filtro.getDataInicial() != null) {
            q.setParameter("dataInicial", DataUtil.getDataFormatada(filtro.getDataInicial()));
        }
        if (filtro.getDataFinal() != null) {
            q.setParameter("dataFinal", DataUtil.getDataFormatada(filtro.getDataFinal()));
        }
        if (filtro.getExercicioInicial() != null && filtro.getMesInicial() != null) {
            q.setParameter("exercicioInicial", filtro.getExercicioInicial().getAno());
            q.setParameter("mesInicial", filtro.getMesInicial().getNumeroMes());
        }
        if (filtro.getExercicioFinal() != null && filtro.getMesFinal() != null) {
            q.setParameter("exercicioFinal", filtro.getExercicioFinal().getAno());
            q.setParameter("mesFinal", filtro.getMesFinal().getNumeroMes());
        }
        if (filtro.getCadastroEconomico() != null) {
            q.setParameter("prestador", filtro.getCadastroEconomico().getId());
        }
        if (filtro.getExigibilidades() != null && !filtro.getExigibilidades().isEmpty()) {
            q.setParameter("naturezas", filtro.getNamesExigibilidades());
        }
        if (filtro.getSituacoes() != null && !filtro.getSituacoes().isEmpty()) {
            List<String> situacoes = Lists.newArrayList();
            for (SituacaoNota s : filtro.getSituacoes()) {
                situacoes.add(s.name());
            }
            q.setParameter("situacoes", situacoes);
        }
        if (filtro.getServicos() != null && !filtro.getServicos().isEmpty()) {
            q.setParameter("servicos", filtro.getIdsServicos());
        }
        return RelatorioNotasFiscaisDTO.popular((List<Object[]>) q.getResultList());
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 10)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<List<RelatorioNotasFiscaisDTO>> buscarNotasFiscaisPorAtividade(AssistenteBarraProgresso assistenteBarraProgresso,
                                                                                 AbstractFiltroNotaFiscal filtro) {
        assistenteBarraProgresso.setDescricaoProcesso("Buscando notas fiscais...");
        assistenteBarraProgresso.setTotal(0);

        StringBuilder sql = new StringBuilder();

        sql.append(CAMPOS_SQL_RELATORIO_NOTA);
        sql.append(SQL_RELATORIO_NOTA);
        sql.append(" where (extract(year from dec.competencia) >= :exercicioInicial and extract(month from dec.competencia) >= :mesInicial) ");
        sql.append("   and (extract(year from dec.competencia) <= :exercicioFinal and extract(month from dec.competencia) <= :mesFinal) ");

        if (filtro.getClassificacoesAtividade() != null && !filtro.getClassificacoesAtividade().isEmpty()) {
            sql.append("   and ce.classificacaoatividade in (:classificacoes) ");
        }
        if (filtro.getContribuinte() != null) {
            sql.append(" and dpt.cpfcnpj = :tomador ");
        }
        if (filtro.getCadastroEconomico() != null) {
            sql.append(" and nota.prestador_id = :prestador ");
        }
        if (filtro.getServicos() != null && !filtro.getServicos().isEmpty()) {
            sql.append(" and s.id in (:servicos) ");
        }
        if (filtro.getSituacoes() != null && !filtro.getSituacoes().isEmpty()) {
            sql.append(" and dec.situacao in (:situacoes) ");
        }

        if (filtro.getValorTotalInicial() != null) {
            sql.append(" and dec.totalservicos >= :valorinicial ");
        }
        if (filtro.getValorTotalFinal() != null) {
            sql.append(" and dec.totalservicos <= :valorfinal ");
        }
        sql.append(" order by s.codigo, ce.classificacaoatividade, nota.numero ");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("exercicioInicial", filtro.getExercicioInicial().getAno());
        q.setParameter("mesInicial", filtro.getMesInicial().getNumeroMes());
        q.setParameter("exercicioFinal", filtro.getExercicioFinal().getAno());
        q.setParameter("mesFinal", filtro.getMesFinal().getNumeroMes());
        if (filtro.getClassificacoesAtividade() != null && !filtro.getClassificacoesAtividade().isEmpty()) {
            q.setParameter("classificacoes", filtro.getNamesClassificacoesAtividade());
        }
        if (filtro.getNumero() != null && !filtro.getNumero().isEmpty()) {
            q.setParameter("numero", filtro.getNumero());
        }
        if (filtro.getContribuinte() != null) {
            q.setParameter("tomador", filtro.getContribuinte().getCpf_Cnpj());
        }
        if (filtro.getCadastroEconomico() != null) {
            q.setParameter("prestador", filtro.getCadastroEconomico().getId());
        }
        if (filtro.getServicos() != null && !filtro.getServicos().isEmpty()) {
            q.setParameter("servicos", filtro.getIdsServicos());
        }
        if (filtro.getSituacoes() != null && !filtro.getSituacoes().isEmpty()) {
            q.setParameter("situacoes", filtro.getNamesSituacoes());
        }
        if (filtro.getValorTotalInicial() != null) {
            q.setParameter("valorinicial", filtro.getValorTotalInicial());
        }
        if (filtro.getValorTotalFinal() != null) {
            q.setParameter("valorfinal", filtro.getValorTotalFinal());
        }
        return new AsyncResult<>(RelatorioNotasFiscaisDTO.popular((List<Object[]>) q.getResultList()));
    }

    public List<RelatorioNotasFiscaisDTO> buscarNotasFiscaisPorRPS(AbstractFiltroNotaFiscal filtro) {
        StringBuilder sql = new StringBuilder();
        sql.append(CAMPOS_SQL_RELATORIO_NOTA);
        sql.append(SQL_RELATORIO_NOTA);
        sql.append(" where lote_rps.situacao = :situacao ");
        if (filtro.getNumero() != null && !filtro.getNumero().isEmpty()) {
            sql.append(" and nota.numero = ").append("cast(:numeroNota as number)");
        }
        if (filtro.getCadastroEconomico() != null) {
            sql.append(" and ce.id = :prestador ");
        }
        if (filtro.getContribuinte() != null) {
            sql.append(" and dpt.cpfcnpj = :tomador ");
        }
        if (filtro.getDataInicial() != null) {
            sql.append(" and nota.emissao >= to_date(:dataInicial, 'dd/mm/yyyy') ");
        }
        if (filtro.getDataFinal() != null) {
            sql.append(" and nota.emissao <= to_date(:dataFinal, 'dd/mm/yyyy') ");
        }
        if (filtro.getNumeroLote() != null && !filtro.getNumeroLote().isEmpty()) {
            sql.append(" and lote_rps.numero = :numeroLote ");
        }
        sql.append(" order by  lote_rps.numero, nota.numero ");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("situacao", filtro.getSituacaoLoteRps().name());
        if (filtro.getNumero() != null && !filtro.getNumero().isEmpty()) {
            q.setParameter("numeroNota", filtro.getNumero());
        }
        if (filtro.getCadastroEconomico() != null) {
            q.setParameter("prestador", filtro.getCadastroEconomico().getId());
        }
        if (filtro.getContribuinte() != null) {
            q.setParameter("tomador", filtro.getContribuinte().getCpf_Cnpj());
        }
        if (filtro.getDataInicial() != null) {
            q.setParameter("dataInicial", DataUtil.getDataFormatada(filtro.getDataInicial()));
        }
        if (filtro.getDataFinal() != null) {
            q.setParameter("dataFinal", DataUtil.getDataFormatada(filtro.getDataFinal()));
        }
        if (filtro.getNumeroLote() != null && !filtro.getNumeroLote().isEmpty()) {
            q.setParameter("numeroLote", filtro.getNumeroLote());
        }
        return RelatorioNotasFiscaisDTO.popular((List<Object[]>) q.getResultList());
    }

    public List<RelatorioCupomParticipanteDTO> buscarCuponsParticipantes(FiltroRelatorioCupomParticipante filtro) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select campanha.descricao, ")
            .append("       premio.premio, ")
            .append("       premio.valorPremio, ")
            .append("       campanha.valorbilhete, ")
            .append("       coalesce(pf.cpf, pj.cnpj) || ' - ' || coalesce(pf.nome, pj.razaosocial) as pessoa, ")
            .append("       campanha.inicio, ")
            .append("       campanha.fim, ")
            .append("       bilhete.premiado, ")
            .append("       premio.dataSorteio, ")
            .append("       bilhete.id as numeroBilhete ")
            .append("  from bilhetepremiocampanhanfse bilhete ")
            .append(" inner join premiocampanhanfse premio on bilhete.premioCampanhaNfse_id = premio.id ")
            .append(" inner join campanhanfse campanha on premio.campanhaNfse_id = campanha.id ")
            .append("  left join pessoa p on bilhete.pessoa_id = p.id ")
            .append("  left join pessoafisica pf on pf.id = p.id ")
            .append("  left join pessoajuridica pj on p.id = pj.id ")
            .append(filtro.montarClausulaWhere())
            .append(" order by campanha.inicio, campanha.descricao, bilhete.id ");
        Query q = em.createNativeQuery(sql.toString());
        List<Object[]> resultado = q.getResultList();
        List<RelatorioCupomParticipanteDTO> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                RelatorioCupomParticipanteDTO campanha = new RelatorioCupomParticipanteDTO();
                campanha.setDescricao((String) obj[0]);
                campanha.setPremio((String) obj[1]);
                campanha.setValorPremio((BigDecimal) obj[2]);
                campanha.setValorBilhete((BigDecimal) obj[3]);
                campanha.setPessoa((String) obj[4]);
                campanha.setInicio((Date) obj[5]);
                campanha.setFim((Date) obj[6]);
                campanha.setPremiado((Boolean) obj[7] ? "Sim" : "Não");
                campanha.setDataSorteio((Date) obj[8]);
                campanha.setNumeroBilhete((BigDecimal) obj[9]);
                retorno.add(campanha);
            }
        }
        return retorno;
    }

    public List<RelatorioSolicitacaoEmissaoNotaDTO> buscarSolicitacoesDeEmissaoDeNota(FiltroSolicitacaoEmissaoNota filtro) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) || ' - ' || coalesce(pf.nome, pj.razaosocial) as cadastroEconomico, ")
            .append("       formatacpfcnpj(pfUser.cpf) || ' - ' || pfUser.nome as solicitante, ")
            .append("       aidfe.solicitadaEm, ")
            .append("       aidfe.quantidadeSolicitada, ")
            .append("       aidfe.quantidadeDeferida, ")
            .append("       aidfe.analisadaEm as deferidaEm, ")
            .append("       aidfe.situacao, ")
            .append("       formatacpfcnpj(pfUsuSis.cpf) || ' - ' || pfUsuSis.nome as deferidoPor ")
            .append("  from aidfe aidfe ")
            .append(" inner join cadastroeconomico cad on aidfe.cadastro_id = cad.id ")
            .append("  left join pessoaFisica pf on pf.id = cad.pessoa_id ")
            .append("  left join pessoaJuridica pj on pj.id = cad.pessoa_id ")
            .append(" inner join usuarioweb us on aidfe.userempresa_id = us.id ")
            .append(" inner join pessoa pUser on us.pessoa_id = pUser.id ")
            .append("  left join pessoaFisica pfUser on pfUser.id = pUser.id ")
            .append("  left join pessoaJuridica pjUser on pjUser.id = pUser.id ")
            .append(" inner join usuariosistema usuSistema on usuSistema.id = aidfe.userprefeitura_id ")
            .append(" inner join pessoafisica pfUsuSis on usuSistema.pessoafisica_id = pfUsuSis.id ")
            .append(filtro.montarClausulaWhere())
            .append(" order by aidfe.solicitadaEm ");
        Query q = em.createNativeQuery(sql.toString());
        List<Object[]> resultado = q.getResultList();
        List<RelatorioSolicitacaoEmissaoNotaDTO> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                RelatorioSolicitacaoEmissaoNotaDTO solicitacao = new RelatorioSolicitacaoEmissaoNotaDTO();
                solicitacao.setCadastroEconomico((String) obj[0]);
                solicitacao.setSolicitante((String) obj[1]);
                solicitacao.setSolicitadaEm((Date) obj[2]);
                solicitacao.setQuantidadeSolicitada(((BigDecimal) obj[3]).intValue());
                solicitacao.setQuantidadeDeferida(((BigDecimal) obj[4]).intValue());
                solicitacao.setDeferidaEm((Date) obj[5]);
                solicitacao.setSituacao(SituacaoAidfe.valueOf((String) obj[6]).getDescricao());
                solicitacao.setDeferidoPor((String) obj[7]);
                retorno.add(solicitacao);
            }
        }
        return retorno;
    }

    public List<RelatorioReceitaDeclaradaISSQNDTO> buscarReceitasDeclaradasISS(FiltroRelatorioReceitaDeclaradaISSQN filtro) {
        String sql = " SELECT " +
            "       formatacpfcnpj(COALESCE(pf.cpf, pj.cnpj)) || ' - ' || COALESCE(pf.nome, pj.razaosocial) AS CADASTRO_ECONOMICO," +
            "       E.ANO AS EXERCICIO," +
            "       FUNCMESTONUMERO(DMS.MES) AS MES," +
            "       DMS.TIPOMOVIMENTO AS TIPO_MOVIMENTO," +
            "       C.ID AS ID_CALCULO," +
            "       DEC.TIPODOCUMENTO AS TIPO_DOC," +
            "       COALESCE(NF.NUMERO, SD.NUMERO) AS NUMERO," +
            "       IDS.QUANTIDADE AS QUANTIDADE," +
            "       S.CODIGO||' - '||S.NOME AS SERVICO," +
            "       IDS.ISS AS ISS," +
            "       IDS.BASECALCULO AS BASE_CALCULO," +
            "       IDS.DEDUCAO AS DEDUCOES," +
            "       IDS.DESCONTOSCONDICIONADOS AS DESCONTO_CONDICIONADO," +
            "       IDS.DESCONTOSINCONDICIONADOS AS DESCONTO_INCONDICIONADO," +
            "       IDS.VALORSERVICO AS VALOR_SERVICO," +
            "       IDS.ALIQUOTASERVICO AS ALIQUOTA" +
            "   FROM DECLARACAOMENSALSERVICO DMS" +
            "  INNER JOIN EXERCICIO E ON E.ID = DMS.EXERCICIO_ID " +
            "  INNER JOIN CADASTROECONOMICO CE ON CE.ID = DMS.PRESTADOR_ID " +
            "  LEFT JOIN PESSOAFISICA PF ON PF.ID = CE.PESSOA_ID" +
            "  LEFT JOIN PESSOAJURIDICA PJ ON PJ.ID = CE.PESSOA_ID" +
            "  INNER JOIN NOTADECLARADA ND ON ND.DECLARACAOMENSALSERVICO_ID = DMS.ID " +
            "  INNER JOIN DECLARACAOPRESTACAOSERVICO DEC ON DEC.ID = ND.DECLARACAOPRESTACAOSERVICO_ID " +
            "  LEFT JOIN NOTAFISCAL NF ON NF.DECLARACAOPRESTACAOSERVICO_ID = DEC.ID " +
            "  LEFT JOIN SERVICODECLARADO SD ON SD.DECLARACAOPRESTACAOSERVICO_ID = DEC.ID " +
            "  INNER JOIN ITEMDECLARACAOSERVICO IDS ON IDS.DECLARACAOPRESTACAOSERVICO_ID = DEC.ID" +
            "  INNER JOIN SERVICO S ON S.ID = IDS.SERVICO_ID   " +
            "  LEFT JOIN PROCESSOCALCULO PC ON PC.ID = DMS.PROCESSOCALCULO_ID " +
            "  LEFT JOIN CALCULO C ON C.PROCESSOCALCULO_ID = PC.ID " +
            "WHERE DMS.SITUACAO = '" + DeclaracaoMensalServico.Situacao.ENCERRADO.name() + "'" +
            "  AND ( (DMS.TIPOMOVIMENTO = '" + TipoMovimentoMensal.NORMAL.name() + "' AND COALESCE(DEC.ISSRETIDO, 0) = 0) OR (DMS.TIPOMOVIMENTO = '" + TipoMovimentoMensal.RETENCAO.name() + "') ) ";
        sql += filtro.montarClausulaWhere();
        sql += " order by 1, 2, 3, 4 ";
        Query q = em.createNativeQuery(sql);
        List<Object[]> resultado = q.getResultList();
        List<RelatorioReceitaDeclaradaISSQNDTO> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            HashMap<Long, BigDecimal> hashValorPorCalculo = new HashMap<>();
            for (Object[] obj : resultado) {
                RelatorioReceitaDeclaradaISSQNDTO apuracao = new RelatorioReceitaDeclaradaISSQNDTO();
                apuracao.setCadastroEconomico((String) obj[0]);
                apuracao.setExercicio(obj[1] != null ? ((Number) obj[1]).longValue() : null);
                apuracao.setMes(obj[2] != null ? ((Number) obj[2]).intValue() : null);
                apuracao.setTipoMovimento(obj[3] != null ? TipoMovimentoMensal.valueOf((String) obj[3]).getDescricao() : null);
                apuracao.setIdCalculo(obj[4] != null ? ((Number) obj[4]).longValue() : null);
                apuracao.setTipoDocumento(obj[5] != null ? TipoDocumentoNfse.valueOf((String) obj[5]).getDescricao() : null);
                apuracao.setNumero(obj[6] != null ? ((Number) obj[6]).toString() : null);
                apuracao.setQuantidade(obj[7] != null ? ((Number) obj[7]).intValue() : null);
                apuracao.setServico(obj[8] != null ? (String) obj[8] : null);
                apuracao.setIss(obj[9] != null ? ((BigDecimal) obj[9]) : BigDecimal.ZERO);
                apuracao.setBaseCalculo(obj[10] != null ? ((BigDecimal) obj[10]) : BigDecimal.ZERO);
                apuracao.setDeducoes(obj[11] != null ? (BigDecimal) obj[11] : BigDecimal.ZERO);
                apuracao.setDescontosIncondicionados(obj[12] != null ? (BigDecimal) obj[12] : BigDecimal.ZERO);
                apuracao.setDescontosCondicionados(obj[13] != null ? (BigDecimal) obj[13] : BigDecimal.ZERO);
                apuracao.setValorServico(obj[14] != null ? (BigDecimal) obj[14] : BigDecimal.ZERO);
                apuracao.setAliquotaServico(obj[15] != null ? (BigDecimal) obj[15] : BigDecimal.ZERO);
                if (apuracao.getIdCalculo() != null) {
                    if (hashValorPorCalculo.get(apuracao.getIdCalculo()) == null) {
                        hashValorPorCalculo.put(apuracao.getIdCalculo(), buscarValorPagoPorCalculoId(apuracao.getIdCalculo()));
                    }
                }
                if (apuracao.getValorPago() == null) {
                    apuracao.setValorPago(BigDecimal.ZERO);
                }
                apuracao.setValorPago(hashValorPorCalculo.get(apuracao.getIdCalculo()));
                retorno.add(apuracao);
            }
        }
        return retorno;
    }

    public TotalizadorRelatorioReceitaDeclaradaISSQNDTO getTotalizadorRelatorioReceitaDeclaradaIssqn(String cadastroEconomico,
                                                                                                     List<TotalizadorRelatorioReceitaDeclaradaISSQNDTO> totalizadores) {
        for (TotalizadorRelatorioReceitaDeclaradaISSQNDTO totalizador : totalizadores) {
            if (totalizador.getCadastroEconomico().equals(cadastroEconomico)) {
                return totalizador;
            }
        }
        TotalizadorRelatorioReceitaDeclaradaISSQNDTO totalizador = new TotalizadorRelatorioReceitaDeclaradaISSQNDTO();
        totalizador.setCadastroEconomico(cadastroEconomico);
        totalizador.setIss(BigDecimal.ZERO);
        totalizador.setValorPago(BigDecimal.ZERO);
        return totalizador;
    }

    public List<TotalizadorRelatorioReceitaDeclaradaISSQNDTO> buscarTotalizadoresReceitasDeclaradasISS(List<RelatorioReceitaDeclaradaISSQNDTO> registros) {
        List<TotalizadorRelatorioReceitaDeclaradaISSQNDTO> retorno = Lists.newArrayList();
        if (!registros.isEmpty()) {
            for (RelatorioReceitaDeclaradaISSQNDTO registro : registros) {
                TotalizadorRelatorioReceitaDeclaradaISSQNDTO totalizador = getTotalizadorRelatorioReceitaDeclaradaIssqn(registro.getCadastroEconomico(), retorno);
                totalizador.setIss(totalizador.getIss().add(registro.getIss()));
                if (registro.getIdCalculo() != null && !totalizador.getIdsCalculos().contains(registro.getIdCalculo())) {
                    totalizador.getIdsCalculos().add(registro.getIdCalculo());
                    totalizador.setValorPago(totalizador.getValorPago().add(registro.getValorPago()));
                }
                retorno = Util.adicionarObjetoEmLista(retorno, totalizador);
            }
        }
        return retorno;
    }

    public List<SorteioNfse> buscarCampanhasNfse(String filtro) {
        return campanhaNfseService.listaFiltrando(filtro.trim(), "descricao");
    }

    public NotaFiscal recupera(Long id, Long prestadorId) {
        NotaFiscal recuperar = recuperar(id);
        Hibernate.initialize(recuperar.getDeclaracaoPrestacaoServico().getItens());
        cadastroEconomicoFacade.inicializarDependencias(recuperar.getPrestador());
        if (recuperar.getPrestador().getId().equals(prestadorId)) {
            return recuperar;
        }
        return null;
    }

    public void delete(Long id) {
        em.remove(recuperar(id));
    }

    public List<CartaCorrecaoNfse> buscarCartaCorrecaoPorNotaFiscal(Long notaFiscalId) {
        String from = "from cartacorrecaonfse where notafiscal_id = :nota";
        Query q = em.createNativeQuery("select * " + from + " order by sequencialcartacorrecao", CartaCorrecaoNfse.class);
        q.setParameter("nota", notaFiscalId);
        return q.getResultList();
    }

    public List<NotaFiscalSearchDTO> buscarNotasSemDeclararPorCompetencia(Long empresaId,
                                                                          int mes,
                                                                          int ano,
                                                                          TipoMovimentoMensal tipoMovimento,
                                                                          boolean somenteComIssDevido) {
        switch (tipoMovimento) {
            case NORMAL:
                return buscarNotasPrestadasSemDeclararPorCompetencia(empresaId, mes, ano, somenteComIssDevido);
            case RETENCAO:
                return buscarNotasTomadasSemDeclararPorCompetencia(empresaId, mes, ano);
            case ISS_RETIDO:
                return buscarNotasPrestadasIssRetidoSemDeclararPorCompetencia(empresaId, mes, ano);
            default:
                return Lists.newArrayList();

        }
    }

    public List<NotaFiscalSearchDTO> buscarNotasPrestadasSemDeclararPorCompetencia(Long empresaId,
                                                                                   int mes, int ano,
                                                                                   boolean somenteComIssDevido) {
        String sql = getSqlNotasNaoDeclaradaMovimentoNormal() +
            " and ce.id = :empresaId " +
            " and extract(month from dec.competencia) = :mes " +
            " and extract(year from dec.competencia) = :ano ";
        if (somenteComIssDevido) {
            sql += " and dec.isspagar > 0 ";
        }
        sql += " order by dec.tipodocumento, nota.numero desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("empresaId", empresaId);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);

        List<Object[]> resultado = q.getResultList();
        List<NotaFiscalSearchDTO> retorno = Lists.newArrayList();
        popularSearchDto(resultado, retorno);
        return retorno;
    }

    public List<NotaFiscalSearchDTO> buscarNotasPrestadasIssRetidoSemDeclararPorCompetencia(Long empresaId,
                                                                                            int mes, int ano) {
        String sql = getSqlNotasNaoDeclaradaMovimentoIssRetido() +
            " and ce.id = :empresaId " +
            " and extract(month from dec.competencia) = :mes " +
            " and extract(year from dec.competencia) = :ano ";
        sql += " order by dec.tipodocumento, coalesce(nota.numero, sdp.numero) desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("empresaId", empresaId);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        List<Object[]> resultado = q.getResultList();
        List<NotaFiscalSearchDTO> retorno = Lists.newArrayList();
        popularSearchDto(resultado, retorno);
        return retorno;
    }

    public List<NotaFiscalSearchDTO> buscarNotasTomadasSemDeclararPorCompetencia(Long empresaId,
                                                                                 int mes, int ano) {
        String hql = getSqlNotasNaoDeclaradaMovimentoRetencao() +
            " and ce.id = :empresaId " +
            " and extract(month from dec.competencia) = :mes " +
            " and extract(year from dec.competencia) = :ano ";
        Query q = em.createNativeQuery(hql);
        q.setParameter("empresaId", empresaId);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        List<Object[]> resultado = q.getResultList();
        List<NotaFiscalSearchDTO> retorno = Lists.newArrayList();
        popularSearchDto(resultado, retorno);
        return retorno;
    }

    @Asynchronous
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public Future<List<NotaFiscalSearchDTO>> buscarNotasParaLancamentoGeralPorFiltro(AssistenteBarraProgresso
                                                                                         assistente,
                                                                                     LoteDeclaracaoMensalServico lancamentoGeralMensal) {
        assistente.setDescricaoProcesso("Buscando Notas Fiscais Emitidas ...");
        return new AsyncResult<>(buscarNotasParaLancamentoGeralPorFiltro(lancamentoGeralMensal));
    }

    public List<NotaFiscalSearchDTO> buscarNotasParaLancamentoGeralPorFiltro(LoteDeclaracaoMensalServico
                                                                                 lancamentoGeralMensal) {
        String sql;
        switch (lancamentoGeralMensal.getTipoMovimento()) {
            case NORMAL: {
                sql = getSqlNotasNaoDeclaradaMovimentoNormal();
                break;
            }
            case RETENCAO: {
                sql = getSqlNotasNaoDeclaradaMovimentoRetencao();
                break;
            }
            case ISS_RETIDO: {
                sql = getSqlNotasNaoDeclaradaMovimentoIssRetido();
                break;
            }
            default: {
                return Lists.newArrayList();
            }
        }

        sql += " and extract(month from dec.competencia) = :mes" +
            " and extract(year from dec.competencia) = :ano" +
            " and ce.inscricaoCadastral >= :cmcInicial" +
            " and ce.inscricaoCadastral <= :cmcFinal ";
        if (!lancamentoGeralMensal.getSelecionarNotas())
            sql += " and not exists (select 1 from naturezajuridicaisencao nji " +
                " where nji.naturezajuridica_id = nj.id " +
                "   and nji.tipomovimentomensal = '" + lancamentoGeralMensal.getTipoMovimento().name() + "') ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("mes", lancamentoGeralMensal.getMes().getNumeroMes());
        q.setParameter("ano", lancamentoGeralMensal.getExercicio().getAno());
        q.setParameter("cmcInicial", lancamentoGeralMensal.getCmcInicial());
        q.setParameter("cmcFinal", lancamentoGeralMensal.getCmcFinal());
        List<Object[]> resultado = q.getResultList();
        List<NotaFiscalSearchDTO> retorno = Lists.newArrayList();
        popularSearchDto(resultado, retorno);
        return retorno;
    }

    private String getSqlNotasNaoDeclaradaMovimentoNormal() {
        return " select " +
            "   dec.id, " +
            "   nota.numero as numero, " +
            "   nota.emissao as emissao, " +
            "   dadospessoais.nomerazaosocial as nome_tomador, " +
            "   dadospessoais.cpfcnpj as cpfcnpj_tomador, " +
            "   dadospessoaisprestador.nomerazaosocial as nome_prestador, " +
            "   dadospessoaisprestador.cpfcnpj as cpfcnpj_prestador, " +
            "   dec.situacao, " +
            "   coalesce(dec.totalservicos, 0) as totalservicos, " +
            "   coalesce(dec.isspagar, 0) as isspagar, " +
            "   coalesce(dec.basecalculo, 0) as basecalculo, " +
            "   coalesce(dec.issretido, 0) as issretido, " +
            "   dec.tipodocumento, " +
            "   dec.issCalculado, " +
            "   ce.id as cadastro_id," +
            "   dec.naturezaoperacao " +
            "  from declaracaoprestacaoservico dec " +
            " inner join notafiscal nota on nota.declaracaoprestacaoservico_id = dec.id " +
            " inner join cadastroeconomico ce on ce.id = nota.prestador_id " +
            " left join dadospessoaisnfse dadospessoais on dadospessoais.id = dec.dadospessoaistomador_id " +
            " left join dadospessoaisnfse dadospessoaisprestador on dadospessoaisprestador.id = dec.dadospessoaisprestador_id " +
            " inner join enquadramentofiscal enq on enq.cadastroeconomico_id = ce.id and enq.fimvigencia is null" +
            " left join naturezajuridica nj on nj.id = ce.naturezajuridica_id " +
            " where dec.situacao != '" + SituacaoNota.CANCELADA.name() + "' " +
            "   and coalesce(nota.homologacao, 0) = 0 " +
            "   and func_nota_declarada('" + TipoMovimentoMensal.NORMAL.name() + "', dec.id) = 0 ";
    }

    private String getSqlNotasNaoDeclaradaMovimentoIssRetido() {
        return " select " +
            "   dec.id, " +
            "   coalesce(nota.numero, sdp.numero) as numero, " +
            "   coalesce(nota.emissao, sdp.emissao) as emissao, " +
            "   dadospessoais.nomerazaosocial as nome_tomador, " +
            "   dadospessoais.cpfcnpj as cpfcnpj_tomador, " +
            "   dadospessoaisprestador.nomerazaosocial as nome_prestador, " +
            "   dadospessoaisprestador.cpfcnpj as cpfcnpj_prestador, " +
            "   dec.situacao, " +
            "   coalesce(dec.totalservicos, 0) as totalservicos, " +
            "   coalesce(dec.issCalculado, 0) as issPagar, " +
            "   coalesce(dec.basecalculo, 0) as basecalculo, " +
            "   coalesce(dec.issretido, 0) as issretido, " +
            "   dec.tipodocumento, " +
            "   dec.issCalculado, " +
            "   ce.id as cadastro_id," +
            "   dec.naturezaoperacao " +
            "  from declaracaoprestacaoservico dec " +
            " left join notafiscal nota on nota.declaracaoprestacaoservico_id = dec.id " +
            " left join servicodeclarado sdp on sdp.declaracaoprestacaoservico_id = dec.id and sdp.tiposervicodeclarado = '" + TipoServicoDeclarado.SERVICO_PRESTADO.name() + "' " +
            " inner join cadastroeconomico ce on ce.id = coalesce(nota.prestador_id, sdp.cadastroeconomico_id) " +
            " inner join enquadramentofiscal enq on enq.cadastroeconomico_id = ce.id and enq.fimvigencia is null " +
            " inner join naturezajuridica nj on nj.id = ce.naturezajuridica_id " +
            " left join dadospessoaisnfse dadospessoais on dadospessoais.id = dec.dadospessoaistomador_id " +
            " left join dadospessoaisnfse dadospessoaisprestador on dadospessoaisprestador.id = dec.dadospessoaisprestador_id " +
            " where dec.situacao != '" + SituacaoNota.CANCELADA.name() + "' " +
            "   and dec.issretido = 1 " +
            "   and coalesce(nota.homologacao, 0) = 0 " +
            "   and func_nota_declarada('" + TipoMovimentoMensal.RETENCAO.name() + "', dec.id) = 0 " +
            "   and func_nota_declarada('" + TipoMovimentoMensal.ISS_RETIDO.name() + "', dec.id) = 0 ";
    }

    private String getSqlNotasNaoDeclaradaMovimentoRetencao() {
        return " select " +
            "     distinct " +
            "     dec.id," +
            "     coalesce(nota.numero, sd.numero) as numero," +
            "     coalesce(nota.emissao, sd.emissao) as emissao," +
            "     dadospessoais.nomerazaosocial as nome_tomador, " +
            "     dadospessoais.cpfcnpj as cpfcnpj_tomador, " +
            "     dadospessoaisprestador.nomerazaosocial as nome_prestador, " +
            "     dadospessoaisprestador.cpfcnpj as cpfcnpj_prestador, " +
            "     dec.situacao," +
            "     coalesce(dec.totalservicos, 0) as totalservicos," +
            "     coalesce(dec.issCalculado, 0) as isspagar," +
            "     coalesce(dec.basecalculo, 0) as basecalculo," +
            "     coalesce(dec.issretido, 0) as issretido," +
            "     dec.tipodocumento, " +
            "     dec.issCalculado, " +
            "     ce.id as cadastro_id," +
            "     dec.naturezaoperacao " +
            "    from declaracaoprestacaoservico dec" +
            "  left join notafiscal nota on dec.id = nota.declaracaoprestacaoservico_id and coalesce(nota.homologacao, 0) = 0 " +
            "  left join servicodeclarado sd on dec.id = sd.declaracaoprestacaoservico_id and sd.tiposervicodeclarado = '" +
            TipoServicoDeclarado.SERVICO_TOMADO.name() + "' " +
            "  inner join dadospessoaisnfse dadospessoais on dadospessoais.id = dec.dadospessoaistomador_id " +
            "  left join pessoajuridica pj on pj.cnpj = dadospessoais.cpfcnpj " +
            "  left join pessoafisica pf on pf.cpf = dadospessoais.cpfcnpj " +
            "  inner join cadastroeconomico ce on ce.pessoa_id = coalesce(pj.id, pf.id) " +
            "  INNER JOIN SITUACAOCADASTROECONOMICO SCE ON SCE.ID = (SELECT MAX(REL.SITUACAOCADASTROECONOMICO_ID) " +
            "                                                          FROM CE_SITUACAOCADASTRAL REL " +
            "                                                       WHERE REL.CADASTROECONOMICO_ID = CE.ID) " +
            "  inner join enquadramentofiscal enq on enq.cadastroeconomico_id = ce.id and enq.fimvigencia is null " +
            "  left join naturezajuridica nj on nj.id = ce.naturezajuridica_id " +
            "  inner join dadospessoaisnfse dadospessoaisprestador on dadospessoaisprestador.id = dec.dadospessoaisprestador_id " +
            " where dec.issretido = 1 " +
            " and SCE.situacaoCadastral in ('" + SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name() + "', '" + SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name() + "')" +
            " and dec.situacao != '" + SituacaoNota.CANCELADA.name() + "' " +
            " and (nota.id is not null or sd.id is not null) " +
            " and func_nota_declarada('" + TipoMovimentoMensal.RETENCAO.name() + "', dec.id) = 0 " +
            " and func_nota_declarada('" + TipoMovimentoMensal.ISS_RETIDO.name() + "', dec.id) = 0 ";
    }

    @Override
    public NotaFiscal recuperar(Object id) {
        NotaFiscal recuperar = super.recuperar(id);
        if (recuperar.getDeclaracaoPrestacaoServico() != null) {
            Hibernate.initialize(recuperar.getDeclaracaoPrestacaoServico().getItens());
            for (ItemDeclaracaoServico itemDeclaracaoServico : recuperar.getDeclaracaoPrestacaoServico().getItens()) {
                Hibernate.initialize(itemDeclaracaoServico.getDetalhes());
            }
        }
        if (recuperar.getRps() != null && recuperar.getRps().getItens() != null) {
            Hibernate.initialize(recuperar.getRps().getItens());
        }
        Hibernate.initialize(recuperar.getPrestador().getEnquadramentos());

        return recuperar;
    }

    public List<NotaFiscalSearchDTO> buscarNotaPorDeclaracaoEmpresa(Long prestadorId, Long declaracaoId) {
        String hql = "select dec.id, coalesce(nota.numero, sd.numero) as numero, " +
            " coalesce(nota.emissao, sd.emissao) as emissao, " +
            " dadosPessoais.nomeRazaoSocial as nome_tomador, dadosPessoais.cpfCnpj as cpfcnpj_tomador, " +
            " prestador.nomeRazaoSocial as nome_prestador, prestador.cpfCnpj as cpfcnpj_prestador, " +
            " dec.situacao, dec.totalServicos, dec.issPagar, dec.baseCalculo, dec.issretido, " +
            " dec.tipodocumento, dec.issCalculado, declaracaoMensal.prestador_id, dec.naturezaoperacao " +
            " from DeclaracaoPrestacaoServico dec " +
            " left join notafiscal nota on dec.id = nota.declaracaoPrestacaoServico_id " +
            " left join servicodeclarado sd on dec.id = sd.declaracaoPrestacaoServico_id " +
            " inner join NOTADECLARADA nd on nd.declaracaoPrestacaoServico_id = dec.id  " +
            " inner join DECLARACAOMENSALSERVICO declaracaoMensal on declaracaoMensal.id = nd.DECLARACAOMENSALSERVICO_id " +
            " left join DadosPessoaisNfse dadosPessoais on dadosPessoais.id = dec.dadosPessoaistomador_id " +
            " left join DadosPessoaisNfse prestador on prestador.id = dec.DADOSPESSOAISPRESTADOR_ID " +
            " where declaracaoMensal.prestador_id = :empresaId " +
            " and declaracaoMensal.id = :declaracaoId " +
            " order by coalesce(nota.numero, sd.numero)";
        Query q = em.createNativeQuery(hql);
        q.setParameter("empresaId", prestadorId);
        q.setParameter("declaracaoId", declaracaoId);
        List<Object[]> resultado = q.getResultList();
        List<NotaFiscalSearchDTO> retorno = Lists.newArrayList();
        popularSearchDto(resultado, retorno);
        return retorno;
    }

    public String getObservacaoDamNotaFiscal(Long calculoId) {
        String sql = " select 'Nfs-e: nº.' || LISTAGG(coalesce(nf.numero, sd.numero) || " +
            "                                case when dpp.cpfcnpj = coalesce(pf.cpf, pj.cnpj) then '' else ' - Prest. '||dpp.nomerazaosocial end, ', ') " +
            "                                 WITHIN GROUP (ORDER BY coalesce(nf.numero, sd.numero) desc) " +
            "    from calculo c " +
            "   inner join calculopessoa cp on cp.calculo_id = c.id " +
            "   left join pessoafisica pf on pf.id = cp.pessoa_id " +
            "   left join pessoajuridica pj on pj.id = cp.pessoa_id " +
            "   inner join processocalculo  pc on pc.id = c.processocalculo_id " +
            "   inner join declaracaomensalservico dms on dms.processocalculo_id = pc.id " +
            "                                         and dms.situacao = :encerrado " +
            "   inner join notadeclarada nd on nd.declaracaomensalservico_id = dms.id " +
            "   inner join declaracaoprestacaoservico dps on dps.id = nd.declaracaoprestacaoservico_id " +
            "   inner join dadospessoaisnfse dpp on dpp.id = dps.dadospessoaisprestador_id " +
            "   left join notafiscal nf on nf.declaracaoprestacaoservico_id = nd.declaracaoprestacaoservico_id " +
            "   left join servicodeclarado sd on sd.declaracaoprestacaoservico_id = nd.declaracaoprestacaoservico_id " +
            " where c.id = :calculo_id " +
            "   and ((dms.tipomovimento = :tipo_normal and dps.isspagar > 0) or (dms.tipomovimento != :tipo_normal and dps.isscalculado > 0)) " +
            "   and rownum <= 10 ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("calculo_id", calculoId);
        q.setParameter("tipo_normal", TipoMovimentoMensal.NORMAL.name());
        q.setParameter("encerrado", DeclaracaoMensalServico.Situacao.ENCERRADO.name());
        List resultList = q.getResultList();
        return resultList != null && !resultList.isEmpty() ? (String) resultList.get(0) : "";
    }

    public boolean hasNfseAutentica(AutenticaNfseDTO autenticaNfse) {
        return buscarNfsePorAutenticacao(autenticaNfse) != null;
    }

    public NotaFiscalNfseDTO buscarNfsePorAutenticacao(AutenticaNfseDTO autenticaNfse) {

        StringBuilder sql = new StringBuilder();
        sql.append("select notafiscal.id, ")
            .append(" notafiscal.numero, ")
            .append(" notafiscal.emissao, ")
            .append(" dec.situacao ")
            .append(" from notafiscal ")
            .append(" inner join declaracaoprestacaoservico dec on dec.id = notafiscal.declaracaoprestacaoservico_id ")
            .append(" inner join dadospessoaisnfse dadosPrestador on dadosPrestador.id = dec.dadospessoaisprestador_id ")
            .append(" where dadosPrestador.cpfCnpj = :cpfCnpj ")
            .append(" and replace(notafiscal.codigoVerificacao, '-', '') = :codigoVerificacao  ")
            .append(" and notafiscal.numero = :numero ");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("codigoVerificacao", autenticaNfse.getCodigoAutenticacao().replaceAll("[^a-zA-Z0-9]", ""));
        q.setParameter("numero", Integer.valueOf(autenticaNfse.getNumeroNfse()));
        q.setParameter("cpfCnpj", autenticaNfse.getCpfCnpjPrestador().replaceAll("[^a-zA-Z0-9]", ""));

        if (!q.getResultList().isEmpty()) {
            Object[] obj = (Object[]) q.getResultList().get(0);
            NotaFiscalNfseDTO nota = new NotaFiscalNfseDTO();
            nota.setId(((Number) obj[0]).longValue());
            if (obj[1] != null) {
                nota.setNumero(((Number) obj[1]).longValue());
            }
            nota.setEmissao((Date) obj[2]);
            nota.setSituacao(SituacaoDeclaracaoNfseDTO.valueOf((String) obj[3]));
            return nota;
        }
        return null;
    }

    public NotaFiscal buscarNotaPorDeclaracaoPrestacaoServico(DeclaracaoPrestacaoServico declaracaoPrestacaoServico) {
        String sql = "select nota from NotaFiscal nota where nota.declaracaoPrestacaoServico.id = :id";
        Query q = em.createQuery(sql).setParameter("id", declaracaoPrestacaoServico.getId());
        if (!q.getResultList().isEmpty()) {
            return (NotaFiscal) q.getResultList().get(0);
        }
        return null;
    }

    public Long buscarIdDoCalculoPorNotaFiscal(Long idNotaFiscal) {
        String sql = "select calculo.id " +
            "  from DeclaracaoPrestacaoServico dec  " +
            " inner join ItemDeclaracaoServico item on dec.id = item.DeclaracaoPrestacaoServico_id  " +
            " inner join notafiscal nf on dec.id = nf.declaracaoPrestacaoServico_id  " +
            " inner join notadeclarada notadec on notadec.declaracaoPrestacaoServico_id = dec.id " +
            " inner join declaracaomensalservico dms on dms.id = notadec.declaracaoMensalServico_id " +
            " inner join processoCalculo proc on proc.id = dms.processoCalculo_id " +
            " inner join calculo on calculo.processocalculo_id = proc.id " +
            " where nf.id = :idNota";
        Query q = em.createNativeQuery(sql).setParameter("idNota", idNotaFiscal);
        if (!q.getResultList().isEmpty()) {
            return ((BigDecimal) q.getResultList().get(0)).longValue();
        }
        return null;
    }

    public List<RelatorioPrestadoresAutorizados> buscarDadosRelatorioPrestadoresAutorizados
        (FiltroRelatorioPrestadoresAutorizados filtro) {
        String sql = "SELECT " +
            "cad.inscricaoCadastral, " +
            "COALESCE(pf.cpf, " +
            "pj.cnpj) || ' - ' || COALESCE(pf.nome, " +
            "pj.razaosocial) AS pessoa, " +
            "sitCad.situacaocadastral, " +
            "sit.porte, " +
            "sit.REGIMETRIBUTARIO, " +
            "cad.CLASSIFICACAOATIVIDADE AS classificacaoAtividade " +
            "FROM cadastroeconomico cad " +
            "INNER JOIN enquadramentofiscal sit ON cad.id = sit.cadastroeconomico_id " +
            "AND sit.fimvigencia IS NULL " +
            "INNER JOIN ce_situacaocadastral s ON s.cadastroeconomico_id = cad.id " +
            "INNER JOIN SituacaoCadastroEconomico sitCad ON sitCad.dataregistro = (SELECT MAX(s_sitCad.dataRegistro) " +
            "FROM SituacaoCadastroEconomico s_sitCad " +
            "INNER JOIN CE_SITUACAOCADASTRAL s_s ON s_s.SITUACAOCADASTROECONOMICO_ID = s_sitCad.ID " +
            "                                      WHERE s_s.CADASTROECONOMICO_ID = cad.id) " +
            "   INNER JOIN pessoa p ON cad.pessoa_id = p.id " +
            "   LEFT JOIN pessoafisica pf ON p.id = pf.id " +
            "   LEFT JOIN pessoajuridica pj ON p.id = pj.id " +
            " WHERE sit.tiponotafiscalservico = :tipoNotaFiscal " +
            "   AND sit.fimvigencia IS NULL ";
        sql += filtro.getCadastroEconomico() != null ? " and cad.id = :cad " : "";
        sql += filtro.getPorte() != null ? " and sit.porte = :porte" : "";
        sql += filtro.getSituacaoCadastral() != null ? " and sitCad.situacaocadastral = :situacao " : "";
        sql += filtro.getPessoa() != null ? " and p.id = :idPessoa " : "";
        sql += "  order by cad.inscricaoanterior ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoNotaFiscal", TipoNotaFiscalServico.ELETRONICA.name());
        if (filtro.getCadastroEconomico() != null) {
            q.setParameter("cad", filtro.getCadastroEconomico().getId());
        }
        if (filtro.getPorte() != null) {
            q.setParameter("porte", filtro.getPorte().name());
        }
        if (filtro.getSituacaoCadastral() != null) {
            q.setParameter("situacao", filtro.getSituacaoCadastral().name());
        }
        if (filtro.getPessoa() != null) {
            q.setParameter("idPessoa", filtro.getPessoa().getId());
        }
        List<Object[]> resultado = q.getResultList();
        List<RelatorioPrestadoresAutorizados> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                RelatorioPrestadoresAutorizados relatorio = new RelatorioPrestadoresAutorizados();
                relatorio.setInscricaoCadastral((String) obj[0]);
                relatorio.setPessoa((String) obj[1]);
                relatorio.setSituacaoCadastral(obj[2] != null ? SituacaoCadastralCadastroEconomico.valueOf((String) obj[2]).getDescricao() : "");
                relatorio.setPorte(obj[3] != null ? TipoPorte.valueOf((String) obj[3]).getDescricao() : "");
                relatorio.setRegimeTributacao(obj[4] != null ? RegimeTributario.valueOf((String) obj[4]).getDescricao() : "");
                relatorio.setClassificacao((String) obj[5]);
                retorno.add(relatorio);
            }
        }
        return retorno;
    }

    public BigDecimal buscarValorPagoPorCalculoId(Long id) {
        List<ResultadoParcela> resultados = new ConsultaParcela()
            .addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, id)
            .addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.PAGO)
            .executaConsulta()
            .getResultados();
        BigDecimal resultado = BigDecimal.ZERO;
        for (ResultadoParcela resultadoParcela : resultados) {
            resultado = resultado.add(resultadoParcela.getValorPago());
        }
        return resultado;
    }

    public List<ResultadoParcela> buscarParcelasDaNota(Long idNotaFiscal) {
        Long idCalculo = buscarIdDoCalculoPorNotaFiscal(idNotaFiscal);
        if (idCalculo != null) {
            return new ConsultaParcela()
                .addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, idCalculo)
                .executaConsulta().getResultados();
        }
        return Lists.newArrayList();
    }

    public NotaFiscal buscarNotaFiscal(CadastroEconomico cadastroEconomico, Integer numero) throws Exception {
        List<ParametroQuery> parametros = Lists.newArrayList();
        parametros.add(new ParametroQuery(" and ",
            Lists.newArrayList(new ParametroQueryCampo(" nf.prestador_id", Operador.IGUAL, cadastroEconomico.getId()),
                new ParametroQueryCampo(" nf.numero", Operador.IGUAL, numero))));
        List<NotaFiscal> notas = buscarNotasFiscais(parametros);
        if (notas != null && !notas.isEmpty()) {
            return notas.get(0);
        }
        return null;
    }

    public List<NotaFiscal> buscarNotasFiscais(List<ParametroQuery> parametros) throws Exception {
        return buscarNotasFiscais(parametros, " order by nf.prestador_id, nf.numero ", null, null);
    }

    public List<NotaFiscal> buscarNotasFiscais(List<ParametroQuery> parametros, String orderBy, Integer
        firstResult, Integer maxResult) throws Exception {

        StringBuilder sql = new StringBuilder();
        sql.append(SELECT_NOTA_FISCAL)
            .append(FROM_NOTA_FISCAL);
        Map<String, Object> parameters = GeradorQuery.montarParametroString(sql, parametros);
        if (!Strings.isNullOrEmpty(orderBy))
            sql.append(orderBy);
        Query q = em.createNativeQuery(sql.toString(), NotaFiscal.class);
        GeradorQuery.adicionarParametrosNaQuery(parameters, q);

        List<NotaFiscal> notasFiscais = buscarNotasByQuery(firstResult, maxResult, q);
        if (notasFiscais != null) return notasFiscais;
        return Lists.newArrayList();
    }


    public List<NotaFiscal> buscarNotasFiscais(String complemento, Integer
        firstResult, Integer maxResult, List<FiltroConsultaEntidade> filtrosConsultaNsfe) {

        StringBuilder sql = new StringBuilder();
        sql.append(SELECT_NOTA_FISCAL)
            .append(FROM_NOTA_FISCAL);
        if (!Strings.isNullOrEmpty(complemento))
            sql.append(complemento);
        Map<String, Object> params = addParamsToQuery(filtrosConsultaNsfe, sql);
        Query q = em.createNativeQuery(sql.toString(), NotaFiscal.class);
        params.keySet().forEach(s -> q.setParameter(s, params.get(s)));
        List<NotaFiscal> notasFiscais = buscarNotasByQuery(firstResult, maxResult, q);
        if (notasFiscais != null) return notasFiscais;
        return Lists.newArrayList();
    }

    public TotalizadorRelatorioNotasFiscaisDTO buscarValoresTotalizador(String complemento,
                                                                        List<FiltroConsultaEntidade> filtrosConsultaNsfe) {

        StringBuilder sql = new StringBuilder();
        sql.append(" select count(1) as qtdNotas, sum(dps.totalservicos) as servicos, ");
        sql.append(" sum(coalesce(dps.descontosincondicionais, 0) + coalesce(dps.descontoscondicionais,0)) as descontos, ");
        sql.append(" sum(coalesce(dps.deducoeslegais, 0)) as deducoes, sum(coalesce(dps.basecalculo, 0)) as baseCalculo, ");
        sql.append(" sum(coalesce(dps.isscalculado, 0)) as iss ");
        sql.append(FROM_NOTA_FISCAL);
        if (!Strings.isNullOrEmpty(complemento))
            sql.append(complemento);
        Map<String, Object> params = addParamsToQuery(filtrosConsultaNsfe, sql);
        Query q = em.createNativeQuery(sql.toString());
        params.keySet().forEach(s -> q.setParameter(s, params.get(s)));
        Object[] obj = (Object[]) q.getSingleResult();
        TotalizadorRelatorioNotasFiscaisDTO dto = new TotalizadorRelatorioNotasFiscaisDTO();
        dto.setQuantidadeNotas(((Number) obj[0]).intValue());
        dto.setTotalServicos((BigDecimal) obj[1]);
        dto.setTotalDescontos((BigDecimal) obj[2]);
        dto.setTotalDeducoes((BigDecimal) obj[3]);
        dto.setTotalBaseCalculo((BigDecimal) obj[4]);
        dto.setTotalIssCalculado((BigDecimal) obj[5]);
        return dto;
    }

    public Integer contarNotasFiscais(String complemento, List<FiltroConsultaEntidade> filtrosConsultaNsfe) {
        StringBuilder sql = new StringBuilder();
        sql.append("select count(distinct nf.id) ")
            .append(FROM_NOTA_FISCAL);
        if (!Strings.isNullOrEmpty(complemento))
            sql.append(complemento);
        Map<String, Object> params = addParamsToQuery(filtrosConsultaNsfe, sql);
        Query q = em.createNativeQuery(sql.toString());
        params.keySet().forEach(s -> q.setParameter(s, params.get(s)));
        if (!q.getResultList().isEmpty()) {
            return ((Number) q.getResultList().get(0)).intValue();
        }
        return 0;
    }

    private static Map<String, Object> addParamsToQuery(List<FiltroConsultaEntidade> filtrosConsultaNsfe, StringBuilder sql) {
        Map<String, Object> params = Maps.newHashMap();
        for (FiltroConsultaEntidade filtro : filtrosConsultaNsfe) {
            if (filtro.getValor() != null) {
                String paramName = "param_" + filtrosConsultaNsfe.indexOf(filtro);
                sql.append(" and ")
                    .append(filtro.getCampoParaQuery()).append(" ")
                    .append(filtro.getOperacao().getOperador()).append(" ").append(":").append(paramName);
                params.put(paramName, filtro.getValorParaQuery());
            }
        }
        return params;
    }

    private static List<NotaFiscal> buscarNotasByQuery(Integer firstResult, Integer maxResult, Query q) {
        if (firstResult != null)
            q.setFirstResult(firstResult);

        if (maxResult != null)
            q.setMaxResults(maxResult);

        List<NotaFiscal> notasFiscais = q.getResultList();
        if (notasFiscais != null && !notasFiscais.isEmpty()) {
            for (NotaFiscal notaFiscal : notasFiscais) {
                Hibernate.initialize(notaFiscal.getDeclaracaoPrestacaoServico().getItens());
            }
            return notasFiscais;
        }
        return null;
    }

    public List<NotaFiscalSearchDTO> buscarNotasPorCompetenciaAndTipo(Long prestadorId, Integer exercicio, Integer mes,
                                                                      TipoMovimentoMensal tipoMovimentoMensal, Boolean declarada) {
        switch (tipoMovimentoMensal) {
            case NORMAL: {
                return buscarNotasPorCompetenciaServicosPrestados(prestadorId, exercicio, mes, declarada);
            }
            case RETENCAO: {
                return buscarNotasPorCompetenciaServicosTomados(prestadorId, exercicio, mes, declarada);
            }
        }
        return Lists.newArrayList();
    }

    public List<NotaFiscalSearchDTO> buscarNotasPorCompetenciaServicosPrestados(Long prestadorId, Integer exercicio,
                                                                                Integer mes, Boolean declarada) {
        String sql = "  SELECT distinct " +
            "   DEC.ID,  " +
            "   COALESCE(NOTA.NUMERO, SDP.NUMERO) AS NUMERO,  " +
            "   COALESCE(NOTA.EMISSAO, SDP.EMISSAO) AS EMISSAO,  " +
            "   DADOSPESSOAIS.NOMERAZAOSOCIAL,  " +
            "   DADOSPESSOAIS.CPFCNPJ,  " +
            "   DEC.SITUACAO,  " +
            "   COALESCE(DEC.TOTALSERVICOS, 0) AS TOTAL_SERVICOS,  " +
            "   COALESCE(COALESCE(DEC.DESCONTOSINCONDICIONAIS, DEC.DESCONTOSCONDICIONAIS), 0) AS DESCONTO, " +
            "   COALESCE(DEC.DEDUCOESLEGAIS, 0) AS DEDUCOES, " +
            "   COALESCE(DEC.VALORLIQUIDO, 0) AS VALORLIQUIDO,  " +
            "   COALESCE(DEC.BASECALCULO, 0) AS BASE_CALCULO,  " +
            "   COALESCE(IDS.ALIQUOTASERVICO, 0) AS ALIQUOTA,  " +
            "   COALESCE(DEC.ISSRETIDO, 0) AS ISS_RETIDO,  " +
            "   COALESCE(DEC.ISSCALCULADO, 0) AS ISS_CALCULADO,  " +
            "   COALESCE(DEC.ISSPAGAR, 0) AS ISS_PAGAR,  " +
            "   DEC.TIPODOCUMENTO, " +
            "   COALESCE(NOTA.PRESTADOR_ID, SDP.CADASTROECONOMICO_ID), " +
            "   DEC.NATUREZAOPERACAO, " +
            "   FUNC_NOTA_DECLARADA('" + TipoMovimentoMensal.NORMAL.name() + "', DEC.ID) AS DECLARADA  " +
            "  FROM DECLARACAOPRESTACAOSERVICO DEC  " +
            " INNER JOIN ITEMDECLARACAOSERVICO IDS ON IDS.DECLARACAOPRESTACAOSERVICO_ID = DEC.ID " +
            " LEFT JOIN NOTAFISCAL NOTA ON NOTA.DECLARACAOPRESTACAOSERVICO_ID = DEC.ID  " +
            " LEFT JOIN SERVICODECLARADO SDP ON SDP.DECLARACAOPRESTACAOSERVICO_ID = DEC.ID AND SDP.TIPOSERVICODECLARADO = :TIPO_SERVICO_PRESTADO " +
            " INNER JOIN CADASTROECONOMICO CE ON CE.ID = COALESCE(NOTA.PRESTADOR_ID, SDP.CADASTROECONOMICO_ID) " +
            " LEFT JOIN DADOSPESSOAISNFSE DADOSPESSOAIS ON DADOSPESSOAIS.ID = DEC.DADOSPESSOAISTOMADOR_ID  " +
            "WHERE (NOTA.ID IS NOT NULL OR SDP.ID IS NOT NULL) " +
            "  AND CE.ID = :PRESTADOR_ID  " +
            "  AND EXTRACT(YEAR FROM DEC.COMPETENCIA) = :EXERCICIO " +
            "  AND EXTRACT(MONTH FROM DEC.COMPETENCIA) = :MES ";
        if (declarada != null) {
            sql += "  AND FUNC_NOTA_ESCRITURADA(DEC.ID) = " + (declarada ? " 1 " : " 0 ");
        }
        sql += " ORDER BY DEC.TIPODOCUMENTO, COALESCE(NOTA.NUMERO, SDP.NUMERO) ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("PRESTADOR_ID", prestadorId);
        q.setParameter("EXERCICIO", exercicio);
        q.setParameter("MES", mes);
        q.setParameter("TIPO_SERVICO_PRESTADO", TipoServicoDeclarado.SERVICO_PRESTADO.name());
        List<Object[]> resultado = q.getResultList();
        List<NotaFiscalSearchDTO> retorno = Lists.newArrayList();
        popularSearchDTOCompleto(resultado, retorno);
        return retorno;
    }

    private void popularSearchDTOCompleto(List<Object[]> resultado, List<NotaFiscalSearchDTO> retorno) {
        for (Object[] obj : resultado) {
            NotaFiscalSearchDTO nota = new NotaFiscalSearchDTO();
            nota.setId(((BigDecimal) obj[0]).longValue());
            if (obj[1] != null) {
                nota.setNumero(((BigDecimal) obj[1]).longValue());
            }
            nota.setEmissao((Date) obj[2]);
            nota.setNomeTomador((String) obj[3]);
            nota.setCpfCnpjTomador((String) obj[4]);
            nota.setSituacao((String) obj[5]);
            nota.setTotalServicos((BigDecimal) obj[6]);
            nota.setDesconto((BigDecimal) obj[7]);
            nota.setDeducoes((BigDecimal) obj[8]);
            nota.setValorLiquido((BigDecimal) obj[9]);
            nota.setBaseCalculo((BigDecimal) obj[10]);
            nota.setAliquota((BigDecimal) obj[11]);
            nota.setIssRetido(((BigDecimal) obj[12]).compareTo(BigDecimal.ZERO) > 0);
            nota.setIssCalculado((BigDecimal) obj[13]);
            nota.setIss((BigDecimal) obj[14]);
            nota.setTipoDocumentoNfse((String) obj[15]);
            nota.setIdPrestador(((BigDecimal) obj[16]).longValue());
            nota.setNaturezaOperacao((String) obj[17]);
            try {
                nota.setDeclarada(((BigDecimal) obj[18]).intValue() == 1);
            } catch (IndexOutOfBoundsException e) {
            }
            retorno.add(nota);
        }
    }

    public List<NotaFiscalSearchDTO> buscarNotasPorCompetenciaServicosTomados(Long prestadorId, Integer exercicio,
                                                                              Integer mes, Boolean declarada) {
        String sql = "  SELECT DISTINCT " +
            "   DEC.ID,  " +
            "   COALESCE(NOTA.NUMERO, SDP.NUMERO) AS NUMERO,  " +
            "   COALESCE(NOTA.EMISSAO, SDP.EMISSAO) AS EMISSAO,  " +
            "   DADOSPESSOAIS.NOMERAZAOSOCIAL,  " +
            "   DADOSPESSOAIS.CPFCNPJ,  " +
            "   DEC.SITUACAO,  " +
            "   COALESCE(DEC.TOTALSERVICOS, 0) AS TOTAL_SERVICOS,  " +
            "   COALESCE(COALESCE(DEC.DESCONTOSINCONDICIONAIS, DEC.DESCONTOSCONDICIONAIS), 0) AS DESCONTO, " +
            "   COALESCE(DEC.DEDUCOESLEGAIS, 0) AS DEDUCOES, " +
            "   COALESCE(DEC.VALORLIQUIDO, 0) AS VALORLIQUIDO,  " +
            "   COALESCE(DEC.BASECALCULO, 0) AS BASE_CALCULO,  " +
            "   COALESCE(IDS.ALIQUOTASERVICO, 0) AS ALIQUOTA,  " +
            "   COALESCE(DEC.ISSRETIDO, 0) AS ISS_RETIDO,  " +
            "   COALESCE(DEC.ISSCALCULADO, 0) AS ISS_CALCULADO,  " +
            "   COALESCE(DEC.ISSPAGAR, 0) AS ISS_PAGAR,  " +
            "   DEC.TIPODOCUMENTO, " +
            "   COALESCE(NOTA.PRESTADOR_ID, SDP.CADASTROECONOMICO_ID), " +
            "   DEC.NATUREZAOPERACAO, " +
            "   FUNC_NOTA_DECLARADA('" + TipoMovimentoMensal.RETENCAO.name() + "', DEC.ID) AS DECLARADA  " +
            "  FROM DECLARACAOPRESTACAOSERVICO DEC  " +
            " INNER JOIN ITEMDECLARACAOSERVICO IDS ON IDS.DECLARACAOPRESTACAOSERVICO_ID = DEC.ID " +
            " LEFT JOIN NOTAFISCAL NOTA ON NOTA.DECLARACAOPRESTACAOSERVICO_ID = DEC.ID  " +
            " LEFT JOIN SERVICODECLARADO SDP ON SDP.DECLARACAOPRESTACAOSERVICO_ID = DEC.ID AND SDP.TIPOSERVICODECLARADO = :TIPO_SERVICO_TOMADO " +
            " LEFT JOIN DADOSPESSOAISNFSE DADOSPESSOAIS ON DADOSPESSOAIS.ID = DEC.DADOSPESSOAISPRESTADOR_ID  " +
            " LEFT JOIN DADOSPESSOAISNFSE DPT ON DPT.ID = DEC.DADOSPESSOAISTOMADOR_ID " +
            " LEFT JOIN PESSOAJURIDICA PJT ON PJT.CNPJ = DPT.CPFCNPJ " +
            " INNER JOIN CADASTROECONOMICO CE ON CE.PESSOA_ID = PJT.ID " +
            "WHERE (NOTA.ID IS NOT NULL OR SDP.ID IS NOT NULL) " +
            "   AND CE.ID =:PRESTADOR_ID  " +
            "   AND EXTRACT(YEAR FROM DEC.COMPETENCIA) = :EXERCICIO " +
            "   AND EXTRACT(MONTH FROM DEC.COMPETENCIA) = :MES ";
        if (declarada != null) {
            sql += "   AND FUNC_NOTA_ESCRITURADA(DEC.ID) = " + (declarada ? " 1 " : " 0 ");
        }
        sql += "ORDER BY DEC.TIPODOCUMENTO, COALESCE(NOTA.NUMERO, SDP.NUMERO) ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("PRESTADOR_ID", prestadorId);
        q.setParameter("EXERCICIO", exercicio);
        q.setParameter("MES", mes);
        q.setParameter("TIPO_SERVICO_TOMADO", TipoServicoDeclarado.SERVICO_TOMADO.name());
        List<Object[]> resultado = q.getResultList();
        List<NotaFiscalSearchDTO> retorno = Lists.newArrayList();
        popularSearchDTOCompleto(resultado, retorno);
        return retorno;
    }

    public List<TotalPorNaturezaSituacaoNfseDTO> agruparPorNaturezaOperacaoAndSituacao(List<TotalPorNaturezaSituacaoNfseDTO> registros,
                                                                                       List<NotaFiscalSearchDTO> documentos) {
        registros = Lists.newArrayList();
        for (NotaFiscalSearchDTO documento : documentos) {
            TotalPorNaturezaSituacaoNfseDTO totalPorNaturezaOperacao = getTotalPorNaturezaSituacao(registros,
                documento.getNaturezaOperacao(), documento.getSituacao());
            totalPorNaturezaOperacao.setTotalServicos(totalPorNaturezaOperacao.getTotalServicos().add(documento.getTotalServicos()));
            totalPorNaturezaOperacao.setDesconto(totalPorNaturezaOperacao.getDesconto().add(documento.getDesconto()));
            totalPorNaturezaOperacao.setDeducoes(totalPorNaturezaOperacao.getDeducoes().add(documento.getDeducoes()));
            totalPorNaturezaOperacao.setValorLiquido(totalPorNaturezaOperacao.getValorLiquido().add(documento.getValorLiquido()));
            totalPorNaturezaOperacao.setBaseCalculo(totalPorNaturezaOperacao.getBaseCalculo().add(documento.getBaseCalculo()));
            if (!documento.getIssRetido()) {
                totalPorNaturezaOperacao.setIssqn(totalPorNaturezaOperacao.getIssqn().add(documento.getIss()));
            } else {
                totalPorNaturezaOperacao.setIssqnRetido(totalPorNaturezaOperacao.getIssqnRetido().add(documento.getIssCalculado()));
            }
            totalPorNaturezaOperacao.setQuantidadeNotas(totalPorNaturezaOperacao.getQuantidadeNotas() + 1);
        }

        Collections.sort(registros, new Comparator<TotalPorNaturezaSituacaoNfseDTO>() {
            @Override
            public int compare(TotalPorNaturezaSituacaoNfseDTO o1, TotalPorNaturezaSituacaoNfseDTO o2) {
                return o1.getNaturezaOperacao().compareTo(o2.getNaturezaOperacao());
            }
        });

        return registros;
    }

    private TotalPorNaturezaSituacaoNfseDTO getTotalPorNaturezaSituacao(List<TotalPorNaturezaSituacaoNfseDTO> registros,
                                                                        String naturezaOperacao,
                                                                        String situacao) {
        for (TotalPorNaturezaSituacaoNfseDTO registro : registros) {
            if (registro.getNaturezaOperacao().equals(naturezaOperacao) && registro.getSituacao().equals(situacao))
                return registro;
        }
        TotalPorNaturezaSituacaoNfseDTO dto = new TotalPorNaturezaSituacaoNfseDTO();
        dto.setNaturezaOperacao(naturezaOperacao);
        dto.setSituacao(situacao);
        dto.setTotalServicos(BigDecimal.ZERO);
        dto.setDesconto(BigDecimal.ZERO);
        dto.setDeducoes(BigDecimal.ZERO);
        dto.setValorLiquido(BigDecimal.ZERO);
        dto.setBaseCalculo(BigDecimal.ZERO);
        dto.setIssqn(BigDecimal.ZERO);
        dto.setIssqnRetido(BigDecimal.ZERO);
        dto.setQuantidadeNotas(0);

        registros.add(dto);
        return dto;
    }

    public byte[] gerarImpressaoNotaFiscalSistemaNfse(Long idNotaFiscal) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(getConfiguracaoNfse().getUrlRest() + "/publico/notaFiscal/get-bytes/" + idNotaFiscal, byte[].class);
    }

    public void removerNaNota(DeclaracaoPrestacaoServico declaracaoPrestacaoServico) {
        if (TipoDocumentoNfse.NFSE.equals(declaracaoPrestacaoServico.getTipoDocumento())) {
            NotaFiscal notaFiscal = buscarNotaPorDeclaracaoPrestacaoServico(declaracaoPrestacaoServico);
            removerNaNota(notaFiscal);
        }
    }

    public void removerNaNota(NotaFiscal nota) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            if (getConfiguracaoNfse().getUrlRest() != null) {
                restTemplate.delete(getConfiguracaoNfse().getUrlRest() + "/api/publico/nota-fiscal/remove-mongo/" + nota.getId());
            }
        } catch (Exception e) {
            logger.error("Não foi possível remover o cadastro no sistema de nota fiscal: ", e);
        }
    }

    public BigDecimal buscarValorPorNatureza(Integer ano, Exigibilidade exigibilidade) {
        try {
            String sql = " select count(1) " +
                " from NOTAFISCAL nota " +
                "            inner join cadastroeconomico cmc on cmc.id = nota.PRESTADOR_ID " +
                "            inner join DECLARACAOPRESTACAOSERVICO dps on dps.id = nota.DECLARACAOPRESTACAOSERVICO_ID " +
                "            inner join pessoajuridica pj on pj.id = cmc.pessoa_id " +
                "            where dps.NATUREZAOPERACAO = :natureza and extract ( year from nota.EMISSAO) = :ano";
            Query q = em.createNativeQuery(sql);
            q.setParameter("natureza", exigibilidade.name());
            q.setParameter("ano", ano);
            q.setMaxResults(1);
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public List<Object[]> buscarMaioresPessoasEmitentesDeNota(Integer ano) {
        try {
            String sql = "select sum(1), pj.cnpj " +
                "from NOTAFISCAL nota " +
                "       inner join cadastroeconomico cmc on cmc.id = nota.PRESTADOR_ID " +
                "       inner join DECLARACAOPRESTACAOSERVICO dps on dps.id = nota.DECLARACAOPRESTACAOSERVICO_ID " +
                "       inner join pessoajuridica pj on pj.id = cmc.pessoa_id " +
                "where extract(year from nota.EMISSAO) = :ano " +
                "group by pj.cnpj " +
                "order by sum(1) desc";
            Query q = em.createNativeQuery(sql);
            q.setParameter("ano", ano);
            q.setMaxResults(10);
            List<Object[]> retorno = Lists.newArrayList();
            List resultList = q.getResultList();
            for (Object o : resultList) {
                Object[] objeto = (Object[]) o;
                retorno.add(objeto);
            }
            return retorno;
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }


    public BigDecimal buscarTotalDeGuias(Integer ano, Mes mes) {
        try {
            String sql = " select count(1) " +
                "from NOTAFISCAL nota " +
                "       inner join cadastroeconomico cmc on cmc.id = nota.PRESTADOR_ID " +
                "       inner join DECLARACAOPRESTACAOSERVICO dps on dps.id = nota.DECLARACAOPRESTACAOSERVICO_ID " +
                "       inner join pessoajuridica pj on pj.id = cmc.pessoa_id " +
                "       inner join NOTADECLARADA n on dps.ID = n.DECLARACAOPRESTACAOSERVICO_ID " +
                "       inner join DECLARACAOMENSALSERVICO decl on n.DECLARACAOMENSALSERVICO_ID = decl.ID " +
                "where extract(year from decl.ABERTURA) = :ano and extract(month from decl.ABERTURA) = :mes";
            Query q = em.createNativeQuery(sql);
            q.setParameter("ano", ano);
            q.setParameter("mes", mes.getNumeroMes());
            q.setMaxResults(1);
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal buscarValorTotalISS(Integer ano, Mes mes) {
        try {
            String sql = " select sum(dps.ISSCALCULADO) " +
                "from NOTAFISCAL nota " +
                "       inner join cadastroeconomico cmc on cmc.id = nota.PRESTADOR_ID " +
                "       inner join DECLARACAOPRESTACAOSERVICO dps on dps.id = nota.DECLARACAOPRESTACAOSERVICO_ID " +
                "       inner join pessoajuridica pj on pj.id = cmc.pessoa_id " +
                "       inner join NOTADECLARADA n on dps.ID = n.DECLARACAOPRESTACAOSERVICO_ID " +
                "       inner join DECLARACAOMENSALSERVICO decl on n.DECLARACAOMENSALSERVICO_ID = decl.ID " +
                "where extract(year from decl.ABERTURA) = :ano and extract(month from decl.ABERTURA) = :mes";
            Query q = em.createNativeQuery(sql);
            q.setParameter("ano", ano);
            q.setParameter("mes", mes.getNumeroMes());
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal buscarValorTotalDeServico(Integer ano, Mes mes) {
        try {
            String sql = " select sum(dps.TOTALSERVICOS) " +
                "from NOTAFISCAL nota " +
                "       inner join cadastroeconomico cmc on cmc.id = nota.PRESTADOR_ID " +
                "       inner join DECLARACAOPRESTACAOSERVICO dps on dps.id = nota.DECLARACAOPRESTACAOSERVICO_ID " +
                "       inner join pessoajuridica pj on pj.id = cmc.pessoa_id " +
                "       inner join NOTADECLARADA n on dps.ID = n.DECLARACAOPRESTACAOSERVICO_ID " +
                "       inner join DECLARACAOMENSALSERVICO decl on n.DECLARACAOMENSALSERVICO_ID = decl.ID " +
                "where extract(year from decl.ABERTURA) = :ano and extract(month from decl.ABERTURA) = :mes";
            Query q = em.createNativeQuery(sql);
            q.setParameter("ano", ano);
            q.setParameter("mes", mes.getNumeroMes());
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal buscarTotalDeNotas(Integer ano, Mes mes) {
        try {
            String sql = " select count(nota.id) " +
                "from NOTAFISCAL nota " +
                "       inner join cadastroeconomico cmc on cmc.id = nota.PRESTADOR_ID " +
                "       inner join DECLARACAOPRESTACAOSERVICO dps on dps.id = nota.DECLARACAOPRESTACAOSERVICO_ID " +
                "       inner join pessoajuridica pj on pj.id = cmc.pessoa_id " +
                "where extract(year from nota.EMISSAO) = :ano and extract(month from nota.EMISSAO) = :mes";
            Query q = em.createNativeQuery(sql);
            q.setParameter("ano", ano);
            q.setParameter("mes", mes.getNumeroMes());
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public void sequenciarNotasFiscais(List<CadastroEconomico> prestadores, Date emissaoInicial) throws
        Exception {
        for (CadastroEconomico prestador : prestadores) {
            Long numero = null;
            List<NotaFiscalRowMapper> notasFiscais = getNotaFiscalDao().buscarNotasPorPrestadorAndEmissao(prestador.getId(), emissaoInicial);
            for (NotaFiscalRowMapper notaFiscal : notasFiscais) {
                if (numero == null) {
                    numero = notaFiscal.getNumero();
                } else {
                    numero += 1;
                }
                getNotaFiscalDao().updateNumeroNotaFiscal(notaFiscal.getId(), numero);
            }
        }
    }

    public NotaFiscal buscarNotaPorRPS(Long rpsId) {
        Query q = em.createNativeQuery("select nota.id from NotaFiscal nota where nota.rps_id = :id");
        q.setParameter("id", rpsId);
        List<Number> resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return recuperar(resultList.get(0).longValue());
        }
        return null;
    }

    public List<LogGeralNfse> recuperarLogsImpressao(Long id) {
        return em.createQuery("select log from LogGeralNfse log where log.idRelacionamento = :idNota")
            .setParameter("idNota", id).getResultList();
    }


    public LogGeralNfse inserirLogImpressao(Long id) {
        return inserirLogImpressao(id, "Rotina interna do Webpublico");
    }

    public LogGeralNfse inserirLogImpressao(Long id, String local) {
        LogGeralNfse log = new LogGeralNfse();
        log.setConteudo("Impressão de Nota Fiscal");
        log.setDataRegistro(new Date());
        log.setIdRelacionamento(id);
        log.setIp(sistemaFacade.getIp());
        log.setLocal(local);
        log.setTipo(LogGeralNfse.Tipo.IMPRESSAO_NFSE);
        log.setUsuario(sistemaFacade.getUsuarioCorrente().getNome());
        return em.merge(log);
    }

    public List<ItemPlanoContasInternoSearchNfseDTO> buscarContasDesif(Long prestadorId,
                                                                       Integer exercicio,
                                                                       Integer mes) {
        String sql = " SELECT  " +
            "    C.CONTA||' - '||C.DESCRICAO AS COSIF,  " +
            "    IPCI.CONTA AS CONTA,  " +
            "    IPCI.DESCRICAO AS DESCRICAO,  " +
            "    SUM(IDS.VALORSERVICO) AS VALOR_SERVICO,  " +
            "    SUM(IDS.BASECALCULO) AS BASE_CALCULO,  " +
            "    SUM(IDS.ISS) AS ISS  " +
            "   FROM DECLARACAOMENSALSERVICO DMS  " +
            "  INNER JOIN EXERCICIO E ON E.ID = DMS.EXERCICIO_ID  " +
            "  INNER JOIN NOTADECLARADA ND ON ND.DECLARACAOMENSALSERVICO_ID = DMS.ID  " +
            "  INNER JOIN DECLARACAOPRESTACAOSERVICO DEC ON DEC.ID = ND.DECLARACAOPRESTACAOSERVICO_ID  " +
            "  INNER JOIN ITEMDECLARACAOSERVICO IDS ON IDS.DECLARACAOPRESTACAOSERVICO_ID = DEC.ID  " +
            "  LEFT JOIN ITEMPLANOCONTASINTERNO IPCI ON IPCI.ID = IDS.ITEMPLANOCONTASINTERNO_ID  " +
            "  LEFT JOIN PGCC ON PGCC.ID = ids.CONTA_ID " +
            "  INNER JOIN COSIF C ON C.ID in (IPCI.COSIF_ID, PGCC.COSIF_ID ) " +
            "  INNER JOIN CADASTROECONOMICO CE ON CE.ID = DMS.PRESTADOR_ID  " +
            "WHERE CE.ID = :PRESTADOR_ID  " +
            "  AND DMS.SITUACAO != :CANCELADO  " +
            "  AND DMS.TIPOMOVIMENTO = :INSTITUICAO_FINANCEIRA  " +
            "  AND E.ANO = :EXERCICIO  " +
            "  AND DMS.MES = :MES  " +
            "GROUP BY C.CONTA, C.DESCRICAO, IPCI.CONTA, IPCI.DESCRICAO ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("PRESTADOR_ID", prestadorId);
        q.setParameter("CANCELADO", DeclaracaoMensalServico.Situacao.CANCELADO.name());
        q.setParameter("INSTITUICAO_FINANCEIRA", TipoMovimentoMensal.INSTITUICAO_FINANCEIRA.name());
        q.setParameter("EXERCICIO", exercicio);
        q.setParameter("MES", Mes.getMesToInt(mes).name());
        List<Object[]> resultado = q.getResultList();
        List<ItemPlanoContasInternoSearchNfseDTO> retorno = Lists.newArrayList();
        for (Object[] obj : resultado) {
            ItemPlanoContasInternoSearchNfseDTO item = new ItemPlanoContasInternoSearchNfseDTO();
            item.setCosif((String) obj[0]);
            item.setConta((String) obj[1]);
            item.setDescricao((String) obj[2]);
            item.setValorServico(obj[3] != null ? (BigDecimal) obj[3] : BigDecimal.ZERO);
            item.setBaseCalculo(obj[4] != null ? (BigDecimal) obj[4] : BigDecimal.ZERO);
            item.setIssCalculado(obj[5] != null ? (BigDecimal) obj[5] : BigDecimal.ZERO);
            retorno.add(item);
        }
        return retorno;
    }

    public void alterarSituacaoNotaFiscal(DeclaracaoPrestacaoServico declaracaoPrestacaoServico, SituacaoNota situacao) {
        declaracaoPrestacaoServico.setSituacao(situacao);
        em.merge(declaracaoPrestacaoServico);
    }

    public Integer getDiaVencimentoIss() {
        Query query = em.createNativeQuery(" select pfp.diavencimento " +
                "   from configuracaonfse c  " +
                "  inner join configuracaonfsedivida cd on cd.configuracaonfse_id = c.id  " +
                "  inner join opcaopagamentodivida opd on opd.divida_id = cd.dividanfse_id  " +
                "  inner join opcaopagamento op on op.id = opd.opcaopagamento_id  " +
                "  inner join parcela p on p.opcaopagamento_id = op.id  " +
                "  inner join parcelafixaperiodica pfp on pfp.id = p.id  " +
                " where cd.tipomovimentomensal = :tipomovimento " +
                "  and cd.tipodeclaracaomensalservico = :tipodeclaracao  " +
                "  and current_date between coalesce(opd.iniciovigencia, current_date)  " +
                "  and coalesce(opd.finalvigencia, current_date) ")
            .setParameter("tipomovimento", TipoMovimentoMensal.NORMAL.name())
            .setParameter("tipodeclaracao", TipoDeclaracaoMensalServico.PRINCIPAL.name())
            .setMaxResults(1);
        List resultList = query.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return ((Number) resultList.get(0)).intValue();
        }
        throw new ValidacaoException("Dia de vencimento do iss não configurado.");
    }

    public Date buscarVencimentoIss(NotaFiscal notaFiscal) {
        LocalDate localDate = LocalDate
            .fromDateFields(notaFiscal.getDeclaracaoPrestacaoServico().getCompetencia())
            .plusMonths(1)
            .withDayOfMonth(getDiaVencimentoIss());
        return feriadoFacade.proximoDiaUtil(localDate.toDate());
    }

    @Override
    public void salvar(NotaFiscal entity) {
        super.salvar(entity);
        removerNaNota(entity);
    }

    public void calcularValoresNotaFiscal(NotaFiscal notaFiscal) {
        if (notaFiscal.getDeclaracaoPrestacaoServico().getItens() == null || notaFiscal.getDeclaracaoPrestacaoServico().getItens().isEmpty()) {
            return;
        }

        notaFiscal.getDeclaracaoPrestacaoServico().setRetencoesFederais(BigDecimal.ZERO);
        TributosFederais tributosFederais = notaFiscal.getDeclaracaoPrestacaoServico().getTributosFederais();
        if (tributosFederais != null) {
            notaFiscal.getDeclaracaoPrestacaoServico().setRetencoesFederais(tributosFederais.getTotalTributos());
        }

        notaFiscal.getDeclaracaoPrestacaoServico().setTotalServicos(BigDecimal.ZERO);
        notaFiscal.getDeclaracaoPrestacaoServico().setDescontosIncondicionais(BigDecimal.ZERO);
        notaFiscal.getDeclaracaoPrestacaoServico().setDeducoesLegais(BigDecimal.ZERO);
        notaFiscal.getDeclaracaoPrestacaoServico().setBaseCalculo(BigDecimal.ZERO);
        notaFiscal.getDeclaracaoPrestacaoServico().setIssCalculado(BigDecimal.ZERO);
        for (ItemDeclaracaoServico itemDeclaracaoServico : notaFiscal.getDeclaracaoPrestacaoServico().getItens()) {
            BigDecimal valorServico = itemDeclaracaoServico.getValorServico().multiply(itemDeclaracaoServico.getQuantidade());
            notaFiscal.getDeclaracaoPrestacaoServico().setTotalServicos(notaFiscal.getDeclaracaoPrestacaoServico().getTotalServicos().add(valorServico));

            notaFiscal.getDeclaracaoPrestacaoServico().setDeducoesLegais(
                notaFiscal.getDeclaracaoPrestacaoServico().getDeducoesLegais()
                    .add(itemDeclaracaoServico.getDeducoes()));

            notaFiscal.getDeclaracaoPrestacaoServico().setDescontosIncondicionais(
                notaFiscal.getDeclaracaoPrestacaoServico().getDescontosIncondicionais()
                    .add(itemDeclaracaoServico.getDescontosIncondicionados()));

            notaFiscal.getDeclaracaoPrestacaoServico().setBaseCalculo(
                notaFiscal.getDeclaracaoPrestacaoServico().getBaseCalculo()
                    .add(itemDeclaracaoServico.getBaseCalculo()));

            notaFiscal.getDeclaracaoPrestacaoServico().setIssCalculado(
                notaFiscal.getDeclaracaoPrestacaoServico().getIssCalculado()
                    .add(itemDeclaracaoServico.getIss()));
        }

        notaFiscal.getDeclaracaoPrestacaoServico().setTotalNota(notaFiscal.getDeclaracaoPrestacaoServico().getTotalServicos()
            .subtract(notaFiscal.getDeclaracaoPrestacaoServico().getDescontos()));

        notaFiscal.getDeclaracaoPrestacaoServico().setIssPagar(BigDecimal.ZERO);
        if (Exigibilidade.TRIBUTACAO_MUNICIPAL.equals(notaFiscal.getDeclaracaoPrestacaoServico().getNaturezaOperacao()) &&
            !notaFiscal.getDeclaracaoPrestacaoServico().getIssRetido() &&
            !notaFiscal.getPrestador().getEnquadramentoVigente().getSuperSimples()) {
            notaFiscal.getDeclaracaoPrestacaoServico().setIssPagar(notaFiscal.getDeclaracaoPrestacaoServico().getIssCalculado());
        }

        BigDecimal valorRetido = notaFiscal.getDeclaracaoPrestacaoServico().getRetencoesFederais();
        if (notaFiscal.getDeclaracaoPrestacaoServico().getIssRetido() &&
            !Exigibilidade.TRIBUTACAO_FORA_MUNICIPIO.equals(notaFiscal.getDeclaracaoPrestacaoServico().getNaturezaOperacao())) {
            valorRetido = valorRetido.add(notaFiscal.getDeclaracaoPrestacaoServico().getIssCalculado());
        }
        notaFiscal.getDeclaracaoPrestacaoServico().setValorLiquido(
            BigDecimalUtils.arredondar(notaFiscal.getDeclaracaoPrestacaoServico().getTotalNota().subtract(valorRetido), 2));

        arredondarValoresNotaFiscal(notaFiscal);
    }

    private void arredondarValoresNotaFiscal(NotaFiscal notaFiscal) {
        notaFiscal.getDeclaracaoPrestacaoServico().setTotalServicos(BigDecimalUtils.arredondar(notaFiscal.getDeclaracaoPrestacaoServico().getTotalServicos(), 2));
        notaFiscal.getDeclaracaoPrestacaoServico().setDeducoesLegais(BigDecimalUtils.arredondar(notaFiscal.getDeclaracaoPrestacaoServico().getDeducoesLegais(), 2));
        notaFiscal.getDeclaracaoPrestacaoServico().setDescontosCondicionais(BigDecimalUtils.arredondar(notaFiscal.getDeclaracaoPrestacaoServico().getDescontosCondicionais(), 2));
        notaFiscal.getDeclaracaoPrestacaoServico().setDescontosIncondicionais(BigDecimalUtils.arredondar(notaFiscal.getDeclaracaoPrestacaoServico().getDescontosIncondicionais(), 2));
        notaFiscal.getDeclaracaoPrestacaoServico().setValorLiquido(BigDecimalUtils.arredondar(notaFiscal.getDeclaracaoPrestacaoServico().getValorLiquido(), 2));
        notaFiscal.getDeclaracaoPrestacaoServico().setBaseCalculo(BigDecimalUtils.arredondar(notaFiscal.getDeclaracaoPrestacaoServico().getBaseCalculo(), 2));
        notaFiscal.getDeclaracaoPrestacaoServico().setIssPagar(BigDecimalUtils.arredondar(notaFiscal.getDeclaracaoPrestacaoServico().getIssPagar(), 2));
        notaFiscal.getDeclaracaoPrestacaoServico().setIssCalculado(BigDecimalUtils.arredondar(notaFiscal.getDeclaracaoPrestacaoServico().getIssCalculado(), 2));
        TributosFederais tributosFederais = notaFiscal.getDeclaracaoPrestacaoServico().getTributosFederais();
        if (tributosFederais != null) {
            tributosFederais.setCofins(BigDecimalUtils.arredondar(tributosFederais.getCofins(), 2));
            tributosFederais.setCsll(BigDecimalUtils.arredondar(tributosFederais.getCsll(), 2));
            tributosFederais.setInss(BigDecimalUtils.arredondar(tributosFederais.getInss(), 2));
            tributosFederais.setIrrf(BigDecimalUtils.arredondar(tributosFederais.getIrrf(), 2));
            tributosFederais.setOutrasRetencoes(BigDecimalUtils.arredondar(tributosFederais.getOutrasRetencoes(), 2));
            tributosFederais.setPis(BigDecimalUtils.arredondar(tributosFederais.getPis(), 2));
            tributosFederais.setCpp(BigDecimalUtils.arredondar(tributosFederais.getCpp(), 2));
        }
        for (ItemDeclaracaoServico itemDeclaracaoServico : notaFiscal.getDeclaracaoPrestacaoServico().getItens()) {
            itemDeclaracaoServico.setAliquotaServico(BigDecimalUtils.arredondar(itemDeclaracaoServico.getAliquotaServico(), 2));
            itemDeclaracaoServico.setIss(BigDecimalUtils.arredondar(itemDeclaracaoServico.getIss(), 2));
            itemDeclaracaoServico.setBaseCalculo(BigDecimalUtils.arredondar(itemDeclaracaoServico.getBaseCalculo(), 2));
            itemDeclaracaoServico.setValorServico(BigDecimalUtils.arredondar(itemDeclaracaoServico.getValorServico(), 2));
            itemDeclaracaoServico.setDescontosCondicionados(BigDecimalUtils.arredondar(itemDeclaracaoServico.getDescontosCondicionados(), 2));
            itemDeclaracaoServico.setDescontosIncondicionados(BigDecimalUtils.arredondar(itemDeclaracaoServico.getDescontosIncondicionados(), 2));
            itemDeclaracaoServico.setDeducoes(BigDecimalUtils.arredondar(itemDeclaracaoServico.getDeducoes(), 2));
            itemDeclaracaoServico.setQuantidade(BigDecimalUtils.arredondar(itemDeclaracaoServico.getQuantidade(), 2));
            itemDeclaracaoServico.setValorServico(BigDecimalUtils.arredondar(itemDeclaracaoServico.getValorServico(), 2));
        }
    }

    public void calcularValoresItemDeclaracaoServico(ItemDeclaracaoServico itemDeclaracaoServico) {
        itemDeclaracaoServico.setBaseCalculo(BigDecimal.ZERO);
        itemDeclaracaoServico.setIss(BigDecimal.ZERO);

        itemDeclaracaoServico.setBaseCalculo(itemDeclaracaoServico.getQuantidade()
            .multiply(itemDeclaracaoServico.getValorServico())
            .subtract(itemDeclaracaoServico.getDescontosIncondicionados())
            .subtract(itemDeclaracaoServico.getDeducoes()));

        itemDeclaracaoServico.setIss(itemDeclaracaoServico
            .getBaseCalculo()
            .multiply(itemDeclaracaoServico.getAliquotaServico().divide(new BigDecimal("100")))
            .setScale(2, RoundingMode.HALF_UP));
    }

    public static final String getSelectNotaFiscalSearch() {
        return " select " +
            "       coalesce(nf.id, sd.id) as id, " +
            "       dec.id as id_declaracao, " +
            "       coalesce(nf.prestador_id, " +
            "           case " +
            "               when sd.tiposervicodeclarado = '" + TipoServicoDeclarado.SERVICO_PRESTADO.name() + "' then sd.cadastroeconomico_id " +
            "           end) as id_prestador, " +
            "       coalesce(nf.tomador_id, " +
            "                case " +
            "                    when sd.tiposervicodeclarado = 'SERVICO_TOMADO' then sd.cadastroeconomico_id " +
            "                end) as id_tomador, " +
            "       nf.rps_id as id_rps, " +
            "       rps.numero as codigo_rps, " +
            "       coalesce(nf.numero, sd.numero) as numero, " +
            "       coalesce(nf.emissao, sd.emissao) as emissao, " +
            "       dec.competencia as competencia, " +
            "       dpt.nomerazaosocial as nome_tomador, " +
            "       dpp.nomerazaosocial as nome_prestador, " +
            "       dpt.cpfcnpj as cpfcnpj_tomador, " +
            "       dpp.cpfcnpj as cpfcnpj_prestador, " +
            "       dec.situacao as situacao, " +
            "       dec.modalidade as  modalidade, " +
            "       coalesce(dec.issretido, 0) as iss_retido, " +
            "       coalesce(dec.totalservicos, 0) as total_servicos, " +
            "       coalesce(dec.descontoscondicionais, 0) + coalesce(dec.descontosincondicionais, 0) as descontos, " +
            "       coalesce(dec.deducoeslegais, 0) as deducoes, " +
            "       coalesce(ids.aliquotaservico, 0) as aliquota, " +
            "       coalesce(dec.valorliquido, 0) as valor_liquido, " +
            "       coalesce(dec.basecalculo, 0) as base_calculo, " +
            "       coalesce(dec.isspagar, 0) as iss, " +
            "       coalesce(dec.isscalculado, 0) as iss_calculado, " +
            "       dec.tipodocumento as tipo_documento, " +
            "       dec.naturezaoperacao as natureza_operacao, " +
            "       nf.descriminacaoservico as discriminacao_servico, " +
            "       rps.serie as serie_rps, " +
            "       rps.tiporps as tipo_rps, " +
            "       nf.codigoverificacao as codigo_verificacao ";
    }

    public static final String getFromNotaFiscalSearch() {
        return "   from declaracaoprestacaoservico dec " +
            "  inner join itemdeclaracaoservico ids on ids.declaracaoprestacaoservico_id = dec.id " +
            "  inner join servico s on s.id = ids.servico_id " +
            "  left join dadospessoaisnfse dpp on dpp.id = dec.dadospessoaisprestador_id " +
            "  left join dadospessoaisnfse dpt on dpt.id = dec.dadospessoaistomador_id " +
            "  left join notafiscal nf on nf.declaracaoprestacaoservico_id = dec.id " +
            "  left join rps on rps.id = nf.rps_id " +
            "  left join servicodeclarado sd on sd.declaracaoprestacaoservico_id = dec.id ";
    }

    public NotaFiscalSearchDTO buscarNotaFiscalOrServicoDeclarado(CancelamentoDeclaracaoPrestacaoServico.TipoDocumento tipoDocumento,
                                                                  CadastroEconomico cadastroEconomico,
                                                                  Long numero) {
        StringBuilder sql = new StringBuilder();
        sql.append(getSelectNotaFiscalSearch()).append(getFromNotaFiscalSearch());
        if (CancelamentoDeclaracaoPrestacaoServico.TipoDocumento.NOTA_FISCAL.equals(tipoDocumento)) {
            sql.append(" where nf.prestador_id = :cadastro_economico ");
            sql.append(" and nf.numero = :numero ");
        } else {
            sql.append(" where sd.cadastroeconomico_id = :cadastro_economico ");
            sql.append(" and sd.numero = :numero ");
        }
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("cadastro_economico", cadastroEconomico.getId());
        q.setParameter("numero", numero);

        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return NotaFiscalSearchDTO.toNotaFiscalSearchDTO((Object[]) resultList.get(0));
        }
        return null;
    }
}

