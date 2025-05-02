update CONTRATORENDASPATRIMONIAIS set ATIVIDADEPONTO_ID = 194606648
where id = 11146774767;

update calculopessoa set pessoa_id = 75216586
where calculo_id in (
    select vd.calculo_id
    from processoparcelamento pp
             inner join exercicio e on e.id = pp.exercicio_id
             inner join itemprocessoparcelamento ipp on ipp.processoparcelamento_id = pp.id
             inner join PARCELAVALORDIVIDA pvd on pvd.id = ipp.parcelavalordivida_id
             inner join valordivida vd on vd.id = pvd.valordivida_id
    where pp.numero = 3890
      and e.ano = 2024);

update processoparcelamento set pessoa_id = 75216586
where id in (
    select pp.id
    from processoparcelamento pp
             inner join exercicio e on e.id = pp.exercicio_id
    where pp.numero = 3890
      and e.ano = 2024);

update calculopessoa set pessoa_id = 75216586
where calculo_id in (
    select pp.id
    from processoparcelamento pp
             inner join exercicio e on e.id = pp.exercicio_id
    where pp.numero = 3890
      and e.ano = 2024);
