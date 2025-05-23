package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.enums.administrativo.SituacaoDocumentoFiscalEntradaMaterial;
import br.com.webpublico.exception.OperacaoEstoqueException;
import br.com.webpublico.negocios.administrativo.dao.JdbcIntegracaoDocumentoFiscalLiquidacao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@Stateless
public class IntegradorDocumentoFiscalLiquidacaoFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfigGrupoMaterialFacade configGrupoMaterialFacade;
    @EJB
    private ConfigDespesaBensFacade configDespesaBensFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private EntradaMaterialFacade entradaMaterialFacade;
    @EJB
    private DoctoFiscalLiquidacaoFacade doctoFiscalLiquidacaoFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private EfetivacaoAquisicaoFacade efetivacaoAquisicaoFacade;
    @EJB
    private TransfBensMoveisFacade transfBensMoveisFacade;
    @EJB
    private TransferenciaBensEstoqueFacade transferenciaBensEstoqueFacade;
    private JdbcIntegracaoDocumentoFiscalLiquidacao jdbcIntegracaoDocumentoFiscalLiquidacao;

    @PostConstruct
    public void init() {
        jdbcIntegracaoDocumentoFiscalLiquidacao = (JdbcIntegracaoDocumentoFiscalLiquidacao) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcIntegracaoDocumentoFiscalLiquidacao");
    }

    public DocumentoFiscalIntegracao criarOrigemDocumentoFsical(DocumentoFiscalIntegracaoAssistente assistente) {
        switch (assistente.getEmpenho().getTipoContaDespesa()) {
            case BEM_MOVEL:
                DocumentoFiscalIntegracao docGrupoBem = buscarDadosDocumentoAquisicaoBemMovel(assistente);
                docGrupoBem.setDoctoFiscalLiquidacao(assistente.getDoctoFiscalLiquidacao());
                docGrupoBem.setGrupos(buscarGrupoBemIntegracao(assistente, docGrupoBem));
                return docGrupoBem;
            case BEM_ESTOQUE:
                DocumentoFiscalIntegracao docGrupoMaterial = buscarDadosDocumentoEntradaPorCompra(assistente);
                docGrupoMaterial.setDoctoFiscalLiquidacao(assistente.getDoctoFiscalLiquidacao());
                docGrupoMaterial.setGrupos(buscarGrupoMaterialIntegracao(assistente, docGrupoMaterial));
                return docGrupoMaterial;
            default:
                return null;
        }
    }

    public DocumentoFiscalIntegracao criarDocumentoFiscalIntegracao(DocumentoFiscalIntegracaoAssistente assistente) {
        switch (assistente.getEmpenho().getTipoContaDespesa()) {
            case BEM_MOVEL:
                DocumentoFiscalIntegracao novoDocGrupoBem = novoDocumentoIntegracao(assistente);
                novoDocGrupoBem.setTipoContaDespesa(TipoContaDespesa.BEM_MOVEL);
                novoDocGrupoBem.setGrupos(buscarGrupoBemIntegracao(assistente, novoDocGrupoBem));
                novoDocGrupoBem.setTransferenciasBens(criarTransferenciasBensMoveisNormal(assistente, novoDocGrupoBem));
                novoDocGrupoBem.setEstornoTransferenciasBens(criarTransferenciasBensMoveisEstorno(assistente, novoDocGrupoBem));
                novoDocGrupoBem.setEmpenhosDocumentoFiscal(buscarEmpenhoDocumentoFiscal(assistente, novoDocGrupoBem));
                return novoDocGrupoBem;
            case BEM_ESTOQUE:
                DocumentoFiscalIntegracao novoDocGrupoMaterial = novoDocumentoIntegracao(assistente);
                novoDocGrupoMaterial.setTipoContaDespesa(TipoContaDespesa.BEM_ESTOQUE);
                novoDocGrupoMaterial.setGrupos(buscarGrupoMaterialIntegracao(assistente, novoDocGrupoMaterial));
                novoDocGrupoMaterial.setTransferenciasBens(criarTransferenciasBensEstoqueNormal(assistente, novoDocGrupoMaterial));
                novoDocGrupoMaterial.setEstornoTransferenciasBens(criarTransferenciasBensEstoqueEstorno(assistente, novoDocGrupoMaterial));
                novoDocGrupoMaterial.setEmpenhosDocumentoFiscal(buscarEmpenhoDocumentoFiscal(assistente, novoDocGrupoMaterial));
                return novoDocGrupoMaterial;
            default:
                return null;
        }
    }

    private DocumentoFiscalIntegracao novoDocumentoIntegracao(DocumentoFiscalIntegracaoAssistente filtro) {
        DocumentoFiscalIntegracao docInteg = new DocumentoFiscalIntegracao();
        docInteg.setDoctoFiscalLiquidacao(filtro.getDoctoFiscalLiquidacao());
        docInteg.setValorLiquidado(doctoFiscalLiquidacaoFacade.buscarValorLiquidado(docInteg.getDoctoFiscalLiquidacao()));
        return docInteg;
    }

    public List<DocumentoFiscalIntegracaoGrupo> buscarGrupoBemIntegracao(DocumentoFiscalIntegracaoAssistente assistente, DocumentoFiscalIntegracao docIntegracao) {
        String sql =
            "     select grupo_integrador, " +
                "        grupo_desdobramento, " +
                "        grupo_item, " +
                "        valor, " +
                "        tipogrupo, " +
                "        case when grupo_integrador = grupo_desdobramento then 1 else 0 end as integrador " +
                "   from " +
                "    (select " +
                "     gb.id        as grupo_integrador, " +
                "     (select config.GRUPOBEM_ID from desdobramentoempenho desd " +
                "      inner join ConfigDespesaBens config on config.CONTADESPESA_ID = desd.CONTA_ID " +
                "      where desd.EMPENHO_ID = :idEmpenho " +
                "      and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(config.iniciovigencia) and coalesce(trunc(config.fimvigencia),  to_date(:dataOperacao, 'dd/mm/yyyy'))" +
                "       )          as grupo_desdobramento," +
                "     gbAq.id      as grupo_item, " +
                "     et.tipogrupo as tipogrupo, " +
                "     sum(itemfiscal.valorunitario) as valor" +
                "   from doctofiscalliquidacao doc " +
                "   inner join itemdoctofiscalliquidacao itemfiscal on doc.id = itemfiscal.doctofiscalliquidacao_id " +
                "   inner join itemdoctoitemaquisicao associacao on associacao.itemdoctofiscalliquidacao_id = itemfiscal.id " +
                "   inner join itemsolicitacaoaquisicao isa on isa.itemdoctoitemaquisicao_id = associacao.id " +
                "   inner join itemaquisicao on itemaquisicao.itemsolicitacaoaquisicao_id = isa.id " +
                "   inner join eventobem ev on ev.id = itemaquisicao.id " +
                "   inner join estadobem et on et.id = ev.estadoresultante_id " +
                "   inner join grupobem gbAq on gbAq.id = et.grupobem_id " +
                "   inner join itemrequisicaodecompra irc on irc.id = associacao.itemrequisicaodecompra_id ";
        sql += getJoinItemProcessoObjetoCompra();
        sql += "    inner join grupoobjcompragrupobem agb on agb.grupoobjetocompra_id = oc.grupoobjetocompra_id " +
            "       inner join grupobem gb on gb.id = agb.grupobem_id " +
            "     where to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(agb.iniciovigencia) and coalesce(trunc(agb.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy'))" +
            "       and itemfiscal.doctofiscalliquidacao_id = :idDocumento " +
            "     group by gbAq.id, gb.id, et.tipogrupo)" +
            "     order by grupo_integrador";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDocumento", docIntegracao.getDoctoFiscalLiquidacao().getId());
        q.setParameter("idEmpenho", assistente.getEmpenho().getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        List<DocumentoFiscalIntegracaoGrupo> grupos = Lists.newArrayList();
        Map<DocumentoFiscalIntegracaoGrupo, Map<GrupoBem, BigDecimal>> mapGrupo = preencherMapGrupoIntegradorGrupoBem((List<Object[]>) q.getResultList());

        for (Map.Entry<DocumentoFiscalIntegracaoGrupo, Map<GrupoBem, BigDecimal>> entry : mapGrupo.entrySet()) {
            DocumentoFiscalIntegracaoGrupo grupoDocumento = entry.getKey();
            if (assistente.getIdProcessoOriginouEmpenho() != null) {
                grupoDocumento.setEmpenho(empenhoFacade.recuperarEmpenhoPorGrupoBemAndProcesso(grupoDocumento.getGrupoBem(), assistente.getIdProcessoOriginouEmpenho()));
            }
            if (!docIntegracao.getIntegrador()) {
                docIntegracao.setIntegrador(grupoDocumento.getIntegrador());
            }
            atribuirDesdobramentoAoDocumentoFiscal(assistente, docIntegracao);

            grupoDocumento.getItens().addAll(buscarItensAquisicaoDocumentoFiscal(assistente, grupoDocumento));
            grupoDocumento.setValorGrupo(grupoDocumento.getValorGrupo().add(grupoDocumento.getValorTotalLancamento()));
            atribuirValorDocumentoIntegrador(assistente, docIntegracao, grupoDocumento);
            grupos.add(grupoDocumento);
        }
        return grupos;
    }

    private void atribuirValorDocumentoIntegrador(DocumentoFiscalIntegracaoAssistente assistente, DocumentoFiscalIntegracao docIntegracao, DocumentoFiscalIntegracaoGrupo grupoDocumento) {
        if (grupoDocumento.getIntegrador() && !assistente.getDadosComponente()) {
            BigDecimal valorLiquidadoPoGrupo = recuperarValorLiquidadoPorGrupo(docIntegracao, grupoDocumento);
            BigDecimal saldoDocumento = assistente.getLancamentoNormal()
                ? grupoDocumento.getValorGrupo().subtract(valorLiquidadoPoGrupo)
                : assistente.getValorEstornarDocumento();
            docIntegracao.setSaldo(saldoDocumento);
            docIntegracao.setValorALiquidar(saldoDocumento);

            for (DocumentoFiscalIntegracaoGrupoItem itemIntegrador : grupoDocumento.getItens()) {
                itemIntegrador.setValorALiquidar(DocumentoFiscalIntegracaoGrupoItem.getValorProporcionalAoItem(grupoDocumento.getValorGrupo(), docIntegracao.getValorALiquidar(), itemIntegrador.getValorLancamento()));
            }
            docIntegracao.setExpandirGrupos(docIntegracao.hasSaldo());
            docIntegracao.setSelecionado(docIntegracao.hasSaldo());

            atribuirValorDesdobramentoIntegrador(assistente, grupoDocumento);
        }
    }

    private BigDecimal recuperarValorLiquidadoPorGrupo(DocumentoFiscalIntegracao docIntegracao, DocumentoFiscalIntegracaoGrupo grupoDocumento) {
        BigDecimal valorLiquidadoPoGrupo = BigDecimal.ZERO;
        HashSet<String> gruposComValorJaRecuperados = new HashSet<>();
        for (DocumentoFiscalIntegracaoGrupoItem item : grupoDocumento.getItens()) {
            if (!gruposComValorJaRecuperados.contains(item.getGrupo())) {
                BigDecimal valorParcial = docIntegracao.isBemMovel()
                    ? efetivacaoAquisicaoFacade.getValorLiquidadoPorGrupo(item.getIdGrupo(), docIntegracao.getDoctoFiscalLiquidacao())
                    : entradaMaterialFacade.getValorLiquidadoPorGrupo(item.getIdGrupo(), docIntegracao.getDoctoFiscalLiquidacao());

                valorLiquidadoPoGrupo = valorLiquidadoPoGrupo.add(valorParcial);
                gruposComValorJaRecuperados.add(item.getGrupo());
            }
        }
        return valorLiquidadoPoGrupo;
    }

    private void atribuirDesdobramentoAoDocumentoFiscal(DocumentoFiscalIntegracaoAssistente assistente, DocumentoFiscalIntegracao docIntegracao) {
        if (assistente.getLancamentoNormal()) {
            docIntegracao.setDesdobramento(assistente.getDesdobramento());
        } else {
            docIntegracao.setDesdobramentoEstorno(assistente.getDesdobramentoEstorno());
        }
    }

    public List<DocumentoFiscalIntegracaoGrupo> buscarGrupoMaterialIntegracao(DocumentoFiscalIntegracaoAssistente assistente, DocumentoFiscalIntegracao docIntegracao) {
        String sql =
            "     select distinct grupo_integrador, " +
                "        grupo_desdobramento, " +
                "        grupo_item, " +
                "        valor, " +
                "        case when grupo_integrador = grupo_desdobramento then 1 else 0 end as integrador " +
                "   from " +
                "    (select " +
                "     gm.id        as grupo_integrador, " +
                "     (select config.grupoMaterial_id from desdobramentoempenho desd " +
                "      inner join ConfigGrupoMaterial config on config.CONTADESPESA_ID = desd.CONTA_ID " +
                "      where desd.EMPENHO_ID = :idEmpenho " +
                "      and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(config.iniciovigencia) and coalesce(trunc(config.fimvigencia),  to_date(:dataOperacao, 'dd/mm/yyyy'))" +
                "       )           as grupo_desdobramento," +
                "     gmEnt.id      as grupo_item, " +
                "     coalesce(round(sum(itemDoc.quantidade * item.valorunitario), 2), 0) as valor " +
                "       from itementradamaterial item " +
                "        inner join itemcompramaterial icm on icm.itementradamaterial_id = item.id " +
                "         inner join itemdoctoitementrada itemDoc on itemDoc.itementradamaterial_id = item.id " +
                "         inner join doctofiscalentradacompra docEnt on docEnt.id = itemDoc.doctofiscalentradacompra_id  " +
                "         inner join material mat on mat.id = item.material_id " +
                "         inner join grupomaterial gmEnt on gmEnt.id = mat.grupo_id " +
                "         inner join itemrequisicaodecompra irc on irc.id = icm.itemrequisicaodecompra_id ";
        sql += getJoinItemProcessoObjetoCompra();
        sql += "          inner join associacaogruobjcomgrumat agm on agm.grupoobjetocompra_id = oc.grupoobjetocompra_id " +
            "             inner join grupomaterial gm on gm.id = agm.grupomaterial_id " +
            "         where to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(agm.iniciovigencia) and coalesce(trunc(agm.finalvigencia), to_date(:dataOperacao, 'dd/mm/yyyy'))" +
            "           and docEnt.doctofiscalliquidacao_id = :idDocumento " +
            "         group by gm.id, gmEnt.id) " +
            "         order by grupo_integrador ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDocumento", assistente.getDoctoFiscalLiquidacao().getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("idEmpenho", assistente.getEmpenho().getId());

        List<DocumentoFiscalIntegracaoGrupo> grupos = Lists.newArrayList();

        Map<DocumentoFiscalIntegracaoGrupo, Map<GrupoMaterial, BigDecimal>> mapGrupo = preencherMapGrupoIntegradorGrupoMaterial((List<Object[]>) q.getResultList());
        for (Map.Entry<DocumentoFiscalIntegracaoGrupo, Map<GrupoMaterial, BigDecimal>> entry : mapGrupo.entrySet()) {
            DocumentoFiscalIntegracaoGrupo grupoDocumento = entry.getKey();
            if (assistente.getIdProcessoOriginouEmpenho() != null) {
                grupoDocumento.setEmpenho(empenhoFacade.recuperarEmpenhoPorGrupoMaterialAndContrato(grupoDocumento.getGrupoMaterial(), assistente.getIdProcessoOriginouEmpenho()));
            }
            if (!docIntegracao.getIntegrador()) {
                docIntegracao.setIntegrador(grupoDocumento.getIntegrador());
            }
            atribuirDesdobramentoAoDocumentoFiscal(assistente, docIntegracao);
            grupoDocumento.getItens().addAll(buscarItensEntradaDocumentoFiscal(assistente, grupoDocumento));
            grupoDocumento.setValorGrupo(grupoDocumento.getValorGrupo().add(grupoDocumento.getValorTotalLancamento()));
            atribuirValorDocumentoIntegrador(assistente, docIntegracao, grupoDocumento);
            grupos.add(grupoDocumento);
        }
        return grupos;
    }

    private void atribuirValorDesdobramentoIntegrador(DocumentoFiscalIntegracaoAssistente assistente, DocumentoFiscalIntegracaoGrupo grupoDocumento) {
        if (assistente.getLancamentoNormal()) {
            Desdobramento desdobramento = assistente.getDesdobramento();
            desdobramento.setValor(desdobramento.getValor().add(grupoDocumento.getValorTotalALiquidarGrupo()));
            desdobramento.setSaldo(desdobramento.getValor());
        } else {
            DesdobramentoLiquidacaoEstorno desdobramento = assistente.getDesdobramentoEstorno();
            desdobramento.setValor(desdobramento.getValor().add(grupoDocumento.getValorTotalALiquidarGrupo()));
        }
    }

    private Map<DocumentoFiscalIntegracaoGrupo, Map<GrupoBem, BigDecimal>> preencherMapGrupoIntegradorGrupoBem(List<Object[]> resultList) {
        Map<DocumentoFiscalIntegracaoGrupo, Map<GrupoBem, BigDecimal>> mapGrupoIntegrador = new HashMap<>();
        for (Object[] obj : resultList) {
            DocumentoFiscalIntegracaoGrupo grupoVO = new DocumentoFiscalIntegracaoGrupo();
            grupoVO.setGrupoBem(em.find(GrupoBem.class, ((BigDecimal) obj[0]).longValue()));
            grupoVO.setGrupoBemDesdobramento(obj[1] != null ? em.find(GrupoBem.class, ((BigDecimal) obj[1]).longValue()) : null);
            grupoVO.setTipoGrupo(TipoGrupo.valueOf((String) obj[4]));
            grupoVO.setIntegrador(((BigDecimal) obj[5]).compareTo(BigDecimal.ONE) == 0);
            grupoVO.setTipoContaDespesa(TipoContaDespesa.BEM_MOVEL);

            if (!mapGrupoIntegrador.containsKey(grupoVO)) {
                mapGrupoIntegrador.put(grupoVO, new HashMap<>());
            }
            Map<GrupoBem, BigDecimal> mapGrupoItem = mapGrupoIntegrador.get(grupoVO);
            GrupoBem grupoBem = em.find(GrupoBem.class, ((BigDecimal) obj[2]).longValue());
            mapGrupoItem.put(grupoBem, (BigDecimal) obj[3]);
            mapGrupoIntegrador.put(grupoVO, mapGrupoItem);
        }
        return mapGrupoIntegrador;
    }

    private Map<DocumentoFiscalIntegracaoGrupo, Map<GrupoMaterial, BigDecimal>> preencherMapGrupoIntegradorGrupoMaterial(List<Object[]> resultList) {
        Map<DocumentoFiscalIntegracaoGrupo, Map<GrupoMaterial, BigDecimal>> mapGrupoIntegrador = new HashMap<>();
        for (Object[] obj : resultList) {
            DocumentoFiscalIntegracaoGrupo grupoVO = new DocumentoFiscalIntegracaoGrupo();
            grupoVO.setGrupoMaterial(em.find(GrupoMaterial.class, ((BigDecimal) obj[0]).longValue()));
            grupoVO.setGrupoMaterialDesdobramento(em.find(GrupoMaterial.class, ((BigDecimal) obj[1]).longValue()));
            grupoVO.setIntegrador(((BigDecimal) obj[4]).compareTo(BigDecimal.ONE) == 0);
            grupoVO.setTipoContaDespesa(TipoContaDespesa.BEM_ESTOQUE);

            if (!mapGrupoIntegrador.containsKey(grupoVO)) {
                mapGrupoIntegrador.put(grupoVO, new HashMap<>());
            }
            Map<GrupoMaterial, BigDecimal> mapGrupoItem = mapGrupoIntegrador.get(grupoVO);
            GrupoMaterial grupoItem = em.find(GrupoMaterial.class, ((BigDecimal) obj[2]).longValue());

            mapGrupoItem.put(grupoItem, (BigDecimal) obj[3]);
            mapGrupoIntegrador.put(grupoVO, mapGrupoItem);
        }
        return mapGrupoIntegrador;
    }

    public List<DocumentoFiscalIntegracaoGrupoItem> buscarItensAquisicaoDocumentoFiscal(DocumentoFiscalIntegracaoAssistente assistente, DocumentoFiscalIntegracaoGrupo grupoDocumento) {
        String sql = " Select ev.id                as id_item," +
            "                 bem.identificacao || ' - ' || case when length(bem.descricao) > 85 then substr(bem.descricao, 0,85) || '...' else bem.descricao end as bem, " +
            "                 gbBem.codigo || ' - ' || case when length(gbBem.descricao) > 85 then substr(gbBem.descricao, 0,85) || '...' else gbBem.descricao end as grupo, " +
            "                 ev.situacaoeventobem as situcao," +
            "                 ev.valordolancamento as valor_lancamento," +
            "                 est.valorliquidado   as valor_liquidado," +
            "                 bem.identificacao                  as identificao, " +
            "                 bem.descricao                      as descricao, " +
            "                 coalesce(round(itemDocFisc.quantidade * itemDocFisc.valorunitario, 2), 0) as total," +
            "                 bem.id                             as idBem, " +
            "                 gbBem.id                           as idGrupo " +
            "          From itemaquisicao itemAq " +
            "           Inner Join eventobem ev On ev.Id = itemAq.Id " +
            "           Inner Join bem on bem.id = ev.bem_id " +
            "           Inner Join estadobem est On ev.estadoResultante_id = est.id " +
            "           Inner Join grupobem gbBem on gbBem.id = est.grupobem_id " +
            "           Inner Join ItemSolicitacaoAquisicao itemSolAq On itemSolAq.id = itemAq.itemsolicitacaoaquisicao_id " +
            "           Inner Join Itemdoctoitemaquisicao associacao On associacao.id = itemSolAq.itemdoctoitemaquisicao_id " +
            "           Inner Join Itemdoctofiscalliquidacao itemDocFisc On itemDocFisc.id = associacao.itemdoctofiscalliquidacao_id " +
            "           Inner Join doctofiscalliquidacao doc On doc.Id = itemDocFisc.doctofiscalliquidacao_id " +
            "           inner join itemrequisicaodecompra irc on irc.id = associacao.itemrequisicaodecompra_id ";
        sql += getJoinItemProcessoObjetoCompra();
        sql += "    inner join grupoobjcompragrupobem agb on agb.grupoobjetocompra_id = oc.grupoobjetocompra_id " +
            "       inner join grupobem gb on gb.id = agb.grupobem_id " +
            "     where to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(agb.iniciovigencia) and coalesce(trunc(agb.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy'))" +
            "       and doc.id = :idDocumento " +
            "       and gb.id = :idGrupo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDocumento", assistente.getDoctoFiscalLiquidacao().getId());
        q.setParameter("idGrupo", grupoDocumento.getGrupoBem().getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        List resultList = q.getResultList();
        List<DocumentoFiscalIntegracaoGrupoItem> itens = Lists.newArrayList();
        if (!resultList.isEmpty()) {
            for (Object[] obj : (List<Object[]>) resultList) {
                DocumentoFiscalIntegracaoGrupoItem item = new DocumentoFiscalIntegracaoGrupoItem();
                item.setDocumentoFiscalIntegracaoGrupo(grupoDocumento);
                item.setIdItem(((BigDecimal) obj[0]).longValue());
                item.setDescricaoItem((String) obj[1]);
                item.setGrupo((String) obj[2]);
                item.setSituacao(SituacaoEventoBem.valueOf((String) obj[3]).getDescricao());
                item.setValorLancamento((BigDecimal) obj[4]);
                item.setValorLiquidado((BigDecimal) obj[5]);
                item.setNumeroItem((String) obj[6]);
                item.setDescricaoItem((String) obj[7]);
                item.setValorTotal((BigDecimal) obj[8]);
                item.setIdBem(((BigDecimal) obj[9]).longValue());
                item.setIdGrupo(((BigDecimal) obj[10]).longValue());
                itens.add(item);
            }
        }
        return itens;
    }

    public List<DocumentoFiscalIntegracaoGrupoItem> buscarItensEntradaDocumentoFiscal(DocumentoFiscalIntegracaoAssistente assistente, DocumentoFiscalIntegracaoGrupo grupoDocumento) {
        String sql = " select " +
            "             itemDoc.id                    as id_item," +
            "             item.numeroitem               as numero, " +
            "             mat.codigo || ' - ' || case when length(mat.descricao) > 85 then substr(mat.descricao, 0,85) || '...' else mat.descricao end as bem, " +
            "             gmEnt.codigo || ' - ' || case when length(gmEnt.descricao) > 85 then substr(gmEnt.descricao, 0,85) || '...' else gmEnt.descricao end as grupo, " +
            "             itemDoc.situacao              as situcao," +
            "             itemDoc.valorliquidado        as valor_liquidado, " +
            "             itemDoc.quantidade            as quantidade, " +
            "             item.valorunitario            as valor_unitario," +
            "             item.entradamaterial_id       as id_entrada, " +
            "             gmEnt.id                      as idGrupo " +
            "     from itementradamaterial item " +
            "              inner join itemcompramaterial icm on icm.itementradamaterial_id = item.id " +
            "              inner join itemdoctoitementrada itemDoc on itemDoc.itementradamaterial_id = item.id " +
            "              inner join doctofiscalentradacompra docEnt on docEnt.id = itemDoc.doctofiscalentradacompra_id " +
            "              inner join material mat on mat.id = item.material_id " +
            "              inner join grupomaterial gmEnt on gmEnt.id = mat.grupo_id " +
            "              inner join itemrequisicaodecompra irc on irc.id = icm.itemrequisicaodecompra_id ";
        sql += getJoinItemProcessoObjetoCompra();
        sql += "           inner join associacaogruobjcomgrumat agm on agm.grupoobjetocompra_id = oc.grupoobjetocompra_id " +
            "              inner join grupomaterial gm on gm.id = agm.grupomaterial_id " +
            "     where to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(agm.iniciovigencia) and coalesce(trunc(agm.finalvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            "       and docEnt.doctofiscalliquidacao_id = :idDocumento " +
            "       and gm.id = :idGrupo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDocumento", assistente.getDoctoFiscalLiquidacao().getId());
        q.setParameter("idGrupo", grupoDocumento.getGrupoMaterial().getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        List resultList = q.getResultList();
        List<DocumentoFiscalIntegracaoGrupoItem> itens = Lists.newArrayList();
        if (!resultList.isEmpty()) {
            for (Object[] obj : (List<Object[]>) resultList) {
                DocumentoFiscalIntegracaoGrupoItem item = new DocumentoFiscalIntegracaoGrupoItem();
                item.setDocumentoFiscalIntegracaoGrupo(grupoDocumento);
                item.setIdItem(((BigDecimal) obj[0]).longValue());
                ItemDoctoItemEntrada itemDoc = em.find(ItemDoctoItemEntrada.class, item.getIdItem());
                item.setItemDoctoItemEntrada(itemDoc);
                item.setNumeroItem(((BigDecimal) obj[1]).toString());
                item.setDescricaoItem((String) obj[2]);
                item.setGrupo((String) obj[3]);
                item.setSituacao(SituacaoDocumentoFiscalEntradaMaterial.valueOf((String) obj[4]).getDescricao());
                item.setValorLiquidado((BigDecimal) obj[5]);
                item.setQuantidade((BigDecimal) obj[6]);
                item.setValorUnitario((BigDecimal) obj[7]);
                item.setIdGrupo(((BigDecimal) obj[9]).longValue());
                item.setValorLancamento(itemDoc.getValorTotal());
                item.setValorTotal(itemDoc.getValorTotal());
                itens.add(item);
            }
        }
        return itens;
    }

    public void liquidarDocumentoFiscalIntegracao(Liquidacao entity, List<DocumentoFiscalIntegracao> documentos) {
        if (entity.isEmpenhoIntegracaoContratoOrReconhecimentoDividaOrAta() && !Util.isListNullOrEmpty(documentos)) {
            switch (entity.getEmpenho().getTipoContaDespesa()) {
                case BEM_MOVEL:
                    liquidarItemAquisicaoDocumentoFiscal(entity, documentos);
                    break;
                case BEM_ESTOQUE:
                    liquidarItemEntradaDocumentoFiscal(documentos);
                    liquidarEntradaDocumentoFiscal(entity);
                    break;
            }
        }
    }

    public void liquidarItemAquisicaoDocumentoFiscal(Liquidacao liquidacao, List<DocumentoFiscalIntegracao> documentosIntegracao) {
        List<Number> bensDesbloqueio = Lists.newArrayList();

        documentosIntegracao.stream().filter(DocumentoFiscalIntegracao::getSelecionado).forEach(docInt -> {
            docInt.getGrupos().stream().filter(DocumentoFiscalIntegracaoGrupo::getIntegrador).forEach(grupoInt -> {
                grupoInt.getItens().forEach(itemInteg -> {
                    ItemAquisicao itemAquisicao = em.find(ItemAquisicao.class, itemInteg.getIdItem());
                    Bem bem = itemAquisicao.getBem();
                    if (bem.getDataAquisicao() == null) {
                        jdbcIntegracaoDocumentoFiscalLiquidacao.updateDataAquisicaoBem(liquidacao.getDataLiquidacao(), bem);
                    }
                    if (itemAquisicao.getItemSolicitacaoAquisicao().getItemDoctoItemAquisicao().getItemDoctoFiscalLiquidacao().getDoctoFiscalLiquidacao().equals(docInt.getDoctoFiscalLiquidacao())) {
                        EstadoBem ultimoEstadoDoBem = itemAquisicao.getEstadoResultante();
                        BigDecimal valorLiquidado = ultimoEstadoDoBem.getValorLiquidado().add(itemInteg.getValorALiquidar());
                        jdbcIntegracaoDocumentoFiscalLiquidacao.updateValorLiquidadoEstadoBem(valorLiquidado, ultimoEstadoDoBem);

                        if (valorLiquidado.compareTo(itemAquisicao.getValorDoLancamento()) == 0) {
                            jdbcIntegracaoDocumentoFiscalLiquidacao.updateEventoBem(SituacaoEventoBem.FINALIZADO, itemAquisicao);

                            jdbcIntegracaoDocumentoFiscalLiquidacao.insertBemNotaFiscal(bem, docInt.getDoctoFiscalLiquidacao(), liquidacao);

                            jdbcIntegracaoDocumentoFiscalLiquidacao.insertBemOrigemRecurso(docInt.getDesdobramento().getConta(), bem, liquidacao);
                            bensDesbloqueio.add(bem.getId());
                        }
                    }
                });
                if (!Util.isListNullOrEmpty(bensDesbloqueio)) {
                    ConfigMovimentacaoBem config = configMovimentacaoBemFacade.buscarConfiguracaoMovimentacaoBem(liquidacao.getDataLiquidacao(), OperacaoMovimentacaoBem.AQUISICAO_BEM);
                    configMovimentacaoBemFacade.desbloquearBensJDBC(config, bensDesbloqueio);
                }
            });
        });
    }

    public void liquidarItemEntradaDocumentoFiscal(List<DocumentoFiscalIntegracao> documentosIntegracao) {
        documentosIntegracao.stream().filter(DocumentoFiscalIntegracao::getSelecionado).forEach(docInt -> {
            docInt.getGrupos().stream().filter(DocumentoFiscalIntegracaoGrupo::getIntegrador).forEach(grupoInt -> {
                grupoInt.getItens().forEach(itemInteg -> {
                    ItemDoctoItemEntrada itemDocto = itemInteg.getItemDoctoItemEntrada();
                    BigDecimal valorLiquidado = itemDocto.getValorLiquidado().add(itemInteg.getValorALiquidar());

                    boolean isLiquidado = valorLiquidado.compareTo(itemDocto.getValorTotal()) == 0;
                    SituacaoDocumentoFiscalEntradaMaterial situacao = isLiquidado ? SituacaoDocumentoFiscalEntradaMaterial.LIQUIDADO : SituacaoDocumentoFiscalEntradaMaterial.LIQUIDADO_PARCIALMENTE;

                    jdbcIntegracaoDocumentoFiscalLiquidacao.updateItemDoctoItemEntrada(valorLiquidado, situacao, itemDocto);
                });
            });
        });
    }

    public void liquidarEntradaDocumentoFiscal(Liquidacao liquidacao) {
        Set<EntradaCompraMaterial> entradasSet = Sets.newHashSet();
        for (LiquidacaoDoctoFiscal ldf : liquidacao.getDoctoFiscais()) {
            DoctoFiscalEntradaCompra doctoEnt = entradaMaterialFacade.recuperarDoctoFiscalEntradaCompra(ldf.getDoctoFiscalLiquidacao());
            if (doctoEnt == null || doctoEnt.getEntradaCompraMaterial() == null) {
                throw new ExcecaoNegocioGenerica("Não foi possível liquidar o documento fiscal de integração com o sistema de materiais.");
            }
            BigDecimal valorLiquidadoDocumento = doctoFiscalLiquidacaoFacade.buscarValorLiquidado(ldf.getDoctoFiscalLiquidacao());
            valorLiquidadoDocumento = valorLiquidadoDocumento.add(ldf.getValorLiquidado());

            boolean isLiquidado = valorLiquidadoDocumento.compareTo(doctoEnt.getDoctoFiscalLiquidacao().getTotal()) == 0;
            SituacaoDocumentoFiscalEntradaMaterial situacao = isLiquidado ? SituacaoDocumentoFiscalEntradaMaterial.LIQUIDADO : SituacaoDocumentoFiscalEntradaMaterial.LIQUIDADO_PARCIALMENTE;
            jdbcIntegracaoDocumentoFiscalLiquidacao.updateDoctoFiscalEntradaCompra(situacao, doctoEnt);

            entradasSet.add(doctoEnt.getEntradaCompraMaterial());
        }

        for (EntradaCompraMaterial entrada : Lists.newArrayList(entradasSet)) {
            List<SituacaoDocumentoFiscalEntradaMaterial> situacoesDocumentos = entradaMaterialFacade.buscarSituacoesDocumentoEntrada(entrada.getId());
            if (situacoesDocumentos.size() == 1
                && situacoesDocumentos.get(0).isLiquidado()
                && entrada.getSituacao().isAtestoAguardandoLiquidacao()) {
                try {
                    entrada.setDataConclusao(DataUtil.getDataComHoraAtual(liquidacao.getDataLiquidacao()));
                    entradaMaterialFacade.concluirEntradaPorCompra(entrada);
                    entradaMaterialFacade.salvarGuiaDistribuicaoMaterial(entrada);
                } catch (OperacaoEstoqueException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void estornarDocumentoFiscalIntegracao(Liquidacao liquidacao, LiquidacaoEstorno entity, List<DocumentoFiscalIntegracao> documentosIntegracao) {
        if (liquidacao.isEmpenhoIntegracaoContratoOrReconhecimentoDividaOrAta()) {
            switch (entity.getLiquidacao().getEmpenho().getTipoContaDespesa()) {
                case BEM_MOVEL:
                    estornarLiquidacaoItemAquisicaoDocumentoFiscal(entity, documentosIntegracao);
                    break;
                case BEM_ESTOQUE:
                    estornarLiquidacaoItemEntradaDocumentoFiscal(documentosIntegracao);
                    estornarLiquidacaoEntradaDocumentoFiscal(entity);
                    break;
            }
        }
    }

    public void estornarLiquidacaoItemAquisicaoDocumentoFiscal(LiquidacaoEstorno entity, List<DocumentoFiscalIntegracao> documentosIntegracao) {
        documentosIntegracao.forEach(doc -> {
            doc.getGrupos().forEach(grupo -> {
                List<Number> bensParaBloqueio = Lists.newArrayList();
                grupo.getItens().forEach(item -> {
                    ItemAquisicao itemAquisicao = em.find(ItemAquisicao.class, item.getIdItem());
                    EventoBem ultimoEvento = bemFacade.recuperarUltimoEventoBem(itemAquisicao.getBem());
                    if (!ultimoEvento.equals(itemAquisicao)) {
                        throw new ExcecaoNegocioGenerica("O documento fiscal não poderá ser estornado. O bem já está em utilização na unidade organizacional " + ultimoEvento.getEstadoResultante().getDetentoraAdministrativa());
                    }
                    EstadoBem ultimoEstadoDoBem = itemAquisicao.getEstadoResultante();
                    BigDecimal valorLiquidado = ultimoEstadoDoBem.getValorLiquidado().subtract(item.getValorALiquidar());
                    jdbcIntegracaoDocumentoFiscalLiquidacao.updateValorLiquidadoEstadoBem(valorLiquidado, ultimoEstadoDoBem);

                    itemAquisicao.setSituacaoEventoBem(SituacaoEventoBem.AGUARDANDO_LIQUIDACAO);
                    jdbcIntegracaoDocumentoFiscalLiquidacao.updateEventoBem(SituacaoEventoBem.AGUARDANDO_LIQUIDACAO, itemAquisicao);

                    bensParaBloqueio.add(itemAquisicao.getBem().getId());
                });
                if (!Util.isListNullOrEmpty(bensParaBloqueio)) {
                    ConfigMovimentacaoBem config = configMovimentacaoBemFacade.buscarConfiguracaoMovimentacaoBem(entity.getDataEstorno(), OperacaoMovimentacaoBem.AQUISICAO_BEM);
                    configMovimentacaoBemFacade.bloquearBensPorSituacaoJDBC(config, bensParaBloqueio, SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
                }
            });
        });
    }

    public void estornarLiquidacaoItemEntradaDocumentoFiscal(List<DocumentoFiscalIntegracao> documentosIntegracao) {
        documentosIntegracao.forEach(doc -> {
            doc.getGrupos().forEach(grupo -> {
                grupo.getItens().forEach(item -> {
                    ItemDoctoItemEntrada itemDocto = em.find(ItemDoctoItemEntrada.class, item.getIdItem());
                    BigDecimal valorLiquidado = itemDocto.getValorLiquidado().subtract(item.getValorALiquidar());

                    boolean isEstornado = valorLiquidado.compareTo(BigDecimal.ZERO) == 0;
                    SituacaoDocumentoFiscalEntradaMaterial situacao = isEstornado ? SituacaoDocumentoFiscalEntradaMaterial.ESTORNADO : SituacaoDocumentoFiscalEntradaMaterial.LIQUIDADO_PARCIALMENTE;

                    jdbcIntegracaoDocumentoFiscalLiquidacao.updateItemDoctoItemEntrada(valorLiquidado, situacao, itemDocto);
                });
            });
        });
    }

    public void estornarLiquidacaoEntradaDocumentoFiscal(LiquidacaoEstorno liquidacaoEstorno) {
        Set<EntradaCompraMaterial> entradasSet = Sets.newHashSet();
        for (LiquidacaoEstornoDoctoFiscal docEst : liquidacaoEstorno.getDocumentosFiscais()) {
            DoctoFiscalEntradaCompra doctoEnt = entradaMaterialFacade.recuperarDoctoFiscalEntradaCompra(docEst.getDocumentoFiscal());
            if (doctoEnt == null || doctoEnt.getEntradaCompraMaterial() == null) {
                throw new ExcecaoNegocioGenerica("Não foi possível estornar o documento fiscal de integração com o sistema de materiais.");
            }
            BigDecimal saldoDoctoFiscal = doctoFiscalLiquidacaoFacade.buscarValorLiquidado(docEst.getDocumentoFiscal());
            saldoDoctoFiscal = saldoDoctoFiscal.subtract(docEst.getValor());

            boolean isEstornado = saldoDoctoFiscal.compareTo(BigDecimal.ZERO) == 0;
            SituacaoDocumentoFiscalEntradaMaterial situacaoDocto = isEstornado ? SituacaoDocumentoFiscalEntradaMaterial.ESTORNADO : SituacaoDocumentoFiscalEntradaMaterial.LIQUIDADO_PARCIALMENTE;
            jdbcIntegracaoDocumentoFiscalLiquidacao.updateDoctoFiscalEntradaCompra(situacaoDocto, doctoEnt);

            entradasSet.add(doctoEnt.getEntradaCompraMaterial());
        }
        for (EntradaCompraMaterial entrada : Lists.newArrayList(entradasSet)) {
            List<SituacaoDocumentoFiscalEntradaMaterial> situacoesDocumentos = entradaMaterialFacade.buscarSituacoesDocumentoEntrada(entrada.getId());
            if (situacoesDocumentos.size() == 1 && situacoesDocumentos.get(0).isEstornado()) {
                entrada.setDataEstorno(DataUtil.getDataComHoraAtual(liquidacaoEstorno.getDataEstorno()));
                entradaMaterialFacade.estornarEntradaPorCompra(entrada);
            }
        }
    }

    public DocumentoFiscalIntegracao buscarDadosDocumentoAquisicaoBemMovel(DocumentoFiscalIntegracaoAssistente filtro) {
        String sql =
            "    select 'Aquisição de Bem Móvel'           as origem, " +
                "       aq.NUMERO || ' - ' || to_char(aq.DATADEAQUISICAO, 'dd/MM/yyyy') || ' - ' || req.DESCRICAO as descricao, " +
                "       vw.codigo || ' - ' || vw.descricao as unidade," +
                "       aq.id                              as idOrigem " +
                " from aquisicao aq " +
                "         inner join solicitacaoaquisicao sol on sol.id = aq.solicitacaoaquisicao_id " +
                "         inner join requisicaodecompra req on req.id = sol.requisicaodecompra_id " +
                "         inner join doctofiscalsolicaquisicao dfa on dfa.solicitacaoaquisicao_id = sol.id " +
                "         left join contrato c on c.id = req.contrato_id " +
                "         left join unidadecontrato uc on uc.contrato_id = c.id " +
                "            and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " +
                "         left join reconhecimentodivida rc on rc.id = req.reconhecimentodivida_id " +
                "         left join requisicaocompraexecucao rce on rce.requisicaocompra_id = req.id " +
                "         left join execucaoprocesso exproc on exproc.id = rce.execucaoprocesso_id " +
                "         left join execucaoprocessoata exata on exata.execucaoprocesso_id = exproc.id " +
                "         left join ataregistropreco ata on ata.id = exata.ataregistropreco_id " +
                "         left join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = exproc.id " +
                "         left join dispensadelicitacao disp on disp.id = exdisp.dispensalicitacao_id " +
                "         left join processodecompra pc on pc.id = disp.processodecompra_id " +
                "         inner join vwhierarquiaadministrativa vw on vw.subordinada_id = coalesce(uc.unidadeadministrativa_id, rc.unidadeadministrativa_id, ata.unidadeorganizacional_id, pc.unidadeorganizacional_id)  " +
                "    where to_date(:dataOperacao, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) " +
                "    and dfa.documentofiscal_id = :idDocumento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDocumento", filtro.getDoctoFiscalLiquidacao().getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            DocumentoFiscalIntegracao doc = new DocumentoFiscalIntegracao();
            for (Object[] obj : (List<Object[]>) resultList) {
                doc.setOrigem((String) obj[0]);
                doc.setDescricao((String) obj[1]);
                doc.setUnidadeAdministrativa((String) obj[2]);
                doc.setIdOrigem(((Number) obj[3]).longValue());
                doc.setTipoContaDespesa(TipoContaDespesa.BEM_MOVEL);
            }
            return doc;
        }
        return null;
    }

    public DocumentoFiscalIntegracao buscarDadosDocumentoEntradaPorCompra(DocumentoFiscalIntegracaoAssistente filtro) {
        String sql =
            "    select 'Entrada por Compra'               as origem, " +
                "       ent.NUMERO || ' - ' || to_char(ent.DATAENTRADA, 'dd/MM/yyyy') || ' - ' || ent.HISTORICO as descricao, " +
                "       vw.codigo || ' - ' || vw.descricao as unidade, " +
                "       ent.id                             as idOrigem " +
                "from entradamaterial ent " +
                "         inner join entradacompramaterial ecm on ecm.id = ent.id " +
                "         inner join doctofiscalentradacompra dotEnt on dotEnt.entradaCompraMaterial_id = ecm.id " +
                "         inner join requisicaodecompra req on req.id = ecm.requisicaodecompra_id " +
                "         left join contrato c on c.id = req.contrato_id " +
                "         left join unidadecontrato uc on uc.contrato_id = c.id " +
                "            and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " +
                "         left join reconhecimentodivida rc on rc.id = req.reconhecimentodivida_id " +
                "         left join requisicaocompraexecucao rce on rce.requisicaocompra_id = req.id " +
                "         left join execucaoprocesso exproc on exproc.id = rce.execucaoprocesso_id " +
                "         left join execucaoprocessoata exata on exata.execucaoprocesso_id = exproc.id " +
                "         left join ataregistropreco ata on ata.id = exata.ataregistropreco_id " +
                "         left join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = exproc.id " +
                "         left join dispensadelicitacao disp on disp.id = exdisp.dispensalicitacao_id " +
                "         left join processodecompra pc on pc.id = disp.processodecompra_id " +
                "         inner join vwhierarquiaadministrativa vw on vw.subordinada_id = coalesce(uc.unidadeadministrativa_id, rc.unidadeadministrativa_id, ata.unidadeorganizacional_id, pc.unidadeorganizacional_id)  " +
                "  where to_date(:dataOperacao, 'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) " +
                "  and dotEnt.doctofiscalliquidacao_id = :idDocumento";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDocumento", filtro.getDoctoFiscalLiquidacao().getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            DocumentoFiscalIntegracao doc = new DocumentoFiscalIntegracao();
            for (Object[] obj : (List<Object[]>) resultList) {
                doc.setOrigem((String) obj[0]);
                doc.setDescricao((String) obj[1]);
                doc.setUnidadeAdministrativa((String) obj[2]);
                doc.setIdOrigem(((Number) obj[3]).longValue());
                doc.setTipoContaDespesa(TipoContaDespesa.BEM_ESTOQUE);
            }
            return doc;
        }
        return null;
    }

    private static String getJoinItemProcessoObjetoCompra() {
        return "" +
            "   left join itemreconhecimentodivida itemrd on itemrd.id = irc.itemreconhecimentodivida_id " +
            "   left join execucaoprocessoitem itemexproc on itemexproc.id = irc.execucaoprocessoitem_id " +
            "   left join itemcontrato ic on ic.id = irc.itemcontrato_id " +
            "   left join itemcontratovigente icv on icv.itemcontrato_id = ic.id " +
            "   left join itemcotacao icot on icot.id = icv.itemcotacao_id " +
            "   left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
            "   left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
            "   left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
            "   left join itemcontratoitemsolext icise on icise.itemcontrato_id = ic.id " +
            "   left join itemcontratoitempropdisp icipfd on icipfd.itemcontrato_id = ic.id " +
            "   left join itempropostafornedisp ipfd on icipfd.itempropfornecdispensa_id = ipfd.id " +
            "   left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
            "   left join itemprocessodecompra ipc on ipc.id = coalesce(itemexproc.itemprocessocompra_id, ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) " +
            "   left join itemsolicitacao itemsol on itemsol.id = ipc.itemsolicitacaomaterial_id " +
            "   left join itemsolicitacaoexterno ise on ise.id = icise.itemsolicitacaoexterno_id " +
            "   inner join objetocompra oc on oc.id = coalesce(itemrd.objetocompra_id, ic.objetocompracontrato_id, itemsol.objetocompra_id, ise.objetocompra_id, icot.objetocompra_id) ";
    }

    public List<LiquidacaoDoctoFiscalTransferenciaBens> criarTransferenciasBensMoveisNormal(DocumentoFiscalIntegracaoAssistente assistente, DocumentoFiscalIntegracao documentosIntegracao) {
        List<LiquidacaoDoctoFiscalTransferenciaBens> retorno = Lists.newArrayList();
        if (assistente.getLancamentoNormal()) {
            HashMap<Long, TransfBensMoveis> mapTransferenciaPorGrupo = montarMapTransferenciaPorGrupoBensMoveis(assistente, documentosIntegracao);
            if (!mapTransferenciaPorGrupo.isEmpty()) {
                for (Map.Entry<Long, TransfBensMoveis> mapTransferencia : mapTransferenciaPorGrupo.entrySet()) {
                    TransfBensMoveis novaTransferencia = mapTransferencia.getValue();
                    retorno.add(new LiquidacaoDoctoFiscalTransferenciaBens(novaTransferencia));
                }
            }
        }
        return retorno;
    }

    public List<EstornoLiquidacaoDoctoFiscalTransferenciaBens> criarTransferenciasBensMoveisEstorno(DocumentoFiscalIntegracaoAssistente assistente, DocumentoFiscalIntegracao documentosIntegracao) {
        List<EstornoLiquidacaoDoctoFiscalTransferenciaBens> retorno = Lists.newArrayList();
        if (!assistente.getLancamentoNormal()) {
            HashMap<Long, TransfBensMoveis> mapTransferenciaPorGrupo = montarMapTransferenciaPorGrupoBensMoveis(assistente, documentosIntegracao);
            if (!mapTransferenciaPorGrupo.isEmpty()) {
                for (Map.Entry<Long, TransfBensMoveis> mapTransferencia : mapTransferenciaPorGrupo.entrySet()) {
                    TransfBensMoveis novaTransferencia = mapTransferencia.getValue();
                    retorno.add(new EstornoLiquidacaoDoctoFiscalTransferenciaBens(novaTransferencia));
                }
            }
        }
        return retorno;
    }

    private HashMap<Long, TransfBensMoveis> montarMapTransferenciaPorGrupoBensMoveis(DocumentoFiscalIntegracaoAssistente assistente, DocumentoFiscalIntegracao documentosIntegracao) {
        HashMap<Long, TransfBensMoveis> mapTransferenciaPorGrupo = new HashMap<>();
        documentosIntegracao.getGrupos().stream().filter(DocumentoFiscalIntegracaoGrupo::getIntegrador).forEach(grupoInt -> {
            grupoInt.getItens().forEach(itemInteg -> {
                if (!grupoInt.getGrupoBemDesdobramento().getId().equals(itemInteg.getIdGrupo())) {
                    if (!mapTransferenciaPorGrupo.containsKey(itemInteg.getIdGrupo())) {
                        TransfBensMoveis novaTransferencia = criarTransfBensMoveis(assistente, grupoInt, itemInteg);
                        mapTransferenciaPorGrupo.put(itemInteg.getIdGrupo(), novaTransferencia);
                    } else {
                        BigDecimal valorAtual = mapTransferenciaPorGrupo.get(itemInteg.getIdGrupo()).getValor();
                        mapTransferenciaPorGrupo.get(itemInteg.getIdGrupo()).setValor(valorAtual.add(itemInteg.getValorALiquidar()));
                    }
                }
            });
        });
        return mapTransferenciaPorGrupo;
    }

    public TransfBensMoveis criarTransfBensMoveis(DocumentoFiscalIntegracaoAssistente assistente,
                                                  DocumentoFiscalIntegracaoGrupo grupoOriginal,
                                                  DocumentoFiscalIntegracaoGrupoItem grupoDesdobrado) {
        TransfBensMoveis novaTransferencia = new TransfBensMoveis();
        novaTransferencia.setDataTransferencia(assistente.getData());
        novaTransferencia.setTipoLancamento(assistente.getLancamentoNormal() ? TipoLancamento.NORMAL : TipoLancamento.ESTORNO);
        novaTransferencia.setValor(grupoDesdobrado.getValorALiquidar());
        novaTransferencia.setExercicio(assistente.getExercicio());
        novaTransferencia.setUnidadeOrigem(assistente.getUnidadeOrganizacional());
        novaTransferencia.setUnidadeDestino(assistente.getUnidadeOrganizacional());

        if (assistente.getLancamentoNormal()) {
            novaTransferencia.setGrupoBemOrigem(grupoOriginal.getGrupoBemDesdobramento());
            novaTransferencia.setGrupoBemDestino(em.find(GrupoBem.class, grupoDesdobrado.getIdGrupo()));
        } else {
            novaTransferencia.setGrupoBemOrigem(em.find(GrupoBem.class, grupoDesdobrado.getIdGrupo()));
            novaTransferencia.setGrupoBemDestino(grupoOriginal.getGrupoBemDesdobramento());
        }

        novaTransferencia.setTipoGrupoOrigem(TipoGrupo.BEM_MOVEL_PRINCIPAL);
        novaTransferencia.setTipoGrupoDestino(TipoGrupo.BEM_MOVEL_PRINCIPAL);
        novaTransferencia.setOperacaoOrigem(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_CONCEDIDA);
        novaTransferencia.setOperacaoDestino(TipoOperacaoBensMoveis.TRANFERENCIA_BENS_MOVEIS_RECEBIDA);
        novaTransferencia.setEventoContabilOrigem(
            transfBensMoveisFacade.getConfigTransfBensMoveisFacade().recuperarConfiguracaoEvento(
                novaTransferencia.getTipoLancamento(),
                novaTransferencia.getOperacaoOrigem(),
                novaTransferencia.getDataTransferencia()
            ).getEventoContabil()
        );
        novaTransferencia.setEventoContabilDestino(
            transfBensMoveisFacade.getConfigTransfBensMoveisFacade().recuperarConfiguracaoEvento(
                novaTransferencia.getTipoLancamento(),
                novaTransferencia.getOperacaoDestino(),
                novaTransferencia.getDataTransferencia()
            ).getEventoContabil()
        );
        return novaTransferencia;
    }

    private List<LiquidacaoDoctoFiscalTransferenciaBens> criarTransferenciasBensEstoqueNormal(DocumentoFiscalIntegracaoAssistente assistente, DocumentoFiscalIntegracao documentosIntegracao) {
        List<LiquidacaoDoctoFiscalTransferenciaBens> retorno = Lists.newArrayList();
        if (assistente.getLancamentoNormal()) {
            HashMap<Long, TransferenciaBensEstoque> mapTransferenciaPorGrupo = montarMapTransferenciaPorGrupoBensEstoque(assistente, documentosIntegracao);
            if (!mapTransferenciaPorGrupo.isEmpty()) {
                for (Map.Entry<Long, TransferenciaBensEstoque> mapTransferencia : mapTransferenciaPorGrupo.entrySet()) {
                    TransferenciaBensEstoque novaTransferencia = mapTransferencia.getValue();
                    retorno.add(new LiquidacaoDoctoFiscalTransferenciaBens(novaTransferencia));
                }
            }
        }
        return retorno;
    }

    private List<EstornoLiquidacaoDoctoFiscalTransferenciaBens> criarTransferenciasBensEstoqueEstorno(DocumentoFiscalIntegracaoAssistente assistente, DocumentoFiscalIntegracao documentosIntegracao) {
        List<EstornoLiquidacaoDoctoFiscalTransferenciaBens> retorno = Lists.newArrayList();
        if (!assistente.getLancamentoNormal()) {
            HashMap<Long, TransferenciaBensEstoque> mapTransferenciaPorGrupo = montarMapTransferenciaPorGrupoBensEstoque(assistente, documentosIntegracao);
            if (!mapTransferenciaPorGrupo.isEmpty()) {
                for (Map.Entry<Long, TransferenciaBensEstoque> mapTransferencia : mapTransferenciaPorGrupo.entrySet()) {
                    TransferenciaBensEstoque novaTransferencia = mapTransferencia.getValue();
                    retorno.add(new EstornoLiquidacaoDoctoFiscalTransferenciaBens(novaTransferencia));
                }
            }
        }
        return retorno;
    }

    private HashMap<Long, TransferenciaBensEstoque> montarMapTransferenciaPorGrupoBensEstoque(DocumentoFiscalIntegracaoAssistente assistente, DocumentoFiscalIntegracao documentosIntegracao) {
        HashMap<Long, TransferenciaBensEstoque> mapTransferenciaPorGrupo = new HashMap<>();
        documentosIntegracao.getGrupos().stream().filter(DocumentoFiscalIntegracaoGrupo::getIntegrador).forEach(grupoInt -> {
            grupoInt.getItens().forEach(itemInteg -> {
                if (!grupoInt.getGrupoMaterialDesdobramento().getId().equals(itemInteg.getIdGrupo())) {
                    if (!mapTransferenciaPorGrupo.containsKey(itemInteg.getIdGrupo())) {
                        TransferenciaBensEstoque novaTransferencia = criarTransfBensEstoque(assistente, grupoInt, itemInteg);
                        mapTransferenciaPorGrupo.put(itemInteg.getIdGrupo(), novaTransferencia);
                    } else {
                        BigDecimal valorAtual = mapTransferenciaPorGrupo.get(itemInteg.getIdGrupo()).getValor();
                        mapTransferenciaPorGrupo.get(itemInteg.getIdGrupo()).setValor(valorAtual.add(itemInteg.getValorALiquidar()));
                    }
                }
            });
        });
        return mapTransferenciaPorGrupo;
    }

    public TransferenciaBensEstoque criarTransfBensEstoque(DocumentoFiscalIntegracaoAssistente assistente, DocumentoFiscalIntegracaoGrupo grupoOriginal, DocumentoFiscalIntegracaoGrupoItem grupoDesdobrado) {
        TransferenciaBensEstoque novaTransferencia = new TransferenciaBensEstoque();
        novaTransferencia.setDataTransferencia(assistente.getData());
        novaTransferencia.setExercicio(assistente.getExercicio());
        novaTransferencia.setTipoLancamento(assistente.getLancamentoNormal() ? TipoLancamento.NORMAL : TipoLancamento.ESTORNO);
        novaTransferencia.setValor(grupoDesdobrado.getValorALiquidar());
        novaTransferencia.setTipoTransferencia(TipoTransferenciaBensEstoque.TRANSFERENCIA);
        novaTransferencia.setUnidadeOrgOrigem(assistente.getUnidadeOrganizacional());
        novaTransferencia.setUnidadeOrgDestino(assistente.getUnidadeOrganizacional());

        if (assistente.getLancamentoNormal()) {
            novaTransferencia.setGrupoMaterial(grupoOriginal.getGrupoMaterialDesdobramento());
            novaTransferencia.setGrupoMaterialDestino(em.find(GrupoMaterial.class, grupoDesdobrado.getIdGrupo()));
        } else {
            novaTransferencia.setGrupoMaterial(em.find(GrupoMaterial.class, grupoDesdobrado.getIdGrupo()));
            novaTransferencia.setGrupoMaterialDestino(grupoOriginal.getGrupoMaterialDesdobramento());
        }

        novaTransferencia.setTipoEstoqueOrigem(TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO);
        novaTransferencia.setTipoEstoqueDestino(TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO);
        novaTransferencia.setOperacaoOrigem(TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_CONCEDIDA);
        novaTransferencia.setOperacaoDestino(TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_RECEBIDA);
        novaTransferencia.setEventoContabilOrigem(
            transferenciaBensEstoqueFacade.getConfigTransfEstoqueFacade().recuperarConfiguracaoEvento(
                novaTransferencia.getTipoLancamento(),
                novaTransferencia.getOperacaoOrigem(),
                novaTransferencia.getDataTransferencia()
            ).getEventoContabil()
        );
        novaTransferencia.setEventoContabilDestino(
            transferenciaBensEstoqueFacade.getConfigTransfEstoqueFacade().recuperarConfiguracaoEvento(
                novaTransferencia.getTipoLancamento(),
                novaTransferencia.getOperacaoDestino(),
                novaTransferencia.getDataTransferencia()
            ).getEventoContabil()
        );
        return novaTransferencia;
    }

    private void salvarTransferenciaBensMoveisGerandoAssossiativa(Liquidacao liquidacao) {
        liquidacao.getDoctoFiscais().forEach(documentoFiscal -> {
            documentoFiscal.getTransferenciasBens().forEach(assassosiativa -> {
                assassosiativa.getTransfBensMoveis().setHistorico("Gerado automaticamente ao salvar a liquidação " + liquidacao.toString());
                TransfBensMoveis transferenciaSalva = transfBensMoveisFacade.salvarTransferencia(assassosiativa.getTransfBensMoveis());
                assassosiativa.setIdTransferencia(transferenciaSalva.getId());
            });
        });
    }

    private void salvarTransferenciaBensMoveisGerandoAssossiativa(LiquidacaoEstorno liquidacaoEstorno) {
        liquidacaoEstorno.getDocumentosFiscais().forEach(documentoFiscal -> {
            documentoFiscal.getTransferenciasBens().forEach(assassosiativa -> {
                assassosiativa.getTransfBensMoveis().setHistorico("Gerado automaticamente ao salvar o estorno de liquidação " + liquidacaoEstorno.toString());
                TransfBensMoveis transferenciaSalva = transfBensMoveisFacade.salvarTransferencia(assassosiativa.getTransfBensMoveis());
                assassosiativa.setIdTransferencia(transferenciaSalva.getId());
            });
        });
    }

    private void salvarTransferenciaBensEstoqueGerandoAssossiativa(Liquidacao liquidacao) {
        liquidacao.getDoctoFiscais().forEach(documentoFiscal -> {
            documentoFiscal.getTransferenciasBens().forEach(assassosiativa -> {
                assassosiativa.getTransferenciaBensEstoque().setHistorico("Gerado automaticamente ao salvar a liquidação " + liquidacao.toString());
                TransferenciaBensEstoque transferenciaSalva = transferenciaBensEstoqueFacade.salvarTransferencia(assassosiativa.getTransferenciaBensEstoque());
                assassosiativa.setIdTransferencia(transferenciaSalva.getId());
            });
        });
    }

    private void salvarTransferenciaBensEstoqueGerandoAssossiativa(LiquidacaoEstorno liquidacaoEstorno) {
        liquidacaoEstorno.getDocumentosFiscais().forEach(documentoFiscal -> {
            documentoFiscal.getTransferenciasBens().forEach(assassosiativa -> {
                assassosiativa.getTransferenciaBensEstoque().setHistorico("Gerado automaticamente ao salvar o estorno de liquidação " + liquidacaoEstorno.toString());
                TransferenciaBensEstoque transferenciaSalva = transferenciaBensEstoqueFacade.salvarTransferencia(assassosiativa.getTransferenciaBensEstoque());
                assassosiativa.setIdTransferencia(transferenciaSalva.getId());
            });
        });
    }

    public Liquidacao gerarTransferenciasDeBensEmGruposDesdobradosDiferentes(Liquidacao entity) {
        if (entity.getDoctoFiscais().stream().anyMatch(docto -> !docto.getTransferenciasBens().isEmpty())) {
            switch (entity.getEmpenho().getTipoContaDespesa()) {
                case BEM_MOVEL:
                    salvarTransferenciaBensMoveisGerandoAssossiativa(entity);
                    return em.merge(entity);
                case BEM_ESTOQUE:
                    salvarTransferenciaBensEstoqueGerandoAssossiativa(entity);
                    return em.merge(entity);
            }
        }
        return entity;
    }

    public LiquidacaoEstorno gerarEstornoTransferenciasDeBensEmGruposDesdobradosDiferentes(LiquidacaoEstorno entity) {
        if (entity.getDocumentosFiscais().stream().anyMatch(docto -> !docto.getTransferenciasBens().isEmpty())) {
            switch (entity.getLiquidacao().getEmpenho().getTipoContaDespesa()) {
                case BEM_MOVEL:
                    salvarTransferenciaBensMoveisGerandoAssossiativa(entity);
                    return em.merge(entity);
                case BEM_ESTOQUE:
                    salvarTransferenciaBensEstoqueGerandoAssossiativa(entity);
                    return em.merge(entity);
            }
        }
        return entity;
    }

    public List<EmpenhoDocumentoFiscal> buscarEmpenhoDocumentoFiscal(DocumentoFiscalIntegracaoAssistente assistente, DocumentoFiscalIntegracao docIntegracao) {
        String sql =
            "   select distinct " +
                "      emp.id                                                           as id_empenho, " +
                "      emp.numero || ' / ' || e.ano                                       as empenho, " +
                "      emp.categoriaorcamentaria                                        as categoria, " +
                "      emp.tipoempenho                                                  as tipo, " +
                "      coalesce(round(sum(irce.valortotal), 2), 0) as valor " +
                " from itementradamaterial item " +
                "         inner join itemcompramaterial icm on icm.itementradamaterial_id = item.id " +
                "         inner join itemdoctoitementrada idie on idie.itementradamaterial_id = item.id " +
                "         inner join doctofiscalentradacompra docent on docent.id = idie.doctofiscalentradacompra_id " +
                "         inner join itemrequisicaodecompra irc on irc.id = icm.itemrequisicaodecompra_id " +
                "         inner join itemrequisicaocompraexec irce on irce.itemrequisicaocompra_id = irc.id " +
                "         inner join execucaocontratoitem exitem on irce.execucaocontratoitem_id = exitem.id " +
                "         inner join execucaocontrato ex on ex.id = exitem.execucaocontrato_id " +
                "         inner join execucaocontratoempenho exemp on exemp.execucaocontrato_id = ex.id " +
                "         inner join empenho emp on emp.id = exemp.empenho_id " +
                "         inner join exercicio e on e.id = emp.exercicio_id " +
                " where docent.doctofiscalliquidacao_id = :idDocumento " +
                " group by emp.numero, e.ano, emp.id, emp.categoriaorcamentaria, emp.tipoempenho " +
                " union all " +
                " select distinct emp.id                                                           as id_empenho, " +
                "                emp.numero || ' / ' || e.ano                                     as empenho, " +
                "                emp.categoriaorcamentaria                                        as categoria, " +
                "                emp.tipoempenho                                                  as tipo, " +
                "                coalesce(round(sum(irce.valortotal), 2), 0) as valor " +
                " from itemaquisicao item " +
                "         inner join itemsolicitacaoaquisicao isa on isa.id = item.itemsolicitacaoaquisicao_id " +
                "         inner join itemdoctoitemaquisicao idia on idia.id = isa.itemdoctoitemaquisicao_id " +
                "         inner join doctofiscalsolicaquisicao docsol on docsol.id = idia.doctofiscalsolicitacao_id " +
                "         inner join itemrequisicaodecompra irc on irc.id = idia.itemrequisicaodecompra_id " +
                "         inner join itemrequisicaocompraexec irce on irce.itemrequisicaocompra_id = irc.id " +
                "         inner join execucaocontratoitem exitem on irce.execucaocontratoitem_id = exitem.id " +
                "         inner join execucaocontrato ex on ex.id = exitem.execucaocontrato_id " +
                "         inner join execucaocontratoempenho exemp on exemp.execucaocontrato_id = ex.id " +
                "         inner join empenho emp on emp.id = exemp.empenho_id " +
                "         inner join exercicio e on e.id = emp.exercicio_id " +
                " where docsol.documentofiscal_id = :idDocumento " +
                " group by emp.numero, e.ano, emp.id, emp.categoriaorcamentaria, emp.tipoempenho";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDocumento", docIntegracao.getDoctoFiscalLiquidacao().getId());
        List<EmpenhoDocumentoFiscal> empenhos = Lists.newArrayList();
        List<Object[]> list = q.getResultList();
        if (!Util.isListNullOrEmpty(list)) {
            list.forEach(objects -> {
                EmpenhoDocumentoFiscal emp = new EmpenhoDocumentoFiscal();
                emp.setId(((BigDecimal) objects[0]).longValue());
                emp.setNumero((String) objects[1]);
                emp.setCategoria(CategoriaOrcamentaria.valueOf((String) objects[2]).getDescricao());
                emp.setTipo(TipoEmpenho.valueOf((String) objects[3]).getDescricao());
                emp.setValor((BigDecimal) objects[4]);
                empenhos.add(emp);
            });
            return empenhos;
        }
        EmpenhoDocumentoFiscal empVO = new EmpenhoDocumentoFiscal(assistente.getEmpenho());
        empenhos.add(empVO);
        return empenhos;
    }
}
