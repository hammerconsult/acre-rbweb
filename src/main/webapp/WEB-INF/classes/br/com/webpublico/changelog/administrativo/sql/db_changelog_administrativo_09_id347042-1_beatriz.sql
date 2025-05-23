merge into solicitaprorrogacaocessao sol
using (
    select s.id              as id_sol,
           rp.responsavel_id as resp_atual
    from solicitaprorrogacaocessao s
             inner join lotecessao c on s.lotecessao_id = c.id
             inner join responsavelpatrimonio rp on rp.unidadeorganizacional_id = c.unidadeorigem_id
    where sysdate between trunc(rp.iniciovigencia) and coalesce(trunc(rp.fimvigencia), sysdate)
      and s.responsavelorigem_id is null
) dados
on (sol.id = dados.id_sol)
when matched then
    update set sol.responsavelorigem_id = dados.resp_atual ;
