update itemprocesso set query39 = 'select sum(valor) from (
  select
    lro.valor,
    lro.UNIDADEORGANIZACIONAL_ID
  from LancamentoReceitaOrc lro
    inner join receitaloa rec on lro.RECEITALOA_ID = rec.ID
    inner join CONFIGCONCCONTABILCONTA cfg on rec.CONTADERECEITA_ID = cfg.CONTA_ID
  where trunc(lro.DATALANCAMENTO) between to_date(:dataInicial, ''dd/mm/yyyy'') and to_date(:dataFinal, ''dd/mm/yyyy'')
        and cfg.configConciliacaoContabil_id = :cfgId
  union all
  select
    coalesce(est.valor, 0) * -1 as valor,
    est.UNIDADEORGANIZACIONALORC as UNIDADEORGANIZACIONAL_ID
  from RECEITAORCESTORNO est
    inner join receitaloa rec on est.RECEITALOA_ID = rec.ID
    inner join CONFIGCONCCONTABILCONTA cfg on rec.CONTADERECEITA_ID = cfg.CONTA_ID
  where trunc(est.dataestorno) between to_date(:dataInicial, ''dd/mm/yyyy'') and to_date(:dataFinal, ''dd/mm/yyyy'')
        and cfg.configConciliacaoContabil_id = :cfgId
) dados
INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON dados.unidadeorganizacional_id = VW.SUBORDINADA_ID
where to_date(:dataFinal, ''dd/mm/yyyy'') between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, to_date(:dataFinal, ''dd/mm/yyyy''))'
