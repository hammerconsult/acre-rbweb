package br.com.webpublico.negocios;

import br.com.webpublico.entidades.BemMovelPropriedadeTerceiro;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 08/06/15
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class BemMovelPropriedadeTerceiroFacade extends AbstractFacade<BemMovelPropriedadeTerceiro> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public BemMovelPropriedadeTerceiroFacade() {
        super(BemMovelPropriedadeTerceiro.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public BemMovelPropriedadeTerceiro recuperar(Object id) {
        BemMovelPropriedadeTerceiro bem = super.recuperar(id);
        if (bem.getDetentorArquivoComposicao() != null) {
            bem.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        try {
            bem.getDetentorOrigemRecurso().getListaDeOriemRecursoBem().size();
        } catch (Exception ex) {
        }
        return bem;
    }
}
