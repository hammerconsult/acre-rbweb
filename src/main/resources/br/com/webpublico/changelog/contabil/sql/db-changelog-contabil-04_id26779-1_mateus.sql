update arquivoremessabanco arq set arq.valortotalbor = (
select sum(b.VALOR) from ArquivoRemessaBanco arb
inner join ArquivoRemBancoBordero arbb on arb.id = arbb.ARQUIVOREMESSABANCO_ID
inner join bordero b on arbb.BORDERO_ID = b.id
where arb.id = arq.id
) where arq.id in (select arb.id from ArquivoRemessaBanco arb
inner join ArquivoRemBancoBordero arbb on arb.id = arbb.ARQUIVOREMESSABANCO_ID
inner join bordero b on arbb.BORDERO_ID = b.id
group by arb.valortotalbor, arb.DATAGERACAO, arb.id
having arb.valortotalbor <> sum(b.VALOR)
)
