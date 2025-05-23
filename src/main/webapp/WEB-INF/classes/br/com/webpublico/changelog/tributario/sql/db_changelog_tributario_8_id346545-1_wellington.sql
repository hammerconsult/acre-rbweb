declare
  id_exercicio numeric(19,0);
  id_usuario numeric(19,0);
  id_atolegal numeric(19,0);
  id_processo numeric(19,0);
  codigo_processo numeric(19,0);
  id_itemprocesso numeric(19,0);
begin
select id into id_exercicio from exercicio where ano = 2024;
select id into id_usuario from usuariosistema where login = 'vanilce.lima';
select id into id_atolegal from atolegal where nome = 'LEI Nº 1.508 DE 08.12.2003 - CODIGO TRIBUTARIO MUNICIPAL';

for cadastro in (select ci.id, ci.inscricaocadastral
	  			        from cadastroimobiliario ci
					 where ci.inscricaocadastral between '100209810900129' and '100209810900256'
				       and exists (select 1
								      from calculoiptu iptu
									 inner join valordivida vd on vd.calculo_id = iptu.id
									 inner join exercicio e on e.id = vd.exercicio_id
									 inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id
									 inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id
								   where e.id = id_exercicio
									 and iptu.cadastroimobiliario_id = ci.id
									 and spvd.situacaoparcela = 'EM_ABERTO')
					 order by ci.inscricaocadastral)
   loop
select hibernate_sequence.nextval into id_processo from dual;
select coalesce(max(codigo), 0) + 1 into codigo_processo from processodebito
where tipo = 'CANCELAMENTO' and exercicio_id = id_exercicio;

insert into processodebito (id, codigo, usuarioincluiu_id, lancamento, numeroprotocolo,
                            tipo, motivo, situacao, exercicio_id, atolegal_id, cadastro_id, tipocadastro)
values (id_processo, codigo_processo, id_usuario, to_date('10/10/2024', 'dd/MM/yyyy'), '27949/2024',
        'CANCELAMENTO', 'Cancelamento de IPTU/TSU concedida a todos os imóveis compõem o Bloco B do condomínio ' ||
                        'RESIDENCIAL SPORTS GARDENS DA AMAZONIA SPE LTDA, os quais não foram construídos no ano de 2024 e assim ' ||
                        'não deveriam ter sido lançados/cobrados IPTU/TSU de acordo com o parecer do processo/protocolo nº 27949/2024',
        'FINALIZADO', id_exercicio, id_atolegal, cadastro.id, 'IMOBILIARIO');

for parcela in (select pvd.id, spvd.referencia
					      from calculoiptu iptu
						 inner join valordivida vd on vd.calculo_id = iptu.id
						 inner join exercicio e on e.id = vd.exercicio_id
						 inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id
						 inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id
					   where e.id = id_exercicio
						 and iptu.cadastroimobiliario_id = cadastro.id
						 and spvd.situacaoparcela = 'EM_ABERTO')
      loop
select hibernate_sequence.nextval into id_itemprocesso from dual;

insert into itemprocessodebito (id, processodebito_id, parcela_id, situacaoanterior,
                                situacaoproxima, referencia)
values (id_itemprocesso, id_processo, parcela.id, 'EM_ABERTO', 'CANCELAMENTO', parcela.referencia);

insert into situacaoparcelavalordivida (id, datalancamento, situacaoparcela, parcela_id,
                                        saldo, inconsistente, referencia)
values (hibernate_sequence.nextval, to_date('10/10/2024', 'dd/MM/yyyy'), 'CANCELAMENTO',
        parcela.id, 0, 0, parcela.referencia);

for itemparcela in (select  ipvd.id, ivd.tributo_id, ipvd.valor
      	                        from itemparcelavalordivida ipvd
      	                       inner join itemvalordivida ivd on ivd.id = ipvd.itemvalordivida_id
      	                     where ipvd.parcelavalordivida_id = parcela.id)
      	 loop
      	 	insert into itemprocessodebitoparcela (id, itemprocessodebito_id, itemparcelavalordivida_id,
      	 	tributo_id, valor, valororiginal)
      	 	values (hibernate_sequence.nextval, id_itemprocesso, itemparcela.id, itemparcela.tributo_id,
      	 	itemparcela.valor, itemparcela.valor);
end loop;
end loop;
end loop;
end;
