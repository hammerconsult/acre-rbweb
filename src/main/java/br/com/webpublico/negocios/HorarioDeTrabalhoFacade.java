/*
 * Codigo gerado automaticamente em Thu Aug 04 11:21:10 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.HorarioDeTrabalho;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.service.S1050Service;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.Util;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class HorarioDeTrabalhoFacade extends AbstractFacade<HorarioDeTrabalho> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    private S1050Service s1050Service;


    @PostConstruct
    public void init() {
        s1050Service = (S1050Service) Util.getSpringBeanPeloNome("s1050Service");
    }
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HorarioDeTrabalhoFacade() {
        super(HorarioDeTrabalho.class);
    }

    @Override
    public void salvarNovo(HorarioDeTrabalho entity) {
        entity.setCodigo(lista().size() + 1);
        super.salvarNovo(entity);
    }

    public void enviarS1050ESocial(ConfiguracaoEmpregadorESocial empregador, HorarioDeTrabalho horarioDeTrabalho) throws ValidacaoException {
        s1050Service.enviarS1050(empregador, horarioDeTrabalho);
    }
}
