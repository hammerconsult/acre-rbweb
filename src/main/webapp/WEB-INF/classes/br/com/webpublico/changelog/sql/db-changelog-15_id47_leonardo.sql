create or replace view vwrhcontrato as 
select vinc.id as id_vinculo, mat.matricula, 
       vinc.numero, 
       pf.nome, 
       cargo.descricao as cargo, 
       vinc.iniciovigencia as iniciovinculo, 
       vinc.finalvigencia as finalvinculo,
       uo.descricao,
       lot.iniciovigencia as iniciolotacao,
       lot.finalvigencia as finalotacao,
       uo.id as id_unidade_lotada
         from pessoafisica pf 
         join matriculafp mat on mat.pessoa_id = pf.id
         join vinculofp vinc on vinc.matriculafp_id = mat.id
         join contratofp cont on cont.id = vinc.id
         join cargo cargo on cargo.id = cont.cargo_id
         left join lotacaofuncional lot on lot.vinculofp_id = vinc.id
         left join unidadeorganizacional uo on uo.id = lot.unidadeorganizacional_id