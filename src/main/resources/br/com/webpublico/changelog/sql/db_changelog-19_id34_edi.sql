update liquidacaoestorno le set le.exercicio_id = (
  select liq.exercicio_id from liquidacao liq
  where liq.id = le.liquidacao_id
)
where le.exercicio_id is null

