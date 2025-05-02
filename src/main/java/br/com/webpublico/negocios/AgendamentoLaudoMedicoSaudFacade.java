package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AgendamentoLaudoMedicoSaud;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class AgendamentoLaudoMedicoSaudFacade extends AbstractFacade<AgendamentoLaudoMedicoSaud> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UsuarioSaudFacade usuarioSaudFacade;

    public AgendamentoLaudoMedicoSaudFacade() {
        super(AgendamentoLaudoMedicoSaud.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioSaudFacade getUsuarioSaudFacade() {
        return usuarioSaudFacade;
    }

    @Override
    public void preSave(AgendamentoLaudoMedicoSaud entidade) {
        entidade.realizarValidacoes();
    }

    @Override
    public AgendamentoLaudoMedicoSaud recuperar(Object id) {
        AgendamentoLaudoMedicoSaud agendamento = super.recuperar(id);
        if (agendamento.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(agendamento.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return agendamento;
    }
}
