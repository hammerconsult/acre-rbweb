update sociedadecadastroeconomico soc set soc.datainicio = (select ce.abertura from cadastroeconomico ce
inner join CE_SOCIEDADE cesoc on ce.id = cesoc.cadastroeconomico_id
where soc.id = cesoc.sociedadecadastroeconomico_id and ce.abertura is not null)
where soc.datainicio is null
