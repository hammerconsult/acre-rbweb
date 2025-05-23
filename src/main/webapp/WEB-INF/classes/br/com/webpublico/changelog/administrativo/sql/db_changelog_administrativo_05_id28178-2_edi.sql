update aditivocontrato ad set ad.unidadeadministrativa_id = (select c.orgao_id from contrato c where c.id = ad.contrato_id);
update apostilamentocontrato ap set ap.unidadeadministrativa_id = (select c.orgao_id from contrato c where c.id = ap.contrato_id)
