create or replace force view vwcontratoadequado (contrato_id, item_id, item_numero, item_descricao)  as
select c.id contrato_id,
       ic.id item_id,
       ipc.numero item_numero, 
       coalesce(obj.descricao, coalesce(lp.descricao, coalesce(sc.descricao, obj_ise.descricao))) item_descricao
   from contrato c
  inner join itemcontrato ic on ic.contrato_id = c.id
  left join itemcontratoitempropfornec icpf on ic.id = icpf.itemcontrato_id
  left join itempropfornec ipf on icpf.itempropostafornecedor_id = ipf.id
  left join itemcontratoitempropdisp icpd on ic.id = icpd.itemcontrato_id
  left join itempropostafornedisp ipfd on icpd.itempropfornecdispensa_id = ipfd.id
  left join itemprocessodecompra ipc on coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) = ipc.id
  left join itemsolicitacao its on ipc.itemsolicitacaomaterial_id = its.id
  left join itemsolicitacaolista itsl on itsl.itemsolicitacao_id = its.id
  left join listadepreco lp on itsl.listadepreco_id = lp.id
  left join itemsolicitacaoservico itss on itss.itemsolicitacao_id = its.id
  left join servicocompravel sc on itss.servicocompravel_id = sc.id
  left join itemsolicitacaomaterial itsm on itsm.itemsolicitacao_id = its.id
  left join objetocompra obj on itsm.objetocompra_id = obj.id  
  left join itemcontratoitemsolext icse on ic.id = icse.itemcontrato_id
  left join itemsolicitacaoexterno ise on icse.itemsolicitacaoexterno_id = ise.id
  left join objetocompra obj_ise on ise.objetocompra_id = obj_ise.id
order by c.id