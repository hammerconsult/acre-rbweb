update Propriedade set veioPorITBI = 1 where id in (
select prop.id from Propriedade prop
inner join cadastroimobiliario ci on prop.imovel_id = ci.id
inner join CalculoITBI citbi on citbi.CADASTROIMOBILIARIO_ID = ci.id
inner join AdquirentesCalculoITBI ac on citbi.id = ac.CALCULOITBI_ID and ac.ADQUIRENTE_ID = prop.PESSOA_ID
where prop.FINALVIGENCIA is not null
and citbi.SITUACAO in ('PAGO', 'ASSINADO', 'EMITIDO')
)
