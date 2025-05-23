update condutorottarquivo condutorarquivo
set condutorarquivo.exercicio_id =
(
select exec.id from condutoroperatransporte cond
inner join exercicio exec on exec.ano = extract(year from cond.datacadastro)
where cond.id = condutorarquivo.condutorott_id
) where condutorarquivo.exercicio_id is null
