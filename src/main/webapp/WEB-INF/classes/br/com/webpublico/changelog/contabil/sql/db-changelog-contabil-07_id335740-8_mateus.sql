update itemprocesso set query32 = 'SELECT sum((DEDUCAORECONHECIMENTO + PROVISAOPERDACREDITO + BAIXARECONHECIMENTO + RECEBIMENTO + DIMINUTIVO) - (s.RECONHECIMENTOCREDITO + s.REVERSAOPROVISAOPERDACREDITO + BAIXADEDUCAO + AUMENTATIVO + atualizacao)) as saldoAtual
 FROM SALDOCREDITORECEBER S
 inner join conta c on S.CONTARECEITA_ID = c.id
 inner join CONTARECEITA cr on c.id = cr.id
 inner join CONFIGCONCCONTABILTPCONREC cfg on cr.TIPOSCREDITO = cfg.TIPOSCREDITO
 INNER JOIN (SELECT trunc(MAX(saldo.datasaldo)) AS DATASALDO, SALDO.CONTARECEITA_ID, SALDO.UNIDADEORGANIZACIONAL_ID,
                saldo.naturezaCreditoReceber,
                saldo.intervalo,
                saldo.contaDeDestinacao_id
           FROM SALDOCREDITORECEBER SALDO
          where trunc(saldo.datasaldo) <= to_date(:dataFinal, ''dd/MM/yyyy'')
           GROUP BY SALDO.CONTARECEITA_ID, SALDO.UNIDADEORGANIZACIONAL_ID, saldo.naturezaCreditoReceber,saldo.intervalo, saldo.contaDeDestinacao_id) MAIORDATA
      ON S.contareceita_id = MAIORDATA.contareceita_id
      AND S.UNIDADEORGANIZACIONAL_ID = MAIORDATA.UNIDADEORGANIZACIONAL_ID
      and trunc(maiordata.datasaldo) = trunc(s.datasaldo)
      and s.naturezaCreditoReceber = maiordata.naturezaCreditoReceber
      and s.intervalo = maiordata.intervalo
      and s.contaDeDestinacao_id  = maiordata.contaDeDestinacao_id
 INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON s.unidadeorganizacional_id = VW.SUBORDINADA_ID
 where cfg.configConciliacaoContabil_id = :cfgId
   and trunc(s.datasaldo) between to_date(:dataInicial, ''dd/MM/yyyy'') and to_date(:dataFinal, ''dd/MM/yyyy'')
   AND TO_DATE(:dataFinal,''dd/mm/yyyy'') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA,TO_DATE(:dataFinal,''dd/mm/yyyy''))'
