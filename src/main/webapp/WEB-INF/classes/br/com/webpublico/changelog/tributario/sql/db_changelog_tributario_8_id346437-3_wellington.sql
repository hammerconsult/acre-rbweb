begin
for registro in (select iida.INSCRICAODIVIDAATIVA_ID as id_processo,
					      vd.id as id_valordivida,
					      spvd.id as id_situacao,
					      e.id as id_exercicio,
					      cd.ano,
					      cd.numero
				       from iteminscricaodividaativa iida
					  inner join valordivida vd on vd.CALCULO_ID = iida.ID
					  inner join parcelavalordivida pvd on pvd.VALORDIVIDA_ID = vd.ID
					  inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id
					  inner join inscricaodividaparcela idp on idp.iteminscricaodividaativa_id = iida.id
					  inner join parcelavalordivida po on po.id = idp.parcelavalordivida_id
					  inner join corrige_debito_af cd on cd.id_valordivida = po.valordivida_id
					  inner join exercicio e on e.ano = cd.ano)
	loop
update valordivida set exercicio_id = registro.id_exercicio
where id = registro.id_valordivida;
update processocalculo set exercicio_id = registro.id_exercicio
where id = registro.id_processo;
update situacaoparcelavalordivida set referencia = 'Exercício: '||registro.ano||' Processo:'||registro.numero
where id = registro.id_situacao;
end loop;
end;
