update AberturaInscricaoResto transp set transp.UNIDADEORGANIZACIONAL_ID = (
select emp.UNIDADEORGANIZACIONAL_ID from AberturaInscricaoResto air
inner join empenho emp on air.EMPENHO_ID = emp.id
where transp.id = air.id
)
