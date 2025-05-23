update borderopagamento bp set bp.contacorrentefavorecido_id = (
  select p.contapgto_id from pagamento p
  where p.id = bp.pagamento_id
)
