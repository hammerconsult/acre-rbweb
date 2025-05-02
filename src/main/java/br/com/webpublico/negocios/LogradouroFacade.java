/*
 * Codigo gerado automaticamente em Fri Feb 11 08:09:30 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Bairro;
import br.com.webpublico.entidades.Logradouro;
import br.com.webpublico.entidades.LogradouroBairro;
import br.com.webpublico.entidades.Setor;
import br.com.webpublico.enums.SituacaoLogradouro;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class LogradouroFacade extends AbstractFacade<Logradouro> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CEPFacade cepFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private SetorFacade setorFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LogradouroFacade() {
        super(Logradouro.class);
    }

    public CEPFacade getCepFacade() {
        return cepFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public SetorFacade getSetorFacade() {
        return setorFacade;
    }

    @Override
    public Logradouro recuperar(Object id) {
        Logradouro l = em.find(Logradouro.class, id);
        l.getArquivos().size();
        return l;
    }

    public List<Logradouro> listaLogradourosAtivos(String s) {
        StringBuilder hql = new StringBuilder("select a from Logradouro a where a.situacao = 'ATIVO' ");
        hql.append(" and ((lower ((trim(a.tipoLogradouro.descricao) || \' \' || trim(a.nome))) like :filtro) or ");
        hql.append(" (lower ((trim(a.tipoLogradouro.sigla) || \' \' || trim(a.nome))) like :filtro) or");
        hql.append(" (lower (trim(a.codigo) ) like :filtro)) ");
        Query q = getEntityManager().createQuery(hql.toString());
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Logradouro> listaLogradouros(String s) {

        StringBuilder hql = new StringBuilder("select a from Logradouro a where ");
        hql.append(" (lower ((trim(a.tipoLogradouro.descricao) || \' \' || trim(a.nome))) like :filtro) or ");
        hql.append(" (lower ((trim(a.tipoLogradouro.sigla) || \' \' || trim(a.nome))) like :filtro) or");
        hql.append(" (lower (trim(a.codigo) ) like :filtro)");
        Query q = getEntityManager().createQuery(hql.toString());
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Logradouro> completaLogradourosPorBairros(String parte, List<Bairro> bairros) {
        List<Long> idsBairros = Lists.newArrayList();
        for (Bairro bairro : bairros) {
            idsBairros.add(bairro.getId());
        }
        if (idsBairros.isEmpty()) {
            return Lists.newArrayList();
        }

        StringBuilder sql = new StringBuilder("select distinct log.* from logradouro log");
        sql.append(" inner join tipoLogradouro tipoLog on tipoLog.id = log.tipologradouro_id");
        sql.append(" inner join logradouroBairro logBairro on logBairro.logradouro_id = log.id");
        sql.append(" inner join bairro bairro on bairro.id = logBairro.bairro_id");
        sql.append(" where  (upper(trim(tipolog.descricao || \' \' || log.nome)) like :parte");
        sql.append(" or upper(trim(tipoLog.descricao)) like :parte");
        sql.append(" or upper(trim(log.nome)) like :parte");
        sql.append(" or log.codigo like :parte)");
        sql.append(" and log.situacao = 'ATIVO' ");
        sql.append(" and bairro.id in (:bairros)");

        Query q = em.createNativeQuery(sql.toString(), Logradouro.class);
        q.setParameter("parte", "%" + parte.trim().toUpperCase() + "%");
        q.setParameter("bairros", idsBairros);
        q.setMaxResults(10);

        return q.getResultList();
    }

    public List<Logradouro> completaLogradourosPorBairro(String parte, Bairro bairro) {
        StringBuilder sql = new StringBuilder("select log.* from logradouro log");
        sql.append(" inner join tipoLogradouro tipoLog on tipoLog.id = log.tipologradouro_id");
        sql.append(" inner join logradouroBairro logBairro on logBairro.logradouro_id = log.id");
        sql.append(" inner join bairro bairro on bairro.id = logBairro.bairro_id");
        sql.append(" where  (upper(trim(tipolog.descricao || \' \' || log.nome)) like :parte");
        sql.append(" or upper(trim(tipoLog.descricao)) like :parte");
        sql.append(" or upper(trim(log.nome)) like :parte");
        sql.append(" or log.codigo like :parte)");
        sql.append(" and log.situacao = 'ATIVO' and bairro.id = :bairro");

        Query q = em.createNativeQuery(sql.toString(), Logradouro.class);
        q.setParameter("parte", "%" + parte.trim().toUpperCase() + "%");
        q.setParameter("bairro", bairro.getId());
        q.setMaxResults(10);

        return q.getResultList();
    }

    public boolean logradouroJaExiste(Logradouro l) {
        String hql = "from Logradouro l where lower(l.nome) = :nome";
        if (l.getId() != null) {
            hql += " and l != :logradouro";
        }
        Query q = em.createQuery(hql);
        q.setParameter("nome", l.getNome().toLowerCase().trim());
        if (l.getId() != null) {
            q.setParameter("logradouro", l);
        }
        return !q.getResultList().isEmpty();
    }

    public List<Logradouro> listarPorBairro(Bairro bairro, String parte) {
        if (bairro == null || bairro.getId() == null) {
            return new ArrayList<>();
        } else {
            String hql = "select distinct l from LogradouroBairro lb " +
                    " join lb.logradouro l " +
                    " join lb.bairro b " +
                    " join l.tipoLogradouro tl " +
                    " where (lower(l.nome) like :parte or lower(tl.descricao) like :parte) and b = :bairro " +
                    " and l.situacao = 'ATIVO'";
            Query q = getEntityManager().createQuery(hql);
            q.setParameter("parte", "%" + parte.toLowerCase() + "%");
            q.setParameter("bairro", bairro);
            return q.getResultList();
        }
    }

    public List<Logradouro> consultaLogradouros(String bairro, String parte) {
        String hql = "select distinct logradouro from LogradouroBairro lb " +
                " join lb.logradouro logradouro" +
                " join lb.bairro bairro" +
                " where lower(bairro.descricao) like :bairro " +
                "   and lower(logradouro.nome) like :parte " +
                "   and coalesce(logradouro.situacao,'" + SituacaoLogradouro.ATIVO.name() + "') = :situacao ";
        Query q = em.createQuery(hql);

//        Query q = em.createQuery("select logradouro FROM Logradouro logradouro, LogradouroBairro lb "
//                + " where lb.logradouro = logradouro and lower(lb.bairro.descricao) "
//                + " like :bairro and lower(logradouro.nome) like :parte"
//                + " and coalesce(situacao,'"+SituacaoLogradouro.ATIVO.name()+"') = :situacao ");
        q.setParameter("bairro", "%" + bairro.toLowerCase() + "%");
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("situacao", SituacaoLogradouro.ATIVO);
        q.setMaxResults(50);
        return q.getResultList();
    }

    public Long proximoCodigo() {
        StringBuilder sql = new StringBuilder("select max(codigo) from logradouro");
        Query q = em.createNativeQuery(sql.toString());

        if (q.getResultList().isEmpty() || q.getResultList().get(0) == null) {
            return 1l;
        } else {
            return ((BigDecimal) q.getResultList().get(0)).longValue() + 1l;
        }
    }

    public boolean existeLogradouroPorCodigo(Long id, Long codigo) {
        StringBuilder sql = new StringBuilder("select * from logradouro where codigo = :codigo");
        if (id != null) {
            sql.append(" and id <> :id");
        }
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("codigo", codigo);
        if (id != null) {
            q.setParameter("id", id);
        }

        if (q.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public Logradouro logradouroPorCodigo(Long codigo) {
        StringBuilder sql = new StringBuilder("select * from logradouro where codigo = :codigo");
        Query q = em.createNativeQuery(sql.toString(), Logradouro.class);
        q.setParameter("codigo", codigo);
        if (!q.getResultList().isEmpty()) {
            return (Logradouro) q.getResultList().get(0);
        } else {
            return null;
        }
    }

    public Logradouro logradouroPorNomeTipo(String nome, String tipo) {
        StringBuilder sql = new StringBuilder("select l.* from Logradouro l inner join TipoLogradouro tp on tp.id = l.tipoLogradouro_id where upper(replace(l.nome,' ', '')) = :nome and tp.descricao = :tipo");
        Query q = em.createNativeQuery(sql.toString(), Logradouro.class);
        q.setParameter("nome", nome.trim().replaceAll(" ", "").toUpperCase());
        q.setParameter("tipo", tipo);
        if (!q.getResultList().isEmpty()) {
            return (Logradouro) q.getResultList().get(0);
        }
        return null;
    }

    public boolean existeLogradouroPorNomeSituacao(Long id, String nome, SituacaoLogradouro situacaoLogradouro, Setor setor) {
        StringBuilder sql = new StringBuilder("select * from logradouro where replace(upper(nome), ' ', '') = :nome");
        sql.append(" and situacao = :situacaoLogradouro");
        if (id != null) {
            sql.append(" and id <> :id");
        }
        if (setor != null) {
            sql.append(" and setor_id = :idSetor");
        }
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("nome", nome.replaceAll(" ", "").toUpperCase());
        q.setParameter("situacaoLogradouro", situacaoLogradouro.name());
        if (id != null) {
            q.setParameter("id", id);
        }
        if (setor != null) {
            q.setParameter("idSetor", setor.getId());
        }

        if (q.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public LogradouroBairro recuperaLogradourBairro(Bairro bairro, Logradouro logradouro) {
        StringBuilder hql = new StringBuilder("select lb from LogradouroBairro lb ")
                .append(" where lb.logradouro = :logradouro ")
                .append(" and lb.bairro = :bairro");

        Query q = em.createQuery(hql.toString());
        q.setParameter("logradouro", logradouro);
        q.setParameter("bairro", bairro);
        if (!q.getResultList().isEmpty()) {
            return (LogradouroBairro) q.getResultList().get(0);
        }
        return null;
    }

    public List<Object[]> buscarLogradouros() {
        String sql = " select log.codigo as codigoLogradouro, " +
            "       tipo.descricao as tipo, " +
            "       log.nome as logradouro,  " +
            "       bairro.codigo as codigoBairro " +
            "  from logradouro log " +
            " inner join tipoLogradouro tipo on log.tipologradouro_id = tipo.id " +
            " inner join LogradouroBairro logBairro on logBairro.LOGRADOURO_ID = log.id " +
            " inner join bairro bairro on logBairro.BAIRRO_ID = bairro.id " +
            " where log.situacao = :situacao " +
            "   and bairro.ATIVO = :true ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("situacao", SituacaoLogradouro.ATIVO.name());
        q.setParameter("true", Boolean.TRUE);
        return q.getResultList();
    }
}
