update itemprocesso set query43 = 'select coalesce(sum(valor), 0) from (
  select liq.valor, liq.UNIDADEORGANIZACIONAL_ID
  from liquidacao liq
    inner join empenho emp on emp.id = liq.EMPENHO_ID
    inner join dividaPublica dp on dp.id = emp.DIVIDAPUBLICA_ID
    inner join CONFIGCONCCONTABILCATDIVPB cfg on dp.CATEGORIADIVIDAPUBLICA_ID = cfg.CATEGORIADIVIDAPUBLICA_ID
    inner join CONFIGCONCCONTABILCONTA cfgConta on cfgConta.CONTA_ID = emp.CONTADESPESA_ID
  where trunc(liq.DATALIQUIDACAO) between to_date(:dataInicial, ''dd/MM/yyyy'') and to_date(:dataFinal, ''dd/MM/yyyy'')
        and cfg.configConciliacaoContabil_id = :cfgId
        and cfgConta.configConciliacaoContabil_id = :cfgId
        and liq.CATEGORIAORCAMENTARIA = ''NORMAL''
  union all
  select coalesce(est.valor, 0) * - 1 as valor, liq.UNIDADEORGANIZACIONAL_ID
  from liquidacaoestorno est
    inner join liquidacao liq on est.LIQUIDACAO_ID = liq.id
    inner join empenho emp on emp.id = liq.EMPENHO_ID
    inner join dividaPublica dp on dp.id = emp.DIVIDAPUBLICA_ID
    inner join CONFIGCONCCONTABILCATDIVPB cfg on dp.CATEGORIADIVIDAPUBLICA_ID = cfg.CATEGORIADIVIDAPUBLICA_ID
    inner join CONFIGCONCCONTABILCONTA cfgConta on cfgConta.CONTA_ID = emp.CONTADESPESA_ID
  where trunc(est.dataestorno) between to_date(:dataInicial, ''dd/MM/yyyy'') and to_date(:dataFinal, ''dd/MM/yyyy'')
        and cfg.configConciliacaoContabil_id = :cfgId
        and cfgConta.configConciliacaoContabil_id = :cfgId
        and liq.CATEGORIAORCAMENTARIA = ''NORMAL''
) dados
  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON dados.unidadeorganizacional_id = VW.SUBORDINADA_ID
where to_date(:dataFinal, ''dd/mm/yyyy'') between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, to_date(:dataFinal, ''dd/mm/yyyy''))'
