insert into execcontratoempsolemp
select hibernate_sequence.nextVal, id, solicitacaoempenho_id
   from execucaocontratoempenho
where solicitacaoempenho_id is not null;
