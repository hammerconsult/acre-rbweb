update AberturaInscricaoResto transp set transp.data = (
select emp.dataempenho from AberturaInscricaoResto air
inner join empenho emp on air.EMPENHO_ID = emp.id
where transp.id = air.id
)
