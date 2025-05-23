insert into execucaocontratoliquidest (id, execucaocontratoempenhoest_id, solicitacaoliquidacaoest_id)
select hibernate_sequence.nextval,
       sol.execucaocontratoempenhoest_id,
       sol.id
from solicitacaoliquidacaoest sol
where sol.execucaocontratoempenhoest_id is not null;
