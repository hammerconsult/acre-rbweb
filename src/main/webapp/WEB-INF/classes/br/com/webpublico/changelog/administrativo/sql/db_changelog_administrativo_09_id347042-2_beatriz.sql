merge into solicitaprorrogacaocessao sol
using (
    select s.id              as id_sol,
           rp.responsavel_id as resp_atual
    from solicitaprorrogacaocessao s
             inner join lotecessao c on s.lotecessao_id = c.id
             inner join responsavelpatrimonio rp on rp.unidadeorganizacional_id = c.unidadedestino_id
    where sysdate between trunc(rp.iniciovigencia) and coalesce(trunc(rp.fimvigencia), sysdate)
      and c.tipocessao = 'INTERNO'
      and s.responsaveldestino_id is null
) dados
on (sol.id = dados.id_sol)
when matched then
    update set sol.responsaveldestino_id = dados.resp_atual ;
