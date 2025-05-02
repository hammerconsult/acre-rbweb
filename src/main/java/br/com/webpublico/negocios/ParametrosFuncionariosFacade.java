package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ParametrosFuncionarios;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 15/06/15
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ParametrosFuncionariosFacade extends AbstractFacade<ParametrosFuncionarios> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParametrosFuncionariosFacade() {
        super(ParametrosFuncionarios.class);
    }

    @Override
    public ParametrosFuncionarios recuperar(Object id) {
        ParametrosFuncionarios parametrosFuncionarios = super.recuperar(id);
        parametrosFuncionarios.getDetentorArquivoComposicao().getArquivosComposicao().size();
        return parametrosFuncionarios;
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        return super.recuperar(id);
    }
}
