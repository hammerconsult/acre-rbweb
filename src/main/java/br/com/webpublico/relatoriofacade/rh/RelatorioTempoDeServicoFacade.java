package br.com.webpublico.relatoriofacade.rh;

import br.com.webpublico.entidades.AverbacaoTempoServico;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.rh.*;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.MatriculaFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Alex on 25/10/17.
 */
@Stateless
public class RelatorioTempoDeServicoFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public List<RelatorioCertidaoTempoServico> buscarDadosRelatorioTempoServico(Long contratoFPID, boolean detalhado) {
        List<RelatorioCertidaoTempoServicoSubReport1> sub1 = buscarDadosSubReport1(contratoFPID);
        int totalLiquidoTempoEfetivo = calcularTotaTempoEfetivoRelatorio1(sub1);
        List<RelatorioCertidaoTempoServicoSubReport2> sub2 = buscarDadosSubReport2(totalLiquidoTempoEfetivo);
        List<RelatorioCertidaoTempoServicoSubReport3> sub3 = buscarDadosSubReport3(contratoFPID);
        List<RelatorioCertidaoTempoServicoSubReport4> sub4 = buscarDadosSubReport4(contratoFPID);
        Integer totalAverbado = calcularTotalAverbadoPeloRelatorio4(sub4);
        List<RelatorioCertidaoTempoServicoSubReport5> sub5 = buscarDadosSubReport5(contratoFPID);
        sub5 = corrigirTempoTotalAverbadoAndTotalExercicio(sub5, totalAverbado, totalLiquidoTempoEfetivo);
        List<RelatorioCertidaoTempoServicoSubReport6> sub6 = buscarDadosSubReport6(contratoFPID);
        return buscarDadosRelatorioPrincipal(contratoFPID,
            sub1,
            sub2,
            sub3,
            sub4,
            sub5,
            sub6,
            detalhado);
    }

    private List<RelatorioCertidaoTempoServicoSubReport5> corrigirTempoTotalAverbadoAndTotalExercicio(List<RelatorioCertidaoTempoServicoSubReport5> sub5, Integer totalAverbado, Integer totalLiquidoTempoEfetivo) {
        for (RelatorioCertidaoTempoServicoSubReport5 item : sub5) {
            return sobrescreverValorFinalSub5(item, totalAverbado, totalLiquidoTempoEfetivo);
        }
        return sub5;
    }

    private List<RelatorioCertidaoTempoServicoSubReport5> sobrescreverValorFinalSub5(RelatorioCertidaoTempoServicoSubReport5 item, Integer totalAverbado, Integer totalLiquidoTempoEfetivo) {
        String sql =
            " SELECT sum(coalesce(total_exercicio, 0))                                                               AS total_exercicio, " +
                "       sum(coalesce(total_averbado, 0))                                                                AS total_averbado, " +
                "       sum((coalesce(total_exercicio, 0)) + sum(coalesce(total_averbado, 0)))                          AS total_geral, " +
                "       replace( " +
                "         replace(frt_extenso_monetario(sum(coalesce(total_exercicio, 0)) + sum(coalesce(total_averbado, 0))), 'REAL'), " +
                "         'REAIS')                                                                                      AS total_geral_extenso, " +
                " " +
                "       trunc((sum(coalesce(total_exercicio, 0) + coalesce(total_averbado, 0)) / 365.25))               AS anos, " +
                "       trunc((sum(coalesce(total_exercicio, 0) + coalesce(total_averbado, 0)) / 365.25 - " +
                "              trunc((sum(coalesce(total_exercicio, 0) + coalesce(total_averbado, 0)) / 365.25))) * 12) AS meses, " +
                "       round((((sum(coalesce(total_exercicio, 0) + coalesce(total_averbado, 0)) / 365.25 - " +
                "                trunc((sum(coalesce(total_exercicio, 0) + coalesce(total_averbado, 0)) / 365.25))) * 12) - trunc( " +
                "                                                                                                             (sum(coalesce(total_exercicio, 0) + coalesce(total_averbado, 0)) / 365.25 - " +
                "                                                                                                              trunc( " +
                "                                                                                                                (sum(coalesce(total_exercicio, 0) + coalesce(total_averbado, 0)) / 365.25))) * " +
                "                                                                                                             12)) * " +
                "             30)                                                                                       AS dias " +
                "from (select :exercicio as total_exercicio, :averbado total_averbado from dual) group by total_averbado, total_exercicio ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", totalLiquidoTempoEfetivo);
        q.setParameter("averbado", totalAverbado);

        if (!q.getResultList().isEmpty()) {
            return RelatorioCertidaoTempoServicoSubReport5.preencherDados(q.getResultList());
        }
        return Lists.newArrayList();
    }

    private Integer calcularTotalAverbadoPeloRelatorio4(List<RelatorioCertidaoTempoServicoSubReport4> sub4) {
        Integer total = 0;
        for (RelatorioCertidaoTempoServicoSubReport4 item : sub4) {
            total = total + (item.getTempoAverbado() != null ? item.getTempoAverbado().intValue() : 0);
        }
        return total;
    }

    private Integer calcularTotaTempoEfetivoRelatorio1(List<RelatorioCertidaoTempoServicoSubReport1> sub1) {
        int total = 0;
        for (RelatorioCertidaoTempoServicoSubReport1 item : sub1) {
            int tempoBruto = item.getTempoBruto() != null ? item.getTempoBruto() : 0;
            int faltasInjustificadas = item.getFaltasInjustificadas() != null ? item.getFaltasInjustificadas() : 0;
            int licencaQueDeduzemDias = item.getLicencasQueDeduzemDias() != null ? item.getLicencasQueDeduzemDias() : 0;
            total = total + tempoBruto - faltasInjustificadas - licencaQueDeduzemDias;

        }
        return total;
    }

    public List<RelatorioCertidaoTempoServicoSubReport5> buscarDadosSubReport5(Long contratoFPID) {
        String sql =
            " SELECT  " +
                "    sum(coalesce(total_exercicio, 0))                                                                             AS total_exercicio,  " +
                "    sum(coalesce(total_averbado,  " +
                "                 0))                                                                                              AS total_averbado,  " +
                "    sum((coalesce(total_exercicio, 0)) + sum(coalesce(total_averbado,  " +
                "                                                      0)))                                                        AS total_geral,  " +
                "    replace(replace(frt_extenso_monetario(sum(coalesce(total_exercicio, 0)) + sum(coalesce(total_averbado, 0))), 'REAL'),  " +
                "            'REAIS')                                                                                              AS total_geral_extenso,  " +
                "    trunc((sum(coalesce(total_exercicio, 0) + coalesce(total_averbado, 0)) / 365.25))                             AS anos,  " +
                "    trunc((sum(coalesce(total_exercicio, 0) + coalesce(total_averbado, 0)) / 365.25 -  " +
                "           trunc((sum(coalesce(total_exercicio, 0) + coalesce(total_averbado, 0)) / 365.25))) *  " +
                "          12)                                                                                                     AS meses,  " +
                "    round((((sum(coalesce(total_exercicio, 0) + coalesce(total_averbado, 0)) / 365.25 -  " +
                "             trunc((sum(coalesce(total_exercicio, 0) + coalesce(total_averbado, 0)) / 365.25))) * 12) -  " +
                "           trunc((sum(coalesce(total_exercicio, 0) + coalesce(total_averbado, 0)) / 365.25 -  " +
                "                  trunc((sum(coalesce(total_exercicio, 0) + coalesce(total_averbado, 0)) / 365.25))) * 12)) * 30) AS dias  " +
                "  FROM (SELECT  " +
                "          (SELECT sum(CASE  " +
                "                      WHEN (to_date(to_char(vinculo2.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') IS NOT NULL AND  " +
                "                            extract(YEAR FROM to_date(to_char(vinculo2.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy')) =  " +
                "                            extract(YEAR FROM to_date(to_char(coalesce(vinculo2.alteracaoVinculo,vinculo2.inicioVigencia), 'dd/MM/yyyy'), 'dd/MM/yyyy')))  " +
                "                        THEN  " +
                "                          (to_date(to_char(vinculo2.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') -  " +
                "                           to_date(to_char(coalesce(vinculo2.alteracaoVinculo,vinculo2.inicioVigencia), 'dd/MM/yyyy'), 'dd/MM/yyyy') + 1)  " +
                "                      WHEN (extract(YEAR FROM to_date(to_char(coalesce(vinculo2.alteracaoVinculo,vinculo2.inicioVigencia), 'dd/MM/yyyy'), 'dd/MM/yyyy')) =  " +
                "                            exercicio.ano)  " +
                "                        THEN  " +
                "                          (to_date(  " +
                "                               CASE WHEN extract(YEAR FROM current_date) = exercicio.ano  " +
                "                                 THEN  " +
                "                                   to_char(current_date, 'dd/MM/yyyy')  " +
                "                               ELSE  " +
                "                                 '31/12/' || cast(exercicio.ano AS VARCHAR(255))  " +
                "                               END, 'dd/MM/yyyy') -  " +
                "                           to_date(coalesce(vinculo2.alteracaoVinculo,vinculo2.inicioVigencia)) + 1)  " +
                "                      WHEN (  " +
                "                        (to_date(to_char(vinculo2.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') IS NOT NULL)  " +
                "                        AND  " +
                "                        (extract(YEAR FROM to_date(to_char(vinculo2.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy')) =  " +
                "                         exercicio.ano)  " +
                "                      )  " +
                "                        THEN  " +
                "                          (to_date(to_char(vinculo2.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') -  " +
                "                           trunc(to_date(to_char(vinculo2.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy'), 'YYYY') + 1)  " +
                "                      WHEN exercicio.ano = extract(YEAR FROM current_date)  " +
                "                        THEN  " +
                "                          (to_date(current_date) -  " +
                "                           trunc(to_date('01/01/' || cast(exercicio.ano AS VARCHAR(255)), 'dd/MM/yyyy'), 'YYYY') + 1)  " +
                "                      ELSE  " +
                "                        (to_date('31/12/' || cast(exercicio.ano AS VARCHAR(255)), 'dd/MM/yyyy') -  " +
                "                         trunc(to_date('01/01/' || cast(exercicio.ano AS VARCHAR(255)), 'dd/MM/yyyy'), 'YYYY') + 1)  " +
                "                      END -  " +
                "                      (coalesce((SELECT sum((CASE WHEN (extract(YEAR FROM trunc(LEAST(coalesce(to_date(to_char(  " +
                "                                                                                                           vinculo2.finalVigencia,  " +
                "                                                                                                           'dd/MM/yyyy'),  " +
                "                                                                                                       'dd/MM/yyyy'),  " +
                "                                                                                               trunc(current_date)),  " +
                "                                                                                      faltasInjustificadas.fim))) >  " +
                "                                                        exercicio.ano)  " +
                "                        THEN to_date('31/12/' || cast(exercicio.ano AS VARCHAR(4)), 'dd/MM/yyyy')  " +
                "                                             ELSE trunc(LEAST(coalesce(  " +
                "                                                                  to_date(to_char(vinculo2.finalVigencia, 'dd/MM/yyyy'),  " +
                "                                                                          'dd/MM/yyyy'), trunc(current_date)),  " +
                "                                                              faltasInjustificadas.fim)) END -  " +
                "                                             CASE WHEN (extract(YEAR FROM faltasInjustificadas.inicio) < exercicio.ano)  " +
                "                                               THEN to_date('01/01/' || cast(exercicio.ano AS VARCHAR(4)), 'dd/MM/yyyy')  " +
                "                                             ELSE faltasInjustificadas.inicio END) + 1)  " +
                "                                 FROM vwfalta faltasInjustificadas  " +
                "                                 WHERE faltasInjustificadas.contratofp_id = contrato.id  " +
                "                                       AND faltasInjustificadas.tipofalta = 'FALTA_INJUSTIFICADA'  " +
                "                                       AND exercicio.ano >= extract(YEAR FROM faltasInjustificadas.inicio)  " +
                "                                       AND exercicio.ano <= extract(YEAR FROM faltasInjustificadas.fim)), 0) +  " +
                "                       coalesce((SELECT sum((CASE WHEN (extract(YEAR FROM trunc(  " +
                "                           LEAST(coalesce(to_date(to_char(vinculo2.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy'), current_date),  " +
                "                                 afastamento.termino))) > exercicio.ano)  " +
                "                         THEN to_date('31/12/' || cast(exercicio.ano AS VARCHAR(4)), 'dd/MM/yyyy')  " +
                "                                             ELSE trunc(LEAST(coalesce(  " +
                "                                                                  to_date(to_char(vinculo2.finalVigencia, 'dd/MM/yyyy'),  " +
                "                                                                          'dd/MM/yyyy'), current_date),  " +
                "                                                              to_date(to_char(afastamento.termino, 'dd/MM/yyyy'),  " +
                "                                                                      'dd/MM/yyyy'))) END -  " +
                "                                             CASE WHEN (extract(YEAR FROM  " +
                "                                                                to_date(to_char(afastamento.inicio, 'dd/MM/yyyy'),  " +
                "                                                                        'dd/MM/yyyy')) < exercicio.ano)  " +
                "                                               THEN to_date('01/01/' || cast(exercicio.ano AS VARCHAR(4)), 'dd/MM/yyyy')  " +
                "                                             ELSE to_date(to_char(afastamento.inicio, 'dd/MM/yyyy'), 'dd/MM/yyyy') END) +  " +
                "                                            1)  " +
                "                                 FROM afastamento afastamento  " +
                "                                   INNER JOIN tipoafastamento ON afastamento.tipoafastamento_id = tipoafastamento.id  " +
                "                                 WHERE tipoafastamento.descTempoServicoAposentadoria = 0   " +
                "                                       AND coalesce(tipoafastamento.descontarTempoServicoEfetivo, 0) = :verdadeiro  " +
                "                                       AND afastamento.contratofp_id = contrato.id  " +
                "                                       AND exercicio.ano >= extract(YEAR FROM afastamento.inicio)  " +
                "                                       AND exercicio.ano <= extract(YEAR FROM afastamento.termino)), 0))  " +
                "          )  " +
                "           FROM contratofp contrato INNER JOIN vinculofp vinculo2 ON contrato.id = vinculo2.id  " +
                "             INNER JOIN matriculafp matricula ON matricula.id = vinculo2.matriculafp_id  " +
                "             INNER JOIN exercicio ON extract(YEAR FROM coalesce(vinculo2.alteracaoVinculo,vinculo2.inicioVigencia)) <= exercicio.ano  " +
                "           WHERE exercicio.ano <=  " +
                "                 extract(YEAR FROM (CASE WHEN vinculo2.finalVigencia IS NOT NULL AND vinculo2.finalVigencia < current_date  " +
                "                   THEN vinculo2.finalVigencia  " +
                "                                    ELSE current_date END)) AND vinculo2.id = vinculo1.id)" +

                " - " +

                "   COALESCE(" +
                "  (SELECT SUM((" +
                "    CASE" +
                "      WHEN ( extract(YEAR FROM TRUNC(LEAST(COALESCE(vinculo.finalVigencia, CURRENT_DATE), reintegracao.DATAREINTEGRACAO))) > exercicio.ano)" +
                "      THEN to_date ('31/12/'" +
                "        || CAST(exercicio.ano AS VARCHAR(4)), 'dd/MM/yyyy')" +
                "      ELSE TRUNC(LEAST(COALESCE(vinculo.finalVigencia, CURRENT_DATE), reintegracao.DATAREINTEGRACAO))" +
                "    END -" +
                "    CASE" +
                "      WHEN (extract(YEAR FROM exoneracao.datarescisao) < exercicio.ano)" +
                "      THEN to_date('01/01/'" +
                "        || CAST(exercicio.ano AS VARCHAR(4)), 'dd/MM/yyyy')" +
                "      ELSE exoneracao.datarescisao" +
                "    END) - 1)" +
                "  FROM reintegracao " +
                "  inner join vinculofp vinculo on reintegracao.CONTRATOFP_ID = vinculo.id " +
                "  INNER JOIN exercicio ON extract(YEAR FROM coalesce(vinculo.alteracaoVinculo, vinculo.inicioVigencia)) <= exercicio.ano" +
                "  inner join EXONERACAORESCISAO exoneracao on exoneracao.VINCULOFP_ID = vinculo.id " +
                "  WHERE vinculo.id              = :contratofpid " +
                "  AND exercicio.ano            >= extract( YEAR FROM exoneracao.datarescisao) " +
                "  AND exercicio.ano            <= extract( YEAR FROM reintegracao.DATAREINTEGRACAO)" +
                "  ), 0)" +
                " AS total_exercicio, " +

                "          (SELECT sum(tempoAverbado)  " +
                "           FROM (  " +
                "             SELECT sum((  " +
                "                          SELECT CASE  " +
                "                                 WHEN (to_date(to_char(averbacao.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') IS NOT NULL  " +
                "                                       AND extract(YEAR FROM  " +
                "                                                   (to_date(to_char(averbacao.finalVigencia, 'dd/MM/yyyy'),  " +
                "                                                            'dd/MM/yyyy'))) =  " +
                "                                           extract(YEAR FROM  " +
                "                                                   (to_date(to_char(averbacao.inicioVigencia, 'dd/MM/yyyy'),  " +
                "                                                            'dd/MM/yyyy'))))  " +
                "                                   THEN (to_date(to_char(averbacao.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') -  " +
                "                                         to_date(to_char(averbacao.inicioVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') + 1)  " +
                "                                 WHEN (  " +
                "                                   extract(YEAR FROM  " +
                "                                           to_date(to_char(averbacao.inicioVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy')) =  " +
                "                                   exercicio.ano)  " +
                "                                   THEN (to_date(CASE WHEN extract(YEAR FROM current_date) = exercicio.ano  " +
                "                                     THEN to_char(current_date, 'dd/MM/yyyy')  " +
                "                                                 ELSE '31/12/' || cast(exercicio.ano AS VARCHAR(255))  " +
                "                                                 END, 'dd/MM/yyyy') -  " +
                "                                         to_date(to_char(averbacao.inicioVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') +  " +
                "                                         1)  " +
                "                                 WHEN ((to_date(to_char(averbacao.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') IS NOT NULL)  " +
                "                                       AND  " +
                "                                       (extract(YEAR FROM  " +
                "                                                to_date(to_char(averbacao.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy')) =  " +
                "                                        exercicio.ano))  " +
                "                                   THEN (to_date(to_char(averbacao.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') -  " +
                "                                         trunc(to_date(to_char(averbacao.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy'),  " +
                "                                               'YYYY') +  " +
                "                                         1)  " +
                "                                 WHEN exercicio.ano = extract(YEAR FROM current_date)  " +
                "                                   THEN (to_date(current_date, 'dd/MM/yyyy') -  " +
                "                                         trunc(to_date('01/01/' || cast(exercicio.ano AS VARCHAR(255)), 'dd/MM/yyyy'),  " +
                "                                               'YYYY')  " +
                "                                         + 1)  " +
                "                                 ELSE (to_date('31/12/' || cast(exercicio.ano AS VARCHAR(255)), 'dd/MM/yyyy') -  " +
                "                                       trunc(to_date('01/01/' || cast(exercicio.ano AS VARCHAR(255)), 'dd/MM/yyyy'),  " +
                "                                             'YYYY') +  " +
                "                                       1)  " +
                "                                 END  " +
                "                          FROM dual)) AS tempoAverbado  " +
                "             FROM averbacaotemposervico averbacao  " +
                "               JOIN contratofp contrato ON averbacao.contratofp_id = contrato.id  " +
                "               INNER JOIN vinculofp vinculo ON vinculo.id = contrato.id  " +
                "               INNER JOIN matriculafp matricula ON matricula.id = vinculo.matriculafp_id  " +
                "               INNER JOIN exercicio ON extract(YEAR FROM averbacao.inicioVigencia) <= exercicio.ano  " +
                "             WHERE exercicio.ano <= extract(YEAR FROM averbacao.finalvigencia) AND contrato.id = :contratofpid  " +
                "                                                                                                  AND averbacao.aposentado = 1  " +
                "             GROUP BY ano))                                                           AS total_averbado,  " +
                "          coalesce((SELECT sum(CASE  " +
                "                               WHEN (to_date(to_char(vinculo2.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') IS NOT NULL AND  " +
                "                                     extract(YEAR FROM  " +
                "                                             to_date(to_char(vinculo2.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy')) =  " +
                "                                     extract(YEAR FROM  " +
                "                                             to_date(to_char(coalesce(vinculo2.alteracaoVinculo,vinculo2.inicioVigencia), 'dd/MM/yyyy'), 'dd/MM/yyyy')))  " +
                "                                 THEN  " +
                "                                   (to_date(to_char(vinculo2.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') -  " +
                "                                    to_date(to_char(coalesce(vinculo2.alteracaoVinculo,vinculo2.inicioVigencia), 'dd/MM/yyyy'), 'dd/MM/yyyy') + 1)  " +
                "                               WHEN (  " +
                "                                 extract(YEAR FROM to_date(to_char(coalesce(vinculo2.alteracaoVinculo,vinculo2.inicioVigencia), 'dd/MM/yyyy'), 'dd/MM/yyyy'))  " +
                "                                 = exercicio.ano)  " +
                "                                 THEN  " +
                "                                   (to_date(  " +
                "                                        CASE WHEN extract(YEAR FROM current_date) = exercicio.ano  " +
                "                                          THEN  " +
                "                                            to_char(current_date, 'dd/MM/yyyy')  " +
                "                                        ELSE  " +
                "                                          '31/12/' || cast(exercicio.ano AS VARCHAR(255))  " +
                "                                        END, 'dd/MM/yyyy') -  " +
                "                                    to_date(coalesce(vinculo2.alteracaoVinculo,vinculo2.inicioVigencia)) + 1)  " +
                "                               WHEN (  " +
                "                                 (to_date(to_char(vinculo2.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') IS NOT NULL)  " +
                "                                 AND  " +
                "                                 (extract(YEAR FROM to_date(to_char(vinculo2.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy'))  " +
                "                                  = exercicio.ano)  " +
                "                               )  " +
                "                                 THEN  " +
                "                                   (to_date(to_char(vinculo2.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') -  " +
                "                                    trunc(to_date(to_char(vinculo2.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy'), 'YYYY') +  " +
                "                                    1)  " +
                "                               WHEN exercicio.ano = extract(YEAR FROM current_date)  " +
                "                                 THEN  " +
                "                                   (to_date(current_date) -  " +
                "                                    trunc(to_date('01/01/' || cast(exercicio.ano AS VARCHAR(255)), 'dd/MM/yyyy'), 'YYYY')  " +
                "                                    + 1)  " +
                "                               ELSE  " +
                "                                 (to_date('31/12/' || cast(exercicio.ano AS VARCHAR(255)), 'dd/MM/yyyy') -  " +
                "                                  trunc(to_date('01/01/' || cast(exercicio.ano AS VARCHAR(255)), 'dd/MM/yyyy'), 'YYYY') +  " +
                "                                  1)  " +
                "                               END -  " +
                "                               (coalesce((SELECT sum((CASE WHEN (extract(YEAR FROM trunc(LEAST(coalesce(to_date(to_char(  " +
                "                                                                                                                    vinculo2.finalVigencia,  " +
                "                                                                                                                    'dd/MM/yyyy'),  " +
                "                                                                                                                'dd/MM/yyyy'),  " +
                "                                                                                                        trunc(current_date)),  " +
                "                                                                                               faltasInjustificadas.fim)))  " +
                "                                                                 > exercicio.ano)  " +
                "                                 THEN to_date('31/12/' || cast(exercicio.ano AS VARCHAR(4)), 'dd/MM/yyyy')  " +
                "                                                      ELSE trunc(LEAST(coalesce(to_date(to_char(vinculo2.finalVigencia,  " +
                "                                                                                                'dd/MM/yyyy'),  " +
                "                                                                                        'dd/MM/yyyy'), trunc(current_date)),  " +
                "                                                                       faltasInjustificadas.fim)) END -  " +
                "                                                      CASE WHEN (extract(YEAR FROM faltasInjustificadas.inicio) <  " +
                "                                                                 exercicio.ano)  " +
                "                                                        THEN to_date('01/01/' || cast(exercicio.ano AS VARCHAR(4)),  " +
                "                                                                     'dd/MM/yyyy')  " +
                "                                                      ELSE faltasInjustificadas.inicio END) + 1)  " +
                "                                          FROM vwfalta faltasInjustificadas  " +
                "                                          WHERE faltasInjustificadas.contratofp_id = contrato.id  " +
                "                                                AND faltasInjustificadas.tipofalta = 'FALTA_INJUSTIFICADA'  " +
                "                                                AND exercicio.ano >= extract(YEAR FROM faltasInjustificadas.inicio)  " +
                "                                                AND exercicio.ano <= extract(YEAR FROM faltasInjustificadas.fim)), 0) +  " +
                "                                coalesce((SELECT sum((CASE WHEN (extract(YEAR FROM trunc(LEAST(coalesce(to_date(to_char(  " +
                "                                                                                                                    vinculo2.finalVigencia,  " +
                "                                                                                                                    'dd/MM/yyyy'),  " +
                "                                                                                                                'dd/MM/yyyy'),  " +
                "                                                                                                        current_date),  " +
                "                                                                                               afastamento.termino))) >  " +
                "                                                                 exercicio.ano)  " +
                "                                  THEN to_date('31/12/' || cast(exercicio.ano AS VARCHAR(4)), 'dd/MM/yyyy')  " +
                "                                                      ELSE trunc(LEAST(coalesce(to_date(to_char(vinculo2.finalVigencia,  " +
                "                                                                                                'dd/MM/yyyy'),  " +
                "                                                                                        'dd/MM/yyyy'), current_date),  " +
                "                                                                       afastamento.termino)) END -  " +
                "                                                      CASE WHEN (extract(YEAR FROM afastamento.inicio) < exercicio.ano)  " +
                "                                                        THEN to_date('01/01/' || cast(exercicio.ano AS VARCHAR(4)),  " +
                "                                                                     'dd/MM/yyyy')  " +
                "                                                      ELSE afastamento.inicio END) + 1)  " +
                "                                          FROM afastamento afastamento  " +
                "                                            INNER JOIN tipoafastamento  " +
                "                                              ON afastamento.tipoafastamento_id = tipoafastamento.id  " +
                "                                          WHERE tipoafastamento.descTempoServicoAposentadoria = 0 AND  " +
                "                                                coalesce(tipoafastamento.descontarTempoServicoEfetivo, 0) = 1  " +
                "                                                AND afastamento.contratofp_id = contrato.id  " +
                "                                                AND exercicio.ano >= extract(YEAR FROM afastamento.inicio)  " +
                "                                                AND exercicio.ano <= extract(YEAR FROM afastamento.termino)), 0))  " +
                "          )  " +
                "                    FROM contratofp contrato INNER JOIN vinculofp vinculo2 ON contrato.id = vinculo2.id  " +
                "                      INNER JOIN matriculafp matricula ON matricula.id = vinculo2.matriculafp_id  " +
                "                      INNER JOIN exercicio ON extract(YEAR FROM coalesce(vinculo2.alteracaoVinculo,vinculo2.inicioVigencia)) <= exercicio.ano  " +
                "                    WHERE exercicio.ano <= extract(YEAR FROM (CASE WHEN vinculo2.finalVigencia IS NOT NULL AND  " +
                "                                                                        vinculo2.finalVigencia < current_date  " +
                "                      THEN vinculo2.finalVigencia  " +
                "                                                              ELSE current_date END)) AND vinculo2.id = vinculo1.id), 0)  " +
                "          +  " +
                "          coalesce((SELECT sum(  " +
                "              CASE  " +
                "              WHEN (to_date(to_char(averbacao.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') IS NOT NULL AND  " +
                "                    extract(YEAR FROM to_date(to_char(averbacao.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy')) =  " +
                "                    extract(YEAR FROM to_date(to_char(averbacao.inicioVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy')))  " +
                "                THEN  " +
                "                  (to_date(to_char(averbacao.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') -  " +
                "                   to_date(to_char(averbacao.inicioVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') + 1)  " +
                "              WHEN (extract(YEAR FROM to_date(to_char(averbacao.inicioVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy')) =  " +
                "                    exercicio.ano)  " +
                "                THEN  " +
                "                  (to_date(  " +
                "                       CASE WHEN extract(YEAR FROM current_date) = exercicio.ano  " +
                "                         THEN  " +
                "                           to_char(current_date, 'dd/MM/yyyy')  " +
                "                       ELSE  " +
                "                         '31/12/' || cast(exercicio.ano AS VARCHAR(255))  " +
                "                       END, 'dd/MM/yyyy') -  " +
                "                   to_date(averbacao.inicioVigencia) + 1)  " +
                "              WHEN (  " +
                "                (to_date(to_char(averbacao.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') IS NOT NULL)  " +
                "                AND  " +
                "                (extract(YEAR FROM to_date(to_char(averbacao.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy')) = exercicio.ano)  " +
                "              )  " +
                "                THEN  " +
                "                  (to_date(to_char(averbacao.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') -  " +
                "                   trunc(to_date(to_char(averbacao.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy'), 'YYYY') + 1)  " +
                "              WHEN exercicio.ano = extract(YEAR FROM current_date)  " +
                "                THEN  " +
                "                  (to_date(current_date, 'dd/MM/yyyy') -  " +
                "                   trunc(to_date('01/01/' || cast(exercicio.ano AS VARCHAR(255)), 'dd/MM/yyyy'), 'YYYY') + 1)  " +
                "              ELSE  " +
                "                (to_date('31/12/' || cast(exercicio.ano AS VARCHAR(255)), 'dd/MM/yyyy') -  " +
                "                 trunc(to_date('01/01/' || cast(exercicio.ano AS VARCHAR(255)), 'dd/MM/yyyy'), 'YYYY') + 1)  " +
                "              END)  " +
                "                    FROM averbacaotemposervico averbacao  " +
                "                      JOIN contratofp contrato ON averbacao.contratofp_id = contrato.id  " +
                "                      INNER JOIN vinculofp vinculo ON vinculo.id = contrato.id  " +
                "                      INNER JOIN matriculafp matricula ON matricula.id = vinculo.matriculafp_id  " +
                "                      INNER JOIN exercicio ON extract(YEAR FROM averbacao.inicioVigencia) <= exercicio.ano  " +
                "                    WHERE averbacao.aposentado = 1 AND  " +
                "                          (exercicio.ano <= extract(YEAR FROM averbacao.finalvigencia) AND vinculo.id = vinculo1.id)),  " +
                "                   0)                                                                 AS TOTAL_GERAL  " +
                "        FROM matriculafp matricula2  " +
                "          INNER JOIN vinculofp vinculo1 ON matricula2.id = vinculo1.matriculafp_id  " +
                "          INNER JOIN contratofp contrato1 ON vinculo1.id = contrato1.id  " +
                "        WHERE contrato1.id = :contratofpid) query  " +
                "  GROUP BY query.TOTAL_GERAL, query.total_averbado, query.total_exercicio";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contratofpid", contratoFPID);
        q.setParameter("verdadeiro", Boolean.TRUE);

        if (!q.getResultList().isEmpty()) {
            return RelatorioCertidaoTempoServicoSubReport5.preencherDados(q.getResultList());
        }
        return Lists.newArrayList();
    }

    private List<RelatorioCertidaoTempoServicoSubReport6> buscarDadosSubReport6(Long contratoFPID) {
        String sql = " select afastamento.INICIO, afastamento.TERMINO, tipo.DESCRICAO, " +
            " afastamento.termino - afastamento.inicio + 1 qtdeDias " +
            " from afastamento  " +
            " inner join contratofp contrato on afastamento.contratofp_id = contrato.id " +
            " inner join tipoafastamento tipo on afastamento.TIPOAFASTAMENTO_ID = tipo.id " +
            " where contrato.id = :contratofpid order by afastamento.INICIO";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contratofpid", contratoFPID);
        if (!q.getResultList().isEmpty()) {
            return RelatorioCertidaoTempoServicoSubReport6.preencherDados(q.getResultList());
        }
        return Lists.newArrayList();
    }

    public List<AverbacaoTempoServico> buscarDataAverbacao(Long contratoID) {
        String sql = " select averbacao.* from AVERBACAOTEMPOSERVICO averbacao" +
            " where CONTRATOFP_ID = :idContrato";
        Query q = em.createNativeQuery(sql, AverbacaoTempoServico.class);
        q.setParameter("idContrato", contratoID);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }

    private List<RelatorioCertidaoTempoServicoSubReport4> buscarDadosSubReport4(Long contratoFPID) {
        String sql =
            " SELECT inicio, fim" +
                " FROM " +
                "  (SELECT averbacao.INICIOVIGENCIA as inicio," +
                "  averbacao.FINALVIGENCIA as fim" +
                "  FROM averbacaotemposervico averbacao" +
                "  JOIN contratofp contrato ON averbacao.contratofp_id = contrato.id" +
                "  INNER JOIN vinculofp vinculo ON vinculo.id = contrato.id" +
                "  INNER JOIN matriculafp matricula ON matricula.id = vinculo.matriculafp_id" +
                "  WHERE contrato.id = :contratofpid and averbacao.INICIOVIGENCIA is not null " +
                "  ) query  " +
                "  GROUP BY query.inicio, query.fim  " +
                "  ORDER BY query.inicio";

        Query q = em.createNativeQuery(sql);
        q.setParameter("contratofpid", contratoFPID);

        if (!q.getResultList().isEmpty()) {
            Map<Integer, Set<Date>> anoDias = new HashMap<>();
            anoDias = preencherMapaAnoDias(q.getResultList(), anoDias);
            BigDecimal totalTempoAverbado = calcularTotalTempoAverbado(anoDias);
            List<RelatorioCertidaoTempoServicoSubReport4> subReport4 = RelatorioCertidaoTempoServicoSubReport4.preencherDados(q.getResultList(), buscarDadosSubReport4SubReport1(totalTempoAverbado), anoDias);
            Collections.sort(subReport4);
            return subReport4;
        }
        return Lists.newArrayList();
    }

    private Map<Integer, Set<Date>> preencherMapaAnoDias(List<Object[]> objs, Map<Integer, Set<Date>> anoDias) {
        for (Object[] obj : objs) {
            anoDias = DataUtil.getTodosOsDiasEntreDatasNoMap((Date) obj[0], (Date) obj[1], anoDias);
        }
        return anoDias;
    }

    private BigDecimal calcularTotalTempoAverbado(Map<Integer, Set<Date>> anoDias) {
        BigDecimal totalTempoVerba = BigDecimal.ZERO;
        for (Map.Entry<Integer, Set<Date>> param : anoDias.entrySet()) {
            totalTempoVerba = totalTempoVerba.add(BigDecimal.valueOf(param.getValue().size()));
        }
        return totalTempoVerba;
    }

    private List<RelatorioCertidaoTempoServicoSubReport4SubReport1> buscarDadosSubReport4SubReport1(BigDecimal totalTempoVerba) {

        String sql =
            " SELECT " + totalTempoVerba + " AS total, REPLACE (frt_extenso_monetario(" + totalTempoVerba + "), 'REAIS') AS total_extenso " +
                " FROM dual";
        Query q = em.createNativeQuery(sql);
        if (!q.getResultList().isEmpty()) {
            return RelatorioCertidaoTempoServicoSubReport4SubReport1.preencherDados(q.getResultList());
        }
        return Lists.newArrayList();
    }

    private List<RelatorioCertidaoTempoServicoSubReport3> buscarDadosSubReport3(Long contratoFPID) {
        String sql =
            "    SELECT " +
                "    trunc(averbacao.INICIOVIGENCIA)                                                                            AS inicio, " +
                "    averbacao.orgaoempresa                                                                                     AS orgaoempresa, " +
                "    to_char(averbacao.INICIOVIGENCIA, 'dd/MM/yyyy') || ' a ' || to_char(averbacao.FINALVIGENCIA, 'dd/MM/yyyy') AS periodo, " +
                "    coalesce(averbacao.FINALVIGENCIA - averbacao.INICIOVIGENCIA + 1, 0)                                        AS total, " +
                "    trunc(averbacao.FINALVIGENCIA)                                                                             AS fim " +
                " FROM averbacaotemposervico averbacao " +
                " JOIN contratofp contrato ON contrato.id = averbacao.contratofp_id " +
                " JOIN vinculofp vinculo ON vinculo.id = contrato.id " +
                " JOIN matriculafp matricula ON matricula.id = vinculo.matriculafp_id " +
                " WHERE contrato.id = :contratofpid  " +
                " AND current_date BETWEEN coalesce(trunc(vinculo.alteracaoVinculo),trunc(vinculo.inicioVigencia)) AND COALESCE (trunc(vinculo.FINALVIGENCIA), current_date ) " +
                " ORDER BY averbacao.INICIOVIGENCIA";

        Query q = em.createNativeQuery(sql);
        q.setParameter("contratofpid", contratoFPID);
        if (!q.getResultList().isEmpty()) {
            return RelatorioCertidaoTempoServicoSubReport3.preencherDados(q.getResultList());
        }
        return Lists.newArrayList();
    }

    private List<RelatorioCertidaoTempoServicoSubReport2> buscarDadosSubReport2(Integer totalLiquidoTempoEfetivo) {
        String sql =
            " SELECT " +
                "    :totalLiquidoTempoEfetivo, " +
                "    replace(replace(frt_extenso_monetario(:totalLiquidoTempoEfetivo), 'REAL'), 'REAIS')           AS extenso, " +
                "    trunc((:totalLiquidoTempoEfetivo / 365.25))                                                   AS anos, " +
                "    trunc((:totalLiquidoTempoEfetivo / 365.25 - trunc((:totalLiquidoTempoEfetivo / 365.25))) * 12)               AS meses, " +
                "    round((((:totalLiquidoTempoEfetivo / 365.25 - trunc((:totalLiquidoTempoEfetivo / 365.25))) * 12) - " +
                "           trunc((:totalLiquidoTempoEfetivo / 365.25 - trunc((:totalLiquidoTempoEfetivo / 365.25))) * 12)) * 30) AS dias " +
                "  FROM dual";
        Query q = em.createNativeQuery(sql);
        q.setParameter("totalLiquidoTempoEfetivo", totalLiquidoTempoEfetivo);
        if (!q.getResultList().isEmpty()) {
            return RelatorioCertidaoTempoServicoSubReport2.preencherDados(q.getResultList());
        }
        return Lists.newArrayList();
    }


    private List<RelatorioCertidaoTempoServicoSubReport1> buscarDadosSubReport1(Long contratoFPID) {
        VinculoFP vinculoFP = vinculoFPFacade.recuperarSimples(contratoFPID);
        Date dataAtual = sistemaFacade.getDataOperacao();
        boolean usarDataAtual = false;
        if (vinculoFP.getFinalVigencia() != null && (vinculoFP.getFinalVigencia().compareTo(dataAtual)) >= 0) {
            usarDataAtual = true;
        }

        String sql =
            " SELECT " +
                "    exercicio.ano                                                          AS ano, " +
                "    sum(CASE " +
                "        WHEN (vinculo.finalVigencia IS NOT NULL AND " +
                "              extract(YEAR FROM vinculo.finalVigencia) = extract(YEAR FROM coalesce(vinculo.alteracaoVinculo,vinculo.inicioVigencia))) " +
                "          THEN " +
                "            (to_date(to_char(vinculo.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') - " +
                "             to_date(to_char(coalesce(vinculo.alteracaoVinculo,vinculo.inicioVigencia), 'dd/MM/yyyy'), 'dd/MM/yyyy') + 1) " +
                "        WHEN (extract(YEAR FROM coalesce(vinculo.alteracaoVinculo,vinculo.inicioVigencia)) = exercicio.ano) " +
                "          THEN " +
                "            (to_date( " +
                "                 CASE WHEN extract(YEAR FROM current_date) = exercicio.ano " +
                "                   THEN " +
                "                     to_char(current_date, 'dd/MM/yyyy') " +
                "                 ELSE " +
                "                   '31/12/' || cast(exercicio.ano AS VARCHAR(255)) " +
                "                 END, 'dd/MM/yyyy') - " +
                "             coalesce(vinculo.alteracaoVinculo,vinculo.inicioVigencia) + 1) " +
                "        WHEN ( " +
                "          (vinculo.finalVigencia IS NOT NULL) " +
                "          AND " +
                "          (extract(YEAR FROM vinculo.finalVigencia) = exercicio.ano) " +
                "        ) " +
                "          THEN " +
                "            (to_date(to_char(vinculo.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy') - " +
                "             trunc(to_date(to_char(vinculo.finalVigencia, 'dd/MM/yyyy'), 'dd/MM/yyyy'), 'YYYY') + 1) " +
                "        WHEN exercicio.ano = extract(YEAR FROM current_date) " +
                "          THEN " +
                "            (to_date(current_date) - " +
                "             trunc(to_date('01/01/' || cast(exercicio.ano AS VARCHAR(255)), 'dd/MM/yyyy'), 'YYYY') + 1) " +
                "        ELSE " +
                "          (to_date('31/12/' || cast(exercicio.ano AS VARCHAR(255)), 'dd/MM/yyyy') - " +
                "           trunc(to_date('01/01/' || cast(exercicio.ano AS VARCHAR(255)), 'dd/MM/yyyy'), 'YYYY') + 1) " +
                "        END)  -" +

                "        coalesce((SELECT SUM(( " +
                "    CASE " +
                "      WHEN ( extract(YEAR FROM TRUNC(LEAST(COALESCE(vinculo.finalVigencia, CURRENT_DATE), reintegracao.DATAREINTEGRACAO))) > exercicio.ano) " +
                "      THEN to_date ('31/12/' " +
                "        || CAST(exercicio.ano AS VARCHAR(4)), 'dd/MM/yyyy') " +
                "      ELSE TRUNC(LEAST(COALESCE(vinculo.finalVigencia, CURRENT_DATE), reintegracao.DATAREINTEGRACAO)) " +
                "    END - " +
                "    CASE " +
                "      WHEN (extract(YEAR FROM exoneracao.datarescisao) < exercicio.ano) " +
                "      THEN to_date('01/01/' " +
                "        || CAST(exercicio.ano AS VARCHAR(4)), 'dd/MM/yyyy') " +
                "      ELSE exoneracao.datarescisao " +
                "    END) - 1) " +
                "  FROM reintegracao " +
                "  inner join vinculofp vinculo on reintegracao.CONTRATOFP_ID = vinculo.id " +
                "  inner join EXONERACAORESCISAO exoneracao on exoneracao.VINCULOFP_ID = vinculo.id " +
                "  WHERE vinculo.id              = :contratofpid " +
                "  AND exercicio.ano            >= extract( YEAR FROM exoneracao.datarescisao) " +
                "  AND exercicio.ano            <= extract( YEAR FROM reintegracao.DATAREINTEGRACAO)" +
                "  ), 0) AS tempoBruto," +

                "    coalesce((SELECT sum (faltasjustificadas.totalfaltasjustificadas) " +
                "              FROM VWFALTA faltasJustificadas " +
                "                JOIN contratofp contrato ON faltasJustificadas.contratofp_id = contrato.id " +
                "                JOIN vinculofp vinculo ON vinculo.id = contrato.id " +
                "              WHERE contrato.id = :contratofpid                                                                    " +
                "    AND exercicio.ano >= extract( YEAR FROM faltasjustificadas.inicio) " +
                "    AND exercicio.ano <= extract( YEAR FROM faltasjustificadas.fim)), 0)   AS faltasJustificadas, " +
                "    " +
                "    " +
                "    coalesce((SELECT sum(faltasInjustificadas.totalfaltasefetivas) " +
                "              FROM VWFALTA faltasInjustificadas " +
                "                JOIN contratofp contrato ON faltasInjustificadas.contratofp_id = contrato.id " +
                "                JOIN vinculofp vinculo ON vinculo.id = contrato.id " +
                "              WHERE contrato.id = :contratofpid " +
                "    AND exercicio.ano >= extract( YEAR FROM faltasInjustificadas.inicio) " +
                "    AND exercicio.ano <= extract( YEAR FROM faltasInjustificadas.fim)), 0) AS faltasInjustificadas, " +
                "    " +
                "    " +
                "    coalesce((SELECT sum((CASE WHEN ( " +
                "      extract(YEAR FROM trunc(LEAST(coalesce(vinculo.finalVigencia, current_date), afastamento.termino))) > exercicio.ano) " +
                "      THEN to_date " +
                "      ('31/12/' || cast(exercicio.ano AS VARCHAR(4)), 'dd/MM/yyyy') " +
                "                          ELSE trunc(LEAST(coalesce(vinculo.finalVigencia, current_date), afastamento.termino)) END - " +
                "                          CASE WHEN (extract(YEAR FROM afastamento.inicio) < exercicio.ano) " +
                "                            THEN to_date('01/01/' || cast(exercicio.ano AS VARCHAR(4)), 'dd/MM/yyyy') " +
                "                          ELSE " +
                "                            afastamento.inicio END) + 1) " +
                "              FROM afastamento afastamento " +
                "                JOIN tipoafastamento ON afastamento.tipoafastamento_id = tipoafastamento.id " +
                "                JOIN contratofp contrato ON afastamento.contratofp_id = contrato.id " +
                "                JOIN vinculofp vinculo ON vinculo.id = contrato.id " +
                "              WHERE contrato.id = :contratofpid" +
                "                                   AND exercicio.ano >= extract( YEAR FROM afastamento.inicio) " +
                "    AND exercicio.ano <= extract( YEAR FROM afastamento.termino) and coalesce(tipoafastamento.processanormal, 0) = :verdadeiro), 0)     " +
                "    AS licencasComOnus, " +

                "    coalesce((SELECT sum((CASE WHEN ( " +
                "      extract(YEAR FROM trunc(LEAST(coalesce(vinculo.finalVigencia, current_date), afastamento.termino))) > exercicio.ano) " +
                "      THEN to_date " +
                "      ('31/12/' || cast(exercicio.ano AS VARCHAR(4)), 'dd/MM/yyyy') " +
                "                          ELSE trunc(LEAST(coalesce(vinculo.finalVigencia, current_date), afastamento.termino)) END - " +
                "                          CASE WHEN (extract(YEAR FROM afastamento.inicio) < exercicio.ano) " +
                "                            THEN to_date('01/01/' || cast(exercicio.ano AS VARCHAR(4)), 'dd/MM/yyyy') " +
                "                          ELSE " +
                "                            afastamento.inicio END) + 1) " +
                "              FROM afastamento afastamento " +
                "                JOIN tipoafastamento ON afastamento.tipoafastamento_id = tipoafastamento.id " +
                "                JOIN contratofp contrato ON afastamento.contratofp_id = contrato.id " +
                "                JOIN vinculofp vinculo ON vinculo.id = contrato.id " +
                "              WHERE contrato.id = :contratofpid" +
                "                                   AND exercicio.ano >= extract( YEAR FROM afastamento.inicio) " +
                "    AND exercicio.ano <= extract( YEAR FROM afastamento.termino) and coalesce(tipoafastamento.processanormal, 0) = :falso), 0)     " +
                "    AS licencasSemOnus, " +

                "    coalesce((SELECT sum((CASE WHEN ( " +
                "      extract(YEAR FROM trunc(LEAST(coalesce(vinculo.finalVigencia, current_date), afastamento.termino))) > exercicio.ano) " +
                "      THEN to_date " +
                "      ('31/12/' || cast(exercicio.ano AS VARCHAR(4)), 'dd/MM/yyyy') " +
                "                          ELSE trunc(LEAST(coalesce(vinculo.finalVigencia, current_date), afastamento.termino)) END - " +
                "                          CASE WHEN (extract(YEAR FROM afastamento.inicio) < exercicio.ano) " +
                "                            THEN to_date('01/01/' || cast(exercicio.ano AS VARCHAR(4)), 'dd/MM/yyyy') " +
                "                          ELSE " +
                "                            afastamento.inicio END) + 1) " +
                "              FROM afastamento afastamento " +
                "                JOIN tipoafastamento ON afastamento.tipoafastamento_id = tipoafastamento.id " +
                "                JOIN contratofp contrato ON afastamento.contratofp_id = contrato.id " +
                "                JOIN vinculofp vinculo ON vinculo.id = contrato.id " +
                "              WHERE contrato.id = :contratofpid" +
                "                                   AND tipoafastamento.descTempoServicoAposentadoria = 0 AND COALESCE (tipoafastamento.descontarTempoServicoEfetivo, 0) = 1 " +
                "                                   AND exercicio.ano >= extract( YEAR FROM afastamento.inicio) " +
                "    AND exercicio.ano <= extract( YEAR FROM afastamento.termino)), 0)      AS licencasQueDeduzemDias, " +
                "    '-'                                                                    AS suspensoes " +
                "  FROM contratofp contrato " +
                "    INNER JOIN vinculofp vinculo ON vinculo.id = contrato.id " +
                "    INNER JOIN matriculafp matricula ON vinculo.matriculafp_id = matricula.id " +
                "    INNER JOIN exercicio ON extract(YEAR FROM coalesce(vinculo.alteracaoVinculo,vinculo.inicioVigencia)) <= exercicio.ano " +
                "  WHERE " +
                "    exercicio.ano <= extract(YEAR FROM (CASE WHEN vinculo.finalVigencia IS NOT NULL AND vinculo.finalVigencia < current_date " +
                "      THEN vinculo.finalVigencia " +
                "                                        ELSE current_date END)) AND contrato.id = :contratofpid" +
                "  GROUP BY exercicio.ano ORDER BY exercicio.ano";


        if (usarDataAtual) {
            sql = sql.replaceAll("vinculo.finalVigencia", " :dataAtual ");
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("contratofpid", contratoFPID);
        q.setParameter("verdadeiro", Boolean.TRUE);
        q.setParameter("falso", Boolean.FALSE);
        if (usarDataAtual) {
            q.setParameter("dataAtual", dataAtual);
        }
        List resultList = q.getResultList();

        if (!resultList.isEmpty()) {
            return RelatorioCertidaoTempoServicoSubReport1.preencherDados(resultList);
        }
        return Lists.newArrayList();
    }

    private List<RelatorioCertidaoTempoServico> buscarDadosRelatorioPrincipal(Long contratoFPID,
                                                                              List<RelatorioCertidaoTempoServicoSubReport1> sub1,
                                                                              List<RelatorioCertidaoTempoServicoSubReport2> sub2,
                                                                              List<RelatorioCertidaoTempoServicoSubReport3> sub3,
                                                                              List<RelatorioCertidaoTempoServicoSubReport4> sub4,
                                                                              List<RelatorioCertidaoTempoServicoSubReport5> sub5,
                                                                              List<RelatorioCertidaoTempoServicoSubReport6> sub6,
                                                                              boolean detalhado) {
        String sql =
            " select pessoa.nome as nome_pessoa, " +
                "        cargo.descricao as cargo_descricao, " +
                "        unidade.descricao as unidade_descricao, " +
                "        matricula.matricula, " +
                "        vinculo.numero, " +
                "        (" +
                "          select max(progressao.descricao) from enquadramentofuncional ef inner join progressaoPCS progressao on ef.progressaoPCS_id = progressao.id " +
                "          where contrato.id = ef.contratoServidor_id and (ef.inicioVigencia  = (select max(eq.inicioVigencia) from EnquadramentoFuncional eq where eq.contratoServidor_id = vinculo.id)) " +
                "        ) as letra, " +
                "        to_char(coalesce(vinculo.alteracaoVinculo,vinculo.inicioVigencia),'dd/MM/yyyy') || ' a ' || " +
                "        to_char( (case when vinculo.finalVigencia is not null and vinculo.finalVigencia < current_date then vinculo.finalVigencia else current_date end),'dd/MM/yyyy') as periodo " +
                " from contratofp contrato " +
                " inner join vinculofp vinculo on vinculo.id = contrato.id " +
                " inner join matriculafp matricula on vinculo.matriculafp_id = matricula.id " +
                " inner join pessoafisica pessoa on matricula.pessoa_id = pessoa.id " +
                " inner join cargo cargo on contrato.cargo_id = cargo.id " +
                " inner join lotacaofuncional lotacao on contrato.id = lotacao.vinculofp_id and lotacao.inicioVigencia = (select max(lot.inicioVigencia) from LotacaoFUncional lot where lot.vinculofp_id = vinculo.id) " +
                " inner join unidadeorganizacional unidade on lotacao.unidadeorganizacional_id = unidade.id " +
                " where contrato.id = :contratofpid " +
                " order by periodo";

        Query q = em.createNativeQuery(sql);
        q.setParameter("contratofpid", contratoFPID);
        if (!q.getResultList().isEmpty()) {
            Boolean averbacaoDataContratoTemporario = false;
            ContratoFP contrato = em.find(ContratoFP.class, contratoFPID);
            String textoAverbacaoDataContratoTemporario = "";
            if (contrato.getAlteracaoVinculo() != null) {
                List<AverbacaoTempoServico> averbacoes = buscarDataAverbacao(contratoFPID);
                if (contrato.getAlteracaoVinculo() != null && (averbacoes != null && !averbacoes.isEmpty())) {
                    for (AverbacaoTempoServico averbacao : averbacoes) {
                        if (DataUtil.dataSemHorario(contrato.getDataAdmissao()).compareTo(DataUtil.dataSemHorario(averbacao.getInicioVigencia())) == 0) {
                            averbacaoDataContratoTemporario = true;
                            textoAverbacaoDataContratoTemporario = "A averbação correspondente a data " + DataUtil.getDataFormatada(averbacao.getInicioVigencia()) +
                                " é referente ao contrato temporário exercido na Prefeitura Municipal de Rio Branco.";
                            break;
                        }
                    }
                }
            }
            return RelatorioCertidaoTempoServico.preencherDados(q.getResultList(), sub1, sub2, sub3, sub4, sub5, sub6, detalhado, averbacaoDataContratoTemporario, textoAverbacaoDataContratoTemporario);
        }
        return Lists.newArrayList();
    }

    public MatriculaFPFacade getMatriculaFPFacade() {
        return matriculaFPFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }
}
