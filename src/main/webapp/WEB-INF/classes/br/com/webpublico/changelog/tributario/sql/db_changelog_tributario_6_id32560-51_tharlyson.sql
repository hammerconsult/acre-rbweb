update veiculoottarquivo veiculoarquivo
set veiculoarquivo.exercicio_id =
(
select exec.id from veiculoottransporte veic
inner join exercicio exec on exec.ano = extract(year from veic.datacadastro)
where veic.id = veiculoarquivo.veiculoott_id
) where veiculoarquivo.exercicio_id is null
