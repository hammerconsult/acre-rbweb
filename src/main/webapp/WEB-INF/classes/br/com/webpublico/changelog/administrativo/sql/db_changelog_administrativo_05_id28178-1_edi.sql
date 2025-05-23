update aditivocontrato set numeroaditivo = numerotermo;
update apostilamentocontrato set numeroapostilamento = numerotermo;
update apostilamentocontrato ap set ap.exercicio_id = (select c.exerciciocontrato_id from contrato c where c.id = ap.contrato_id);
update aditivocontrato ad set ad.exercicio_id = (select c.exerciciocontrato_id from contrato c where c.id = ad.contrato_id)
