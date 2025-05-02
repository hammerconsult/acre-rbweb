/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModalidadeConta;
import br.com.webpublico.interfaces.FebrabanFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author peixe
 */
@Stateless
public class FebrabanFacadeImpl implements FebrabanFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;

    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public FebrabanHeaderArquivo recuperaFebrabanHeaderArquivo(UnidadeOrganizacional unidade, ContaBancariaEntidade conta) {
        StringBuilder sb = new StringBuilder();
        sb.append("select new br.com.webpublico.entidades.FebrabanHeaderArquivo(agencia, banco, unidade, contaBancariaEntidade, pessoa, entidade)");
        sb.append("  from UnidadeOrganizacional unidade, ContaBancariaEntidade contaBancariaEntidade");
        sb.append(" inner join unidade.entidade entidade ");
        sb.append(" inner join entidade.pessoaJuridica pessoa");
        sb.append(" inner join contaBancariaEntidade.entidade entidadeConta");
        sb.append(" inner join contaBancariaEntidade.agencia agencia");
        sb.append(" inner join agencia.banco banco");
        sb.append(" where entidadeConta = entidade");
        sb.append("   and entidade = :unidade ");
        sb.append("   and contaBancariaEntidade = :conta");

        Query q = em.createQuery(sb.toString());
        q.setParameter("unidade", unidade.getEntidade());
        q.setParameter("conta", conta);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (FebrabanHeaderArquivo) q.getSingleResult();
    }

    @Override
    public List<FebrabanDetalheSegmentoA> recuperaDetalheSegmentoAContaCorrente(HierarquiaOrganizacional hierarquia, FolhaDePagamento folhaPagamento, ModalidadeConta tipoConta, Banco banco, ContratoFP c) {
        StringBuilder sb = new StringBuilder();
        sb.append("select new br.com.webpublico.entidades.FebrabanDetalheSegmentoA(contrato, ficha)");
        sb.append(" from ContratoFP contrato, FichaFinanceiraFP ficha, VwHierarquiaAdministrativa vw ");
        sb.append(" where ficha.vinculoFP = contrato ");
        sb.append(" and contrato.contaCorrente.modalidadeConta = :tipo");
        sb.append(" and contrato.contaCorrente.agencia.banco = :banco");
        sb.append(" and ficha.folhaDePagamento = :folha");
        sb.append(" ");
        sb.append(" and contrato.matriculaFP.unidadeMatriculado.id = vw.subordinadaId ");
        sb.append(" and ficha.creditoSalarioPago is false and vw.codigo like :unidade ");
        sb.append(c != null ? " and contrato = :c" : "");

        Query q = em.createQuery(sb.toString());
        q.setParameter("unidade", hierarquia.getCodigoSemZerosFinais() + "%");
        q.setParameter("folha", folhaPagamento);
        q.setParameter("tipo", tipoConta);
        q.setParameter("banco", banco);
        if (c != null){
            q.setParameter("c", c);
        }
        return q.getResultList();
//        for (HierarquiaOrganizacional u : hierarquiaOrganizacionalFacade.filtrandoHierarquiaHorganizacionalTipo(hierarquia)) {
//            if (u.getSubordinada().getEntidade() != null) {
//                StringBuilder hql = new StringBuilder();
//                hql.append("select new br.com.webpublico.entidades.FebrabanDetalheSegmentoA(contrato, ficha)");
//                hql.append(" from ContratoFP contrato, FichaFinanceiraFP ficha inner join contrato.contaCorrente conta");
//                hql.append(" inner join conta.agencia agencia");
//                hql.append(" inner join contrato.matriculaFP matricula");
//                hql.append(" inner join matricula.pessoa pessoa");
//                hql.append(" inner join ficha.folhaDePagamento folha");
//                hql.append(" where ficha.vinculoFP = contrato ");
//                hql.append(" and conta.modalidadeConta = :tipo and agencia.banco = :banco");
//                hql.append(" and contrato.unidadeOrganizacional = :unidade and folha = :folha");
//                Query query = em.createQuery(sb.toString());
//                query.setParameter("unidade", u.getSubordinada());
//                query.setParameter("folha", folhaPagamento);
//                query.setParameter("tipo", tipoConta);
//                query.setParameter("banco", banco);
//                toReturn.addAll(query.getResultList());
//            }
//        }
//        return toReturn;
    }

    @Override
    public List<FebrabanDetalheSegmentoA> recuperaDetalheSegmentoOutrosBanco(HierarquiaOrganizacional hierarquia, FolhaDePagamento folhaPagamento, Banco banco, ContratoFP c) {
        //List<FebrabanDetalheSegmentoA> toReturn = new ArrayList<FebrabanDetalheSegmentoA>();
        StringBuilder sb = new StringBuilder();
        sb.append("select new br.com.webpublico.entidades.FebrabanDetalheSegmentoA(contrato, ficha)");
        sb.append(" from ContratoFP contrato, FichaFinanceiraFP ficha inner join contrato.contaCorrente conta, VwHierarquiaAdministrativa vw ");
        sb.append(" inner join conta.agencia agencia");
        sb.append(" inner join contrato.matriculaFP matricula");
        sb.append(" inner join matricula.pessoa pessoa");
        sb.append(" inner join ficha.folhaDePagamento folha");
        sb.append(" where ficha.vinculoFP = contrato ");
        sb.append(" and agencia.banco != :banco");
        sb.append(" and contrato.matriculaFP.unidadeMatriculado.id = vw.subordinadaId and folha = :folha ");
        sb.append(" and ficha.creditoSalarioPago is false and vw.codigo like :unidade ");
        sb.append(c != null ? " and contrato = :c" : "");

        Query q = em.createQuery(sb.toString());
        //q.setMaxResults(100);
        q.setParameter("unidade", hierarquia.getCodigoSemZerosFinais() + "%");
        q.setParameter("folha", folhaPagamento);
        q.setParameter("banco", banco);
        if (c != null){
            q.setParameter("c", c);
        }
        return q.getResultList();
//        toReturn.addAll(q.getResultList());
//        for (HierarquiaOrganizacional u : hierarquiaOrganizacionalFacade.filtrandoHierarquiaHorganizacionalTipo(hierarquia)) {
//            if (u.getSubordinada().getEntidade() != null) {
//                StringBuilder hql = new StringBuilder();
//                hql.append("select new br.com.webpublico.entidades.FebrabanDetalheSegmentoA(contrato, ficha)");
//                hql.append(" from ContratoFP contrato, FichaFinanceiraFP ficha inner join contrato.contaCorrente conta");
//                hql.append(" inner join conta.agencia agencia");
//                hql.append(" inner join contrato.matriculaFP matricula");
//                hql.append(" inner join matricula.pessoa pessoa");
//                hql.append(" inner join ficha.folhaDePagamento folha");
//                hql.append(" where ficha.vinculoFP = contrato ");
//                hql.append(" and agencia.banco != :banco");
//                hql.append(" and contrato.unidadeOrganizacional = :unidade and folha = :folha ");
//                hql.append(" and ficha.creditoSalarioPago is false ");
//                Query query = em.createQuery(sb.toString());
//                query.setParameter("unidade", u.getSubordinada());
//                query.setParameter("folha", folhaPagamento);
//                query.setParameter("banco", banco);
//                toReturn.addAll(query.getResultList());
//            }
//        }
//        return toReturn;
    }
}
