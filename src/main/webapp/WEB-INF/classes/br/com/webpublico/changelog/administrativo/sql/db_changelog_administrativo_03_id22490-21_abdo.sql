update lotereducaovalorbem lote set situacaolotereducaovalorbem =
case when exists (select 1 from loteestornoreducaovalorbem estorno where estorno.lotereducaovalorbem_id = lote.id) then 'ESTORNO'
else 'NORMAL' end;
