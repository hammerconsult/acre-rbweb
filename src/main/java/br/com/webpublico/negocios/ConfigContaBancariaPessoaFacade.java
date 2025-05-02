package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created with IntelliJ IDEA. User: Edi Date: 13/01/15 Time: 09:49 To
 * change this template use File | Settings | File Templates.
 */
@Stateless
public class ConfigContaBancariaPessoaFacade extends AbstractFacade<ConfigContaBancariaPessoa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContaCorrenteBancariaFacade contaCorrenteBancariaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;

    public ConfigContaBancariaPessoaFacade() {
        super(ConfigContaBancariaPessoa.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfigContaBancariaPessoa recuperarConfiguracaoContaBancariaPessoa(Pessoa pessoa, ClasseCredor classeCredor) {

        Preconditions.checkNotNull(pessoa, " A pessoa não foi informada.");
        Preconditions.checkNotNull(classeCredor, " A classe da pessoa não foi informada.");

        String sql = " SELECT C.* FROM CONFIGCONTABANCARIAPESSOA C "
                + " WHERE C.PESSOA_ID = :idPessoa "
                + " AND C.CLASSECREDOR_ID = :idClasse ";
        Query q = em.createNativeQuery(sql, ConfigContaBancariaPessoa.class);
        q.setParameter("idPessoa", pessoa.getId());
        q.setParameter("idClasse", classeCredor.getId());
        try {
            ConfigContaBancariaPessoa configuracao = (ConfigContaBancariaPessoa) q.getSingleResult();
            if (configuracao != null) {
                return configuracao;
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    public ContaCorrenteBancariaFacade getContaCorrenteBancariaFacade() {
        return contaCorrenteBancariaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }
}
