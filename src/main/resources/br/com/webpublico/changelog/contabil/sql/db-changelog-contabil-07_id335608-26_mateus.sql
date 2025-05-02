update itemprocesso set query31 = 'select coalesce(sum(ssb.totaldebito - ssb.totalcredito), 0) as saldoAtual
FROM SALDOSUBCONTA SSB
inner join subconta sub on ssb.subconta_id = sub.id
inner join vwhierarquiaorcamentaria vw on ssb.unidadeorganizacional_id = vw.subordinada_id
inner join (select trunc(max(datasaldo)) as datasaldo, subconta_id, fontederecursos_id, unidadeorganizacional_id
             from saldosubconta where trunc(datasaldo) <= TO_DATE(:dataFinal, ''dd/MM/yyyy'')
            group by subconta_id, fontederecursos_id, unidadeorganizacional_id
    ) dataSaldo on dataSaldo.subconta_id = ssb.subconta_id
      and trunc(ssb.datasaldo) = trunc(dataSaldo.datasaldo)
      and ssb.fontederecursos_id = dataSaldo.fontedeRecursos_id
      and ssb.unidadeorganizacional_id = datasaldo.unidadeorganizacional_id
inner join CONFIGCONCCONTABILSUBCONTA cfg on ssb.subconta_id = cfg.subconta_id
where cfg.configConciliacaoContabil_id = :cfgId
and  trunc(ssb.datasaldo) between to_date(:dataInicial, ''dd/MM/yyyy'') and to_date(:dataFinal, ''dd/MM/yyyy'')
and to_date(:dataFinal, ''dd/MM/yyyy'') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:dataFinal, ''dd/MM/yyyy''))'
