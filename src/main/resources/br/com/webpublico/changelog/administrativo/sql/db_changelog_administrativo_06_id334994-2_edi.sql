update ITEMREQUISICAODECOMPRA set itemcontrato_id = (select iex.itemcontrato_id from execucaocontratoitem iex where EXECUCAOCONTRATOITEM_ID = iex.id);
