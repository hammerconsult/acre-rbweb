merge into alvara using
(select aud.id as alvara_id, revisao.datahora
 from alvara_aud aud
 inner join revisaoauditoria revisao on aud.rev = revisao.id
 where aud.rev = (select aud2.rev from alvara_aud aud2
                  where aud2.id = aud.id
                  order by aud2.rev fetch first 1 rows only)) dados
on (alvara.id = dados.alvara_id)
when matched then update set alvara.dataalteracao = dados.datahora
where extract(hour from alvara.dataalteracao) = 0
and extract(minute from alvara.dataalteracao) = 0
and extract(second from alvara.dataalteracao) = 0
