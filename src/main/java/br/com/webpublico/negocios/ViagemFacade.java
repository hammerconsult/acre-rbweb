/*
 * Codigo gerado automaticamente em Mon Nov 14 00:20:54 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Viagem;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;

@Stateless
public class ViagemFacade extends AbstractFacade<Viagem> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @EJB
    private VeiculoFacade veiculoFacade;

    public ViagemFacade() {
        super(Viagem.class);
    }

    @Override
    public Viagem recuperar(Object id) {
        Viagem viagem = super.recuperar(id);
        viagem.getItinerarioViagem().size();
        viagem.getAbastecimentos().size();
        viagem.getManutencoesVeiculo().size();
        return viagem;
    }

    public Viagem salvarViagem(Viagem entity) {
        entity = em.merge(entity);
        veiculoFacade.criarLancamentoKmPercorrido(entity.getVeiculo(),
            entity.getKmRetorno() != null ? new BigDecimal(entity.getKmRetorno()) : new BigDecimal(entity.getKmSaida()));
        return entity;
    }
}
