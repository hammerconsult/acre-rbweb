merge into itemdeclaracaoservico
    using (select ids.id,
                  (select max(pgcc.id)
                   from pgcc
                   where pgcc.cadastroeconomico_id = dms.prestador_id
                     and pgcc.conta = ipc.conta
                     and pgcc.iniciovigencia = case
                                                   when pc.ativo = 1
                                                       then to_date('01/01/' || extract(year from current_date), 'dd/MM/yyyy')
                                                   else to_date('01/01/' || extract(year from current_date) - 1, 'dd/MM/yyyy')
                       end
                  ) as conta_id
           from itemdeclaracaoservico ids
                    inner join declaracaoprestacaoservico dec
           on dec.id = ids.declaracaoprestacaoservico_id
               inner join notadeclarada nd on nd.declaracaoprestacaoservico_id = dec.id
               inner join declaracaomensalservico dms on dms.id = nd.declaracaomensalservico_id
               inner join itemplanocontasinterno ipc on ipc.id = ids.itemplanocontasinterno_id
               inner join planocontasinterno pc on pc.id = ipc.planocontasinterno_id) dados
    on (dados.id = itemdeclaracaoservico.id)
    when matched then
        update set conta_id = dados.conta_id
