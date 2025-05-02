UPDATE AUTUACAOFISCALIZACAO AG SET AG.agenteautuador_id = (select distinct agente.id from contratofp cfp
                                                           inner join vinculofp vfp on vfp.id = cfp.id
                                                           inner join matriculafp mfp on mfp.id = vfp.matriculafp_id
                                                           inner join agenteautuador agente on agente.pessoafisica_id = mfp.pessoa_id
                                                           where cfp.id = ag.agenteautuador_id)
