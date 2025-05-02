merge into ITEMPREGAO item
using ITEMPREGAOLANCEVENCEDOR iplv on (iplv.lancepregao_id = item.LANCEPREGAOVENCEDOR_ID)
when matched then update set item.ITEMPREGAOLANCEVENCEDOR_ID = iplv.ID
