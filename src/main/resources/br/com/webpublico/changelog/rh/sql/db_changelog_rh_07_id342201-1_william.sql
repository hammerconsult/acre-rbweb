update VINCULOFP
set CATEGORIATRABALHADOR_ID = 827394564
where id in (select vinculo.id
             from vinculofp vinculo
                      inner join contratofp contrato on vinculo.ID = contrato.ID
             where contrato.MODALIDADECONTRATOFP_ID = 6190097
               and sysdate between vinculo.INICIOVIGENCIA and coalesce(vinculo.FINALVIGENCIA, sysdate)
               and vinculo.CATEGORIATRABALHADOR_ID is null)
