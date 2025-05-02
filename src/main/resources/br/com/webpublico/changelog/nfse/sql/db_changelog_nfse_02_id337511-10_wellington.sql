insert into loterpslog (id, loterps_id, log)
select hibernate_sequence.nextval, id, log
from loterps
where log is not null
  and situacao = 'AGUARDANDO'

