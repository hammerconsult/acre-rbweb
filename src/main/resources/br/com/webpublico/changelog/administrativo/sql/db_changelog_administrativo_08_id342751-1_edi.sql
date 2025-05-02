update contrato cont
set cont.RESPONSAVELPREFEITURAPF_ID = (select pf.id
                                       from contrato c
                                                inner join contratofp cfp on c.RESPONSAVELPREFEITURA_ID = cfp.id
                                                inner join VINCULOFP vfp on vfp.id = cfp.id
                                                inner join matriculafp mat on mat.id = vfp.MATRICULAFP_ID
                                                inner join pessoafisica pf on pf.id = mat.PESSOA_ID
                                       where c.id = cont.id)
