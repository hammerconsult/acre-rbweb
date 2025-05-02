update contratoceasa set situacaocontrato = 'ENCERRADO' where extract(year from datainicio) <= 2020 and situacaocontrato = 'ATIVO';
update contratoceasa set situacaocontrato = 'RENOVADO' where extract(year from datainicio) = 2021;
