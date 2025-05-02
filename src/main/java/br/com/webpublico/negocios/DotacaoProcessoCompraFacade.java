package br.com.webpublico.negocios;

import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.entidadesauxiliares.DotacaoProcessoCompraVO;
import br.com.webpublico.entidadesauxiliares.FiltroDotacaoProcessoCompraVO;
import br.com.webpublico.enums.TipoOperacaoORC;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class DotacaoProcessoCompraFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public List<DotacaoProcessoCompraVO> buscarValoresReservaDotacaoProcessoCompra(FiltroDotacaoProcessoCompraVO filtro) {
        String sql = "" +
            " select distinct fd.id as id_fonte from dotsolmat dsm " +
            "   inner join solicitacaomaterial sm on sm.id = dsm.solicitacaomaterial_id " +
            "   inner join dotacaosolmatitem dsmi on dsmi.dotacaosolicitacaomaterial_id = dsm.id " +
            "   inner join dotacaosolmatitemfonte dsmid on dsmid.dotacaosolmatitem_id = dsmi.id " +
            "   inner join fontedespesaorc fd on fd.id = dsmid.fontedespesaorc_id " +
            "   inner join despesaorc do on do.id = fd.despesaorc_id " +
            "   inner join provisaoppadespesa ppd on ppd.id = do.provisaoppadespesa_id " +
            " where sm.id = :idSolicitacao " +
            "   and dsmid.exercicio_id = :idExercicio ";
        sql += filtro.getFonteDespesaORC() != null ? " and fd.id = :idFonteDespesaOrc " : " ";
        sql += filtro.getUnidadeOrganizacional() != null ? " and ppd.unidadeorganizacional_id = :idUnidadeOrc " : "";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", filtro.getSolicitacaoMaterial().getId());
        q.setParameter("idExercicio", filtro.getExercicio().getId());
        if (filtro.getFonteDespesaORC() != null) {
            q.setParameter("idFonteDespesaOrc", filtro.getFonteDespesaORC().getId());
        }
        if (filtro.getUnidadeOrganizacional() != null) {
            q.setParameter("idUnidadeOrc", filtro.getUnidadeOrganizacional().getId());
        }
        List<Object> resultado = q.getResultList();
        List<DotacaoProcessoCompraVO> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object obj : resultado) {
                DotacaoProcessoCompraVO vo = new DotacaoProcessoCompraVO();
                long idFonte = ((BigDecimal) obj).longValue();
                FonteDespesaORC fonteDespesaORC = em.find(FonteDespesaORC.class, idFonte);
                vo.setFonteDespesaORC(fonteDespesaORC);

                filtro.setFonteDespesaORC(fonteDespesaORC);

                filtro.setTipoOperacao(TipoOperacaoORC.NORMAL);
                vo.setValorReservado(getValorReservado(filtro));

                filtro.setTipoOperacao(TipoOperacaoORC.ESTORNO);
                vo.setValorEstornoReservado(getValorReservado(filtro));

                filtro.setExecucaoContratoOrSemContrato(false);
                vo.setValorExecutadoReservaInicial(getValorExecutado(filtro));

                filtro.setExecucaoContratoOrSemContrato(true);
                vo.setValorExecutadoReservaExecucao(getValorExecutado(filtro));

                filtro.setExecucaoContratoOrSemContrato(false);
                vo.setValorEstornoExecutadoReservaInicial(getValorEstornoExecutado(filtro));

                filtro.setExecucaoContratoOrSemContrato(true);
                vo.setValorEstornoExecutadoReservaExecucao(getValorEstornoExecutado(filtro));
                retorno.add(vo);
            }
        }
        return retorno;
    }

    public BigDecimal getValorReservado(FiltroDotacaoProcessoCompraVO filtro) {
        String sql = " " +
            " select coalesce(sum(dsmid.valor),0) as vl_reservado " +
            "  from dotsolmat dsm " +
            "  inner join solicitacaomaterial sm on sm.id = dsm.solicitacaomaterial_id " +
            "  inner join dotacaosolmatitem dsmi on dsmi.dotacaosolicitacaomaterial_id = dsm.id " +
            "  inner join dotacaosolmatitemfonte dsmid on dsmid.dotacaosolmatitem_id = dsmi.id " +
            "  inner join fontedespesaorc fd on fd.id = dsmid.fontedespesaorc_id " +
            "  inner join despesaorc do on do.id = fd.despesaorc_id " +
            "  inner join provisaoppadespesa ppd on ppd.id = do.provisaoppadespesa_id " +
            " where sm.id = :idSolicitacao " +
            "  and dsmi.tipoobjetocompra = :tipoObjCompra " +
            "  and dsmid.exercicio_id = :idExercicio " +
            "  and dsmid.tipooperacao = :tipoOperacao ";
        sql += filtro.getFonteDespesaORC() != null ? " and fd.id = :idFonteDespesaOrc " : " ";
        sql += filtro.getUnidadeOrganizacional() != null ? " and ppd.unidadeorganizacional_id = :idUnidadeOrc " : "";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", filtro.getSolicitacaoMaterial().getId());
        q.setParameter("tipoObjCompra", filtro.getTipoObjetoCompra().name());
        q.setParameter("tipoOperacao", filtro.getTipoOperacao().name());
        q.setParameter("idExercicio", filtro.getExercicio().getId());
        if (filtro.getFonteDespesaORC() != null) {
            q.setParameter("idFonteDespesaOrc", filtro.getFonteDespesaORC().getId());
        }
        if (filtro.getUnidadeOrganizacional() != null) {
            q.setParameter("idUnidadeOrc", filtro.getUnidadeOrganizacional().getId());
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException ne) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorExecutadoContrato(FiltroDotacaoProcessoCompraVO filtro) {
        String sql = " " +
            " select coalesce(sum(ectf.valor), 0) as vl_executado " +
            "   from contrato c " +
            "    inner join execucaocontrato ec on ec.contrato_id = c.id " +
            "    inner join execucaocontratotipo ect on ect.execucaocontrato_id = ec.id " +
            "    inner join execucaocontratotipofonte ectf on ectf.execucaocontratotipo_id = ect.id " +
            "    left join conlicitacao conlic on conlic.contrato_id = c.id " +
            "    left join licitacao l on l.id = conlic.licitacao_id " +
            "    left join condispensalicitacao cdl on cdl.contrato_id = c.id " +
            "    left join dispensadelicitacao dl on dl.id = cdl.dispensadelicitacao_id " +
            "    inner join processodecompra pc on pc.id = coalesce(l.processodecompra_id, dl.processodecompra_id) " +
            "    inner join solicitacaomaterial sm on sm.id = pc.solicitacaomaterial_id " +
            " where sm.id = :idSolicitacao " +
            "   and ect.tipoobjetocompra = :tipoObjCompra " +
            "   and ectf.gerarreserva = :execucaoPosReservaInicial ";
        sql += filtro.getFonteDespesaORC() != null ? " and ectf.fontedespesaorc_id = :idFonteDespesaOrc " : " ";
        sql += filtro.getUnidadeOrganizacional() != null ? " and ec.unidadeorcamentaria_id = :idUnidadeOrc " : "";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", filtro.getSolicitacaoMaterial().getId());
        q.setParameter("tipoObjCompra", filtro.getTipoObjetoCompra().name());
        q.setParameter("execucaoPosReservaInicial", filtro.getExecucaoContratoOrSemContrato());
        if (filtro.getFonteDespesaORC() != null) {
            q.setParameter("idFonteDespesaOrc", filtro.getFonteDespesaORC().getId());
        }
        if (filtro.getUnidadeOrganizacional() != null) {
            q.setParameter("idUnidadeOrc", filtro.getUnidadeOrganizacional().getId());
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException ne) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorEstornoExecutadoContrato(FiltroDotacaoProcessoCompraVO filtro) {
        String sql = " select coalesce(sum(case " +
            "                              when solest.empenho_id is null then solest.valor " +
            "                              when empest.id is not null then empest.valor " +
            "                              else 0 end), 0) as vl_executado_est " +
            "  from execucaocontratoestorno exest " +
            "   inner join execucaocontrato ec on ec.id = exest.execucaocontrato_id " +
            "   inner join execucaocontratoempenho exemp on exemp.execucaocontrato_id = ec.id " +
            "   inner join solicitacaoempenho solex on solex.id = exemp.solicitacaoempenho_id " +
            "   inner join contrato c on c.id = ec.contrato_id " +
            "   inner join execucaocontratotipo ect on ect.execucaocontrato_id = ec.id " +
            "   inner join execucaocontratoempenhoest exempest on exempest.execucaocontratoestorno_id = exest.id " +
            "   inner join solicitacaoempenhoestorno solest on solest.id = exempest.solicitacaoempenhoestorno_id " +
            "   left join solicitacaoempenho sol on sol.id = solest.solicitacaoempenho_id " +
            "   left join empenhoestorno empest on empest.id = solest.empenhoestorno_id " +
            "   left join conlicitacao conlic on conlic.contrato_id = c.id " +
            "   left join licitacao l on l.id = conlic.licitacao_id " +
            "   left join condispensalicitacao cdl on cdl.contrato_id = c.id " +
            "   left join dispensadelicitacao dl on dl.id = cdl.dispensadelicitacao_id " +
            "   inner join processodecompra pc on pc.id = coalesce(l.processodecompra_id, dl.processodecompra_id) " +
            "  inner join solicitacaomaterial sm on sm.id = pc.solicitacaomaterial_id " +
            " where sm.id = :idSolicitacao " +
            "  and ect.tipoobjetocompra = :tipoObjCompra " +
            "  and solex.gerarreserva = :execucaoPosReservaInicial ";
        sql += filtro.getFonteDespesaORC() != null ? " and sol.fontedespesaorc_id = :idFonteDespesaOrc " : " ";
        sql += filtro.getUnidadeOrganizacional() != null ? " and ec.unidadeorcamentaria_id = :idUnidadeOrc " : "";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", filtro.getSolicitacaoMaterial().getId());
        q.setParameter("tipoObjCompra", filtro.getTipoObjetoCompra().name());
        q.setParameter("execucaoPosReservaInicial", filtro.getExecucaoContratoOrSemContrato());
        if (filtro.getFonteDespesaORC() != null) {
            q.setParameter("idFonteDespesaOrc", filtro.getFonteDespesaORC().getId());
        }
        if (filtro.getUnidadeOrganizacional() != null) {
            q.setParameter("idUnidadeOrc", filtro.getUnidadeOrganizacional().getId());
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException ne) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorExecutadoSemContrato(FiltroDotacaoProcessoCompraVO filtro) {
        String sql = " " +
            " select coalesce(sum(exf.valor), 0)      as vl_executado " +
            "   from execucaoprocesso ex " +
            "    inner join execucaoprocessoreserva exr on exr.execucaoprocesso_id = ex.id " +
            "    inner join execucaoprocessofonte exf on exf.execucaoprocessoreserva_id = exr.id " +
            "    left join execucaoprocessoata exata on exata.execucaoprocesso_id = ex.id " +
            "    left join ataregistropreco ata on ata.id = exata.ataregistropreco_id " +
            "    left join licitacao l on l.id = ata.licitacao_id " +
            "    left join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = ex.id " +
            "    left join dispensadelicitacao disp on disp.id = exdisp.dispensalicitacao_id " +
            "    inner join processodecompra pc on pc.id = coalesce(l.processodecompra_id, disp.processodecompra_id) " +
            "    inner join solicitacaomaterial sm on sm.id = pc.solicitacaomaterial_id " +
            " where sm.id = :idSolicitacao " +
            "   and exr.tipoobjetocompra = :tipoObjCompra " +
            "   and exf.gerareserva = :execucaoPosReservaInicial ";
        sql += filtro.getFonteDespesaORC() != null ? " and exf.fontedespesaorc_id = :idFonteDespesaOrc " : " ";
        sql += filtro.getUnidadeOrganizacional() != null ? " and ex.unidadeorcamentaria_id = :idUnidadeOrc " : "";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", filtro.getSolicitacaoMaterial().getId());
        q.setParameter("tipoObjCompra", filtro.getTipoObjetoCompra().name());
        q.setParameter("execucaoPosReservaInicial", filtro.getExecucaoContratoOrSemContrato());
        if (filtro.getFonteDespesaORC() != null) {
            q.setParameter("idFonteDespesaOrc", filtro.getFonteDespesaORC().getId());
        }
        if (filtro.getUnidadeOrganizacional() != null) {
            q.setParameter("idUnidadeOrc", filtro.getUnidadeOrganizacional().getId());
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException ne) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorEstornoExecutadoSemContrato(FiltroDotacaoProcessoCompraVO filtro) {
        String sql = " select coalesce(sum(case " +
            "                              when solest.empenho_id is null then solest.valor " +
            "                              when empest.id is not null then empest.valor " +
            "                              else 0 end), 0) as vl_executado_est " +
            "      from execucaoprocessoestorno exest " +
            "               inner join execucaoprocesso ex on ex.id = exest.execucaoprocesso_id " +
            "               inner join execucaoprocessoempenho exemp on exemp.execucaoprocesso_id = ex.id " +
            "               inner join solicitacaoempenho solex on solex.id = exemp.solicitacaoempenho_id " +
            "               inner join execucaoprocessoempenhoest exempest on exempest.execucaoprocessoestorno_id = exest.id " +
            "               inner join solicitacaoempenhoestorno solest on solest.id = exempest.solicitacaoempenhoestorno_id " +
            "               left join solicitacaoempenho sol on sol.id = solest.solicitacaoempenho_id " +
            "               left join empenhoestorno empest on empest.id = solest.empenhoestorno_id " +
            "               inner join execucaoprocessoreserva exr on exr.execucaoprocesso_id = ex.id " +
            "               left join execucaoprocessoata exata on exata.execucaoprocesso_id = ex.id " +
            "               left join ataregistropreco ata on ata.id = exata.ataregistropreco_id " +
            "               left join licitacao l on l.id = ata.licitacao_id " +
            "               left join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = ex.id " +
            "               left join dispensadelicitacao disp on disp.id = exdisp.dispensalicitacao_id " +
            "               inner join processodecompra pc on pc.id = coalesce(l.processodecompra_id, disp.processodecompra_id) " +
            "               inner join solicitacaomaterial sm on sm.id = pc.solicitacaomaterial_id " +
            "      where sm.id = :idSolicitacao " +
            "        and exr.tipoobjetocompra = :tipoObjCompra " +
            "        and solex.gerarreserva = :execucaoPosReservaInicial ";
        sql += filtro.getFonteDespesaORC() != null ? " and sol.fontedespesaorc_id = :idFonteDespesaOrc " : " ";
        sql += filtro.getUnidadeOrganizacional() != null ? " and ex.unidadeorcamentaria_id = :idUnidadeOrc " : "";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", filtro.getSolicitacaoMaterial().getId());
        q.setParameter("tipoObjCompra", filtro.getTipoObjetoCompra().name());
        q.setParameter("execucaoPosReservaInicial", filtro.getExecucaoContratoOrSemContrato());
        if (filtro.getFonteDespesaORC() != null) {
            q.setParameter("idFonteDespesaOrc", filtro.getFonteDespesaORC().getId());
        }
        if (filtro.getUnidadeOrganizacional() != null) {
            q.setParameter("idUnidadeOrc", filtro.getUnidadeOrganizacional().getId());
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException ne) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorExecutado(FiltroDotacaoProcessoCompraVO filtro) {
        return getValorExecutadoContrato(filtro).add(getValorExecutadoSemContrato(filtro));
    }

    public BigDecimal getValorEstornoExecutado(FiltroDotacaoProcessoCompraVO filtro) {
        return getValorEstornoExecutadoContrato(filtro).add(getValorEstornoExecutadoSemContrato(filtro));
    }
}
