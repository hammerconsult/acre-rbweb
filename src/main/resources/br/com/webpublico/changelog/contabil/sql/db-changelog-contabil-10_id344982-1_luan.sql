merge into DIVIDAPUBLICA divida using (
    SELECT div.id as idDivida,
           SUM(saldo.inscricao + saldo.atualizacao + saldo.receita + saldo.transferenciaCredito - saldo.pagamento - saldo.apropriacao - saldo.cancelamento - saldo.transferenciaDebito) as valor
    FROM saldodividapublica saldo
             inner join dividapublica div on div.id = saldo.DIVIDAPUBLICA_ID
    WHERE div.SALDO < 0
      and saldo.DATA = (select max(data)
                        from saldodividapublica s
                        where s.DIVIDAPUBLICA_ID = saldo.DIVIDAPUBLICA_ID
                          AND S.INTERVALO = SALDO.INTERVALO
                          AND S.UNIDADEORGANIZACIONAL_ID = SALDO.UNIDADEORGANIZACIONAL_ID
                          AND S.CONTADEDESTINACAO_ID = SALDO.CONTADEDESTINACAO_ID)
    group by div.id
) dados on (dados.idDivida = divida.id)
    when matched then update set divida.SALDO = dados.valor
