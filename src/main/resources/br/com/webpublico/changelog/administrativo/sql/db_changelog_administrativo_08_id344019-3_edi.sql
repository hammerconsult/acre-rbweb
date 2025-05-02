update entradamaterial
set situacao = 'ATESTO_PROVISORIO_AGUARDANDO_LIQUIDACAO'
where id in (select ecm.id
             from doctofiscalentradacompra docEnt
                      inner join entradacompramaterial ecm on ecm.id = docEnt.ENTRADACOMPRAMATERIAL_ID
             where docEnt.SITUACAO in ('AGUARDANDO_LIQUIDACAO', 'LIQUIDADO_PARCIALMENTE'));


update entradamaterial
set situacao = 'ATESTO_DEFINITIVO_LIQUIDADO'
where id not in (select ecm.id
                 from doctofiscalentradacompra docEnt
                          inner join entradacompramaterial ecm on ecm.id = docEnt.ENTRADACOMPRAMATERIAL_ID
                 where docEnt.SITUACAO in ('AGUARDANDO_LIQUIDACAO', 'LIQUIDADO_PARCIALMENTE'));
