update certidaodividaativa set softplan = 1 where id in (
select cda.id from certidaodividaativa cda
inner join cadastroimobiliario ci on ci.id = cda.cadastro_id
inner join exercicio ex on ex.id = cda.exercicio_id
where cda.pessoa_id not in (select prop.pessoa_id from Propriedade prop where prop.imovel_Id = ci.id and prop.finalvigencia is null)
 and cda.SITUACAOCERTIDAODA = 'ABERTA'
 and ex.ano >= 2015)
