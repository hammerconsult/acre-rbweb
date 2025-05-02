package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class DuplicacaoProjetoAtividadeFacade extends AbstractFacade<DuplicacaoProjetoAtividade> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    public DuplicacaoProjetoAtividadeFacade() {
        super(DuplicacaoProjetoAtividade.class);
    }

    @Override
    public void salvarNovo(DuplicacaoProjetoAtividade entity) {
        duplicarProjetoAtividade(entity);
        super.salvar(entity);
    }

    private void duplicarProjetoAtividade(DuplicacaoProjetoAtividade entity) {
        AcaoPPA novaAcaoPPA = (AcaoPPA) Util.clonarObjeto(entity.getProjetoAtividade());
        novaAcaoPPA.setId(null);
        novaAcaoPPA.setResponsavel(entity.getUnidadeOrganizacional());
        novaAcaoPPA.setDescricao(entity.getNovaDescricao());
        novaAcaoPPA.setDataRegistro(entity.getData());
        novaAcaoPPA.setSomenteLeitura(true);
        novaAcaoPPA.setSubAcaoPPAs(Lists.<SubAcaoPPA>newArrayList());
        novaAcaoPPA.setParticipanteAcaoPPA(Lists.<ParticipanteAcaoPPA>newArrayList());
        if (!projetoAtividadeFacade.hasCodigoIgualParaPrograma(novaAcaoPPA, sistemaFacade.getExercicioCorrente())) {
            ValidacaoException ve = new ValidacaoException();
            ve.adicionarMensagemDeOperacaoNaoPermitida("O código informado: " + novaAcaoPPA.getCodigo() + ", pertence a outro Projeto/Atividade cadastrado no sistema.");
            ve.lancarException();
        }
        for (ParticipanteAcaoPPA participanteAcaoPPA : projetoAtividadeFacade.recuperar(entity.getProjetoAtividade().getId()).getParticipanteAcaoPPA()) {
            ParticipanteAcaoPPA novoParticipanteAcaoPPA = (ParticipanteAcaoPPA) Util.clonarObjeto(participanteAcaoPPA);
            novoParticipanteAcaoPPA.setId(null);
            novoParticipanteAcaoPPA.setAcaoPPA(novaAcaoPPA);
            novaAcaoPPA.getParticipanteAcaoPPA().add(novoParticipanteAcaoPPA);
        }
        for (SubAcaoPPA subAcaoPPA : projetoAtividadeFacade.recuperar(entity.getProjetoAtividade().getId()).getSubAcaoPPAs()) {
            SubAcaoPPA novaSubAcaoPPA = (SubAcaoPPA) Util.clonarObjeto(subAcaoPPA);
            novaSubAcaoPPA.setId(null);
            novaSubAcaoPPA.setAcaoPPA(novaAcaoPPA);
            novaSubAcaoPPA.setProvisaoPPADespesas(Lists.<ProvisaoPPADespesa>newArrayList());
            novaSubAcaoPPA.setDescricao(entity.getNovaDescricao());
            novaAcaoPPA.getSubAcaoPPAs().add(novaSubAcaoPPA);
        }
        projetoAtividadeFacade.salvar(novaAcaoPPA);
        entity.getProjetoAtividade().setSomenteLeitura(true);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public ProjetoAtividadeFacade getProjetoAtividadeFacade() {
        return projetoAtividadeFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
