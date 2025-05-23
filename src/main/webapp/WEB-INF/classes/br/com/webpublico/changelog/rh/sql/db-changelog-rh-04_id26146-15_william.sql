update TipoPrevidenciaFP set TipoRegimePrevidenciario = 'RPPS' where codigo <> 1;
update TipoPrevidenciaFP set TipoRegimePrevidenciario = 'RGPS' where codigo = 1;
