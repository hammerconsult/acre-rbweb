update empenhoestorno ee set ee.exercicio_id = (
    select emp.exercicio_id from empenho emp
    where emp.id = ee.empenho_id
)
where ee.exercicio_id is null

