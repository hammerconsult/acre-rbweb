update processocalculo p set p.anoprotocolo = (select ex.ano from processocalculoalvara proc
                                               inner join exercicio ex on proc.exercicio_id = ex.id
                                               where p.id = proc.id)
where p.id in (select id from processocalculoalvara)
and p.anoprotocolo is null;
