update processoparcelamento set numeroreparcelamento = 1 where id in (
select pp.id from processoparcelamento pp
inner join paramparcelamento parampp on parampp.id = pp.paramparcelamento_id
where parampp.descricao like '%REPARCELAMENTO%'
  and pp.numeroreparcelamento = 0
  and exists (select ipp.id from itemprocessoparcelamento ipp where ipp.processoparcelamento_id = pp.id)
)
