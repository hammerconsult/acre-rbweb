/*
 * Codigo gerado automaticamente em Thu Mar 17 11:36:45 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.consultaentidade.ConsultaEntidade;
import br.com.webpublico.consultaentidade.ConsultaEntidadeFacade;
import br.com.webpublico.consultaentidade.FiltroConsultaEntidade;
import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.AuditoriaFacade;
import br.com.webpublico.negocios.tributario.auxiliares.CalculadorIPTU;
import br.com.webpublico.nfse.domain.dtos.CadastroImobiliarioSearchNfseDTO;
import br.com.webpublico.nfse.domain.dtos.Operador;
import br.com.webpublico.nfse.domain.dtos.ParametroQuery;
import br.com.webpublico.nfse.domain.dtos.ParametroQueryCampo;
import br.com.webpublico.nfse.util.GeradorQuery;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
public class CadastroImobiliarioFacade extends AbstractFacade<CadastroImobiliario> {

    private static final Logger logger = LoggerFactory.getLogger(CadastroImobiliarioFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private SetorFacade setorFacade;
    @EJB
    private QuadraFacade quadraFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AtributoFacade atributoFacade;
    @EJB
    private ProcessoFacade processoFacade;
    @EJB
    private CartorioFacade cartorioFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    @EJB
    private UFFacade ufFacade;
    @EJB
    private CidadeFacade cidadeFacade;
    @EJB
    private HistoricoFacade historicoFacade;
    @EJB
    private UnidadeExternaFacade unidadeExternaFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private CalculaITBIFacade calculaITBIFacade;
    @EJB
    private AuditoriaFacade auditoriaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ConsultaEntidadeFacade consultaEntidadeFacade;
    @EJB
    private AlvaraConstrucaoFacade alvaraConstrucaoFacade;

    public CadastroImobiliarioFacade() {
        super(CadastroImobiliario.class);
    }

    public CalculaITBIFacade getCalculaITBIFacade() {
        return calculaITBIFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CadastroImobiliario recuperarSemCalcular(Object id) {
        Query q = em.createQuery("from CadastroImobiliario where id = :id");
        q.setParameter("id", id);
        List<CadastroImobiliario> lista = q.getResultList();
        if (!lista.isEmpty()) {
            CadastroImobiliario ci = (CadastroImobiliario) lista.get(0);
            Hibernate.initialize(ci.getPropriedade());
            Hibernate.initialize(ci.getPropriedadeCartorio());
            Hibernate.initialize(ci.getConstrucoes());
            Hibernate.initialize(ci.getLote().getTestadas());
            Hibernate.initialize(ci.getLote().getCaracteristicasLote());
            Hibernate.initialize(ci.getListaCompromissarios());
            return ci;
        }
        return null;
    }

    public CadastroImobiliario inicializarHistoricosImpressao(Long id) {
        CadastroImobiliario cadastroImobiliario = super.recuperar(id);
        Hibernate.initialize(cadastroImobiliario.getCadastroImobiliarioImpressaoHists());
        return cadastroImobiliario;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public CadastroImobiliario recuperar(Object id) {
        Query q = em.createQuery("from CadastroImobiliario where id = :id");
        q.setParameter("id", id);
        List<CadastroImobiliario> lista = q.getResultList();
        atualizaValoresDosCadastros(lista);
        if (!q.getResultList().isEmpty()) {
            CadastroImobiliario ci = (CadastroImobiliario) q.getResultList().get(0);
            ci.getPropriedade().size();
            ci.getListaCompromissarios().size();
            ci.getPropriedadeCartorio().size();
            ci.getConstrucoes().size();
            ci.getCaracteristicasBci().size();
            for (Construcao construcao : ci.getConstrucoes()) {
                if (construcao.getEventos() != null) {
                    construcao.getEventos().size();
                }
                if (construcao.getCaracteristicasConstrucao() != null) {
                    construcao.getCaracteristicasConstrucao().size();
                }
            }
            ci.getEventosCalculoBCI().size();
            ci.getInscricoesAnterioes().size();
            ci.getIsencoes().size();
            if (ci.getObservacoes() != null) {
                ci.getObservacoes().hashCode();
            }
            if (ci.getDetentorArquivoComposicao() != null) {
                Hibernate.initialize(ci.getDetentorArquivoComposicao().getArquivosComposicao());
            }
            ci.getHistoricos().size();
            if (ci.getLote() != null) {
                ci.getLote().getTestadas().size();
                ci.getLote().getCaracteristicasLote().size();
                if (ci.getLote() != null && ci.getLote().getQuadra() != null) {
                    quadraFacade.recuperar(ci.getLote().getQuadra().getId());
                }
            }
            return ci;
        }
        return null;
    }

    public CadastroImobiliario recuperarSimples(Object id) {
        return em.find(CadastroImobiliario.class, id);
    }

    public void atualizaValoresDosCadastros(List<CadastroImobiliario> lista) {
        Calendar c = GregorianCalendar.getInstance();
        Exercicio exercicio = exercicioFacade.getExercicioPorAno(c.get(Calendar.YEAR));
        atualizaValoresDosCadastros(lista, exercicio);
    }

    public void atualizaValoresDosCadastros(List<CadastroImobiliario> lista, Exercicio exercicio) {
        try {

            AtualizadorBCI atualizadorBCI = new AtualizadorBCI(configuracaoTributarioFacade.retornaUltimo(), lista, exercicio);
            atualizadorBCI.atualizando = true;
            atualizadorBCI.inicializa(lista.size());
            new CalculadorIPTU().atualizarValoresBCI(atualizadorBCI);
            em.flush();
        } catch (Exception ex) {
            logger.error("Erro ao atualiza os valores do imóvel: {}", ex.getMessage());
        }
    }

    public List<Construcao> getConstrucoes(CadastroImobiliario cadastroImobiliario) {
        return cadastroImobiliario.getConstrucoesAtivas();
    }

    public List<Object[]> getSetoresBairros() {
        return em.createNativeQuery(" select distinct setor.codigo as setor, quadra.codigo as quadra  from quadra\n" +
            " inner join setor on setor.id = quadra.setor_id and to_number(setor.codigo) > 0\n" +
            " order by setor.codigo, quadra.codigo").getResultList();
    }

    public void atualizaAreaTotalConstruida(CadastroImobiliario cadastroImobiliario) {
        if (cadastroImobiliario == null) {
            return;
        }
        List<Construcao> listaConstrucao = getConstrucoes(cadastroImobiliario);
        Double areaTotalConstruida = 0D;
        if (listaConstrucao != null) {
            for (Construcao constucao : listaConstrucao) {
                areaTotalConstruida += constucao.getAreaConstruida();
            }
        }
    }

    @Override
    public void salvarNovo(CadastroImobiliario entity) {
        em.persist(entity.getObservacoes());
        super.salvarNovo(entity);
    }

    public void alterar(CadastroImobiliario entity) {
        em.persist(em.merge(entity));
    }

    public CadastroImobiliario salvarCadastro(CadastroImobiliario entity) throws Exception {
        entity.popularCaracteristicas();
        entity.getLote().popularCaracteristicas();
        if (entity.getObservacoes() != null) {
            em.merge(entity.getObservacoes());
        }
        entity = em.merge(entity);

        return entity;
    }

    public void salvarDetentor(CadastroImobiliario entity) throws Exception {
        DetentorArquivoComposicao detentor = em.merge(entity.getDetentorArquivoComposicao());
        em.createNativeQuery(" update cadastroimobiliario set detentorArquivoComposicao_id = :idDetentor where id = :idCadastro ")
            .setParameter("idDetentor", detentor.getId())
            .setParameter("idCadastro", entity.getId())
            .executeUpdate();
    }

    public Boolean removerSomenteArquivo(CadastroImobiliario entity) throws Exception {

        List resultado = em.createNativeQuery(" select detentorArquivoComposicao_id from cadastroimobiliario where id = :id ")
            .setParameter("id", entity.getId())
            .getResultList();

        if (!resultado.isEmpty() && resultado.get(0) != null) {

            DetentorArquivoComposicao detentor = (DetentorArquivoComposicao) em.createNativeQuery(" select * from DetentorArquivoComposicao where id = :id ", DetentorArquivoComposicao.class)
                .setParameter("id", resultado.get(0))
                .getResultList().get(0);

            detentor.getArquivosComposicao().clear();
            em.merge(detentor);

            return true;
        }
        return false;
    }

    public List<BigDecimal> recuperaPorIntervalo(String cadastroImobiliarioInicio, String cadastroImobiliarioFim, Condominio condominio) {

        String sql = "select ci.id from cadastroimobiliario ci "
            + " left join lote on lote.id = ci.lote_id "
            + " left join lotecondominio condominio on condominio.lote_id = lote.id";


        String juncao = " where ";
        if (cadastroImobiliarioInicio != null && !"".equals(cadastroImobiliarioInicio)) {
            sql += juncao + " ci.inscricaoCadastral >= :ciInicio ";
            juncao = " and ";
        }
        if (cadastroImobiliarioFim != null && !"".equals(cadastroImobiliarioFim)) {
            sql += juncao + " ci.inscricaoCadastral <= :ciFim ";
            juncao = " and ";
        }
        if (condominio != null && condominio.getId() != null) {
            sql += juncao + " condominio.id = :condominio";
            juncao += " and ";
        }
        sql += juncao + " (ci.ativo =1 or ci.ativo is null)";
        Query q = em.createNativeQuery(sql);
        // q.setMaxResults(50);
        if (cadastroImobiliarioInicio != null && !"".equals(cadastroImobiliarioInicio)) {
            q.setParameter("ciInicio", cadastroImobiliarioInicio);
        }
        if (cadastroImobiliarioFim != null && !"".equals(cadastroImobiliarioFim)) {
            q.setParameter("ciFim", cadastroImobiliarioFim);
        }
        if (condominio != null && condominio.getId() != null) {
            q.setParameter("condominio", condominio.getId());
        }
        return q.getResultList();
    }

    @Override
    public List<CadastroImobiliario> listaFiltrando(String s, String... atributos) {
        return listaFiltrando(s, true, atributos);
    }

    public List<CadastroImobiliario> listaFiltrando(String s, boolean ativo, String... atributos) {
        String hql = "select new CadastroImobiliario(obj.id, obj.codigo, obj.inscricaoCadastral, obj.lote) " +
            " from CadastroImobiliario obj where coalesce(obj.ativo, true) = :ativo and ";
        for (String atributo : atributos) {
            hql += "lower(obj." + atributo + ") like :filtro OR ";
        }
        hql = hql.substring(0, hql.length() - 3);
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("ativo", ativo);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<CadastroImobiliario> listarNaoDevedores(String parte) {
        Query q = em.createQuery("select distinct calculo.cadastroImobiliario from ValorDivida vdivida "
            + " inner join vdivida.parcelaValorDividas parcela "
            + " inner join vdivida.calculo calculo "
            + " inner join calculo.cadastroImobiliario ci "
            + " where vdivida.dataQuitacao is null "
            + " and ci.codigo like :parte"
            + " and parcela.vencimento >= :hoje ");
        q.setParameter("parte", "%" + parte + "%");
        q.setParameter("hoje", new Date());

        try {
            return q.getResultList();
        } catch (NoResultException nre) {
            return new ArrayList<>();
        }
    }

    public CadastroImobiliario geraNovoPorDesmembramento(CadastroImobiliario originario, Lote novoLote, CadastroImobiliario novoCadastroImobiliario) {
        originario = em.find(CadastroImobiliario.class, originario.getId());
        Lote loteDeOrigem = originario.getLote();

        CadImobOriginario cadImobOrig = new CadImobOriginario();
        cadImobOrig.setOriginario(originario);
        cadImobOrig.setDataOperacao(new Date());

        novoLote.setAtributos(new HashMap<Atributo, ValorAtributo>());
        for (Atributo at : loteDeOrigem.getAtributos().keySet()) {
            ValorAtributo novoValorAtributo = new ValorAtributo();
            ValorAtributo valorAtributo = loteDeOrigem.getAtributos().get(at);
            novoValorAtributo.setAtributo(at);
            novoValorAtributo.setValorDate(valorAtributo.getValorDate());
            novoValorAtributo.setValorDecimal(valorAtributo.getValorDecimal());
            novoValorAtributo.setValorDiscreto(valorAtributo.getValorDiscreto());
            novoValorAtributo.setValorInteiro(valorAtributo.getValorInteiro());
            novoValorAtributo.setValorString(valorAtributo.getValorString());
            novoLote.getAtributos().put(at, novoValorAtributo);
        }
        novoLote.setComplemento(loteDeOrigem.getComplemento());
        //novoLote.setNumeroCorreio(loteDeOrigem.getNumeroCorreio());
        novoLote.setQuadra(loteDeOrigem.getQuadra());
        novoLote.setSetor(loteDeOrigem.getSetor());

        novoCadastroImobiliario.setAtivo(Boolean.TRUE);
        novoCadastroImobiliario.setDetentorArquivoComposicao(originario.getDetentorArquivoComposicao());
        novoCadastroImobiliario.setAtoLegalIsencao(originario.getAtoLegalIsencao());
        novoCadastroImobiliario.setPropriedade(new ArrayList<Propriedade>());

        for (Propriedade p : originario.getPropriedade()) {
            Propriedade novaPropriedade = new Propriedade();
            novaPropriedade.setAtual(p.getAtual());
            novaPropriedade.setDataRegistro(new Date());
            novaPropriedade.setFinalVigencia(p.getFinalVigencia());
            novaPropriedade.setImovel(novoCadastroImobiliario);
            novaPropriedade.setInicioVigencia(p.getInicioVigencia());
            novaPropriedade.setPessoa(p.getPessoa());
            novaPropriedade.setProporcao(p.getProporcao());
            novaPropriedade.setTipoProprietario(p.getTipoProprietario());
            novoCadastroImobiliario.getPropriedade().add(novaPropriedade);
        }

        novoCadastroImobiliario.setAtributos(new HashMap<Atributo, ValorAtributo>());
        for (Atributo at : originario.getAtributos().keySet()) {
            ValorAtributo novoValorAtributo = new ValorAtributo();
            ValorAtributo valorAtributo = originario.getAtributos().get(at);
            novoValorAtributo.setAtributo(at);
            novoValorAtributo.setValorDate(valorAtributo.getValorDate());
            novoValorAtributo.setValorDecimal(valorAtributo.getValorDecimal());
            novoValorAtributo.setValorDiscreto(valorAtributo.getValorDiscreto());
            novoValorAtributo.setValorInteiro(valorAtributo.getValorInteiro());
            novoValorAtributo.setValorString(valorAtributo.getValorString());
            novoCadastroImobiliario.getAtributos().put(at, novoValorAtributo);
        }
        novoCadastroImobiliario.setCartorio(originario.getCartorio());
        novoCadastroImobiliario.setFolhaRegistro(originario.getFolhaRegistro());
        novoCadastroImobiliario.setLivroRegistro(originario.getLivroRegistro());
        novoCadastroImobiliario.setMatriculaRegistro(originario.getMatriculaRegistro());
        novoCadastroImobiliario.setObservacoes(originario.getObservacoes());
        novoCadastroImobiliario.setProcesso(originario.getProcesso());
        novoCadastroImobiliario.setNumero(originario.getNumero());
        novoCadastroImobiliario.setLote(novoLote);


        return novoCadastroImobiliario;

    }

    public void geraNovoPorUnificacao(List<CadastroImobiliario> originarios, String novaInscricaoCadastral, List<Propriedade> propriedades, Historico historico) {
    }

    public List<CadastroImobiliario> listaFiltrandoPorQuadra(String parte, Quadra quadra) {
        if (quadra == null || quadra.getId() == null) {
            return this.listaFiltrando(parte.trim(), "codigo");
        } else {
            Query q = em.createQuery("from CadastroImobiliario ci where ci.ativo = true and "
                + "ci.codigo like :codigo and ci.lote.quadra = :quadra");
            q.setMaxResults(50);
            q.setParameter("codigo", "%" + parte.trim() + "%");
            q.setParameter("quadra", quadra);
            return q.getResultList();
        }

    }

    public List<CadastroImobiliario> listaPorQuadra(Quadra quadra) {
        if (quadra == null || quadra.getId() == null) {
            return this.lista();
        } else {
            Query q = em.createQuery("from CadastroImobiliario ci where coalesce(ci.ativo, 1) = 1 and "
                + " ci.lote.quadra = :quadra");
            q.setMaxResults(50);
            q.setParameter("quadra", quadra);
            return q.getResultList();
        }
    }

    public List<CadastroImobiliario> listaPorSetorQuadra(Setor setor, Quadra quadra) {
        if (quadra == null || quadra.getId() == null) {
            return this.lista();
        } else {
            Query q = em.createQuery(" select ci from CadastroImobiliario ci "
                + " inner join ci.lote lote "
                + " where ci.ativo is true and "
                + " lote.setor = :setor and "
                + " lote.quadra = :quadra"
                + " order by ci.codigo");
            q.setParameter("quadra", quadra);
            q.setParameter("setor", setor);
            return q.getResultList();
        }
    }

    public boolean validaCodigo(CadastroImobiliario ci) {
        String hql = " from CadastroImobiliario ci where ci.codigo = :cod";
        if (ci.getId() != null) {
            hql += " and ci != :ci";
        }
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("cod", ci.getCodigo());
        if (ci.getId() != null) {
            q.setParameter("ci", ci);
        }
        return (q.getResultList().isEmpty());
    }

    public List<CadastroImobiliario> listaAtivosPorCondominio(String parte, Condominio condominio) {
        String hql = "select ci from CadastroImobiliario ci left join ci.lote.lotesCondominio lc where coalesce(ci.ativo, 1) = 1 "
            + " and codigo like :codigo";
        if (condominio != null) {
            if (condominio.getId() == null) {
                hql += " and lc is empty";
            } else {
                hql += " and lc.condominio = :condominio";
            }
        }
        Query q = em.createQuery(hql);
        q.setParameter("codigo", "%" + parte.trim() + "%");
        if (condominio != null && condominio.getId() != null) {
            q.setParameter("condominio", condominio);
        }
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<CadastroImobiliario> listaPorFiltro(int inicio,
                                                    int max,
                                                    String filtroCodigo,
                                                    String filtroContribuinte,
                                                    String filtroInscricao,
                                                    String filtroLote,
                                                    String filtroQuadra,
                                                    String filtroSetor,
                                                    String filtroCompromissario,
                                                    int filtroMunicipioLoteamento,
                                                    int filtroTipoProprietario,
                                                    String filtroCpfCnpj,
                                                    String filtroLogradouro,
                                                    String filtroBairro,
                                                    String filtroNumeroLote,
                                                    boolean filtroSituacaoCadastral) {
        if (filtroTipoProprietario == 0) {
            filtroTipoProprietario = 1;
        }

        String juncao = " where ";
        String hql = "select ci from CadastroImobiliario ci "
            + "left join ci.propriedade p "
            + "left join ci.listaCompromissarios cp "
            + "left join ci.construcoes construcao "
            + "left join ci.lote lote "
            + "left join lote.testadas testada ";
        Date dataAtual = new Date();

        if (filtroCpfCnpj != null && !filtroCpfCnpj.isEmpty()) {
            hql += juncao + " (p.pessoa in (select pf from PessoaFisica pf where replace(replace(pf.cpf, '-', ''), '.', '') like :cpfCnpj)) or (p.pessoa in (select pj from PessoaJuridica pj where replace(replace(replace(pj.cnpj, '/', ''), '-', ''), '.', '') like :cpfCnpj))";
            juncao = " and ";
        }

        if (filtroLogradouro != null && !filtroLogradouro.isEmpty()) {

            hql += juncao + " lower(trim(testada.face.logradouro.tipoLogradouro.descricao) ||' '|| trim(testada.face.logradouro.nome)) like :logradouro";
            juncao = " and ";
        }

        if (filtroBairro != null && !filtroBairro.isEmpty()) {
            hql += juncao + " lower(testada.face.logradouro.bairro.descricao) like :bairro";
            juncao = " and ";
        }
        if (filtroSituacaoCadastral) {
            hql += juncao + "coalesce(ci.ativo, 1) = 1";
            juncao = " and ";
        } else {
            hql += juncao + "ci.ativo = 0";
            juncao = " and ";
        }

        if (filtroNumeroLote != null && !filtroNumeroLote.isEmpty()) {
            hql += juncao + " lower(ci.lote.numeroCorreio) like :numeroLote";
            juncao = " and ";
        }

        if (filtroCompromissario != null && !filtroCompromissario.trim().equals("")) {
            hql += juncao + " (cp.compromissario in ("
                + " from PessoaFisica obj "
                + " where lower(obj.nome) like :compromissario "
                + " or (replace(replace(replace(obj.cpf,'.',''),'-',''),'/','') like :compromissarioCPFCNPJ))"
                + " or "
                + " cp.compromissario in (from PessoaJuridica obj "
                + " where lower(obj.razaoSocial) like :compromissario "
                + " or (replace(replace(replace(obj.cnpj,'.',''),'-',''),'/','') like :compromissarioCPFCNPJ) "
                + " or lower(obj.nomeFantasia) like :compromissario)"
                + ")";
            juncao = " and ";
        }

        if (filtroTipoProprietario == 1) {
            hql += juncao + " (p.finalVigencia >= :dataAtual or p.finalVigencia is null)";
            juncao = " and ";
        } else if (filtroTipoProprietario == 2) {
            hql += juncao + " (p.finalVigencia <= :dataAtual)";
            juncao = " and ";
        }

        if (filtroContribuinte != null && !filtroContribuinte.trim().equals("")) {
            hql += juncao + " (p.pessoa in ("
                + " from PessoaFisica obj "
                + " where lower(obj.nome) like :contribuinte "
                + " or (replace(replace(replace(obj.cpf,'.',''),'-',''),'/','') like :contribuinteCPFCNPJ))"
                + " or "
                + " p.pessoa in (from PessoaJuridica obj "
                + " where lower(obj.razaoSocial) like :contribuinte "
                + " or (replace(replace(replace(obj.cnpj,'.',''),'-',''),'/','') like :contribuinteCPFCNPJ) "
                + " or lower(obj.nomeFantasia) like :contribuinte)"
                + ") and (coalesce(p.finalVigencia,current_date) >= current_date) ";
            juncao = " and ";
        }

        if (filtroCodigo != null && filtroCodigo.length() > 0) {
            hql += juncao + " ci.codigo like :filtroCodigo";
            juncao = " and ";
        }
        if (filtroInscricao != null && filtroInscricao.length() > 0) {
            hql += juncao + " ci.inscricaoCadastral like :filtroInscricao";
            juncao = " and ";
        }
        if (filtroMunicipioLoteamento != 0) {
            if (filtroMunicipioLoteamento == 1) {
                if (filtroLote.length() > 0 && filtroLote != null) {
                    hql += juncao + " ci.lote.codigoLote like :filtroLote";
                    juncao = " and ";
                }
                if (filtroQuadra.length() > 0 && filtroQuadra != null) {
                    hql += juncao + " lower(ci.lote.quadra.descricao) like :filtroQuadra";
                    juncao = " and ";
                }
            } else if (filtroMunicipioLoteamento == 2) {

                if (filtroLote.length() > 0 && filtroLote != null) {
                    hql += juncao + " lote.descricaoLoteamento like :filtroLote";
                    juncao = " and ";
                }
                if (filtroQuadra.length() > 0 && filtroQuadra != null) {

                    hql += juncao + " lower(lote.quadra.descricaoLoteamento) like :filtroQuadra";
                    juncao = " and ";
                }
            } else {
                if (filtroLote.length() > 0 && filtroLote != null) {
                    hql += juncao + " lote.codigoLote like :filtroLote or ci.lote.descricaoLoteamento like :filtroLote";
                    juncao = " and ";
                }
                if (filtroQuadra.length() > 0 && filtroQuadra != null) {
                    hql += juncao + " (lower(lote.quadra.descricao) like :filtroQuadra or lower(lote.quadra.descricaoLoteamento) like :filtroQuadra )";
                    juncao = " and ";
                }
            }
        }

        if (filtroSetor != null && filtroSetor.length() > 0) {
            hql += juncao + " lower(lote.quadra.setor.nome) like :filtroSetor";
            juncao = " and ";
        }

        Query q = em.createQuery(hql);
        q.setMaxResults(max + 3);
        q.setFirstResult(inicio);

        if (filtroCpfCnpj != null && filtroCpfCnpj.length() > 0) {
            q.setParameter("cpfCnpj", "%" + filtroCpfCnpj.trim().replace("/", "").replace("-", "").replace(".", "") + "%");
        }
        if (filtroCodigo != null && filtroCodigo.length() > 0) {
            q.setParameter("filtroCodigo", "%" + filtroCodigo.trim() + "%");
        }
        if (filtroBairro != null && filtroBairro.length() > 0) {
            q.setParameter("bairro", "%" + filtroBairro.trim().toLowerCase() + "%");
        }
        if (filtroNumeroLote != null && filtroNumeroLote.length() > 0) {
            q.setParameter("numeroLote", "%" + filtroNumeroLote.trim() + "%");
        }
        if (filtroMunicipioLoteamento != 0) {
            if (filtroQuadra.length() > 0 && filtroQuadra != null) {
                q.setParameter("filtroQuadra", "%" + filtroQuadra.trim().toLowerCase() + "%");
            }
        }
        if (filtroSetor != null && filtroSetor.length() > 0) {
            q.setParameter("filtroSetor", "%" + filtroSetor.trim().toLowerCase() + "%");
        }
        if (filtroInscricao != null && filtroInscricao.length() > 0) {
            q.setParameter("filtroInscricao", "%" + filtroInscricao.trim() + "%");
        }
        if (filtroLote != null && filtroLote.length() > 0) {
            q.setParameter("filtroLote", "%" + filtroLote.trim() + "%");
        }

        if (filtroCompromissario != null && !filtroCompromissario.trim().equals("")) {
            q.setParameter("compromissario", "%" + filtroCompromissario.toLowerCase() + "%");
            q.setParameter("compromissarioCPFCNPJ", "%" + filtroCompromissario.toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%");
        }

        if (filtroLogradouro != null && !filtroLogradouro.isEmpty()) {

            q.setParameter("logradouro", "%" + filtroLogradouro.toLowerCase().trim() + "%");
        }
        if (filtroContribuinte != null && !filtroContribuinte.trim().equals("")) {
            q.setParameter("contribuinte", "%" + filtroContribuinte.toLowerCase() + "%");
            q.setParameter("contribuinteCPFCNPJ", "%" + filtroContribuinte.toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%");
        }
        if (filtroTipoProprietario != 3) {
            q.setParameter("dataAtual", dataAtual);
        }

        ArrayList<CadastroImobiliario> lista;
        lista = new ArrayList<>(new HashSet<CadastroImobiliario>(q.getResultList()));

        for (CadastroImobiliario c : lista) {
            c.getPropriedade().size();
            c.getConstrucoes().size();
            c.getLote().getTestadas().size();
        }

        return lista;
    }

    public long recuperarSequenciaHistorico(CadastroImobiliario ci) {
        String sql = "select count(hist.id) from cadastroimobiliario ci inner join historico hist ON ci.id = hist.cadastro_id"
            + " where hist.cadastro_id = :cadastro";
        BigDecimal resultado = ((BigDecimal) em.createNativeQuery(sql).setParameter("cadastro", ci.getId()).getResultList().get(0));
        return resultado.add(BigDecimal.ONE).longValue();
    }

    @Override
    public List<CadastroImobiliario> lista() {
        return em.createQuery("from CadastroImobiliario").getResultList();
    }

    public Integer retornaTotalDigitosInscricaoCadastral(ConfiguracaoTributario cfg) {
        return cfg.getNumDigitosDistrito()
            + cfg.getNumDigitosSetor()
            + cfg.getNumDigitosQuadra()
            + cfg.getNumDigitosLote();
    }

    public Long calcularAutonomaPeloLote(Lote l) {
        String sql = "select ci.inscricaoCadastral from cadastroimobiliario ci "
            + " where ci.lote_id = :idLote order by to_number(ci.inscricaoCadastral) desc fetch first 1 rows only";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLote", l.getId());
        List<String> result = q.getResultList();
        if (!result.isEmpty()) {
            String inscricao = result.get(0);
            long retorno = Long.parseLong(inscricao.substring(inscricao.length() - 1));
            return retorno + 1;
        }
        return 1L;
    }

    public Lote calculaEnderecamentoPorInscricao(String inscricao) {
        Setor setor = new Setor();
        Quadra quadra = new Quadra();
        Lote lote = new Lote();
        if (!inscricao.trim().equals("")) {
            ConfiguracaoTributario cfg = configuracaoTributarioFacade.retornaUltimo();
            if (inscricao.length() < retornaTotalDigitosInscricaoCadastral(cfg)) {
                FacesUtil.addWarn("Atenção!", "A Inscrição Cadastral deve conter no mínimo 12 dígitos.");
            } else {
                int qtdDigitosDistrito = cfg.getNumDigitosDistrito();
                int qtdDigitosSetor = cfg.getNumDigitosSetor();
                int qtdDigitosQuadra = cfg.getNumDigitosQuadra();
                int qtdDigitosLote = cfg.getNumDigitosLote();

                String digitosDistrito = "";
                String digitosSetor = "";
                String digitosQuadra = "";
                String digitosLote = "";
                int indice = 0;
                try {
                    digitosDistrito = inscricao.substring(indice, qtdDigitosDistrito);
                    indice += qtdDigitosDistrito;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    digitosSetor = inscricao.substring(indice, qtdDigitosSetor + indice);
                    indice += qtdDigitosSetor;
                    setor = setorFacade.recuperarSetorPorCodigo(digitosSetor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    digitosQuadra = inscricao.substring(indice, qtdDigitosQuadra + indice);
                    indice += qtdDigitosQuadra;
                    if (setor != null && setor.getId() != null) {
                        quadra = quadraFacade.recuperaQuadraPorSetor(setor, digitosQuadra);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    digitosLote = inscricao.substring(indice, indice + qtdDigitosLote);
                    if (setor != null && setor.getId() != null && quadra != null && quadra.getId() != null) {
                        lote = loteFacade.recuperarPorCodigoQuadraSetor(digitosLote, quadra, setor);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return lote;
    }

    public String calculaInscricaoImobiliaria(CadastroImobiliario cadastro) throws ExcecaoNegocioGenerica {
        StringBuilder inscricao = new StringBuilder("");
        Lote lote;
        ConfiguracaoTributario cfg = configuracaoTributarioFacade.retornaUltimo();
        if (cfg == null ||
            (cfg.getNumDigitosDistrito() == null
                && cfg.getNumDigitosLote() == null
                && cfg.getNumDigitosQuadra() == null
                && cfg.getNumDigitosSetor() == null
                && cfg.getNumDigitosUnidade() == null)) {
            throw new ExcecaoNegocioGenerica("A configuração do Tributário não é valida");
        }
        String digitosSetor = "";
        String digitosQuadra = "";
        String digitosLote = "";
        if (cadastro.getLote() != null && cadastro.getLote().getId() != null) {
            lote = em.find(Lote.class, cadastro.getLote().getId());
            lote.getTestadas().size();
            if (lote.getSetor() != null && lote.getSetor().getCodigo() != null) {
                digitosSetor = StringUtil.preencheString(lote.getSetor().getCodigo().toString(), 3, '0');
            }
            if (lote.getQuadra() != null && lote.getQuadra().getCodigo() != null) {
                digitosQuadra = lote.getQuadra().getCodigo().toString();
            }

            if (lote.getCodigoLote() != null) {
                digitosLote = lote.getCodigoLote();
            }
        }
        inscricao.append(digitosSetor)
            .append(digitosQuadra)
            .append(digitosLote);
        ////System.out.println("Inscrição ---- " + inscricao.toString());
        return inscricao.toString();
    }

    public List<Ocorrencia> listaOcorrencias() {
        String hql = "from Ocorrencia";
        return em.createQuery(hql).getResultList();
    }

    public boolean imovelComPenhora(CadastroImobiliario cadastroImobiliario) {
        if (cadastroImobiliario == null || cadastroImobiliario.getId() == null) {
            return false;
        }
        String hql = "select p.cadastroImobiliario from Penhora p where p.cadastroImobiliario = :ci and p.dataBaixa is null";
        Query q = em.createQuery(hql);
        q.setParameter("ci", cadastroImobiliario);
        return !q.getResultList().isEmpty();
    }

    public Penhora retornaPenhoraDoImovel(CadastroImobiliario cadastroImobiliario) {
        if (cadastroImobiliario == null || cadastroImobiliario.getId() == null) {
            return null;
        }
        String hql = "select p from Penhora p where p.cadastroImobiliario = :ci and p.dataBaixa is null";
        Query q = em.createQuery(hql);
        q.setParameter("ci", cadastroImobiliario);
        q.setMaxResults(1);
        return !q.getResultList().isEmpty() ? (Penhora) q.getSingleResult() : null;
    }

    public boolean imovelComPenhoraComBloqueio(CadastroImobiliario cadastroImobiliario) {
        if (cadastroImobiliario == null || cadastroImobiliario.getId() == null) {
            return false;
        }
        String hql = "select p.cadastroImobiliario "
            + " from Penhora p where p.cadastroImobiliario = :ci and p.dataBaixa is null "
            + " and p.bloqueio = true";
        Query q = em.createQuery(hql);
        q.setParameter("ci", cadastroImobiliario);
        return !q.getResultList().isEmpty();
    }

    public List<Propriedade> recuperarProprietariosVigentes(CadastroImobiliario cadastroImobiliario) {
        List<Propriedade> propriedades;
        try {
            String sql = " select p.* from propriedade p " +
                " where p.imovel_id = :idImovel " +
                " and (p.finalvigencia is null or trunc(p.finalVigencia) > trunc(current_date)) ";

            Query q = em.createNativeQuery(sql, Propriedade.class);
            q.setParameter("idImovel", cadastroImobiliario.getId());

            propriedades = q.getResultList();
        } catch (Exception e) {
            logger.error("Erro ao recuperar proprietarios vigentes. ", e);
            return Lists.newArrayList();
        }
        return propriedades != null ? propriedades : Lists.<Propriedade>newArrayList();
    }

    public List<Propriedade> recuperarProprietariosNaoVigentes(CadastroImobiliario cadastroImobiliario) {
        List<Propriedade> propriedades;
        try {
            String sql = " select p.* from propriedade p " +
                " where p.imovel_id = :idImovel " +
                " and p.finalvigencia is not null ";

            Query q = em.createNativeQuery(sql, Propriedade.class);
            q.setParameter("idImovel", cadastroImobiliario.getId());

            propriedades = q.getResultList();
        } catch (Exception e) {
            logger.error("Erro ao recuperar proprietarios não vigentes. ", e);
            return Lists.newArrayList();
        }
        return propriedades != null ? propriedades : Lists.<Propriedade>newArrayList();
    }


    public List<Propriedade> recuperarPropriedadesVigentes(Pessoa pessoa) {
        try {
            String sql = "select * from Propriedade where pessoa_id = :idPessoa and (finalVigencia is null or finalVigencia > :dataAtual)";
            Query q = em.createNativeQuery(sql, Propriedade.class);

            q.setParameter("idPessoa", pessoa.getId());
            q.setParameter("dataAtual", new Date());
            List resultList = q.getResultList();
            return resultList;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return Lists.newArrayList();
        }
    }

    public Propriedade recuperarProprietarioPrincipal(Long idCadastro) {
        CadastroImobiliario ci = em.find(CadastroImobiliario.class, idCadastro);
        return recuperarProprietarioPrincipal(ci);
    }

    public Propriedade recuperarProprietarioPrincipal(CadastroImobiliario cadastroImobiliario) {
        Propriedade principal = null;
        if (cadastroImobiliario != null && cadastroImobiliario.getId() != null) {
            List<Propriedade> proprietariosVigentes = recuperarProprietariosVigentes(cadastroImobiliario);
            for (Propriedade p : proprietariosVigentes) {
                if (principal == null) {
                    principal = p;
                    continue;
                }

                if (p.getProporcao() > principal.getProporcao()) {
                    p = principal;
                }

                if ((p.getProporcao() == principal.getProporcao()) && (p.getId() > principal.getId())) {
                    p = principal;
                }
            }
        }
        return principal;
    }

    public List<Long> listaIDsAtivos(int maxResults) {
//        String hql = "select ci.id from CadastroImobiliario ci where (ci.ativo = true) and (size(ci.construcoes) > 0) and (ci.valoresCalculaveisCI.valorVenal is null or ci.valoresCalculaveisCI.valorVenal = 0)";
        String hql = "select ci.id from CadastroImobiliario ci where ci.inscricaoCadastral = '100307830166001'";
        Query q = em.createQuery(hql);
        q.setMaxResults(maxResults);
        return q.getResultList();
    }

    public List<FaceServico> getServicosFace(Face face) {
        String hql = "select servico from FaceServico servico where servico.face = :face";
        Query q = em.createQuery(hql);
        q.setParameter("face", face);
        return q.getResultList();
    }

    public LogradouroBairro recuperaLogradouroBairroCadastro(CadastroImobiliario cadastro) {
        String hql = " select logradouroBairro from CadastroImobiliario ci " +
            " join ci.lote lote " +
            " join lote.testadas testada " +
            " join testada.face face " +
            " join face.logradouroBairro logradouroBairro " +
            " where ci = :cadastro" +
            " and testada.principal = 1 ";

//        String sql = "select logradouroBairro.* "
//                + "      from cadastroimobiliario cad"
//                + " left join lote lote on cad.lote_id = lote.id"
//                + " left join testada testada on testada.lote_id = lote.id and testada.principal = 1 "
//                + " left join face face on testada.face_id = face.id"
//                + " left join logradouroBairro logradouroBairro on face.logradouroBairro_id = logradouroBairro.id"
//                + " where cad.id = :cadastro";
//        Query consulta = em.createNativeQuery(sql);
        Query consulta = em.createQuery(hql);
        //consulta.setParameter("cadastro", cadastro.getId());
        consulta.setParameter("cadastro", cadastro);
        List<LogradouroBairro> retorno = consulta.getResultList();
        if (!retorno.isEmpty()) {
            return retorno.get(0);
        }
        return null;
    }

    public CadastroImobiliario geraDesmembramentoIncorporadoNovo(CadastroImobiliario originario, Lote novoLote, CadastroImobiliario novoCadastroImobiliario) {
        Lote loteDeOrigem = originario.getLote();

        novoLote.setAtributos(new HashMap<Atributo, ValorAtributo>());
        for (Atributo at : loteDeOrigem.getAtributos().keySet()) {
            ValorAtributo novoValorAtributo = new ValorAtributo();
            ValorAtributo valorAtributo = loteDeOrigem.getAtributos().get(at);
            novoValorAtributo.setAtributo(at);
            novoValorAtributo.setValorDate(valorAtributo.getValorDate());
            novoValorAtributo.setValorDecimal(valorAtributo.getValorDecimal());
            novoValorAtributo.setValorDiscreto(valorAtributo.getValorDiscreto());
            novoValorAtributo.setValorInteiro(valorAtributo.getValorInteiro());
            novoValorAtributo.setValorString(valorAtributo.getValorString());
            novoLote.getAtributos().put(at, novoValorAtributo);
        }
        novoLote.setComplemento(loteDeOrigem.getComplemento());
        //novoLote.setNumeroCorreio(loteDeOrigem.getNumeroCorreio());
        novoLote.setQuadra(loteDeOrigem.getQuadra());
        novoLote.setSetor(loteDeOrigem.getSetor());

        novoCadastroImobiliario.setAtivo(Boolean.TRUE);
        novoCadastroImobiliario.setDetentorArquivoComposicao(originario.getDetentorArquivoComposicao());
        novoCadastroImobiliario.setAtoLegalIsencao(originario.getAtoLegalIsencao());
        novoCadastroImobiliario.setPropriedade(new ArrayList<Propriedade>());

        for (Propriedade p : originario.getPropriedade()) {
            Propriedade novaPropriedade = new Propriedade();
            novaPropriedade.setAtual(p.getAtual());
            novaPropriedade.setDataRegistro(new Date());
            novaPropriedade.setFinalVigencia(p.getFinalVigencia());
            novaPropriedade.setImovel(novoCadastroImobiliario);
            novaPropriedade.setInicioVigencia(p.getInicioVigencia());
            novaPropriedade.setPessoa(p.getPessoa());
            novaPropriedade.setProporcao(p.getProporcao());
            novaPropriedade.setTipoProprietario(p.getTipoProprietario());
            novoCadastroImobiliario.getPropriedade().add(novaPropriedade);
        }

        novoCadastroImobiliario.setAtributos(new HashMap<Atributo, ValorAtributo>());
        for (Atributo at : originario.getAtributos().keySet()) {
            ValorAtributo novoValorAtributo = new ValorAtributo();
            ValorAtributo valorAtributo = originario.getAtributos().get(at);
            novoValorAtributo.setAtributo(at);
            novoValorAtributo.setValorDate(valorAtributo.getValorDate());
            novoValorAtributo.setValorDecimal(valorAtributo.getValorDecimal());
            novoValorAtributo.setValorDiscreto(valorAtributo.getValorDiscreto());
            novoValorAtributo.setValorInteiro(valorAtributo.getValorInteiro());
            novoValorAtributo.setValorString(valorAtributo.getValorString());
            novoCadastroImobiliario.getAtributos().put(at, novoValorAtributo);
        }
        novoCadastroImobiliario.setCartorio(originario.getCartorio());
        novoCadastroImobiliario.setFolhaRegistro(originario.getFolhaRegistro());
        novoCadastroImobiliario.setLivroRegistro(originario.getLivroRegistro());
        novoCadastroImobiliario.setMatriculaRegistro(originario.getMatriculaRegistro());
        novoCadastroImobiliario.setObservacoes(originario.getObservacoes());
        novoCadastroImobiliario.setProcesso(originario.getProcesso());
        novoCadastroImobiliario.setNumero(originario.getNumero());
        novoCadastroImobiliario.setLote(novoLote);

        return novoCadastroImobiliario;
    }

    public CadastroImobiliario geraDesmembramentoIncorporadoOrigem(CadastroImobiliario originario, List<CadastroImobiliario> resultantes) {
        originario = recuperar(originario.getId());
        Lote loteDeOrigem = this.loteFacade.recuperar(originario.getLote().getId());


        for (CadastroImobiliario cadastroImobiliario : resultantes) {
            loteDeOrigem.setAreaLote(loteDeOrigem.getAreaLote() - cadastroImobiliario.getLote().getAreaLote());
            for (Testada t : cadastroImobiliario.getLote().getTestadas()) {
                for (Testada testada : loteDeOrigem.getTestadas()) {
                    if (t.getFace().getLogradouroBairro().equals(testada.getFace().getLogradouroBairro())) {
                        testada.setTamanho(testada.getTamanho().subtract(t.getTamanho()));
                    }
                }

            }
            List<Construcao> remover = new ArrayList<>();
            for (Construcao construcao : cadastroImobiliario.getConstrucoes()) {
                for (Construcao construcao1 : originario.getConstrucoes()) {
                    if (construcao.getDescricao().equals(construcao1.getDescricao()) && (construcao1.getAnoConstrucao() == construcao.getAnoConstrucao())) {
                        remover.add(construcao1);
                    }
                }
            }
            // originario.getConstrucoes().removeAll(remover);
        }
        return originario;
    }

    public CadastroImobiliario recuperaPorInscricao(String codigo) {
        return recuperaPorInscricao(codigo, true, true);
    }

    public CadastroImobiliario recuperaPorInscricao(String codigo, boolean atualizarValores, boolean recuperarDependencias) {
        String hql = "from CadastroImobiliario ci where ci.inscricaoCadastral = :inscricao";
        Query q = em.createQuery(hql);
        q.setParameter("inscricao", codigo);
        List<CadastroImobiliario> lista = q.getResultList();
        if (atualizarValores) {
            try {
                atualizaValoresDosCadastros(lista);
            } catch (Exception e) {
                logger.error("Erro ao atualizar os valores: " + e.getMessage());
            }
        }
        if (!lista.isEmpty()) {
            CadastroImobiliario ci = lista.get(0);
            if (recuperarDependencias) {
                ci.getPropriedade().size();
                ci.getListaCompromissarios().size();
                ci.getPropriedadeCartorio().size();
                for (Construcao construcao : ci.getConstrucoes()) {
                    construcao.getEventos().size();
                }
                if (ci.getDetentorArquivoComposicao() != null) {
                    Hibernate.initialize(ci.getDetentorArquivoComposicao().getArquivosComposicao());
                }
                ci.getInscricoesAnterioes().size();
                ci.getIsencoes().size();
                ci.getEventosCalculoBCI().size();
                if (ci.getObservacoes() != null) {
                    ci.getObservacoes().hashCode();
                }
                ci.getHistoricos().size();
            }
            return ci;
        }
        return null;
        //  throw new ExcecaoNegocioGenerica("Nenhum cadastro foi encontrado com o id informado");
    }

    public CadastroImobiliario recuperarTransmitentes(Object id) {
        CadastroImobiliario calculo = super.recuperar(id);
        calculo.getPropriedade().size();
        return calculo;
    }

    public List<Construcao> recuperarConstrucoes(CadastroImobiliario ci) {
        String sql = "select c from Construcao c " +
            " inner join c.eventos " +
            " where c.cancelada = false " +
            "   and c.imovel = :ci";

        Query q = em.createQuery(sql);
        q.setParameter("ci", ci);

        List<Construcao> cons = q.getResultList();
        for (Construcao c : cons) {
            c.getEventos().size();
        }
        return cons;
    }

    public List<Propriedade> recuperarPropriedadePorDataFinal(CadastroImobiliario ci, Date fim) {
        String sql = " select p.* from propriedade p " +
            " where p.imovel_id = :idImovel " +
            " and trunc(p.finalVigencia) <= to_date(:fim, 'dd/MM/yyyy') ";
        Query q = em.createNativeQuery(sql, Propriedade.class);
        q.setParameter("idImovel", ci.getId());
        q.setParameter("fim", DataUtil.getDataFormatada(fim));

        return q.getResultList();
    }

    public Object merge(Object o) {
        return em.merge(o);
    }

    public Object mergeCadastroImobiliario(CadastroImobiliario ci) {
        List<Testada> testadas = new ArrayList<>();
        for (Testada t : ci.getLote().getTestadas()) {
            testadas.add(em.merge(t));
        }

        ci.getLote().setTestadas(testadas);
        for (Construcao construcao : ci.getConstrucoes()) {
            construcao.getEventos().clear();
        }
        return em.merge(ci);
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void atualizaBCI(AtualizadorBCI a) {
        new CalculadorIPTU().atualizarValoresBCI(a);
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

//    ------------------------------------------- GETTERS FACADES -------------------------------------------

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }


    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public LoteFacade getLoteFacade() {
        return loteFacade;
    }

    public SetorFacade getSetorFacade() {
        return setorFacade;
    }

    public QuadraFacade getQuadraFacade() {
        return quadraFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public AtributoFacade getAtributoFacade() {
        return atributoFacade;
    }

    public ProcessoFacade getProcessoFacade() {
        return processoFacade;
    }

    public CartorioFacade getCartorioFacade() {
        return cartorioFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    public EnderecoFacade getEnderecoFacade() {
        return enderecoFacade;
    }

    public UFFacade getUfFacade() {
        return ufFacade;
    }

    public CidadeFacade getCidadeFacade() {
        return cidadeFacade;
    }


    public HistoricoFacade getHistoricoFacade() {
        return historicoFacade;
    }

    public UnidadeExternaFacade getUnidadeExternaFacade() {
        return unidadeExternaFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public List<CadastroImobiliario> recuperaCadastrosDaPessoa(Pessoa pessoa) {
        Query q = em.createQuery("select distinct c from CadastroImobiliario c " +
                " join c.propriedade p " +
                " left join c.listaCompromissarios cp " +
                " left join c.construcoes construcao" +
                " where p.pessoa = :pessoa or cp.compromissario = :pessoa")
            .setParameter("pessoa", pessoa);
        return q.getResultList();
    }

    public List<CadastroImobiliario> buscarCadastrosAtivosDaPessoa(Pessoa pessoa, Integer indexInicio, Integer maxResult) {
        Query q = em.createQuery("select c from CadastroImobiliario c " +
                " join c.propriedade p " +
                " where p.pessoa = :pessoa and c.ativo = 1" +
                "  and (p.inicioVigencia <= sysdate) " +
                "  and (p.finalVigencia is null or p.finalVigencia >= sysdate) " +
                " order by c.inscricaoCadastral")
            .setParameter("pessoa", pessoa);
        if (indexInicio != null && maxResult != null) {
            q.setFirstResult(indexInicio);
            q.setMaxResults(maxResult);
        }
        return q.getResultList();
    }

    public List<CadastroImobiliario> buscarCadastrosAtivosDaPessoaParaPesquisa(Pessoa pessoa) {
        Query q = em.createQuery("select new CadastroImobiliario(ci.id, ci.inscricaoCadastral) " +
                " from CadastroImobiliario ci " +
                " join ci.propriedade prop " +
                " left join ci.listaCompromissarios comp " +
                " where ci.ativo = :ativo " +
                " and (((prop.pessoa = :pessoa) and (prop.inicioVigencia <= :hoje) and (prop.finalVigencia is null or prop.finalVigencia > :hoje))" +
                "  or  (((comp.compromissario = :pessoa) and comp.inicioVigencia <= :hoje) and (comp.fimVigencia is null or comp.fimVigencia > :hoje))) " +
                " order by ci.inscricaoCadastral")
            .setParameter("pessoa", pessoa)
            .setParameter("hoje", DataUtil.dataSemHorario(new Date()))
            .setParameter("ativo", true);
        return q.getResultList();
    }

    public Integer contarCadastrosAtivosDaPessoa(Pessoa pessoa) {
        Query q = em.createQuery("select count(c) from CadastroImobiliario c " +
                " join c.propriedade p " +
                " where p.pessoa = :pessoa and c.ativo = 1" +
                "  and (p.inicioVigencia <= sysdate) " +
                "  and (p.finalVigencia is null or p.finalVigencia >= sysdate) ")
            .setParameter("pessoa", pessoa);

        return ((Long) q.getSingleResult()).intValue();
    }

    public List<CadastroImobiliario> buscarCadastrosAtivosDaPessoa(Pessoa pessoa) {
        return buscarCadastrosAtivosDaPessoa(pessoa, null, null);
    }


    public List<CadastroImobiliario> recuperaCadastrosAtivosDaPessoaCompromissario(Pessoa pessoa) {
        Query q = em.createQuery("select c from CadastroImobiliario c " +
                " left join c.listaCompromissarios compromissario" +
                " where compromissario.compromissario = :pessoa and c.ativo = 1")
            .setParameter("pessoa", pessoa);
        return q.getResultList();
    }


    public String recuperaEnderecoCadastro(Long id) {
        String sql = "SELECT " +
            " TRIM(COALESCE(TP.DESCRICAO,'')) || ' ' || TRIM(COALESCE(LOGRADOURO.NOME,''))," +
            " TRIM(CI.NUMERO)," +
            " TRIM(CI.COMPLEMENTOENDERECO), " +
            " TRIM(BAIRRO.DESCRICAO), " +
            " TRIM(COALESCE(LOTEAMENTO.NOME,'')) || ' ' || TRIM(COALESCE(LOTE.DESCRICAOLOTEAMENTO,'')), " +
            " TRIM(FACE.CEP)," +
            " TRIM(SETOR.CODIGO), " +
            " TRIM(QUADRA.CODIGO), " +
            " TRIM(LOTE.CODIGOLOTE), " +
            " TRIM(LOTE.COMPLEMENTO) " +
            "FROM CADASTROIMOBILIARIO CI " +
            "INNER JOIN LOTE LOTE ON LOTE.ID = CI.LOTE_ID " +
            "LEFT JOIN QUADRA QUADRA ON QUADRA.ID = LOTE.QUADRA_ID " +
            "LEFT JOIN SETOR SETOR ON SETOR.ID = QUADRA.SETOR_ID " +
            "LEFT JOIN LOTEAMENTO LOTEAMENTO ON LOTEAMENTO.ID = QUADRA.LOTEAMENTO_ID " +
            "LEFT JOIN TESTADA TESTADAS ON TESTADAS.LOTE_ID = LOTE.ID " +
            "LEFT JOIN FACE FACE ON FACE.ID = TESTADAS.FACE_ID " +
            "LEFT JOIN LOGRADOUROBAIRRO LOGBAIRRO ON LOGBAIRRO.ID = FACE.LOGRADOUROBAIRRO_ID " +
            "LEFT JOIN LOGRADOURO LOGRADOURO ON LOGRADOURO.ID = LOGBAIRRO.LOGRADOURO_ID " +
            "LEFT JOIN TIPOLOGRADOURO TP ON TP.ID = LOGRADOURO.TIPOLOGRADOURO_ID " +
            "LEFT JOIN BAIRRO BAIRRO ON BAIRRO.ID = LOGBAIRRO.BAIRRO_ID " +
            "WHERE CI.ID = :id";
        Query q = em.createNativeQuery(sql).setParameter("id", id);
        if (!q.getResultList().isEmpty()) {
            Object[] ob = (Object[]) q.getResultList().get(0);
            CadastroImobiliario.EnderecoCadastroStrings end = new CadastroImobiliario.EnderecoCadastroStrings();
            end.setLogradouro((String) ob[0]);
            end.setNumero((String) ob[1]);
            end.setComplemento((String) ob[2]);
            end.setBairro((String) ob[3]);
            end.setLoteamento((String) ob[4]);
            end.setCep((String) ob[5]);
            end.setSetor((String) ob[6]);
            end.setQuadra((String) ob[7]);
            end.setLote((String) ob[8]);
            end.setComplementoLote((String) ob[9]);
            return end.getEnderecoCompleto();
        }
        return null;
    }


    public IntegracaoSit buscarUltimaSincronizacaoSit(CadastroImobiliario cadastro) {
        String hql = "from IntegracaoSit where cadastroImobiliario = :cadastro and dataIntegracao = (select max(dataIntegracao) from IntegracaoSit where cadastroImobiliario = :cadastro)";
        Query q = em.createQuery(hql);
        q.setParameter("cadastro", cadastro);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (IntegracaoSit) q.getResultList().get(0);
        }
        return null;
    }

    public List<String> buscarInscricoesCadastraisNaoAtualiazadasDoPeriodoAteHoje(Date inicio) {
        String sql = "select ci.inscricaocadastral " +
            "from cadastroimobiliario ci " +
            "where ci.ativo = :verdadeiro " +
            "  and not exists ( " +
            "  select sit.id from integracaosit sit " +
            "  where sit.cadastroimobiliario_id = ci.id " +
            "   and sit.sucesso = :verdadeiro " +
            "   and sit.dataintegracao > :inicio " +
            ")";

        Query q = em.createNativeQuery(sql);
        q.setParameter("inicio", inicio);
        q.setParameter("verdadeiro", Boolean.TRUE);
        q.setMaxResults(100);
        return q.getResultList();
    }

    public List<IntegracaoSit> buscarIntegracoesSitComUsuarioDaAuditoria(CadastroImobiliario cadastro) {
        String sql = "select sit.id, sit.cadastroimobiliario_id, sit.dataintegracao, sit.sucesso, sit.mensagem, rev.usuario " +
            "from integracaosit_aud sit " +
            "left join revisaoauditoria rev on rev.id = sit.rev " +
            "where sit.revtype = :revtype and sit.cadastroimobiliario_id = :idCadastro " +
            "order by sit.dataintegracao desc";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", cadastro.getId());
        q.setParameter("revtype", 0);
        List<Object[]> lista = q.getResultList();
        List<IntegracaoSit> retorno = Lists.newArrayList();
        for (Object[] obj : lista) {
            IntegracaoSit sit = new IntegracaoSit();
            sit.setId(((BigDecimal) obj[0]).longValue());
            sit.setDataIntegracao((Date) obj[2]);
            sit.setSucesso(((BigDecimal) obj[3]).longValue() == 1 ? true : false);
            sit.setMensagem((String) obj[4]);
            sit.setUsuarioDaAuditoria((String) obj[5]);
            retorno.add(sit);
        }
        return retorno;
    }

    public Long ultimaRevisaoNaData(Date dataRevisao, Long id) {
        String sql = " select max(rev.id) \n" +
            "   from cadastroimobiliario_aud c_aud\n" +
            "  inner join revisaoauditoria rev on c_aud.rev = rev.id\n" +
            "where c_aud.id = :id " +
            " and rev.datahora <= :dataRevisao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id", id);
        q.setParameter("dataRevisao", dataRevisao);
        q.getResultList();
        return q.getResultList() != null && q.getResultList().get(0) != null ? ((BigDecimal) q.getResultList().get(0)).longValue() : null;
    }

    public CadastroImobiliario recuperarPorRevisao(Long id, Long revisao) {
        AuditReader reader = AuditReaderFactory.get(Util.criarEntityManagerOpenViewReadOnly());
        CadastroImobiliario ci = reader.find(CadastroImobiliario.class, id, revisao);
        RevisaoAuditoria revisaoAuditoria = auditoriaFacade.recuperar(revisao);
        Calendar c = GregorianCalendar.getInstance();
        c.setTime(revisaoAuditoria.getDataHora());
        Exercicio exercicio = exercicioFacade.getExercicioPorAno(c.get(Calendar.YEAR));
        atualizaValoresDosCadastros(Lists.newArrayList(ci), exercicio);
        return ci;
    }

    public void recuperarLotePorRevisao(CadastroImobiliario ci) {
        inicializarObjetoNoHibernate(ci.getLote());
        if (ci.getLote() != null) {
            inicializarObjetoNoHibernate(ci.getLote().getSetor());
            inicializarObjetoNoHibernate(ci.getLote().getQuadra());
            if (ci.getLote().getQuadra() != null) {
                inicializarObjetoNoHibernate(ci.getLote().getQuadra().getLoteamento());
            }
            inicializarListaNoHibernate(ci.getLote().getCaracteristicasLote());
            for (CaracteristicasLote caracteristicasLote : ci.getLote().getCaracteristicasLote()) {
                inicializarObjetoNoHibernate(caracteristicasLote.getAtributo());
                inicializarObjetoNoHibernate(caracteristicasLote.getValorAtributo());
                inicializarObjetoNoHibernate(caracteristicasLote.getValorAtributo().getValorDiscreto());
            }
            inicializarListaNoHibernate(ci.getLote().getTestadas());
            if (ci.getLote().getTestadas() != null) {
                for (Testada testada : ci.getLote().getTestadas()) {
                    inicializarObjetoNoHibernate(testada);
                    inicializarObjetoNoHibernate(testada.getFace());
                    if (testada.getFace() != null) {
                        inicializarObjetoNoHibernate(testada.getFace().getLogradouroBairro());
                        if (testada.getFace().getLogradouroBairro() != null) {
                            inicializarObjetoNoHibernate(testada.getFace().getLogradouroBairro().getBairro());
                            inicializarObjetoNoHibernate(testada.getFace().getLogradouroBairro().getLogradouro());
                            inicializarObjetoNoHibernate(testada.getFace().getLogradouroBairro().getLogradouro().getTipoLogradouro());
                        }
                    }
                }
            }
        }
    }

    public void inicializarListaNoHibernate(List lista) {
        for (Object o : lista) {
            inicializarObjetoNoHibernate(o);
        }
    }

    public void inicializarObjetoNoHibernate(Object objeto) {
        Hibernate.initialize(objeto);
    }


    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public List<CadastroImobiliario> recuperaPorInscricaoPorIntervalo(String inscricaoInicial, String inscricaoFinal) {
        String hql = "from CadastroImobiliario ci where ci.inscricaoCadastral between :inicial and :final";
        Query q = em.createQuery(hql);
        q.setParameter("inicial", inscricaoInicial.trim());
        q.setParameter("final", inscricaoFinal.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }


    public IsencaoCadastroImobiliario buscarIsencaoCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        String sql = "SELECT isencao.* FROM isencaocadastroimobiliario isencao " +
            " where isencao.iniciovigencia <= sysdate " +
            " and (isencao.finalvigencia is null or isencao.finalvigencia > sysdate )  " +
            " and isencao.tipolancamentoisencao is not null" +
            " and (isencao.situacao is null or isencao.situacao = :situacao) " +
            " and isencao.cadastroimobiliario_id =  :idCadastro";
        Query q = em.createNativeQuery(sql, IsencaoCadastroImobiliario.class);
        q.setParameter("idCadastro", cadastroImobiliario.getId());
        q.setParameter("situacao", IsencaoCadastroImobiliario.Situacao.ATIVO.name());
        if (!q.getResultList().isEmpty()) {
            return (IsencaoCadastroImobiliario) q.getResultList().get(0);
        }
        return null;
    }

    public IsencaoCadastroImobiliario buscarIsencaoCadastroImobiliarioPorExercicio(CadastroImobiliario cadastroImobiliario,
                                                                                   Exercicio exercicio) {
        String sql = "SELECT isencao.* " +
            "   FROM isencaocadastroimobiliario isencao " +
            " where :ano between extract(year from isencao.iniciovigencia) " +
            "            and extract(year from coalesce(isencao.finalvigencia, sysdate)) " +
            " and isencao.tipolancamentoisencao is not null " +
            " and (isencao.situacao is null or isencao.situacao = :situacao) " +
            " and isencao.cadastroimobiliario_id = :idCadastro ";
        Query q = em.createNativeQuery(sql, IsencaoCadastroImobiliario.class);
        q.setParameter("ano", exercicio.getAno());
        q.setParameter("idCadastro", cadastroImobiliario.getId());
        q.setParameter("situacao", IsencaoCadastroImobiliario.Situacao.ATIVO.name());
        if (!q.getResultList().isEmpty()) {
            return (IsencaoCadastroImobiliario) q.getResultList().get(0);
        }
        return null;
    }

    public List<CadastroImobiliario> buscarCadastroImobiliarioAtivoPorPessoaAndInscricaoCadastral(String inscricaoCadastral, Pessoa pessoa) {
        String hql = "select c from CadastroImobiliario c " +
            " join c.propriedade p " +
            " where p.pessoa = :pessoa " +
            "  and c.ativo = :ativo" +
            "  and c.inscricaoCadastral like :inscricaoCadastral " +
            "  and (p.inicioVigencia <= sysdate) " +
            "  and (p.finalVigencia is null or p.finalVigencia >= sysdate) ";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoa);
        q.setParameter("inscricaoCadastral", "%" + inscricaoCadastral.trim() + "%");
        q.setParameter("ativo", Boolean.TRUE);
        return q.getResultList();
    }

    public List<CadastroImobiliario> buscarCadastroImobiliarioAtivoPorInscricaoCadastralAndCpfCnpj(String inscricaoCadastral, String cpfCnpj) {
        String sql = "select cad.*, ci.* from CadastroImobiliario ci " +
            " inner join Cadastro cad on cad.id = ci.id" +

            " left join Propriedade prop on prop.imovel_id = ci.id and prop.finalVigencia is null " +
            " left join PessoaFisica pf on pf.id = prop.pessoa_Id " +
            " left join PessoaJuridica pj on pj.id = prop.pessoa_Id " +

            " left join compromissario comp on comp.cadastroImobiliario_id = ci.id and comp.fimVigencia is null " +
            " left join PessoaFisica pfcomp on pfcomp.id = comp.compromissario_Id " +
            " left join PessoaJuridica pjcomp on pjcomp.id = comp.compromissario_Id " +

            " where (coalesce(pf.cpf, pj.cnpj) = :cpfCnpj or coalesce(pfcomp.cpf, pjcomp.cnpj) = :cpfCnpj) " +
            "  and ci.ativo = :ativo" +
            "  and ci.inscricaoCadastral like :inscricaoCadastral";
        Query q = em.createNativeQuery(sql, CadastroImobiliario.class);
        q.setParameter("cpfCnpj", cpfCnpj);
        q.setParameter("inscricaoCadastral", inscricaoCadastral.trim());
        q.setParameter("ativo", 1);
        return q.getResultList();
    }

    public List<CadastroImobiliario> buscarCadastroImobiliarioAtivo(String parte) {
        String sql = "select cad.responsavelPeloCadastro_id, cad.migracaoChave, ci.* " +
            "from CadastroImobiliario ci " +
            "         inner join Cadastro cad on cad.id = ci.id " +
            "         inner join Propriedade prop on prop.imovel_id = ci.id and prop.finalVigencia is null " +
            "         left join PessoaFisica pf on pf.id = prop.pessoa_Id " +
            "         left join PessoaJuridica pj on pj.id = prop.pessoa_Id " +
            "         inner join lote on lote.id = ci.lote_id " +
            "         left join quadra on quadra.id = lote.quadra_id " +
            "         left join setor on setor.id = quadra.setor_id " +
            "         left join testada testadas on testadas.lote_id = lote.id " +
            "         left join face on face.id = testadas.face_id " +
            "         left join logradourobairro logbairro on logbairro.id = face.logradourobairro_id " +
            "         left join logradouro lg on lg.id = logbairro.logradouro_id " +
            "where ci.ativo = :ativo " +
            "  and (ci.inscricaoCadastral like :parte " +
            "    or lower(lg.nome) like :parte " +
            "    or lower(ci.complementoendereco) like :parte " +
            "    or face.cep like :parte " +
            "    or replace(replace(face.cep, '.', ''), '-', '') like :parte " +
            "    or lower(coalesce(pf.nome, pj.razaoSocial)) like :parte " +
            "    or coalesce(pf.cpf, pj.cnpj) like :parte " +
            "    or replace(replace(replace(coalesce(pf.cpf, pj.cnpj), '.', ''), '-', ''), '/', '') like :parte) ";
        Query q = em.createNativeQuery(sql, CadastroImobiliario.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("ativo", Boolean.TRUE);
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<CadastroImobiliario> buscarCadastroImobiliarioContribuicaoMelhoria(String condicao, Logradouro logradouro, Bairro bairro) {
        ValidacaoException ve = new ValidacaoException();
        if ((condicao == null || condicao.equals("")) && (logradouro == null && bairro == null)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe ao menos um filtro para realizar a pesquisa.");
            throw ve;
        }
        String hql = "select distinct ci from CadastroImobiliario ci " +
            " left join ci.lote lote " +
            " left join lote.quadra quadra" +
            " left join lote.testadas testada" +
            " left join testada.face face" +
            " left join face.logradouroBairro logradouroBairro " +
            " where ci.ativo = 1 ";
        if (!condicao.trim().equals("")) {
            hql += " and ci.inscricaoCadastral like :condicao";
        }
        if (logradouro != null) {
            hql += " and logradouroBairro.logradouro = :logradouro";
        }
        if (bairro != null) {
            hql += " and logradouroBairro.bairro = :bairro";
        }
        Query q = em.createQuery(hql);
        if (!condicao.trim().equals("")) {
            q.setParameter("condicao", condicao + "%");
        }
        if (logradouro != null) {
            q.setParameter("logradouro", logradouro);
        }
        if (bairro != null) {
            q.setParameter("bairro", bairro);
        }
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }

    public List<Object[]> buscarCadastrosExportacaoBI() {
        Query q = em.createNativeQuery("select null as inscricaoFiscal, " +
            "REGEXP_REPLACE(coalesce(pf.cpf,pj.cnpj,''), '[^0-9]', '') as cpfcnpj, " +
            "logra.CODIGO as codigologradouro, " +
            "REGEXP_REPLACE(cI.NUMERO, '[^0-9]', '') as numerologradouro, " +
            "bairro.CODIGO as codigobairro, " +
            "coalesce(pf.NOME,pj.NOMEFANTASIA) as nome , " +
            "cI.INSCRICAOCADASTRAL, " +
            "cI.ATIVO from cadastroimobiliario cI " +
            "inner join lote lote on lote.id = cI.LOTE_ID " +
            "inner join testada testa on testa.LOTE_ID = lote.id " +
            "inner join face face on face.id = testa.FACE_ID " +
            "inner join logradourobairro logbairro on logbairro.id = face.LOGRADOUROBAIRRO_ID " +
            "inner join bairro bairro on bairro.id = logbairro.BAIRRO_ID " +
            "inner join logradouro logra on logra.id = logbairro.LOGRADOURO_ID " +
            "inner join propriedade propi on propi.IMOVEL_ID = cI.id and propi.FINALVIGENCIA is null " +
            "inner join pessoa pe on pe.id = propi.pessoa_id " +
            "left join pessoajuridica pj on pj.id=pe.id " +
            "left join pessoafisica pf on pf.id = pe.id " +
            "where coalesce(cI.ATIVO,0) = 1 " +
            "order by cI.INSCRICAOCADASTRAL");
        return q.getResultList();
    }

    public List<String> buscarRegistrosSincronizarSit(String hql) {
        Query q = em.createQuery(hql);
        try {
            return (List<String>) q.getResultList();
        } catch (NoResultException nre) {
            return Lists.newArrayList();
        }
    }

    public Boolean buscarSituacaoCadastroImobiliario(Long idCadastro) {
        String sql = "select coalesce(ci.ativo,1) from CadastroImobiliario ci where ci.id = :idCadastro";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", idCadastro);
        List resultList = q.getResultList();
        return !resultList.isEmpty() ? ((BigDecimal) resultList.get(0)).compareTo(BigDecimal.ZERO) != 0 : false;
    }

    public static class AtualizadorBCI {
        private boolean atualizando;
        private ConfiguracaoTributario configuracaoTributario;
        private List<CadastroImobiliario> cadastros;
        private Exercicio exercicio;
        private Double totalCalculos;
        private Integer calculosProcessados;
        private boolean processando;

        public AtualizadorBCI(ConfiguracaoTributario configuracaoTributario, List<CadastroImobiliario> cadastros, Exercicio exercicio) {
            this.cadastros = cadastros;
            this.exercicio = exercicio;
            this.configuracaoTributario = configuracaoTributario;

            atualizando = false;
        }

        public Double getTotalCalculos() {
            return totalCalculos;
        }

        public void setTotalCalculos(Double totalCalculos) {
            this.totalCalculos = totalCalculos;
        }

        public Double getPorcentagem() {
            try {
                return (calculosProcessados / (totalCalculos)) * 100;
            } catch (Exception e) {
                e.printStackTrace();
                return 0d;
            }
        }

        public synchronized void contaLinha() {
            calculosProcessados++;
        }

        public Integer getCalculosProcessados() {
            return calculosProcessados;
        }

        public void setCalculosProcessados(Integer calculosProcessados) {
            this.calculosProcessados = calculosProcessados;
        }

        private void zeraTudo() {
            calculosProcessados = 0;
            totalCalculos = 0d;
            processando = false;
        }

        public void inicializa(Integer totalCalculos) {
            zeraTudo();
            this.totalCalculos = totalCalculos.doubleValue();
            this.processando = true;

        }

        public synchronized boolean isProcessando() {
            return processando;
        }

        public synchronized void finaliza() {
            if (calculosProcessados >= totalCalculos.intValue()) {
                aborta();
            }
        }

        public synchronized void aborta() {
            processando = false;
            atualizando = false;
        }

        public ConfiguracaoTributario getConfiguracaoTributario() {
            return configuracaoTributario;
        }

        public List<CadastroImobiliario> getCadastros() {
            return cadastros;
        }

        public Exercicio getExercicio() {
            return exercicio;
        }

        public Integer getTotal() {
            return cadastros.size();
        }

        public synchronized boolean isAtualizando() {
            return atualizando;
        }

        public synchronized void setAtualizando(boolean atualizando) {
            this.atualizando = atualizando;
        }
    }

    private class FiltroRelatorioBCI {
        private String setor;
        private String quadra;
        private String lote;
        private String unidade;

        public FiltroRelatorioBCI(String setor, String quadra, String lote, String unidade) {
            this.setor = setor;
            this.quadra = quadra;
            this.lote = lote;
            this.unidade = unidade;
        }

        public String getSetor() {
            return setor;
        }

        public String getQuadra() {
            return quadra;
        }

        public String getLote() {
            return lote;
        }

        public String getUnidade() {
            return unidade;
        }

        public FiltroRelatorioBCI ajustar() {
            if (setor.trim().length() < 3) {
                setor = StringUtil.preencheString(setor, 3, '0');
            }
            if (quadra.trim().length() < 4) {
                quadra = StringUtil.preencheString(quadra, 4, '0');
            }
            if (!lote.trim().equals("") && lote.trim().length() < 4) {
                lote = StringUtil.preencheString(lote, 4, '0');
            }
            if (!unidade.trim().equals("") && unidade.trim().length() < 3) {
                unidade = StringUtil.preencheString(unidade, 3, '0');
            }
            return this;
        }
    }

    public AuditoriaFacade getAuditoriaFacade() {
        return auditoriaFacade;
    }

    public Page<CadastroImobiliarioSearchNfseDTO> buscarCadastroImobiliarioSearch(Pageable pageable, String search) throws Exception {
        List<ParametroQuery> parametros = Lists.newArrayList();
        parametros.add(new ParametroQuery(" or ",
            Lists.newArrayList(
                new ParametroQueryCampo(" ci.inscricaocadastral ", Operador.LIKE, "%" + search.trim() + "%"),
                new ParametroQueryCampo(" ci.ativo ", Operador.IGUAL, "1"),
                new ParametroQueryCampo(" trim(lower(coalesce(pf.nome, pj.razaosocial))) ", Operador.LIKE, "%" + search.trim().toLowerCase() + "%"),
                new ParametroQueryCampo("regexp_replace(coalesce(pf.cpf, pj.cnpj) , '[^0-9]*', '')  ", Operador.LIKE, "%" + search.trim().toLowerCase() + "%")
            )));

        List<CadastroImobiliarioSearchNfseDTO> toReturn = Lists.newArrayList();
        String sql = "select ci.id,\n" +
            "       ci.inscricaocadastral as inscricaocadastral,\n" +
            "       (select listagg(coalesce(pf.cpf, pj.cnpj) || ' - ' || coalesce(pf.nome, pj.razaosocial),\n" +
            "                       ' ') WITHIN GROUP (ORDER BY 1)\n" +
            "        from propriedade prop\n" +
            "               left join pessoafisica pf on pf.id = prop.pessoa_id\n" +
            "               left join pessoajuridica pj on pj.id = prop.pessoa_id\n" +
            "        where prop.imovel_id = ci.id\n" +
            "          and sysdate between prop.iniciovigencia and coalesce(prop.finalvigencia, sysdate)) as proprietarios,\n" +
            "    lg.nome as logradouro,\n" +
            "    b.descricao as bairro,\n" +
            "    ci.numero as numero\n" +
            "    from cadastroimobiliario ci\n" +
            "    inner join lote l on l.id = ci.lote_id\n" +
            "    left join testada t on t.id = COALESCE ((SELECT max (s_testada.id) AS max FROM testada s_testada\n" +
            "    WHERE s_testada.lote_id = l.id\n" +
            "    AND s_testada.principal = 1), (SELECT max (s_testada.id) AS max FROM testada s_testada\n" +
            "    WHERE s_testada.lote_id = l.id))\n" +
            "    left join face f on f.id = t.face_id\n" +
            "    left join logradourobairro lb on lb.id = f.logradourobairro_id\n" +
            "    left join logradouro lg on lg.id = lb.logradouro_id\n" +
            "    left join bairro b on b.id = lb.bairro_id\n" +
            "    left join propriedade prop on prop.imovel_id = ci.id and sysdate between prop.iniciovigencia and coalesce (prop.finalvigencia, sysdate)\n" +
            "    left join pessoafisica pf on pf.id = prop.pessoa_id\n" +
            "    left join pessoajuridica pj on pj.id = prop.pessoa_id";

        Query query = new GeradorQuery(em, null, sql).parametros(parametros).build();
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        if (query.getResultList() != null && !query.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) query.getResultList()) {
                CadastroImobiliarioSearchNfseDTO cadastroImobiliarioSearch = new CadastroImobiliarioSearchNfseDTO();
                cadastroImobiliarioSearch.setId(((BigDecimal) obj[0]).longValue());
                cadastroImobiliarioSearch.setInscricaoCadastral(((String) obj[1]));
                cadastroImobiliarioSearch.setProprietarios(((String) obj[2]));
                cadastroImobiliarioSearch.setLogradouro(((String) obj[3]));
                cadastroImobiliarioSearch.setBairro(((String) obj[4]));
                cadastroImobiliarioSearch.setNumero(((String) obj[5]));
                toReturn.add(cadastroImobiliarioSearch);
            }
        }
        return new PageImpl<>(toReturn);
    }

    public String recuperarUrlPortal() {
        return configuracaoTributarioFacade.recuperarUrlPortal();
    }

    public CadastroImobiliario buscarCadastroImobiliarioPorId(Long id) {

        String sql = "select cad.* from CADASTROIMOBILIARIO cad " +
            "where cad.id = :id";

        Query q = em.createNativeQuery(sql, CadastroImobiliario.class);
        q.setParameter("id", id);
        CadastroImobiliario cadastroImobiliario = (CadastroImobiliario) q.getResultList().get(0);
        return cadastroImobiliario;
    }


    public List<CadastroImobiliario> buscarCadastroImobiliarioPorSituacao(String s, Boolean ativo, String... atributos) {
        String hql = " select new CadastroImobiliario(bci.id, bci.codigo, bci.inscricaoCadastral, bci.lote) " +
            " from CadastroImobiliario bci " +
            " where bci.ativo in :ativo and ";

        for (String atributo : atributos) {
            hql += "lower(bci." + atributo + ") like :filtro OR ";
        }
        hql = hql.substring(0, hql.length() - 3);

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.trim().toLowerCase() + "%");

        List<Boolean> ativos = Lists.newArrayList(true, false);
        if (ativo != null) {
            ativos = Lists.newArrayList(ativo);
        }
        q.setParameter("ativo", ativos);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public CadastroImobiliario recuperarCadastroImobiliarioSit(Long idCadastro) {
        String sql = "select c.*, ci.* from cadastroimobiliario ci " +
            " inner join cadastro c on ci.id = c.id " +
            " where ci.id = :idCadastro ";

        Query q = em.createNativeQuery(sql, CadastroImobiliario.class);
        q.setParameter("idCadastro", idCadastro);

        List<CadastroImobiliario> cadastros = q.getResultList();

        if (cadastros != null && !cadastros.isEmpty()) {
            CadastroImobiliario cadastroImobiliario = cadastros.get(0);
            Hibernate.initialize(cadastroImobiliario.getLote().getTestadas());
            Hibernate.initialize(cadastroImobiliario.getLote().getCaracteristicasLote());
            Hibernate.initialize(cadastroImobiliario.getPropriedade());
            Hibernate.initialize(cadastroImobiliario.getListaCompromissarios());
            Hibernate.initialize(cadastroImobiliario.getCaracteristicasBci());
            Hibernate.initialize(cadastroImobiliario.getConstrucoes());
            for (Testada testada : cadastroImobiliario.getLote().getTestadas()) {
                Hibernate.initialize(testada.getFace().getFaceServicos());
            }

            return cadastroImobiliario;
        }
        return null;
    }

    public List<String> buscarInscricoesCadastraisDosListadosParaIntegracaoSit(ConsultaEntidade consulta) {
        String sql = " select distinct ci.inscricaocadastral ";
        sql += consultaEntidadeFacade.trocarTags(consulta, consulta.getFrom());
        sql += consultaEntidadeFacade.agruparCondicoesPeloOperador(consulta, "");

        Query q = em.createNativeQuery(sql);

        for (int i = 0; i < consulta.getFiltros().size(); i++) {
            FiltroConsultaEntidade filtro = consulta.getFiltros().get(i);
            if (filtro.getValor() != null && filtro.getOperacao() != null && !filtro.operadorIsNull()) {
                q.setParameter("param_" + i, filtro.getValorParaQuery());
            }
        }

        List<String> inscricoes = q.getResultList();
        return inscricoes != null ? inscricoes : Lists.<String>newArrayList();
    }

    public AlvaraConstrucaoFacade getAlvaraConstrucaoFacade() {
        return alvaraConstrucaoFacade;
    }
}
