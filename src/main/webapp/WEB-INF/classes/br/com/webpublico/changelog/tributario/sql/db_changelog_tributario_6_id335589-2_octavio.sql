update veiculotransporte set placa = 'QWO7J81'
where id = 797231152;

update veiculopermissionario set finalvigencia = null,
                                 iniciovigencia = to_timestamp('2021-02-04 08:28:47.678', 'yyyy-mm-dd hh24:mi:ss.ff6')
where veiculotransporte_id = 797231152;
