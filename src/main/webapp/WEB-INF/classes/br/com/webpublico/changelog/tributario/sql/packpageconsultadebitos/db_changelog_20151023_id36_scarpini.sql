update calculo set PROCESSOCALCULO_ID = null where PROCESSOCALCULO_ID in (
select calculo.PROCESSOCALCULO_ID
from CANCELAMENTOPARCELAMENTO cancelamento
INNER JOIN CALCULO ON CALCULO.ID = CANCELAMENTO.ID
inner join processocalculoiptu iptu on iptu.id = calculo.processocalculo_id)
