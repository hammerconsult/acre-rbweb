package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ExportacaoMatrizSaldoContabil;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.entidadesauxiliares.MatrizSaldoContabil;
import br.com.webpublico.enums.ClassificacaoContaAuxiliar;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoBalanceteExportacao;
import br.com.webpublico.enums.TipoMatrizSaldoContabil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class ExportacaoMatrizSaldoContabilFacade extends AbstractFacade<ExportacaoMatrizSaldoContabil> implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UnidadeGestoraFacade unidadeGestoraFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private ArquivoFacade arquivoFacade;

    public ExportacaoMatrizSaldoContabilFacade() {
        super(ExportacaoMatrizSaldoContabil.class);
    }

    @Override
    public ExportacaoMatrizSaldoContabil recuperar(Object id) {
        ExportacaoMatrizSaldoContabil entity = super.recuperar(id);
        Hibernate.initialize(entity.getArquivo().getPartes());
        return entity;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<MatrizSaldoContabil> buscarSaldos(Mes mes, UnidadeGestora unidadeGestora, TipoBalanceteExportacao tipoBalanceteExportacao, List<String> tiposIniciais, List<String> tiposFinais) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT codigo, ");
        sql.append("  COALESCE(SUM(saldoanterior), 0) AS saldoanterior, ");
        sql.append("  COALESCE(SUM(totalcredito), 0) AS totalcredito, ");
        sql.append("  COALESCE(SUM(totaldebito), 0) AS totaldebito, ");
        sql.append("  COALESCE(SUM(saldofinal), 0) AS saldofinal, ");
        sql.append("  tipocategoria, ");
        sql.append("  contaContabilSiconfi ");
        sql.append("FROM ");
        sql.append("  (SELECT id, ");
        sql.append("    codigo, ");
        sql.append("    superior_id, ");
        sql.append("    saldoanterior, ");
        sql.append("    totalcredito, ");
        sql.append("    totaldebito, ");
        sql.append("    (saldoanterior + totalcredito - totaldebito) AS saldofinal, ");
        sql.append("    tipocategoria, ");
        sql.append("    contaContabilSiconfi ");
        sql.append("  FROM ");
        sql.append("    (SELECT c.id, ");
        sql.append("      c.codigo, ");
        sql.append("      c.superior_id, ");
        sql.append("      SUM(s.totalcredito - s.totaldebito) AS saldoanterior, ");
        sql.append("      0 AS totalcredito, ");
        sql.append("      0 AS totaldebito, ");
        sql.append("      tca.codigo as tipocategoria, ");
        sql.append("      coalesce(cc.CODIGOSICONFI, replace(cc.CODIGO, '.', '')) as contaContabilSiconfi ");
        sql.append("    FROM saldocontacontabil s ");
        sql.append("    inner join contaauxiliar ca on s.CONTACONTABIL_ID = ca.id ");
        sql.append("    inner join conta c on ca.id = c.id ");
        sql.append("    inner join conta cc on ca.CONTACONTABIL_ID = cc.id ");
        sql.append("    inner join tipocontaauxiliar tca on ca.TIPOCONTAAUXILIAR_ID = tca.id ");
        sql.append(unidadeGestora != null ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on s.unidadeorganizacional_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "");
        sql.append("    INNER JOIN ");
        sql.append("      (SELECT trunc(MAX(datasaldo)) AS datasaldo, ");
        sql.append("        tipobalancete, ");
        sql.append("        unidadeorganizacional_id, ");
        sql.append("        contacontabil_id ");
        sql.append("      FROM saldocontacontabil ");
        sql.append(TipoBalanceteExportacao.ENCERRAMENTO.equals(tipoBalanceteExportacao) ?
            " WHERE trunc(dataSaldo) <= TO_DATE(:dataFinal, 'DD/MM/YYYY') " :
            Mes.JANEIRO.equals(mes) ? " WHERE trunc(dataSaldo) <= TO_DATE(:dataInicial, 'DD/MM/YYYY') " : " WHERE trunc(dataSaldo) < TO_DATE(:dataInicial, 'DD/MM/YYYY') ");
        sql.append("      GROUP BY tipobalancete, ");
        sql.append("        unidadeorganizacional_id, ");
        sql.append("        contacontabil_id ");
        sql.append("      ) ms ");
        sql.append("    ON trunc(s.dataSaldo) = trunc(ms.dataSaldo) ");
        sql.append("    AND s.contacontabil_id = ms.contacontabil_id ");
        sql.append("    AND s.unidadeorganizacional_id = ms.unidadeorganizacional_id ");
        sql.append("    AND s.tipobalancete = ms.tipobalancete ");
        sql.append("    WHERE s.tipobalancete IN :tipoBalanceteInicial ");
        sql.append("    AND c.exercicio_id = :exercicio ");
        sql.append("    AND tca.classificacaoContaAuxiliar = :siconfi ");
        sql.append(unidadeGestora != null ? " and ug.id = :unidadeGestora " : "");
        sql.append("    GROUP BY c.id, ");
        sql.append("      c.codigo, ");
        sql.append("      c.superior_id, ");
        sql.append("      tca.codigo, ");
        sql.append("      coalesce(cc.CODIGOSICONFI, replace(cc.CODIGO, '.', '')) ");
        sql.append("    UNION ALL ");
        sql.append("    SELECT id, ");
        sql.append("      codigo, ");
        sql.append("      superior_id, ");
        sql.append("      0 AS saldoanterior, ");
        sql.append("      SUM(creditofinal - creditoanterior) AS credito, ");
        sql.append("      SUM(debitofinal - debitoanterior) AS debito, ");
        sql.append("      tipocategoria, ");
        sql.append("      contaContabilSiconfi ");
        sql.append("    FROM ");
        sql.append("      (SELECT c.id, ");
        sql.append("        c.codigo, ");
        sql.append("        c.superior_id, ");
        sql.append("        SUM(s.totalcredito) AS creditoanterior, ");
        sql.append("        SUM(s.totaldebito)  AS debitoanterior, ");
        sql.append("        0 AS creditofinal, ");
        sql.append("        0 AS debitofinal, ");
        sql.append("        tca.codigo as tipocategoria, ");
        sql.append("        coalesce(cc.CODIGOSICONFI, replace(cc.CODIGO, '.', '')) as contaContabilSiconfi ");
        sql.append("      FROM saldocontacontabil s ");
        sql.append("      inner join contaauxiliar ca on s.CONTACONTABIL_ID = ca.id ");
        sql.append("      inner join conta c on ca.id = c.id ");
        sql.append("      inner join conta cc on ca.CONTACONTABIL_ID = cc.id ");
        sql.append("      inner join tipocontaauxiliar tca on ca.TIPOCONTAAUXILIAR_ID = tca.id ");
        sql.append(unidadeGestora != null ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on s.unidadeorganizacional_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "");
        sql.append("      INNER JOIN ");
        sql.append("        (SELECT trunc(MAX(datasaldo)) AS datasaldo, ");
        sql.append("          tipobalancete, ");
        sql.append("          unidadeorganizacional_id, ");
        sql.append("          contacontabil_id ");
        sql.append("        FROM saldocontacontabil ");
        sql.append("        WHERE trunc(dataSaldo) < TO_DATE(:dataInicial, 'DD/MM/YYYY') ");
        sql.append("        GROUP BY tipobalancete, ");
        sql.append("          unidadeorganizacional_id, ");
        sql.append("          contacontabil_id ");
        sql.append("        ) ms ");
        sql.append("      ON trunc(s.dataSaldo) = trunc(ms.dataSaldo) ");
        sql.append("      AND s.contacontabil_id = ms.contacontabil_id ");
        sql.append("      AND s.unidadeorganizacional_id = ms.unidadeorganizacional_id ");
        sql.append("      AND s.tipobalancete = ms.tipobalancete ");
        sql.append("      WHERE s.tipobalancete IN :tipoBalanceteFinal ");
        sql.append("      AND c.exercicio_id = :exercicio ");
        sql.append("      AND tca.classificacaoContaAuxiliar = :siconfi ");
        sql.append(unidadeGestora != null ? " and ug.id = :unidadeGestora " : "");
        sql.append("      GROUP BY c.id, ");
        sql.append("        c.codigo, ");
        sql.append("        c.superior_id, ");
        sql.append("        tca.codigo, ");
        sql.append("        coalesce(cc.CODIGOSICONFI, replace(cc.CODIGO, '.', '')) ");
        sql.append("      UNION ALL ");
        sql.append("      SELECT c.id, ");
        sql.append("        c.codigo, ");
        sql.append("        c.superior_id, ");
        sql.append("        0 AS creditoanterior, ");
        sql.append("        0 AS debitoanterior, ");
        sql.append("        SUM(s.totalcredito) AS creditofinal, ");
        sql.append("        SUM(s.totaldebito)  AS debitofinal, ");
        sql.append("        tca.codigo as tipocategoria, ");
        sql.append("        coalesce(cc.CODIGOSICONFI, replace(cc.CODIGO, '.', '')) as contaContabilSiconfi ");
        sql.append("      FROM saldocontacontabil s ");
        sql.append("      inner join contaauxiliar ca on s.CONTACONTABIL_ID = ca.id ");
        sql.append("      inner join conta c on ca.id = c.id ");
        sql.append("      inner join conta cc on ca.CONTACONTABIL_ID = cc.id ");
        sql.append("      inner join tipocontaauxiliar tca on ca.TIPOCONTAAUXILIAR_ID = tca.id ");
        sql.append(unidadeGestora != null ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on s.unidadeorganizacional_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "");
        sql.append("      INNER JOIN ");
        sql.append("        (SELECT trunc(MAX(datasaldo)) AS datasaldo, ");
        sql.append("          tipobalancete, ");
        sql.append("          unidadeorganizacional_id, ");
        sql.append("          contacontabil_id ");
        sql.append("        FROM saldocontacontabil ");
        sql.append("        WHERE trunc(dataSaldo) <= to_date(:dataFinal,'dd/MM/yyyy') ");
        sql.append("        GROUP BY tipobalancete, ");
        sql.append("          unidadeorganizacional_id, ");
        sql.append("          contacontabil_id ");
        sql.append("        ) ms ");
        sql.append("      ON trunc(s.dataSaldo) = trunc(ms.dataSaldo) ");
        sql.append("      AND s.contacontabil_id = ms.contacontabil_id ");
        sql.append("      AND s.unidadeorganizacional_id = ms.unidadeorganizacional_id ");
        sql.append("      AND s.tipobalancete = ms.tipobalancete ");
        sql.append("      WHERE s.tipobalancete IN :tipoBalanceteFinal ");
        sql.append("      AND c.exercicio_id = :exercicio ");
        sql.append("      AND tca.classificacaoContaAuxiliar = :siconfi ");
        sql.append(unidadeGestora != null ? " and ug.id = :unidadeGestora " : "");
        sql.append("      GROUP BY c.id, ");
        sql.append("        c.codigo, ");
        sql.append("        c.superior_id, ");
        sql.append("        tca.codigo, ");
        sql.append("        coalesce(cc.CODIGOSICONFI, replace(cc.CODIGO, '.', '')) ");
        sql.append("      ) ");
        sql.append("    GROUP BY id, ");
        sql.append("      codigo, ");
        sql.append("      superior_id, ");
        sql.append("      tipocategoria, ");
        sql.append("      contaContabilSiconfi ");
        sql.append("    ) ");
        sql.append("  ) ");
        sql.append("WHERE (saldoanterior <> 0 ");
        sql.append("OR totalcredito <> 0 ");
        sql.append("OR totaldebito <> 0 ");
        sql.append("OR saldofinal <> 0) ");
        sql.append("GROUP BY codigo, ");
        sql.append("         tipocategoria, ");
        sql.append("         contaContabilSiconfi ");
        sql.append("ORDER BY regexp_substr(codigo, '[^.]+', 2, 2), ");
        sql.append("  contaContabilSiconfi, ");
        sql.append("  regexp_substr(codigo, '[^.]+', 3, 2), ");
        sql.append("  regexp_substr(codigo, '[^.]+', 4, 3),  ");
        sql.append("  regexp_substr(codigo, '[^.]+', 5, 4), ");
        sql.append("  regexp_substr(codigo, '[^.]+', 6, 5) ");

        Query q = em.createNativeQuery(sql.toString());
        Exercicio exercicioAtual = sistemaFacade.getExercicioCorrente();
        q.setParameter("dataInicial", "01/" + mes.getNumeroMesString() + "/" + sistemaFacade.getExercicioCorrente().getAno());
        q.setParameter("dataFinal", Util.getDiasMes(mes.getNumeroMes(), sistemaFacade.getExercicioCorrente().getAno()) + "/" + mes.getNumeroMesString() + "/" + sistemaFacade.getExercicioCorrente().getAno());
        q.setParameter("exercicio", exercicioAtual.getId());
        q.setParameter("siconfi", ClassificacaoContaAuxiliar.SICONFI.name());
        q.setParameter("tipoBalanceteInicial", tiposIniciais);
        q.setParameter("tipoBalanceteFinal", tiposFinais);
        if (unidadeGestora != null) {
            q.setParameter("unidadeGestora", unidadeGestora.getId());
        }
        List<Object[]> resultado = q.getResultList();
        List<MatrizSaldoContabil> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                String[] matriz = ((String) obj[0]).split("\\.");
                MatrizSaldoContabil matrizSaldoContabil;
                if (obj[1] != null && ((BigDecimal) obj[1]).compareTo(BigDecimal.ZERO) != 0) {
                    //SaldoAnterior
                    retorno.add(gerarMatrizSaldoContabil(matriz, (BigDecimal) obj[1], TipoMatrizSaldoContabil.BEGINNING_BALANCE, ((BigDecimal) obj[1]).compareTo(BigDecimal.ZERO) >= 0 ? "C" : "D", (String) obj[5], (String) obj[6], exercicioAtual));
                }
                if (obj[3] != null && ((BigDecimal) obj[3]).compareTo(BigDecimal.ZERO) != 0) {
                    //Movimento de Débito
                    retorno.add(gerarMatrizSaldoContabil(matriz, (BigDecimal) obj[3], TipoMatrizSaldoContabil.PERIOD_CHANGE, "D", (String) obj[5], (String) obj[6], exercicioAtual));
                }
                if (obj[2] != null && ((BigDecimal) obj[2]).compareTo(BigDecimal.ZERO) != 0) {
                    //Movimento de Crédito
                    retorno.add(gerarMatrizSaldoContabil(matriz, (BigDecimal) obj[2], TipoMatrizSaldoContabil.PERIOD_CHANGE, "C", (String) obj[5], (String) obj[6], exercicioAtual));
                }
                if (obj[4] != null && ((BigDecimal) obj[4]).compareTo(BigDecimal.ZERO) != 0) {
                    //Saldo Atual
                    retorno.add(gerarMatrizSaldoContabil(matriz, (BigDecimal) obj[4], TipoMatrizSaldoContabil.ENDING_BALANCE, (((BigDecimal) obj[4]).compareTo(BigDecimal.ZERO) >= 0 ? "C" : "D"), (String) obj[5], (String) obj[6], exercicioAtual));
                }
            }
        }
        return retorno;
    }


    //Montar os tipos pelo Tipo de Conta Auxiliar.
    private MatrizSaldoContabil gerarMatrizSaldoContabil(String[] matriz,
                                                         BigDecimal valor,
                                                         TipoMatrizSaldoContabil tipoMatrizSaldoContabil,
                                                         String naturezaValor,
                                                         String codigoTipoContaAuxiliar,
                                                         String codigoContaContabilSiconfi,
                                                         Exercicio exercicioAtual) {
        MatrizSaldoContabil matrizSaldoContabil = new MatrizSaldoContabil();
        matrizSaldoContabil.setContaContabilSiconfi(codigoContaContabilSiconfi);
        matrizSaldoContabil.setIc1(Util.zerosAEsquerda(matriz[1], 5));
        matrizSaldoContabil.setTipo1("PO");
        switch (codigoTipoContaAuxiliar) {
            //91 - PO
            case "91":
                matrizTipo2Vazio(matrizSaldoContabil);
                matrizTipo3Vazio(matrizSaldoContabil);
                matrizTipo4Vazio(matrizSaldoContabil);
                matrizTipo5Vazio(matrizSaldoContabil);
                matrizTipo6Vazio(matrizSaldoContabil);
                break;
            //92 - PO - FP
            case "92":
                matrizSaldoContabil.setIc2(matriz[2]);
                matrizSaldoContabil.setTipo2("FP");
                matrizTipo3Vazio(matrizSaldoContabil);
                matrizTipo4Vazio(matrizSaldoContabil);
                matrizTipo5Vazio(matrizSaldoContabil);
                matrizTipo6Vazio(matrizSaldoContabil);
                break;
            //93 - PO - FP - DC
            case "93":
                matrizSaldoContabil.setIc2(matriz[2]);
                matrizSaldoContabil.setTipo2("FP");
                matrizSaldoContabil.setIc3(matriz[3]);
                matrizSaldoContabil.setTipo3("DC");
                matrizTipo4Vazio(matrizSaldoContabil);
                matrizTipo5Vazio(matrizSaldoContabil);
                matrizTipo6Vazio(matrizSaldoContabil);
                break;
            //até 2021 94 - PO - FP - FR
            //após 2021 94 - PO - FP - FR - CO
            case "94":
                if (exercicioAtual.getAno() <= 2021) {
                    matrizSaldoContabil.setIc2(matriz[2]);
                    matrizSaldoContabil.setTipo2("FP");
                    matrizSaldoContabil.setIc3(matriz[3]);
                    matrizSaldoContabil.setTipo3("FR");
                    matrizTipo4Vazio(matrizSaldoContabil);
                } else {
                    matrizSaldoContabil.setIc2(matriz[2]);
                    matrizSaldoContabil.setTipo2("FP");
                    matrizSaldoContabil.setIc3(matriz[3]);
                    matrizSaldoContabil.setTipo3("FR");
                    if (matriz.length == 5) {
                        matrizSaldoContabil.setIc4(matriz[4]);
                        matrizSaldoContabil.setTipo4("CO");
                    } else {
                        matrizTipo4Vazio(matrizSaldoContabil);
                    }
                }
                matrizTipo5Vazio(matrizSaldoContabil);
                matrizTipo6Vazio(matrizSaldoContabil);
                break;
            //até 2021 95 - PO - FR
            //após 2021 95 - PO - FR - CO
            case "95":
                if (exercicioAtual.getAno() <= 2021) {
                    matrizSaldoContabil.setIc2(matriz[2]);
                    matrizSaldoContabil.setTipo2("FR");
                    matrizTipo3Vazio(matrizSaldoContabil);
                } else {
                    matrizSaldoContabil.setIc2(matriz[2]);
                    matrizSaldoContabil.setTipo2("FR");
                    if (matriz.length == 4) {
                        matrizSaldoContabil.setIc3(matriz[3]);
                        matrizSaldoContabil.setTipo3("CO");
                    } else {
                        matrizTipo3Vazio(matrizSaldoContabil);
                    }
                }
                matrizTipo4Vazio(matrizSaldoContabil);
                matrizTipo5Vazio(matrizSaldoContabil);
                matrizTipo6Vazio(matrizSaldoContabil);
                break;
            //até 2021 96 - PO - FR - NR
            //após 2021 96 - PO - FR - CO - NR
            case "96":
                if (exercicioAtual.getAno() <= 2021) {
                    matrizSaldoContabil.setIc2(matriz[2]);
                    matrizSaldoContabil.setTipo2("FR");
                    matrizSaldoContabil.setIc3(matriz[3]);
                    matrizSaldoContabil.setTipo3("NR");
                    matrizTipo4Vazio(matrizSaldoContabil);
                } else {
                    matrizSaldoContabil.setIc2(matriz[2]);
                    matrizSaldoContabil.setTipo2("FR");
                    if (matriz.length == 5) {
                        matrizSaldoContabil.setIc3(matriz[3]);
                        matrizSaldoContabil.setTipo3("CO");
                        matrizSaldoContabil.setIc4(matriz[4]);
                        matrizSaldoContabil.setTipo4("NR");
                    } else {
                        matrizSaldoContabil.setIc3(matriz[3]);
                        matrizSaldoContabil.setTipo3("NR");
                        matrizTipo4Vazio(matrizSaldoContabil);
                    }
                }
                matrizTipo5Vazio(matrizSaldoContabil);
                matrizTipo6Vazio(matrizSaldoContabil);
                break;
            //até 2021 97 - PO - FS - FR - ND - ES
            //após 2021 97 - PO - FS - FR - CO - ND
            case "97":
                if (exercicioAtual.getAno() <= 2021) {
                    matrizSaldoContabil.setIc2(matriz[2]);
                    matrizSaldoContabil.setTipo2("FS");
                    matrizSaldoContabil.setIc3(matriz[3]);
                    matrizSaldoContabil.setTipo3("FR");
                    matrizSaldoContabil.setIc4(matriz[4]);
                    matrizSaldoContabil.setTipo4("ND");
                    matrizSaldoContabil.setIc5("0".equals(matriz[5]) ? "" : matriz[5]);
                    matrizSaldoContabil.setTipo5("0".equals(matriz[5]) ? "" : "ES");
                } else {
                    matrizSaldoContabil.setIc2(matriz[2]);
                    matrizSaldoContabil.setTipo2("FS");
                    matrizSaldoContabil.setIc3(matriz[3]);
                    matrizSaldoContabil.setTipo3("FR");
                    if (matriz.length == 6) {
                        matrizSaldoContabil.setIc4(matriz[4]);
                        matrizSaldoContabil.setTipo4("CO");
                        matrizSaldoContabil.setIc5(matriz[5]);
                        matrizSaldoContabil.setTipo5("ND");
                    } else {
                        matrizSaldoContabil.setIc4(matriz[4]);
                        matrizSaldoContabil.setTipo4("ND");
                        matrizTipo5Vazio(matrizSaldoContabil);
                    }
                }
                matrizTipo6Vazio(matrizSaldoContabil);
                break;
            //98 - PO - FP - DC - FR
            case "98":
                matrizSaldoContabil.setIc2(matriz[2]);
                matrizSaldoContabil.setTipo2("FP");
                matrizSaldoContabil.setIc3(matriz[3]);
                matrizSaldoContabil.setTipo3("DC");
                matrizSaldoContabil.setIc4(matriz[4]);
                matrizSaldoContabil.setTipo4("FR");
                matrizTipo5Vazio(matrizSaldoContabil);
                matrizTipo6Vazio(matrizSaldoContabil);
                break;
            //até 2021 99 - PO - FS - FR - ND - ES - AI
            //após 2021 99 - PO - FS - FR - CO - ND - AI
            case "99":
                if (exercicioAtual.getAno() <= 2021) {
                    matrizSaldoContabil.setIc2(matriz[2]);
                    matrizSaldoContabil.setTipo2("FS");
                    matrizSaldoContabil.setIc3(matriz[3]);
                    matrizSaldoContabil.setTipo3("FR");
                    matrizSaldoContabil.setIc4(matriz[4]);
                    matrizSaldoContabil.setTipo4("ND");
                    matrizSaldoContabil.setIc5("0".equals(matriz[5]) ? "" : matriz[5]);
                    matrizSaldoContabil.setTipo5("0".equals(matriz[5]) ? "" : "ES");
                    matrizSaldoContabil.setIc6(matriz[6]);
                    matrizSaldoContabil.setTipo6("AI");
                } else {
                    matrizSaldoContabil.setIc2(matriz[2]);
                    matrizSaldoContabil.setTipo2("FS");
                    matrizSaldoContabil.setIc3(matriz[3]);
                    matrizSaldoContabil.setTipo3("FR");
                    if (matriz.length == 7) {
                        matrizSaldoContabil.setIc4(matriz[4]);
                        matrizSaldoContabil.setTipo4("CO");
                        matrizSaldoContabil.setIc5(matriz[5]);
                        matrizSaldoContabil.setTipo5("ND");
                        matrizSaldoContabil.setIc6(matriz[6]);
                        matrizSaldoContabil.setTipo6("AI");
                    } else {
                        matrizSaldoContabil.setIc4(matriz[4]);
                        matrizSaldoContabil.setTipo4("ND");
                        matrizSaldoContabil.setIc5(matriz[5]);
                        matrizSaldoContabil.setTipo5("AI");
                        matrizTipo6Vazio(matrizSaldoContabil);
                    }
                }
                break;
        }
        if (exercicioAtual.getAno() <= 2021) {
            matrizTipo7Vazio(matrizSaldoContabil);
        }
        matrizSaldoContabil.setValor(valor);
        matrizSaldoContabil.setTipoValor(tipoMatrizSaldoContabil);
        matrizSaldoContabil.setNaturezaValor(naturezaValor);
        return matrizSaldoContabil;
    }

    private void matrizTipo2Vazio(MatrizSaldoContabil matrizSaldoContabil) {
        matrizSaldoContabil.setIc2("");
        matrizSaldoContabil.setTipo2("");
    }

    private void matrizTipo3Vazio(MatrizSaldoContabil matrizSaldoContabil) {
        matrizSaldoContabil.setIc3("");
        matrizSaldoContabil.setTipo3("");
    }

    private void matrizTipo4Vazio(MatrizSaldoContabil matrizSaldoContabil) {
        matrizSaldoContabil.setIc4("");
        matrizSaldoContabil.setTipo4("");
    }

    private void matrizTipo5Vazio(MatrizSaldoContabil matrizSaldoContabil) {
        matrizSaldoContabil.setIc5("");
        matrizSaldoContabil.setTipo5("");
    }

    private void matrizTipo6Vazio(MatrizSaldoContabil matrizSaldoContabil) {
        matrizSaldoContabil.setIc6("");
        matrizSaldoContabil.setTipo6("");
    }

    private void matrizTipo7Vazio(MatrizSaldoContabil matrizSaldoContabil) {
        matrizSaldoContabil.setIc7("");
        matrizSaldoContabil.setTipo7("");
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public UnidadeGestoraFacade getUnidadeGestoraFacade() {
        return unidadeGestoraFacade;
    }

    public ConfiguracaoContabilFacade getConfiguracaoContabilFacade() {
        return configuracaoContabilFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }
}
