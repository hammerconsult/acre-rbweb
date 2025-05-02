/*
 * Codigo gerado automaticamente em Fri Feb 11 14:10:10 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.singletons.CacheTributario;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class ExercicioFacade extends AbstractFacade<Exercicio> {

    private static final int ANO_ATUAL = Calendar.getInstance().get(Calendar.YEAR);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CacheTributario cacheTributario;

    public ExercicioFacade() {
        super(Exercicio.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Exercicio getExercicioPorAno(int ano) {
        Query q = em.createQuery("from Exercicio exercicio where exercicio.ano=:ano");
        q.setParameter("ano", ano);
        if (!q.getResultList().isEmpty()) {
            return (Exercicio) q.getSingleResult();
        }
        throw new ExcecaoNegocioGenerica("Nenhum Exercício foi cadastrado para o ano de " + ano);
    }


    public Exercicio getExercicioCorrente() {
        return getExercicioPorAno(DataUtil.getAno(SistemaFacade.getDataCorrente()));
    }

    //    public static Integer getAnoCorrente(){
//        Calendar c = Calendar.getInstance();
//        return c.get(Calendar.YEAR);
//    }
    public Integer sugereProximoExerciocio() {
        Query q = getEntityManager().createQuery("from Exercicio e order by e.id desc");
        q.setMaxResults(1);
        if (q.getResultList().size() > 0) {
            Exercicio e = (Exercicio) q.getResultList().get(0);
            Integer retorno = e.getAno();
            return retorno + 1;
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            Integer ano = Integer.parseInt(format.format(new Date()));
            return ano;
        }
    }

    public List<Exercicio> listaNaoUtilizados() {
        String sql = "SELECT * FROM exercicio ex "
                + "WHERE ex.id NOT IN (SELECT b.exercicio_id FROM exercplanoestrategico b)"
                + " ORDER BY ex.ano";
        Query q = em.createNativeQuery(sql, Exercicio.class);
        return q.getResultList();
    }

    public List<Exercicio> listaExerciciosAtualFuturos() {
        String sql = "from Exercicio ex"
                + " where ex.ano >= :parametroAno"
                + " order by ex.ano";
        Query q = em.createQuery(sql);
        q.setParameter("parametroAno", ANO_ATUAL);
        return q.getResultList();
    }

    public List<Exercicio> listaExerciciosAtualFuturosFiltrando(String parte) {
        String sql = " select ex.* from Exercicio ex "
            + " where ex.ano >= :parametroAno "
            + " and ex.ano like :parte "
            + " order by ex.ano ";
        Query q = em.createNativeQuery(sql, Exercicio.class);
        q.setParameter("parametroAno", ANO_ATUAL);
        q.setParameter("parte", "%" + parte + "%");
        return q.getResultList();
    }

    public List<Exercicio> buscarExerciciosFuturosSemRelatorio(String parte, RelatoriosItemDemonst relatoriosItemDemonst, Exercicio exercicioAtual) {
        String sql = " select ex.* from Exercicio ex "
            + " where ex.ano >= :parametroAno "
            + " and ex.ano like :parte "
            + " and ex.id not in (select exercicio_id from relatoriositemdemonst "
            + "  where descricao like :descricao and tipoRelatorioItemDemonstrativo like :tipoRelatorio and exercicio_id is not null) "
            + " order by ex.ano ";
        Query q = em.createNativeQuery(sql, Exercicio.class);
        q.setParameter("parametroAno", exercicioAtual.getAno());
        q.setParameter("parte", "%" + parte + "%");
        q.setParameter("descricao", relatoriosItemDemonst.getDescricao());
        q.setParameter("tipoRelatorio", relatoriosItemDemonst.getTipoRelatorioItemDemonstrativo().name());
        return q.getResultList();
    }

    public List<Exercicio> listaExerciciosAtual() {
        String sql = "from Exercicio ex"
                + " where ex.ano <= :parametroAno"
                + " order by ex.ano desc";
        Query q = em.createQuery(sql);
        q.setParameter("parametroAno", ANO_ATUAL);
        return q.getResultList();
    }

    /**
     * Método utilizado principalmente para montar SelectOneMenus de exercícios
     *
     * @return uma lista com os exercícios anteriores e atual ordenados de forma
     *         decrescente.
     */
    public List<Exercicio> getExerciciosAtualPassados() {
        String sql = "from Exercicio ex"
                + " where ex.ano <= :parametroAno"
                + " order by ex.ano desc";
        Query q = em.createQuery(sql);
        q.setParameter("parametroAno", ANO_ATUAL);
        return q.getResultList();
    }

    public List<Exercicio> buscarExerciciosAnterioresAnoAtual(Integer filtro) {
        String sql = "FROM Exercicio e WHERE e.ano <= :filtro order by e.ano desc";
        Query q = em.createQuery(sql);
        q.setParameter("filtro", filtro - 1);
        return q.getResultList();
    }


    public List<Exercicio> listaFiltrandoExerciciosAtualPassados(String parte) {
        String sql = " select ex.* from exercicio ex "
                + " where ex.ano <= :parametroAno "
                + " and ex.ano like :parte "
                + " order by ex.ano desc ";
        Query q = em.createNativeQuery(sql, Exercicio.class);
        q.setParameter("parametroAno", ANO_ATUAL);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Exercicio> listaPorPpa(PPA ppa) throws ExcecaoNegocioGenerica {
        try {

            String sql = "SELECT DISTINCT e.* FROM exercicio e "
                    + "INNER JOIN exercplanoestrategico epe ON e.id = epe.exercicio_id "
                    + "INNER JOIN planejamentoestrategico pe ON pe.id = epe.planejamentoestrategico_id "
                    + "INNER JOIN ppa x ON pe.id = x.planejamentoestrategico_id "
                    + "WHERE x.id = :param";
            Query q = em.createNativeQuery(sql, Exercicio.class);
            q.setParameter("param", ppa.getId());
            return q.getResultList();
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar exercicios do PPA " + ppa);
        }
    }

    public List<Exercicio> listaPorPpaFiltrando(PPA ppa, String parte) {
        String sql = "SELECT DISTINCT e.* FROM exercicio e "
                + "INNER JOIN exercplanoestrategico epe ON e.id = epe.exercicio_id "
                + "INNER JOIN planejamentoestrategico pe ON pe.id = epe.planejamentoestrategico_id "
                + "INNER JOIN ppa x ON pe.id = x.planejamentoestrategico_id "
                + "WHERE x.id = :param AND E.ANO LIKE :parte";
        Query q = em.createNativeQuery(sql, Exercicio.class);
        q.setParameter("param", ppa.getId());
        q.setParameter("parte", "%" + parte.trim() + "%");
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    @Override
    public List<Exercicio> listaDecrescente() {
        String sql = "select ex from Exercicio ex"
                + " order by ex.ano desc";
        Query q = em.createQuery(sql);
        return q.getResultList();
    }


    public List<Exercicio> listaFiltrandoEspecial(String parte) {
        String hql = "select e from " + Exercicio.class.getSimpleName() + " as e"
                + " where to_char(e.ano) like :parte";

        Query query = em.createQuery(hql);
        query.setMaxResults(50);
        query.setParameter("parte", "%" + parte.toLowerCase() + "%");
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<Exercicio>();
        }
    }

    public LOA getLoaPorExercicio(Exercicio exerc) throws ExcecaoNegocioGenerica {
        String sql = "SELECT lo.* FROM LOA lo INNER JOIN LDO ld ON lo.LDO_ID= ld.ID AND ld.EXERCICIO_ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql, LOA.class);
        q.setParameter("param", exerc.getId());
        int tot = q.getResultList().size();

        if (tot == 0) {
            throw new ExcecaoNegocioGenerica("Não Existe nenhuma LOA para este exercicio");
        } else if (tot > 1) {
            throw new ExcecaoNegocioGenerica("Existe mais de uma LOA para este exercicio");
        }

        if (q.getSingleResult() instanceof LOA) {
            //System.out.println("E loa");
        } else if (q.getSingleResult() instanceof LDO) {
            //System.out.println("E LDO");
        } else if (q.getSingleResult() instanceof Long) {
            //System.out.println("long");
        }
        return (LOA) q.getSingleResult();
    }

    public LDO getLdoPorExercicio(Exercicio exerc) throws ExcecaoNegocioGenerica {
        String sql = "SELECT ld.* FROM LDO ld WHERE ld.EXERCICIO_ID =:param ";
        Query q = getEntityManager().createNativeQuery(sql, LDO.class);
        q.setParameter("param", exerc.getId());
        int tot = q.getResultList().size();

        if (tot == 0) {
            throw new ExcecaoNegocioGenerica("Não Existe nenhuma LDO para este exercicio");
        } else if (tot > 1) {
            throw new ExcecaoNegocioGenerica("Existe mais de uma LDO para este exercicio");
        }

        return (LDO) q.getSingleResult();
    }

    @Override
    public void salvarNovo(Exercicio exercicio) {
        ParametrosExercicio p = new ParametrosExercicio();
        p.setUltimoDAM(new Long(0));
        p.setExercicio(exercicio);
        exercicio.setParametros(p);
        super.salvarNovo(exercicio);
        cacheTributario.init();
    }

    @Override
    public void salvar(Exercicio exercicio) {
        //System.out.println("exercicioFacade.salvar() getParametros() = " + exercicio.getParametros());
        if (exercicio.getParametros() == null) {
            ParametrosExercicio p = new ParametrosExercicio();
            p.setExercicio(exercicio);
            p.setUltimoDAM(new Long(0));
            exercicio.setParametros(p);
        }
        super.salvar(exercicio);
        cacheTributario.init();
    }

    @Override
    public List<Exercicio> lista() {
        return em.createQuery("from Exercicio e order by e.ano desc").getResultList();
    }

    public List<SelectItem> getExerciciosComprasLicitacao() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (Exercicio exercicio : listaExerciciosAtual()) {
            retorno.add(new SelectItem(exercicio, exercicio.getAno().toString()));
        }
        return retorno;
    }

    public Exercicio recuperarExercicioPeloAno(Integer ano) {
        String hql = "from Exercicio exercicio where exercicio.ano=:ano";
        Query q = em.createQuery(hql);
        q.setParameter("ano", ano);
        if (!q.getResultList().isEmpty()) {
            return (Exercicio) q.getResultList().get(0);
        }
        return null;
    }

    public List<Exercicio> buscarPorIntervaloDeAno(Integer anoInicial, Integer anoFinal) {
        return buscarPorIntervaloDeAno(anoInicial, anoFinal, true);
    }

    public List<Exercicio> buscarPorIntervaloDeAno(Integer anoInicial, Integer anoFinal, boolean ordenacaoDesc) {
        StringBuilder sql = new StringBuilder();
        String juncao = " where ";
        sql.append(" select * from exercicio e ");

        if (anoInicial != null && anoInicial > 0) {
            sql.append(juncao).append(" e.ano >= :inicial ");
            juncao = " and ";
        }
        if (anoFinal != null && anoFinal > 0) {
            sql.append(juncao).append("  e.ano <= :final ");
            juncao = " and ";
        }
        sql.append(" order by e.ano  ").append(ordenacaoDesc ? " desc " : " asc ");
        Query q = em.createNativeQuery(sql.toString(), Exercicio.class);
        if (anoInicial != null && anoInicial > 0) {
            q.setParameter("inicial", anoInicial);
        }
        if (anoFinal != null && anoFinal > 0) {
            q.setParameter("final", anoFinal);
        }
        q.setMaxResults(1000);
        return q.getResultList();

    }
}
