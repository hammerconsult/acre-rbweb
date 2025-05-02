update pagamentoextra pagExt set idobn350 = (
    select ape.arquivo_id from ARQUIVO_PAGAMENTOEXTRA ape
    where ape.PAGAMENTOEXTRA_ID = pagExt.id
     and ape.ARQUIVO_ID = (select max(a.ARQUIVO_ID) from ARQUIVO_PAGAMENTOEXTRA a where a.PAGAMENTOEXTRA_ID = ape.PAGAMENTOEXTRA_ID)
)
