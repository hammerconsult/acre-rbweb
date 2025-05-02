merge into CONTAAUXILIARDETALHADA cad using (
  select
    cd.id,
    destino.id as contadestinacao
  from conta c
    inner join contaauxiliardetalhada cd on c.id = cd.id
    inner join conta cdest on cd.CONTADEDESTINACAO_ID = cdest.id
    inner join contaequivalente ce on cdest.id = ce.CONTAORIGEM_ID
    inner join conta destino on ce.CONTADESTINO_ID = destino.id
  where c.EXERCICIO_ID = (select id
                          from exercicio
                          where ano = 2020)
        and cdest.EXERCICIO_ID = (select id
                                  from exercicio
                                  where ano = 2018)
) dados on (dados.id = cad.id)
when matched then update set cad.CONTADEDESTINACAO_ID = dados.contadestinacao
