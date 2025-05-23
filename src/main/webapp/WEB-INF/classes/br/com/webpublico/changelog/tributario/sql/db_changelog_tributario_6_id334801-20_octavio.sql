delete
from alteracaocmcendereco
where alteracaocadastroeconomico_id in (select al.id
                                        from alteracaocadastroeconomico al
                                        inner join cadastroeconomico cmc on al.cadastroeconomico_id = cmc.id
                                        where cmc.inscricaocadastral = '1313614');

delete
from alteracaocmccnae
where alteracaocadastroeconomico_id in (select al.id
                                        from alteracaocadastroeconomico al
                                        inner join cadastroeconomico cmc on al.cadastroeconomico_id = cmc.id
                                        where cmc.inscricaocadastral = '1313614');


delete alteracaocadastroeconomico
where cadastroeconomico_id = (select id
                              from cadastroeconomico cmc
                              where cmc.inscricaocadastral = '1313614' and rownum = 1);
