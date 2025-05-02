/*
 * Codigo gerado automaticamente em Tue Feb 22 16:08:31 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoConta;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ContaCorrenteBancariaFacade extends AbstractFacade<ContaCorrenteBancaria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContaCorrenteBancariaFacade contaCorrenteBancariaFacade;
    @EJB
    private ContaCorrenteBancPessoaFacade contaCorrenteBancPessoaFacade;
    @EJB
    private AgenciaFacade agenciaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private BancoFacade bancoFacade;

    public ContaCorrenteBancariaFacade() {
        super(ContaCorrenteBancaria.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ContaCorrenteBancaria recuperar(Object id) {
        ContaCorrenteBancaria cc = em.find(ContaCorrenteBancaria.class, id);
        cc.getPessoas().size();
        cc.getContaCorrenteBancPessoas().size();
        return cc;
    }

    public ContaCorrenteBancaria salvar(ContaCorrenteBancaria entity, List<ContaCorrenteBancPessoa> listaExcluidos) {
//        List<Pessoa> lista2 = new ArrayList<Pessoa>();
//        for (Pessoa p : entity.getPessoas()) {
//            lista2.add(em.merge(p));
//        }

        for (ContaCorrenteBancPessoa c : listaExcluidos) {
            c = em.find(ContaCorrenteBancPessoa.class, c.getId());
            em.remove(c);
            if (!existeVinculoDeContaCorrente(c.getContaCorrenteBancaria())) {
                contaCorrenteBancariaFacade.remover(c.getContaCorrenteBancaria());
            }
        }

        return getEntityManager().merge(entity);
//        entity.getPessoas().clear();
//        entity.getPessoas().addAll(lista2);
//        if (entity.getId() != null) {
//            entity = em.merge(entity);
//        }
        //em.persist(entity);
    }

    public ContaCorrenteBancaria salvarContaCorrente(ContaCorrenteBancaria entity) {
        return getEntityManager().merge(entity);
    }

    public List<ContaCorrenteBancaria> listaContasPorPessoaFisica(PessoaFisica pessoa) {
        /*
         * String sql=" select cb.* from contacorrentebancaria cb,
         * contacor_pessoa cp, pessoafisica pf "; sql+=" where cb.id =
         * cp.contascorrentesbancarias_id and cp.pessoas_id= pf.id and pf.id =
         * :pessoa";
         *
         */
        String hql = "select ccb from ContaCorrenteBancaria ccb "
            + "inner join ccb.pessoas p "
            + "where p = :pessoa";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("pessoa", pessoa);
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<ContaCorrenteBancaria> listaContasFiltrandoAtributosAgencia(String s) {
        String sql = " SELECT CCB.* FROM CONTACORRENTEBANCARIA CCB "
            + " INNER JOIN AGENCIA AG ON AG.ID = CCB.AGENCIA_ID "
            + " INNER JOIN BANCO B ON B.ID = AG.BANCO_ID "
            + " INNER JOIN CONTACORRENTEBANCPESSOA C ON C.CONTACORRENTEBANCARIA_ID = CCB.ID "
            + " LEFT JOIN PESSOAFISICA PF ON PF.ID = C.PESSOA_ID "
            + " LEFT JOIN PESSOAJURIDICA PJ ON PJ.ID = C.PESSOA_ID "
            + " where (ccb.numeroConta like :filtro or ccb.digitoVerificador like :filtro "
            + " or lower(ag.nomeAgencia) like :filtro or ag.numeroAgencia like :filtro or ag.digitoVerificador like :filtro "
            + " or lower(b.descricao) like :filtro "
            + " or lower(pf.nome) like :filtro "
            + " or (replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like :cpfCnpj) "
            + " or lower(pj.nomeFantasia) like :filtro"
            + " or (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :cpfCnpj))"
            + " and ccb.situacao = 'ATIVO'"
            + " and ag.situacao = 'ATIVO'"
            + " and b.situacao = 'ATIVO'";

        Query q = em.createNativeQuery(sql, ContaCorrenteBancaria.class);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("cpfCnpj", "%" + s.toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%");
        q.setParameter("cpfCnpj", "%" + s.toLowerCase().replace(".", "").replace("-", "") + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<ContaCorrenteBancaria> listaContasPorPessoaJuridica(Pessoa pessoa) {
        String hql = " select ccb.contaCorrenteBancaria from ContaCorrenteBancPessoa ccb  where ccb.pessoa = :parametroPessoa ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroPessoa", pessoa);
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<ContaCorrenteBancaria> listaContasPorPessoa(Pessoa pessoa) {
        String sql = "SELECT * FROM CONTACORRENTEBANCARIA CCB " +
            " INNER JOIN CONTACORRENTEBANCPESSOA CCP ON CCP.CONTACORRENTEBANCARIA_ID = CCB.ID " +
            " INNER JOIN PESSOA P ON P.ID = CCP.PESSOA_ID " +
            " WHERE P.ID = :parametroPessoa " +
            " AND CCB.SITUACAO = 'ATIVO' ";

        Query q = em.createNativeQuery(sql, ContaCorrenteBancaria.class);
        q.setParameter("parametroPessoa", pessoa);
        q.setMaxResults(10);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public List<ContaCorrenteBancaria> listaContasPorPessoaFiltrandoPorCodigo(Pessoa pessoa, String parte) {
        String sql = "SELECT * FROM CONTACORRENTEBANCARIA CCB " +
            " INNER JOIN AGENCIA AG ON AG.ID = CCB.AGENCIA_ID " +
            " INNER JOIN BANCO B ON B.ID = AG.BANCO_ID " +
            " INNER JOIN CONTACORRENTEBANCPESSOA CCP ON CCP.CONTACORRENTEBANCARIA_ID = CCB.ID " +
            " INNER JOIN PESSOA P ON P.ID = CCP.PESSOA_ID " +
            " WHERE P.ID = :parametroPessoa " +
            "  and (ccb.numeroConta like :filtro or ccb.digitoVerificador like :filtro " +
            "     or lower(ag.nomeAgencia) like :filtro or ag.numeroAgencia like :filtro or ag.digitoVerificador like :filtro " +
            "     or lower(b.descricao) like :filtro )" +
            " AND CCB.SITUACAO = 'ATIVO' ";

        Query q = em.createNativeQuery(sql, ContaCorrenteBancaria.class);
        q.setParameter("parametroPessoa", pessoa);
        q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public ContaCorrenteBancaria contaCorrenteExistente(ContaCorrenteBancaria contaCorrenteBancaria) {
        try {
            String hql = " select ccb from ContaCorrenteBancaria ccb "
                + " where ccb.agencia = :agencia "
                + " and ltrim(ccb.numeroConta, 0) = ltrim(:numeroContaCorrente, 0) ";
            Query q = getEntityManager().createQuery(hql);
            q.setParameter("agencia", contaCorrenteBancaria.getAgencia());
            q.setParameter("numeroContaCorrente", contaCorrenteBancaria.getNumeroConta());

            ContaCorrenteBancaria ccb = (ContaCorrenteBancaria) q.getResultList().get(0);
            if (ccb == null) {
                return null;
            }
            return ccb;
        } catch (IndexOutOfBoundsException | NoResultException idx) {
            return null;
        }
    }

    public boolean jaPossuiContaCorrente(String numeroConta, String numeroAgencia, Long pessoaId) {

        String hql = " select contac.* " +
            "from CONTACORRENTEBANCPESSOA cpessoa " +
            "         inner join CONTACORRENTEBANCARIA contac on cpessoa.CONTACORRENTEBANCARIA_ID = contac.ID " +
            "         inner join agencia agencia on contac.AGENCIA_ID = agencia.ID " +
            "         inner join banco bb on agencia.BANCO_ID = bb.ID " +
            "where ltrim(contac.NUMEROCONTA,0) = ltrim(:numeroContaCorrente,0) " +
            "      and ltrim(agencia.NUMEROAGENCIA,0) = ltrim(:numeroAgencia, 0) " +
            "      and cpessoa.PESSOA_ID = :pessoa ";
        Query q = getEntityManager().createNativeQuery(hql, ContaCorrenteBancaria.class);
        q.setParameter("numeroAgencia", numeroAgencia);
        q.setParameter("numeroContaCorrente", numeroConta);
        q.setParameter("pessoa", pessoaId);
        return !q.getResultList().isEmpty();

    }

    public boolean existeVinculoDeContaCorrente(ContaCorrenteBancaria contaCorrenteBancaria) {
        String hql = "select ccbp from ContaCorrenteBancPessoa ccbp "
            + " where ccbp.contaCorrenteBancaria = :contaCorrenteBancaria";
        Query q = em.createQuery(hql);
        q.setParameter("contaCorrenteBancaria", contaCorrenteBancaria);
        List<ContaCorrenteBancPessoa> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return false;
        }
        return true;
    }


    public List<ContaCorrenteBancaria> listaContaCorrentePorSituacao(String parte, SituacaoConta situacaoConta) {
        String consulta = " SELECT ccb FROM ContaCorrenteBancaria ccb "
            + " INNER JOIN ccb.agencia ag "
            + " INNER JOIN ag.banco b"
            + " WHERE (ccb.numeroConta LIKE :PARTE "
            + " OR ag.nomeAgencia LIKE :PARTE "
            + " OR ag.numeroAgencia LIKE :PARTE "
            + " OR b.descricao LIKE :PARTE "
            + " OR b.numeroBanco LIKE :PARTE)"
            + " AND ccb.situacao = :SITUACAO";
        Query q = em.createQuery(consulta, ContaCorrenteBancaria.class);
        q.setParameter("PARTE", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("SITUACAO", situacaoConta);
        q.setMaxResults(10);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public ContaCorrenteBancariaFacade getContaCorrenteBancariaFacade() {
        return contaCorrenteBancariaFacade;
    }

    public ContaCorrenteBancPessoaFacade getContaCorrenteBancPessoaFacade() {
        return contaCorrenteBancPessoaFacade;
    }

    public AgenciaFacade getAgenciaFacade() {
        return agenciaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public List<ContaCorrenteBancaria> listaFiltrandoPorAgencia(String parte, Agencia ag) {
        String hql = "select ccb from ContaCorrenteBancaria ccb "
            + " inner join ccb.agencia agencia "
            + "      where agencia = :agencia and (ccb.numeroConta like :filtro  or ccb.digitoVerificador like :filtro)";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("agencia", ag);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public Boolean verificaViculosContaCorrenteBancaria(ContaCorrenteBancPessoa conta) {
        String sql = " SELECT CCP.* FROM CONTACORRENTEBANCPESSOA CCP " +
            " INNER JOIN CONTACORRENTEBANCARIA CC ON CC.ID = CCP.CONTACORRENTEBANCARIA_ID " +
            " WHERE CC.ID = :param";
        Query q = em.createNativeQuery(sql, ContaCorrenteBancPessoa.class);
        q.setParameter("param", conta.getId());
        if (q.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public BancoFacade getBancoFacade() {
        return bancoFacade;
    }
}
