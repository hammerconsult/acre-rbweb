/*
 * Codigo gerado automaticamente em Tue Feb 08 13:53:03 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.UF;
import br.com.webpublico.singletons.CacheRH;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.HashMap;
import java.util.List;

@Stateless
public class UFFacade extends AbstractFacade<UF> {

    @EJB
    private PaisFacade paisFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CacheRH cacheRH;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvar(UF entity) {
        super.salvar(entity);
        initializeCache();
    }

    @Override
    public void salvarNovo(UF entity) {
        super.salvarNovo(entity);
        initializeCache();
    }

    private void initializeCache() {
        cacheRH.setCidades(null);
        cacheRH.setCidadesPorEstado(new HashMap<Long, List<Cidade>>());
    }

    public UFFacade() {
        super(UF.class);
    }

    public List<UF> listaUFNaoNula() {
        Query q = em.createNativeQuery(" SELECT uf.* FROM uf WHERE uf.uf IS NOT null ORDER BY uf.uf ", UF.class);
        return q.getResultList();
    }

    public String recuperaUfPorNome(String nome) throws ExcecaoNegocioGenerica {
        try {
            if (nome.trim().length() > 2) {
                String sql = "select trim(e.uf) as sigla from enderecocorreio en\n"
                    + "inner join uf e on upper(e.nome) = upper(en.uf)\n"
                    + "where length(en.uf)>2"
                    + "and e.nome=:nome ";
                Query q = getEntityManager().createNativeQuery(sql);
                q.setParameter("nome", nome.toUpperCase().trim());

                String toReturn = (String) q.getSingleResult();
                return toReturn;
            } else {
                return nome;
            }
        } catch (NonUniqueResultException ex) {
            throw new ExcecaoNegocioGenerica("O " + nome + " Contem mais de uma UF cadastrada");
        } catch (NoResultException ex) {
            throw new ExcecaoNegocioGenerica("O " + nome + " Não Contem uma UF cadastrada");
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao tentar Localizar a UF " + nome + " " + ex.getMessage());
        }
    }

    public UF recuperaUfPorUf(String uf) {
        String sql = "select uf from UF uf "
            + "where uf.uf = :uf ";
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("uf", uf.toUpperCase().trim());
        if (!q.getResultList().isEmpty()) {
            return (UF) q.getResultList().get(0);
        }
        return null;
    }

    public UF recuperaUfPorCodigoIBGE(Integer codigoIBGE) {
        String sql = " from UF "
            + "where uf.codigoIBGE = :codigoIBGE ";
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("codigoIBGE", codigoIBGE);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (UF) resultList.get(0);
        }
        return null;
    }

    public List<UF> buscarUFsDoBrasil() {
        Query q = em.createNativeQuery(" SELECT uf.* FROM uf JOIN pais on uf.pais_id = pais.id WHERE pais.codigo =:codigoPais ORDER BY uf.uf ", UF.class);
        q.setParameter("codigoPais", 55);
        return q.getResultList();
    }

    @Override
    public List<UF> listaFiltrando(String s, String... atributos) {
        return super.listaFiltrando(s, atributos);
    }

    public PaisFacade getPaisFacade() {
        return paisFacade;
    }
}
