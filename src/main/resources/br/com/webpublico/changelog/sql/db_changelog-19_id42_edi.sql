update borderopagamentoextra bp set bp.valor = (
  select p.valor from pagamentoextra p
  where p.id = bp.pagamentoextra_id
)
