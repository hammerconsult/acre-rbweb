/*
 * Codigo gerado automaticamente em Wed Apr 20 13:58:30 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.FormulaItemDemonstrativo;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.enums.OperacaoFormula;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class FormulaItemDemonstrativoFacade extends AbstractFacade<FormulaItemDemonstrativo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    @EJB
    private PlanoDeContasFacade planoDeContasFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private SubProjetoAtividadeFacade subProjetoAtividadeFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private FuncaoFacade funcaoFacade;
    @EJB
    private SubFuncaoFacade subFuncaoFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private SubContaFacade subContaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FormulaItemDemonstrativoFacade() {
        super(FormulaItemDemonstrativo.class);
    }


    public FormulaItemDemonstrativo salvarRetornando(FormulaItemDemonstrativo formulaItemDemonstrativo) {
        return em.merge(formulaItemDemonstrativo);
    }

    @Override
    public FormulaItemDemonstrativo recuperar(Object id) {
        FormulaItemDemonstrativo fid = em.find(FormulaItemDemonstrativo.class, id);
        fid.getComponenteFormula().size();
        return fid;
    }

    public List<FormulaItemDemonstrativo> listaExercicio(Exercicio exercicioCorrente, String filtro) {
        String hql = "from FormulaItemDemonstrativo obj where obj.exercicio = :exerc ";
        if (filtro != null) {
            if (!filtro.equals("")) {
                hql += " and lower(obj.itemDemonstrativo.descricao) like \'%" + filtro.toLowerCase() + "%\'";
            }
        }
        hql += " order by obj.id";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("exerc", exercicioCorrente);
        return q.getResultList();
    }

    public FormulaItemDemonstrativo recuperarFormulaPorConfiguracaoAndOperacaoAndColuna(ItemDemonstrativo it, OperacaoFormula operacaoFormula, RelatoriosItemDemonst relatoriosItemDemonst, Integer coluna) {
        String sql = " SELECT DISTINCT FID.* FROM FORMULAITEMDEMONSTRATIVO FID" +
            " INNER JOIN ITEMDEMONSTRATIVO IT ON FID.ITEMDEMONSTRATIVO_ID = IT.ID" +
            " INNER JOIN COMPONENTEFORMULA CF ON CF.FORMULAITEMDEMONSTRATIVO_ID = FID.ID" +
            " INNER JOIN RELATORIOSITEMDEMONST REL ON IT.RELATORIOSITEMDEMONST_ID = REL.ID " +
            " WHERE IT.ID = :ITID AND FID.OPERACAOFORMULA = :OPERACAO and REL.ID = :RELID ";
        if (coluna != null) {
            sql += " and fid.coluna = :coluna ";
        } else {
            sql += " and fid.coluna is null ";
        }
        Query q = em.createNativeQuery(sql, FormulaItemDemonstrativo.class);
        q.setParameter("ITID", it.getId());
        q.setParameter("RELID", relatoriosItemDemonst.getId());
        q.setParameter("OPERACAO", operacaoFormula.name());
        if (coluna != null) {
            q.setParameter("coluna", coluna);
        }
        if (q.getResultList().isEmpty()) {
            return new FormulaItemDemonstrativo();
        } else {
            FormulaItemDemonstrativo formulaItemDemonstrativo = (FormulaItemDemonstrativo) q.getSingleResult();
            formulaItemDemonstrativo.getComponenteFormula().size();
            return formulaItemDemonstrativo;
        }
    }

    public ItemDemonstrativoFacade getItemDemonstrativoFacade() {
        return itemDemonstrativoFacade;
    }

    public PlanoDeContasFacade getPlanoDeContasFacade() {
        return planoDeContasFacade;
    }

    public ProgramaPPAFacade getProgramaPPAFacade() {
        return programaPPAFacade;
    }

    public SubProjetoAtividadeFacade getSubProjetoAtividadeFacade() {
        return subProjetoAtividadeFacade;
    }

    public FuncaoFacade getFuncaoFacade() {
        return funcaoFacade;
    }

    public SubFuncaoFacade getSubFuncaoFacade() {
        return subFuncaoFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public EntityManager getEm() {
        return em;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public ProjetoAtividadeFacade getProjetoAtividadeFacade() {
        return projetoAtividadeFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }
}
