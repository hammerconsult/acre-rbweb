merge into itemdeclaracaoservico i
    using (select ids.id,
                  (select max(id)
                   from pgcc
                   where pgcc.cadastroeconomico_id = dms.prestador_id
                     and pgcc.conta = ipc.conta) as conta_id
           from itemdeclaracaoservico ids
                    inner join declaracaoprestacaoservico dec
           on dec.id = ids.declaracaoprestacaoservico_id
               inner join notadeclarada nd on nd.declaracaoprestacaoservico_id = dec.id
               inner join declaracaomensalservico dms on dms.id = nd.declaracaomensalservico_id
               inner join itemplanocontasinterno ipc on ipc.id = ids.itemplanocontasinterno_id) dados
    on (dados.id = i.id)
    when matched then
        update set i.conta_id = dados.conta_id
