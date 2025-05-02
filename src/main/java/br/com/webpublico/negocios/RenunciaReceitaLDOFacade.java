/*
 * Codigo gerado automaticamente em Thu Jun 09 10:17:19 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.LDO;
import br.com.webpublico.entidades.RenunciaReceitaExercicioLDO;
import br.com.webpublico.entidades.RenunciaReceitaLDO;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class RenunciaReceitaLDOFacade extends AbstractFacade<RenunciaReceitaLDO> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RenunciaReceitaLDOFacade() {
        super(RenunciaReceitaLDO.class);
    }

    /**
     * @param {@link #LDO}(ldo.getExercicio) fornecera o exercicio para resgatar
     *               os três proximos exercios.
     * @return {@link java.util.List}&lt;{@link br.com.webpublico.entidades.Exercicio}&gt;
     *         Lista com os três próximos exercicios calculados através do
     *         {@link br.com.webpublico.entidades.LDO}(ldo.getExercicio)
     *         informado para criar uma RenunciaReceitaExercicioLDO referente a cada
     *         exercicio.
     * @throws ExcecaoNegocioGenerica
     * @throws Exceção                no caso do cadastro de exercio não possuir os três
     *                                proximos exercicios reaferente a o
     *                                {@link br.com.webpublico.entidades.LDO} informado
     * @author Wlademir
     * @see Exercicio
     * @see LDO
     */
    private List<Exercicio> listaExercicios(LDO ldo) throws ExcecaoNegocioGenerica {
        Integer anoLdo = ldo.getExercicio().getAno();
        Integer anoUm = anoLdo;
        Integer anoDois = anoLdo + 1;
        Integer anoTres = anoLdo + 2;
        String hql = "from Exercicio e where e.ano in(:anoUm,:anoDois,:anoTres)";
        String msg = "";
        Query q = em.createQuery(hql);
        q.setParameter("anoUm", anoUm);
        q.setParameter("anoDois", anoDois);
        q.setParameter("anoTres", anoTres);

        List<Exercicio> listaExerc = q.getResultList();
        msg = "É necessário que os três próximos exercícios estejam cadastrados e só foram encontrado os Exercícios ";

        if (listaExerc.size() < 3) {
            listaExerc = new ArrayList<Exercicio>();
            throw new ExcecaoNegocioGenerica("É necessário que os três próximos exercícios estejam cadastrados");
        } else {
            for (Exercicio e : listaExerc) {
                msg += "," + e.getAno();
            }
        }
        msg += ". Verifique os Exercicios!";
        return listaExerc;
    }

    /**
     * @param {@link #LDO} fornecera o exercicio para chamar o metodo
     *               {@link #listaExercicios}.
     * @return {@link java.util.List}&lt;{@link br.com.webpublico.entidades.RenunciaReceitaLDO}&gt;
     *         Lista com os três registro com os
     *         {@link br.com.webpublico.entidades.LDO}(ldo.setExercicio) ja
     *         informados.
     * @author Wlademir
     * @see Exercicio
     * @see LDO
     * @see RenunciaReceitaLDO
     */
    public List<RenunciaReceitaExercicioLDO> listaRenunciaReceitaLDO(RenunciaReceitaLDO renunciaReceitaLDO) throws ExcecaoNegocioGenerica {
        List<Exercicio> listaExer = listaExercicios(renunciaReceitaLDO.getLdo());
        List<RenunciaReceitaExercicioLDO> listaRenucia = new ArrayList<RenunciaReceitaExercicioLDO>();
        RenunciaReceitaExercicioLDO renunciaReceitaExercicioLDO;
        LDO ldo = renunciaReceitaLDO.getLdo();
        for (Exercicio e : listaExer) {
            renunciaReceitaExercicioLDO = new RenunciaReceitaExercicioLDO(renunciaReceitaLDO, e, ldo, BigDecimal.ZERO);
            listaRenucia.add(renunciaReceitaExercicioLDO);
        }
        return listaRenucia;
    }

    @Override
    public RenunciaReceitaLDO recuperar(Object id) {
        RenunciaReceitaLDO r = em.find(RenunciaReceitaLDO.class, id);
        r.getRenunciaReceitaExercicioLDOs().size();
        return r;
    }
}
