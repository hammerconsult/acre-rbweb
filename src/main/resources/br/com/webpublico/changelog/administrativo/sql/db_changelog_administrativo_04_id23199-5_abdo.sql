insert into execucaocontratoempenho (id, execucaocontrato_id, empenho_id, solicitacaoempenho_id)
select hibernate_sequence.nextVal, iec.execucaocontrato_id, se.empenho_id, iecr.solicitacaoempenho_id
   from itemexeccontratoreserva iecr
  inner join itemexecucaocontrato iec on iec.id = iecr.itemexecucaocontrato_id
  inner join solicitacaoempenho se on se.id = iecr.solicitacaoempenho_id
