update itemprocesso set query40 = 'select coalesce(sum(valor), 0) from (
select liq.valor, liq.UNIDADEORGANIZACIONAL_ID
from liquidacao liq
  inner join CONFIGCONCCONTABILCATEGORC cfg on liq.CATEGORIAORCAMENTARIA = cfg.categoriaorcamentaria
where trunc(liq.DATALIQUIDACAO) between to_date(:dataInicial, ''dd/MM/yyyy'') and to_date(:dataFinal, ''dd/MM/yyyy'')
and cfg.configConciliacaoContabil_id = :cfgId
union all
select coalesce(est.valor, 0) * - 1 as valor, liq.UNIDADEORGANIZACIONAL_ID
from liquidacaoestorno est
inner join liquidacao liq on est.LIQUIDACAO_ID = liq.id
inner join CONFIGCONCCONTABILCATEGORC cfg on liq.CATEGORIAORCAMENTARIA = cfg.categoriaorcamentaria
where trunc(est.dataestorno) between to_date(:dataInicial, ''dd/MM/yyyy'') and to_date(:dataFinal, ''dd/MM/yyyy'')
and cfg.configConciliacaoContabil_id = :cfgId
) dados
INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON dados.unidadeorganizacional_id = VW.SUBORDINADA_ID
where to_date(:dataFinal, ''dd/mm/yyyy'') between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, to_date(:dataFinal, ''dd/mm/yyyy''))'
