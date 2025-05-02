update enquadramentofiscal set REGIMETRIBUTARIO = 'SIMPLES_NACIONAL' where coalesce(SIMPLESNACIONAL,0) = 1;
update enquadramentofiscal set REGIMETRIBUTARIO = 'LUCRO_PRESUMIDO' where coalesce(SIMPLESNACIONAL,0) = 0;
