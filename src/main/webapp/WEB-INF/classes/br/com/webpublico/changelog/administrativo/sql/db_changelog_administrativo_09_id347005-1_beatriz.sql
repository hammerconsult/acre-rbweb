merge into registrosolmatext sol
using (select s.id          as id_sol,
              mfp.pessoa_id as id_pessoa
       from registrosolmatext s
                inner join contratofp cfp on s.contratofp_id = cfp.id
                inner join vinculofp vfp on cfp.id = vfp.id
                inner join matriculafp mfp on vfp.matriculafp_id = mfp.id) dados
on (sol.id = dados.id_sol)
when matched then
    update
    set sol.representanteaderentepf_id = dados.id_pessoa ;
