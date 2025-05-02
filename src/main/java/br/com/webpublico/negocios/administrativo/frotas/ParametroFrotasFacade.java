package br.com.webpublico.negocios.administrativo.frotas;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.TaxaVeiculo;
import br.com.webpublico.entidades.administrativo.frotas.ParametroFrotas;
import br.com.webpublico.entidades.administrativo.frotas.ParametroFrotasHierarquia;
import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Created by Wellington on 04/02/2016.
 */
@Stateless
public class ParametroFrotasFacade extends AbstractFacade<ParametroFrotas> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private TaxaVeiculoFacade taxaVeiculoFacade;

    public ParametroFrotasFacade() {
        super(ParametroFrotas.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ParametroFrotas recuperar(Object id) {
        ParametroFrotas entity = super.recuperar(id);
        entity.getGruposPatrimoniais().size();
        entity.getItemParametroFrotasHierarquia().size();
        for (ParametroFrotasHierarquia parametroFrotasHierarquia : entity.getItemParametroFrotasHierarquia()) {
            parametroFrotasHierarquia.getItemTaxaVeiculo().size();
        }
        return entity;
    }

    public ParametroFrotas buscarParametroFrotas() {
        String sql = "select * from parametrofrotas ";
        Query q = em.createNativeQuery(sql, ParametroFrotas.class);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ParametroFrotas) q.getResultList().get(0);
        }
        return null;
    }

    public Boolean verificarSeExisteConfiguracaoVigenteParaGrupoPatrimonial(TipoObjetoFrota tipoObjetoFrota) {
        String sql = "" +
            " select " +
            "  distinct p.* " +
            " from parametrofrotas p " +
            "  inner join tipoobjetofrotagrupobem t on t.parametrofrotas_id = p.id " +
            " where to_date(:dataOperacao,'dd/MM/yyyy') between trunc(t.inicioVigencia) and  coalesce(trunc(t.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))" +
            "   and t.tipoObjetoFrota = :tipoObjetoFrota ";
        Query q = em.createNativeQuery(sql, ParametroFrotas.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("tipoObjetoFrota", tipoObjetoFrota.name());
        return q.getResultList() != null && !q.getResultList().isEmpty();
    }

    public List<TaxaVeiculo> buscarTaxasPorHierarquia(HierarquiaOrganizacional hierarquiaOrganizacional, Date dataVigencia) {
        String sql = " select taxa.* from ParametroFrotasTaxa pft " +
            " inner join taxaveiculo taxa on pft.taxaveiculo_id = taxa.id " +
            " inner join parametrofrotashierarquia pfh on PFT.PARAMETROFROTASHIERARQUIA_ID = pfh.id " +
            " where PFH.HIERARQUIAORGANIZACIONAL_ID = :hierarquia_id " +
            "   and to_date(:dataVigencia, 'dd/mm/yyyy') between taxa.iniciovigencia and coalesce(taxa.fimvigencia, to_date(:dataVigencia, 'dd/mm/yyyy'))";
        Query q = em.createNativeQuery(sql, TaxaVeiculo.class);
        q.setParameter("hierarquia_id", hierarquiaOrganizacional.getId());
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(dataVigencia));
        return q.getResultList();
    }

    public GrupoMaterialFacade getGrupoMaterialFacade() {
        return grupoMaterialFacade;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }

    public TaxaVeiculoFacade getTaxaVeiculoFacade() {
        return taxaVeiculoFacade;
    }

    public void setTaxaVeiculoFacade(TaxaVeiculoFacade taxaVeiculoFacade) {
        this.taxaVeiculoFacade = taxaVeiculoFacade;
    }
}
