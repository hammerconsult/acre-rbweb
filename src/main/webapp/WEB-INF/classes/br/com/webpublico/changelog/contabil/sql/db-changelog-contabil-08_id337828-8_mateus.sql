update pagamentoextra pagext set idobn600 = (
  select distinct arb.id from ArquivoRemessaBanco arb
    inner join ArquivoRemBancoBordero arbb on arb.id = arbb.ARQUIVOREMESSABANCO_ID
    inner join bordero bord on arbb.BORDERO_ID = bord.id
    inner join BorderoPagamentoExtra bpe on bord.id = bpe.BORDERO_ID
  where bpe.PAGAMENTOEXTRA_ID = pagext.id
  and arb.id = (select max(a.id)
                from ArquivoRemessaBanco a
                  inner join ArquivoRemBancoBordero ar on a.id = ar.ARQUIVOREMESSABANCO_ID
                  inner join bordero b on ar.BORDERO_ID = b.id
                  inner join BorderoPagamentoExtra bpage on b.id = bpage.BORDERO_ID
                where bpage.PAGAMENTOEXTRA_ID = bpe.PAGAMENTOEXTRA_ID)
)
