update borderopagamento bp set bp.valor = (
  select p.valor from pagamento p
  where p.id = bp.pagamento_id
)