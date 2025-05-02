update ITEMAPOSENTADORIA
set INICIOVIGENCIA = to_date('01/01/2024', 'dd/MM/yyyy')
where id in (select itemApo.id
             from ITEMAPOSENTADORIA itemApo
             where itemApo.REAJUSTERECEBIDO_ID in (select reaj.id
                                                   from reajustemediaaposentadoria reaj
                                                            inner join exercicio ex on reaj.EXERCICIO_ID = ex.ID
                                                   where ex.ANO = 2024))
