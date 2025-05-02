insert into calculo
select
cancelamento.id ,
cancelamento.DATACANCELAMENTO,
0,
0,
0,
0,
pp.cadastro_id,
1,
'CANCELAMENTO_PARCELAMENTO',
null,
194859027,
' ',
null
from CANCELAMENTOPARCELAMENTO cancelamento
inner join processoparcelamento pp on pp.id = cancelamento.PROCESSOPARCELAMENTO_ID;
