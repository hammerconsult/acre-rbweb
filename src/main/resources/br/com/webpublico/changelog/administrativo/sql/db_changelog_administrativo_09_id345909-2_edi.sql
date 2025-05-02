update execucaocontratoitem item
set item.tipomovimentoorigem = 'VALOR_EXECUCAO_POS_EXECUCAO'
where item.execucaocontrato_id in (select ex.id
                                   from execucaocontrato ex
                                            inner join execucaocontratoempenho exemp on exemp.execucaocontrato_id = ex.id
                                            inner join execucaocontratoitem exitem on exitem.execucaocontrato_id = ex.id
                                   where ex.id = item.execucaocontrato_id
                                     and exemp.empenhoreajuste = 1);

update execucaocontratoitem item
set item.tipomovimentoorigem = 'VALOR_EXECUCAO_PRE_EXECUCAO'
where item.tipomovimentoorigem is null;
