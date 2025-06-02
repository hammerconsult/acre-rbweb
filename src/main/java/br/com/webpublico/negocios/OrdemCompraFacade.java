package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.entidades.OrdemDeServicoContrato;
import br.com.webpublico.entidadesauxiliares.DocumentoFiscalOrdemCompra;
import br.com.webpublico.entidadesauxiliares.OrdemCompraResultadoVo;
import br.com.webpublico.entidadesauxiliares.OrdemCompraServicoItemVo;
import br.com.webpublico.entidadesauxiliares.OrdemCompraServicoVo;
import br.com.webpublico.entidadesauxiliares.contabil.LiquidacaoEstornoVO;
import br.com.webpublico.entidadesauxiliares.contabil.LiquidacaoVO;
import br.com.webpublico.enums.*;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class OrdemCompraFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;

    public List<OrdemDeServicoContrato> buscarOrdemServicoContrato(Contrato contrato) {
        String sql = " select os.* from ordemdeservicocontrato os Where os.contrato_id = :idContrato ";
        Query q = em.createNativeQuery(sql, OrdemDeServicoContrato.class);
        q.setParameter("idContrato", contrato.getId());
        return q.getResultList();
    }

    public List<OrdemCompraServicoVo> buscarOrdemCompraAndServicoPorContrato(Contrato contrato) {
        String sql = " " +
            " select distinct " +
            "        rc.id," +
            "        rc.numero," +
            "        rc.dataRequisicao," +
            "        rc.localEntrega, " +
            "        rc.descricao, " +
            "        rc.situacaoRequisicaoCompra," +
            "        rc.tipoobjetocompra," +
            "        rc.prazoentrega," +
            "        rc.tipoprazoentrega, " +
            "        coalesce(rc.descricaoprazoentrega,''), " +
            "        coalesce(pf.nome, '') as criadopor " +
            "   from requisicaodecompra rc " +
            "   inner join requisicaocompraexecucao rce on rce.requisicaocompra_id = rc.id " +
            "   inner join execucaocontratoempenho exemp on exemp.id = rce.execucaocontratoempenho_id " +
            "   inner join execucaocontrato ex on ex.id = exemp.execucaocontrato_id " +
            "   left join pessoafisica pf on pf.id = rc.criadopor_id " +
            "   where ex.contrato_id = :idContrato " +
            "   and rc.tipoobjetocompra in (:tiposObjetoCompra) " +
            "   order by rc.numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContrato", contrato.getId());
        q.setParameter("tiposObjetoCompra", TipoObjetoCompra.recuperarListaName(TipoObjetoCompra.getTiposObjetoCompraOrdemCompraAndServico()));
        List<Object[]> resultList = q.getResultList();
        List<OrdemCompraServicoVo> toReturn = Lists.newArrayList();

        for (Object[] obj : resultList) {
            OrdemCompraServicoVo oc = new OrdemCompraServicoVo();
            oc.setIdRequisicao(((BigDecimal) obj[0]).longValue());
            oc.setNumero(((BigDecimal) obj[1]).longValue());
            oc.setDataRequisicao((Date) obj[2]);
            oc.setLocalEntrega((String) obj[3]);
            oc.setDescricao((String) obj[4]);
            oc.setSituacaoRequisicaoCompra(SituacaoRequisicaoCompra.valueOf((String) obj[5]).getDescricao());
            oc.setTipoObjetoCompra(TipoObjetoCompra.valueOf((String) obj[6]));
            oc.setPrazoEntrega(obj[7] != null ? ((BigDecimal) obj[7]).intValue() : null);
            oc.setTipoPrazoEntrega(obj[8] != null ? TipoPrazo.valueOf((String) obj[8]) : null);
            oc.setDescricaoPrazoEntrega((String) obj[9]);
            oc.setCriadoPor((String) obj[10]);
            oc.setItens(buscarItensOrdemCompra(oc.getIdRequisicao()));
            oc.setExecucoes(requisicaoDeCompraFacade.buscarRequisicaoExecucaoComponente(oc.getIdRequisicao()));
            oc.setEstornosOrdemCompraServico(buscarEstornoOrdemCompra(oc));
            oc.setDocumentosFiscais(oc.getTipoObjetoCompra().isServico() ? buscarDocumentosServico(oc) : buscarDocumentosEntradaOrAquisicao(oc));
            oc.setAtestos(buscarAtesto(oc));
            oc.setEstornosAtestos(buscarAtestoEstornoAquisicao(oc));
            toReturn.add(oc);
        }
        return toReturn;
    }

    private List<OrdemCompraServicoItemVo> buscarItensOrdemCompra(long idRequisicao) {
        String sql = " " +
            " select item.numero," +
            "        to_char(item.descricaocomplementar) as desc_compl ," +
            "        oc.codigo || ' - ' || oc.descricao as objeto_compra, " +
            "        um.sigla as unidade_medida, " +
            "        um.mascaraquantidade as mascara_qtde, " +
            "        um.mascaravalorunitario as mascara_valor, " +
            "        item.quantidade as quantidade , " +
            "        item.valorunitario as valor_unitario , " +
            "        item.valortotal as valor_total " +
            "   from itemrequisicaodecompra item " +
            "   inner join objetocompra oc on oc.id = item.objetocompra_id " +
            "   left join unidademedida um on um.id = item.unidademedida_id " +
            "   where item.requisicaodecompra_id = :idRequisicao " +
            "   and item.quantidade > 0 " +
            "   order by item.numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idRequisicao", idRequisicao);
        List<Object[]> resultList = q.getResultList();
        List<OrdemCompraServicoItemVo> toReturn = Lists.newArrayList();

        for (Object[] obj : resultList) {
            OrdemCompraServicoItemVo item = new OrdemCompraServicoItemVo();
            item.setNumero(((BigDecimal) obj[0]).intValue());
            item.setDescricaoComplementar((String) obj[1]);
            item.setObjetoCompra((String) obj[2]);
            item.setUnidadeMedida((String) obj[3]);
            item.setMascaraQuantidade(obj[4] != null ? TipoMascaraUnidadeMedida.valueOf((String) obj[4]) : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL);
            item.setMascaraValor(obj[5] != null ? TipoMascaraUnidadeMedida.valueOf((String) obj[5]) : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL);
            item.setQuantidade((BigDecimal) obj[6]);
            item.setValorUnitario((BigDecimal) obj[7]);
            item.setValorTotal((BigDecimal) obj[8]);
            toReturn.add(item);
        }
        return toReturn;
    }

    public List<OrdemCompraResultadoVo> buscarEstornoOrdemCompra(OrdemCompraServicoVo ordemVo) {
        String sql = " " +
                " select id_est, " +
                "       numero_est, " +
                "       data_est, " +
                "       motivo, " +
                "       sum(qtde * vl_unit) as vl_total " +
                " from (select  est.id               as id_est, " +
                "               est.numero           as numero_est, " +
                "               est.datalancamento   as data_est, " +
                "               est.motivo           as motivo, " +
                "               itemest.quantidade   as qtde, " +
                "               irce.valorunitario   as vl_unit " +
                "      from requisicaocompraestorno est " +
                "               inner join itemrequisicaocompraest itemest on est.id = itemest.requisicaocompraestorno_id " +
                "               inner join itemrequisicaocompraexec irce on irce.id = itemest.itemRequisicaoCompraExec_id " +
                "      where est.requisicaodecompra_id = :idRequisicao) " +
                " group by id_est, numero_est, data_est, motivo  ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idRequisicao", ordemVo.getIdRequisicao());
        List<Object[]> resultList = q.getResultList();
        List<OrdemCompraResultadoVo> toReturn = Lists.newArrayList();

        for (Object[] obj : resultList) {
            OrdemCompraResultadoVo item = new OrdemCompraResultadoVo();
            item.setId(((BigDecimal) obj[0]).longValue());
            item.setNumero(((BigDecimal) obj[1]).toString());
            item.setDataLancamento((Date) obj[2]);
            item.setMotivo((String) obj[3]);
            item.setValor((BigDecimal) obj[4]);
            toReturn.add(item);
        }
        return toReturn;
    }

    public List<OrdemCompraResultadoVo> buscarAtesto(OrdemCompraServicoVo ordemVo) {
        String sql = "" +
            " select " +
            "    id, " +
            "    numero, " +
            "    data_lancamento, " +
            "    situacao, " +
            "    motivo, " +
            "    link, " +
            "    sum(valor_total) as valor_total " +
            " from (select em.id                     as id, " +
            "             to_char(em.numero)         as numero," +
            "             em.dataentrada              as data_lancamento, " +
            "             em.situacao                 as situacao, " +
            "             em.historico                as motivo, " +
            "             'entrada-por-compra'        as link, " +
            "             coalesce(icm.valortotal, 0) as valor_total " +
            "      from entradamaterial em " +
            "               inner join entradacompramaterial ecm on ecm.id = em.id " +
            "               inner join itementradamaterial icm on icm.entradamaterial_id = em.id " +
            "      where ecm.requisicaodecompra_id = :idRequisicao " +
            "      union all " +
            "      select aq.id                             as id, " +
            "             to_char(aq.numero)                as numero," +
            "             aq.datadeaquisicao                as data_lancamento, " +
            "             aq.situacao                       as situacao, " +
            "             req.descricao                     as motivo, " +
            "             'efetivacao-aquisicao-bem-movel'  as link, " +
            "             coalesce(ev.valordolancamento, 0) as valor_total " +
            "      from aquisicao aq " +
            "               inner join itemaquisicao item on item.aquisicao_id = aq.id " +
            "               inner join eventobem ev on ev.id = item.id " +
            "               inner join solicitacaoaquisicao sol on sol.id = aq.solicitacaoaquisicao_id " +
            "               inner join requisicaodecompra req on req.id = sol.requisicaodecompra_id " +
            "      where sol.requisicaodecompra_id = :idRequisicao " +
            "   union all " +
            "    select liq.id                              as id, " +
            "       liq.numero                              as numero, " +
            "       cast(liq.dataliquidacao as date)        as data_lancamento, " +
            "       'CONCLUIDA'                             as situacao, " +
            "       liq.complemento                         as motivo, " +
            "       case " +
            "           when liq.categoriaorcamentaria = 'NORMAL' " +
            "               then 'liquidacao' " +
            "           else 'liquidacao/resto-a-pagar' end as link, " +
            "       coalesce(liq.valor, 0)                  as valor_total " +
            "   from requisicaodecompra req " +
            "         inner join requisicaocompraexecucao reqex on reqex.requisicaocompra_id = req.id " +
            "         inner join execucaocontrato ex on ex.id = reqex.execucaocontrato_id " +
            "         inner join execucaocontratoempenho exemp on exemp.execucaocontrato_id = ex.id " +
            "         inner join empenho emp on emp.id = exemp.empenho_id " +
            "         inner join liquidacao liq on emp.id = liq.empenho_id " +
            "         inner join liquidacaodoctofiscal ldf on ldf.liquidacao_id = liq.id" +
            "    where req.id = :idRequisicao " +
            "        and req.tipoobjetocompra = :tipoServico " +
            " ) group by id, numero, data_lancamento, situacao, motivo, link " +
            "   order by numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idRequisicao", ordemVo.getIdRequisicao());
        q.setParameter("tipoServico", TipoObjetoCompra.SERVICO.name());
        List<Object[]> resultList = q.getResultList();
        List<OrdemCompraResultadoVo> toReturn = Lists.newArrayList();

        for (Object[] obj : resultList) {
            OrdemCompraResultadoVo item = new OrdemCompraResultadoVo();
            item.setId(((BigDecimal) obj[0]).longValue());
            item.setNumero((String) obj[1]);
            item.setDataLancamento((Date) obj[2]);
            item.setSituacaoAtesto(OrdemCompraResultadoVo.SituacaoAtesto.valueOf((String) obj[3]));
            item.setMotivo((String) obj[4]);
            item.setLink((String) obj[5]);
            item.setValor((BigDecimal) obj[6]);
            toReturn.add(item);
        }
        return toReturn;
    }

    public List<OrdemCompraResultadoVo> buscarAtestoEstornoAquisicao(OrdemCompraServicoVo ordemVo) {
        String sql = "" +
            " select est.id, " +
            "        est.numero," +
            "        est.dataestorno, " +
            "        est.motivo, " +
            "        coalesce(sum(ev.valordolancamento), 0) as total " +
            " from aquisicaoestorno est " +
            "         inner join aquisicao aq on aq.id = est.aquisicao_id " +
            "         inner join itemaquisicaoestorno itemest on itemest.aquisicaoestorno_id = est.id " +
            "         inner join eventobem ev on ev.id = itemest.id " +
            "         inner join solicitacaoaquisicao sol on aq.solicitacaoaquisicao_id = sol.id " +
            " where sol.requisicaodecompra_id = :idRequisicao " +
            " group by est.id, est.numero, est.dataestorno, est.motivo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idRequisicao", ordemVo.getIdRequisicao());
        List<Object[]> resultList = q.getResultList();
        List<OrdemCompraResultadoVo> toReturn = Lists.newArrayList();

        for (Object[] obj : resultList) {
            OrdemCompraResultadoVo item = new OrdemCompraResultadoVo();
            item.setId(((BigDecimal) obj[0]).longValue());
            item.setNumero(((BigDecimal) obj[1]).toString());
            item.setDataLancamento((Date) obj[2]);
            item.setMotivo((String) obj[3]);
            item.setValor((BigDecimal) obj[4]);
            toReturn.add(item);
        }
        return toReturn;
    }

    public List<DocumentoFiscalOrdemCompra> buscarDocumentosServico(OrdemCompraServicoVo ordemVo) {
        String sql = " " +
            " select " +
            "       docto.id            as id, " +
            "       docto.NUMERO        as numero, " +
            "       docto.DATADOCTO     as data_emissao, " +
            "       docto.DATAATESTO    as data_atesto, " +
            "       tipo.DESCRICAO      as tipo, " +
            "       docto.CHAVEDEACESSO as chave, " +
            "       docto.VALOR         as valor, " +
            "       'Liquidado'         as situacao " +
            " from requisicaodecompra r " +
            "    inner join requisicaocompraexecucao reqex on reqex.requisicaocompra_id = r.id " +
            "    inner join execucaocontrato ex on ex.id = reqex.execucaocontrato_id " +
            "    inner join execucaocontratoempenho exemp on exemp.execucaocontrato_id = ex.id " +
            "    inner join empenho emp on emp.id = exemp.empenho_id " +
            "    inner join liquidacao liq on emp.id = liq.empenho_id " +
            "    inner join liquidacaodoctofiscal ldf on ldf.liquidacao_id = liq.id" +
            "    inner join doctofiscalliquidacao docto on ldf.doctofiscalliquidacao_id = docto.id " +
            "    inner join tipodocumentofiscal tipo on tipo.id = docto.TIPODOCUMENTOFISCAL_ID " +
            " where r.id = :idRequisicao " +
            " and r.tipoobjetocompra = :tipoObjetoCompra " +
            " order by docto.numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idRequisicao", ordemVo.getIdRequisicao());
        q.setParameter("tipoObjetoCompra", TipoObjetoCompra.SERVICO.name());
        List<Object[]> resultList = q.getResultList();
        List<DocumentoFiscalOrdemCompra> toReturn = Lists.newArrayList();

        for (Object[] obj : resultList) {
            DocumentoFiscalOrdemCompra doc = new DocumentoFiscalOrdemCompra();
            doc.setId(((BigDecimal) obj[0]).longValue());
            doc.setNumero((String) obj[1]);
            doc.setDataDocto((Date) obj[2]);
            doc.setDataAtesto((Date) obj[3]);
            doc.setTipoDocumento((String) obj[4]);
            doc.setChaveDeAcesso((String) obj[5]);
            doc.setValor((BigDecimal) obj[6]);
            doc.setSituacao((String) obj[7]);
            doc.setLiquidacoes(buscarLiquidacaoPorDocumentoFiscal(doc.getId()));
            toReturn.add(doc);
        }
        return toReturn;
    }

    public List<DocumentoFiscalOrdemCompra> buscarDocumentosEntradaOrAquisicao(OrdemCompraServicoVo ordemVo) {
        String sql = " " +
            " select " +
            "       docto.id            as id, " +
            "       docto.NUMERO        as numero, " +
            "       docto.DATADOCTO     as data_emissao, " +
            "       docto.DATAATESTO    as data_atesto, " +
            "       tipo.DESCRICAO      as tipo, " +
            "       docto.CHAVEDEACESSO as chave, " +
            "       docto.VALOR         as valor, " +
            "       case " +
            "           when det.id is not null then case det.SITUACAO " +
            "                                            when 'LIQUIDADO' then 'Liquidado' " +
            "                                            when 'AGUARDANDO_LIQUIDACAO' then 'Aguardando Liquidação' " +
            "                                            when 'LIQUIDADO_PARCIALMENTE' then 'Liquidado Parcialmente' end " +
            "           else " +
            "           case " +
            "             when exists(select 1 from aquisicaoestorno est " +
            "                         inner join aquisicao aq on aq.id = est.aquisicao_id " +
            "                         inner join solicitacaoaquisicao sol on sol.id = aq.solicitacaoaquisicao_id " +
            "                         inner join doctofiscalsolicaquisicao docsol on docsol.solicitacaoaquisicao_id = sol.id " +
            "                         where docsol.documentofiscal_id = docto.id " +
            "                         and sol.requisicaodecompra_id = r.id) " +
            "                         then 'Estornado'" +
            "             when not exists(select 1 " +
            "                           from LIQUIDACAODOCTOFISCAL ldf " +
            "                           where ldf.DOCTOFISCALLIQUIDACAO_ID = docto.id) " +
            "                           then 'Aguardando Liquidação' " +
            "             when (select coalesce(sum(ldf.valorliquidado), 0) " +
            "                           from LIQUIDACAODOCTOFISCAL ldf " +
            "                           where ldf.DOCTOFISCALLIQUIDACAO_ID = docto.id) = docto.VALOR " +
            "                           then 'Liquidado' " +
            "                           else 'Liquidado Parcialmente' end " +
            "           end             as situacao " +
            " from requisicaodecompra r " +
            "    left join entradacompramaterial e on r.id = e.requisicaodecompra_id " +
            "    left join doctofiscalentradacompra det on e.id = det.entradacompramaterial_id " +
            "    left join solicitacaoaquisicao s on r.id = s.requisicaodecompra_id " +
            "    left join doctofiscalsolicaquisicao d on s.id = d.solicitacaoaquisicao_id " +
            "    inner join doctofiscalliquidacao docto on coalesce(d.documentofiscal_id, det.doctofiscalliquidacao_id) = docto.id " +
            "   inner join tipodocumentofiscal tipo on tipo.id = docto.TIPODOCUMENTOFISCAL_ID " +
            " where r.id = :idRequisicao " +
            " order by docto.numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idRequisicao", ordemVo.getIdRequisicao());
        List<Object[]> resultList = q.getResultList();
        List<DocumentoFiscalOrdemCompra> toReturn = Lists.newArrayList();

        for (Object[] obj : resultList) {
            DocumentoFiscalOrdemCompra doc = new DocumentoFiscalOrdemCompra();
            doc.setId(((BigDecimal) obj[0]).longValue());
            doc.setNumero((String) obj[1]);
            doc.setDataDocto((Date) obj[2]);
            doc.setDataAtesto((Date) obj[3]);
            doc.setTipoDocumento((String) obj[4]);
            doc.setChaveDeAcesso((String) obj[5]);
            doc.setValor((BigDecimal) obj[6]);
            doc.setSituacao((String) obj[7]);
            doc.setLiquidacoes(buscarLiquidacaoPorDocumentoFiscal(doc.getId()));
            toReturn.add(doc);
        }
        return toReturn;
    }

    public List<LiquidacaoVO> buscarLiquidacaoPorDocumentoFiscal(Long idDocumentoFiscal) {
        String sql = " select " +
            "           li.id,    " +
            "           li.numero,    " +
            "           li.dataliquidacao,    " +
            "           li.complemento,    " +
            "           ldf.valorliquidado," +
            "           li.categoriaorcamentaria  " +
            "         from liquidacao li " +
            "           inner join liquidacaodoctofiscal ldf on ldf.liquidacao_id = li.id " +
            "          where ldf.doctofiscalliquidacao_id = :idDocumentoFiscal ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDocumentoFiscal", idDocumentoFiscal);
        List<Object[]> resultado = q.getResultList();

        List<LiquidacaoVO> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            resultado.forEach(obj -> {
                LiquidacaoVO liq = new LiquidacaoVO();
                liq.setIdLiquidacao(((BigDecimal) obj[0]).longValue());
                liq.setNumero((String) obj[1]);
                liq.setData((Date) obj[2]);
                liq.setLiquidacao((String) obj[3]);
                liq.setValor((BigDecimal) obj[4]);
                liq.setCategoriaOrcamentaria(CategoriaOrcamentaria.valueOf((String) obj[5]));
                liq.setEstornos(buscarLiquidacaoEstornoPorLiquidacao(liq.getIdLiquidacao(), idDocumentoFiscal));
                retorno.add(liq);
            });
        }
        return retorno;
    }

    public List<LiquidacaoEstornoVO> buscarLiquidacaoEstornoPorLiquidacao(Long idLiquidacao, Long idDocumentoFiscal) {
        String sql = " select " +
            "           le.id," +
            "           le.numero," +
            "           le.dataestorno," +
            "           doc.valor," +
            "           le.categoriaorcamentaria " +
            "           from liquidacaoestorno le  " +
            "          inner join liquidacaoestdoctofiscal doc on doc.liquidacaoestorno_id = le.id " +
            "          where le.liquidacao_id = :idLiquidacao " +
            "          and doc.documentofiscal_id = :idDocumentoFiscal ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLiquidacao", idLiquidacao);
        q.setParameter("idDocumentoFiscal", idDocumentoFiscal);
        List<Object[]> resultado = q.getResultList();
        List<LiquidacaoEstornoVO> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            resultado.forEach(obj -> {
                LiquidacaoEstornoVO estLiq = new LiquidacaoEstornoVO();
                estLiq.setId(((BigDecimal) obj[0]).longValue());
                estLiq.setNumero((String) obj[1]);
                estLiq.setData((Date) obj[2]);
                estLiq.setValor((BigDecimal) obj[3]);
                estLiq.setCategoriaOrcamentaria(CategoriaOrcamentaria.valueOf((String) obj[4]));
                retorno.add(estLiq);
            });
        }
        return retorno;
    }

    public RequisicaoDeCompraFacade getRequisicaoDeCompraFacade() {
        return requisicaoDeCompraFacade;
    }
}
