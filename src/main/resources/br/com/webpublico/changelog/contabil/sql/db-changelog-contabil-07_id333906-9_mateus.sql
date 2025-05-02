merge into conta conta using (
  select
    c.id
  from conta c
  where c.exercicio_id = (select id
                          from exercicio
                          where ano = 2021)
        and c.Dtype like 'ContaDeDestinacao'
        and c.superior_id is not null
  group by c.id, c.codigo having nivelestrutura(c.codigo, '.') = 2
) cd on (conta.id = cd.id)
when matched then update set PERMITIRDESDOBRAMENTO = 1, categoria =  'SINTETICA'
