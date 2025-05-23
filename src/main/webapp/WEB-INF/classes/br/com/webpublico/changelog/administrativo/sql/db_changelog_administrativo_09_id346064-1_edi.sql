update execucaocontrato ex
set ex.origem         = (select item.origemexecucaocontrato
                         from execucaocontratoitem item
                         where item.execucaocontrato_id = ex.id
                             fetch first 1 row only),
    ex.idorigem       = (select item.idmovimentoorigem
                         from execucaocontratoitem item
                         where item.execucaocontrato_id = ex.id
                             fetch first 1 row only),
    ex.operacaoorigem = (select case
                                    when item.tipomovimentoorigem = 'VALOR_EXECUCAO_POS_EXECUCAO' then 'POS_EXECUCAO'
                                    else 'PRE_EXECUCAO' end
                         from execucaocontratoitem item
                         where item.execucaocontrato_id = ex.id
                             fetch first 1 row only)
where ex.origem is null;

update execucaocontrato set origem = 'CONTRATO',
                            idorigem = contrato_id,
                            operacaoorigem = 'PRE_EXECUCAO'
where origem is null or idorigem is null or operacaoorigem is null;
