package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DocumentoComprobatorioLevantamentoBemImovel;
import br.com.webpublico.entidades.LevantamentoBemImovel;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MGA
 * Date: 03/09/14
 * Time: 10:20
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class LevantamentoBemImovelFacade extends AbstractFacade<LevantamentoBemImovel> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private CondicaoDeOcupacaoFacade condicaoDeOcupacaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private SingletonHierarquiaOrganizacional singletonHO;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private FaseFacade faseFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private PropriedadeFacade propriedadeFacade;

    public LevantamentoBemImovelFacade() {
        super(LevantamentoBemImovel.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public LevantamentoBemImovel recuperar(Object id) {
        LevantamentoBemImovel lbi = super.recuperar(id);
        Hibernate.initialize(lbi.getDocumentosComprobatorios());
        if (lbi.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(lbi.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        for (DocumentoComprobatorioLevantamentoBemImovel dc : lbi.getDocumentosComprobatorios()) {
            Hibernate.initialize(dc.getEmpenhos());
        }
        Hibernate.initialize(lbi.getDetentorOrigemRecurso().getListaDeOriemRecursoBem());
        return lbi;
    }

    @Override
    public LevantamentoBemImovel salvarRetornando(LevantamentoBemImovel levantamento) {
        validarRegrasDeNegocio(levantamento);
        return super.salvarRetornando(levantamento);
    }

    private void validarRegrasDeNegocio(LevantamentoBemImovel levantamento) {
        ValidacaoException ve = new ValidacaoException();
        if (bemFacade.bemImovelTemCodigoRepetido(levantamento)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O código " + levantamento.getCodigoPatrimonio() + " já está sendo utilizado.");
        }
        ve.lancarException();
    }

    public List<LevantamentoBemImovel> buscarLevantamentoImovelPorUnidadeOrcamentaria(UnidadeOrganizacional orcamentaria) {
        String sql = " SELECT * " +
            "            from LevantamentoBemImovel lev " +
            "            where lev.unidadeOrcamentaria_id = :orc" +
            "          and not exists(select 1 " +
            "                           from efetivacaolevantamentoimov imovel " +
            "                         where imovel.levantamentobemimovel_id = lev.id) ";
        Query q = em.createNativeQuery(sql, LevantamentoBemImovel.class);
        q.setParameter("orc", orcamentaria.getId());
        return q.getResultList();
    }

    public Boolean isExisteEfetivacaoToLevantamentoSelecionado(Long id) {

        if (id == null)
            return false;

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT efetivacao.id FROM EFETIVACAOLEVANTAMENTOIMOV efetivacao ");
        sql.append(" INNER JOIN LOTEEFETLEVIMOVEL lote ON efetivacao.LOTEEFETIVACAOIMOVEL_ID = lote.ID ");
        sql.append(" INNER JOIN LEVANTAMENTOBEMIMOVEL levantamento ON efetivacao.LEVANTAMENTOBEMIMOVEL_ID = levantamento.ID ");
        sql.append(" WHERE levantamento.ID = :id and rownum =1 ");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("id", id);
        return !q.getResultList().isEmpty();
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }

    public CondicaoDeOcupacaoFacade getCondicaoDeOcupacaoFacade() {
        return condicaoDeOcupacaoFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public FaseFacade getFaseFacade() {
        return faseFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public LoteFacade getLoteFacade() {
        return loteFacade;
    }

    public PropriedadeFacade getPropriedadeFacade() {
        return propriedadeFacade;
    }
}
