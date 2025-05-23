update unidadeirp
set iniciovigencia = to_date('2023/02/24', 'yyyy/MM/dd')
where id = (select u.id from unidadeirp u
                                 inner join intencaoregistropreco i on u.intencaoregistropreco_id = i.id
            where i.numero = 890)
