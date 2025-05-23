merge into DESDOBRAMENTOOBRIGACAOPAGA o
using (
        select
          desd.id,
          cDest.id     as conta_ID
        from OBRIGACAOAPAGAR obr
          inner join DESDOBRAMENTOOBRIGACAOPAGA desd on obr.id = desd.OBRIGACAOAPAGAR_ID
          inner join conta c on desd.conta_ID = c.ID
          inner join CONTAEQUIVALENTE ce on c.id = ce.CONTAORIGEM_ID
          inner join conta cDest on ce.CONTADESTINO_ID = cdest.id
        where obr.TRANSPORTADO = 1
              and trunc(obr.DATALANCAMENTO) = to_date('01/01/2019', 'dd/mm/yyyy')
      ) ob on (ob.id = o.id)
when matched then update set o.conta_ID = ob.conta_ID
