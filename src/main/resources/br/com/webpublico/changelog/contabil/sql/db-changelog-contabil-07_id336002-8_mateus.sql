update itemprocesso set query37 = 'select (coalesce(s.inscricao, 0) + coalesce(s.atualizacao, 0) + coalesce(s.receita, 0) - coalesce(s.apropriacao, 0) - coalesce(s.pagamento, 0) - coalesce(s.cancelamento, 0)) as valor
from saldodividapublica s
  inner join dividapublica div on s.DIVIDAPUBLICA_ID = div.id
  inner join CATEGORIADIVIDAPUBLICA cat on div.CATEGORIADIVIDAPUBLICA_ID = cat.id
  inner join CONFIGCONCCONTABILNATDIVPB cfg on cat.NATUREZADIVIDAPUBLICA = cfg.NATUREZADIVIDAPUBLICA
  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON s.unidadeorganizacional_id = VW.SUBORDINADA_ID
  INNER JOIN
  (SELECT MAX(trunc(sld.data)) AS maiorData,
     sld.dividapublica_id,
     sld.unidadeorganizacional_id AS unidade,
     sld.fontederecursos_id as fonte,
     sld.CONTADEDESTINACAO_ID as destinacao,
     sld.intervalo
   FROM SALDODIVIDAPUBLICA sld
   WHERE trunc(sld.data) <= to_date(:dataFinal,''dd/mm/yyyy'')
   GROUP BY sld.dividapublica_id ,
     sld.unidadeorganizacional_id,
     sld.fontederecursos_id,
     sld.CONTADEDESTINACAO_ID,
     sld.intervalo
  ) reg
    ON trunc(s.data) = trunc(reg.maiordata)
       AND s.dividapublica_id = reg.dividapublica_id
       AND s.unidadeorganizacional_id = reg.unidade
       and s.fontederecursos_id = reg.fonte
       and s.CONTADEDESTINACAO_ID = reg.destinacao
       and s.intervalo = reg.intervalo
where trunc(s.DATA) between to_date(:dataInicial, ''dd/mm/yyyy'') and to_date(:dataFinal, ''dd/mm/yyyy'')
    and cfg.configConciliacaoContabil_id = :cfgId
    and to_date(:dataFinal, ''dd/mm/yyyy'') between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, to_date(:dataFinal, ''dd/mm/yyyy''))'
