package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ContratoCardapioMaterialVO;
import br.com.webpublico.entidadesauxiliares.ContratoCardapioVO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class CardapioFacade extends AbstractFacade<Cardapio> {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;
    @EJB
    private ProgramaAlimentacaoFacade programaAlimentacaoFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private RefeicaoFacade refeicaoFacade;
    @EJB
    private FeriadoFacade feriadoFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public CardapioFacade() {
        super(Cardapio.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(Cardapio entity) {
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(Cardapio.class, "numero"));
        }
        super.salvarNovo(entity);
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void setSistemaFacade(SistemaFacade sistemaFacade) {
        this.sistemaFacade = sistemaFacade;
    }

    public MaterialFacade getMaterialFacade() {
        return materialFacade;
    }

    public RequisicaoDeCompraFacade getRequisicaoDeCompraFacade() {
        return requisicaoDeCompraFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public ProgramaAlimentacaoFacade getProgramaAlimentacaoFacade() {
        return programaAlimentacaoFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public RefeicaoFacade getRefeicaoFacade() {
        return refeicaoFacade;
    }

    public FeriadoFacade getFeriadoFacade() {
        return feriadoFacade;
    }

    @Override
    public Cardapio recuperar(Object id) {
        Cardapio recuperar = super.recuperar(id);
        Hibernate.initialize(recuperar.getDiasSemana());
        Hibernate.initialize(recuperar.getRequisicoesCompra());
        for (CardapioAgenda dia : recuperar.getDiasSemana()) {
            Hibernate.initialize(dia.getRefeicoes());
            for (CardapioAgendaRefeicao refeicao : dia.getRefeicoes()) {
                Hibernate.initialize(refeicao.getMateriais());
            }
        }
        return recuperar;
    }

    public List<Cardapio> buscarCardapio(String parte) {
        String sql;
        sql = " select cd.* from cardapio cd " +
            "     where (cd.numero like :parte or lower(cd.descricao) like :parte)" +
            "   order by cd.numero ";
        Query q = em.createNativeQuery(sql, Cardapio.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public List<LocalEstoque> buscarCardapioLocalEstoqueFilhos(Cardapio cardapio) {
        String sql = " select le.* from cardapio card " +
            "             inner join programaalimentacao pa on pa.id = card.programaalimentacao_id " +
            "             inner join programaalimentacaolocales ple on ple.programaalimentacao_id = pa.id " +
            "             inner join localestoque le on le.id = ple.localestoque_id " +
            "          where card.id = :idCardapio " +
            "            and le.superior_id is not null ";
        Query q = em.createNativeQuery(sql, LocalEstoque.class);
        q.setParameter("idCardapio", cardapio.getId());
        return q.getResultList();
    }

    public LocalEstoque buscarCardapioLocalEstoquePai(Cardapio cardapio) {
        String sql = " select lepai.* from cardapio card " +
            "             inner join programaalimentacao pa on pa.id = card.programaalimentacao_id " +
            "             inner join programaalimentacaolocales ple on ple.programaalimentacao_id = pa.id " +
            "             inner join localestoque le on le.id = ple.localestoque_id " +
            "             inner join localestoque lepai on lepai.id = le.superior_id " +
            "          where card.id = :idCardapio " +
            "            and not exists (select 1 from localestoque l where l.superior_id = le.id) ";
        Query q = em.createNativeQuery(sql, LocalEstoque.class);
        q.setParameter("idCardapio", cardapio.getId());
        try {
            return (LocalEstoque) q.getSingleResult();
        } catch (NoResultException ex) {
            throw new ValidacaoException("Local de estoque pai não encontrado para a integração com o cardápio " + cardapio + ".");
        }
    }

    public List<ContratoCardapioVO> buscarContratos(Cardapio cardapio, HierarquiaOrganizacional hierarquiaAdm) {
        String sql;
        sql = "select distinct cont.id                                                                               as id,  " +
            "                cont.NUMEROCONTRATO || ' - ' || cont.NUMEROTERMO || '/' || ex.ano                       as numeroContrato,  " +
            "                FORMATACPFCNPJ(coalesce(pf.CPF, pj.CNPJ)) || ' - ' || coalesce(pf.nome, pj.RAZAOSOCIAL) as fornecedor,  " +
            "                vwha.CODIGO || ' - ' || vwha.DESCRICAO                                                  as unidadeAdm,  " +
            "                cont.TERMINOVIGENCIAATUAL                                                               as fimVigencia  " +
            "from contrato cont  " +
            "         inner join pessoa p on cont.CONTRATADO_ID = p.ID  " +
            "         left join PESSOAFISICA pf on p.ID = pf.ID  " +
            "         left join PESSOAJURIDICA pj on p.id = pj.id  " +
            "         inner join EXERCICIO ex on cont.EXERCICIOCONTRATO_ID = ex.ID  " +
            "         inner join UNIDADECONTRATO unid on cont.ID = unid.CONTRATO_ID  " +
            "         inner join VWHIERARQUIAADMINISTRATIVA vwha on vwha.SUBORDINADA_ID = unid.UNIDADEADMINISTRATIVA_ID  " +
            "         inner join ITEMCONTRATO ic on cont.ID = ic.CONTRATO_ID  " +
            "         left join itemcontratoitempropfornec icpf on ic.id = icpf.itemcontrato_id  " +
            "         left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id  " +
            "         left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id  " +
            "         left join itemcontratoitempropdisp icpd on ic.id = icpd.itemcontrato_id  " +
            "         left join itempropostafornedisp ipfd on icpd.itempropfornecdispensa_id = ipfd.id  " +
            "         left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id,  " +
            "                                                  iata.itempropostafornecedor_id) = ipf.id  " +
            "         left join itemprocessodecompra ipc  " +
            "                   on coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) = ipc.id  " +
            "         left join itemsolicitacao its on ipc.itemsolicitacaomaterial_id = its.id  " +
            "         left join itemcontratoitemsolext icse on ic.id = icse.itemcontrato_id  " +
            "         left join itemsolicitacaoexterno ise on icse.itemsolicitacaoexterno_id = ise.id  " +
            "         left join itemcontratovigente icv on ic.id = icv.itemcontrato_id  " +
            "         left join itemcotacao itcot on itcot.id = icv.itemcotacao_id  " +
            "         inner join objetocompra oc  " +
            "                    on coalesce(its.OBJETOCOMPRA_ID, ise.OBJETOCOMPRA_ID, itcot.OBJETOCOMPRA_ID) = oc.id  " +
            "         inner join material mat on oc.ID = mat.OBJETOCOMPRA_ID  " +
            "inner join CARDAPIOAGENDAREFEICAOMAT cmat on cmat.MATERIAL_ID = mat.ID  " +
            "inner join CardapioAgendaRefeicao cref on cref.id = cmat.CARDAPIOAGENDAREFEICAO_ID  " +
            "inner join CardapioAgenda cagenda on cref.CARDAPIOAGENDA_ID = cagenda.ID  " +
            "where cagenda.CARDAPIO_ID = :idCardapio  " +
            "    and vwha.CODIGO like :codigoHo " +
            "    and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(vwha.iniciovigencia) and coalesce(trunc(vwha.FIMVIGENCIA),  " +
            "                                                                                           to_date(:dataOperacao, 'dd/MM/yyyy'))  " +
            "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(unid.iniciovigencia) and coalesce(trunc(unid.FIMVIGENCIA),  " +
            "                                                                                           to_date(:dataOperacao, 'dd/MM/yyyy'))" +
            "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(cont.INICIOVIGENCIA) and coalesce(trunc(cont.TERMINOVIGENCIAATUAL), " +
            "                                                                                           to_date(:dataOperacao, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("idCardapio", cardapio.getId());
        q.setParameter("codigoHo", hierarquiaAdm.getCodigo().substring(0, 6) + "%");
        List<Object[]> list = q.getResultList();

        List<ContratoCardapioVO> contratos = new ArrayList<>();
        for (Object[] objects : list) {
            ContratoCardapioVO contrato = new ContratoCardapioVO();
            contrato.setId(((BigDecimal) objects[0]).longValue());
            contrato.setNumero((String) objects[1]);
            contrato.setFornecedor((String) objects[2]);
            contrato.setUnidadeContrato((String) objects[3]);
            contrato.setFimVigencia((Date) objects[4]);
            contrato.setMateriais(buscarMateriaisContratos(contrato.getId()));
            contratos.add(contrato);
        }
        return contratos;
    }

    public List<ContratoCardapioMaterialVO> buscarMateriaisContratos(Long idContrato) {
        String sql;
        sql = " select distinct " +
            "       mat.codigo                                     as codigomaterial, " +
            "       mat.descricao                                     as descricaomaterial, " +
            "       coalesce(ipc.numero, ise.numero, itcot.numero)    as numeroitem, " +
            "       coalesce(lote.numero, lcot.numero, 1)             as numerolote " +
            " from itemcontrato ic " +
            "         left join itemcontratoitempropfornec icpf on ic.id = icpf.itemcontrato_id " +
            "         left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
            "         left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
            "         left join itemcontratoitempropdisp icpd on ic.id = icpd.itemcontrato_id " +
            "         left join itempropostafornedisp ipfd on icpd.itempropfornecdispensa_id = ipfd.id " +
            "         left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
            "         left join itemprocessodecompra ipc on coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) = ipc.id " +
            "         left join loteprocessodecompra lote on lote.id = ipc.loteprocessodecompra_id " +
            "         left join itemsolicitacao its on ipc.itemsolicitacaomaterial_id = its.id " +
            "         left join itemcontratoitemsolext icse on ic.id = icse.itemcontrato_id " +
            "         left join itemsolicitacaoexterno ise on icse.itemsolicitacaoexterno_id = ise.id " +
            "         left join itemcontratovigente icv on ic.id = icv.itemcontrato_id " +
            "         left join itemcotacao itcot on itcot.id = icv.itemcotacao_id " +
            "         left join lotecotacao lcot on lcot.id = itcot.lotecotacao_id " +
            "         inner join objetocompra oc on coalesce(its.objetocompra_id, ise.objetocompra_id, itcot.objetocompra_id) = oc.id " +
            "         inner join material mat on oc.id = mat.objetocompra_id " +
            "         inner join refeicaomaterial rm on rm.material_id = mat.id" +
            " where ic.contrato_id = :idContrato " +
            " order by coalesce(lote.NUMERO, lcot.NUMERO, 1), coalesce(ipc.numero, ise.numero, itcot.numero) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContrato", idContrato);
        List<Object[]> list = q.getResultList();
        List<ContratoCardapioMaterialVO> materiais = new ArrayList<>();
        for (Object[] objects : list) {
            ContratoCardapioMaterialVO material = new ContratoCardapioMaterialVO();
            material.setCodigoMaterial(((BigDecimal) objects[0]).longValue());
            material.setDescricaoMaterial((String) objects[1]);
            material.setNumeroItem(((BigDecimal) objects[2]).longValue());
            material.setNumeroLote(((BigDecimal) objects[3]).longValue());
            materiais.add(material);
        }
        return materiais;
    }
}
