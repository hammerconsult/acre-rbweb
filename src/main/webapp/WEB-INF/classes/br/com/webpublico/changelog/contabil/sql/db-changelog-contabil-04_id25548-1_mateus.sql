update DOCTOFISCALLIQUIDACAO set saldo = valor where id in (
select d.id from DOCTOFISCALLIQUIDACAO d
inner join liquidacao l on d.LIQUIDACAO_ID = l.id
inner join empenho e on l.EMPENHO_ID = e.id
where
l.CATEGORIAORCAMENTARIA = 'RESTO'
and l.saldo > 0
and e.TIPORESTOSPROCESSADOS = 'PROCESSADOS'
and e.EXERCICIO_ID = (select id from exercicio where ano = 2018)
)
