package br.com.webpublico.negocios;

import br.com.webpublico.entidades.PlanoContratacaoAnual;
import br.com.webpublico.entidades.PlanoContratacaoAnualGrupoObjetoCompra;
import br.com.webpublico.entidadesauxiliares.PlanoContratacaoAnualObjetoCompraContratoVO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class PlanoContratacaoAnualFacade extends AbstractFacade<PlanoContratacaoAnual> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private GrupoObjetoCompraFacade grupoObjetoCompraFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private PlanoContratacaoAnualObjetoCompraFacade planoContratacaoAnualObjetoCompraFacade;
    @EJB
    private ContratoFacade contratoFacade;

    public PlanoContratacaoAnualFacade() {
        super(PlanoContratacaoAnual.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public PlanoContratacaoAnual recuperar(Object id) {
        PlanoContratacaoAnual entity = super.recuperar(id);
        Hibernate.initialize(entity.getGruposObjetoCompra());
        for (PlanoContratacaoAnualGrupoObjetoCompra grupo : entity.getGruposObjetoCompra()) {
            Hibernate.initialize(grupo.getObjetosCompra());
        }
        return entity;
    }

    @Override
    public PlanoContratacaoAnual salvarRetornando(PlanoContratacaoAnual entity) {
        if (entity.getNumero() == null){
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(PlanoContratacaoAnual.class, "numero").intValue());
        }
        return super.salvarRetornando(entity);
    }

    public boolean hasPlanoParaUnidadeAndExercicio(PlanoContratacaoAnual planoContratacaoAnual) {
        String sql = "select pca.* " +
            "from planocontratacaoanual pca " +
            "         inner join exercicio ex on ex.id = pca.exercicio_id " +
            "         inner join vwhierarquiaadministrativa vw on vw.subordinada_id = pca.unidadeorganizacional_id " +
            "         inner join unidadeorganizacional uo on uo.id = vw.subordinada_id " +
            "where ex.ano = :exercicio " +
            "and uo.id = :idUnidade ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", planoContratacaoAnual.getExercicio().getAno());
        q.setParameter("idUnidade", planoContratacaoAnual.getUnidadeOrganizacional().getId());

        return !q.getResultList().isEmpty();
    }

    public List<PlanoContratacaoAnualObjetoCompraContratoVO> buscarContratoPorObjetoCompra(Long idObjetoCompra) {
        String sql = " select distinct" +
                "       c.dataassinatura                                                                                 as dataAssinatura, " +
                "       c.numerocontrato || '/' || extract(year from c.datalancamento) || ' - ' || c.numerotermo || " +
                "       '/' || e.ano || ' - ' || coalesce(pf.cpf, pj.cnpj) || ' - ' || coalesce(pf.nome, pj.razaosocial) as descricaoContrato, " +
                "       vwadm.codigo || ' - ' || vwadm.descricao                                                         as unidadeAdm, " +
                "       coalesce( " +
                "               cast(itemsol.descricaocomplementar as varchar2(3000)), " +
                "               cast(ise.descricaocomplementar     as varchar2(3000)), " +
                "               cast(icot.descricaocomplementar    as varchar2(3000)) " +
                "       )                                                                                                as especificacao, " +
                "       und.sigla                                                                                        as unidadeMedida, " +
                "       ic.valorunitario                                                                                 as valorUnitario," +
                "       c.id                                                                                             as idContrato " +
                " from contrato c " +
                "         inner join exercicio e on c.exerciciocontrato_id = e.id " +
                "         inner join pessoa p on c.contratado_id = p.id " +
                "         left join pessoafisica pf on p.id = pf.id " +
                "         left join pessoajuridica pj on p.id = pj.id " +
                "         inner join itemcontrato ic on ic.contrato_id = c.id " +
                "         left join itemcontratovigente icv on icv.itemcontrato_id = ic.id " +
                "         left join itemcotacao icot on icot.id = icv.itemcotacao_id " +
                "         left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
                "         left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
                "         left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
                "         left join itemcontratoitemsolext icise on icise.itemcontrato_id = ic.id " +
                "         left join itemcontratoitempropdisp icipfd on icipfd.itemcontrato_id = ic.id " +
                "         left join itempropostafornedisp ipfd on icipfd.itempropfornecdispensa_id = ipfd.id " +
                "         left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
                "         left join itemprocessodecompra ipc on ipc.id = coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) " +
                "         left join itemsolicitacao itemsol on itemsol.id = ipc.itemsolicitacaomaterial_id " +
                "         left join itemsolicitacaoexterno ise on ise.id = icise.itemsolicitacaoexterno_id " +
                "         inner join objetocompra oc on oc.id = coalesce(ic.objetocompracontrato_id, itemsol.objetocompra_id, ise.objetocompra_id, icot.objetocompra_id) " +
                "         inner join unidademedida und on und.id = itemsol.unidademedida_id " +
                "         inner join unidadecontrato uc on uc.contrato_id = c.id " +
                "         inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = uc.unidadeadministrativa_id " +
                "where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(c.iniciovigencia) and trunc(c.terminovigenciaatual) " +
                "  and to_date(:dataOperacao, 'dd/MM/yyyy') between vwadm.inicioVigencia and coalesce(vwadm.fimVigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "  and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), " +
                "                                                                                         to_date(:dataOperacao, 'dd/mm/yyyy')) " +
                "  and oc.id = :idObjetoCompra " ;
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("idObjetoCompra", idObjetoCompra);
        q.setMaxResults(10);

        List<Object[]> resultado = q.getResultList();
        List<PlanoContratacaoAnualObjetoCompraContratoVO> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                PlanoContratacaoAnualObjetoCompraContratoVO vo = new PlanoContratacaoAnualObjetoCompraContratoVO();
                vo.setDataAssinatura((Date) obj[0]);
                vo.setDescricaoContrato((String) obj[1]);
                vo.setUnidadeAdm((String) obj[2]);
                vo.setEspecificacao((String) obj[3]);
                vo.setUnidadeMedida((String) obj[4]);
                vo.setValorUnitario(((BigDecimal) obj[5]));
                vo.setIdContrato(((BigDecimal) obj[6]).longValue());
                retorno.add(vo);
            }
        }

        try {
            return retorno;
        } catch (NoResultException ex) {
            return null;
        }
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public GrupoObjetoCompraFacade getGrupoObjetoCompraFacade() {
        return grupoObjetoCompraFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public PlanoContratacaoAnualObjetoCompraFacade getPlanoContratacaoAnualObjetoCompraFacade() {
        return planoContratacaoAnualObjetoCompraFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }
}
