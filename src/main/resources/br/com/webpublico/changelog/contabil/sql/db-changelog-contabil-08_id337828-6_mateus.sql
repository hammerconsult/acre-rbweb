update pagamento pag set idobn600 = (
  select distinct arb.id from ArquivoRemessaBanco arb
    inner join ArquivoRemBancoBordero arbb on arb.id = arbb.ARQUIVOREMESSABANCO_ID
    inner join bordero bord on arbb.BORDERO_ID = bord.id
    inner join BorderoPagamento bp on bord.id = bp.BORDERO_ID
  where bp.PAGAMENTO_ID = pag.id
  and arb.id = (select max(a.id)
  from ArquivoRemessaBanco a
    inner join ArquivoRemBancoBordero ar on a.id = ar.ARQUIVOREMESSABANCO_ID
    inner join bordero b on ar.BORDERO_ID = b.id
    inner join BorderoPagamento bpag on b.id = bpag.BORDERO_ID
                             where bpag.PAGAMENTO_ID = bp.pagamento_id)
)
