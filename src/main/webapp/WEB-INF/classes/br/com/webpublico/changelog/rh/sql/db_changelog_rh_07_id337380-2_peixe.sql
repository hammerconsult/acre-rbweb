insert into itembasefp(ID, DATAREGISTRO, OPERACAOFORMULA, BASEFP_ID, EVENTOFP_ID, SOMAVALORRETROATIVO, TIPOVALOR)
select HIBERNATE_SEQUENCE.nextval,
       current_date,
       'ADICAO',
       (select id from basefp where codigo = '1018'),
       e.id,
       1,
       'NORMAL'
from eventofp e
where e.codigo = '101'
  and e.id not in (select it.eventofp_id
                   from itembasefp it
                            inner join basefp base on it.BASEFP_ID = base.ID
                            inner join eventofp ev on it.EVENTOFP_ID = ev.ID
                   where base.codigo = '1018'
                     and ev.codigo = '101')
