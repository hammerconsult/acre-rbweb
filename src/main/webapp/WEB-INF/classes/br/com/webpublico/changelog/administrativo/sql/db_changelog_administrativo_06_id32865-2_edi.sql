merge into itemrequisicaodecompra it using (
select
id_item,
objeto,
unidade_medida,
especificacao
from(
select
irc.id as id_item,
coalesce(coalesce(objLic.id,objDisp.id,objSolExt.id, objCV.id), null) as objeto,
coalesce(itemSol.unidademedida_id, itemsol.unidademedida_id, itemSolCv.unidademedida_id, itemCot.unidademedida_id) as unidade_medida,
coalesce(to_char(itemsol.descricaocomplementar), to_char(itemsoldisp.descricaocomplementar), to_char(itemsolext.descricaocomplementar), to_char(itemcot.descricaocomplementar)) as especificacao
from execucaocontratoitem ex
inner join itemrequisicaodecompra irc on irc.execucaocontratoitem_id = ex.id
inner join itemcontrato ic on ic.id = ex.itemcontrato_id

left join itemcontratoitempropfornec prop on prop.itemcontrato_id = ic.id
left join itempropfornec ipf on ipf.id = prop.itempropostafornecedor_id
left join itemprocessodecompra ipc on ipc.id = ipf.itemprocessodecompra_id
left join itemsolicitacao itemSol on itemSol.id = ipc.itemsolicitacaomaterial_id
left join itemsolicitacaomaterial itemsolpro on itemsolpro.itemsolicitacao_id = itemSol.id
left join objetocompra objLic on objLic.id = itemsolpro.objetocompra_id

left join itemcontratoitempropdisp propostadispensa on propostadispensa.itemcontrato_id = ic.id
left join itempropostafornedisp itemPropDisp on itemPropDisp.id = propostadispensa.itempropfornecdispensa_id
left join itemprocessodecompra itemdisp on itemdisp.id = itemPropDisp.itemprocessodecompra_id
left join itemsolicitacao itemSolDisp on itemSolDisp.id = itemdisp.itemsolicitacaomaterial_id
left join itemsolicitacaomaterial itemsolmatdisp on itemsolmatdisp.itemsolicitacao_id = itemSolDisp.id
left join objetocompra objDisp on objDisp.id = itemsolmatdisp.objetocompra_id

left join itemcontratoitemsolext solicitacaoexterna on solicitacaoexterna.itemcontrato_id = ic.id
left join itemsolicitacaoexterno itemSolExt on itemSolExt.id = solicitacaoexterna.itemsolicitacaoexterno_id

left join itemprocessodecompra ipcCv on ipcCv.id = itemsolext.itemprocessocompra_id
left join itemsolicitacao itemSolCv on itemSolCv.id = ipcCv.itemsolicitacaomaterial_id
left join objetocompra objSolExt on objSolExt.id = itemSolExt.objetocompra_id

left join itemcontratovigente icv on icv.itemcontrato_id = ic.id
left join itemcotacao itemCot on itemCot.id = icv.itemcotacao_id
left join objetocompra objCV on objCV.id = itemCot.objetoCompra_id
)
) itx on (itx.id_item = it.id)
when matched then update set it.objetocompra_id = itx.objeto, it.unidademedida_id = unidade_medida, it.especificacaoobjetocompra = especificacao;
