/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.RevisaoAuditoria;
import br.com.webpublico.entidadesauxiliares.RelatorioPesquisaGenerico;
import br.com.webpublico.util.Persistencia;
import org.hibernate.Hibernate;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Renato
 */
@Stateless
public class ComponentePesquisaGenericoFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private List<Object[]> listaHistoricoGeral;
    @EJB
    private CabecalhoRelatorioFacade cabecalhoRelatorioFacade;
    @EJB
    private RecuperadorFacade recuperadorFacade;
    @EJB
    private RelatorioPesquisaGenericoFacade relatorioPesquisaGenericoFacade;

    public List<Object[]> getListaHistoricoGeral() {
        return listaHistoricoGeral;
    }

    public void setListaHistoricoGeral(List<Object[]> listaHistoricoGeral) {
        this.listaHistoricoGeral = listaHistoricoGeral;
    }

    public CabecalhoRelatorioFacade getCabecalhoRelatorioFacade() {
        return cabecalhoRelatorioFacade;
    }

    public RecuperadorFacade getRecuperadorFacade() {
        return recuperadorFacade;
    }


    public List<Object[]> recuperarHistoricoAlteracoes(Object objeto) throws IllegalArgumentException, IllegalAccessException {
        AuditReader reader = AuditReaderFactory.get(em);
        Long id = (Long) Persistencia.getId(objeto);
        Class<?> classe = objeto.getClass();
        List<Object[]> lista = reader.createQuery().forRevisionsOfEntity(classe, false, true).add(AuditEntity.id().eq(id)).addOrder(AuditEntity.revisionNumber().desc()).getResultList();

        for (Object[] object : lista) {
            for (Field field : Persistencia.getAtributos(object[0].getClass())) {
                field.setAccessible(true);
                if (possuiRelacionamento(field)) {
                    try {
                        Persistencia.getAttributeValue(object[0], field.getName()).toString();
                        field.set(object[0], initializeAndUnproxy(Persistencia.getAttributeValue(object[0], field.getName())));
                    } catch (Exception ex) {
                        field.set(object[0], null);
                    }
                }
            }
        }

        return lista;
    }

    public Object[] recuperarEntidadeEspecifica(Object obj, Number revisao) {
        AuditReader reader = AuditReaderFactory.get(em);
        Object[] o = (Object[]) reader.createQuery().forRevisionsOfEntity(obj.getClass(), false, true).add(AuditEntity.id().eq(Persistencia.getId(obj))).add(AuditEntity.revisionNumber().eq(revisao)).getSingleResult();
        return o;
    }

    public List<Object[]> recuperarRevisaoClasse(Class classe, RevisaoAuditoria rev) {
        AuditReader reader = AuditReaderFactory.get(em);

        List<Object[]> lista = reader.createQuery().forRevisionsOfEntity(classe, false, true).add(AuditEntity.revisionNumber().eq(rev.getId())).getResultList();

        return lista;
    }

    public boolean possuiRelacionamento(Field campo) {
        return campo.isAnnotationPresent(ManyToOne.class)
            || campo.isAnnotationPresent(OneToOne.class);
    }

    public Object initializeAndUnproxy(Object var) {
        if (var == null) {
            return null;
        }
        Hibernate.initialize(var);
        if (var instanceof HibernateProxy) {
            var = ((HibernateProxy) var).getHibernateLazyInitializer().getImplementation();
        }
        return var;
    }

    public EntityManager getEm() {
        return em;
    }

    public void salvarConfiguracaoRelatorioPesquisaGenerico(RelatorioPesquisaGenerico relatorio) {
        try {
            if (relatorio.getId() == null) {
                em.persist(relatorio);
            } else {
                em.merge(relatorio);
            }
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }


    public RelatorioPesquisaGenericoFacade getRelatorioPesquisaGenericoFacade() {
        return relatorioPesquisaGenericoFacade;
    }

    public Long count(String hql) {

        try {
            Query query = em.createQuery(hql);
            return (Long) query.getSingleResult();
        } catch (NoResultException nre) {
            return 0l;
        }
    }

    public List<Object> filtar(String sql, Object objeto, int inicio, int max) throws Exception {
        Query consulta = em.createQuery(sql);
        if (max != 0) {
            consulta.setMaxResults(max + 1);
            consulta.setFirstResult(inicio);
        }
        List<Object> lista = consulta.getResultList();
        for (Object object : lista) {
            for (Field field : Persistencia.getAtributos(object.getClass())) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(br.com.webpublico.util.anotacoes.Invisivel.class) && recuperadorFacade.isCampoTipoLista(field)) {
//                    field.set(object, recuperadorFacade.obterCampo(objeto, field));
                    field.set(object, initializeAndUnproxy(Persistencia.getAttributeValue(object, field.getName())));
                }
            }
        }
        return lista;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Object[] filtarComContadorDeRegistros(String sql, String sqlCount, Object objeto, int inicio, int max) throws Exception {
        Query consulta = em.createQuery(sql);

        Query consultaCount = em.createQuery(sqlCount);

        Long count = 0l;
        List<Object> lista = new ArrayList<>();
        try {
            count = (Long) consultaCount.getSingleResult();

            if (max != 0) {
                consulta.setMaxResults(max);
                consulta.setFirstResult(inicio);
            }

            lista = consulta.getResultList();
//            Util.imprimeSQL(sql, consulta);
            //Util.imprimeSQL(sqlCount, consultaCount);
            for (Object object : lista) {
                for (Field field : Persistencia.getAtributos(object.getClass())) {
                    field.setAccessible(true);
                    if (!field.isAnnotationPresent(br.com.webpublico.util.anotacoes.Invisivel.class) && recuperadorFacade.isCampoTipoLista(field)) {
                        field.set(object, initializeAndUnproxy(Persistencia.getAttributeValue(object, field.getName())));
                    }
                }
            }
        } catch (NoResultException nre) {
        }
        Object[] retorno = new Object[2];
        retorno[0] = lista;
        retorno[1] = count;
        return retorno;
    }
}
