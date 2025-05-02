/*
 * Codigo gerado automaticamente em Thu Jan 12 14:49:52 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OrigemReservaFonte;
import br.com.webpublico.enums.TipoOperacaoORC;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class ReservaDotacaoFacade extends AbstractFacade<ReservaDotacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ReservaFonteDespesaOrcFacade reservaFonteDespesaOrcFacade;
    @EJB
    private LiberacaoFonteDespesaORCFacade liberacaoFonteDespesaORCFacade;
    @EJB
    private DespesaORCFacade despesaORCFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReservaDotacaoFacade() {
        super(ReservaDotacao.class);
    }

    @Override
    public void salvarNovo(ReservaDotacao entity) {
        em.persist(entity);
        ReservaFonteDespesaOrc res = reservaFonteDespesaOrcFacade.gerarReserva(entity.getValor(), entity.getFonteDespesaORC(),
            entity.getOrigemReservaFonte(), TipoOperacaoORC.NORMAL, entity.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getUnidadeOrganizacional(),
            entity.getId().toString(),  "Reserva de dotação feita manualmente pelo departamento de planejamento.");
        ReservaDotacaoReservaFonte rdrf = new ReservaDotacaoReservaFonte();
        rdrf.setReservaDotacao(entity);
        rdrf.setReservaFonteDespesaOrc(res);
        em.persist(rdrf);
    }

    @Override
    public void salvar(ReservaDotacao entity) {
        em.merge(entity);
    }

    public void geraLiberacao(ReservaDotacao entity) {
        ReservaFonteDespesaOrc res = reservaFonteDespesaOrcFacade.recuperaReservaPorReservaDotacao(entity);
        entity.setLiberado(true);
        em.merge(entity);
        if (res != null) {
            LiberacaoFonteDespesaOrc lib = liberacaoFonteDespesaORCFacade.geraLiberacao(entity.getValor(), entity.getFonteDespesaORC(), OrigemReservaFonte.ATO_LEGAL, TipoOperacaoORC.ESTORNO, res);
            ReservaDotacaoLiberacaoFonte rl = new ReservaDotacaoLiberacaoFonte();
            rl.setLiberacaoFonteDespesaOrc(lib);
            rl.setReservaDotacao(entity);
            em.persist(rl);
        }
    }

    public DespesaORCFacade getDespesaORCFacade() {
        return despesaORCFacade;
    }

    public List<Object[]> buscarBloqueios(Exercicio exercicio, Date dataOperacao) {
        String sql = "         select extract(year from dataMovimento) as ano, " +
            "        extract(month from dataMovimento) as mes, " +
            "        extract(day from dataMovimento) as dia, " +
            "        substr(contaCodigo, 0, 12) as contaCodigo, " +
            "        unidade, " +
            "        funcaoCodigo, " +
            "        programaCodigo, " +
            "        subFuncaoCodigo, " +
            "        tipoAcaoCodigo, " +
            "        acaoCodigo, " +
            "        fonteCodigo, " +
            "        sum(valor) as valor " +
            " from ( " +
            " select res.dataReserva as dataMovimento, " +
            "        c.codigo as contaCodigo, " +
            "        vw.codigo as unidade, " +
            "        f.codigo as funcaoCodigo, " +
            "        prog.codigo as programaCodigo, " +
            "        sf.codigo as subFuncaoCodigo, " +
            "        tpa.codigo as tipoAcaoCodigo, " +
            "        a.codigo as acaoCodigo, " +
            "        fr.codigo as fonteCodigo, " +
            "        res.valor as valor " +
            "   from ReservaDotacao res  " +
            "  INNER JOIN FONTEDESPESAORC fontdesp ON res.FONTEDESPESAORC_ID = fontdesp.ID   " +
            "  inner join PROVISAOPPAFONTE ppf on fontdesp.PROVISAOPPAFONTE_ID = ppf.id   " +
            "  inner join despesaorc desp on fontdesp.DESPESAORC_ID = desp.id   " +
            "  inner join provisaoppadespesa provdesp on ppf.provisaoppadespesa_id = provdesp.id   " +
            "  inner join contadedestinacao cd on ppf.DESTINACAODERECURSOS_ID = cd.id   " +
            "  inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_id = fr.id   " +
            "  INNER JOIN CONTA C ON provdesp.CONTADEDESPESA_ID = C.ID " +
            "  INNER JOIN PLANODECONTASEXERCICIO PCE ON pce.planodedespesas_id = C.PLANODECONTAS_ID " +
            "  inner join exercicio ex on pce.exercicio_id = ex.id " +
            "  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = PROVDESP.UNIDADEORGANIZACIONAL_ID  " +
            "  INNER JOIN SUBACAOPPA SUB ON SUB.ID = provdesp.subacaoppa_id " +
            "  INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " +
            "  INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " +
            "  INNER JOIN programappa PROG ON PROG.ID = A.programa_id " +
            "  INNER JOIN FUNCAO F ON F.ID = A.funcao_id " +
            "  INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " +
            "  where ex.id = :exercicio " +
            "    AND TO_DATE(:dataOperacao, 'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataOperacao, 'DD/MM/YYYY')) " +
            " union all " +
            "  " +
            " select d.realizadaem as dataMovimento, " +
            "        c.codigo as contaCodigo, " +
            "        vw.codigo as unidade, " +
            "        f.codigo as funcaoCodigo, " +
            "        prog.codigo as programaCodigo, " +
            "        sf.codigo as subFuncaoCodigo, " +
            "        tpa.codigo as tipoAcaoCodigo, " +
            "        a.codigo as acaoCodigo, " +
            "        fr.codigo as fonteCodigo, " +
            "        dir.valor as valor " +
            "     from dotsolmat d  " +
            "  inner join solicitacaomaterial sm on sm.id = d.solicitacaomaterial_id   " +
            "  inner join dotacaosolmatitem di on di.dotacaosolicitacaomaterial_id = d.id  " +
            "  inner join dotacaosolmatitemfonte dir on dir.dotacaosolmatitem_id = di.id  " +
            "  inner join fontedespesaorc fontdesp on fontdesp.id = dir.fontedespesaorc_id  " +
            "  inner join PROVISAOPPAFONTE ppf on fontdesp.PROVISAOPPAFONTE_ID = ppf.id   " +
            "  inner join despesaorc desp on fontdesp.DESPESAORC_ID = desp.id   " +
            "  inner join provisaoppadespesa provdesp on ppf.provisaoppadespesa_id = provdesp.id   " +
            "  inner join contadedestinacao cd on ppf.DESTINACAODERECURSOS_ID = cd.id   " +
            "  inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_id = fr.id   " +
            "  INNER JOIN CONTA C ON provdesp.CONTADEDESPESA_ID = C.ID " +
            "  INNER JOIN PLANODECONTASEXERCICIO PCE ON pce.planodedespesas_id = C.PLANODECONTAS_ID " +
            "  inner join exercicio ex on pce.exercicio_id = ex.id " +
            "  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = PROVDESP.UNIDADEORGANIZACIONAL_ID  " +
            "  INNER JOIN SUBACAOPPA SUB ON SUB.ID = provdesp.subacaoppa_id " +
            "  INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " +
            "  INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " +
            "  INNER JOIN programappa PROG ON PROG.ID = A.programa_id " +
            "  INNER JOIN FUNCAO F ON F.ID = A.funcao_id " +
            "  INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " +
            "  where ex.id = :exercicio " +
            "    AND TO_DATE(:dataOperacao, 'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataOperacao, 'DD/MM/YYYY')) " +
            " ) " +
            " group by dataMovimento, " +
            "          contaCodigo, " +
            "          unidade, " +
            "          funcaoCodigo, " +
            "          programaCodigo, " +
            "          subFuncaoCodigo, " +
            "          tipoAcaoCodigo, " +
            "          acaoCodigo, " +
            "          fonteCodigo " +
            " order by dataMovimento, " +
            "          contaCodigo, " +
            "          unidade, " +
            "          funcaoCodigo, " +
            "          programaCodigo, " +
            "          subFuncaoCodigo, " +
            "          tipoAcaoCodigo, " +
            "          acaoCodigo, " +
            "          fonteCodigo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        return q.getResultList();
    }
}
