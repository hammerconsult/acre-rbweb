merge into itemlotebaixaparcela item
using (select ilbp.id as ilbparcela_id,
       cast(round((round((round((((select coalesce((idam.valororiginaldevido + idam.juros + idam.multa + idam.correcaomonetaria + idam.honorarios - idam.desconto), 0)
                                   from itemdam idam where idam.id = ilbp.itemdam_id) * 100) / ilb.valorpago), 6) / 100), 6) * ilb.valorpago), 2) as decimal(12, 2)) as proporcional
       from itemlotebaixa ilb
       inner join lotebaixa lb on ilb.lotebaixa_id = lb.id
       inner join itemlotebaixaparcela ilbp on ilb.id = ilbp.itemlotebaixa_id
       where lb.situacaolotebaixa in ('BAIXADO', 'BAIXADO_INCONSITENTE')
       and coalesce(ilbp.valorpago, 0) = 0
       and coalesce(ilb.valorpago, 0) > 0
       and (select coalesce(valororiginaldevido, 0) from itemdam where id = ilbp.itemdam_id) > 0) dados
on (item.id = dados.ilbparcela_id)
when matched then update
set item.valorpago = dados.proporcional
