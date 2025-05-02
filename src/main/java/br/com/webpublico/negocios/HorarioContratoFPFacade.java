/*
 * Codigo gerado automaticamente em Thu Aug 04 16:53:01 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.HorarioContratoFP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class HorarioContratoFPFacade extends AbstractFacade<HorarioContratoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HorarioContratoFPFacade() {
        super(HorarioContratoFP.class);
    }

    @Override
    public void salvar(HorarioContratoFP entity) {
        entity = getHorarioContratoFPComHistorico(entity);
        super.salvar(entity);
    }

    public HorarioContratoFP getHorarioContratoFPComHistorico(HorarioContratoFP horarioContratoFP) {
        HorarioContratoFP horarioContratoFPAtualmentePersistido = getHorarioContratoFPAtualmentePersistido(horarioContratoFP);
        horarioContratoFP.criarOrAtualizarAndAssociarHistorico(horarioContratoFPAtualmentePersistido);
        return horarioContratoFP;
    }

    private HorarioContratoFP getHorarioContratoFPAtualmentePersistido(HorarioContratoFP horarioContratoFP) {
        return recuperar(horarioContratoFP.getId());
    }

    @Override
    public HorarioContratoFP recuperar(Object id) {
        HorarioContratoFP horario = super.recuperar(id);
        horario.getLocalTrabalho().size();
        return horario;

    }
}
