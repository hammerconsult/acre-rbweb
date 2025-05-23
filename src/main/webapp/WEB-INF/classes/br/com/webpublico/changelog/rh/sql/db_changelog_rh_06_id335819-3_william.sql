update FUNCAOGRATIFICADA set tipoFuncaoGratificada = 'COORDENACAO' where id in (
select funcao.id from CONTRATOFP contrato
                          inner join MODALIDADECONTRATOFP modalidade on contrato.MODALIDADECONTRATOFP_ID = modalidade.ID
                          inner join FUNCAOGRATIFICADA funcao on contrato.ID = funcao.CONTRATOFP_ID
where modalidade.codigo = 9);

update FUNCAOGRATIFICADA set tipoFuncaoGratificada = 'NORMAL' where id in (
    select funcao.id from CONTRATOFP contrato
                              inner join MODALIDADECONTRATOFP modalidade on contrato.MODALIDADECONTRATOFP_ID = modalidade.ID
                              inner join FUNCAOGRATIFICADA funcao on contrato.ID = funcao.CONTRATOFP_ID
    where modalidade.codigo <> 9);
