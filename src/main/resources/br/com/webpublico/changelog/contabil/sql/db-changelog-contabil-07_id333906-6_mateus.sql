delete from CONTADEDESTINACAO cd
where cd.id in (
  select c.id from conta c
  where c.EXERCICIO_ID in (select id from exercicio where ano in (2020, 2021)) and c.permitirdesdobramento = 0
        and c.dtype = 'ContaDeDestinacao');

delete from CONTAEQUIVALENTE ce
where ce.CONTAORIGEM_ID in (
  select c.id from conta c
  where c.EXERCICIO_ID in (select id from exercicio where ano in (2020)) and c.permitirdesdobramento = 0
        and c.dtype = 'ContaDeDestinacao');

delete from CONTAEQUIVALENTE ce
where ce.CONTADESTINO_ID in (
  select c.id from conta c
  where c.EXERCICIO_ID in (select id from exercicio where ano in (2020)) and c.permitirdesdobramento = 0
        and c.dtype = 'ContaDeDestinacao');

delete from conta cd
where cd.id in (
  select c.id from conta c
  where c.EXERCICIO_ID in (select id from exercicio where ano in (2020, 2021)) and c.permitirdesdobramento = 0
        and c.dtype = 'ContaDeDestinacao');

update conta c set PERMITIRDESDOBRAMENTO = 0, categoria = 'ANALITICA'
where c.dtype = 'ContaDeDestinacao'
      and  c.EXERCICIO_ID in (select id from exercicio where ano in (2020, 2021))
      and c.superior_id is not null;
