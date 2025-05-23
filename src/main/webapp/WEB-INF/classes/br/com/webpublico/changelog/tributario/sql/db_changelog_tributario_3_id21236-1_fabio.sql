update enquadramentofiscal set TIPOISSQN = 'MEI' where REGIMEESPECIALTRIBUTACAO = 'MICROEMPRESARIO_INDIVIDUAL';
update enquadramentofiscal set TIPOISSQN = 'ISENTO' where isento = 1;
update enquadramentofiscal set TIPOISSQN = 'IMUNE' where imuneISS = 1;
