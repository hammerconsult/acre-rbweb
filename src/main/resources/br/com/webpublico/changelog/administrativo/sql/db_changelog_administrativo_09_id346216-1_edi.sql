update execucaocontrato set reprocessada = 1
where contrato_id in (select distinct c.id
                      from contrato c
                      where not exists (select 1 from alteracaocontratual ec where ec.contrato_id = c.id)
                        and exists (select 1 from execucaocontrato ec where ec.contrato_id = c.id));
