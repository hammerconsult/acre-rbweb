package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AnulacaoEmenda;
import br.com.webpublico.entidades.ProvisaoPPADespesa;
import br.com.webpublico.entidades.ProvisaoPPAFonte;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 16/06/15
 * Time: 10:50
 * To change this template use File | Settings | File Templates.
 */

@Stateless
public class AnulacaoEmendaFacade extends AbstractFacade<AnulacaoEmenda> {

    @EJB
    private ProvisaoPPAFacade provisaoPPAFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AnulacaoEmendaFacade() {
        super(AnulacaoEmenda.class);
    }

    public EntityManager getEm() {
        return em;
    }

    public void movimentarProvisaoPPAAnulacao(AnulacaoEmenda anulacaoEmenda) {
        ProvisaoPPADespesa provisaoPPADespesa = buscarDespesaOrcamento(anulacaoEmenda);
        if (provisaoPPADespesa != null) {
            for (ProvisaoPPAFonte provisaoPPAFonte : provisaoPPADespesa.getProvisaoPPAFontes()) {
                if (provisaoPPAFonte.getDestinacaoDeRecursos().equals(anulacaoEmenda.getDestinacaoDeRecursos())) {
                    provisaoPPAFonte.setValor(provisaoPPAFonte.getValor().subtract(anulacaoEmenda.getValor()));
                    em.merge(provisaoPPAFonte);
                }
            }
            provisaoPPADespesa.setValor(provisaoPPADespesa.getValor().subtract(anulacaoEmenda.getValor()));
            em.merge(provisaoPPADespesa);
        }
    }

    private ProvisaoPPADespesa buscarDespesaOrcamento(AnulacaoEmenda anulacaoEmenda) {
        List<ProvisaoPPADespesa> provisaoPPADespesas = provisaoPPAFacade.getProvisaoPPADespesaFacade().listaProvisaoDispesaPPARecuperandoFontes(anulacaoEmenda.getSubAcaoPPA());
        for (ProvisaoPPADespesa ppaDespesa : provisaoPPADespesas) {
            if (anulacaoEmenda.getConta().getId().equals(ppaDespesa.getContaDeDespesa().getId())) {
                return ppaDespesa;
            }
        }
        return null;
    }
}
