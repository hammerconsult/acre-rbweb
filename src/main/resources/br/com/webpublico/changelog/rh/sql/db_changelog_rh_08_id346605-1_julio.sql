update itemfichafinanceirafp itemficha
set eventofp_id =
        (select evento.id from eventofp evento where evento.codigo = '554')
where itemficha.id in (
    select distinct item.id
    from fichafinanceirafp ficha
             inner join itemfichafinanceirafp item on ficha.id = item.fichafinanceirafp_id
             inner join folhadepagamento folha on ficha.folhadepagamento_id = folha.id
             inner join eventofp evento on evento.id = item.eventofp_id
    where folha.mes = 9
      and folha.ano = 2024
      and item.tipoeventofp = 'DESCONTO'
      and evento.codigo = '268');
