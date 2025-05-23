create or replace
function numero_ajuizamento_parcela( 
PARCELA_ID in number 
) return VARCHAR2  
is PROCESSOS VARCHAR2(255);
begin  
select listagg(numeroprocessoforum, ',')  within group (order by numeroprocessoforum) as numero 
INTO PROCESSOS
from (
with cteparcelamento(originada, parcelamento, original, calculo) as (
select pvd.id as originada, parcelamento.id as parcelamento, pvdoriginal.id as original, calculo.id as calculo
from parcelavalordivida pvd
inner join valordivida vd on vd.id = pvd.valordivida_id
inner join processoparcelamento parcelamento on parcelamento.id = vd.CALCULO_ID
inner join ITEMPROCESSOPARCELAMENTO itemparcelamento on itemparcelamento.PROCESSOPARCELAMENTO_ID = parcelamento.id
inner join parcelavalordivida pvdoriginal on pvdoriginal.id = itemparcelamento.PARCELAVALORDIVIDA_ID
inner join valordivida vdoriginal on vdoriginal.id = pvdoriginal.valordivida_id
inner join calculo on calculo.id = vdoriginal.calculo_id
where pvd.id = PARCELA_ID and rownum = 1
union all
select pvd.id as originada, parcelamento.id as parcelamento, pvdoriginal.id as original, calculo.id as calculo
from parcelavalordivida pvd
inner join CTEPARCELAMENTO cte on cte.original = pvd.id
left join valordivida vd on vd.id = pvd.valordivida_id
left join processoparcelamento parcelamento on parcelamento.id = vd.CALCULO_ID
left join ITEMPROCESSOPARCELAMENTO itemparcelamento on itemparcelamento.PROCESSOPARCELAMENTO_ID = parcelamento.id
left join parcelavalordivida pvdoriginal on pvdoriginal.id = itemparcelamento.PARCELAVALORDIVIDA_ID
left join valordivida vdoriginal on vdoriginal.id = pvdoriginal.valordivida_id
inner join calculo on calculo.id = vdoriginal.calculo_id
where rownum= 1
)

SEARCH DEPTH FIRST BY parcelamento SET SORTING
CYCLE parcelamento SET is_cycle TO 1 DEFAULT 0

select distinct processo.numeroprocessoforum
from CTEPARCELAMENTO cte
inner join parcelavalordivida pvd on pvd.id = cte.originada
left join ITEMINSCRICAODIVIDAATIVA itemDA on itemDA.id= cte.calculo
left join ITEMCERTIDAODIVIDAATIVA itemcda on itemcda.iteminscricaodividaativa_id = itemda.id
left join CERTIDAODIVIDAATIVA cda on cda.id = itemcda.certidao_id
left join processojudicialcda processocda on processocda.certidaodividaativa_id = cda.id
left join processojudicial processo on processo.id = processocda.processojudicial_id
union all
select processo.numeroprocessoforum 
from processojudicial processo 
inner join ProcessoJudicialCDA processocda on processocda.processojudicial_id = processo.id 
inner join certidaodividaativa cda on cda.id = processocda.certidaodividaativa_id
inner join itemcertidaodividaativa itemcda on itemcda.certidao_id = cda.id 
inner join iteminscricaodividaativa itemda on itemda.id = itemcda.iteminscricaodividaativa_id 
inner join valordivida vd on vd.calculo_id = itemda.id
inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id 
where pvd.id = PARCELA_ID);
return PROCESSOS;
end;