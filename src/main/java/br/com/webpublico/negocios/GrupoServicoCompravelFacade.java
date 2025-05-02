/*
 * Codigo gerado automaticamente em Wed Jan 11 15:47:44 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.GrupoServicoCompravel;
import br.com.webpublico.entidades.ServicoCompravel;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class GrupoServicoCompravelFacade extends AbstractFacade<GrupoServicoCompravel> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrupoServicoCompravelFacade() {
        super(GrupoServicoCompravel.class);
    }

    public String geraCodigoNovo() {
        String codigoRetorno;
        String hql = "from GrupoServicoCompravel a where a.codigo = (select max(b.codigo) from GrupoServicoCompravel b)";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            String codigoInteiro = ((GrupoServicoCompravel) q.getSingleResult()).getCodigo();
            String quebrado[] = codigoInteiro.split("\\.");
            int primeiro = Integer.parseInt(quebrado[0]);
            primeiro++;
            if (primeiro < 10) {
                codigoRetorno = "0" + String.valueOf(primeiro);
            } else {
                codigoRetorno = String.valueOf(primeiro);
            }

        } else {
            codigoRetorno = "01";
        }
        return codigoRetorno;
    }

    public String geraCodigoFilho(GrupoServicoCompravel grupoServicoCompravel) {
        String codigoRetorno;
        String codigoPai = grupoServicoCompravel.getCodigo();

        String hql = "from GrupoServicoCompravel a where a.codigo = (select max(b.codigo) from GrupoServicoCompravel b where b.superior = :superior)";
        Query q = em.createQuery(hql).setParameter("superior", grupoServicoCompravel);
        q.setMaxResults(1);

        if (!q.getResultList().isEmpty()) {
            String codigoInteiro = ((GrupoServicoCompravel) q.getSingleResult()).getCodigo();
            String quebrado[] = codigoInteiro.split("\\.");
            int ultimo = Integer.parseInt(quebrado[quebrado.length - 1]);
            ultimo++;
            if (ultimo < 10) {
                String temp = ".0" + String.valueOf(ultimo);
                codigoRetorno = codigoPai + temp;
            } else {
                codigoRetorno = codigoPai + "." + String.valueOf(ultimo);
            }

        } else {
            codigoRetorno = codigoPai + ".01";
        }

        return codigoRetorno;
    }

    public GrupoServicoCompravel getRaiz() {
        GrupoServicoCompravel grupoServicoCompravel = null;
        Query q = em.createQuery("from GrupoMaterial g where g.superior is null");
        if (q.getResultList().isEmpty()) {
        } else {
            grupoServicoCompravel = (GrupoServicoCompravel) q.getSingleResult();

        }
        return grupoServicoCompravel;
    }

    public List<GrupoServicoCompravel> getFilhosDe(GrupoServicoCompravel raiz) {
        Query q = em.createQuery("from GrupoServicoCompravel g where g.superior = :superior");
        q.setParameter("superior", raiz);
        return q.getResultList();
    }

    public List<GrupoServicoCompravel> listaDescricaoPeloId(String string) {
        Query q = em.createQuery("from GrupoServicoCompravel g where g.descricao like :parte order by g.codigo asc");
        q.setParameter("parte", "%" + string + "%");
        return q.getResultList();
    }

    public List<ServicoCompravel> getObjetosDeCompra(GrupoServicoCompravel gb) {
        Query q = em.createQuery("from ServicoCompravel o where o.grupoServCompravel = :grupo");
        q.setParameter("grupo", gb);
        return q.getResultList();
    }
}
