update itemprocesso set query38 = 'select
  sum(case when pam.TIPOLANCAMENTO = ''NORMAL''
    then pam.VALORLANCAMENTO
      else pam.VALORLANCAMENTO * -1 end) as valor
from ProvAtuarialMatematica pam
  inner join CONFIGCONCCONTABILPAT cfg on cfg.TIPOPLANO = pam.TIPOPLANO
  inner join VWHIERARQUIAORCAMENTARIA vw on vw.SUBORDINADA_ID = pam.UNIDADEORGANIZACIONAL_ID
where cfg.configConciliacaoContabil_id = :cfgId
  and trunc(pam.DATALANCAMENTO) between to_date(:dataInicial, ''dd/MM/yyyy'') and to_date(:dataFinal, ''dd/MM/yyyy'')
  and to_date(:dataFinal, ''dd/mm/yyyy'') between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, to_date(:dataFinal, ''dd/mm/yyyy''))'
