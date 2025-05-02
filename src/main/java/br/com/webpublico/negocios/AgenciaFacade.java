package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Agencia;
import br.com.webpublico.entidades.Banco;
import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.enums.Situacao;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class AgenciaFacade extends AbstractFacade<Agencia> {


    @EJB
    private ConsultaCepFacade consultaCepFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AgenciaFacade() {
        super(Agencia.class);
    }

    public List<Agencia> listaFiltrandoAtributosAgenciaBanco(String s) {
        Query q = em.createQuery(" select agencia from Agencia agencia "
            + " inner join agencia.banco banco "
            + " where (lower(banco.descricao) like :filtro"
            + " or lower(agencia.nomeAgencia) like :filtro "
            + " or lower(agencia.numeroAgencia) like :filtro"
            + " or lower(agencia.digitoVerificador) like :filtro) "
            + " and agencia.situacao = :situacao "
            + " and banco.situacao = :situacao  ");
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.trim().toLowerCase() + "%");
        q.setParameter("situacao", Situacao.ATIVO);
        return q.getResultList();
    }

    public List<Agencia> listaFiltrandoPorBanco(String parte, Banco b) {
        String sql = "SELECT AG.* "
            + " FROM AGENCIA AG"
            + " INNER JOIN BANCO BC ON AG.BANCO_ID = BC.ID AND BC.ID = :banco"
            + " WHERE ((LOWER(AG.NUMEROAGENCIA) LIKE :parte) OR (LOWER (AG.NOMEAGENCIA) LIKE :parte))"
            + " and ag.situacao = 'ATIVO' and bc.situacao = 'ATIVO' ";
        Query q = em.createNativeQuery(sql, Agencia.class);
        q.setParameter("banco", b.getId());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public Agencia getAgenciaPorEntidade(Entidade entidade) {
        //System.out.println("Id da entidade..: " + entidade.getId());
        String hql = "select agencia from ContaBancariaEntidade conta "
                + "inner join conta.agencia agencia "
                + "where conta.entidade = :entidade";
        Query q = em.createQuery(hql);
        q.setParameter("entidade", entidade);
        List<Agencia> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return new Agencia();
        }
        return lista.get(0);
    }

    public Agencia buscarAgenciaPeloNumeroDaAgenciaAndBanco(String numeroAgencia, String numeroBanco) {
        String sql = "select a.* " +
            "from agencia a " +
            "         inner join banco b on a.banco_id = b.id " +
            "where a.numeroagencia = :numeroagencia and numerobanco = :numerobanco";
        Query q = em.createNativeQuery(sql, Agencia.class);
        q.setParameter("numeroagencia", numeroAgencia);
        q.setParameter("numerobanco", numeroBanco);
        List<Agencia> resultado = q.getResultList();
        if (resultado != null && !resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }

    public Agencia buscarAgenciaPeloNumeroDaAgenciaAndIspbBanco(String numeroAgencia, String ispbBanco) {
        String sql = "select a.* " +
            "from agencia a " +
            "         inner join banco b on a.banco_id = b.id " +
            "where trim(a.numeroagencia) = :numeroagencia and trim(b.ispb) = :ispbBanco";
        Query q = em.createNativeQuery(sql, Agencia.class);
        q.setParameter("numeroagencia", numeroAgencia.trim());
        q.setParameter("ispbBanco", ispbBanco.trim());
        List<Agencia> resultado = q.getResultList();
        if (resultado != null && !resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }

    public List<Agencia> buscarAgenciasPorIdBanco(Long idBanco) {
        String sql = "SELECT AG.* " +
            " FROM AGENCIA AG " +
            "   INNER JOIN BANCO BC ON AG.BANCO_ID = BC.ID " +
            " WHERE BC.ID = :idBanco " +
            "   and ag.situacao = :ativo " +
            "   and bc.situacao = :ativo " +
            " ORDER BY to_number(regexp_replace(AG.numeroAgencia, '[^0-9]', '')) ";
        Query q = em.createNativeQuery(sql, Agencia.class);
        q.setParameter("idBanco", idBanco);
        q.setParameter("ativo", Situacao.ATIVO.name());
        return q.getResultList();
    }

    public ConsultaCepFacade getConsultaCepFacade() {
        return consultaCepFacade;
    }
}
