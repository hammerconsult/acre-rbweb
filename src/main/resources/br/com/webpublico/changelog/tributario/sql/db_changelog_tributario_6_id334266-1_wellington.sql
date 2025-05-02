update calculo
set observacao = null
where id > 0
  and id in (select c.id
             from calculo c
                      inner join processocalculo pc on pc.id = c.processocalculo_id
                      inner join declaracaomensalservico dms on dms.processocalculo_id = pc.id)
