update itemprocesso set query42 = 'select coalesce(sum(valor), 0)
from (
       select dLiq.VALOR, liq.UNIDADEORGANIZACIONAL_ID
       from LIQUIDACAO liq
         inner join Desdobramento dLiq on liq.ID = dLiq.LIQUIDACAO_ID
         inner join CONFIGCONCCONTABILGRUPOM cfgGm on cfgGm.CATEGORIAORCAMENTARIA = liq.CATEGORIAORCAMENTARIA
         inner join ConfigGrupoMaterial cgm on cgm.GRUPOMATERIAL_ID = cfgGm.GRUPOMATERIAL_ID and cgm.CONTADESPESA_ID = dLiq.CONTA_ID
         inner join CONFIGCONCCONTABILCONTA cfgConta on cfgConta.CONTA_ID = dLiq.CONTA_ID
       where trunc(liq.DATALIQUIDACAO) between to_date(:dataInicial, ''dd/MM/yyyy'') and to_date(:dataFinal, ''dd/MM/yyyy'')
             and trunc(liq.DATALIQUIDACAO) between trunc(cgm.INICIOVIGENCIA) and trunc(cgm.FIMVIGENCIA)
             and cfgGm.configConciliacaoContabil_id = :cfgId
             and cfgConta.configConciliacaoContabil_id = :cfgId
  union all
       select coalesce(estDLiq.valor, 0) * - 1 as valor, liq.UNIDADEORGANIZACIONAL_ID
       from liquidacaoestorno est
         inner join LIQUIDACAO liq on liq.ID = est.LIQUIDACAO_ID
         inner join DesdobramentoLiqEstorno estDLiq on est.ID = estDLiq.LIQUIDACAOESTORNO_ID
         inner join CONFIGCONCCONTABILGRUPOM cfgGm on cfgGm.CATEGORIAORCAMENTARIA = liq.CATEGORIAORCAMENTARIA
         inner join ConfigGrupoMaterial cgm on cgm.GRUPOMATERIAL_ID = cfgGm.GRUPOMATERIAL_ID and cgm.CONTADESPESA_ID = estDLiq.CONTA_ID
         inner join CONFIGCONCCONTABILCONTA cfgConta on cfgConta.CONTA_ID = estDLiq.CONTA_ID
       where trunc(est.dataestorno) between to_date(:dataInicial, ''dd/MM/yyyy'') and to_date(:dataFinal, ''dd/MM/yyyy'')
             and trunc(est.dataestorno) between trunc(cgm.INICIOVIGENCIA) and trunc(cgm.FIMVIGENCIA)
             and cfgGm.configConciliacaoContabil_id = :cfgId
             and cfgConta.configConciliacaoContabil_id = :cfgId
     ) dados
  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON dados.unidadeorganizacional_id = VW.SUBORDINADA_ID
where to_date(:dataFinal, ''dd/mm/yyyy'') between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, to_date(:dataFinal, ''dd/mm/yyyy''))'
