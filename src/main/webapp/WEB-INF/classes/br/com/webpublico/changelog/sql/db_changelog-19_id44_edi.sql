update borderopagamentoextra bp set bp.tipooperacaopagto = (
  select p.tipooperacaopagto from pagamentoextra p
  where p.id = bp.pagamentoextra_id
)
