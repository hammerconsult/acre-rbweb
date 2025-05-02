/*
 * Codigo gerado automaticamente em Thu Jan 12 10:56:38 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ServicoCompravel;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class ServicoCompravelFacade extends AbstractFacade<ServicoCompravel> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ServicoCompravelFacade() {
        super(ServicoCompravel.class);
    }

    public boolean validaCodigoEditar(ServicoCompravel sc) {

        String hql = " from ServicoCompravel m where m.codigo = :cod ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("cod", sc.getCodigo());

        List<ServicoCompravel> lista = q.getResultList();


        if (lista.contains(sc)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public Long recuperaUltimoCodigo() {
        String hql = " from ServicoCompravel a where a.codigo = (select max(b.codigo) from ServicoCompravel b)";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            ServicoCompravel sc = ((ServicoCompravel) q.getSingleResult());
            return sc.getCodigo() + 1;
        } else {
            return 1l;
        }
    }

    public boolean validaAlteracaoEExclusao(ServicoCompravel servico) {
        String sql = "SELECT count(servicocompravel_id)"
                + "     FROM itemsolicitacaoservico"
                + "    WHERE servicocompravel_id = :param";

        Query q = em.createNativeQuery(sql);
        q.setParameter("param", servico.getId());

        BigDecimal resultado = (BigDecimal) q.getSingleResult();

        if (resultado.compareTo(BigDecimal.ZERO) == 1) {
            return false;
        }

        return true;
    }

    public List<ServicoCompravel> completaServicoCompravel(String prm) {
        String sql = " select * from SERVICOCOMPRAVEL where (lower(descricao)) like :prm";
        Query q = em.createNativeQuery(sql, ServicoCompravel.class);
        q.setParameter("prm", "%" + prm.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }
}
