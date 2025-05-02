begin
for registro in (select l.id as lote_id,
					       ns.id as setor_id
					   from cadastroimobiliario ci
					  inner join lote l on l.id = ci.lote_id
					  inner join setor s on s.id = l.setor_id
					  inner join setor ns on ns.codigo = substr(ci.inscricaocadastral, 3, 3)
					where s.codigo = '000')
   loop
update lote set setor_id = registro.setor_id where id = registro.lote_id;
end loop;
end;
