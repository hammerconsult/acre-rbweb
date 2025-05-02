package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ExecucaoContratoItem;
import br.com.webpublico.entidades.ExecucaoProcessoItem;
import br.com.webpublico.entidades.SolicitacaoEmpenhoEstorno;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.TipoExecucaoProcesso;
import br.com.webpublico.enums.TipoResumoExecucao;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class ResumoExecucaoFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public List<ResumoExecucaoVO> buscarExecucoes(FiltroResumoExecucaoVO filtro) {
        String sql;
        if (filtro.getTipoResumoExecucao().isExecucaoContrato()) {
            sql = " " +
                " select ex.id, " +
                "        ex.numero, " +
                "        ex.datalancamento, " +
                "        ex.valor, " +
                "        'CONTRATO' as tipo " +
                " from execucaocontrato ex " +
                "  where ex.contrato_id = :idProcesso ";
            sql += filtro.getExecucaoContrato() != null ? " and ex.id = :idExecucao " : " ";
            sql += " order by ex.numero ";
        } else {
            sql = " " +
                " select ex.id,  " +
                "        ex.numero, " +
                "        ex.datalancamento, " +
                "        ex.valor, " +
                "        'PROCESSO' as tipo " +
                " from execucaoprocesso ex " +
                "   left join execucaoprocessoata exata on exata.execucaoprocesso_id = ex.id " +
                "   left join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = ex.id " +
                "  where coalesce(exata.ataregistropreco_id, exdisp.dispensalicitacao_id) = :idProcesso ";
            sql += filtro.getExecucaoProcesso() != null ? " and ex.id = :idExecucao " : " ";
            sql += " order by ex.numero ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("idProcesso", filtro.getIdProcesso());
        if (filtro.getExecucaoContrato() != null) {
            q.setParameter("idExecucao", filtro.getExecucaoContrato().getId());
        }
        if (filtro.getExecucaoProcesso() != null) {
            q.setParameter("idExecucao", filtro.getExecucaoProcesso().getId());
        }
        List<Object[]> resultList = q.getResultList();
        if (Util.isListNullOrEmpty(resultList)) {
            return Lists.newArrayList();
        }
        List<ResumoExecucaoVO> movimentos = Lists.newArrayList();
        for (Object[] obj : resultList) {
            ResumoExecucaoVO ex = new ResumoExecucaoVO();
            ex.setId(((BigDecimal) obj[0]).longValue());
            ex.setNumero(((BigDecimal) obj[1]).longValue());
            ex.setData((Date) obj[2]);
            ex.setValorExecucao((BigDecimal) obj[3]);
            ex.setTipoExecucao(TipoResumoExecucao.valueOf((String) obj[4]));
            ex.setOrigemExecucao(buscarOrigemExecucao(ex.getId()));
            ex.setItens(buscarItens(ex, filtro));
            ex.setFontes(buscarFontes(ex));
            ex.setEstornosExecucao(buscarEstornoExecucao(ex));
            ex.setEmpenhos(buscarEmpenho(ex));
            if (!Util.isListNullOrEmpty(ex.getIdsEmpenhos())) {
                ex.getEstornosEmpenho().addAll(buscarEstornoEmpenho(ex.getIdsEmpenhos()));
            }
            movimentos.add(ex);
        }
        return movimentos;
    }

    public ResumoExecucaoOrigemVO buscarOrigemExecucao(Long idExecucao) {
        String sql = " " +
            " select distinct excont.idorigem                                 as id_origem, " +
            "                case when excont.origem = 'ADITIVO' then excont.origem || '_CONTRATO' " +
            "                     when excont.origem = 'APOSTILAMENTO' then excont.origem || '_CONTRATO' " +
            "                else excont.origem end                                          as origem, " +
            "                coalesce(c.numerotermo, ac.numero)               as numero, " +
            "                ex.ano                                           as ano, " +
            "                coalesce(c.dataaprovacao, ac.dataaprovacao)      as data " +
            " from execucaocontrato excont " +
            "         left join contrato c on c.id = excont.idorigem " +
            "         left join alteracaocontratual ac on ac.id = excont.idorigem " +
            "         inner join exercicio ex on ex.id = coalesce(c.exerciciocontrato_id, ac.exercicio_id) " +
            " where excont.id = :idExecucao " +
            "   union all " +
            " select distinct exproc.idorigem                                                as id_origem, " +
            "                case when exproc.origem = 'ADITIVO' then exproc.origem || '_ATA' " +
            "                     when exproc.origem = 'APOSTILAMENTO' then exproc.origem || '_ATA' " +
            "                else exproc.origem end                                          as origem, " +
            "                coalesce(to_char(ata.numero), to_char(disp.numerodadispensa), ac.numero) as numero, " +
            "                ex.ano                                                          as ano, " +
            "                coalesce(ata.datainicio, disp.datadadispensa, ac.dataaprovacao) as data " +
            " from execucaoprocesso exproc " +
            "   left join execucaoprocessoata exata on exata.execucaoprocesso_id = exproc.id " +
            "   left join ataregistropreco ata on ata.id = exata.ataregistropreco_id " +
            "   left join licitacao lic on lic.id = ata.licitacao_id " +
            "   left join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = exproc.id " +
            "   left join dispensadelicitacao disp on disp.id = exdisp.dispensalicitacao_id " +
            "   left join alteracaocontratual ac on ac.id = exproc.idorigem " +
            "   inner join exercicio ex on ex.id = coalesce(lic.exercicio_id, disp.exerciciodadispensa_id) " +
            " where exproc.id = :idExecucao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExecucao", idExecucao);
        q.setMaxResults(1);
        Object[] obj = (Object[]) q.getSingleResult();
        if (obj == null) {
            return null;
        }
        ResumoExecucaoOrigemVO origem = new ResumoExecucaoOrigemVO();
        origem.setId(((BigDecimal) obj[0]).longValue());
        origem.setOrigem(ResumoExecucaoOrigemVO.OrigemExecucao.valueOf((String) obj[1]));
        if (!origem.getOrigem().isContrato()) {
            origem.setNumero((String) obj[2]);
            origem.setAno(((BigDecimal) obj[3]).intValue());
            origem.setData((Date) obj[4]);
        }
        return origem;
    }

    public List<ResumoExecucaoItemVO> buscarItens(ResumoExecucaoVO resumoExecucaoVO, FiltroResumoExecucaoVO filtro) {
        String sql = " ";
        Query q;
        if (filtro.getTipoResumoExecucao().isExecucaoContrato()) {
            sql = " select item.* from execucaocontratoitem item " +
                "   where item.execucaocontrato_id = :idExecucao ";
            q = em.createNativeQuery(sql, ExecucaoContratoItem.class);
        } else {
            sql = " select item.* from execucaoprocessoitem item " +
                " where item.execucaoprocesso_id = :idExecucao ";
            q = em.createNativeQuery(sql, ExecucaoProcessoItem.class);
        }
        q.setParameter("idExecucao", resumoExecucaoVO.getId());
        if (Util.isListNullOrEmpty(q.getResultList())) {
            return Lists.newArrayList();
        }
        List<ResumoExecucaoItemVO> movimentos = Lists.newArrayList();
        if (filtro.getTipoResumoExecucao().isExecucaoContrato()) {
            List<ExecucaoContratoItem> resultList = q.getResultList();
            for (ExecucaoContratoItem itemCont : resultList) {
                ResumoExecucaoItemVO item = new ResumoExecucaoItemVO(itemCont);
                movimentos.add(item);
            }
            return movimentos;
        }
        List<ExecucaoProcessoItem> resultList = q.getResultList();
        for (ExecucaoProcessoItem itemProc : resultList) {
            ResumoExecucaoItemVO item = new ResumoExecucaoItemVO(itemProc);
            movimentos.add(item);
        }
        return movimentos;
    }

    public List<ResumoExecucaoFonteVO> buscarFontes(ResumoExecucaoVO resumoExecucaoVO) {
        String sql = " ";
        if (resumoExecucaoVO.getTipoExecucao().isExecucaoContrato()) {
            sql += " select" +
                "    do.codigo || '  -  ' || cdesp.codigo || '  -  ' || cd.codigo || '-' || cd.descricao || ' / ' || fdr.codigo || '-' ||" +
                "    fdr.descricao as fonte," +
                "    exfont.valor        as valor" +
                "  from execucaocontratotipo exres" +
                "         inner join execucaocontratotipofonte exfont on exfont.execucaocontratotipo_id = exres.id" +
                "         inner join fontedespesaorc fdo on fdo.id = exfont.fontedespesaorc_id" +
                "         inner join provisaoppafonte ppf on ppf.id = fdo.provisaoppafonte_id" +
                "         inner join conta cd on cd.id = ppf.destinacaoderecursos_id" +
                "         inner join contadedestinacao cdr on cdr.id = cd.id" +
                "         inner join fontederecursos fdr on fdr.id = cdr.fontederecursos_id" +
                "         inner join despesaorc do on do.id = fdo.despesaorc_id" +
                "         inner join provisaoppadespesa ppd on ppd.id = do.provisaoppadespesa_id" +
                "         inner join conta cdesp on cdesp.id = ppd.contadedespesa_id" +
                "  where exres.execucaocontrato_id = :idExecucao ";
        } else {
            sql += " select " +
                "    do.codigo || '  -  ' || cdesp.codigo || '  -  ' || cd.codigo || '-' || cd.descricao || ' / ' || fdr.codigo || '-' || " +
                "    fdr.descricao as fonte, " +
                "    exfont.valor        as valor " +
                "  from execucaoprocessoreserva exres " +
                "         inner join execucaoprocessofonte exfont on exfont.execucaoprocessoreserva_id = exres.id " +
                "         inner join fontedespesaorc fdo on fdo.id = exfont.fontedespesaorc_id " +
                "         inner join provisaoppafonte ppf on ppf.id = fdo.provisaoppafonte_id " +
                "         inner join conta cd on cd.id = ppf.destinacaoderecursos_id " +
                "         inner join contadedestinacao cdr on cdr.id = cd.id " +
                "         inner join fontederecursos fdr on fdr.id = cdr.fontederecursos_id " +
                "         inner join despesaorc do on do.id = fdo.despesaorc_id " +
                "         inner join provisaoppadespesa ppd on ppd.id = do.provisaoppadespesa_id " +
                "         inner join conta cdesp on cdesp.id = ppd.contadedespesa_id " +
                " where exres.execucaoprocesso_id = :idExecucao ";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("idExecucao", resumoExecucaoVO.getId());
        List<Object[]> resultList = q.getResultList();
        if (Util.isListNullOrEmpty(resultList)) {
            return Lists.newArrayList();
        }
        List<ResumoExecucaoFonteVO> fontes = Lists.newArrayList();
        for (Object[] obj : resultList) {
            ResumoExecucaoFonteVO font = new ResumoExecucaoFonteVO();
            font.setFonteDespesaOrc((String) obj[0]);
            font.setValorTotal((BigDecimal) obj[1]);
            fontes.add(font);
        }
        return fontes;
    }

    public List<ResumoExecucaoEstornoVO> buscarEstornoExecucao(ResumoExecucaoVO resumoExecucaoVO) {
        String sql = "" +
            " select * from ( " +
            " select est.id," +
            "        est.numero," +
            "        est.datalancamento," +
            "        est.valortotal " +
            "  from execucaocontratoestorno est " +
            "  where est.execucaocontrato_id = :idExecucao" +
            " union all" +
            " select est.id," +
            "        est.numero," +
            "        est.datalancamento," +
            "        est.valor " +
            "  from execucaoprocessoestorno est " +
            "  where est.execucaoprocesso_id = :idExecucao " +
            ") order by numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExecucao", resumoExecucaoVO.getId());
        List<Object[]> resultList = q.getResultList();
        if (Util.isListNullOrEmpty(resultList)) {
            return Lists.newArrayList();
        }
        List<ResumoExecucaoEstornoVO> movimentos = Lists.newArrayList();
        for (Object[] obj : resultList) {
            ResumoExecucaoEstornoVO estEx = new ResumoExecucaoEstornoVO();
            estEx.setId(((BigDecimal) obj[0]).longValue());
            estEx.setNumero(((BigDecimal) obj[1]).toString());
            estEx.setData((Date) obj[2]);
            estEx.setValor((BigDecimal) obj[3]);
            estEx.setResumoExecucaoVO(resumoExecucaoVO);
            List<SolicitacaoEmpenhoEstorno> solicitacoesEstorno = buscarSolicitacaoEmpenhoEstorno(estEx.getId());
            estEx.setSolicitacoesEstorno(solicitacoesEstorno);
            movimentos.add(estEx);
        }
        return movimentos;
    }

    public List<ResumoExecucaoEmpenhoVO> buscarEmpenho(ResumoExecucaoVO resumoExecucaoVO) {
        String sql = "" +
            "    select * from ( " +
            " select sol.id                    as id_sol, " +
            "       sol.datasolicitacao       as data_sol, " +
            "       emp.id                    as id, " +
            "       emp.numero                as numero, " +
            "       emp.dataempenho           as data, " +
            "       emp.categoriaorcamentaria as categoria, " +
            "       emp.complementohistorico  as historico, " +
            "       emp.valor                 as valor " +
            " from execucaocontratoempenho exemp " +
            "         inner join solicitacaoempenho sol on exemp.solicitacaoempenho_id = sol.id " +
            "         left join empenho emp on exemp.empenho_id = emp.id " +
            " where exemp.execucaocontrato_id = :idExecucao " +
            " union all  " +
            " select sol.id                    as id_sol, " +
            "       sol.datasolicitacao       as data_sol, " +
            "       emp.id                    as id, " +
            "       emp.numero                as numero, " +
            "       emp.dataempenho           as data, " +
            "       emp.categoriaorcamentaria as categoria, " +
            "       emp.complementohistorico  as historico, " +
            "       coalesce(sol.valor, emp.valor)  as valor " +
            " from execucaoprocessoempenho exemp " +
            "         inner join solicitacaoempenho sol on exemp.solicitacaoempenho_id = sol.id " +
            "         left join empenho emp on exemp.empenho_id = emp.id " +
            " where exemp.execucaoprocesso_id = :idExecucao " +
            " ) order by numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExecucao", resumoExecucaoVO.getId());
        List<Object[]> resultList = q.getResultList();
        if (Util.isListNullOrEmpty(resultList)) {
            return Lists.newArrayList();
        }
        List<ResumoExecucaoEmpenhoVO> movimentos = Lists.newArrayList();
        for (Object[] obj : resultList) {
            ResumoExecucaoEmpenhoVO emp = new ResumoExecucaoEmpenhoVO();
            emp.setIdSolitiacaoEmpenho(((BigDecimal) obj[0]).longValue());
            emp.setDataSolicitacaoEmpenho((Date) obj[1]);
            emp.setId(obj[2] != null ? ((BigDecimal) obj[2]).longValue() : null);
            emp.setNumero(obj[3] != null ? (String) obj[3] : null);
            emp.setData(obj[4] != null ? (Date) obj[4] : null);
            emp.setCategoria(obj[5] != null ? CategoriaOrcamentaria.valueOf((String) obj[5]) : null);
            emp.setHistorico(obj[6] != null ? (String) obj[6] : null);
            emp.setValor(obj[7] != null ? (BigDecimal) obj[7] : null);
            if (emp.getId() != null) {
                emp.setEmpenhoResto(buscarEmpenhoResto(emp.getId()));
            }
            movimentos.add(emp);
        }
        return movimentos;
    }

    public ResumoExecucaoEmpenhoVO buscarEmpenhoResto(Long idEmpenho) {
        String sql = " " +
            " select " +
            "       emp.id                    as id, " +
            "       emp.numero                as numero, " +
            "       emp.dataempenho           as data, " +
            "       emp.categoriaorcamentaria as categoria, " +
            "       emp.complementohistorico  as historico, " +
            "       emp.valor                 as valor " +
            " from empenho emp " +
            " where emp.empenho_id = :idEmpenho" +
            " order by emp.numero  ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEmpenho", idEmpenho);
        q.setMaxResults(1);
        try {
            Object[] obj = (Object[]) q.getSingleResult();
            ResumoExecucaoEmpenhoVO resto = new ResumoExecucaoEmpenhoVO();
            resto.setId(((BigDecimal) obj[0]).longValue());
            resto.setNumero((String) obj[1]);
            resto.setData((Date) obj[2]);
            resto.setCategoria(CategoriaOrcamentaria.valueOf((String) obj[3]));
            resto.setHistorico((String) obj[4]);
            resto.setValor((BigDecimal) obj[5]);
            return resto;
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<ResumoExecucaoEmpenhoEstornoVO> buscarEstornoEmpenho(List<Long> idsEmpenho) {
        String sql = " " +
            " select est.id                    as id, " +
            "       est.numero                as numero, " +
            "       cast(est.dataestorno as date) as data, " +
            "       est.categoriaorcamentaria as categoria, " +
            "       est.complementohistorico  as historico, " +
            "       est.valor                 as valor " +
            " from empenhoestorno est " +
            "   where est.empenho_id in (:idsEmpenho)" +
            "  order by est.numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idsEmpenho", idsEmpenho);
        List<Object[]> resultList = q.getResultList();
        if (Util.isListNullOrEmpty(resultList)) {
            return Lists.newArrayList();
        }
        List<ResumoExecucaoEmpenhoEstornoVO> movimentos = Lists.newArrayList();
        for (Object[] obj : resultList) {
            ResumoExecucaoEmpenhoEstornoVO ex = new ResumoExecucaoEmpenhoEstornoVO();
            ex.setId(((BigDecimal) obj[0]).longValue());
            ex.setNumero((String) obj[1]);
            ex.setData((Date) obj[2]);
            ex.setCategoria(CategoriaOrcamentaria.valueOf((String) obj[3]));
            ex.setHistorico((String) obj[4]);
            ex.setValor((BigDecimal) obj[5]);
            movimentos.add(ex);
        }
        return movimentos;
    }

    public List<SolicitacaoEmpenhoEstorno> buscarSolicitacaoEmpenhoEstorno(Long idExecucaoEstorno) {
        String sql = " select sol.* from solicitacaoempenhoestorno sol " +
            "           left join execucaocontratoempenhoest exsol on exsol.solicitacaoempenhoestorno_id = sol.id " +
            "           left join execucaocontratoestorno est on est.id = exsol.execucaocontratoestorno_id " +
            "           left join execucaoprocessoempenhoest exsolata on exsolata.solicitacaoempenhoestorno_id = sol.id " +
            "           left join execucaoprocessoestorno estexproc on estexproc.id = exsolata.execucaoprocessoestorno_id " +
            "          where coalesce(est.id, estexproc.id) = :idExecEst " +
            "          order by sol.datasolicitacao ";
        Query q = em.createNativeQuery(sql, SolicitacaoEmpenhoEstorno.class);
        q.setParameter("idExecEst", idExecucaoEstorno);
        List resultList = q.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultList;
    }
}
