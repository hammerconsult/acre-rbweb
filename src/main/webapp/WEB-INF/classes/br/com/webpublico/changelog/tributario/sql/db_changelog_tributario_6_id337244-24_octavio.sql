update laudoavaliacaoitbi laudo
set laudo.processocalculoitbi_id = (select citbi.processocalculoitbi_id from calculoitbi citbi
                                    where citbi.id = laudo.calculoitbi_id)
where laudo.processocalculoitbi_id is null
