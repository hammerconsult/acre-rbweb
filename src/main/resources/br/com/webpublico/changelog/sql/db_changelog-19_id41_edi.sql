update borderopagamento bp set bp.tipooperacaopagto = (
  select p.tipooperacaopagto from pagamento p
  where p.id = bp.pagamento_id
)
