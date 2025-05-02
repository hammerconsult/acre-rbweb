/*
 * Codigo gerado automaticamente em Thu Aug 04 15:16:54 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.PerfilEnum;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Stateless
public class MatriculaFPFacade extends AbstractFacade<MatriculaFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public MatriculaFPFacade() {
        super(MatriculaFP.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<MatriculaFP> recuperaMatriculasPorPessoa(PessoaFisica pessoaFisica) throws Exception {
        Query q = em.createQuery("from MatriculaFP m where m.pessoa = :pessoa");
        q.setParameter("pessoa", pessoaFisica);
        try {
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean pessoaJaPossuiMatricula(MatriculaFP matricula) {
        Query q = em.createQuery("from MatriculaFP m where m.pessoa = :pessoa");
        q.setParameter("pessoa", matricula.getPessoa());
        q.setMaxResults(1);
        try {
            return q.getSingleResult() != null ? true : false;
        } catch (NoResultException e) {
            return false;
        }

    }

    public String incrementaNumeroPorUnidadeOrganizacional(UnidadeOrganizacional u) {
        Query q = em.createQuery("select max(m.matricula) from MatriculaFP m where m.unidadeMatriculado = :u");
        q.setParameter("u", u);
        return (String) q.getSingleResult();
    }

    public String incrementaNumero() {
        String num;
        String hql = " select max(cast(m.matricula as integer)) from MatriculaFP m ";
        Query query = em.createQuery(hql);
        query.setMaxResults(1);
        if (query.getSingleResult() != null) {
            Integer i = (Integer) query.getSingleResult() + 1;
            num = String.valueOf(i);
        } else {
            return "1";
        }
        return num;
    }

    public List<MatriculaFP> recuperaMatriculaFiltroPessoa(String s) {
        List<MatriculaFP> retorno = new ArrayList<>();

        String hql1 = " from MatriculaFP obj "
            + "    where (lower(obj.pessoa.nome) like :filtro1) OR  (replace(replace(replace(obj.pessoa.cpf,'.',''),'-',''),'/','') like :filtro1) "
            + "       OR (lower(obj.matricula) like :filtro1) ";

        String hql2 = " from MatriculaFP obj "
            + "    where (lower(obj.pessoa.nome) like :filtro2) OR  (replace(replace(replace(obj.pessoa.cpf,'.',''),'-',''),'/','') like :filtro2) "
            + "       OR (lower(obj.matricula) like :filtro2) ";

        Query q1 = em.createQuery(hql1).setMaxResults(10).setParameter("filtro1", s.toLowerCase() + "%");
        ;
        Query q2 = em.createQuery(hql2).setMaxResults(10).setParameter("filtro2", "%" + s.toLowerCase() + "%");
        ;

        List<MatriculaFP> resultado1 = q1.getResultList();
        List<MatriculaFP> resultado2 = q2.getResultList();

        for (MatriculaFP matriculaFP : resultado1) {
            if (resultado2.contains(matriculaFP)) {
                resultado2.remove(matriculaFP);
            }
        }
        retorno.addAll(resultado1);
        retorno.addAll(resultado2);

        ordenaMatriculasPorId(retorno);

        return retorno;
    }

    private void ordenaMatriculasPorId(List<MatriculaFP> retorno) {
        Collections.sort(retorno, new Comparator<MatriculaFP>() {
            @Override
            public int compare(MatriculaFP o1, MatriculaFP o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
    }

    public List<MatriculaFP> recuperaMatriculaFiltroPessoaPensionista(String s) {
        String hql = " select distinct obj from MatriculaFP obj join obj.pessoa.perfis per where :perfil IN(per) and ";
        hql += "(lower(obj.pessoa.nome) like :filtro) OR  (replace(replace(replace(obj.pessoa.cpf,'.',''),'-',''),'/','') like :filtro)"
            + " OR (lower(obj.matricula) like :filtro) ";
        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("perfil", PerfilEnum.PERFIL_PENSIONISTA.name());
        return q.getResultList();
    }

    public List<MatriculaFP> buscarMatriculaFpPorNomeOrCpf(String s) {
        String hql = "from MatriculaFP obj where ((lower(obj.pessoa.nome) like :filtro) OR (lower(obj.pessoa.cpf) like :filtro))";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<MatriculaFP> listaFiltrandoX(String s, int inicio, int max) {
        String hql = " select matricula from MatriculaFP matricula "
            + " inner join matricula.pessoa pf "
            + " inner join matricula.unidadeMatriculado unidade "
            + " where (lower(matricula.matricula) like :filtro) "
            + " or (lower(pf.nome) like :filtro) "
            + " or (lower(unidade.descricao) like :filtro) ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");

        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public MatriculaFP recuperaMatriculaFPPorContratoFP(ContratoFP contrato) {
        Query q = this.em.createNativeQuery("SELECT m.* FROM matriculafp JOIN vinculofp v ON m.id = v.matriculafp_id WHERE v.id = :contratofp");
        q.setParameter("contratofp", contrato.getId());
        if (!q.getResultList().isEmpty()) {
            return (MatriculaFP) q.getResultList().get(0);
        } else {
            throw new ExcecaoNegocioGenerica("A matricula do contrato " + contrato.getDescricao() + "não foi encontrada.");
        }
    }

    public List<MatriculaFP> recuperarMatriculasComContratoVigente(String parte) {
        StringBuilder sql = new StringBuilder("");
        sql.append(" select matricula.* from MatriculaFP matricula");
        sql.append(" inner join VinculoFP vinculo on vinculo.matriculafp_id = matricula.id");
        sql.append(" inner join ContratoFP contrato on contrato.id = vinculo.id and :data >= vinculo.inicioVigencia and :data <= coalesce(vinculo.finalVigencia, :data) ");
        sql.append(" inner join Pessoa pessoa on pessoa.id = matricula.pessoa_id");
        sql.append(" inner join PessoaFisica pessoaFisica on pessoaFisica.id = pessoa.id");
        sql.append(" where lower(trim(matricula.matricula)) like :parte");
        sql.append(" or lower(trim(pessoafisica.nome)) like :parte");
        Query q = em.createNativeQuery(sql.toString(), MatriculaFP.class);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        q.setParameter("parte", "%" + Util.removeMascaras(parte).trim().toLowerCase() + "%");
        q.setMaxResults(10);

        return q.getResultList();
    }

    public MatriculaFP matriculaFPVigentePorPessoa(PessoaFisica pessoa) {
        Query q = em.createQuery("from VinculoFP obj where obj.matriculaFP.pessoa = :pessoa and :data between obj.inicioVigencia and coalesce(obj.finalVigencia,:data) ");
        q.setParameter("pessoa", pessoa);
        q.setParameter("data", sistemaFacade.getDataOperacao());
        if (q.getResultList().isEmpty()) {
            return null;
        }
        VinculoFP v = (VinculoFP) q.getResultList().get(0);
        return v.getMatriculaFP();
    }

    public Long buscarMatriculaDaPessoa(Long idPessoa) {
        Query q = em.createNativeQuery("select m.id from matriculafp m where m.pessoa_id = :pessoa_id");
        q.setParameter("pessoa_id", idPessoa);
        q.setMaxResults(1);
        try {
            return Long.parseLong("" + q.getSingleResult());
        } catch (NoResultException nre) {
            return null;
        }
    }

    public Boolean isExistsMatriculaCadastrada(String matricula) {
        Query q = em.createNativeQuery("select 1 from matriculafp m where m.MATRICULA = :matricula ");
        q.setParameter("matricula", matricula);
        if (q.getResultList().isEmpty()) {
            return false;
        }
        return true;
    }

    public List<MatriculaFP> buscarMatriculasPorCapacitacao(Capacitacao capacitacao, String filtro) {
        String sql = " Select m.* from capacitacao c " +
            "          inner join inscricaocapacitacao ic on ic.capacitacao_id = c.id " +
            "          inner join matriculafp m on m.id = ic.matriculafp_id " +
            "          inner join pessoafisica pf on pf.id = m.pessoa_id " +
            "          where c.id = :capacitacao ";
        if (!filtro.isEmpty()) {
            sql += " and upper(pf.nome) like :filtro ";
        }
        Query q = em.createNativeQuery(sql, MatriculaFP.class);
        q.setParameter("capacitacao", capacitacao.getId());

        if (!filtro.isEmpty()) {
            q.setParameter("filtro", "%" + filtro.toUpperCase() + "%");
        }
        return q.getResultList();
    }

    public MatriculaFP buscarMatriculaFPPorMatricula(String matricula) {
        Query q = em.createQuery("from MatriculaFP obj where obj.matricula = :matricula ");
        q.setParameter("matricula", matricula);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (MatriculaFP) resultList.get(0);
        }
        return null;
    }

    public List<MatriculaFP> buscarMatriculasPorPessoaOrMatricula(String filtro) {
        Query q = em.createQuery("select distinct mat from VinculoFP obj "
            + " inner join obj.matriculaFP mat "
            + " where (lower(obj.matriculaFP.pessoa.nome) like :filtro)"
            + " or (lower(obj.matriculaFP.matricula) like :filtro)");
        q.setParameter("filtro", "%" + filtro.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }
}
