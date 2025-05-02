/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.SecretariaFiscalizacao;
import br.com.webpublico.entidades.UnidadeOrganizacional;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Renato
 */
@Stateless
public class SecretariaFiscalizacaoFacade extends AbstractFacade<SecretariaFiscalizacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public SecretariaFiscalizacaoFacade() {
        super(SecretariaFiscalizacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public void setDividaFacade(DividaFacade dividaFacade) {
        this.dividaFacade = dividaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public PessoaFisicaFacade getPessoaFacade() {
        return pessoaFisicaFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public List<SecretariaFiscalizacao> getLista() {
        Query consulta = em.createQuery("select s from SecretariaFiscalizacao s");
        return consulta.getResultList();
    }

    public List<UnidadeOrganizacional> completaSecretaria(String parte) {
        Query consulta = em.createQuery("select distinct ho.superior from HierarquiaOrganizacional ho "
            + " where lower(ho.superior.descricao) like :parte and ho.tipoHierarquiaOrganizacional = 'ADMINISTRATIVA'");
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        ////
        //        consulta.setParameter("superior", superior);


        //    Query consulta = em.createQuery(" from UnidadeOrganizacional uo where lower(uo.descricao) like :parte");
        //  consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return consulta.getResultList();
    }

    public List<UnidadeOrganizacional> completaDepartamento(String parte, UnidadeOrganizacional superior) {
        Query consulta = em.createQuery("select ho.subordinada from HierarquiaOrganizacional ho "
            + " where lower(ho.subordinada.descricao) like :parte and ho.tipoHierarquiaOrganizacional = 'ADMINISTRATIVA'"
            + " and ho.superior = :superior");
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        consulta.setParameter("superior", superior);
        return consulta.getResultList();
    }

    public List<Divida> completaDivida(String parte) {
        return dividaFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return pessoaFisicaFacade.listaPessoaPorMatriculaFP(parte.trim());
    }

    public Long recuperaProximaCodigo() {
        Query consulta = em.createQuery("select max(s.codigo) from SecretariaFiscalizacao s");
        try {
            return (Long) consulta.getSingleResult() + 1l;
        } catch (Exception e) {
            return 1l;
        }
    }

    public List<SecretariaFiscalizacao> completarSecretariaFiscalizacao(String parte) {
        Query consulta = em.createQuery("select sec from SecretariaFiscalizacao sec where to_char(sec.codigo) like :parte or sec.unidadeOrganizacional.descricao like :parte");
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return consulta.getResultList();
    }

    public List<SecretariaFiscalizacao> secretariasDaUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        Query q = em.createQuery("select sec from SecretariaFiscalizacao sec where sec.unidadeOrganizacional = :unidade");
        q.setParameter("unidade", unidadeOrganizacional);
        return q.getResultList();
    }
}
