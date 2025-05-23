package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DocumentoComprobatorioLevantamentoBemImovel;
import br.com.webpublico.entidades.LevantamentoBemImovel;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.TipoAquisicaoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        lbi.setHierarquiaOrganizacionalAdministrativa(hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(lbi.getDataLevantamento(), lbi.getUnidadeAdministrativa(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
        lbi.setHierarquiaOrganizacionalOrcamentaria(hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(lbi.getDataLevantamento(), lbi.getUnidadeOrcamentaria(), TipoHierarquiaOrganizacional.ORCAMENTARIA));

        lbi.getDocumentosComprobatorios().size();
        if (lbi.getDetentorArquivoComposicao() != null) {
            lbi.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }

        for (DocumentoComprobatorioLevantamentoBemImovel dc : lbi.getDocumentosComprobatorios()) {
            dc.getEmpenhos().size();
        }

        lbi.getDetentorOrigemRecurso().getListaDeOriemRecursoBem().size();

        return lbi;
    }

    @Override
    public void salvar(LevantamentoBemImovel levantamento) {
        validarRegrasDeNegocio(levantamento);
        super.salvar(levantamento);
    }

    private void validarRegrasDeNegocio(LevantamentoBemImovel levantamento) {
        ValidacaoException ve = new ValidacaoException();
        if (bemFacade.bemImovelTemCodigoRepetido(levantamento)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O código " + levantamento.getCodigoPatrimonio() + " já está sendo utilizado.");
        }
        ve.lancarException();
    }

    @Override
    public void salvarNovo(LevantamentoBemImovel levantamento) {
        validarRegrasDeNegocio(levantamento);
        super.salvarNovo(levantamento);
    }

    public LevantamentoBemImovel salvarLevantamento(LevantamentoBemImovel levantamento) {
        validarRegrasDeNegocio(levantamento);
        return salvarRetornando(levantamento);
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

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
