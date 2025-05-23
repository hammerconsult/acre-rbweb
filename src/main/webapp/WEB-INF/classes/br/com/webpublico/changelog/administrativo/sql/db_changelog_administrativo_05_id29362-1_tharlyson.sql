merge into licitacao o
using (
select distinct
lic.id,
sol.tiponaturezadoprocedimento as naturezadoprocedimento
from licitacao lic
inner join exercicio ex on ex.id = lic.exercicio_id
inner join processodecompra pc on pc.id = lic.processodecompra_id
inner join loteprocessodecompra lote on lote.processodecompra_id = pc.id
inner join itemprocessodecompra item on item.loteprocessodecompra_id = lote.id
inner join itemsolicitacao itemSol on itemSol.id = item.itemsolicitacaomaterial_id
inner join lotesolicitacaomaterial loteSol on loteSol.id = itemsol.lotesolicitacaomaterial_id
inner join solicitacaomaterial sol on sol.id = lotesol.solicitacaomaterial_id
where naturezadoprocedimento is null
and sol.tiponaturezadoprocedimento is not null
) ob on (ob.id = o.id)
when matched then update set o.naturezadoprocedimento = ob.naturezadoprocedimento
