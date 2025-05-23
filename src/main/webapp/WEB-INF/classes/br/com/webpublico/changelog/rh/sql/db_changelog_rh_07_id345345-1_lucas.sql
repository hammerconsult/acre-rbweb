update ITEMAPOSENTADORIA
set FINALVIGENCIA = to_date('31/12/2023', 'dd/MM/yyyy')
where id in (select itemApo1.id
             from ITEMAPOSENTADORIA itemApo1
             where trunc(itemApo1.FINALVIGENCIA) = to_date('30/12/2023', 'dd/MM/yyyy')
               and itemApo1.APOSENTADORIA_ID in (select itemApo.APOSENTADORIA_ID
                                                 from ITEMAPOSENTADORIA itemApo
                                                 where itemApo.REAJUSTERECEBIDO_ID in (select reaj.id
                                                                                       from reajustemediaaposentadoria reaj
                                                                                                inner join exercicio ex on reaj.EXERCICIO_ID = ex.ID
                                                                                       where ex.ANO = 2024)))
