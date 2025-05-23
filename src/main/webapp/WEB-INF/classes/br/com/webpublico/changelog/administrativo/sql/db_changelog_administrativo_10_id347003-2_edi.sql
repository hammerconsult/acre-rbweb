merge into itemrequisicaodecompra itemReq
    using (select irc.id     as id_item_req,
                  coalesce(ipc.numero, ise.numero, icot.numero) as numero_item
           from itemrequisicaodecompra irc
                    left join execucaoprocessoitem exitem on exitem.id = irc.execucaoprocessoitem_id
                    left join itemcontrato ic on ic.id = irc.itemcontrato_id
                    left join itemcontratovigente icv on icv.itemcontrato_id = ic.id
                    left join itemcotacao icot on icot.id = icv.itemcotacao_id
                    left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id
                    left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id
                    left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id
                    left join itemcontratoitemsolext icise on icise.itemcontrato_id = ic.id
                    left join itemcontratoitempropdisp icipfd on icipfd.itemcontrato_id = ic.id
                    left join itempropostafornedisp ipfd on icipfd.itempropfornecdispensa_id = ipfd.id
                    left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id
                    left join itemprocessodecompra ipc on ipc.id = coalesce(exitem.itemprocessocompra_id, ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id)
                    left join itemsolicitacaoexterno ise on ise.id = icise.itemsolicitacaoexterno_id
    ) dados on  (dados.id_item_req = itemReq.id)
    when matched then update set itemreq.numeroitemprocesso = dados.numero_item;
