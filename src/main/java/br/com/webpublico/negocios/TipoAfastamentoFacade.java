/*
 * Codigo gerado automaticamente em Mon Sep 05 09:12:19 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class TipoAfastamentoFacade extends AbstractFacade<TipoAfastamento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ReferenciaFPFacade referenciaFPFacade;
    @EJB
    private FaixaReferenciaFPFacade faixaReferenciaFPFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoAfastamentoFacade() {
        super(TipoAfastamento.class);
    }

    public ReferenciaFPFacade getReferenciaFPFacade() {
        return referenciaFPFacade;
    }

    public FaixaReferenciaFPFacade getFaixaReferenciaFPFacade() {
        return faixaReferenciaFPFacade;
    }

    @Override
    public TipoAfastamento recuperar(Object id) {
        TipoAfastamento tipoAfastamento = super.recuperar(id);

        if (tipoAfastamento.getReferenciaFP() != null) {
            if (tipoAfastamento.getReferenciaFP().getFaixasReferenciasFPs() != null && tipoAfastamento.getReferenciaFP().getFaixasReferenciasFPs().isEmpty()) {
                tipoAfastamento.getReferenciaFP().getFaixasReferenciasFPs().size();
            }
        }
        forcarRegistroInicialNaAuditoria(tipoAfastamento);


        return tipoAfastamento;
    }

    public boolean existeCodigo(TipoAfastamento tipoAfastamento) {
        String hql = " from TipoAfastamento tipo where tipo.codigo = :parametroCodigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", tipoAfastamento.getCodigo());

        List<TipoAfastamento> lista = new ArrayList<TipoAfastamento>();
        lista = q.getResultList();

        if (lista.contains(tipoAfastamento)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public List<TipoAfastamento> getTipoAfastamentoPorDescricaoCodigo(String filtro) {
        String hql = " from TipoAfastamento tipo where cast(tipo.codigo as string) like :parametroCodigo or lower(tipo.descricao) like :parametroCodigo order by tipo.codigo, tipo.descricao";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", "%" + filtro.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<TipoAfastamento> buscarTiposAfastamentoPorDescricaoCodigoAtivo(String filtro) {
        String hql = " from TipoAfastamento tipo where tipo.ativo = true and (cast(tipo.codigo as string) like :parametroCodigo or lower(tipo.descricao) like :parametroCodigo) order by tipo.codigo, tipo.descricao";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", "%" + filtro.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<TipoAfastamento> tiposAfastamentosAtivos() {
        String hql = " from TipoAfastamento tipo where tipo.ativo = true order by tipo.codigo";
        Query q = getEntityManager().createQuery(hql);
        return q.getResultList();
    }

    public List<TipoAfastamentoTipoPrevidenciaFP> recuperarTipoAfastamentoTipoPrevidenciaFPPorTipoAfastamento(Long id) {
        String sql = "select tAtP.* from tipoafastamentotipoprevidenciafp tAtP " +
            "inner join tipoAfastamento tA on tA.id = tAtP.tipoafastamento_id " +
            "where tA.id = :id";

        Query q = em.createNativeQuery(sql, TipoAfastamentoTipoPrevidenciaFP.class);
        q.setParameter("id", id);

        List<TipoPrevidenciaFP> resultado = q.getResultList();
        if (!resultado.isEmpty()){
            return q.getResultList();
        }else {
            return new ArrayList<>();
        }
    }

}
