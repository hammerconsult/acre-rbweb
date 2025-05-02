insert into configagendamentotarefa (id, hora, minuto, tipotarefaagendada, cron)
select HIBERNATE_SEQUENCE.nextval, 0, 0, 'ATUALIZACAO_RBT12', '0 0 1 1 * ?'
from dual
where not exists (select 1 from configagendamentotarefa
                  where TIPOTAREFAAGENDADA = 'ATUALIZACAO_RBT12')
