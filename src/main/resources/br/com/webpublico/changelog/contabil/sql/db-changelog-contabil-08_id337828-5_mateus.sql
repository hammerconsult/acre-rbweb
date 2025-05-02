update pagamento pag set idobn350 = (
    select ap.arquivo_id from ARQUIVO_PAGAMENTO ap
    where ap.PAGAMENTO_ID = pag.id
      and ap.ARQUIVO_ID = (select max(a.ARQUIVO_ID) from ARQUIVO_PAGAMENTO a where a.PAGAMENTO_ID = ap.PAGAMENTO_ID)
)
