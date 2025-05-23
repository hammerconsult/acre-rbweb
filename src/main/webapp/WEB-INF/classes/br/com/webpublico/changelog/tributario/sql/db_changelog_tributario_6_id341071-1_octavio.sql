delete from credencialtransporte
where id in (select ct.id from credencialtransporte ct
             inner join credencialrbtrans cr on ct.id = cr.id
             inner join permissaotransporte pt on cr.permissaotransporte_id = pt.id
             where ct.cadastroeconomico_id = 810595342 and extract(year from cr.datavalidade) = 2023
             and cr.numero in (655, 656, 657, 658))
