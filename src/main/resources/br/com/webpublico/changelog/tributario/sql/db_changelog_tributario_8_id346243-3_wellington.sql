update servico
set anexolei1232006_id            =
            (select id from anexolei1232006 where descricao = 'Anexo III'),
    permitealteraranexolei1232006 = 1;

update servico
set anexolei1232006_id            =
            (select id from anexolei1232006 where descricao = 'Anexo IV'),
    permitealteraranexolei1232006 = 0
where codigo in ('702', '1102', '1714');

update servico
set anexolei1232006_id            =
            (select id from anexolei1232006 where descricao = 'Anexo V'),
    permitealteraranexolei1232006 = 0
where codigo in ('501', '3301', '1009', '1709', '1713', '1716', '1717', '1718', '3501',
                 '902', '1001', '1002', '1003', '1004', '1005', '1006', '1007', '1008');

