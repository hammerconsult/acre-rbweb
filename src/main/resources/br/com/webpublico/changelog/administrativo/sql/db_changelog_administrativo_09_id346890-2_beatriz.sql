merge into gestorcontrato gc
using (select g.id        as id_gestor,
              mfp.pessoa_id as id_pessoa
       from gestorcontrato g
                inner join contratofp cfp on g.servidor_id = cfp.id
                inner join vinculofp vfp on cfp.id = vfp.id
                inner join matriculafp mfp on vfp.matriculafp_id = mfp.id) dados
on (gc.id = dados.id_gestor)
when matched then
    update
    set gc.servidorpf_id = dados.id_pessoa ;
