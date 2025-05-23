update dam set situacao = 'PAGO' where id in (
select dam.ID from restituicao res
inner join itemrestituicao item on item.RESTITUICAO_ID = res.id
inner join itemdam idam on idam.PARCELA_ID = item.PARCELAVALORDIVIDA_ID
inner join dam dam on dam.id = idam.dam_id
where res.SITUACAO = 'FINALIZADO'
  and dam.situacao = 'CANCELADO'
  and dam.id in (select ilb.dam_id from ItemLoteBaixa ilb
                  inner join LoteBaixa lb on lb.id = ilb.lotebaixa_id
                  where lb.SITUACAOLOTEBAIXA in ('BAIXADO','BAIXADO_INCONSITENTE')))
