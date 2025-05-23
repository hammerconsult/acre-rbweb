/*
 * Codigo gerado automaticamente em Mon Feb 28 16:58:30 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.SituacaoEconomicoCNAE.Situacao;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.AssistenteRelatorioBCE;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.negocios.comum.UsuarioWebFacade;
import br.com.webpublico.negocios.tributario.AuditoriaFacade;
import br.com.webpublico.negocios.tributario.dao.JdbcCadastroEconomicoDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoCadastroEconomico;
import br.com.webpublico.nfse.domain.ConfiguracaoNfse;
import br.com.webpublico.nfse.domain.TributosFederais;
import br.com.webpublico.nfse.domain.UserNfseCadastroEconomico;
import br.com.webpublico.nfse.domain.dtos.Operador;
import br.com.webpublico.nfse.domain.dtos.*;
import br.com.webpublico.nfse.domain.dtos.enums.PermissaoUsuarioEmpresaNfse;
import br.com.webpublico.nfse.facades.AnexoLei1232006Facade;
import br.com.webpublico.nfse.facades.ConfiguracaoNfseFacade;
import br.com.webpublico.nfse.util.GeradorQuery;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.tributario.dto.EventoRedeSimDTO;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.hibernate.Hibernate;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class CadastroEconomicoFacade extends AbstractFacade<CadastroEconomico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EscritorioContabilFacade escritorioContabilFacade;
    @EJB
    private TributoFacade tributoFacade;
    @EJB
    private TipoDocumentoFiscalFacade tipoDocumentoFiscalFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @EJB
    private AtributoFacade atributoFacade;
    @EJB
    private ServicoFacade servicoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private CNAEFacade cnaeFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private VistoriaFiscalizacaoFacade vistoriaFiscalizacaoFacade;
    @EJB
    private CadastroAidfFacade cadastroAidfFacade;
    @EJB
    private TipoAutonomoFacade tipoAutonomoFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private HistoricoFacade historicoFacade;
    @EJB
    private CaracteristicasFuncionamentoFacede caracteristicasFuncionamentoFacede;
    @EJB
    private HorarioFuncionamentoFacade horarioFuncionamentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private HistoricoLegadoFacade historicoLegadoFacade;
    @EJB
    private NaturezaJuridicaFacade naturezaJuridicaFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private CEPFacade cepFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private PermissaoTransporteFacade permissaoTransporteFacade;
    @EJB
    private AuditoriaFacade auditoriaFacade;
    @EJB
    private UsuarioWebFacade usuarioWebFacade;
    @EJB
    private SingletonGeradorCodigoCadastroEconomico singletonGeradorCodigo;
    @EJB
    private ConfiguracaoNfseFacade configuracaoNfseFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private CalculoAlvaraFacade calculoAlvaraFacade;
    private WebApplicationContext ap;
    private JdbcCadastroEconomicoDAO jdbcCadastroEconomicoDAO;
    @EJB
    private IntegracaoRedeSimFacade integracaoRedeSimFacade;
    @EJB
    private ParametroETRFacade parametroETRFacade;
    @EJB
    private RequerimentoLicenciamentoETRFacade requerimentoLicenciamentoETRFacade;
    @EJB
    private HistoricoInscricaoCadastralFacade historicoInscricaoCadastralFacade;

    public CadastroEconomicoFacade() {
        super(CadastroEconomico.class);
    }

    @PostConstruct
    private void init() {
        this.ap = ContextLoader.getCurrentWebApplicationContext();
        this.jdbcCadastroEconomicoDAO = (JdbcCadastroEconomicoDAO) this.ap.getBean("cadastroEconomicoDAO");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public CadastroEconomico recuperar(Object id) {
        CadastroEconomico ce = em.find(CadastroEconomico.class, id);
        if (ce != null) {
            inicializarDependencias(ce);
        }
        return ce;
    }

    public AuditoriaFacade getAuditoriaFacade() {
        return auditoriaFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        CadastroEconomico ce = (CadastroEconomico) em.find(entidade, id);
        if (ce != null) {
            inicializarDependencias(ce);
        }
        return ce;
    }

    public CadastroEconomico recuperarParaPesquisaGenerica(Object id) {
        CadastroEconomico ce = em.find(CadastroEconomico.class, id);
        if (ce != null) {
            ce.getEconomicoCNAE().size();
            ce.getEconomicoCNAEVigentes();
            ce.getSituacaoCadastroEconomico().size();
            ce.setSituacaoAtual(ce.getSituacaoAtual());
        }
        return ce;
    }

    public void inicializarDependencias(CadastroEconomico ce) {
        Hibernate.initialize(ce.getEconomicoCNAE());
        Collections.sort(ce.getEconomicoCNAE());
        Hibernate.initialize(ce.getServico());
        Hibernate.initialize(ce.getRegistroJuntas());
        Hibernate.initialize(ce.getSociedadeCadastroEconomico());
        Hibernate.initialize(ce.getSituacaoCadastroEconomico());
        Hibernate.initialize(ce.getTipoProcessoEconomico());
        Hibernate.initialize(ce.getArquivos());
        Hibernate.initialize(ce.getHistoricos());
        Hibernate.initialize(ce.getListaBCECaracFuncionamento());
        Hibernate.initialize(ce.getPlacas());
        Hibernate.initialize(ce.getIsencoes());
        Hibernate.initialize(ce.getListaEnderecoCadastroEconomico());
        Hibernate.initialize(ce.getPessoa().getEnderecos());
        Hibernate.initialize(ce.getPessoa().getTelefones());
        Hibernate.initialize(ce.getEnquadramentos());
        Hibernate.initialize(ce.getEnquadramentosAmbientais());
        Hibernate.initialize(ce.getUsuariosNota());
        Hibernate.initialize(ce.getPessoa().getPessoaCNAE());
        Hibernate.initialize(ce.getPessoa().getHorariosFuncionamento());
        for (PessoaHorarioFuncionamento pessoaHorarioFuncionamento : ce.getPessoa().getHorariosFuncionamento()) {
            Hibernate.initialize(pessoaHorarioFuncionamento.getHorarioFuncionamento().getItens());
        }
        if (ce.getPessoa() instanceof PessoaFisica) {
            Hibernate.initialize(((PessoaFisica) ce.getPessoa()).getDocumentosPessoais());
            Hibernate.initialize(((PessoaFisica) ce.getPessoa()).getConselhoClasseContratos());
            Hibernate.initialize(((PessoaFisica) ce.getPessoa()).getFormacoes());
            Hibernate.initialize(((PessoaFisica) ce.getPessoa()).getHabilidades());
            Hibernate.initialize(((PessoaFisica) ce.getPessoa()).getDependentes());
        }
    }

    public CadastroEconomico recuperarParaAlvara(Long id) {
        CadastroEconomico ce = em.find(CadastroEconomico.class, id);
        if (ce != null) {
            ce.getEconomicoCNAE().size();
            ce.getListaBCECaracFuncionamento().size();
            ce.getListaEnderecoCadastroEconomico().size();
            ce.getEnquadramentos().size();
        }
        return ce;
    }

    public CadastroEconomico recuperarSimples(Object id) {
        return em.find(CadastroEconomico.class, id);
    }

    public void carregarLista(List list) {
        try {
            list.size();
        } catch (Exception e) {
            //nothing here
        }
    }

    public CadastroEconomico recuperaPorInscricao(String codigo) {
        String hql = "from CadastroEconomico ce where ce.inscricaoCadastral = :inscricao";
        Query q = em.createQuery(hql);
        q.setParameter("inscricao", codigo);
        List<CadastroEconomico> lista = q.getResultList();
        if (!lista.isEmpty()) {
            CadastroEconomico ce = (CadastroEconomico) lista.get(0);
            inicializarObjetoNoHibernate(ce.getEconomicoCNAE());
            inicializarObjetoNoHibernate(ce.getServico());
            inicializarObjetoNoHibernate(ce.getRegistroJuntas());
            inicializarObjetoNoHibernate(ce.getSociedadeCadastroEconomico());
            inicializarObjetoNoHibernate(ce.getSituacaoCadastroEconomico());
            inicializarObjetoNoHibernate(ce.getTipoProcessoEconomico());
            inicializarObjetoNoHibernate(ce.getArquivos());
            inicializarObjetoNoHibernate(ce.getHistoricos());
            inicializarObjetoNoHibernate(ce.getListaBCECaracFuncionamento());
            inicializarObjetoNoHibernate(ce.getPlacas());
            inicializarObjetoNoHibernate(ce.getIsencoes());
            inicializarObjetoNoHibernate(ce.getListaEnderecoCadastroEconomico());
            inicializarObjetoNoHibernate(ce.getEnquadramentos());
            return ce;
        }
        return null;
    }

    public CadastroEconomico salvar(CadastroEconomico cadastroEconomico, List<FileUploadEvent> files) {
        try {
            salvarArquivos(cadastroEconomico, files);
            cadastroEconomico = update(cadastroEconomico);
        } catch (Exception e) {
            logger.error("Erro ao alterar o cadastro economico no salvar: ", e);
        }
        return cadastroEconomico;
    }

    @Override
    public CadastroEconomico salvarRetornando(CadastroEconomico entity) {
        return update(entity);
    }

    @Override
    public void salvar(CadastroEconomico entity) {
        update(entity);
    }

    public void salvarOficioMEI(CadastroEconomico cadastroEconomico) {
        em.merge(cadastroEconomico);
    }

    private CadastroEconomico update(CadastroEconomico cadastroEconomico) {
        try {
            cadastroEconomico = em.merge(cadastroEconomico);
            removerNaNota(sistemaFacade.getUsuarioCorrente(), cadastroEconomico);
        } catch (Exception e) {
            logger.error("Erro ao alterar o cadastro economico no update: ", e);
        }
        return cadastroEconomico;
    }

    public void removerNaNota(UsuarioSistema usuarioSistema, CadastroEconomico cadastroEconomico) {
        AsyncExecutor.getInstance().execute(new AssistenteBarraProgresso(usuarioSistema, "Removendo cmc do cache da nfs-e", 0),
            () -> {
                if (cadastroEconomico != null) {
                    SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
                    clientHttpRequestFactory.setConnectTimeout(10_000);
                    clientHttpRequestFactory.setReadTimeout(10_000);
                    RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
                    ConfiguracaoNfse configuracaoNfse = configuracaoNfseFacade.recuperarConfiguracao();
                    if (configuracaoNfse.getUrlRest() != null) {
                        restTemplate.delete(configuracaoNfse.getUrlRest() +
                            "/api/publico/prestador-servico/remove-mongo/" + cadastroEconomico.getId());
                    }
                }
                return null;
            });
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void criarUsuarioWeb(CadastroEconomico cadastroEconomico) {
        if (usuarioWebFacade.recuperarUsuarioPorLogin(StringUtil.retornaApenasNumeros(cadastroEconomico.getPessoa().getCpf_Cnpj())) == null) {
            usuarioWebFacade.criarUsuarioWeb(cadastroEconomico, "senha10", Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);
        }
    }

    public void atualizarLocalizacaoDoCadastroEconomicoComEnderecoPrincipalPessoa(CadastroEconomico cadastroEconomico, Pessoa pessoa) {
        try {
            cadastroEconomico.getLocalizacao().setNumero(pessoa.getEnderecoPrincipal().getNumero());
            cadastroEconomico.getLocalizacao().setComplemento(pessoa.getEnderecoPrincipal().getComplemento());
            cadastroEconomico.getLocalizacao().setCep(pessoa.getEnderecoPrincipal().getCep());
            cadastroEconomico.getLocalizacao().setLogradouro(pessoa.getEnderecoPrincipal().getLogradouro());
            cadastroEconomico.getLocalizacao().setBairro(pessoa.getEnderecoPrincipal().getBairro());
            cadastroEconomico.getLocalizacao().setLocalidade(pessoa.getEnderecoPrincipal().getLocalidade());
            cadastroEconomico.getLocalizacao().setUf(pessoa.getEnderecoPrincipal().getUf());
        } catch (Exception e) {
            logger.error("Erro ao atualizar o endereço de localização do cmc. {}", e.getMessage());
            logger.debug("Detalhes do erro ao atualizar o endereço de localização do cmc. ", e);
        }
    }

    public void atualizarCnaesDoCadastroEconomicoComOsCnaesDaPessoaJuridica(CadastroEconomico cadastroEconomico, PessoaJuridica pessoaJuridica) {
        try {
            if (cadastroEconomico != null) {
                removerCnaesDoCadastroEconomicoNaoExistentesNaPessoaJuridica(cadastroEconomico, pessoaJuridica);
                adicionarCnaesDaPessoaNaoExistentesNoCadastroEconomico(cadastroEconomico, pessoaJuridica);
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar os cnaes do cmc. {}", e.getMessage());
            logger.debug("Detalhes do erro ao atualizar os cnaes do cmc. ", e);
        }
    }

    private void removerCnaesDoCadastroEconomicoNaoExistentesNaPessoaJuridica(CadastroEconomico cadastroEconomico, PessoaJuridica pessoaJuridica) {
        cadastroEconomico.getEconomicoCNAE().removeIf(economicoCNAE -> pessoaJuridica.getPessoaCNAEAtivos().stream()
            .noneMatch(pessoaCNAE -> pessoaCNAE.getCnae().equals(economicoCNAE.getCnae())));
    }

    private void adicionarCnaesDaPessoaNaoExistentesNoCadastroEconomico(CadastroEconomico cadastroEconomico, PessoaJuridica pessoaJuridica) {
        for (PessoaCNAE pessoaCNAE : pessoaJuridica.getPessoaCNAEAtivos()) {
            if (cadastroEconomico.getEconomicoCNAE().stream().noneMatch(economicoCNAE -> economicoCNAE.getCnae().equals(pessoaCNAE.getCnae()))) {
                EconomicoCNAE economicoCNAE = new EconomicoCNAE();
                economicoCNAE.setCadastroEconomico(cadastroEconomico);
                economicoCNAE.setCnae(pessoaCNAE.getCnae());
                economicoCNAE.setTipo(pessoaCNAE.getTipo());
                economicoCNAE.setDataregistro(pessoaCNAE.getDataregistro());
                economicoCNAE.setInicio(pessoaCNAE.getInicio());
                economicoCNAE.setFim(pessoaCNAE.getFim());
                economicoCNAE.setHorarioFuncionamento(pessoaCNAE.getHorarioFuncionamento());
                economicoCNAE.setExercidaNoLocal(pessoaCNAE.getExercidaNoLocal());
                cadastroEconomico.getEconomicoCNAE().add(economicoCNAE);
            }
        }
    }

    public void atualizarSociedadeDoCadastroEconomicoComSociedadeDaPessoaJuridica(CadastroEconomico cadastroEconomico,
                                                                                  PessoaJuridica pessoaJuridica) {
        try {
            if (cadastroEconomico == null || pessoaJuridica == null
                || pessoaJuridica.getSociedadePessoaJuridica() == null) return;
            removerSocioDoCadastroEconomicoQueNaoExisteNaPessoaJuridica(cadastroEconomico, pessoaJuridica);
            adicionarSocioNoCadastroEconomicoComSocioDaPessoaJuridica(cadastroEconomico, pessoaJuridica);
        } catch (Exception e) {
            logger.error("Erro ao atualizar a sociedade do cmc. {}", e.getMessage());
            logger.debug("Detalhes do erro ao atualizar a sociedade do cmc. ", e);
        }
    }

    private void removerSocioDoCadastroEconomicoQueNaoExisteNaPessoaJuridica(CadastroEconomico cadastroEconomico,
                                                                             PessoaJuridica pessoaJuridica) {
        cadastroEconomico.getSociedadeCadastroEconomico().removeIf(sociedadeCadastroEconomico ->
            pessoaJuridica.getSociedadePessoaJuridica().stream().noneMatch(sociedadePessoaJuridica ->
                sociedadePessoaJuridica.getSocio().equals(sociedadeCadastroEconomico.getSocio())));
    }

    private void adicionarSocioNoCadastroEconomicoComSocioDaPessoaJuridica(CadastroEconomico cadastroEconomico, PessoaJuridica pessoaJuridica) {
        for (SociedadePessoaJuridica sociedadePessoaJuridica : pessoaJuridica.getSociedadePessoaJuridica()) {
            SociedadeCadastroEconomico sociedadeCadastroEconomico = cadastroEconomico.getSociedadeCadastroEconomico()
                .stream().filter(sce ->
                    sce.getSocio().equals(sociedadePessoaJuridica.getSocio()))
                .findFirst()
                .orElse(null);
            if (sociedadeCadastroEconomico == null) {
                sociedadeCadastroEconomico = new SociedadeCadastroEconomico();
                sociedadeCadastroEconomico.setCadastroEconomico(cadastroEconomico);
                sociedadeCadastroEconomico.setSocio(sociedadePessoaJuridica.getSocio());
                sociedadeCadastroEconomico.setDataRegistro(sociedadePessoaJuridica.getDataRegistro());
                sociedadeCadastroEconomico.setDataInicio(sociedadePessoaJuridica.getDataInicio());
                cadastroEconomico.getSociedadeCadastroEconomico().add(sociedadeCadastroEconomico);
            }
            sociedadeCadastroEconomico.setProporcao(sociedadePessoaJuridica.getProporcao());
            sociedadeCadastroEconomico.setDataFim(sociedadePessoaJuridica.getDataFim());
        }
    }

    public void adicionarServicos(CadastroEconomico cadastroEconomico, PessoaJuridica pessoaJuridica) {
        List<PessoaCNAE> pessoaCnaes = pessoaJuridica.getPessoaCNAEAtivos();
        if (pessoaCnaes != null && !pessoaCnaes.isEmpty()) {
            for (PessoaCNAE pessoaCnae : pessoaCnaes) {
                CNAE cnae = cnaeFacade.recuperar(pessoaCnae.getCnae().getId());
                if (cnae.getServicos() != null && !cnae.getServicos().isEmpty()) {
                    for (CNAEServico cnaeServico : cnae.getServicos()) {
                        if (!cadastroEconomico.temServico(cnaeServico.getServico())) {
                            cadastroEconomico.adicionarServico(cnaeServico.getServico());
                        }
                    }
                }
            }
        }
    }

    private void salvarArquivos(CadastroEconomico entity, List<FileUploadEvent> files) {
        try {
            for (FileUploadEvent event : files) {
                UploadedFile arquivoRecebido = event.getFile();
                Arquivo arq = new Arquivo();
                arq.setNome(arquivoRecebido.getFileName());
                //arq.setMimeType(arquivoRecebido.getContentType()); //No prime 2 não funciona
                arq.setMimeType(arquivoFacade.getMimeType(arquivoRecebido.getFileName()));
                arq.setTamanho(arquivoRecebido.getSize());
                entity.getArquivos().add(arq);
                arquivoFacade.novoArquivo(arq, arquivoRecebido.getInputstream());
                arquivoRecebido.getInputstream().close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<CadastroEconomico> listaCadastroEconomicoPorPessoa(String parte) {
        StringBuilder hql = new StringBuilder(" select c from CadastroEconomico c ")
            .append(" where c.pessoa in (select pf from PessoaFisica pf ")
            .append("                    where lower(pf.nome) like :parte ")
            .append("                     or lower(pf.cpf) like :parte ")
            .append("                     or (replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like :parte)) ")
            .append("    or c.pessoa in (select pj from PessoaJuridica pj ")
            .append("                    where lower(pj.razaoSocial) like :parte ")
            .append("                     or lower(pj.nomeFantasia) like :parte ")
            .append("                     or lower(pj.cnpj) like :parte ")
            .append("                     or (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :parte)) ")
            .append(" or c.inscricaoCadastral like :parte ")
            .append(" and c.abertura = (select max(abertura) from CadastroEconomico ce where ce.pessoa = c.pessoa) ");
        Query q = getEntityManager().createQuery(hql.toString());
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    @Override
    public List<CadastroEconomico> lista() {
        return em.createQuery("from CadastroEconomico").getResultList();
    }

    public boolean existeBcePorPessoa(CadastroEconomico ce) {
        if (ce == null || ce.getPessoa() == null || ce.getPessoa().getId() == null) {
            return false;
        }
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ce.id, ");
        sql.append("ce.inscricaocadastral, ");
        sql.append("sce.situacaocadastral ");
        sql.append("FROM cadastroeconomico ce ");
        sql.append("INNER JOIN ce_situacaocadastral ce_sit ");
        sql.append("ON ce_sit.cadastroeconomico_id = ce.id ");
        sql.append("INNER JOIN situacaocadastroeconomico sce ");
        sql.append("ON sce.id = ce_sit.situacaocadastroeconomico_id ");
        sql.append("WHERE ce.pessoa_id = :pessoaID ");
        if (ce.getId() != null) {
            sql.append("AND ce.id <> :ceID ");
        }
        sql.append("AND ((sce.situacaocadastral = '").append(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name()).append("' ");
        sql.append(" OR sce.situacaocadastral = '").append(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name()).append("') ");
        sql.append("AND sce.dataregistro = ");
        sql.append("  (SELECT max(sc.dataregistro) ");
        sql.append("  FROM cadastroeconomico cadeco ");
        sql.append("  INNER JOIN ce_situacaocadastral cad_situ ");
        sql.append("  ON cad_situ.cadastroeconomico_id = cadeco.id ");
        sql.append("  INNER JOIN situacaocadastroeconomico sc ");
        sql.append("  ON sc.id                 = cad_situ.situacaocadastroeconomico_id ");
        sql.append("  WHERE cadeco.pessoa_id   = :pessoaID ");
        sql.append("  and cadeco.id = ce.id ");
        sql.append("  ))");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("pessoaID", ce.getPessoa().getId());
        if (ce.getId() != null) {
            q.setParameter("ceID", ce.getId());
        }
        new Util().imprimeSQL(sql.toString(), q);
        return !q.getResultList().isEmpty();
    }

    public List<CadastroEconomico> listaPorFiltro(int inicio, int max, String filtroCI, String filtroCnae, String filtroCpfCnpj, String filtroPessoa, String filtroProcesso, SituacaoCadastralCadastroEconomico situacaoCadastral, ClassificacaoAtividade filtroClassificacao, String endereco, String nomeFantasia) {
        StringBuilder sql = new StringBuilder("select ce.* ")
            .append(" from cadastroeconomico ce ")
            .append(" inner join cadastro cadastro on cadastro.ID = ce.ID ")
            .append(" left join pessoafisica pf on ce.PESSOA_ID = pf.ID ")
            .append(" left join PESSOAJURIDICA pj on ce.PESSOA_ID = pj.ID ")
            .append(" left join vwenderecopessoa vwendereco on ce.pessoa_id = vwendereco.pessoa_id ");
        String juncao = " where ";
        if (filtroCnae != null && !filtroCnae.isEmpty()) {
            sql.append(juncao).append(" (ce.ID in (select ecnae.CADASTROECONOMICO_ID " +
                "                from economicocnae ecnae " +
                "                         inner join cnae c on ecnae.CNAE_ID = c.ID " +
                "                where replace(replace(replace(c.CODIGOCNAE, '/', ''), '-', ''), '.', '') like :filtroCnae)) ");
            juncao = " and ";
        }
        if (filtroProcesso != null && !filtroProcesso.isEmpty()) {
            sql.append(juncao).append(" (ce.ID in (select processo.CADASTROECONOMICO_ID " +
                "                 from TIPOPROCESSOECONOMICO processo " +
                "                 where processo.NUMEROPROCESSO like :filtroProcesso)) ");
            juncao = " and ";
        }
        if (situacaoCadastral != null) {
            sql.append(juncao).append("'").append(situacaoCadastral.name())
                .append("' = (select MAX(situacao.SITUACAOCADASTRAL) " +
                    "                 from SITUACAOCADASTROECONOMICO SITUACAO " +
                    "                          INNER JOIN CE_SITUACAOCADASTRAL CESIT ON SITUACAO.ID = CESIT.SITUACAOCADASTROECONOMICO_ID " +
                    "                 where CESIT.CADASTROECONOMICO_ID = CE.ID) ");
            juncao = " and ";
        }
        if (filtroCI != null && !filtroCI.isEmpty()) {
            sql.append(juncao).append(" ce.inscricaoCadastral like :filtroCI");
            juncao = " and ";
        }
        if (filtroClassificacao != null) {
            sql.append(juncao).append(" ce.classificacaoAtividade = '").append(filtroClassificacao.name()).append("' ");
            juncao = " and ";
        }
        if (nomeFantasia != null && !nomeFantasia.isEmpty()) {
            sql.append(juncao).append(" (ce.ID in (select sociedade.CADASTROECONOMICO_ID " +
                    "                from sociedadeCadastroEconomico sociedade " +
                    "                         left join pessoafisica pfsocio on sociedade.SOCIO_ID = pfsocio.ID " +
                    "                         left join PESSOAJURIDICA pjsocio on sociedade.SOCIO_ID = pjsocio.ID " +
                    "                where lower(coalesce(pfsocio.NOME, pjsocio.NOMEFANTASIA)) like :nomeFantasia) ")
                .append(" or coalesce(pf.NOME, pj.NOMEFANTASIA) like :nomeFantasia) ");
            juncao = " and ";
        }
        if (filtroPessoa != null && !filtroPessoa.isEmpty()) {
            sql.append(juncao).append(" (ce.ID in (select sociedade.CADASTROECONOMICO_ID " +
                    "                from sociedadeCadastroEconomico sociedade " +
                    "                         left join pessoafisica pfsocio on sociedade.SOCIO_ID = pfsocio.ID " +
                    "                         left join PESSOAJURIDICA pjsocio on sociedade.SOCIO_ID = pjsocio.ID " +
                    "                where lower(coalesce(pfsocio.NOME, pjsocio.RAZAOSOCIAL)) like :filtroPessoa) ")
                .append(" or coalesce(pf.NOME, pj.RAZAOSOCIAL) like :filtroPessoa) ");
            juncao = " and ";
        }
        if (endereco != null && !endereco.isEmpty()) {
            sql.append(juncao).append(" (lower(vwendereco.tipologradouro || ' ' || vwendereco.logradouro) like :endereco) ")
                .append(" or lower(vwendereco.complemento) like :endereco ")
                .append(" or lower(vwendereco.bairro) like :endereco ")
                .append(" or lower(vwendereco.numero) like :endereco)");
            juncao = " and ";
        }
        if (filtroCpfCnpj != null && !filtroCpfCnpj.isEmpty()) {
            sql.append(juncao).append(" (ce.ID in (select sociedade.CADASTROECONOMICO_ID " +
                    "                from sociedadeCadastroEconomico sociedade " +
                    "                         left join pessoafisica pfsocio on sociedade.SOCIO_ID = pfsocio.ID " +
                    "                         left join PESSOAJURIDICA pjsocio on sociedade.SOCIO_ID = pjsocio.ID " +
                    "                where replace(replace(replace(coalesce(pfsocio.CPF, pjsocio.CNPJ), '/', ''), '-', ''), '.', '') like :filtroCpfCnpj) ")
                .append(" or replace(replace(replace(coalesce(pf.CPF, pj.CNPJ), '/', ''), '-', ''), '.', '') like :filtroCpfCnpj) ");
            juncao = " and ";
        }
        Query q = em.createNativeQuery(sql.toString(), CadastroEconomico.class);
        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        if (filtroCI != null && !filtroCI.isEmpty()) {
            q.setParameter("filtroCI", "%" + filtroCI.trim() + "%");
        }
        if (filtroPessoa != null && !filtroPessoa.isEmpty()) {
            q.setParameter("filtroPessoa", "%" + filtroPessoa.trim().toLowerCase() + "%");
        }
        if (nomeFantasia != null && !nomeFantasia.isEmpty()) {
            q.setParameter("nomeFantasia", "%" + nomeFantasia.trim().toLowerCase() + "%");
        }
        if (endereco != null && !endereco.isEmpty()) {
            q.setParameter("endereco", "%" + endereco.trim().toLowerCase() + "%");
        }
        if (filtroCpfCnpj != null && !filtroCpfCnpj.isEmpty()) {
            q.setParameter("filtroCpfCnpj", "%" + filtroCpfCnpj.replace(".", "").replace("-", "").replace("/", "").trim().toLowerCase() + "%");
        }
        if (filtroCnae != null && !filtroCnae.isEmpty()) {
            String cnae = "%" + filtroCnae.replace(".", "").replace("-", "").replace("/", "").trim() + "%";
            q.setParameter("filtroCnae", cnae);
        }
        if (filtroProcesso != null && !filtroProcesso.isEmpty()) {
            q.setParameter("filtroProcesso", "%" + filtroProcesso.trim() + "%");
        }
        return q.getResultList();
    }

    public List<CadastroEconomico> listaCadastroEconomicoPorPessoaTipoIss(String parte, TipoIssqn... tipoIss) {
        StringBuilder hql = new StringBuilder("select c from CadastroEconomico c ")
            .append(" left join c.situacaoCadastroEconomico situacoesCMC ")
            .append(" left join c.enquadramentos enquadramento with enquadramento.fimVigencia is null ")
            .append(" where ((c.pessoa in ( ")
            .append(" select pf from PessoaFisica pf ")
            .append(" where lower(replace(replace(replace(pf.nome, '/', ''), '-', ''), '.', '')) like :parte ")
            .append(" or replace(replace(pf.cpf, '-', ''), '.', '') like :parte) ")
            .append(" or c.pessoa in (select pj from PessoaJuridica pj ")
            .append(" where lower(replace(replace(replace(pj.razaoSocial, '/', ''), '-', ''), '.', '')) like :parte ")
            .append(" or replace(replace(replace(pj.cnpj, '/', ''), '-', ''), '.', '') like :parte))")
            .append(" or (c.inscricaoCadastral like :parte))")
            .append(" and enquadramento.tipoIssqn in (:tipoissqn)")
            .append(" and situacoesCMC =  (select max(situacao.id) from CadastroEconomico cad ")
            .append(" inner join cad.situacaoCadastroEconomico situacao where cad = c)")
            .append(" and situacoesCMC.situacaoCadastral in (:situacoes)");
        Query q = getEntityManager().createQuery(hql.toString());
        q.setParameter("parte", "%" + parte.toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%");
        q.setParameter("tipoissqn", Lists.newArrayList(tipoIss));
        q.setParameter("situacoes", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR, SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR));
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<CadastroEconomico> listaFiltrando(String parte) {
        StringBuilder hql = new StringBuilder("select c from CadastroEconomico c where ((c.pessoa in (")
            .append(" select pf from PessoaFisica pf ")
            .append(" where lower(replace(replace(replace(pf.nome, '/', ''), '-', ''), '.', '')) like :parte ")
            .append(" or replace(replace(pf.cpf, '-', ''), '.', '') like :parte) ")
            .append(" or c.pessoa in (select pj from PessoaJuridica pj ")
            .append(" where lower(replace(replace(replace(pj.razaoSocial, '/', ''), '-', ''), '.', '')) like :parte ")
            .append(" or replace(replace(replace(pj.cnpj, '/', ''), '-', ''), '.', '') like :parte))")
            .append(" or (c.inscricaoCadastral like :parte))");
        Query q = getEntityManager().createQuery(hql.toString());
        q.setParameter("parte", "%" + parte.toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<Servico> listaServicoPorCE(CadastroEconomico cadastroEconomico) {
        String hql = "select se from Servico se, CadastroEconomico ce where se in elements(ce.servico) and ce = :ce";
        Query q = em.createQuery(hql).setParameter("ce", cadastroEconomico);
        return q.getResultList();
    }

    public Situacao ultimaSituacaoCNAE(CadastroEconomico bce, CNAE cnae) {
        Situacao retorno = null;
        StringBuilder hql = new StringBuilder("select sec from SituacaoEconomicoCNAE sec ")
            .append(" where sec.economicoCNAE.cadastroEconomico = :bce ")
            .append(" and sec.economicoCNAE.cnae = :cnae ")
            .append(" and sec.id = (select max(sit.id) from SituacaoEconomicoCNAE sit where sit.economicoCNAE = sec.economicoCNAE)");
        Query q = em.createQuery(hql.toString());
        q.setParameter("bce", bce);
        q.setParameter("cnae", cnae);
        List<SituacaoEconomicoCNAE> resultados = q.getResultList();
        if (!resultados.isEmpty()) {
            retorno = resultados.get(0).getSituacao();
        }
        return retorno;
    }

    public List<EconomicoCNAE> listaCNAENaoEmbargado(CadastroEconomico bce) {
        List<EconomicoCNAE> retorno = new ArrayList<EconomicoCNAE>();
        bce = em.find(CadastroEconomico.class, bce.getId());
        if (bce == null) {
            return null;
        }
        for (EconomicoCNAE ec : bce.getEconomicoCNAEVigentes()) {
            if (ultimaSituacaoCNAE(bce, ec.getCnae()) != Situacao.EMBARGADO) {
                retorno.add(ec);
            }
        }
        return retorno;
    }

    public List<EconomicoCNAE> getEconomicoCNAEVigentes(CadastroEconomico bce) {
        List<EconomicoCNAE> retorno = new ArrayList<EconomicoCNAE>();
        bce = em.find(CadastroEconomico.class, bce.getId());
        if (bce != null && bce.getId() != null) {
            bce.getEconomicoCNAE().size();
            retorno = bce.getEconomicoCNAEVigentes();
        }
        return retorno;
    }

    public CadastroEconomico recuperarEconomicoCnaeCadastro(Long id) {
        CadastroEconomico cmc = em.find(CadastroEconomico.class, id);
        Collections.sort(cmc.getEconomicoCNAE());
        Hibernate.initialize(cmc.getEconomicoCNAE());
        return cmc;
    }

    public List<CadastroEconomico> buscarCadastrosPorInscricaoOrCpfCnpjOrNome(String parte) {
        return buscarCadastrosPorInscricaoOrCpfCnpjOrNome(null, parte);
    }

    public List<CadastroEconomico> buscarCadastrosPorInscricaoOrCpfCnpjOrNome(RegimeEspecialTributacao regimeEspecialTributacao,
                                                                              String parte) {
        String sql = " select c.responsavelpelocadastro_id, c.migracaochave, ce.* " +
            "   from cadastroeconomico ce " +
            "  inner join cadastro c on c.id = ce.id " +
            "  left join pessoafisica pf on pf.id = ce.pessoa_id " +
            "  left join pessoajuridica pj on pj.id = ce.pessoa_id " +
            "  left join enquadramentofiscal ef on ef.cadastroeconomico_id = ce.id and ef.fimvigencia is null " +
            " where (lower(coalesce(pf.nome, pj.razaosocial)) like :parte ";
        String parteNumerica = StringUtil.retornaApenasNumeros(parte);
        if (!parteNumerica.isEmpty()) {
            sql += " or ce.inscricaocadastral like :parte_numeros " +
                "    or replace(replace(replace(coalesce(pf.cpf, pj.cnpj),'.', ''),'-', ''),'/', '') like :parte_numeros ";
        }
        sql += ")";
        if (regimeEspecialTributacao != null) {
            sql += " and ef.regimeespecialtributacao = :regime ";
        }
        sql += " order by ce.inscricaocadastral ";
        Query q = em.createNativeQuery(sql, CadastroEconomico.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        if (regimeEspecialTributacao != null) {
            q.setParameter("regime", regimeEspecialTributacao.name());
        }
        if (!parteNumerica.isEmpty()) {
            q.setParameter("parte_numeros", "%" + StringUtil.retornaApenasNumeros(parte) + "%");
        }
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<CadastroEconomico> listaCadastroEconomicoPorPessoaFisica(String parte) {
        StringBuilder hql = new StringBuilder("select cmc from CadastroEconomico cmc")
            .append(" where cmc.pessoa in (select pf from PessoaFisica pf where trim(lower(pf.nome)) like :parte or pf.cpf like :parte)");

        Query q = em.createQuery(hql.toString()).setMaxResults(10);
        q.setParameter("parte", "%" + parte + "%");

        List<CadastroEconomico> listaRetorno = q.getResultList();

        if (listaRetorno.isEmpty()) {
            return new ArrayList<CadastroEconomico>();
        } else {
            return listaRetorno;
        }
    }

    public List<CadastroEconomico> recuperaCMCporParametrosTransporte(TipoAutonomo tipoAutonomo, String parte) {
        List<CadastroEconomico> toReturn = new ArrayList<>();
        StringBuilder hql = new StringBuilder("select ce from CadastroEconomico ce ")
            .append(" join ce.situacaoCadastroEconomico situacao ")
            .append(" left join ce.enquadramentos enquadramento with enquadramento.fimVigencia is null ")
            .append(" where enquadramento.tipoIssqn = 'FIXO'")
            .append(" and ce.tipoAutonomo = :tipoautonomo ")
            .append(" and (ce.inscricaoCadastral like :parte ")
            .append(" or ce.pessoa in(select pf from PessoaFisica pf where lower(pf.nome) like :parte or pf.cpf like :parte) ")
            .append(" or ce.pessoa in(select pj from PessoaJuridica pj where lower(pj.razaoSocial) like :parte or pj.cnpj like :parte)")
            .append(" )")// RECUPERA O MAIOR VALOR DA LISTA ENCONTRADA E COMPARA COM A CONSULTA ALTERIOR
            .append(" and situacao.id = (select max(s.id) from SituacaoCadastroEconomico s where s in elements (ce.situacaoCadastroEconomico))")
            .append(" and (situacao.situacaoCadastral = '").append(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name()).append("'")
            .append(" or situacao.situacaoCadastral = '").append(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name()).append("')");

        Query q = em.createQuery(hql.toString());
        q.setMaxResults(10);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("tipoautonomo", tipoAutonomo);
        toReturn = q.getResultList();

        for (CadastroEconomico obj : toReturn) {
            obj.getPessoa();
        }
        return toReturn;
    }

    public List<CadastroEconomico> recuperaCMCmotorista(String parte) {
        List<CadastroEconomico> toReturn = new ArrayList<>();
        StringBuilder hql = new StringBuilder("select ce from CadastroEconomico ce ")
            .append(" join ce.situacaoCadastroEconomico situacao ")
            .append(" left join ce.enquadramentos enquadramento with enquadramento.fimVigencia is null ")
            .append(" where enquadramento.tipoIssqn = 'FIXO'")
            .append(" and (ce.inscricaoCadastral like :parte ")
            .append(" or ce.pessoa in(select pf from PessoaFisica pf where lower(pf.nome) like :parte or pf.cpf like :parte) ")
            .append(" or ce.pessoa in(select pj from PessoaJuridica pj where lower(pj.razaoSocial) like :parte or pj.cnpj like :parte)")
            .append(" )")// RECUPERA O MAIOR VALOR DA LISTA ENCONTRADA E COMPARA COM A CONSULTA ALTERIOR
            .append(" and situacao.id = (select max(s.id) from SituacaoCadastroEconomico s where s in elements (ce.situacaoCadastroEconomico))")
            .append(" and (situacao.situacaoCadastral = '").append(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name()).append("'")
            .append(" or situacao.situacaoCadastral = '").append(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name()).append("')");

        Query q = em.createQuery(hql.toString());
        q.setMaxResults(10);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        toReturn = q.getResultList();

        for (CadastroEconomico obj : toReturn) {
            obj.getPessoa();
        }
        return toReturn;
    }

    public List<CadastroEconomico> listaCadastroEconomicoPorTipoAutomoto(String parte) {
        StringBuilder sql = new StringBuilder("SELECT * ")
            .append("       FROM cadastroeconomico ce ")
            .append(" INNER JOIN pessoa p ON p.id = ce.pessoa_id")
            .append(" INNER JOIN pessoafisica pf ON pf.id = p.id")
            .append("      WHERE lower(pf.nome) LIKE :parte")
            .append("         OR pf.cpf LIKE :parte");

        Query q = em.createNativeQuery(sql.toString(), CadastroEconomico.class).setMaxResults(15);
        q.setParameter("parte", "%" + parte + "%");

        List<CadastroEconomico> listaRetorno = q.getResultList();

        if (listaRetorno.isEmpty()) {
            return new ArrayList<CadastroEconomico>();
        } else {
            return listaRetorno;
        }
    }

    public List<CadastroEconomico> listaCadastroEconomicoPorSituacao(String parte, SituacaoCadastralCadastroEconomico... situacoes) {
        StringBuilder hql = new StringBuilder("select ce from CadastroEconomico ce join ce.situacaoCadastroEconomico situacao ")
            .append(" where (ce.inscricaoCadastral like :parte ")
            .append(" or ce.pessoa in(select pf from PessoaFisica pf where lower(pf.nome) like :parte or replace(replace(pf.cpf,'.',''),'-','') like :parte) ")
            .append(" or ce.pessoa in(select pj from PessoaJuridica pj where lower(pj.razaoSocial) like :parte or replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :parte))")
            .append(" and situacao.id = (select max(s.id) from SituacaoCadastroEconomico s where s in elements (ce.situacaoCadastroEconomico))");
        if (situacoes.length > 0) {
            int tamanho = situacoes.length;
            int count = 0;
            hql.append(" and ( ");
            for (SituacaoCadastralCadastroEconomico situacao : situacoes) {
                count++;
                hql.append(" situacao.situacaoCadastral = '" + situacao.name() + "'");
                if (count > 1 && count < tamanho) {
                    hql.append(" or ");
                }
            }
            hql.append(" ) ");
        }
        Query q = em.createQuery(hql.toString());
        q.setMaxResults(10);
        q.setParameter("parte", "%" + parte.trim().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%");
        return q.getResultList();
    }

    public CadastroEconomico recuperarCadastroEconomicoDaPessoa(Pessoa pessoa) {
        String hql = " from CadastroEconomico ce where ce.pessoa = :pessoa";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoa);
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        } else {
            return (CadastroEconomico) resultList.get(0);
        }
    }

    public SituacaoCadastroEconomico recuperarUltimaSituacaoDoCadastroEconomico(CadastroEconomico cadastroEconomico) {
        return recuperarUltimaSituacaoDoCadastroEconomico(cadastroEconomico.getId());
    }

    public SituacaoCadastroEconomico recuperarUltimaSituacaoDoCadastroEconomico(Long idCadastroEconomico) {
        StringBuilder sql = new StringBuilder("SELECT sce.* ")
            .append("       FROM situacaocadastroeconomico sce")
            .append(" INNER JOIN ce_situacaocadastral cesc ON cesc.situacaocadastroeconomico_id = sce.id")
            .append(" INNER JOIN cadastroeconomico ce ON ce.id = cesc.cadastroeconomico_id")
            .append("      WHERE sce.dataRegistro = (SELECT max(sit.dataRegistro)")
            .append("                                   FROM situacaocadastroeconomico sit")
            .append("                                  INNER JOIN ce_situacaocadastral ceSit on ceSit.situacaocadastroeconomico_id = sit.id ")
            .append("                                  WHERE ceSit.CADASTROECONOMICO_ID = ce.id)")
            .append("        AND ce.id = :cadastroEconomico")
            .append("        AND rownum = 1");
        Query q = em.createNativeQuery(sql.toString(), SituacaoCadastroEconomico.class);
        q.setParameter("cadastroEconomico", idCadastroEconomico);
        List resultList = q.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            return null;
        } else {
            return (SituacaoCadastroEconomico) resultList.get(0);
        }
    }

    public void gerarInscricaoCadastral(CadastroEconomico cadastroEconomico) {
        String inscricaoCadastral = cadastroEconomico.getPessoa() instanceof PessoaJuridica ?
            StringUtil.retornaApenasNumeros(cadastroEconomico.getPessoa().getCpf_Cnpj()) :
            buscarUltimaInscricaoCMC();
        cadastroEconomico.setInscricaoCadastral(inscricaoCadastral);
    }

    private String buscarUltimaInscricaoCMC() {
        Long proximaInscricao = singletonGeradorCodigo.getProximaInscricao();
        return String.valueOf(proximaInscricao);
    }

    public boolean validaCodigoExiste(CadastroEconomico cadastro) {
        String sql = "SELECT inscricaocadastral FROM cadastroeconomico WHERE inscricaocadastral = :codigo";
        if (cadastro.getId() != null) {
            sql += " AND ID != :id ";
        }
        Query consulta = em.createNativeQuery(sql);

        consulta.setParameter("codigo", cadastro.getInscricaoCadastral().substring(0, 6));
        if (cadastro.getId() != null) {
            consulta.setParameter("id", cadastro.getId());
        }
        try {
            return !consulta.getResultList().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public List<PessoaFisica> completaPessoaFisicaComCMC(String parte) {
        StringBuilder hql = new StringBuilder("select p from CadastroEconomico ce ")
            .append(" inner join ce.pessoa p")
            .append(" where p.class = ").append(PessoaFisica.class.getSimpleName())
            .append("   and lower(p.nome) like :parte");
        Query q = em.createQuery(hql.toString());
        q.setParameter("parte", "%" + parte + "%");

        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public List<EconomicoCNAE> listaFiltrandoPessoa(Pessoa pessoa) {
        StringBuilder hql = new StringBuilder("select economicoCNAE from EconomicoCNAE economicoCNAE ")
            .append(" inner join economicoCNAE.cadastroEconomico cadastro")
            .append(" where cadastro.pessoa = :parametro ");
        Query q = em.createQuery(hql.toString());
        q.setParameter("parametro", pessoa);
        return q.getResultList();
    }

    public boolean jaExisteEsteCMC(CadastroEconomico cmc) {
        StringBuilder sql = new StringBuilder("SELECT inscricaocadastral FROM cadastroeconomico WHERE inscricaocadastral = :inscricao");
        if (cmc.getId() != null) {
            sql.append(" AND ID != :id ");
        }
        Query consulta = em.createNativeQuery(sql.toString());
        consulta.setParameter("inscricao", cmc.getInscricaoCadastral());
        if (cmc.getId() != null) {
            consulta.setParameter("id", cmc.getId());
        }
        //new Util().imprimeSQL(sql, consulta);
        return !consulta.getResultList().isEmpty();
    }

    public List<CadastroEconomico> listaCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJ(String parte) {
        StringBuilder hql = new StringBuilder("select cmc from CadastroEconomico cmc ")
            .append(" where cmc.pessoa in (select pf from PessoaFisica pf where trim(lower(pf.nome)) like :filtro or pf.cpf like :filtro)")
            .append(" or cmc.pessoa in (select pj from PessoaJuridica pj where trim(lower(pj.razaoSocial)) like :filtro or pj.cnpj like :filtro) or cmc.inscricaoCadastral like :filtro");
        Query q = em.createQuery(hql.toString());
        q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<PrestadorServicoNfseDTO> listaCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJ(String parte, Pageable pageable) {
        StringBuilder hql = new StringBuilder("select cmc.id, ")
            .append(" cmc.inscricaocadastral, ")
            .append(" coalesce(pf.nome,pj.razaosocial), ")
            .append(" coalesce(pf.nome,pj.nomefantasia), ")
            .append(" coalesce(pf.cpf,pj.cnpj) ")
            .append(getFromCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJ());
        Query q = em.createNativeQuery(hql.toString());
        q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
        q.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        q.setMaxResults(pageable.getPageSize());

        List<Object[]> objs = q.getResultList();
        List<PrestadorServicoNfseDTO> retorno = Lists.newLinkedList();
        for (Object[] obj : objs) {
            retorno.add(new PrestadorServicoNfseDTO(((BigDecimal) obj[0]).longValue(),
                obj[1].toString(),
                obj[2].toString(),
                obj[3].toString(),
                obj[4].toString()
            ));
        }

        return retorno;
    }

    public String getFromCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJ() {
        return new StringBuilder(" from CadastroEconomico cmc ")
            .append(" left join pessoafisica pf on pf.id = cmc.pessoa_id ")
            .append(" left join pessoajuridica pj on pj.id = cmc.pessoa_id ")
            .append(" where ((trim(lower(pf.nome)) like :filtro or replace(replace(pf.cpf,'.',''),'-','') like replace(replace(:filtro,'.',''),'-',''))")
            .append(" or (trim(lower(pj.razaoSocial)) like :filtro or trim(lower(pj.nomeFantasia)) like :filtro ")
            .append(" or replace(replace(replace(pj.cnpj,'.',''),'-',''), '/', '') like replace(replace(replace(:filtro,'.',''),'-',''), '/', '')) ")
            .append(" or cmc.inscricaoCadastral like :filtro )")
            .toString();
    }

    public Integer contarCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJ(String parte) {
        StringBuilder hql = new StringBuilder("select count(cmc.id) from CadastroEconomico cmc ")
            .append(" where cmc.pessoa in (select pf from PessoaFisica pf where trim(lower(pf.nome)) like :filtro or pf.cpf like :filtro)")
            .append(" or cmc.pessoa in (select pj from PessoaJuridica pj where trim(lower(pj.razaoSocial)) like :filtro or pj.cnpj like :filtro) or cmc.inscricaoCadastral like :filtro");
        Query q = em.createQuery(hql.toString());
        q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
        return ((Long) q.getSingleResult()).intValue();
    }


    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public List<CadastroEconomico> listaCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJPorSituacao(String parte, SituacaoCadastralCadastroEconomico... situacoes) {
        if (situacoes == null) {
            situacoes = SituacaoCadastralCadastroEconomico.values();
        }
        StringBuilder hql = new StringBuilder("select cmc from CadastroEconomico cmc")
            .append(" left join cmc.situacaoCadastroEconomico situacoesCMC ")
            .append(" where (cmc.pessoa in (select pf from PessoaFisica pf where trim(lower(pf.nome)) like :filtro ")
            .append(" or (REPLACE(REPLACE(REPLACE(pf.cpf,'.',''),'-',''),'/','') LIKE :filtro))")
            .append(" or cmc.pessoa IN (SELECT pj FROM PessoaJuridica pj WHERE TRIM(LOWER(pj.razaoSocial)) LIKE :filtro ")
            .append(" or REPLACE(REPLACE(REPLACE(pj.cnpj,'.',''),'-',''),'/','') LIKE :filtro)")
            .append(" or cmc.inscricaoCadastral like :filtro)")
            .append(" and situacoesCMC.dataRegistro =  (select max(situacao.dataRegistro) from CadastroEconomico cad ")
            .append(" inner join cad.situacaoCadastroEconomico situacao where cad = cmc)")
            .append(" and situacoesCMC.situacaoCadastral in (:situacoes)");
        Query q = em.createQuery(hql.toString());
        q.setParameter("filtro", "%" + parte.toLowerCase().trim().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%");
        q.setParameter("situacoes", Lists.newArrayList(situacoes));
        q.setMaxResults(10);
        List<CadastroEconomico> cadastros = q.getResultList();
        for (CadastroEconomico cadastro : cadastros) {
            Hibernate.initialize(cadastro.getEnquadramentos());
        }
        return cadastros;
    }


    public List<CadastroEconomico> listaCadastroEconomicoPorCMCPessoaFisica(String parte) {
        StringBuilder hql = new StringBuilder("select cmc from CadastroEconomico cmc ")
            .append(" where cmc.pessoa in (select pf from PessoaFisica pf ")
            .append("                      where trim(lower(pf.nome)) like :filtro ")
            .append("                      or pf.cpf like :filtro)")
            .append(" or (cmc.inscricaoCadastral like :filtro ")
            .append("       and exists (select pf from PessoaFisica pf ")
            .append("                   where pf = cmc.pessoa))");
        Query q = em.createQuery(hql.toString());
        q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(10);

        return q.getResultList();
    }

    public List<CadastroEconomico> listaCadastrosEconomicosPermissionarioComPermissaoVigente(String parte, TipoPermissaoRBTrans tipoPermissao) {
        StringBuilder sql = new StringBuilder("SELECT cadastro.migracaochave, ce.*")
            .append(" FROM cadastroeconomico ce")
            .append(" INNER JOIN cadastro cadastro ON cadastro.id = ce.id")
            .append(" WHERE ")
            .append("      ((ce.id IN")
            .append("      (SELECT pt.cadastroeconomico_id")
            .append("       FROM permissaotransporte pt")
            .append("       inner join cadastroeconomico cadec on cadec.id = pt.cadastroeconomico_id")
            .append("       inner join pessoa p on p.id = cadec.pessoa_id")
            .append("       left join pessoajuridica pj on pj.id = p.id")
            .append("       left join pessoafisica pf on pf.id = p.id")
            .append("       WHERE (pt.iniciovigencia <= current_date and coalesce(pt.finalvigencia,current_date) >= current_date)")
            .append("       AND (pt.tipopermissaorbtrans = :tipoPermissao)")
            .append("       AND (pt.numero LIKE :parte")
            .append("       OR upper(pf.nome) LIKE :parte")
            .append("       OR pf.cpf LIKE :parte")
            .append("       OR (REPLACE(REPLACE(REPLACE(pf.cpf,'.',''),'-',''),'/','') LIKE :parte)")
            .append("       OR upper(pj.nomefantasia) LIKE :parte")
            .append("       OR pj.cnpj LIKE :parte")
            .append("       OR (REPLACE(REPLACE(REPLACE(pj.cnpj,'.',''),'-',''),'/','') LIKE :parte)")
            .append("       OR ce.inscricaocadastral LIKE :parte")
            .append("       ))))");
        Query q = em.createNativeQuery(sql.toString(), CadastroEconomico.class);
        q.setParameter("parte", "%" + parte.trim().toUpperCase() + "%");
        q.setParameter("tipoPermissao", tipoPermissao.name());
        q.setMaxResults(10);

        return q.getResultList();
    }

    public List<String> recuperarInscricaoCadastralPorFaixaETipo(String cmcInicial, String cmcFinal, TipoAutonomo tipoAutonomo) {
        StringBuilder sql = new StringBuilder(
            "select ce.inscricaocadastral from cadastroeconomico ce  ")
            .append(" left join ce_situacaocadastral cesit on cesit.cadastroeconomico_id = ce.id ")
            .append(" left join SituacaoCadastroEconomico sit on cesit.situacaocadastroeconomico_id = sit.id")
            .append(" left join EnquadramentoFiscal enquadramento on enquadramento.cadastroeconomico_id = ce.id and enquadramento.fimVigencia is null ")
            .append(" where ce.inscricaocadastral >= :cmcInicial")
            .append(" and ce.inscricaocadastral <= :cmcFinal")
            .append(" and enquadramento.tipoissqn = :tipo_iss")
            .append(" and sit.id =  (select max(situacao.id) from ce_situacaocadastral cesit ")
            .append(" inner join SituacaoCadastroEconomico situacao on cesit.situacaocadastroeconomico_id = situacao.id ")
            .append(" where cesit.cadastroeconomico_id = ce.id) ")
            .append("  and sit.situacaocadastral in (:ativo) ");

        if (tipoAutonomo != null) {
            sql.append(" and tipoautonomo_id = :tipoAutonomo");
        }
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("cmcInicial", cmcInicial);
        q.setParameter("cmcFinal", cmcFinal);
        q.setParameter("tipo_iss", TipoIssqn.FIXO.name());
        q.setParameter("ativo", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name(), SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name()));
        if (tipoAutonomo != null) {
            q.setParameter("tipoAutonomo", tipoAutonomo.getId());
        }
        return q.getResultList();
    }

    public List<CadastroEconomico> recuperaCadastroEconomicoPorCPF(String cpf) {
        List<CadastroEconomico> toReturn = new ArrayList<>();
        StringBuilder sql = new StringBuilder("select c.migracaochave, ce.* from cadastro c ")
            .append(" inner join cadastroeconomico ce on ce.id = c.id ")
            .append(" inner join pessoafisica pf on pf.id = ce.pessoa_id ")
            .append(" where pf.cpf = :cpf OR (REPLACE(REPLACE(REPLACE(pf.cpf,'.',''),'-',''),'/','') = :cpf)");
        Query q = em.createNativeQuery(sql.toString(), CadastroEconomico.class);
        q.setParameter("cpf", cpf);

        toReturn = q.getResultList();
        for (CadastroEconomico ce : toReturn) {
            ce = this.recuperar(ce.getId());
        }
        return toReturn;
    }

    public List<CadastroEconomico> listaCadastroEconomicoPessoaJuridica(String parte) {
        StringBuilder hql = new StringBuilder("select cmc from CadastroEconomico cmc")
            .append(" where cmc.pessoa.id in  (select id from PessoaJuridica) and ( cmc.pessoa in (select pj from PessoaJuridica pj where trim(lower(pj.razaoSocial)) like :filtro ")
            .append(" or REPLACE(REPLACE(REPLACE(pj.cnpj,'.',''),'-',''),'/','') like :filtro) or cmc.inscricaoCadastral like :filtro)");
        Query q = em.createQuery(hql.toString());
        q.setParameter("filtro", "%" + parte.toLowerCase().trim().toLowerCase().replace(".", "").replace("-", "") + "%");
        q.setMaxResults(10);

        return q.getResultList();
    }

    public EnderecoCorreio recuperarEnderecoPessoaCMC(Long idCmc) {
        CadastroEconomico cadastroEconomico = em.find(CadastroEconomico.class, idCmc);
        if (cadastroEconomico != null) {
            return cadastroEconomico.getLocalizacao().converterEnderecoCadastroEconomicoPrincipalParaEnderecoCorreio();
        }
        return null;
    }

    public EnderecoCadastroEconomico retornaEnderecoCadastroEconomico(Long id, TipoEndereco tipoEndereco) {
        StringBuilder sql = new StringBuilder(" select * from EnderecoCadastroEconomico ")
            .append(" where  cadastroeconomico_id =:cadastroeconomico_id ")
            .append("    and tipoEndereco =:tipoEndereco  ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), EnderecoCadastroEconomico.class);
        q.setParameter("cadastroeconomico_id", id);
        q.setParameter("tipoEndereco", tipoEndereco.name());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty() && q.getResultList().size() > 0) {
            return (EnderecoCadastroEconomico) q.getSingleResult();
        }

        return null;
    }

    public List<CadastroEconomico> recuperarCadastroEconomicoPeloCnpj(String cnpj) {
        String sql = "select cad " +
            "       from CadastroEconomico cad" +
            "       join cad.pessoa pj " +
            "      where replace(replace(replace(pj.cnpj,'/',''),'.',''),'-','') = :cnpj";
        Query q = em.createQuery(sql);
        q.setParameter("cnpj", cnpj.replace(".", "").replace("-", "").replace("/", ""));
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultList;
    }

    public EscritorioContabilFacade getEscritorioContabilFacade() {
        return escritorioContabilFacade;
    }

    public TributoFacade getTributoFacade() {
        return tributoFacade;
    }

    public TipoDocumentoFiscalFacade getTipoDocumentoFiscalFacade() {
        return tipoDocumentoFiscalFacade;
    }

    public AtributoFacade getAtributoFacade() {
        return atributoFacade;
    }

    public ServicoFacade getServicoFacade() {
        return servicoFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public CNAEFacade getCnaeFacade() {
        return cnaeFacade;
    }

    public EnderecoFacade getEnderecoFacade() {
        return enderecoFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public VistoriaFiscalizacaoFacade getVistoriaFiscalizacaoFacade() {
        return vistoriaFiscalizacaoFacade;
    }

    public CadastroAidfFacade getCadastroAidfFacade() {
        return cadastroAidfFacade;
    }

    public TipoAutonomoFacade getTipoAutonomoFacade() {
        return tipoAutonomoFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public HistoricoFacade getHistoricoFacade() {
        return historicoFacade;
    }

    public CaracteristicasFuncionamentoFacede getCaracteristicasFuncionamentoFacede() {
        return caracteristicasFuncionamentoFacede;
    }

    public HorarioFuncionamentoFacade getHorarioFuncionamentoFacade() {
        return horarioFuncionamentoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public LoteFacade getLoteFacade() {
        return loteFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public HistoricoLegadoFacade getHistoricoLegadoFacade() {
        return historicoLegadoFacade;
    }

    public NaturezaJuridicaFacade getNaturezaJuridicaFacade() {
        return naturezaJuridicaFacade;
    }

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    public CEPFacade getCepFacade() {
        return cepFacade;
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public PermissaoTransporteFacade getPermissaoTransporteFacade() {
        return permissaoTransporteFacade;
    }

    public List<BCECaracFuncionamento> recuperarCaracteristicasFuncionamento(CadastroEconomico cadastroEconomico) {
        return em.createQuery("from BCECaracFuncionamento where cadastroEconomico = :cmc", BCECaracFuncionamento.class).setParameter("cmc", cadastroEconomico).getResultList();
    }


    public List<CadastroEconomico> recuperarCadastrosDaPessoa(Pessoa pessoa) {
        return recuperarCadastrosDaPessoa(pessoa, null, null);
    }

    public List<CadastroEconomico> recuperarCadastrosDaPessoa(Pessoa pessoa, Integer inicio, Integer max) {
        Query q = em.createQuery("select c from CadastroEconomico c " +
                " left join c.sociedadeCadastroEconomico s " +
                " where c.pessoa = :pessoa or s.socio = :pessoa" +
                " order by c.inscricaoCadastral")
            .setParameter("pessoa", pessoa);
        if (inicio != null && max != null) {
            q.setFirstResult(inicio);
            q.setMaxResults(max);
        }
        List<CadastroEconomico> lista = q.getResultList();
        for (CadastroEconomico cadastroEconomico : lista) {
            cadastroEconomico.getEnquadramentos().size();
        }
        return new ArrayList<>(new HashSet<CadastroEconomico>(q.getResultList()));
    }

    public List<CadastroEconomico> recuperarCadastrosSomenteDaPessoa(Pessoa pessoa) {
        if (pessoa == null) {
            return Lists.newArrayList();
        }
        String sql = " select c from CadastroEconomico c " +
            " where c.pessoa.id = :pessoaId " +
            " order by c.inscricaoCadastral ";
        Query q = em.createQuery(sql);
        q.setParameter("pessoaId", pessoa.getId());
        List<CadastroEconomico> resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            return Lists.newArrayList();
        }
        for (CadastroEconomico cadastroEconomico : resultado) {
            Hibernate.initialize(cadastroEconomico.getEnquadramentos());
        }
        return new ArrayList<>(new HashSet<>(resultado));
    }

    public Integer contarCadastrosDaPessoa(Pessoa pessoa) {
        Query q = em.createQuery("select count(distinct c.id) from CadastroEconomico c " +
                " left join c.sociedadeCadastroEconomico s " +
                " where c.pessoa = :pessoa or s.socio = :pessoa")
            .setParameter("pessoa", pessoa);

        return ((Long) q.getSingleResult()).intValue();
    }

    public List<CadastroEconomico> recuperaCadastrosAtivosDaPessoa(Pessoa pessoa) {
        Query q = em.createQuery("select c from CadastroEconomico c " +
                " left join c.situacaoCadastroEconomico situacoes " +
                " where c.pessoa = :pessoa " +
                " and situacoes.id = (select max(situacao.id) from CadastroEconomico cad " +
                " inner join cad.situacaoCadastroEconomico situacao where cad = c) " +
                " and (situacoes.situacaoCadastral = '" + SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name() + "' " +
                " or situacoes.situacaoCadastral = '" + SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name() + "')")
            .setParameter("pessoa", pessoa);
        return q.getResultList();
    }


    public List<SociedadeCadastroEconomico> recuperaSocios(Pessoa pessoa) {
        Query q = em.createNativeQuery("select s.* " +
            "      from sociedadecadastroeconomico s " +
            " where s.socio_id = :idPessoa ", SociedadeCadastroEconomico.class);
        q.setParameter("idPessoa", pessoa.getId());
        return q.getResultList();
    }

    public List<CadastroEconomico> recuperaCadastrosAtivosDaPessoaSocia(Pessoa pessoa) {
        Query q = em.createQuery("select c from CadastroEconomico c " +
                " left join c.sociedadeCadastroEconomico s " +
                " left join c.situacaoCadastroEconomico situacoes " +
                " where s.socio = :pessoa " +
                " and situacoes.id = (select max(situacao.id) from CadastroEconomico cad " +
                " inner join cad.situacaoCadastroEconomico situacao where cad = c) " +
                " and (situacoes.situacaoCadastral = '" + SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name() + "' " +
                " or situacoes.situacaoCadastral = '" + SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name() + "')")
            .setParameter("pessoa", pessoa);
        return q.getResultList();
    }

    public String recuperaEnderecoCadastro(Long id) {
        String sql = " select vwendereco.tipologradouro, vwendereco.logradouro, vwendereco.numero, " +
            "                  vwendereco.complemento, vwendereco.bairro, vwendereco.cep " +
            " from vwenderecopessoa vwendereco " +
            " inner join pessoa pes on vwendereco.pessoa_id = pes.id " +
            " inner join cadastroeconomico cmc on pes.id = cmc.pessoa_id " +
            " where cmc.id = :idCmc ";

        Query q = em.createNativeQuery(sql).setParameter("idCmc", id);
        List<Object[]> dadosEndereco = q.getResultList();

        if (dadosEndereco != null && !dadosEndereco.isEmpty()) {
            Object[] endereco = dadosEndereco.get(0);
            String enderecoCompleto = "Endereço de Localização: ";
            enderecoCompleto += endereco[0] != null ? endereco[0] + " " : "";
            enderecoCompleto += endereco[1] != null ? endereco[1] + ", " : "";
            enderecoCompleto += endereco[2] != null ? endereco[2] + ", " : "";
            enderecoCompleto += endereco[3] != null ? endereco[3] + ", " : "";
            enderecoCompleto += endereco[4] != null ? endereco[4] + ", " : "";
            enderecoCompleto += endereco[5] != null ? endereco[5] + ", " : "";

            return enderecoCompleto;
        }
        return null;
    }

    public EnderecoCadastroEconomico getLocalizacao(CadastroEconomico c) {
        String hql = "select endereco from EnderecoCadastroEconomico endereco " +
            " where endereco.cadastroEconomico = :cadastro " +
            "   and endereco.tipoEndereco = :tipoEndereco ";
        Query q = em.createQuery(hql);
        q.setParameter("cadastro", c);
        q.setParameter("tipoEndereco", TipoEndereco.COMERCIAL);
        if (!q.getResultList().isEmpty()) {
            return (EnderecoCadastroEconomico) q.getResultList().get(0);
        }
        return null;
    }


    public Integer numeroDeSocios(CadastroEconomico ce) {
        String sql = " select count(sociedade.id) from sociedadecadastroeconomico sociedade " +
            " inner join cadastroeconomico cad on sociedade.cadastroeconomico_id = cad.id " +
            " where sysdate between sociedade.datainicio and coalesce(sociedade.datafim, sysdate) " +
            " and cad.id = :ce ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ce", ce.getId());
        if (q.getResultList().isEmpty()) {
            return 0;
        }
        return ((BigDecimal) q.getSingleResult()).intValue();
    }

    public LogradouroBairro salvarLogradouroBairro(LogradouroBairro logradouroBairro) {
        return em.merge(logradouroBairro);
    }

    public List<CadastroEconomico> buscarCadastroEconomicoAtivoPorPessoaAndInscricaoCadastral(String inscricaoCadastral, Pessoa pessoa) {
        String hql = "select ce from CadastroEconomico ce " +
            " join ce.situacaoCadastroEconomico situacoes " +
            " where ce.pessoa = :pessoa " +
            "   and situacoes.id = (select max(situacao.id) " +
            "                          from CadastroEconomico cad " +
            "                         inner join cad.situacaoCadastroEconomico situacao " +
            "                       where cad = ce) " +
            "   and situacoes.situacaoCadastral in :situacoes " +
            "   and ce.inscricaoCadastral like :inscricaoCadastral ";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoa);
        q.setParameter("situacoes", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR, SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR));
        q.setParameter("inscricaoCadastral", "%" + inscricaoCadastral.trim() + "%");
        return q.getResultList();
    }

    public CadastroEconomico buscarCadastroEconomicoAtivoPorPessoa(Pessoa pessoa) {
        return buscarCadastroEconomicoAtivoPorPessoa(pessoa, Boolean.TRUE);
    }

    public CadastroEconomico buscarCadastroEconomicoAtivoPorPessoa(Pessoa pessoa, boolean initializeLists) {
        String hql = "select ce from CadastroEconomico ce " +
            " join ce.situacaoCadastroEconomico situacoes " +
            " where ce.pessoa = :pessoa " +
            "   and situacoes.id = (select max(sit.id) " +
            "                          from CadastroEconomico cad " +
            "                         inner join cad.situacaoCadastroEconomico sit " +
            "                       where cad = ce) " +
            "   and situacoes.situacaoCadastral in :situacoes " +
            "   order by ce.id desc ";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoa);
        q.setParameter("situacoes", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR, SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR));
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            CadastroEconomico cmc = (CadastroEconomico) resultList.get(0);
            if (initializeLists) {
                Hibernate.initialize(cmc.getSituacaoCadastroEconomico());
            }
            return cmc;
        }
        return null;
    }

    public EnquadramentoFiscal buscarEnquadramentoFiscalVigente(CadastroEconomico cadastroEconomico) {
        String hql = "select eq from EnquadramentoFiscal eq where eq.cadastroEconomico = :cadastroEconomico and fimVigencia is null";
        Query q = em.createQuery(hql);
        q.setParameter("cadastroEconomico", cadastroEconomico);
        List<EnquadramentoFiscal> enquadramentos = q.getResultList();
        if (!enquadramentos.isEmpty()) {
            return enquadramentos.get(0);
        }
        return null;
    }

    public List<EconomicoCNAE> buscarListaEconomicoCNAE(CadastroEconomico cadastroEconomico) {
        String hql = "select ec from EconomicoCNAE ec where ec.cadastroEconomico = :cadastroEconomico";
        Query q = em.createQuery(hql);
        q.setParameter("cadastroEconomico", cadastroEconomico);
        return q.getResultList();
    }

    public List<EnderecoCadastroEconomico> buscarListaEndereco(CadastroEconomico cadastroEconomico) {
        String hql = "select ec from EnderecoCadastroEconomico ece where ece.cadastroEconomico = :cadastroEconomico";
        Query q = em.createQuery(hql);
        q.setParameter("cadastroEconomico", cadastroEconomico);
        return q.getResultList();
    }

    public CadastroEconomico recuperarPorRevisao(Long id, Long revisao) throws EntityNotFoundException {
        AuditReader reader = AuditReaderFactory.get(Util.criarEntityManagerOpenViewReadOnly());
        CadastroEconomico bce = reader.find(CadastroEconomico.class, id, revisao);
        Map<Atributo, ValorAtributo> atributos = buscarMapsAtributosNaRevisao(bce, revisao);
        bce.setAtributos(atributos);
        return bce;
    }

    public void inicializarListaNoHibernate(List lista) {
        for (Object o : lista) {
            inicializarObjetoNoHibernate(o);
        }
    }

    public void inicializarObjetoNoHibernate(Object objeto) {
        Hibernate.initialize(objeto);
    }

    private Pessoa buscarPessoaDoCadastroEconomico(CadastroEconomico bce) {
        String hql = "select ce.pessoa from CadastroEconomico ce where ce.id = :idCadastro";
        Query q = em.createQuery(hql).setParameter("idCadastro", bce.getId());
        if (!q.getResultList().isEmpty()) {
            return (Pessoa) q.getResultList().get(0);
        }
        return null;
    }

    private Map<Atributo, ValorAtributo> buscarMapsAtributosNaRevisao(CadastroEconomico cadastroEconomico, Long revisao) {
        Map<Atributo, ValorAtributo> atributos = Maps.newHashMap();
        String sql = "select ceva.atributos_key, ceva.atributos_id from valoratributo_aud vaaud " +
            "inner join ce_valoratributos ceva on ceva.atributos_key = vaaud.atributo_id " +
            "where ceva.cadastroeconomico_id = :idCadastroEconomico " +
            "  and vaaud.rev = :revisao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastroEconomico", cadastroEconomico.getId());
        q.setParameter("revisao", revisao);
        List<Object[]> lista = q.getResultList();

        AuditReader reader = AuditReaderFactory.get(em);
        for (Object[] obj : lista) {
            Long idAtributo = ((BigDecimal) obj[0]).longValue();
            Atributo att = reader.find(Atributo.class, idAtributo, revisao);

            Long idValorAtributo = ((BigDecimal) obj[1]).longValue();
            ValorAtributo va = reader.find(ValorAtributo.class, idValorAtributo, revisao);

            atributos.put(att, va);
        }
        return atributos;
    }

    public CadastroEconomico buscarCadastroEconomicoPorCnpjRedeSim(String cnpj) {
        CadastroEconomico cadastroEconomico = buscarCadastroEconomicoPorCnpjRedeSim(cnpj, SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        if (cadastroEconomico == null) {
            cadastroEconomico = buscarCadastroEconomicoPorCnpjRedeSim(cnpj, null);
        }
        return cadastroEconomico;
    }

    private CadastroEconomico buscarCadastroEconomicoPorCnpjRedeSim(String cnpj, SituacaoCadastralCadastroEconomico situacaoCadastral) {
        String sql = "select c.*, ce.* " +
            "   from cadastroeconomico ce " +
            "  inner join cadastro c on c.id = ce.id " +
            "  inner join pessoajuridica pj on pj.id = ce.pessoa_id " +
            "  inner join ce_situacaocadastral cesit on cesit.cadastroeconomico_id = ce.id " +
            "  inner join situacaocadastroeconomico sit on sit.id = cesit.situacaocadastroeconomico_id " +
            "  where replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') = :cnpj " +
            "    and sit.dataregistro = (select max(sc.dataregistro) from situacaocadastroeconomico sc " +
            "                                    inner join ce_situacaocadastral cesc on cesc.situacaocadastroeconomico_id = sc.id " +
            "                                    where cesc.cadastroeconomico_id = ce.id)";
        if (situacaoCadastral != null) {
            sql += "    and sit.situacaocadastral = :situacao ";
        }
        Query q = em.createNativeQuery(sql, CadastroEconomico.class);
        q.setParameter("cnpj", cnpj.trim().replace(".", "").replace("-", "").replace("/", ""));
        if (situacaoCadastral != null) {
            q.setParameter("situacao", situacaoCadastral.name());
        }
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            CadastroEconomico cadastroEconomico = (CadastroEconomico) resultList.get(0);
            Hibernate.initialize(cadastroEconomico.getEconomicoCNAE());
            Hibernate.initialize(cadastroEconomico.getEnquadramentos());
            Hibernate.initialize(cadastroEconomico.getListaEnderecoCadastroEconomico());
            return cadastroEconomico;
        }
        return null;
    }

    public CadastroEconomico buscarCadastroEconomicoPorCnpj(String cnpj) {
        String sql = "select c.*, ce.* " +
            "   from cadastroeconomico ce " +
            "  inner join cadastro c on c.id = ce.id " +
            "  inner join pessoajuridica pj on pj.id = ce.pessoa_id " +
            "  inner join ce_situacaocadastral cesit on cesit.cadastroeconomico_id = ce.id " +
            "  inner join situacaocadastroeconomico sit on sit.id = cesit.situacaocadastroeconomico_id " +
            "  where replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') = :cnpj " +
            "    and sit.situacaocadastral in (:ativo) " +
            "    and trunc(sit.dataregistro) = (select trunc(max(sc.dataregistro)) from situacaocadastroeconomico sc " +
            "                                    inner join ce_situacaocadastral cesc on cesc.situacaocadastroeconomico_id = sc.id " +
            "                                    where cesc.cadastroeconomico_id = ce.id)";
        Query q = em.createNativeQuery(sql, CadastroEconomico.class);
        q.setParameter("cnpj", cnpj.trim().replace(".", "").replace("-", "").replace("/", ""));
        q.setParameter("ativo", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name(), SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name()));
        if (!q.getResultList().isEmpty()) {
            CadastroEconomico cadastroEconomico = (CadastroEconomico) q.getResultList().get(0);
            Hibernate.initialize(cadastroEconomico.getEconomicoCNAE());
            Hibernate.initialize(cadastroEconomico.getEnquadramentos());
            return cadastroEconomico;
        }
        return null;
    }

    public List<CadastroEconomico> buscarCadastroEconomicoPorCnpjComSituacao(String cnpj) {
        String sql = "select c.*, ce.* " +
            "   from cadastroeconomico ce " +
            "  inner join cadastro c on c.id = ce.id " +
            "  inner join pessoajuridica pj on pj.id = ce.pessoa_id " +
            "   inner join situacaocadastroeconomico sce on sce.id = " +
            "   (select max(rel.situacaocadastroeconomico_id) from ce_situacaocadastral rel where rel.cadastroeconomico_id = ce.id) " +
            " where replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') = :cnpj" +
            "   and sce.situacaocadastral in (:ativo) ";
        Query q = em.createNativeQuery(sql, CadastroEconomico.class);
        q.setParameter("cnpj", cnpj.trim().replace(".", "").replace("-", "").replace("/", ""));
        q.setParameter("ativo", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name(), SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name()));
        List<CadastroEconomico> lista = q.getResultList();
        for (CadastroEconomico cadastroEconomico : lista) {
            Hibernate.initialize(cadastroEconomico.getSituacaoCadastroEconomico());
        }
        return lista;
    }

    public List<CadastroEconomico> buscarCadastroEconomicoPorCpfComSituacao(String cpf) {
        String sql = "select c.*, ce.* " +
            "   from cadastroeconomico ce " +
            "  inner join cadastro c on c.id = ce.id " +
            "  inner join pessoafisica pf on pf.id = ce.pessoa_id " +
            " where replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') = :cpf";
        Query q = em.createNativeQuery(sql, CadastroEconomico.class);
        q.setParameter("cpf", cpf.trim().replace(".", "").replace("-", "").replace("/", ""));
        List<CadastroEconomico> lista = q.getResultList();
        for (CadastroEconomico cadastroEconomico : lista) {
            Hibernate.initialize(cadastroEconomico.getSituacaoCadastroEconomico());
        }
        return lista;
    }

    public CadastroEconomico buscarCadastroEconomicoCNPJ(String cnpj) {
        String sql = "select c.*, ce.* from cadastroeconomico ce " +
            "  inner join cadastro c on c.id = ce.id " +
            "  inner join pessoajuridica pj on pj.id = ce.pessoa_id  where pj.cnpj = :cnpj ";
        Query q = em.createNativeQuery(sql, CadastroEconomico.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return (CadastroEconomico) q.getResultList().get(0);
        }
        return null;

    }

    public void atualizarDadosPessoaDaf607(String cnpj, String usuarioBancoDados, UsuarioSistema usuarioSistema) {
        EventoRedeSimDTO eventoRedeSimDTO = integracaoRedeSimFacade.buscarRedeSimPorCnpj(cnpj, usuarioBancoDados, true);
        integracaoRedeSimFacade.atualizarDadosPessoaJuridica(eventoRedeSimDTO, "Inserão via DAF607", usuarioSistema, usuarioBancoDados);
        CadastroEconomico cadastroEconomico = buscarCadastroEconomicoPorCnpj(cnpj);
        integracaoRedeSimFacade.atualizarDadosDoCadastro(eventoRedeSimDTO, cadastroEconomico, false, usuarioSistema);
    }

    public CadastroEconomico criarEnquadramentoFiscal(CadastroEconomico cadastroEconomico, RegimeTributario regimeTributario, TipoIssqn tipoIssqn) {
        if (cadastroEconomico.getEnquadramentoVigente() == null) {
            EnquadramentoFiscal enquadramentoFiscal = new EnquadramentoFiscal();
            enquadramentoFiscal.setTipoContribuinte(TipoContribuinte.PERMANENTE);
            enquadramentoFiscal.setTipoIssqn(tipoIssqn);
            enquadramentoFiscal.setCadastroEconomico(cadastroEconomico);
            enquadramentoFiscal.setTipoPeriodoValorEstimado(TipoPeriodoValorEstimado.MENSAL);
            enquadramentoFiscal.setRegimeTributario(regimeTributario);
            enquadramentoFiscal.setInicioVigencia(new Date());
            enquadramentoFiscal.setTipoNotaFiscalServico(!TipoIssqn.MEI.equals(tipoIssqn) ?
                TipoNotaFiscalServico.ELETRONICA : TipoNotaFiscalServico.NAO_EMITE);
            cadastroEconomico.getEnquadramentos().add(enquadramentoFiscal);
        }
        return cadastroEconomico;
    }


    public CadastroEconomico salvarRetornado(CadastroEconomico entity) {
        try {
            entity = update(entity);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return entity;
    }

    public ImagemUsuarioNfseDTO buscarImagemDoCadastro(Long id) {
        CadastroEconomico cadastroEconomico = recuperar(id);
        return pessoaFacade.buscarImagemDaPessoa(cadastroEconomico.getPessoa().getId());
    }

    public CadastroEconomico createAndSave(PrestadorServicoNfseDTO dto) throws Exception {
        String cpfCnpj = dto.getPessoa().getDadosPessoais().getCpfCnpj();
        Pessoa pessoa = pessoaFacade.buscarPessoaPorCpfOrCnpj(cpfCnpj);
        if (pessoa != null) {
            throw new ExcecaoNegocioGenerica("Já existe um pessoa cadastrada com o CPF/CNPJ " + cpfCnpj + ".");
        }
        Date hoje = new Date();
        CadastroEconomico cadastroEconomico = new CadastroEconomico();
        cadastroEconomico.setInscricaoCadastral(dto.getInscricaoMunicipal());
        cadastroEconomico.setDataCadastro(hoje);

        adicionarSalvarPessoaArquivo(cadastroEconomico, dto);
        adicionarSituacao(cadastroEconomico, hoje, SituacaoCadastralCadastroEconomico.AGUARDANDO_AVALIACAO);
        adicionarEnquadramentoFiscal(cadastroEconomico, dto.getEnquadramentoFiscal(), hoje);
        adicionarSocios(cadastroEconomico, dto, hoje);
        adicionarServicos(cadastroEconomico, dto);
        adicionarCnaes(cadastroEconomico, dto, hoje);
        cadastroEconomico = update(cadastroEconomico);
        adicionarUsuarios(cadastroEconomico, dto);
        return cadastroEconomico;
    }

    private void adicionarUsuarios(CadastroEconomico cadastroEconomico, PrestadorServicoNfseDTO dto) {
        for (UserNfseDTO usuarioDto : dto.getUsuarios()) {
            UsuarioWeb usuarioNfse = usuarioWebFacade.recuperarUsuarioPorLogin(usuarioDto.getLogin());
            UserNfseCadastroEconomico cadastroUsuario = new UserNfseCadastroEconomico();
            cadastroUsuario.setCadastroEconomico(cadastroEconomico);
            cadastroUsuario.setUsuarioNfse(usuarioNfse);
            cadastroUsuario.setSituacao(UserNfseCadastroEconomico.Situacao.PENDENTE);
            cadastroUsuario.setPermissoes(Lists.newArrayList(PermissaoUsuarioEmpresaNfse.values()));
            usuarioNfse.getUserNfseCadastroEconomicos().add(cadastroUsuario);
            usuarioWebFacade.salvarRetornando(usuarioNfse);
        }
    }

    public void adicionarSalvarPessoaArquivo(CadastroEconomico cadastroEconomico, PrestadorServicoNfseDTO dto) throws Exception {
        Pessoa pessoa = pessoaFacade.createOrSave(dto.getPessoa());
        if (dto.getImagem() != null && Strings.isNullOrEmpty(dto.getImagem().getConteudo())) {
            Arquivo arquivo = Arquivo.toArquivo(dto.getImagem());
            arquivo = arquivoFacade.retornaArquivoSalvo(arquivo, arquivo.getInputStream());
            pessoa.setArquivo(arquivo);
        }
        pessoa = pessoaFacade.salvarRetornando(pessoa);
        cadastroEconomico.setPessoa(pessoa);
    }

    private void adicionarSocios(CadastroEconomico cadastroEconomico, PrestadorServicoNfseDTO dto, Date hoje) {
        for (SocioNfseDTO socioNfseDTO : dto.getSocios()) {
            Pessoa pessoa = pessoaFacade.buscarPessoaPorCpfOrCnpj(socioNfseDTO.getSocio().getDadosPessoais().getCpfCnpj());
            if (pessoa == null) {
                pessoa = pessoaFacade.createOrSave(socioNfseDTO.getSocio());
            }

            if (cadastroEconomico.getPessoa() instanceof PessoaFisica) {
                SociedadeCadastroEconomico socio = new SociedadeCadastroEconomico();
                socio.setSocio(pessoa);
                socio.setDataRegistro(hoje);
                socio.setProporcao(socioNfseDTO.getProporcao());
                socio.setDataInicio(hoje);
                cadastroEconomico.getSociedadeCadastroEconomico().add(socio);
            } else {
                SociedadePessoaJuridica socio = new SociedadePessoaJuridica();
                socio.setSocio(pessoa);
                socio.setDataRegistro(hoje);
                socio.setProporcao(socioNfseDTO.getProporcao());
                socio.setDataInicio(hoje);
                ((PessoaJuridica) cadastroEconomico.getPessoa()).getSociedadePessoaJuridica().add(socio);
            }
        }

    }

    private void adicionarCnaes(CadastroEconomico cadastroEconomico, PrestadorServicoNfseDTO dto, Date hoje) {
        for (CnaeNfseDTO cnaeNfseDTO : dto.getCnaes()) {
            CNAE cnae = cnaeFacade.createAndSave(cnaeNfseDTO);

            EconomicoCNAE economicoCNAE = new EconomicoCNAE();
            economicoCNAE.setCadastroEconomico(cadastroEconomico);
            economicoCNAE.setDataregistro(hoje);
            economicoCNAE.setInicio(hoje);
            economicoCNAE.setCnae(cnae);
            economicoCNAE.setTipo(EconomicoCNAE.TipoCnae.Primaria);
            cadastroEconomico.getEconomicoCNAE().add(economicoCNAE);

            if (cadastroEconomico.getPessoa() instanceof PessoaJuridica) {
                PessoaCNAE pessoaCNAE = new PessoaCNAE();
                pessoaCNAE.setPessoa(cadastroEconomico.getPessoa());
                pessoaCNAE.setDataregistro(hoje);
                pessoaCNAE.setInicio(hoje);
                pessoaCNAE.setCnae(cnae);
                pessoaCNAE.setTipo(EconomicoCNAE.TipoCnae.Primaria);
                cadastroEconomico.getPessoa().getPessoaCNAE().add(pessoaCNAE);
            }
        }
    }

    private void adicionarServicos(CadastroEconomico cadastroEconomico, PrestadorServicoNfseDTO dto) {
        for (ServicoNfseDTO servicoNfseDTO : dto.getServicos()) {
            cadastroEconomico.getServico().add(servicoFacade.createAndSave(servicoNfseDTO));
        }

    }

    public void adicionarSituacao(CadastroEconomico cadastroEconomico, Date hoje, SituacaoCadastralCadastroEconomico situacao) {
        SituacaoCadastroEconomico situacaoCadastroEconomico = new SituacaoCadastroEconomico();
        situacaoCadastroEconomico.setSituacaoCadastral(situacao);
        situacaoCadastroEconomico.setDataRegistro(hoje);
        situacaoCadastroEconomico.setDataAlteracao(hoje);
        cadastroEconomico.getSituacaoCadastroEconomico().add(situacaoCadastroEconomico);
    }

    public void adicionarEnquadramentoFiscal(CadastroEconomico cadastroEconomico, EnquadramentoFiscalNfseDTO dto, Date hoje) {
        EnquadramentoFiscal enquadramentoFiscal = new EnquadramentoFiscal();
        enquadramentoFiscal.setCadastroEconomico(cadastroEconomico);
        enquadramentoFiscal.setPorte(TipoPorte.valueOf(dto.getPorte().name()));
        enquadramentoFiscal.setTipoContribuinte(TipoContribuinte.valueOf(dto.getTipoContribuinte().name()));
        enquadramentoFiscal.setRegimeTributario(RegimeTributario.valueOf(dto.getRegimeTributario().name()));
        enquadramentoFiscal.setTipoNotaFiscalServico(TipoNotaFiscalServico.valueOf(dto.getTipoNotaFiscal().name()));
        enquadramentoFiscal.setInicioVigencia(hoje);
        enquadramentoFiscal.setSubstitutoTributario(dto.getSubstitutoTributario());
        cadastroEconomico.getEnquadramentos().add(enquadramentoFiscal);
    }

    public CadastroEconomico atualizarCadastroViaNfse(PrestadorServicoNfseDTO dto) {
        CadastroEconomico cmc = recuperar(dto.getId());
        cmc.setResumoSobreEmpresa(dto.getResumoSobreEmpresa());
        cmc.setTelefoneParaContato(dto.getTelefoneParaContato());
        cmc.setNomeParaContato(dto.getNomeParaContato());
        cmc.setEnviaEmailNfseTomador(dto.getEnviaEmailNfseTomador());
        cmc.setEnviaEmailNfseContador(dto.getEnviaEmailNfseContador());
        cmc.setEnviaEmailCancelaNfseTomador(dto.getEnviaEmailCancelaNfseTomador());
        cmc.setEnviaEmailCancelaNfseContador(dto.getEnviaEmailCancelaNfseContador());
        if (dto.getEscritorioContabil() != null) {
            cmc.setEscritorioContabil(escritorioContabilFacade.recuperar(dto.getEscritorioContabil().getId()));
        }
        if (cmc.getPessoa() instanceof PessoaJuridica) {
            PessoaJuridica pessoa = (PessoaJuridica) cmc.getPessoa();
            Hibernate.initialize(pessoa.getTelefones());
            pessoa.setNomeFantasia(dto.getPessoa().getDadosPessoais().getNomeFantasia());
            pessoa.setHomePage(dto.getPessoa().getDadosPessoais().getSite());
            pessoa.setEmail(dto.getPessoa().getDadosPessoais().getEmail());

            String celularDto = dto.getPessoa().getDadosPessoais().getCelular();
            if (!Strings.isNullOrEmpty(celularDto)) {
                Telefone celular = pessoa.getTelefonePorTipo(TipoTelefone.CELULAR);
                if (celular != null) {
                    celular.setTelefone(celularDto);
                } else {
                    pessoa.getTelefones().add(new Telefone(pessoa, celularDto, false, TipoTelefone.CELULAR));
                }
            }
            String telefoneDto = dto.getPessoa().getDadosPessoais().getTelefone();
            if (!Strings.isNullOrEmpty(telefoneDto)) {
                Telefone telefone = pessoa.getTelefonePorTipo(TipoTelefone.FIXO);
                if (telefone != null) {
                    telefone.setTelefone(telefoneDto);
                } else {
                    pessoa.getTelefones().add(new Telefone(pessoa, telefoneDto, false, TipoTelefone.FIXO));
                }
            }
            em.merge(pessoa);
            UsuarioWeb usuarioWeb = usuarioWebFacade.buscarUsuarioWebByIdPessoa(pessoa.getId());
            usuarioWeb.setEmail(pessoa.getEmail());
            em.merge(usuarioWeb);
        }

        return update(cmc);
    }

    public EconomicoCNAE buscarEconomicoCnaeDaPessoaCnae(PessoaCNAE pessoaCNAE) {
        StringBuilder hql = new StringBuilder("select economicoCNAE from EconomicoCNAE economicoCNAE ")
            .append(" inner join economicoCNAE.cadastroEconomico cadastro")
            .append(" where cadastro.pessoa = :pessoa ")
            .append(" and economicoCNAE.cnae = :cnae ")
            .append(" and economicoCNAE.tipo = :tipo ");
        Query q = em.createQuery(hql.toString());
        q.setParameter("pessoa", pessoaCNAE.getPessoa());
        q.setParameter("cnae", pessoaCNAE.getCnae());
        q.setParameter("tipo", pessoaCNAE.getTipo());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (EconomicoCNAE) q.getResultList().get(0);
        }
        return null;
    }

    public Long recuperarIdRevisaoCadastroEconomicoHistoricoRedeSim(CadastroEconomico cadastroEconomico, HistoricoAlteracaoRedeSim historico) {
        String pattern = "dd/MM/yyyy HH24:MI";
        StringBuilder sql = new StringBuilder()
            .append("select max(ce.rev) from cadastroeconomico_aud ce ")
            .append("inner join revisaoauditoria rev on rev.id = ce.rev ")
            .append("where ce.id = :idCadastro ")
            .append("  and to_date(to_char(rev.DATAHORA,'").append(pattern)
            .append("'), '").append(pattern).append("') = to_date(to_char(:dataHora,'")
            .append(pattern).append("'), '").append(pattern).append("') ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idCadastro", cadastroEconomico.getId());
        q.setParameter("dataHora", historico.getDataAlteracao());
        try {
            return ((BigDecimal) q.getSingleResult()).longValue();
        } catch (Exception ex) {
            return null;
        }
    }

    public boolean isAutonomoMotorista(CadastroEconomico cmc) {
        if (cmc.getId() != null) {
            String sql = " select ptransp.* from permissaotransporte ptransp " +
                " inner join permissionario p on ptransp.id = p.permissaotransporte_id " +
                " inner join cadastroeconomico cmc on p.cadastroeconomico_id = cmc.id " +
                " where to_date(:dataAtual, 'dd/MM/yyyy') between p.iniciovigencia " +
                " and coalesce(p.finalvigencia, to_date(:dataAtual, 'dd/MM/yyyy')) " +
                " and cmc.id = :idCMC ";

            Query q = em.createNativeQuery(sql);
            q.setParameter("dataAtual", DataUtil.getDataFormatada(new Date(), "dd/MM/yyyy"));
            q.setParameter("idCMC", cmc.getId());
            return !q.getResultList().isEmpty();
        }
        return false;
    }

    public List<CadastroEconomico> buscarCadastroEconomicoSocioAdministrador(String cnpjSocioAdministrador) throws Exception {
        String existsSocio = " exists (select 1" +
            "                             from sociedadecadastroeconomico sce " +
            "                            left join pessoafisica sce_pf on sce_pf.id = sce.socio_id " +
            "                            left join pessoajuridica sce_pj on sce_pj.id = sce.socio_id " +
            "                          where regexp_replace(coalesce(sce_pf.cpf, sce_pj.cnpj), '[^0-9]*', '') = '" +
            StringUtil.retornaApenasNumeros(cnpjSocioAdministrador) + "') ";

        return buscarCadastroEconomico(Lists.newArrayList(new ParametroQuery(Lists.newArrayList(new ParametroQueryCampo(existsSocio)))));
    }

    public List<CadastroEconomico> buscarCadastroEconomicoContador(String cpfContador) throws Exception {
        String campoCpfCnpjSocio = " regexp_replace(ec_pf.cpf, '[^0-9]*', '') ";

        List<ParametroQuery> parametros = Lists.newArrayList();
        parametros.add(new ParametroQuery(
            Lists.newArrayList(
                new ParametroQueryCampo(campoCpfCnpjSocio, Operador.IGUAL, StringUtil.retornaApenasNumeros(cpfContador)))));

        return buscarCadastroEconomico(parametros);
    }

    public List<CadastroEconomico> buscarCadastroEconomico(List<ParametroQuery> parametros) throws Exception {
        String sql = " select c.*, ce.* " +
            "   from cadastroeconomico ce " +
            "  inner join cadastro c on c.id = ce.id " +
            "  left join escritoriocontabil ec on ec.id = ce.escritoriocontabil_id " +
            "  left join pessoafisica ec_pf on ec_pf.id = ec.responsavel_id " +
            "  inner join situacaocadastroeconomico sce on sce.id = (select max(ce_s.situacaocadastroeconomico_id) " +
            "                                                           from ce_situacaocadastral ce_s " +
            "                                                        where ce_s.cadastroeconomico_id = ce.id) " +
            "  inner join enquadramentofiscal eq on eq.cadastroeconomico_id = ce.id and eq.fimvigencia is null ";

        return new GeradorQuery(em, CadastroEconomico.class, sql).parametros(parametros).build().getResultList();
    }

    public List<CadastroEconomico> buscarCadastrosEconomicosAtivosPorPessoa(Pessoa pessoa) throws Exception {
        List<ParametroQueryCampo> campos = Lists.newArrayList();
        campos.add(new ParametroQueryCampo(" sce.situacaocadastral ", Operador.IN, "('" + SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name() + "', '" + SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name() + "')"));
        campos.add(new ParametroQueryCampo(" ce.pessoa_id ", Operador.IGUAL, pessoa.getId()));

        List<CadastroEconomico> retorno = buscarCadastroEconomico(Lists.newArrayList(new ParametroQuery(campos)));
        return retorno;
    }

    public List<CadastroEconomico> buscarCadastroEconomicoAtivoSimplesNacional() throws Exception {
        List<ParametroQueryCampo> campos = Lists.newArrayList();
        campos.add(new ParametroQueryCampo(" sce.situacaocadastral ", Operador.IN, "('" + SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name() + "', '" + SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name() + "')"));
        campos.add(new ParametroQueryCampo(" eq.regimetributario ", Operador.IGUAL, RegimeTributario.SIMPLES_NACIONAL.name()));

        return buscarCadastroEconomico(Lists.newArrayList(new ParametroQuery(campos)));
    }

    public CadastroEconomico buscarCadastroEconomicoPorInscricaoMunicipal(String inscricaoCadastral) {
        inscricaoCadastral = StringUtil.removeZerosEsquerda(inscricaoCadastral);
        String sql = "select c.*, ce.* " +
            "   from cadastroeconomico ce " +
            "  inner join cadastro c on c.id = ce.id " +
            " where ce.inscricaocadastral = :inscricaoCadastral ";
        Query q = em.createNativeQuery(sql, CadastroEconomico.class);
        q.setParameter("inscricaoCadastral", inscricaoCadastral);
        if (!q.getResultList().isEmpty()) {
            return (CadastroEconomico) q.getSingleResult();
        }
        return null;
    }

    public Page<PrestadorServicoNfseDTO> buscarcadastrosPorUsuarioNfse(Pageable pageable, String filtro, String login) {
        UsuarioWeb nfseUser = usuarioWebFacade.recuperarUsuarioPorLogin(login);
        List<PrestadorServicoNfseDTO> result;
        Integer count;
        if (nfseUser.isAdmin()) {
            result = buscarCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJ(filtro, pageable);
            count = contarCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJ(filtro);
        } else {
            result = buscarcadastrosPorUsuarioNfse(login, filtro, pageable);
            count = contarCadastrosPorUsuarioNfse(login, filtro);
        }

        return new PageImpl<>(result, pageable, count);
    }

    public List<PrestadorServicoNfseDTO> buscarcadastrosPorUsuarioNfse(String login, String filtro, Pageable pageable) {


        String sql = "select cmc.id, cmc.inscricaocadastral," +
            " coalesce(pf.nome,pj.razaosocial) as nomeRazao," +
            " coalesce(pf.nome,pj.nomefantasia) as nomeFantasia," +
            " coalesce(pf.cpf,pj.cnpj) as cpfCnpj " +
            getSelectPorUsuarioAndFiltro();

        Query q = em.createNativeQuery(sql);
        q.setParameter("login", login);
        q.setParameter("filtro", "%" + filtro.toLowerCase().trim() + "%");
        q.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        q.setMaxResults(pageable.getPageSize());

        List<Object[]> cadastros = q.getResultList();
        return getPrestadorServicoNfseDTOS(cadastros);
    }

    public Integer contarCadastrosPorUsuarioNfse(String login, String filtro) {
        String sql = "select count(cmc.id)" + getSelectPorUsuarioAndFiltro();
        Query q = em.createNativeQuery(sql);
        q.setParameter("login", login);
        q.setParameter("filtro", "%" + filtro.toLowerCase().trim() + "%");

        return ((Number) q.getSingleResult()).intValue();
    }

    public String getSelectPorUsuarioAndFiltro() {
        return " from CadastroEconomico cmc " +
            " inner join UserNfseCadastroEconomico userCmc on userCmc.cadastroEconomico_id = cmc.id" +
            " inner join usuarioweb u on u.id = userCmc.usuarioNfse_id" +
            " left join pessoafisica pf on pf.id = cmc.pessoa_id " +
            " left join pessoajuridica pj on pj.id = cmc.pessoa_id " +
            " where u.login = :login and ((trim(lower(pf.nome)) like :filtro or replace(replace(pf.cpf,'.',''),'-','') = replace(replace(:filtro,'.',''),'-',''))" +
            " or (trim(lower(pj.razaoSocial)) like :filtro or replace(replace(replace(pj.cnpj,'.',''),'-',''), '/', '') = replace(replace(replace(:filtro,'.',''),'-',''), '/', '')) or cmc.inscricaoCadastral like :filtro )";
    }

    public List<PrestadorServicoNfseDTO> buscarCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJ(String parte, Pageable pageable) {
        StringBuilder hql = new StringBuilder("select cmc.id, ")
            .append(" cmc.inscricaocadastral, ")
            .append(" coalesce(pf.nome,pj.razaosocial) nomeRazaoSocial, ")
            .append(" coalesce(pf.nome,pj.nomefantasia) nomeFantasia, ")
            .append(" coalesce(pf.cpf,pj.cnpj) cpfCnpj ")
            .append(getFromCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJ());
        Query q = em.createNativeQuery(hql.toString());
        q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
        q.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        q.setMaxResults(pageable.getPageSize());

        List<Object[]> objs = q.getResultList();
        return getPrestadorServicoNfseDTOS(objs);
    }


    private List<PrestadorServicoNfseDTO> getPrestadorServicoNfseDTOS(List<Object[]> objs) {
        List<PrestadorServicoNfseDTO> retorno = Lists.newLinkedList();
        for (Object[] obj : objs) {
            retorno.add(new PrestadorServicoNfseDTO(((BigDecimal) obj[0]).longValue(),
                obj[1] != null ? obj[1].toString() : " ",
                obj[2] != null ? obj[2].toString() : " ",
                obj[3] != null ? obj[3].toString() : " ",
                obj[4] != null ? obj[4].toString() : " "
            ));
        }

        return retorno;
    }

    public List<CadastroEconomico> buscarCadastrosEconomicosPorCnpj(String cnpj) {
        if (Strings.isNullOrEmpty(cnpj)) {
            return Lists.newArrayList();
        }
        String sql = "select c.*, ce.* " +
            "   from cadastroeconomico ce" +
            "   inner join cadastro c on c.id = ce.id " +
            "   inner join pessoajuridica p on p.id = ce.pessoa_id " +
            "   inner join situacaocadastroeconomico sce on sce.id = " +
            "   (select max(rel.situacaocadastroeconomico_id) from ce_situacaocadastral rel where rel.cadastroeconomico_id = ce.id) " +
            " where replace(replace(replace(p.cnpj,'.',''),'-',''),'/','') = :cnpj " +
            "   and sce.situacaocadastral in (:ativo) ";


        Query q = em.createNativeQuery(sql, CadastroEconomico.class);
        q.setParameter("cnpj", cnpj.replace(".", "").replace("-", "").replace("/", ""));
        q.setParameter("ativo", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name(), SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name()));
        if (!q.getResultList().isEmpty()) {
            List<CadastroEconomico> cadastroEconomicos = q.getResultList();
            for (CadastroEconomico cadastroEconomico : cadastroEconomicos) {
                inicializarDependencias(cadastroEconomico);
            }
            return cadastroEconomicos;
        }
        return Lists.newArrayList();
    }

    public CadastroEconomico salvar(PrestadorServicoNfseDTO dto) throws ExcecaoNegocioGenerica {
        try {
            Date hoje = new Date();
            CadastroEconomico cadastroEconomico = new CadastroEconomico();
            cadastroEconomico.setInscricaoCadastral(dto.getInscricaoMunicipal());
            cadastroEconomico.setDataCadastro(hoje);
            if (dto.getNaturezaJuridica() != null) {
                cadastroEconomico.setNaturezaJuridica(naturezaJuridicaFacade.recuperar(dto.getNaturezaJuridica().getId()));
            }
            adicionarSalvarPessoaArquivo(cadastroEconomico, dto);
            adicionarSituacao(cadastroEconomico, hoje, SituacaoCadastralCadastroEconomico.AGUARDANDO_AVALIACAO);
            adicionarEnquadramentoFiscal(cadastroEconomico, dto.getEnquadramentoFiscal(), hoje);
            adicionarSocios(cadastroEconomico, dto, hoje);
            adicionarServicos(cadastroEconomico, dto);
            adicionarCnaes(cadastroEconomico, dto, hoje);
            cadastroEconomico = update(cadastroEconomico);
            adicionarUsuarios(cadastroEconomico, dto);
            return cadastroEconomico;
        } catch (ExcecaoNegocioGenerica e) {
            logger.error("Operação não permitida ao salvar o cadastro econômico via DTO da Nsfe");
            logger.debug("Error: ", e);
            throw e;
        } catch (Exception e) {
            logger.error("Não foi possível salvar o cadastro econômico via DTO da Nsfe");
            logger.debug("Error: ", e);
            throw new ExcecaoNegocioGenerica("Não foi possível salvar o cadastro");
        }
    }

    public void salvarConfiguracoesTributosFederais(TributosFederaisNfseDTO dto) {
        CadastroEconomico cadastroEconomico = recuperar(dto.getPrestadorServicosId());
        TributosFederais tributosFederais = new TributosFederais();
        tributosFederais.setCofins(dto.getCofins());
        tributosFederais.setRetencaoCofins(dto.getRetencaoCofins());
        tributosFederais.setCpp(dto.getCpp());
        tributosFederais.setRetencaoCpp(dto.getRetencaoCpp());
        tributosFederais.setCsll(dto.getCsll());
        tributosFederais.setRetencaoCsll(dto.getRetencaoCsll());
        tributosFederais.setInss(dto.getInss());
        tributosFederais.setRetencaoInss(dto.getRetencaoInss());
        tributosFederais.setIrrf(dto.getIrrf());
        tributosFederais.setRetencaoIrrf(dto.getRetencaoIrrf());
        tributosFederais.setOutrasRetencoes(dto.getOutrasRetencoes());
        tributosFederais.setRetencaoOutrasRetencoes(dto.getRetencaoOutrasRetencoes());
        tributosFederais.setPis(dto.getPis());
        tributosFederais.setRetencaoPis(dto.getRetencaoPis());
        cadastroEconomico.setTributosFederais(em.merge(tributosFederais));
        salvar(cadastroEconomico);
    }

    public TributosFederais buscarTributosFederais(Long prestadorId) {
        CadastroEconomico cadastroEconomico = recuperar(prestadorId);
        return cadastroEconomico.getTributosFederais();
    }

    public CadastroEconomico buscarCadastroEconomicoPorCnpjBase(String cnpjBase) throws Exception {
        List<ParametroQuery> parametros = Lists.newArrayList();
        parametros.add(new ParametroQuery(Lists.newArrayList(new ParametroQueryCampo("substrb(pj.cnpj, 1, 8)",
            Operador.IGUAL, cnpjBase))));
        List<CadastroEconomico> cadastros = buscarCadastroEconomico(parametros);
        if (cadastros != null && !cadastros.isEmpty()) {
            CadastroEconomico cadastroEconomico = cadastros.get(0);
            inicializarDependencias(cadastroEconomico);
            return cadastroEconomico;
        }
        return null;
    }

    public void importarPlanilhaSequenciaNfse(InputStream inputStream) throws IOException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();
        while (iterator.hasNext()) {
            Row row = iterator.next();

            String cmc = row.getCell(0).getStringCellValue();

            Long notasEmitidas = row.getCell(4) == null ? 0L : new Double(row.getCell(4).getNumericCellValue()).longValue();
            Long notasCanceladas = row.getCell(4) == null ? 0L : new Double(row.getCell(5).getNumericCellValue()).longValue();

            CadastroEconomico cadastroEconomico = jdbcCadastroEconomicoDAO.recuperarPorCmc(StringUtil.retornaApenasNumeros(cmc));
            if (cadastroEconomico != null) {
                cadastroEconomico.setNumeroUltimaNotaFiscal(notasEmitidas + notasCanceladas);
                jdbcCadastroEconomicoDAO.updateNumeroUltimaNotaFiscal(cadastroEconomico);
            }
        }
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void ajustarDadosDaPlanilha(String cpfCnpj, String email, String regimeTributario, String emitente, String tipoIssqn, CadastroEconomico cadastroEconomico) {
        try {

            cadastroEconomico.getEnquadramentoVigente().setTipoNotaFiscalServico("Sim".equalsIgnoreCase(emitente) ? TipoNotaFiscalServico.ELETRONICA : TipoNotaFiscalServico.NAO_EMITE);
            cadastroEconomico.getEnquadramentoVigente().setSubstitutoTributario("Sim".equalsIgnoreCase(emitente));
            cadastroEconomico.getEnquadramentoVigente().setRegimeTributario("Simples Nacional".equalsIgnoreCase(regimeTributario) ? RegimeTributario.SIMPLES_NACIONAL : RegimeTributario.LUCRO_PRESUMIDO);

            TipoIssqn tipo = TipoIssqn.getPorDescricao(tipoIssqn);
            if (tipo != null) {
                cadastroEconomico.getEnquadramentoVigente().setTipoIssqn(tipo);
            }

            jdbcCadastroEconomicoDAO.updateEnquadramentoFiscal(cadastroEconomico.getEnquadramentoVigente());
            logger.error("Salvou o cadastro " + cadastroEconomico);
            UsuarioWeb usuarioWeb = usuarioWebFacade.recuperarUsuarioPorLogin(Util.removerCaracteresEspeciais(cpfCnpj));
            if (usuarioWeb == null) {
                criarUsuarioWeb(cadastroEconomico);
                logger.error("Criou o usuario " + usuarioWeb);
            } else {
                jdbcCadastroEconomicoDAO.updateEmailUsuario(email, usuarioWeb.getId());
                logger.error("Salvou o usuario " + usuarioWeb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String atribuirUrlPortal() {
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        String urlPortal = "";
        if (configuracaoTributario != null && !Strings.isNullOrEmpty(configuracaoTributario.getUrlPortalContribuinte()) &&
            configuracaoTributario.getUrlPortalContribuinte().endsWith("/")) {
            urlPortal = StringUtils.chop(configuracaoTributario.getUrlPortalContribuinte());
        }
        return urlPortal;
    }

    public CadastroEconomico inicializarHistoricosImpressao(Long id) {
        CadastroEconomico cadastroEconomico = super.recuperar(id);
        Hibernate.initialize(cadastroEconomico.getCadastroEconomicoImpressaoHists());
        return cadastroEconomico;
    }

    public byte[] gerarBcm(CadastroEconomico cadastroEconomico) throws IOException {
        AssistenteRelatorioBCE assistente = new AssistenteRelatorioBCE();
        UsuarioSistema usuarioSistema = Util.recuperarUsuarioCorrente();
        assistente.setTipoRelatorio(1);
        assistente.setWhere(new StringBuilder());
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();

        if (cadastroEconomico != null) {
            assistente.getWhere().append(" and ce.id = '").append(cadastroEconomico.getId()).append("' ");
            assistente.setOrdemSql(" order by ce.inscricaocadastral ");
        }

        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeRelatorio("Boletim De Cadastro Mobiliário - BCM");
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("USUARIO", usuarioSistema != null ? usuarioSistema.getNome() : "");
        dto.adicionarParametro("LOGIN", usuarioSistema != null ? usuarioSistema.getNome() : "");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
        dto.adicionarParametro("NOMERELATORIO", "BOLETIM DE CADASTRO MOBILIÁRIO - BCM");
        dto.adicionarParametro("URL_PORTAL", atribuirUrlPortal());
        dto.adicionarParametro("TIPO_RELATORIO", assistente.getTipoRelatorio().equals(0));
        dto.adicionarParametro("FILTROS", "Inscrição Cadastral : " + cadastroEconomico.getInscricaoCadastral());
        dto.adicionarParametro("WHERE", assistente.getWhere());
        dto.adicionarParametro("ORDERBY", assistente.getOrdemSql());
        dto.setApi("tributario/boletim-cadastro-mobiliario/");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);

        ResponseEntity<byte[]> responseEntity =
            new RestTemplate().exchange(configuracao.getUrlCompleta(dto.getApi() + "gerar-sincrono"), HttpMethod.POST, request, byte[].class);

        byte[] bytes = responseEntity.getBody();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
        baos.write(bytes);
        return bytes;
    }

    public void enviarBcmParaRedeSim(CadastroEconomico cadastroEconomico) throws Exception {
        byte[] bytes = gerarBcm(cadastroEconomico);
        if (bytes != null) {
            calculoAlvaraFacade.montarEventoRedeSimAndConfirmarRespostaBCM(bytes, cadastroEconomico,
                TipoArquivoLogAlvaraRedeSim.BCM);
            if (FacesContext.getCurrentInstance() != null) {
                FacesUtil.addOperacaoRealizada("O BCM foi enviado para RedeSim com sucesso.");
            }
        }
    }

    public List<CadastroEconomico> buscarCadastrosEmitenteNfse() {
        String sql = " select c.*, ce.* " +
            "   from cadastroeconomico ce " +
            "  inner join cadastro c on c.id = ce.id " +
            "  inner join enquadramentofiscal ef on ef.cadastroeconomico_id = ce.id and ef.fimvigencia is null " +
            "  inner join ce_situacaocadastral ce_s on ce_s.cadastroeconomico_id = ce.id " +
            "  inner join situacaocadastroeconomico s on s.id = ce_s.situacaocadastroeconomico_id " +
            "where ef.tiponotafiscalservico = :tiponota " +
            "  and s.dataregistro = (select max(s_s.dataregistro) " +
            "                           from situacaocadastroeconomico s_s " +
            "                          inner join ce_situacaocadastral s_ce_s on s_ce_s.situacaocadastroeconomico_id = s_s.id " +
            "                        where s_ce_s.cadastroeconomico_id = ce.id) " +
            "  and s.situacaocadastral in (:situacoes) ";
        Query q = em
            .createNativeQuery(sql, CadastroEconomico.class)
            .setParameter("tiponota", TipoNotaFiscalServico.ELETRONICA.name())
            .setParameter("situacoes", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name(), SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name()));
        return q.getResultList();
    }

    public void criarNotificacaoDeCalculoDeAlvara(CadastroEconomico cadastroEconomico, boolean isMei) {
        NotificacaoService.getService().marcarNotificaoComoLidaPorIdDoLink(cadastroEconomico.getId());
        if (!isMei) {
            String mensagem = "Obrigatório calcular um novo Alvará para o Cadastro Econômico " + cadastroEconomico.getInscricaoCadastral();
            String link = "/calculo-alvara/notificacao/" + cadastroEconomico.getId() + "/";
            NotificacaoService.getService().notificar(TipoNotificacao.CALCULO_ALVARA.getDescricao(),
                mensagem, link, Notificacao.Gravidade.ATENCAO, TipoNotificacao.CALCULO_ALVARA);
        }
    }

    public List<CadastroEconomico> buscarCadastroEconomicoPorUsuarioNfse(UsuarioWeb usuarioWeb, String parte) {
        String sql = " select c.*, ce.* " +
            "   from cadastroeconomico ce " +
            "  inner join cadastro c on c.id = ce.id " +
            "  left join pessoafisica pf on pf.id = ce.pessoa_id " +
            "  left join pessoajuridica pj on pj.id = ce.pessoa_id " +
            "  inner join usernfsecadastroeconomico unce on unce.cadastroeconomico_id = ce.id " +
            " where unce.usuarionfse_id = :id_usuarionfse " +
            "   and ( replace(replace(replace(coalesce(pf.cpf, pj.cnpj), '.', ''), '-', ''), '/', '') like replace(replace(replace(:parte, '.', ''), '/', ''), '-', '')  or " +
            "         lower(coalesce(pf.nome, pj.razaosocial)) like :parte or " +
            "         ce.inscricaocadastral like :parte ) ";
        Query q = em
            .createNativeQuery(sql, CadastroEconomico.class)
            .setParameter("id_usuarionfse", usuarioWeb.getId())
            .setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<CadastroEconomico> buscarCMCsPorNomeRazaoSocialOrCpfCnpjOrSituacao(String parte) {
        String sql = "select cmc.id from cadastroeconomico cmc                                        " +
            "inner join ce_situacaocadastral ce_sit on cmc.id = ce_sit.cadastroeconomico_id           " +
            "inner join situacaocadastroeconomico sit on ce_sit.situacaocadastroeconomico_id = sit.id " +
            "left join pessoafisica pf on pf.id = cmc.pessoa_id                                       " +
            "left join pessoajuridica pj on pj.id = cmc.pessoa_id                                     " +
            "where (trim(lower(coalesce(pf.nome, pj.razaosocial))) like :filtro                       " +
            "or coalesce(replace(replace(replace(pf.cpf,'.',''),'-',''),'/',''),                      " +
            "replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','')) like :filtro                    " +
            "or cmc.inscricaocadastral like :filtro)                                                  " +
            "and sit.dataregistro = (select max(s.dataregistro) from situacaocadastroeconomico s      " +
            "inner join ce_situacaocadastral ce on s.id = ce.situacaocadastroeconomico_id             " +
            "where ce.cadastroeconomico_id = cmc.id) " +
            "and sit.situacaocadastral in (:situacoes)   ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("filtro", "%" + parte.toLowerCase().trim().toLowerCase().replace(".", "")
            .replace("-", "").replace("/", "") + "%");
        q.setParameter("situacoes", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name(), SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name()));
        q.setMaxResults(10);

        List<CadastroEconomico> cadastros = Lists.newArrayList();
        List<BigDecimal> ids = q.getResultList();

        for (BigDecimal id : ids) {
            cadastros.add(em.find(CadastroEconomico.class, id.longValue()));
        }
        return cadastros;
    }

    public Future<List<CadastroEconomico>> buscarCadastrosComAlvaraVigenteSemDebito(Exercicio exercicio,
                                                                                    GrauDeRiscoDTO grauDeRisco,
                                                                                    CadastroEconomico cadastroEconomico) {
        String sql = " select c.*, ce.* " +
            "    from cadastroeconomico ce " +
            "   inner join pessoa p on p.id = ce.pessoa_id " +
            "   inner join cadastro c on c.id = ce.id " +
            "   inner join ce_situacaocadastral sitcad on ce.id = sitcad.cadastroeconomico_id " +
            "   inner join situacaocadastroeconomico sitcmc on sitcad.situacaocadastroeconomico_id = sitcmc.id " +
            " where sitcmc.id = (select max(s.id) from situacaocadastroeconomico s " +
            "                    inner join ce_situacaocadastral ces on ces.situacaocadastroeconomico_id = s.id " +
            "                    where ces.cadastroeconomico_id = ce.id) " +
            "   and p.situacaocadastralpessoa = :situacaoPessoa " +
            "   and sitcmc.situacaocadastral in (:situacaoCmc) ";
        sql += getWhereGrauDeRisco(grauDeRisco);
        sql += (cadastroEconomico != null ? " and ce.id = :id_cadastro " : " ") +
            "  and exists (select 1 " +
            "                     from processocalculoalvara pca " +
            "                    inner join processocalculo pc on pc.id = pca.id " +
            "                    inner join exercicio e on e.id = pc.exercicio_id " +
            "                  where pca.situacaocalculoalvara != :estornado" +
            "                    and pca.cadastroeconomico_id = ce.id " +
            "                    and e.ano = :ano_anterior) " +
            "  and not exists (select 1 " +
            "                     from processocalculoalvara pca " +
            "                    inner join processocalculo pc on pc.id = pca.id " +
            "                    inner join exercicio e on e.id = pc.exercicio_id " +
            "                    inner join calculoalvara ca on pca.id = ca.processocalculoalvara_id " +
            "                    inner join calculo c on ca.id = c.id " +
            "                    left join valordivida vd on c.id = vd.calculo_id " +
            "                    left join parcelavalordivida pvd on vd.id = pvd.valordivida_id " +
            "                    left join situacaoparcelavalordivida spvd on pvd.situacaoatual_id = spvd.id " +
            "                    where pca.situacaocalculoalvara != :estornado" +
            "                    and (spvd.id is null or spvd.situacaoparcela != :cancelamento) " +
            "                    and pca.cadastroeconomico_id = ce.id " +
            "                    and e.ano = :ano) " +
            " order by ce.inscricaocadastral ";
        Query q = em.createNativeQuery(sql, CadastroEconomico.class);
        setParameterGrauDeRisco(q, grauDeRisco);
        if (cadastroEconomico != null) {
            q.setParameter("id_cadastro", cadastroEconomico.getId());
        }
        q.setParameter("ano", exercicio.getAno());
        q.setParameter("ano_anterior", exercicio.getAno() - 1);
        q.setParameter("estornado", SituacaoCalculoAlvara.ESTORNADO.name());
        q.setParameter("cancelamento", SituacaoParcela.CANCELAMENTO.name());
        q.setParameter("situacaoPessoa", SituacaoCadastralPessoa.ATIVO.name());
        q.setParameter("situacaoCmc", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name(), SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name()));
        return new AsyncResult<>(q.getResultList());
    }

    private void setParameterGrauDeRisco(Query q, GrauDeRiscoDTO grauDeRisco) {
        if (GrauDeRiscoDTO.BAIXO_RISCO_A.equals(grauDeRisco)) {
            q.setParameter("grauRiscoA", GrauDeRiscoDTO.BAIXO_RISCO_A.name());
        } else {
            q.setParameter("grauRiscoB", GrauDeRiscoDTO.BAIXO_RISCO_B.name());
            q.setParameter("grauRiscoAlto", GrauDeRiscoDTO.ALTO.name());
        }
        q.setParameter("cnaeAtivo", CNAE.Situacao.ATIVO.name());
    }

    public List<EnquadramentoAmbiental> buscarEnquadramentosAmbientais(CadastroEconomico cadastroEconomico) {
        return em.createQuery("from EnquadramentoAmbiental where cadastroEconomico = :cmc", EnquadramentoAmbiental.class)
            .setParameter("cmc", cadastroEconomico).getResultList();
    }

    public Future<List<CadastroEconomico>> buscarCadastrosAtivosSemAlvaraVigente(GrauDeRiscoDTO grauDeRisco, CadastroEconomico cadastroEconomico) {
        String sql = " select c.*, ce.* from cadastroeconomico ce " +
            " inner join pessoa p on p.id = ce.pessoa_id " +
            " inner join cadastro c on c.id = ce.id " +
            " inner join ce_situacaocadastral sitcad on ce.id = sitcad.cadastroeconomico_id " +
            " inner join situacaocadastroeconomico sitcmc on sitcad.situacaocadastroeconomico_id = sitcmc.id " +
            " inner join EnquadramentoFiscal enq on enq.cadastroeconomico_id = ce.id and enq.fimVigencia is null and enq.TIPOISSQN <> :mei" +
            " where sitcmc.id = (select max(s.id) from situacaocadastroeconomico s " +
            "                    inner join ce_situacaocadastral ces on ces.situacaocadastroeconomico_id = s.id " +
            "                    where ces.cadastroeconomico_id = ce.id) " +
            " and p.situacaocadastralpessoa = :situacaoPessoa " +
            " and sitcmc.situacaocadastral in (:situacaoCmc) ";
        sql += getWhereGrauDeRisco(grauDeRisco);
        sql += " and not exists (select 1 from processocalculoalvara pca " +
            "                 inner join processocalculo pc on pc.id = pca.id " +
            "                 inner join exercicio e on e.id = pc.exercicio_id " +
            "                 where pca.situacaocalculoalvara <> :estornado" +
            "                   and pca.cadastroeconomico_id = ce.id) " +
            (cadastroEconomico != null ? " and ce.id = :id_cadastro " : " ") +
            " order by ce.inscricaocadastral ";
        Query q = em.createNativeQuery(sql, CadastroEconomico.class);
        setParameterGrauDeRisco(q, grauDeRisco);
        if (cadastroEconomico != null) {
            q.setParameter("id_cadastro", cadastroEconomico.getId());
        }
        q.setParameter("situacaoPessoa", SituacaoCadastralPessoa.ATIVO.name());
        q.setParameter("situacaoCmc", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name(), SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name()));
        q.setParameter("cnaeAtivo", CNAE.Situacao.ATIVO.name());
        q.setParameter("estornado", SituacaoCalculoAlvara.ESTORNADO.name());
        q.setParameter("mei", TipoIssqn.MEI.name());
        return new AsyncResult<>(q.getResultList());
    }

    private String getWhereGrauDeRisco(GrauDeRiscoDTO grauDeRisco) {
        if (GrauDeRiscoDTO.BAIXO_RISCO_A.equals(grauDeRisco)) {
            return " and exists (select 1 from economicocnae ec " +
                "                 inner join cnae c on c.id = ec.cnae_id " +
                "                 where ec.cadastroeconomico_id = ce.id and c.situacao = :cnaeAtivo " +
                "                   and current_date between ec.inicio and coalesce(ec.fim, current_date) " +
                "                   and c.grauderisco = :grauRiscoA) " +
                " and not exists (select 1 from economicocnae ec " +
                "                 inner join cnae c on c.id = ec.cnae_id " +
                "                 where ec.cadastroeconomico_id = ce.id and c.situacao = :cnaeAtivo " +
                "                   and current_date between ec.inicio and coalesce(ec.fim, current_date) " +
                "                   and c.grauderisco <> :grauRiscoA) ";
        }
        return " and exists (select 1 from economicocnae ec " +
            "                 inner join cnae c on c.id = ec.cnae_id " +
            "                 where ec.cadastroeconomico_id = ce.id and c.situacao = :cnaeAtivo " +
            "                   and current_date between ec.inicio and coalesce(ec.fim, current_date) " +
            "                   and c.grauderisco = :grauRiscoB) " +
            " and not exists (select 1 from economicocnae ec " +
            "                 inner join cnae c on c.id = ec.cnae_id " +
            "                 where ec.cadastroeconomico_id = ce.id and c.situacao = :cnaeAtivo " +
            "                   and current_date between ec.inicio and coalesce(ec.fim, current_date) " +
            "                   and c.grauderisco = :grauRiscoAlto) ";
    }

    public ParametroETRFacade getParametroETRFacade() {
        return parametroETRFacade;
    }

    public RequerimentoLicenciamentoETRFacade getRequerimentoLicenciamentoETRFacade() {
        return requerimentoLicenciamentoETRFacade;
    }

    public HistoricoInscricaoCadastralFacade getHistoricoInscricaoCadastralFacade() {
        return historicoInscricaoCadastralFacade;
    }

}

