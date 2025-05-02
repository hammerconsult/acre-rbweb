merge into CALAMIDADEPUBLICABEMSERV cps using (
  select c.id as cId, ux.PESSOAJURIDICA_ID as pessoaId
  from CALAMIDADEPUBLICABEMSERV c
    inner join unidadeexterna ux on c.unidadeexterna_id = ux.id
 where ux.PESSOAJURIDICA_ID is not null
) bemser on (bemser.cId = cps.id)
when matched then update set cps.pessoa_id = bemser.pessoaId
