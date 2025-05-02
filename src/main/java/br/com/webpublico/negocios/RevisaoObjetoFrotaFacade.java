/*
 * Codigo gerado automaticamente em Thu Nov 03 15:57:46 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.RevisaoObjetoFrota;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class RevisaoObjetoFrotaFacade extends AbstractFacade<RevisaoObjetoFrota> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private ProgramaRevisaoVeiculoFacade programaRevisaoVeiculoFacade;
    @EJB
    private ManutencaoObjetoFrotaFacade manutencaoObjetoFrotaFacade;
    @EJB
    private ProgramaRevisaoEquipamentoFacade programaRevisaoEquipamentoFacade;

    public RevisaoObjetoFrotaFacade() {
        super(RevisaoObjetoFrota.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public void salvarNovo(RevisaoObjetoFrota entity) {
        movimentarProgramaRevisaoVeiculoOrEquipamento(entity, Boolean.TRUE);
        super.salvarNovo(entity);
    }

    @Override
    public void salvar(RevisaoObjetoFrota entity) {
        movimentarProgramaRevisaoVeiculoOrEquipamento(entity, Boolean.TRUE);
        super.salvar(entity);
    }

    private void movimentarProgramaRevisaoVeiculoOrEquipamento(RevisaoObjetoFrota entity, Boolean revisaoRealizada) {
        if (entity.getTipoObjetoFrota().isVeiculo()) {
            programaRevisaoVeiculoFacade.movimentarProgramaRevisaoVeiculo(entity.getProgramaRevisaoVeiculo(), revisaoRealizada);
        }else{
            programaRevisaoEquipamentoFacade.movimentarProgramaRevisaoEquipamento(entity.getProgramaRevisaoEquipamento(), revisaoRealizada);
        }
    }

    @Override
    public void remover(RevisaoObjetoFrota entity) {
        movimentarProgramaRevisaoVeiculoOrEquipamento(entity, Boolean.FALSE);
        super.remover(entity);
    }

    @Override
    public RevisaoObjetoFrota recuperar(Object id) {
        RevisaoObjetoFrota revisaoObjetoFrota = super.recuperar(id);
        if (revisaoObjetoFrota.getDetentorArquivoComposicao() != null) {
            revisaoObjetoFrota.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return revisaoObjetoFrota;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public ProgramaRevisaoVeiculoFacade getProgramaRevisaoVeiculoFacade() {
        return programaRevisaoVeiculoFacade;
    }

    public ManutencaoObjetoFrotaFacade getManutencaoObjetoFrotaFacade() {
        return manutencaoObjetoFrotaFacade;
    }

    public ProgramaRevisaoEquipamentoFacade getProgramaRevisaoEquipamentoFacade() {
        return programaRevisaoEquipamentoFacade;
    }
}
