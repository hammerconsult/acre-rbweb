update ITEMVALORPENSIONISTA
set FINALVIGENCIA = to_date('31/12/2023', 'dd/MM/yyyy')
where id in (select itemPens1.id
             from ITEMVALORPENSIONISTA itemPens1
             where trunc(itemPens1.FINALVIGENCIA) = to_date('30/12/2023', 'dd/MM/yyyy')
               and itemPens1.PENSIONISTA_ID in (select itemPens.PENSIONISTA_ID
                                                from ITEMVALORPENSIONISTA itemPens
                                                where itemPens.REAJUSTERECEBIDO_ID in (select reaj.id
                                                                                       from reajustemediaaposentadoria reaj
                                                                                                inner join exercicio ex on reaj.EXERCICIO_ID = ex.ID
                                                                                       where ex.ANO = 2024)))
