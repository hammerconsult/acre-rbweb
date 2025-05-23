insert into loterpsxml (id, loterps_id, xml)
select hibernate_sequence.nextval, id, xml
from loterps
where xml is not null
  and situacao = 'AGUARDANDO'
