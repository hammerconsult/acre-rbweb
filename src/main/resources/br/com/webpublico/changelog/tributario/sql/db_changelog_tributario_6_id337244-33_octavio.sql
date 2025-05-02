merge into processocalculoitbi pcitbi
using (select citbi.processocalculoitbi_id as id_processo, citbi.observacao
       from calculoitbi citbi
       where observacao is not null) calculo
on (pcitbi.id = calculo.id_processo)
when matched then update
set pcitbi.observacao = calculo.observacao
