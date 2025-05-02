update borderotransffinanceira b set b.tipooperacaopagto = (
  select b.tipooperacaopagto from transferenciacontafinanc t
  where t.id = b.transffinanceira_id
)
