/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.enums.LayoutArquivoBordero;
import br.com.webpublico.entidades.LayoutArquivoRemessa;
import java.math.BigDecimal;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author wiplash
 */
@Stateless
public class LayoutArquivoRemessaFacade extends AbstractFacade<LayoutArquivoRemessa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public LayoutArquivoRemessaFacade() {
        super(LayoutArquivoRemessa.class);
    }

    @Override
    public LayoutArquivoRemessa recuperar(Object id) {
        LayoutArquivoRemessa lar = em.find(LayoutArquivoRemessa.class, id);
        lar.getHeaderArquivoRemessas().size();
        lar.getRegistroUmArquivoRemessas().size();
        lar.getRegistroDoisArquivoRemessas().size();
        lar.getRegistroTresArquivoRemessas().size();
        lar.getRegistroQuatroArquivoRemessas().size();
        lar.getRegistroCincoArquivoRemessas().size();
        lar.getTrailerArquivoRemessas().size();
        return lar;
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        LayoutArquivoRemessa lar = (LayoutArquivoRemessa) em.find(entidade, id);
        lar.getHeaderArquivoRemessas().size();
        lar.getRegistroUmArquivoRemessas().size();
        lar.getRegistroDoisArquivoRemessas().size();
        lar.getRegistroTresArquivoRemessas().size();
        lar.getRegistroQuatroArquivoRemessas().size();
        lar.getRegistroCincoArquivoRemessas().size();
        lar.getTrailerArquivoRemessas().size();
        return lar;
    }

    public BigDecimal geraProximoNumeroLayout(LayoutArquivoRemessa layout) {
        String sql = " select numero + 1 from LayoutArquivoRemessa lar where lar.id = :layout ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("layout", layout.getId());
        return (BigDecimal) q.getSingleResult();
    }

    public LayoutArquivoRemessa recuperaPorTipoLayout(LayoutArquivoBordero layout) {
        String hql = "from LayoutArquivoRemessa lar where lar.layoutArquivoBordero = :layout order by lar.id desc";
        Query q = em.createQuery(hql, LayoutArquivoRemessa.class);
        q.setParameter("layout", layout);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return new LayoutArquivoRemessa();
        } else {
            LayoutArquivoRemessa lar = (LayoutArquivoRemessa) q.getSingleResult();
            lar.getHeaderArquivoRemessas().size();
            lar.getRegistroUmArquivoRemessas().size();
            lar.getRegistroDoisArquivoRemessas().size();
            lar.getRegistroTresArquivoRemessas().size();
            lar.getRegistroQuatroArquivoRemessas().size();
            lar.getRegistroCincoArquivoRemessas().size();
            lar.getTrailerArquivoRemessas().size();
            return lar;
        }
    }

    public void validaExitenciaConfiguracaoMesmolayout(LayoutArquivoBordero layoutArquivoBordero) throws ExcecaoNegocioGenerica {
        try {
            String SQL = "select * from layoutarquivoremessa l where l.layoutarquivobordero=:param";
            Query q = em.createNativeQuery(SQL).setParameter("param", layoutArquivoBordero.name());

            q.getSingleResult();

        } catch (NonUniqueResultException ex) {
            throw new ExcecaoNegocioGenerica("Ja existe uma condifuração para o layout " + layoutArquivoBordero.getDescricao() + " Altere ou Exclua o Layout Cadastrado!");
        } catch (NoResultException ex) {
            return;
        }
        throw new ExcecaoNegocioGenerica("Ja existe uma condifuração para o layout " + layoutArquivoBordero.getDescricao() + " Altere ou Exclua o Layout Cadastrado!");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(LayoutArquivoRemessa entity) {
        super.salvarNovo(entity); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void salvar(LayoutArquivoRemessa entity) {
        super.salvar(entity); //To change body of generated methods, choose Tools | Templates.
    }
}
