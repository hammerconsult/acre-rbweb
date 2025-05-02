update ITEMPROCESSODECOMPRA
set SITUACAOITEMPROCESSODECOMPRA = 'HOMOLOGADO'
where id in (select item.id
             from ITEMPROCESSODECOMPRA item
                      inner join loteprocessodecompra lote on lote.id = item.LOTEPROCESSODECOMPRA_ID
                      inner join processodecompra pc on pc.id = lote.PROCESSODECOMPRA_ID
                      inner join dispensadelicitacao disp on disp.PROCESSODECOMPRA_ID = pc.id
             where disp.SITUACAO = 'CONCLUIDO');
