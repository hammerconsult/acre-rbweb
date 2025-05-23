merge into fiscalinternocontrato fi
using (select fic.id        as id_fiscal,
              mfp.pessoa_id as id_pessoa
       from fiscalinternocontrato fic
                inner join contratofp cfp on fic.servidor_id = cfp.id
                inner join vinculofp vfp on cfp.id = vfp.id
                inner join matriculafp mfp on vfp.matriculafp_id = mfp.id) dados
on (fi.id = dados.id_fiscal)
when matched then
    update
    set fi.servidorpf_id = dados.id_pessoa ;
