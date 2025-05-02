update aditivocontrato set finalidade = 'ACRESCIMO'
where finalidade = 'ADICAO';
update apostilamentocontrato a set a.datalancamento = (select c.datalancamento from contrato c where a.contrato_id = c.id);
update apostilamentocontrato a set a.dataaprovacao = (select c.dataaprovacao from contrato c where a.contrato_id = c.id);
update apostilamentocontrato a set a.datadeferimento = (select c.datadeferimento from contrato c where a.contrato_id = c.id);
update apostilamentocontrato a set a.dataassinatura = (select c.dataassinatura from contrato c where a.contrato_id = c.id);
update apostilamentocontrato set situacao = 'DEFERIDO';
update apostilamentocontrato set numerotermo = numero;
update apostilamentocontrato adit set adit.numerotermo = '00' || adit.numerotermo
where length(adit.numerotermo) > 1;
update apostilamentocontrato adit set adit.numerotermo = '000' || adit.numerotermo
where length(adit.numerotermo) > 0 and length(adit.numerotermo) <= 1

