update lotebaixa set INTEGRACAOARRECADACAO = 1 where id in (
select lb.id from lotebaixa lb
where exists (select item.id from itemintegracaotribcont item
              inner join INTEGRACAOTRIBCONT integrador on integrador.ITEM_ID = item.id
              where item.TIPO = 'ARRECADACAO'
                and item.LOTEBAIXA_ID = lb.id)
);

update lotebaixa set INTEGRACAOARRECADACAO = 0 where id in (
select lb.id from lotebaixa lb
where not exists (select item.id from itemintegracaotribcont item
              inner join INTEGRACAOTRIBCONT integrador on integrador.ITEM_ID = item.id
              where item.TIPO = 'ARRECADACAO'
                and item.LOTEBAIXA_ID = lb.id)
);

update lotebaixa set INTEGRACAOESTORNO = 1 where id in (
select lb.id from lotebaixa lb
where exists (select item.id from itemintegracaotribcont item
              inner join INTEGRACAOTRIBCONT integrador on integrador.ITEM_ID = item.id
              where item.TIPO = 'ESTORNO_ARRECADACAO'
                and item.LOTEBAIXA_ID = lb.id)
and SITUACAOLOTEBAIXA = 'ESTORNADO'
);

update lotebaixa set INTEGRACAOESTORNO = 0 where id in (
select lb.id from lotebaixa lb
where not exists (select item.id from itemintegracaotribcont item
              inner join INTEGRACAOTRIBCONT integrador on integrador.ITEM_ID = item.id
              where item.TIPO = 'ESTORNO_ARRECADACAO'
                and item.LOTEBAIXA_ID = lb.id)
and SITUACAOLOTEBAIXA = 'ESTORNADO'
);

update lotebaixa set INTEGRACAOESTORNO = 0 where INTEGRACAOESTORNO is null;
