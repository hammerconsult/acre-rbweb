update CONFIGURACAOACRESCIMOS set TIPOBASEHONORARIOS = 'VENCIMENTO' where TIPOBASEHONORARIOS is null;
update CONFIGURACAOACRESCIMOS set TIPOBASEMULTA = 'VENCIMENTO' where TIPOBASEMULTA is null;
insert into honorariosconfigacrescimos (id, honorariosadvocaticios, iniciovigencia, configuracaoacrescimos_id)
select hibernate_sequence.nextval, HONORARIOSADVOCATICIOS, to_date('01/01/1980','dd/MM/yyyy'), id from CONFIGURACAOACRESCIMOS
where not exists (select id from HONORARIOSCONFIGACRESCIMOS where HONORARIOSCONFIGACRESCIMOS.CONFIGURACAOACRESCIMOS_ID = CONFIGURACAOACRESCIMOS.id);
