update REPROCESSCONTABILHISTORICO rep set rep.PROCESSADOSCOMERRO = (
select count(1) from REPROCESSCONTABILLOG log
where rep.id = log.REPROCESSCONTABILHISTORICO_ID
and log.LOGDEERRO = 1
and log.classeobjeto is not null
);

update REPROCESSCONTABILHISTORICO rep set rep.PROCESSADOSSEMERRO = (
select count(1) from REPROCESSCONTABILLOG log
where rep.id = log.REPROCESSCONTABILHISTORICO_ID
and log.LOGDEERRO = 0
and log.classeobjeto is not null
);
