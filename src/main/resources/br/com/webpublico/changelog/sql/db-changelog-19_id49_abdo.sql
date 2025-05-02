  update dam set situacao = 'PAGO' where id in(
  select dam.id
     from itemprocessodebito ipd
    inner join processodebito pd on ipd.processodebito_id = pd.id
    inner join situacaoparcelavalordivida spvd on spvd.id = getultimasituacao(ipd.parcela_id)
    inner join itemdam on itemdam.parcela_id = ipd.parcela_id
    inner join dam on dam.id = itemdam.dam_id
  where pd.situacao = 'FINALIZADO' and pd.tipo = 'BAIXA' and spvd.situacaoparcela = 'BAIXADO' and dam.situacao <> 'PAGO'
   and itemdam.id = (select max(s_itemdam.id) from itemdam s_itemdam where s_itemdam.parcela_id = ipd.parcela_id))