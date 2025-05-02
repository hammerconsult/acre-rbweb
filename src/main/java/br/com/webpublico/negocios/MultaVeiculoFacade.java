/*
 * Codigo gerado automaticamente em Mon Oct 17 17:36:16 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MultaVeiculo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class MultaVeiculoFacade extends AbstractFacade<MultaVeiculo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MultaVeiculoFacade() {
        super(MultaVeiculo.class);
    }

    @Override
    public MultaVeiculo recuperar(Object id) {
        MultaVeiculo retorno = super.recuperar(id);
        if (retorno.getDetentorArquivoComposicao() != null &&
                retorno.getDetentorArquivoComposicao().getArquivosComposicao() != null) {
            retorno.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        retorno.getJulgamentosMultaVeiculo().size();
        return retorno;
    }
}
