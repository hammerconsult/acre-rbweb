package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AssociacaoGrupoObjetoCompraGrupoMaterial;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidades.GrupoObjetoCompra;
import br.com.webpublico.entidadesauxiliares.ImportacaoAssociacaoGrupoObjetoCompraGrupoMaterialDTO;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.joda.time.LocalDate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Buzatto on 06/12/2016.
 */
@Stateless
public class AssociacaoGrupoObjetoCompraGrupoMaterialFacade extends AbstractFacade<AssociacaoGrupoObjetoCompraGrupoMaterial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private GrupoObjetoCompraFacade grupoObjetoCompraFacade;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfigGrupoMaterialFacade configGrupoMaterialFacade;

    public AssociacaoGrupoObjetoCompraGrupoMaterialFacade() {
        super(AssociacaoGrupoObjetoCompraGrupoMaterial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public AssociacaoGrupoObjetoCompraGrupoMaterial buscarAssociacaoPorGrupoObjetoCompraVigente(AssociacaoGrupoObjetoCompraGrupoMaterial associacaoGrupoObjetoCompraGrupoMaterial) {
        try {
            String hql = "select associacao from AssociacaoGrupoObjetoCompraGrupoMaterial associacao where associacao.grupoObjetoCompra = :goc " +
                "            and (:dataVigencia between associacao.inicioVigencia and coalesce(associacao.finalVigencia, :dataVigencia) or associacao.finalVigencia is null) ";
            if (associacaoGrupoObjetoCompraGrupoMaterial.getId() != null) {
                hql += " and associacao.id != :id ";
            }
            Query q = em.createQuery(hql);
            q.setParameter("goc", associacaoGrupoObjetoCompraGrupoMaterial.getGrupoObjetoCompra());
            q.setParameter("dataVigencia", associacaoGrupoObjetoCompraGrupoMaterial.getInicioVigencia(), TemporalType.DATE);
            if (associacaoGrupoObjetoCompraGrupoMaterial.getId() != null) {
                q.setParameter("id", associacaoGrupoObjetoCompraGrupoMaterial.getId());
            }
            return (AssociacaoGrupoObjetoCompraGrupoMaterial) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (NonUniqueResultException ex) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais de uma Associação Grupo de Objeto de Compra e Grupo Material vigente para o grupo " + associacaoGrupoObjetoCompraGrupoMaterial.getGrupoObjetoCompra().getCodigo() + ", por favor, efetue a correção das associações e tente novamente.");
        }
    }

    public AssociacaoGrupoObjetoCompraGrupoMaterial buscarAssociacaoPorGrupoObjetoCompraVigente(GrupoObjetoCompra grupoObjetoCompra, Date dataVigencia) {
        String sql = "select associacao.* from associacaogruobjcomgrumat associacao " +
            "           where associacao.grupoObjetoCompra_id = :goc " +
            "           and :dataVigencia between  associacao.iniciovigencia and coalesce(associacao.finalvigencia, :dataVigencia) ";
        Query q = em.createNativeQuery(sql, AssociacaoGrupoObjetoCompraGrupoMaterial.class);
        q.setParameter("goc", grupoObjetoCompra.getId());
        q.setParameter("dataVigencia", dataVigencia, TemporalType.DATE);
        try {
            return (AssociacaoGrupoObjetoCompraGrupoMaterial) q.getSingleResult();
        } catch (NoResultException e) {
            throw new ExcecaoNegocioGenerica("Nenhuma associação de grupo material encontrada para grupo de objeto de compra: " + grupoObjetoCompra.getCodigo());
        } catch (NonUniqueResultException ex) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais de uma Associação Grupo de Objeto de Compra e Grupo Material vigente para o grupo " + grupoObjetoCompra.getCodigo() + ", por favor, efetue a correção das associações e tente novamente.");
        }
    }

    public AssociacaoGrupoObjetoCompraGrupoMaterial recuperarAssociacaoVigentePorGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra, Date dataVigencia) {
        String sql = "select associacao.* from associacaogruobjcomgrumat associacao " +
            "           where associacao.grupoObjetoCompra_id = :goc " +
            "           and :dataVigencia between  associacao.iniciovigencia and coalesce(associacao.finalvigencia, :dataVigencia) ";
        Query q = em.createNativeQuery(sql, AssociacaoGrupoObjetoCompraGrupoMaterial.class);
        q.setParameter("goc", grupoObjetoCompra.getId());
        q.setParameter("dataVigencia", dataVigencia, TemporalType.DATE);
        q.setMaxResults(1);
        try {
            return (AssociacaoGrupoObjetoCompraGrupoMaterial) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<AssociacaoGrupoObjetoCompraGrupoMaterial> buscarAssociacoesVigentes(Date dataAtual) {
        String sql = " select associacao.* " +
            "   from ASSOCIACAOGRUOBJCOMGRUMAT associacao " +
            " where :data_atual between associacao.inicioVigencia and coalesce(associacao.finalVigencia, :data_atual) ";
        Query q = em.createNativeQuery(sql, AssociacaoGrupoObjetoCompraGrupoMaterial.class);
        q.setParameter("data_atual", DataUtil.dataSemHorario(dataAtual));
        return q.getResultList();
    }

    public AssociacaoGrupoObjetoCompraGrupoMaterial getAssociacaoVigenteDaAssociacao(List<AssociacaoGrupoObjetoCompraGrupoMaterial> vigentes,
                                                                                     AssociacaoGrupoObjetoCompraGrupoMaterial associacaoGrupoObjetoCompraGrupoMaterial) {
        for (AssociacaoGrupoObjetoCompraGrupoMaterial vigente : vigentes) {
            if (vigente.getGrupoObjetoCompra().equals(associacaoGrupoObjetoCompraGrupoMaterial.getGrupoObjetoCompra()) &&
                vigente.getGrupoMaterial().equals(associacaoGrupoObjetoCompraGrupoMaterial.getGrupoMaterial())) {
                return vigente;
            }
        }

        return null;
    }

    public void inserirAssociacaoViaImportacao(List<ImportacaoAssociacaoGrupoObjetoCompraGrupoMaterialDTO> importados) {
        List<AssociacaoGrupoObjetoCompraGrupoMaterial> associacoes = new AssociacaoViaImportacaoDados(grupoObjetoCompraFacade, grupoMaterialFacade).processarDadosImportados(importados);
        List<AssociacaoGrupoObjetoCompraGrupoMaterial> associacoesVigentes = buscarAssociacoesVigentes(sistemaFacade.getDataOperacao());
        for (AssociacaoGrupoObjetoCompraGrupoMaterial associacao : associacoes) {
            inserirAssociacaoGrupoObjetoCompraGrupoMaterial(associacoesVigentes, associacao);
        }
    }

    private void inserirAssociacaoGrupoObjetoCompraGrupoMaterial(List<AssociacaoGrupoObjetoCompraGrupoMaterial> associacoesVigentes,
                                                                 AssociacaoGrupoObjetoCompraGrupoMaterial associacao) {
        AssociacaoGrupoObjetoCompraGrupoMaterial associacaoVigenteDaAssociacao = getAssociacaoVigenteDaAssociacao(associacoesVigentes, associacao);
        if (associacaoVigenteDaAssociacao != null) {
            encerrarVigenciaAssociacao(associacaoVigenteDaAssociacao);
        }
        em.persist(associacao);
    }

    private void encerrarVigenciaAssociacao(AssociacaoGrupoObjetoCompraGrupoMaterial associacao) {
        LocalDate hoje = LocalDate.now();
        associacao.setFinalVigencia(hoje.plusDays(-1).toDate());
        em.merge(associacao);
    }

    public class AssociacaoViaImportacaoDados {
        private GrupoObjetoCompraFacade grupoObjetoCompraFacade;
        private GrupoMaterialFacade grupoMaterialFacade;
        private List<GrupoObjetoCompra> gruposObjetoCompra = Lists.newArrayList();
        private List<GrupoMaterial> gruposMaterial = Lists.newArrayList();

        public AssociacaoViaImportacaoDados(GrupoObjetoCompraFacade grupoObjetoCompraFacade,
                                            GrupoMaterialFacade grupoMaterialFacade) {
            this.grupoObjetoCompraFacade = grupoObjetoCompraFacade;
            this.grupoMaterialFacade = grupoMaterialFacade;
            this.gruposObjetoCompra = this.grupoObjetoCompraFacade.lista();
            this.gruposMaterial = this.grupoMaterialFacade.lista();
        }

        public GrupoObjetoCompra getGrupoObjetoCompra(String codigoGrupoObjetoCompra) {
            for (GrupoObjetoCompra grupoObjetoCompra : gruposObjetoCompra) {
                if (codigoGrupoObjetoCompra.trim().equals(grupoObjetoCompra.getCodigo().trim())) {
                    return grupoObjetoCompra;
                }
            }
            return null;
        }

        public GrupoMaterial getGrupoMaterial(String codigoGrupoMaterial) {
            for (GrupoMaterial grupoMaterial : gruposMaterial) {
                if (codigoGrupoMaterial.trim().equals(grupoMaterial.getCodigo().trim())) {
                    return grupoMaterial;
                }
            }
            return null;
        }

        public List<AssociacaoGrupoObjetoCompraGrupoMaterial> processarDadosImportados(List<ImportacaoAssociacaoGrupoObjetoCompraGrupoMaterialDTO> importados) {
            List<AssociacaoGrupoObjetoCompraGrupoMaterial> toReturn = Lists.newArrayList();
            if (importados != null) {
                for (ImportacaoAssociacaoGrupoObjetoCompraGrupoMaterialDTO importado : importados) {
                    GrupoObjetoCompra grupoObjetoCompra = getGrupoObjetoCompra(importado.getGrupoObjetoCompra());
                    GrupoMaterial grupoMaterial = getGrupoMaterial(importado.getGrupoMaterial());
                    toReturn.add(new AssociacaoGrupoObjetoCompraGrupoMaterial(importado.getInicioVigencia(), grupoObjetoCompra, grupoMaterial));
                }
            }
            return toReturn;
        }
    }

    public AssociacaoGrupoObjetoCompraGrupoMaterial buscarAssociacaoPorGrupoObjetoCompraImportacao(GrupoObjetoCompra grupoObjetoCompra) {
        try {
            String sql = "select associacao.* " +
                "           from ASSOCIACAOGRUOBJCOMGRUMAT associacao " +
                "         where associacao.grupoObjetoCompra_id = :goc" +
                "  and :data BETWEEN  associacao.iniciovigencia and coalesce(associacao.finalvigencia, :data)";
            Query q = em.createNativeQuery(sql, AssociacaoGrupoObjetoCompraGrupoMaterial.class);
            q.setParameter("goc", grupoObjetoCompra.getId());
            q.setParameter("data", sistemaFacade.getDataOperacao(), TemporalType.DATE);
            return (AssociacaoGrupoObjetoCompraGrupoMaterial) q.getResultList().get(0);
        } catch (Exception nre) {
            throw new ExcecaoNegocioGenerica("Nenhuma associação encontrada com grupo de objeto de compra: " + grupoObjetoCompra);
        }
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public GrupoMaterialFacade getGrupoMaterialFacade() {
        return grupoMaterialFacade;
    }

    public List<Conta> buscarContasDespesaPorGrupoMaterial(GrupoMaterial grupoMaterial) {
        return configGrupoMaterialFacade.buscarContasDespesaPorGrupoMaterial(grupoMaterial, sistemaFacade.getDataOperacao(), null);
    }
}
