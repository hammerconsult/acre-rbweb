update pagamentoestorno pe set pe.exercicio_id = (
  select pag.exercicio_id from pagamento pag
  where pag.id = pe.pagamento_id
)
where pe.exercicio_id is null

