update itemdoctoitemaquisicao item set item.garantia_id =
(select itemreq.garantia_id from itemrequisicaodecompra itemReq where itemreq.id = item.itemrequisicaodecompra_id)
where item.garantia_id is null;

update itemdoctoitemaquisicao item set item.seguradora_id =
(select itemreq.seguradora_id from itemrequisicaodecompra itemReq where itemreq.id = item.itemrequisicaodecompra_id)
where item.seguradora_id is null
