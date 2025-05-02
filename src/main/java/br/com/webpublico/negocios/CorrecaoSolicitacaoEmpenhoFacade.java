package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.entidades.SolicitacaoEmpenho;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * Created by mateus on 02/10/17.
 */
@Stateless
public class CorrecaoSolicitacaoEmpenhoFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SolicitacaoEmpenhoFacade solicitacaoEmpenhoFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public CorrecaoSolicitacaoEmpenhoFacade() {
    }

    public void salvarSolicitacaoEmpenho(Empenho empenho, SolicitacaoEmpenho solicitacaoEmpenho) {
        if (empenho.getPropostaConcessaoDiaria() != null) {
            empenhoFacade.removerReservaDotacaoParaIntegracaoDeDiaria(empenho);
        } else if (empenho.getContrato() != null) {
            empenhoFacade.removerSaldoOrcamentarioReservadoPorLicitacao(empenho);
        }
        empenho = em.find(Empenho.class, empenho.getId());
        solicitacaoEmpenho = em.find(SolicitacaoEmpenho.class, solicitacaoEmpenho.getId());
        empenho.setSolicitacaoEmpenho(solicitacaoEmpenho);
        empenho = em.merge(empenho);
        empenhoFacade.atribuirSolicitacaoEmpenhoNoEmpenho(empenho);
    }

    public SolicitacaoEmpenhoFacade getSolicitacaoEmpenhoFacade() {
        return solicitacaoEmpenhoFacade;
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
