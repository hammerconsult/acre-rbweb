package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.CalamidadePublicaPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class CalamidadePublicaFacade extends AbstractFacade<CalamidadePublica> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeExternaFacade unidadeExternaFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private UnidadeMedidaFacade unidadeMedidaFacade;
    @EJB
    private PessoaFacade pessoaFacade;

    public CalamidadePublicaFacade() {
        super(CalamidadePublica.class);
    }

    @Override
    public CalamidadePublica recuperar(Object id) {
        CalamidadePublica calamidadePublica = em.find(CalamidadePublica.class, id);
        Hibernate.initialize(calamidadePublica.getAtosLegais());
        Hibernate.initialize(calamidadePublica.getContratos());
        Hibernate.initialize(calamidadePublica.getRecursos());
        Hibernate.initialize(calamidadePublica.getBensServicosRecebidos());
        Hibernate.initialize(calamidadePublica.getBensDoados());
        for (CalamidadePublicaBemDoado item : calamidadePublica.getBensDoados()) {
            if (item.getDetentorArquivoComposicao() != null) {
                Hibernate.initialize(item.getDetentorArquivoComposicao().getArquivosComposicao());
            }
        }
        return calamidadePublica;
    }

    @Override
    public void salvar(CalamidadePublica entity) {
        super.salvar(entity);
        salvarPortal(entity);
    }

    private void salvarPortal(CalamidadePublica entity) {
        portalTransparenciaNovoFacade.salvarPortal(new CalamidadePublicaPortal(entity));
    }

    @Override
    public void salvarNovo(CalamidadePublica entity) {
        entity = em.merge(entity);
        salvarPortal(entity);
    }

    public List<Contrato> buscarContratos(String filtro) {
        return contratoFacade.buscarContratoPorNumeroAndExercicio(filtro, sistemaFacade.getExercicioCorrente());
    }

    public List<AtoLegal> buscarAtosLegais(String filtro) {
        return atoLegalFacade.buscarTodosAtosLegaisPorExercicio(filtro, sistemaFacade.getExercicioCorrente());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CalamidadePublica buscarPorAbrevisacao(CalamidadePublica calamidadePublica) {
        String sql = "SELECT * FROM calamidadePublica WHERE abreviacao = :abreviacao ";
        if (calamidadePublica.getId() != null) {
            sql += " and id <> :id";
        }
        Query q = em.createNativeQuery(sql, CalamidadePublica.class);
        q.setParameter("abreviacao", calamidadePublica.getAbreviacao().name());
        if (calamidadePublica.getId() != null) {
            q.setParameter("id", calamidadePublica.getId());
        }
        q.setMaxResults(1);
        try {
            return (CalamidadePublica) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<HierarquiaOrganizacional> completarUnidadeOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.listaPorUsuarioEIndiceDoNo(sistemaFacade.getUsuarioCorrente(), parte.trim(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaFacade.getDataOperacao(), 2);
    }

    public List<HierarquiaOrganizacional> completarUnidadeOrcamentaria(String parte) {
        return hierarquiaOrganizacionalFacade.listaTodasHierarquiaHorganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaFacade.getDataOperacao());
    }

    public List<UnidadeExterna> completarUnidadeExterna(String parte) {
        return unidadeExternaFacade.listaFiltrandoPessoaJuridicaEsferaGoverno(parte.trim());
    }

    public List<FonteDeRecursos> completarFonteDeRecurso(String parte) {
        return fonteDeRecursosFacade.listaFiltrandoPorExercicio(parte.trim(), sistemaFacade.getExercicioCorrente());
    }

    public List<UnidadeMedida> buscarUnidadesMedida(String filtro) {
        return unidadeMedidaFacade.listaFiltrando(filtro, "descricao", "sigla");
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
