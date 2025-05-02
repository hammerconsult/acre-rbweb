package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CadastroEconomicoSubvencao;
import br.com.webpublico.entidades.OrdenacaoParcelaSubvencao;
import br.com.webpublico.entidades.SubvencaoParametro;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 17/12/13
 * Time: 16:01
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class SubvencaoParametroFacade extends AbstractFacade<SubvencaoParametro> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public SubvencaoParametroFacade() {
        super(SubvencaoParametro.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Boolean temMaisDeUmRegistroCadastrado() {
        Query q = em.createNativeQuery(" select * from subvencaoparametro");
        if (q.getResultList().isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public SubvencaoParametro recuperar(Object id) {
        SubvencaoParametro subvencao = em.find(SubvencaoParametro.class, id);
        subvencao.getCadastroEconomicoSubvencao().size();
        subvencao.getListaDividaSubvencao().size();
        subvencao.getItemOrdanacaoParcelaSubvencao().size();
        if (!subvencao.getCadastroEconomicoSubvencao().isEmpty()) {
            for (CadastroEconomicoSubvencao cadastroEconomicoSubvencao : subvencao.getCadastroEconomicoSubvencao()) {
                cadastroEconomicoSubvencao.getEmpresaDevedoraSubvencao().size();
            }
        }
        return subvencao;
    }

    public String retornaPrimeiroAssinante() {
        Query q = em.createNativeQuery(" select parametro.primeiroassinante from subvencaoparametro parametro");
        if (q.getResultList().isEmpty()) {
            return "";
        } else {
            return (String) q.getSingleResult();
        }
    }

    public String retornaPrimeiroCargo() {
        Query q = em.createNativeQuery(" select parametro.primeirocargo from subvencaoparametro parametro");
        if (q.getResultList().isEmpty()) {
            return "";
        } else {
            return (String) q.getSingleResult();
        }
    }

    public String retornaPrimeiroDecreto() {
        Query q = em.createNativeQuery(" select parametro.primeirodecreto from subvencaoparametro parametro");
        if (q.getResultList().isEmpty()) {
            return "";
        } else {
            return (String) q.getSingleResult();
        }
    }

    public String retornaSegundoAssinante() {
        Query q = em.createNativeQuery(" select parametro.segundoassinante from subvencaoparametro parametro");
        if (q.getResultList().isEmpty()) {
            return "";
        } else {
            return (String) q.getSingleResult();
        }
    }

    public String retornaSegundoCargo() {
        Query q = em.createNativeQuery(" select parametro.segundocargo from subvencaoparametro parametro");
        if (q.getResultList().isEmpty()) {
            return "";
        } else {
            return (String) q.getSingleResult();
        }
    }


    public String retornaSegundoDecreto() {
        Query q = em.createNativeQuery(" select parametro.segundodecreto from subvencaoparametro parametro");
        if (q.getResultList().isEmpty()) {
            return "";
        } else {
            return (String) q.getSingleResult();
        }
    }

    public SubvencaoParametro retornarParametro() {
        String sql = "select parametro from SubvencaoParametro parametro";
        Query q = em.createQuery(sql);
        if (!q.getResultList().isEmpty()) {
            return (SubvencaoParametro) q.getResultList().get(0);
        }
        return null;
    }

    public List<OrdenacaoParcelaSubvencao> buscarListaOrdenacaoSubvencao() {
        String sql = "select * from OrdenacaoParcelaSubvencao order by ordem";
        Query q = em.createNativeQuery(sql, OrdenacaoParcelaSubvencao.class);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }
}

