/*
 * Codigo gerado automaticamente em Thu Aug 04 08:42:59 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.Sindicato;
import org.springframework.transaction.annotation.Transactional;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class SindicatoFacade extends AbstractFacade<Sindicato> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    @EJB
    private ItemSindicatoFacade itemSindicatoFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SindicatoFacade() {
        super(Sindicato.class);
    }

    public boolean existeCodigo(Sindicato sindicato) {
        String hql = " from Sindicato s where lower(trim(s.codigo)) = :codigoParametro ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigoParametro", sindicato.getCodigo());

        List<Sindicato> lista = q.getResultList();

        if (lista.contains(sindicato)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    @Override
    public Sindicato recuperar(Object id) {
        Sindicato sindicato = em.find(Sindicato.class, id);
        sindicato.getItensSindicatos().size();
        return sindicato;
    }

    public PessoaJuridicaFacade getPessoaJuridicaFacade() {
        return pessoaJuridicaFacade;
    }

    public ItemSindicatoFacade getItemSindicatoFacade() {
        return itemSindicatoFacade;
    }

    public EventoFPFacade getEventoFPFacade() {
        return eventoFPFacade;
    }

    public List<Sindicato> buscarSindicatoCNPJRazao(String filtro) {
        String sql = " select s.* " +
            "            from sindicato s " +
            "         inner join pessoajuridica pj on pj.id = s.pessoaJuridica_id" +
            "          where (lower(pj.razaoSocial) like :filtro) " +
            "             or (pj.cnpj like :filtro) " +
            "             or (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :filtro) ";
        Query q = em.createNativeQuery(sql, Sindicato.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    @Transactional
    public Sindicato buscarSindicatoPeloCargo(Cargo cargo) {
        Query query = em.createNativeQuery("select  sindicato.* from ItemCargoSindicato item " +
            " inner join sindicato sindicato on item.sindicato_id = sindicato.id " +
            " where sysdate between item.DATAINICIO and coalesce(item.datafim, sysdate)" +
            " and item.CARGO_ID = :cargo", Sindicato.class);
        query.setParameter("cargo", cargo.getId());
        List resultList = query.getResultList();
        if (!resultList.isEmpty()) {
            return (Sindicato) resultList.get(0);
        }
        return null;
    }
}
