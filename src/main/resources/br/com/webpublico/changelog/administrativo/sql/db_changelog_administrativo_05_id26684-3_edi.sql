update aditivocontrato ad set ad. datalancamento =  (select c.datalancamento from contrato c where c.id = ad.contrato_id);
update aditivocontrato ad set ad.dataaprovacao =  (select c.dataaprovacao from contrato c where c.id = ad.contrato_id);
update aditivocontrato ad set ad.datadeferimento =  (select c.datadeferimento from contrato c where c.id = ad.contrato_id);

update aditivocontrato adit set adit.numero = '00' || adit.numero
where length(adit.numero) > 1;

update aditivocontrato adit set adit.numero = '000' || adit.numero
where length(adit.numero) > 0 and length(adit.numero) <= 1
