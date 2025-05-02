package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.administrativo.ParticipanteLicitacaoFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class AlteracaoFornecedorLicitacaoFacade extends AbstractFacade<AlteracaoFornecedorLicitacao> {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;

    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private PropostaFornecedorFacade propostaFornecedorFacade;
    @EJB
    private ParticipanteLicitacaoFacade participanteLicitacaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ItemPregaoFacade itemPregaoFacade;
    @EJB
    private PregaoFacade pregaoFacade;
    @EJB
    private DispensaDeLicitacaoFacade dispensaLicitacaoFacade;
    @EJB
    private FornecedorDispensaDeLicitacaoFacade fornecedorDispensaDeLicitacaoFacade;
    @EJB
    private ContratoFacade contratoFacade;

    public AlteracaoFornecedorLicitacaoFacade() {
        super(AlteracaoFornecedorLicitacao.class);
    }

    @Override
    public AlteracaoFornecedorLicitacao recuperar(Object id) {
        AlteracaoFornecedorLicitacao entity = super.recuperar(id);
        Hibernate.initialize(entity.getItens());
        return entity;
    }


    public AlteracaoFornecedorLicitacao salvarRetornando(AlteracaoFornecedorLicitacao entity, Pessoa participanteSubstituicao) {
        salvarPropostarOrParticipante(entity, participanteSubstituicao);
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(AlteracaoFornecedorLicitacao.class, "numero"));
        }
        return super.salvarRetornando(entity);
    }

    public void salvarLance(ItemPregao itemPregao) {
        for (RodadaPregao rod : itemPregao.getListaDeRodadaPregao()) {
            for (LancePregao lance : rod.getListaDeLancePregao()) {
                if (lance.getId() == null) {
                    em.persist(lance);
                }
            }
        }
    }

    private void salvarPropostarOrParticipante(AlteracaoFornecedorLicitacao entity, Pessoa participanteSubstituicao) {
        switch (entity.getTipoAlteracao()) {
            case NOVA_PROPOSTA:
            case ALTERAR_PROPOSTA:
                PropostaFornecedor propostaFornecedor = entity.getPropostaFornecedor();
                propostaFornecedor = propostaFornecedorFacade.salvarRetornando(propostaFornecedor);
                atribuirItemPropostaAoItemAlteracao(entity, propostaFornecedor);
                entity.setPropostaFornecedor(propostaFornecedor);
                break;
            case NOVO_PARTICIPANTE:
            case ALTERAR_PARTICIPANTE:
                LicitacaoFornecedor licitacaoFornecedor = entity.getLicitacaoFornecedor();
                if (licitacaoFornecedor.getRepresentante() != null) {
                    RepresentanteLicitacao representante = licitacaoFornecedor.getRepresentante();
                    if (representante.getId() == null) {
                        em.persist(representante);
                    } else {
                        em.merge(representante);
                    }
                }
                entity.setLicitacaoFornecedor(em.merge(licitacaoFornecedor));
                break;
            case SUBSTITUIR_PARTICIPANTE:
                PropostaFornecedor propostaFornec = propostaFornecedorFacade.recuperarPropostaDoFornecedorPorLicitacao(entity.getLicitacaoFornecedor().getEmpresa(), entity.getLicitacaoFornecedor().getLicitacao());
                if (propostaFornec == null) {
                    throw new ExcecaoNegocioGenerica("Proposta não encontrada para o fornecedor " + participanteSubstituicao + ".");
                }
                propostaFornec.setFornecedor(participanteSubstituicao);
                propostaFornec = em.merge(propostaFornec);
                entity.setPropostaFornecedor(propostaFornec);

                entity.getLicitacaoFornecedor().setEmpresa(participanteSubstituicao);
                em.merge(entity.getLicitacaoFornecedor());
                break;
            case SUBSTITUIR_FORNECEDOR_DISPENSA_LICITACAO:
                entity.setFornecedorDispensaLicitacao(entity.getFornecedorDispensaLicitacao());
                entity.getFornecedorDispensaLicitacao().setPessoa(participanteSubstituicao);
                em.merge(entity.getFornecedorDispensaLicitacao());

                List<Contrato> contratos = contratoFacade.buscarContratoDispensa(entity.getFornecedorDispensaLicitacao().getDispensaDeLicitacao());
                for (Contrato c : contratos) {
                    c.setContratado(participanteSubstituicao);
                    for (FornecedorContrato fc : c.getFornecedores()) {
                        fc.setFornecedor(participanteSubstituicao);
                    }
                }
                break;
        }
    }

    private void atribuirItemPropostaAoItemAlteracao(AlteracaoFornecedorLicitacao entity, PropostaFornecedor propostaFornecedor) {
        for (AlteracaoFornecedorLicitacaoItem itemAlt : entity.getItens()) {
            for (LotePropostaFornecedor lote : propostaFornecedor.getLotes()) {
                for (ItemPropostaFornecedor itemProp : lote.getItens()) {
                    if (itemProp.getItemProcessoDeCompra().equals(itemAlt.getItemPropostaFornecedor().getItemProcessoDeCompra())) {
                        itemAlt.setItemPropostaFornecedor(itemProp);
                    }
                }
            }
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public PropostaFornecedorFacade getPropostaFornecedorFacade() {
        return propostaFornecedorFacade;
    }

    public ParticipanteLicitacaoFacade getParticipanteLicitacaoFacade() {
        return participanteLicitacaoFacade;
    }

    public ItemPregaoFacade getItemPregaoFacade() {
        return itemPregaoFacade;
    }

    public PregaoFacade getPregaoFacade() {
        return pregaoFacade;
    }

    public DispensaDeLicitacaoFacade getDispensaLicitacaoFacade() {
        return dispensaLicitacaoFacade;
    }

    public FornecedorDispensaDeLicitacaoFacade getFornecedorDispensaDeLicitacaoFacade() {
        return fornecedorDispensaDeLicitacaoFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }
}
