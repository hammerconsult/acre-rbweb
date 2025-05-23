update processocalculoalvara
set alteroufuncionamento = 0 where id in (select p.id from processocalculoalvara p
                                          inner join calculoalvara ca on p.id = ca.processocalculoalvara_id
                                          where p.alteroufuncionamento = 1
                                          and ca.tipoalvara = 'FUNCIONAMENTO'
                                          and ca.controlecalculo <> 'RECALCULO');
