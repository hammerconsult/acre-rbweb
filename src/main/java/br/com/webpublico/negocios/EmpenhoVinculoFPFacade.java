package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class EmpenhoVinculoFPFacade extends AbstractFacade<EmpenhoVinculoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private CompetenciaFPFacade competenciaFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;

    public EmpenhoVinculoFPFacade() {
        super(EmpenhoVinculoFP.class);
    }

    @Override
    public EmpenhoVinculoFP recuperar(Object id) {
        EmpenhoVinculoFP empenhoVinculoFP = em.find(EmpenhoVinculoFP.class, id);
        empenhoVinculoFP.getFichasFinanceiras().size();
        return empenhoVinculoFP;
    }

    public List<Empenho> buscarEmpenhos(String filtro) {
        List<CategoriaOrcamentaria> categorias = Lists.newArrayList();
        categorias.add(CategoriaOrcamentaria.NORMAL);
        categorias.add(CategoriaOrcamentaria.RESTO);
        return empenhoFacade.buscarPorNumeroAndPessoaRelatorioPorCategoria(filtro, null, categorias);
    }

    public List<VinculoFP> buscarServidores(String filtro) {
        return vinculoFPFacade.listaTodosVinculos1(filtro.trim());
    }

    public List<CompetenciaFP> buscarCompetencias() {
        return competenciaFPFacade.buscarTodasCompetencias();
    }

    public List<FolhaDePagamento> buscarFolhasDePagamento(CompetenciaFP competenciaFP) {
        return folhaDePagamentoFacade.recuperaFolhaPelaCompetencia(competenciaFP);
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }

    public List<FichaFinanceiraFP> buscarFichasFinanceirasPorCompetenciaOrServidorOrFolha(String filtros) {
        return fichaFinanceiraFPFacade.buscarFichasFinanceirasPorCompetenciaOrServidorOrFolha(filtros);
    }

    public boolean hasVinculoComFichaCadastrado(FichaFinanceiraFP fichaFinanceiraFP) {
        String sql = " select 1 from EmpenhoFichaFinanceiraFP eff " +
            " where eff.fichaFinanceiraFP_id = :ficha ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ficha", fichaFinanceiraFP.getId());
        return !q.getResultList().isEmpty();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
