update ajustecontratodadoscad set numerotermo = '0' || numerotermo;

update CONTRATO set NUMEROTERMO = '0' || NUMEROTERMO
where length(NUMEROTERMO) = 7;



