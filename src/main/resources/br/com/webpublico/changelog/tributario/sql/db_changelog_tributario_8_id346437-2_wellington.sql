begin
for registro in (select c.processocalculo_id as id_processo,
	                        vd.id as id_valordivida,
						    spvd.id as id_situacao,
						    e.id as id_exercicio,
						    cd.ano,
						    cd.numero
						   from corrige_debito_af cd
						  inner join exercicio e on e.ano = cd.ano
						  inner join valordivida vd on vd.id = cd.id_valordivida
                          inner join calculo c on c.id = vd.calculo_id
						  inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id
						  inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id)
	loop
update valordivida set exercicio_id = registro.id_exercicio
where id = registro.id_valordivida;
update processocalculo set exercicio_id = registro.id_exercicio
where id = registro.id_processo;
update situacaoparcelavalordivida set referencia = 'Exercício: '||registro.ano||' Processo:'||registro.numero
where id = registro.id_situacao;
end loop;
end;
