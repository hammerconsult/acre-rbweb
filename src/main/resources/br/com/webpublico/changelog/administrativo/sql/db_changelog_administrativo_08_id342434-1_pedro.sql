delete from processohistorico ph where ph.id = (select id from processohistorico where PROCESSO_ID = 10867008298 ORDER BY DATAHORA DESC FETCH FIRST 1 ROW ONLY);
update processo p set SITUACAO = (select ph.situacaoprocesso from processohistorico ph where ph.PROCESSO_ID = 10867008298 order by ph.DATAHORA desc FETCH FIRST 1 ROW ONLY) where p.ID = 10867008298;
update processo set TRAMITEFINALIZADOR_ID = null where id = 10867008298;
update tramite set STATUS = 1 where PROCESSO_ID = 10867008298;
