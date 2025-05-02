update borderotransffinanceira b set b.valor = (
  select t.valor from transferenciacontafinanc t
  where t.id = b.transffinanceira_id
)