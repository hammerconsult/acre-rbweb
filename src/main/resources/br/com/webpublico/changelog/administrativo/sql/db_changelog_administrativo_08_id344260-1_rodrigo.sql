CREATE OR REPLACE VIEW VWCONTRATOADEQUADO AS select c.id            contrato_id,
       ic.id item_id,
       ipc.numero item_numero,
       oc.descricao as item_descricao
from contrato c
   inner join itemcontrato ic on ic.contrato_id = c.id
   left join itemcontratovigente icv on icv.itemcontrato_id = ic.id
   left join itemcotacao icot on icot.id = icv.itemcotacao_id
   left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id
   left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id
   left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id
   left join itemcontratoitemsolext icise on icise.itemcontrato_id = ic.id
   left join itemcontratoitempropdisp icipfd on icipfd.itemcontrato_id = ic.id
   left join itempropostafornedisp ipfd on icipfd.itempropfornecdispensa_id = ipfd.id
   left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id
   left join itemprocessodecompra ipc on ipc.id = coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id)
   left join itemsolicitacao itemsol on itemsol.id = ipc.itemsolicitacaomaterial_id
   left join itemsolicitacaoexterno ise on ise.id = icise.itemsolicitacaoexterno_id
   inner join objetocompra oc on oc.id = coalesce(ic.objetocompracontrato_id, itemsol.objetocompra_id, ise.objetocompra_id, icot.objetocompra_id)
order by c.id
