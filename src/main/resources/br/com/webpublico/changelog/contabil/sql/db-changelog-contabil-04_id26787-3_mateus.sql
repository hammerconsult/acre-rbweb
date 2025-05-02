update empenhoestorno est set est.saldodisponivel = (
select emp.saldodisponivel from empenho emp
where emp.id = est.empenho_id
)
