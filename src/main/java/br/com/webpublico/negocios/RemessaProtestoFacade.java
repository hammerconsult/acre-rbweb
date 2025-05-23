package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOCdaProtesto;
import br.com.webpublico.entidadesauxiliares.VODamRemessaFile;
import br.com.webpublico.entidadesauxiliares.VOEndereco;
import br.com.webpublico.entidadesauxiliares.VOPessoa;
import br.com.webpublico.enums.SituacaoCertidaoDA;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.enums.TipoLogradouroEnderecoCorreio;
import br.com.webpublico.enums.tributario.LogRemessaProtesto;
import br.com.webpublico.enums.tributario.SituacaoRemessaProtesto;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.LeitorWsConfig;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JDBCProtestoDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaRemessaProtesto;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.itextpdf.html2pdf.HtmlConverter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Stateless
public class RemessaProtestoFacade extends AbstractFacade<RemessaProtesto> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private LeitorWsConfig leitorWsConfig;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private CancelamentoParcelamentoFacade cancelamentoParcelamentoFacade;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private SingletonConcorrenciaRemessaProtesto singletonConcorrenciaRemessaProtesto;

    private JDBCProtestoDAO protestoDAO;

    public RemessaProtestoFacade() {
        super(RemessaProtesto.class);
    }

    @PostConstruct
    public void init() {
        protestoDAO = Util.recuperarSpringBean(JDBCProtestoDAO.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public JDBCProtestoDAO getProtestoDAO() {
        return protestoDAO;
    }

    public ConsultaDebitoFacade getConsultaDebitoFacade() {
        return consultaDebitoFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public CertidaoDividaAtivaFacade getCertidaoDividaAtivaFacade() {
        return certidaoDividaAtivaFacade;
    }

    public SingletonConcorrenciaRemessaProtesto getSingletonConcorrenciaRemessaProtesto() {
        return singletonConcorrenciaRemessaProtesto;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public RemessaProtesto recuperar(Object id) {
        RemessaProtesto remessa = em.find(RemessaProtesto.class, id);
        Hibernate.initialize(remessa.getParcelas());
        Hibernate.initialize(remessa.getCdas());
        Hibernate.initialize(remessa.getLogs());
        for (LogRemessaProtesto log : remessa.getLogs()) {
            Hibernate.initialize(log.getValidacoes());
        }
        return remessa;
    }

    public RetornoRemessaProtesto recuperarRetornoRemessa(Long idRetornoRemessa) {
        return em.find(RetornoRemessaProtesto.class, idRetornoRemessa);
    }

    public UsuarioSistema recuperarUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public Exercicio recuperarExercicioCorrente() {
        return sistemaFacade.getExercicioCorrente();
    }

    public Long gerarSequenciaRemessa() {
        try {
            String sql = " select coalesce(remessa.sequencia, 0) " +
                " from remessaprotesto remessa " +
                " order by coalesce(sequencia, 0) desc fetch first 1 rows only ";

            Query q = em.createNativeQuery(sql);

            List<BigDecimal> sequencias = q.getResultList();

            if (sequencias != null && !sequencias.isEmpty() && sequencias.get(0) != null) {
                return sequencias.get(0).longValue() + 1;
            }
        } catch (Exception e) {
            logger.error("Erro ao recuperar sequecia de remessa. ", e);
            return 1L;
        }
        return configuracaoTributarioFacade.retornaUltimo().getSeqIncialRemessaProtesto();
    }

    public List<CdaRemessaProtesto> buscarCdasParaEnvioDeRemessa() {
        return buscarCdasParaEnvioDeRemessa(null, null, Lists.<Divida>newArrayList());
    }

    public List<CdaRemessaProtesto> buscarCdasParaEnvioDeRemessa(Date dataInicial, Date dataFinal, List<Divida> dividas) {
        List<CdaRemessaProtesto> cdasProtesto = Lists.newArrayList();
        String sql = "select cda.id as idCda, pp.codigo || '/' || ex.ano as codigo, " +
            "  case when cmc.id is not null then cmc.inscricaocadastral " +
            "       when ci.id is not null then ci.inscricaocadastral " +
            "       when pes.id is not null then " +
            "  coalesce(pf.nome, pj.razaosocial) || ' (' || coalesce(pf.cpf, pj.cnpj) || ')' end as cadastro " +
            "from CDAPROCESSODEPROTESTO cpp " +
            "inner join PROCESSODEPROTESTO pp on pp.id = cpp.PROCESSODEPROTESTO_ID " +
            "inner join CertidaoDividaAtiva cda on cda.id = cpp.cda_id " +
            "inner join exercicio ex on pp.exercicio_id = ex.id " +
            "left join cadastroeconomico cmc on pp.cadastroprotesto_id = cmc.id " +
            "left join cadastroimobiliario ci on pp.cadastroprotesto_id = ci.id " +
            "left join pessoa pes on pp.pessoaprotesto_id = pes.id " +
            "left join pessoafisica pf on pes.id = pf.id " +
            "left join pessoajuridica pj on pes.id = pj.id " +
            "where pp.SITUACAO = :situacaoProcesso ";
        if (dataInicial != null && dataFinal != null) {
            sql += " and trunc(pp.lancamento) between to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy') "
                + " and to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy') ";
        }
        if (!dividas.isEmpty()) {
            sql += " and exists (select item.id from ItemInscricaoDividaAtiva item " +
                "               inner join ItemCertidaoDividaAtiva icda on icda.itemInscricaoDividaAtiva_id = item.id " +
                "               inner join ValorDivida vd on vd.calculo_id = item.id " +
                "               where icda.certidao_id = cda.id and vd.divida_id in (:dividas))";
        }
        sql += " and cpp.cda_id not in (select parc.CDA_ID from remessaprotesto remessa " +
            "                            inner join parcelaremessaprotesto parc on remessa.id = parc.remessaprotesto_id " +
            "                            where remessa.situacaoRemessa = :situacaoRemessa " +
            "                              and coalesce(parc.situacaoProtesto,'') in (:situacaoProtesto)) " +
            " and cpp.cda_id not in (select parc.CDA_ID from remessaprotesto remessa " +
            "                         inner join cdaremessaprotesto parc on remessa.id = parc.remessaprotesto_id" +
            "                         where remessa.situacaoRemessa = :situacaoRemessa" +
            "                           and coalesce(parc.situacaoProtesto,'') in (:situacaoProtesto)) " +
            " and cda.pessoa_id not in (select pes.id from pessoa pes " +
            "                             left join pessoafisica pf on pes.id = pf.id " +
            "                             left join pessoajuridica pj on pes.id = pj.id " +
            "                            where pes.id = cda.pessoa_id " +
            "                              and valida_cpf_cnpj(coalesce(pf.cpf, pj.cnpj)) = 'N')";

        Query q = em.createNativeQuery(sql);
        q.setParameter("situacaoProcesso", SituacaoProcessoDebito.FINALIZADO.name());
        q.setParameter("situacaoRemessa", SituacaoRemessaProtesto.ENVIADO.name());
        q.setParameter("situacaoProtesto", Lists.newArrayList("", "Aguardando protocolização", "Protestado", "Em processo de intimação", "Pago", "Retirado"));
        if (!dividas.isEmpty()) {
            List<Long> idsDividas = Lists.newArrayList();
            for (Divida divida : dividas) {
                idsDividas.add(divida.getId());
            }
            q.setParameter("dividas", idsDividas);
        }
        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                CdaRemessaProtesto cdaProtesto = new CdaRemessaProtesto();
                cdaProtesto.setCda(em.find(CertidaoDividaAtiva.class, ((BigDecimal) obj[0]).longValue()));
                cdaProtesto.setCodigoProcesso((String) obj[1]);
                cdaProtesto.setCadastroProcesso((String) obj[2]);
                cdaProtesto.setNossoNumero(cdaProtesto.getCda().getNumeroCdaComExercicio());
                Hibernate.initialize(cdaProtesto.getCda().getItensCertidaoDividaAtiva());
                cdasProtesto.add(cdaProtesto);
            }
        }

        return cdasProtesto;
    }

    public List<ParcelaRemessaProtesto> buscarParcelasParaEnvioDeRemessa(Date dataInicial, Date dataFinal) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DEBITO_PROTESTADO, ConsultaParcela.Operador.IGUAL, 1);
        consultaParcela.addComplementoJoin(" inner join itemprocessodeprotesto ipp on vw.parcela_id = ipp.parcela_id ");
        consultaParcela.addComplementoJoin(" inner join processodeprotesto pp on ipp.processodeprotesto_id = pp.id ");

        if (dataInicial != null && dataFinal != null) {
            consultaParcela.addComplementoDoWhere(" and trunc(pp.lancamento) between to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy') "
                + " and to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy') ");
        }
        consultaParcela.addComplementoDoWhere(" and vw.parcela_id not in (select parc.parcelavalordivida_id from remessaprotesto remessa " +
            "                                   inner join parcelaremessaprotesto parc on remessa.id = parc.remessaprotesto_id) ");

        consultaParcela.addComplementoDoWhere(" and vw.pessoa_id not in (select pes.id from pessoa pes " +
            "                                   left join pessoafisica pf on pes.id = pf.id " +
            "                                   left join pessoajuridica pj on pes.id = pj.id " +
            "                                   where pes.id = vw.pessoa_id " +
            "                                   and valida_cpf_cnpj(coalesce(pf.cpf, pj.cnpj)) = 'N') ");

        List<ResultadoParcela> parcelas = consultaParcela.executaConsulta().getResultados();
        List<ParcelaRemessaProtesto> parcelasProtesto = Lists.newArrayList();

        for (ResultadoParcela parcela : parcelas) {
            ParcelaRemessaProtesto parcelaProtesto = new ParcelaRemessaProtesto();
            parcelaProtesto.setResultadoParcela(parcela);
            parcelaProtesto = preencherDadosProcessoDeProtesto(parcela.getIdParcela(), parcelaProtesto);

            parcelasProtesto.add(parcelaProtesto);
        }

        return parcelasProtesto;
    }

    public ParcelaRemessaProtesto preencherDadosProcessoDeProtesto(Long idParcela, ParcelaRemessaProtesto parcelaProtesto) {
        String sql = " select item.parcela_id as idParcela, pp.codigo || '/' || ex.ano as codigo, " +
            "                 case when cmc.id is not null then cmc.inscricaocadastral " +
            "                      when ci.id is not null then ci.inscricaocadastral " +
            "                      when pes.id is not null then " +
            "                      coalesce(pf.nome, pj.razaosocial) || ' (' || coalesce(pf.cpf, pj.cnpj) || ')' end as cadastro " +
            " from processodeprotesto pp " +
            " inner join itemprocessodeprotesto item on pp.id = item.processodeprotesto_id " +
            " inner join exercicio ex on pp.exercicio_id = ex.id " +
            " left join cadastroeconomico cmc on pp.cadastroprotesto_id = cmc.id " +
            " left join cadastroimobiliario ci on pp.cadastroprotesto_id = ci.id " +
            " left join pessoa pes on pp.pessoaprotesto_id = pes.id " +
            " left join pessoafisica pf on pes.id = pf.id " +
            " left join pessoajuridica pj on pes.id = pj.id " +
            " where item.parcela_id = :idParcela " +
            " and pp.situacao = :finalizado ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", idParcela);
        q.setParameter("finalizado", SituacaoProcessoDebito.FINALIZADO.name());

        List<Object[]> retorno = q.getResultList();

        if (retorno != null && !retorno.isEmpty()) {
            Object[] obj = retorno.get(0);

            parcelaProtesto.setParcelaValorDivida(em.find(ParcelaValorDivida.class, ((BigDecimal) obj[0]).longValue()));
            parcelaProtesto.setCodigoProcesso((String) obj[1]);
            parcelaProtesto.setCadastroProcesso((String) obj[2]);
            if (parcelaProtesto.getResultadoParcela().getTipoCalculoEnumValue().isCancelamentoParcelamento()) {
                Long idParcelaCda = buscarIdParcelaOriginalDaParcelaDoCancelamento(idParcela);
                if (idParcelaCda != null) {
                    List<CertidaoDividaAtiva> cdas = buscarCertidoesDividaAtivaDaParcela(idParcelaCda);
                    if (!cdas.isEmpty()) {
                        parcelaProtesto.setNossoNumero(cdas.get(0).getNumeroCdaComExercicio());
                        parcelaProtesto.setCda(cdas.get(0));
                    }
                }
            } else {
                parcelaProtesto.setCda(buscarCDAParcela(idParcela));
                if (parcelaProtesto.getCda() != null) {
                    parcelaProtesto.setNossoNumero(parcelaProtesto.getCda().getNumeroCdaComExercicio());
                } else {
                    parcelaProtesto.setNossoNumero("");
                }
            }

        }
        return parcelaProtesto;
    }

    public CdaRemessaProtesto preencherDadosProcessoDeProtesto(Long idCda, CdaRemessaProtesto cdaProtesto) {
        String sql = " select item.cda_id as idCda, pp.codigo || '/' || ex.ano as codigo, " +
            "                 case when cmc.id is not null then cmc.inscricaocadastral " +
            "                      when ci.id is not null then ci.inscricaocadastral " +
            "                      when pes.id is not null then " +
            "                      coalesce(pf.nome, pj.razaosocial) || ' (' || coalesce(pf.cpf, pj.cnpj) || ')' end as cadastro " +
            " from processodeprotesto pp " +
            " inner join itemprocessodeprotesto item on pp.id = item.processodeprotesto_id " +
            " inner join exercicio ex on pp.exercicio_id = ex.id " +
            " left join cadastroeconomico cmc on pp.cadastroprotesto_id = cmc.id " +
            " left join cadastroimobiliario ci on pp.cadastroprotesto_id = ci.id " +
            " left join pessoa pes on pp.pessoaprotesto_id = pes.id " +
            " left join pessoafisica pf on pes.id = pf.id " +
            " left join pessoajuridica pj on pes.id = pj.id " +
            " where item.cda_id = :idCda " +
            " and pp.situacao = :finalizado ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCda", idCda);
        q.setParameter("finalizado", SituacaoProcessoDebito.FINALIZADO.name());

        List<Object[]> retorno = q.getResultList();

        if (retorno != null && !retorno.isEmpty()) {
            Object[] obj = retorno.get(0);

            cdaProtesto.setCda(em.find(CertidaoDividaAtiva.class, ((BigDecimal) obj[0]).longValue()));
            cdaProtesto.setCodigoProcesso((String) obj[1]);
            cdaProtesto.setCadastroProcesso((String) obj[2]);
            if (cdaProtesto.getCda() != null) {
                cdaProtesto.setNossoNumero(cdaProtesto.getCda().getNumeroCdaComExercicio());
            } else {
                cdaProtesto.setNossoNumero("");
            }
        }
        return cdaProtesto;
    }


    public String buscarValorTag(String xml, String tag) {
        try {
            if (!StringUtils.isBlank(xml)) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(xml)));
                return document.getElementsByTagName(tag).item(0).getFirstChild().getNodeValue();
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }


    public String formatarData(Date data) {
        if (data != null) {
            return formatarData(data, "ddMMyyyy");
        }
        return "";
    }

    public String formatarData(Date data, String pattern) {
        if (data != null) {
            return DataUtil.getDataFormatada(data, pattern);
        }
        return "";
    }

    public VOPessoa buscarInformacoesContribuinte(CertidaoDividaAtiva cda) {
        if (cda != null) {
            Long idPessoa = null;
            if (cda.getTipoCadastroTributario().isImobiliario()) {
                List<Propriedade> propriedades = cadastroImobiliarioFacade.recuperarProprietariosVigentes((CadastroImobiliario) cda.getCadastro());
                idPessoa = propriedades.get(0).getPessoa().getId();
            }

            if (cda.getPessoa() != null || idPessoa != null) {
                String sql = "select coalesce(pf.nome, pj.razaoSocial) as nome, coalesce(pf.cpf, pj.cnpj) as cpfCnpj," +
                    "                case when pf.id is not null then 'F' when pj.id is not null then 'J' end as tipoPessoa, " +
                    "                vw.logradouro, vw.numero, vw.bairro, vw.cep, vw.tipoLogradouro, vw.localidade, vw.uf" +
                    " from Pessoa pes " +
                    " left join PessoaFisica pf on pes.id = pf.id " +
                    " left join PessoaJuridica pj on pes.id = pj.id " +
                    " left join VWEnderecoPessoa vw on pes.id = vw.pessoa_id " +
                    " where pes.id = :idPessoa" +
                    " order by coalesce(vw.endereco_id, pes.id) desc fetch first 1 rows only ";

                Query q = em.createNativeQuery(sql);
                q.setParameter("idPessoa", idPessoa != null ? idPessoa : cda.getPessoa().getId());
                List<Object[]> dadosPessoa = q.getResultList();

                if (dadosPessoa != null && !dadosPessoa.isEmpty() && dadosPessoa.get(0) != null) {
                    Object[] dados = dadosPessoa.get(0);

                    VOPessoa pessoa = new VOPessoa();
                    pessoa.setNome(dados[0] != null ? (String) dados[0] : "");
                    pessoa.setCpfCnpj(dados[1] != null ? (String) dados[1] : "");
                    pessoa.setTipoPessoa(dados[2] != null ? ((Character) dados[2]).toString() : "");

                    VOEndereco endereco = new VOEndereco();
                    endereco.setLogradouro(dados[3] != null ? (String) dados[3] : "");
                    endereco.setNumero(dados[4] != null ? (String) dados[4] : "");
                    endereco.setBairro(dados[5] != null ? (String) dados[5] : "");
                    endereco.setCep(dados[6] != null ? (String) dados[6] : "");
                    endereco.setTipoLogradouro(buscarTipoLogradouro((String) dados[7]));
                    endereco.setLocalidade(dados[8] != null ? (String) dados[8] : "");
                    endereco.setUf(dados[9] != null ? (String) dados[9] : "");

                    pessoa.setEndereco(endereco);

                    return pessoa;
                }
            }
        }
        return null;
    }

    public VOPessoa buscarInformacoesSacador() {
        String sql = " select pj.razaosocial,  pj.cnpj, " +
            "                 vwe.tipologradouro, vwe.logradouro, vwe.numero, " +
            "                 vwe.bairro, vwe.cep, vwe.localidade, vwe.uf " +
            " from vwhierarquiaadministrativa vw " +
            " inner join hierarquiaorganizacional ho on ho.id = vw.id " +
            " inner join entidade e on vw.entidade_id = e.id " +
            " inner join pessoajuridica pj on e.pessoajuridica_id = pj.id " +
            " inner join vwenderecopessoa vwe on pj.id = vwe.pessoa_id " +
            " where vw.superior_id is null" +
            " and to_date(:dataAtual, 'dd/MM/yyyy') between vw.iniciovigencia " +
            " and coalesce(vw.fimvigencia, to_date(:dataAtual, 'dd/MM/yyyy')) " +
            " order by ho.codigo fetch first 1 rows only ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("dataAtual", DataUtil.getDataFormatada(new Date()));

        List<Object[]> dadosPessoa = q.getResultList();

        if (dadosPessoa != null && !dadosPessoa.isEmpty() && dadosPessoa.get(0) != null) {
            Object[] dados = dadosPessoa.get(0);

            VOPessoa pessoa = new VOPessoa();
            pessoa.setNome(dados[0] != null ? (String) dados[0] : "");
            pessoa.setCpfCnpj(dados[1] != null ? (String) dados[1] : "");

            VOEndereco endereco = new VOEndereco();
            endereco.setTipoLogradouro(buscarTipoLogradouro((String) dados[2]));
            endereco.setLogradouro(dados[3] != null ? (String) dados[3] : "");
            endereco.setNumero(dados[4] != null ? (String) dados[4] : "");
            endereco.setBairro(dados[5] != null ? (String) dados[5] : "");
            endereco.setCep(dados[6] != null ? (String) dados[6] : "");
            endereco.setLocalidade(dados[7] != null ? (String) dados[7] : "");
            endereco.setUf(dados[8] != null ? (String) dados[8] : "");

            pessoa.setEndereco(endereco);

            return pessoa;
        }
        return new VOPessoa();
    }

    private TipoLogradouroEnderecoCorreio buscarTipoLogradouro(String tipoLogradouro) {
        try {
            if (!StringUtils.isBlank(tipoLogradouro)) {
                return TipoLogradouroEnderecoCorreio.valueOf(tipoLogradouro);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public String buscarEnderecoPessoa(VOEndereco endereco) {
        try {
            String enderecoCompleto = "";
            if (endereco != null) {
                if (endereco.getTipoLogradouro() != null) {
                    enderecoCompleto += StringUtil.removeAcentuacao(endereco.getTipoLogradouro().getDescricao()) + " ";
                }
                enderecoCompleto += endereco.getLogradouro() != null ? (StringUtil.removeAcentuacao(endereco.getLogradouro()) + " ") : "";
                enderecoCompleto += endereco.getNumero() != null ? (", " + StringUtil.removeAcentuacao(endereco.getNumero()) + " ") : "";
                enderecoCompleto += endereco.getBairro() != null ? ("- " + StringUtil.removeAcentuacao(endereco.getBairro()) + "") : "";
            }
            return enderecoCompleto.toUpperCase();
        } catch (Exception e) {
            logger.error("Erro ao buscar endereco da pessoa para remessa de protesto. ", e);
            return "";
        }
    }

    public DAM gerarDamAgrupadoParaParcelasDaCda(List<ResultadoParcela> parcelas, AssistenteBarraProgresso assistente) {
        try {
            Calendar c = DataUtil.ultimoDiaMes(new Date());
            if (parcelas.size() > 1) {
                List<DAM> damsAgrupados = damFacade.buscarDamsAgrupadosDaParcela(parcelas.get(0).getIdParcela());
                if (damsAgrupados.isEmpty() || !verificarTotalDamAgrupadoComParcelas(damsAgrupados.get(0), parcelas)) {
                    c = DataUtil.ultimoDiaUtil(c, feriadoFacade);
                    Date vencimentoDam = c.getTime();
                    return damFacade.gerarDamAgrupado(parcelas, vencimentoDam, assistente.getUsuarioSistema());
                }
                return damsAgrupados.get(0);
            } else {
                ResultadoParcela parcela = parcelas.get(0);
                DAM dam = damFacade.recuperaDAMPeloIdParcela(parcela.getIdParcela());
                if (dam == null) {
                    Date vencimentoDam = parcela.isVencido(new Date()) ? c.getTime() : parcela.getVencimento();
                    vencimentoDam = DataUtil.ajustarDataUtil(vencimentoDam, feriadoFacade);
                    dam = damFacade.gerarDAM(new ParcelaValorDivida(parcela.getIdParcela()), vencimentoDam, assistente.getExercicio(),
                        true, assistente.getUsuarioSistema());
                }
                return dam;
            }
        } catch (Exception e) {
            logger.error("Erro ao recuperar dam para remessa de protesto. ", e);
            return null;
        }
    }

    private boolean verificarTotalDamAgrupadoComParcelas(DAM dam, List<ResultadoParcela> parcelas) {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            valorTotal = valorTotal.add(parcela.getValorTotal());
        }
        return valorTotal.compareTo(dam.getValorTotal()) == 0;
    }

    public String gerarBase64AnexosProtesto(CertidaoDividaAtiva cda, List<ResultadoParcela> parcelas, AssistenteBarraProgresso assistente, Map<Long, VODamRemessaFile> mapaDamPorCDA) {
        try {
            if (cda != null) {
                if (!mapaDamPorCDA.containsKey(cda.getId())) {
                    List<ResultadoParcela> parcelasCda = Lists.newArrayList();
                    for (ResultadoParcela parcela : parcelas) {
                        if (!parcelasCda.contains(parcela)) {
                            parcelasCda.add(parcela);
                        }
                    }
                    mapaDamPorCDA.put(cda.getId(), new VODamRemessaFile(gerarDamAgrupadoParaParcelasDaCda(parcelasCda, assistente)));
                    mapaDamPorCDA.get(cda.getId()).setParcelasCda(parcelasCda);
                    try {
                        cda = certidaoDividaAtivaFacade.gerarDocumento(cda, assistente.getUsuarioSistema(), assistente.getExercicio(), assistente.getIp());
                    } catch (AtributosNulosException | UFMException e) {
                        logger.error("Erro ao gerar o documento da CDA: ", e);
                    }
                    if (cda.getDocumentoOficial() != null && cda.getDocumentoOficial().getConteudo() != null) {
                        String conteudo = certidaoDividaAtivaFacade.getDocumentoOficialFacade().geraConteudoDocumento(cda.getDocumentoOficial());
                        File pdfCda = gerarPdfAnexo(null, conteudo, "cda" + cda.getNumero() + cda.getExercicio().getAno(), true);
                        mapaDamPorCDA.get(cda.getId()).setFileCda(pdfCda);
                    }

                    ByteArrayOutputStream byteArray;
                    if (DAM.Tipo.COMPOSTO.equals(mapaDamPorCDA.get(cda.getId()).getDam().getTipo())) {
                        logger.error("Vai gerar o byteArray do DAM Agrupado: " + mapaDamPorCDA.get(cda.getId()).getDam().getNumeroCompleto());
                        byteArray = consultaDebitoFacade.gerarImpressaoDAMAgrupado(mapaDamPorCDA.get(cda.getId()).getDam(), HistoricoImpressaoDAM.TipoImpressao.SISTEMA, parcelasCda, assistente.getUsuarioSistema(), false);
                    } else {
                        logger.error("Vai gerar o byteArray do DAM Unico: " + mapaDamPorCDA.get(cda.getId()).getDam().getNumeroCompleto());
                        byteArray = consultaDebitoFacade.gerarImpressaoDAM(Lists.newArrayList(mapaDamPorCDA.get(cda.getId()).getDam()), assistente.getUsuarioSistema(), false);
                    }
                    File pdfDam = gerarPdfAnexo(byteArray.toByteArray(), null, ("dam" + mapaDamPorCDA.get(cda.getId()).getDam().getNumeroCompleto()), false);
                    mapaDamPorCDA.get(cda.getId()).setFileDam(pdfDam);
                }

                byte[] buffer = new byte[1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                List<File> arquivos = Lists.newArrayList(mapaDamPorCDA.get(cda.getId()).getFileCda(), mapaDamPorCDA.get(cda.getId()).getFileDam());
                logger.error(arquivos.size() + " Vai gerar os arquivos pra CDA: " + cda.getNumeroCdaComExercicio());
                try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                    for (File pdf : arquivos) {
                        try (FileInputStream fis = new FileInputStream(pdf)) {
                            logger.error("Arquivo: " + pdf.getName());
                            zos.putNextEntry(new ZipEntry(pdf.getName()));
                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, length);
                            }
                            zos.closeEntry();
                        }
                    }
                }
                for (File arquivo : arquivos) {
                    arquivo.deleteOnExit();
                }
                return Base64.encodeBase64String(baos.toByteArray());
            }
        } catch (Exception e) {
            logger.error("Erro ao gerar base64 dos anexos da remessa de protesto. ", e);
        }

        return "";
    }

    private File gerarPdfAnexo(byte[] bytes, String conteudo, String pathname, boolean isHtml) {
        File pdf = null;
        try {
            pdf = new File(pathname + ".pdf");
            if (!isHtml) {
                try (FileOutputStream fos = new FileOutputStream(pdf)) {
                    fos.write(bytes);
                    fos.flush();
                }
            } else {
                try (ByteArrayOutputStream fos = new ByteArrayOutputStream(conteudo.length())) {
                    HtmlConverter.convertToPdf(conteudo, fos);
                    FileUtils.writeByteArrayToFile(pdf, fos.toByteArray());
                    fos.flush();
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao criar pdf pathname : {} {}", pathname, e);
        }
        return pdf;
    }

    public void atualizarSituacaoDeProtestoDasParcelas() {
        try {
            atualizarSituacoes(null, null, false);
        } catch (ValidacaoException ve) {
            logger.error("Erro de validação na remessa: ", ve);
        }
    }

    public void atualizarSituacoes(final Long idRemessa, String usuario, boolean correcao) {
        List<VOCdaProtesto> cdasProtesto = buscarParcelasRemessaProtesto(idRemessa);
        try {
            consultarSituacaoDeProtestoDasParcelas(cdasProtesto);
            protestoDAO.atualizarSituacaoProtestoParcelas(cdasProtesto, usuario, correcao);
        } catch (Exception e) {
            protestoDAO.inserirLogsCDAs(cdasProtesto, usuario, "Erro: " + e.getMessage());
        }
    }

    public boolean validarConfiguracaoWsProtesto(ConfiguracaoWebService configuracaoWs, boolean mensagem) {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoWs == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para enviar a remessa de protesto é necessário uma configuração de conexão, " +
                "confira nas configurações do tributário, aba WebServices se existe essa configuração.");
        } else {
            if (StringUtils.isBlank(configuracaoWs.getUrl())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para conexão com o CRA é necessário informar a URL de conexão nas configuração do tributário, aba WebServices");
            }
            if (StringUtils.isBlank(configuracaoWs.getUsuario())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para conexão com o CRA é necessário informar o usuário de conexão nas configuração do tributário, aba WebServices");
            }
            if (StringUtils.isBlank(configuracaoWs.getSenha())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para conexão com o CRA é necessário informar a senha de conexão nas configuração do tributário, aba WebServices");
            }
            if (StringUtils.isBlank(configuracaoWs.getDetalhe())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para conexão com o CRA é necessário informar a URL do integrador nas configuração do tributário, aba WebServices, campo detalhes");
            }
        }
        if (mensagem) {
            ve.lancarException();
        }
        return ve.getMensagens().isEmpty();
    }

    public void consultarSituacaoDeProtestoDasParcelas(List<VOCdaProtesto> cdasProtesto) throws ValidacaoException {
        try {
            ConfiguracaoWebService configuracaoWs = leitorWsConfig.getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.PROTESTO);
            if (!validarConfiguracaoWsProtesto(configuracaoWs, false)) {
                throw new ValidacaoException("Configuração do serviço de envio de remessa de protesto não encontrada.");
            }

            String url = configuracaoWs.getDetalhe() + "/consultar-situacao-protestos" +
                "?usuario=" + configuracaoWs.getUsuario() +
                "&senha=" + configuracaoWs.getSenha() +
                "&urlCra=" + configuracaoWs.getUrl();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<List> response = restTemplate.postForEntity(url, cdasProtesto, List.class);
            List body = response.getBody();
            for (Object parcela : body) {
                VOCdaProtesto voCdaProtesto = new ObjectMapper().convertValue(parcela, VOCdaProtesto.class);
                cdasProtesto.stream()
                    .filter((cda) -> cda.getNossoNumero().equals(voCdaProtesto.getNossoNumero()))
                    .findFirst()
                    .ifPresent((cda) -> cda.setSituacaoParcela(voCdaProtesto.getSituacaoParcela()));
            }
        } catch (ValidacaoException ve) {
            throw ve;
        } catch (RestClientException ce) {
            logger.error("Erro ao conectar com o serviço 'consultar-situacao-protestos' de integração de protestos. ", ce);
            throw new ValidacaoException("Erro ao conectar com o serviço de envio de remessa de protesto.");
        } catch (Exception e) {
            logger.error("Erro ao consultar situacoes dos protestos. ", e);
            throw new ValidacaoException("Erro inesperado, contate o suporte.");
        }
    }

    public boolean validarConfiguracaoTributaria(ConfiguracaoTributario configuracaoTributario, boolean mensagem) {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoTributario == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrada Configuração Tributária vigente.");
        } else {
            if (StringUtils.isBlank(configuracaoTributario.getCodigoApresentante())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Código do Apresentante na aba Protesto de Débitos das Configurações do Tributário " +
                    "deve ser informado.");
            }
            if (StringUtils.isBlank(configuracaoTributario.getNomeApresentante())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Nome do Apresentante na aba Protesto de Débitos das Configurações do Tributário " +
                    "deve ser informado.");
            }
        }
        if (mensagem) {
            ve.lancarException();
        }
        return !ve.temMensagens();
    }

    public void consultarRetornoRemessa() {
        try {
            ConfiguracaoTributario config = configuracaoTributarioFacade.retornaUltimo();
            if (validarConfiguracaoTributaria(config, false)) {

                String nomeArquivoResposta = "R" + config.getCodigoApresentante() + formatarData(new Date(), "ddMM.yy") + "1";
                ConfiguracaoWebService configuracaoWs = leitorWsConfig.getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.PROTESTO);
                if (!validarConfiguracaoWsProtesto(configuracaoWs, false)) {
                    return;
                }
                String url = configuracaoWs.getDetalhe() + "/consultar-retorno" +
                    "?nomeArquivo=" + nomeArquivoResposta +
                    "&usuario=" + configuracaoWs.getUsuario() +
                    "&senha=" + configuracaoWs.getSenha() +
                    "&urlCra=" + configuracaoWs.getUrl();

                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

                String xmlRetorno = response.getBody();

                if (!isXmlRetornoContemCodigoErro(xmlRetorno) && isXmlRetornoContemTagRetorno(xmlRetorno)) {
                    RetornoRemessaProtesto retornoRemessa = buscarRetornoDataAtual();

                    if (retornoRemessa == null) {
                        retornoRemessa = new RetornoRemessaProtesto();
                        retornoRemessa.setNomeArquivo(nomeArquivoResposta);
                    }
                    retornoRemessa.setDataArquivo(new Date());
                    retornoRemessa.setXmlRetorno(xmlRetorno);

                    em.merge(retornoRemessa);
                }
            } else {
                logger.error("Não foi possível consultar Retorno Remessa!");
            }
        } catch (RestClientException ce) {
            logger.error("Erro ao conectar com o serviço 'consultar-retorno' de integração de protestos. ", ce);
        } catch (Exception e) {
            logger.error("Erro ao recuperar resposta de envio da remessa. ", e);
        }
    }

    private boolean isXmlRetornoContemCodigoErro(String xml) {
        return !StringUtils.isBlank(buscarValorTag(xml, "codigo"));
    }

    private boolean isXmlRetornoContemTagRetorno(String xml) {
        return !StringUtils.isBlank(buscarValorTag(xml, "retorno"));
    }

    public Long buscarNumeroDeRemessasNaDataAtual() {
        String sql = " select distinct remessa.id from remessaprotesto remessa" +
            " where trunc(remessa.envioremessa) = to_date(:dataAtual, 'dd/MM/yyyy') ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("dataAtual", DataUtil.getDataFormatada(new Date()));

        List<BigDecimal> ids = q.getResultList();
        return (ids != null && !ids.isEmpty()) ? ids.size() : 0L;
    }

    private RetornoRemessaProtesto buscarRetornoDataAtual() {
        String sql = " select retorno.* from retornoremessaprotesto retorno " +
            " where trunc(retorno.dataarquivo) = to_date(:dataAtual, 'dd/MM/yyyy') ";

        Query q = em.createNativeQuery(sql, RetornoRemessaProtesto.class);
        q.setParameter("dataAtual", DataUtil.getDataFormatada(new Date()));

        List<RetornoRemessaProtesto> retornos = q.getResultList();
        return (retornos != null && !retornos.isEmpty()) ? retornos.get(0) : null;
    }

    private List<VOCdaProtesto> buscarParcelasRemessaProtesto(Long idRemessa) {
        String sql = " select prp.id as id_cdaprotesto, " +
            "                 prp.nossonumero, " +
            "                 cda.id, " +
            "                 cda.numero || '/' || ex.ano," +
            "                 prp.situacaoprotesto " +
            "   from cdaremessaprotesto prp " +
            " inner join remessaprotesto remessa on prp.remessaprotesto_id = remessa.id " +
            " inner join certidaodividaativa cda on cda.id = prp.cda_id " +
            " inner join exercicio ex on ex.id = cda.exercicio_id " +
            " where remessa.situacaoremessa = :enviado ";
        sql += (idRemessa != null ? " and remessa.id = :idRemessa " : "");

        Query q = em.createNativeQuery(sql);
        q.setParameter("enviado", SituacaoRemessaProtesto.ENVIADO.name());
        if (idRemessa != null) {
            q.setParameter("idRemessa", idRemessa);
        }

        List<Object[]> cdas = q.getResultList();
        Set<VOCdaProtesto> cdasProtesto = Sets.newHashSet();

        if (cdas != null && !cdas.isEmpty()) {
            for (Object[] cda : cdas) {
                if (cda[0] != null && cda[1] != null) {
                    cdasProtesto.add(new VOCdaProtesto(((BigDecimal) cda[0]).longValue(),
                        (String) cda[1], ((BigDecimal) cda[2]).longValue(), (String) cda[3],
                        (String) cda[4]));
                }
            }
        }
        return Lists.newArrayList(cdasProtesto);
    }

    public CertidaoDividaAtiva buscarCDAParcela(Long idParcela) {
        String sql = " select cda.* " +
            " from certidaodividaativa cda " +
            " inner join itemcertidaodividaativa icda on icda.certidao_id = cda.id " +
            " inner join valordivida vd on vd.calculo_id = icda.iteminscricaodividaativa_id " +
            " inner join parcelavalordivida pvd on pvd.valorDivida_id = vd.id " +
            " inner join exercicio ex on cda.exercicio_id = ex.id " +
            " where pvd.id = :idParcela " +
            " and cda.situacaocertidaoda <> :cancelada ";
        Query q = em.createNativeQuery(sql, CertidaoDividaAtiva.class);
        q.setParameter("idParcela", idParcela);
        q.setParameter("cancelada", SituacaoCertidaoDA.CANCELADA.name());
        List<CertidaoDividaAtiva> cdas = q.getResultList();
        if (cdas != null && !cdas.isEmpty()) {
            return cdas.get(0);
        }
        return null;
    }

    private CertidaoDividaAtiva buscarCDAPelaParcela(Long idParcela) {
        String sql = " select cda.* " +
            " from certidaodividaativa cda " +
            " inner join itemcertidaodividaativa icda on icda.certidao_id = cda.id " +
            " inner join valordivida vd on vd.calculo_id = icda.iteminscricaodividaativa_id " +
            " inner join parcelavalordivida pvd on pvd.valorDivida_id = vd.id " +
            " inner join exercicio ex on cda.exercicio_id = ex.id " +
            " where pvd.id = :idParcela " +
            " and cda.situacaocertidaoda <> :cancelada ";

        Query q = em.createNativeQuery(sql, CertidaoDividaAtiva.class);
        q.setParameter("idParcela", idParcela);
        q.setParameter("cancelada", SituacaoCertidaoDA.CANCELADA.name());

        List<CertidaoDividaAtiva> cdas = q.getResultList();
        return (cdas != null && !cdas.isEmpty()) ? cdas.get(0) : null;
    }

    public Long buscarIdParcelaOriginalDaParcelaDoCancelamento(Long idParcela) {
        return cancelamentoParcelamentoFacade.buscarIdParcelaOriginalDaParcelaDoCancelamento(idParcela);
    }

    public List<CertidaoDividaAtiva> buscarCertidoesDividaAtivaDaParcela(Long idParcela) {
        return certidaoDividaAtivaFacade.buscarCertidoesDividaAtivaDaParcela(idParcela);
    }

    public List<ResultadoParcela> buscarParcelasEmAbertoDaCDA(CertidaoDividaAtiva cda) {
        List<ResultadoParcela> parcelas = certidaoDividaAtivaFacade.recuperaParcelasDaCertidaoDaView(cda);
        List<ResultadoParcela> parcelasEmAberto = Lists.newArrayList();
        for (ResultadoParcela parcela : parcelas) {
            if (parcela.isEmAberto()) {
                parcelasEmAberto.add(parcela);
            }
        }
        return parcelasEmAberto;
    }

    public List<LogCdaRemessaProtesto> buscarLogsCDA(CdaRemessaProtesto cda) {
        return em.createQuery("from LogCdaRemessaProtesto log " +
            " where log.cdaRemessaProtesto = :cda" +
            " order by log.dataRegistro ")
            .setParameter("cda", cda)
            .getResultList();
    }
}
