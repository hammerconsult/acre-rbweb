package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DocumentoComprobatorioLevantamentoBemImovel;
import br.com.webpublico.entidades.LevantamentoBemImovel;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: MGA
 * Date: 15/09/14
 * Time: 15:11
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class DocumentoComprobatorioLevantamentoBemImovelFacade extends AbstractFacade<DocumentoComprobatorioLevantamentoBemImovel> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private LevantamentoBemImovelFacade levantamentoBemImovelFacade;

    public DocumentoComprobatorioLevantamentoBemImovelFacade() {
        super(DocumentoComprobatorioLevantamentoBemImovel.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public DocumentoComprobatorioLevantamentoBemImovel recuperar(Object id) {
        DocumentoComprobatorioLevantamentoBemImovel docto = super.recuperar(id);

        docto.setHierarquiaOrganizacionalAdministrativa(hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(docto.getDataVinculoLevantamento(), docto.getUnidadeAdministrativa(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
        docto.setHierarquiaOrganizacionalOrcamentaria(hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(docto.getDataVinculoLevantamento(), docto.getUnidadeOrcamentaria(), TipoHierarquiaOrganizacional.ORCAMENTARIA));

        docto.getEmpenhos().size();
        if (docto.getDetentorArquivoComposicao() != null) {
            docto.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }

        docto.setLevantamentoBemImovel(levantamentoBemImovelFacade.recuperar(docto.getLevantamentoBemImovel().getId()));

        return docto;
    }

    @Override
    public void remover(DocumentoComprobatorioLevantamentoBemImovel documento) {
        LevantamentoBemImovel levantamentoBemImovel = levantamentoBemImovelFacade.recuperar(documento.getLevantamentoBemImovel().getId());
        levantamentoBemImovel.getDocumentosComprobatorios().remove(documento);
        em.merge(levantamentoBemImovel);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ParametroPatrimonioFacade getParametroPatrimonioFacade() {
        return parametroPatrimonioFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public LevantamentoBemImovelFacade getLevantamentoBemImovelFacade() {
        return levantamentoBemImovelFacade;
    }
}
