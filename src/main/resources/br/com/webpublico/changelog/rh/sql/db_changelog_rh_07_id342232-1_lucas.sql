update VINCULOFP v
set v.CATEGORIATRABALHADOR_ID = 827394568
where v.id in (select vinculo.id
               from vinculofp vinculo
                        inner join contratofp contrato on vinculo.ID = contrato.ID
                        inner join categoriatrabalhador categoria on vinculo.CATEGORIATRABALHADOR_ID = categoria.ID
               where  sysdate between vinculo.INICIOVIGENCIA and coalesce(vinculo.FINALVIGENCIA, sysdate)
                 and categoria.CODIGO = 106)
