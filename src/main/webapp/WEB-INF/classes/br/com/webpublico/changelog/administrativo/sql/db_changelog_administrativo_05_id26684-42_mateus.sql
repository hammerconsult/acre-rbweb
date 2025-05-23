update aditivocontrato set NUMERO_TEMP = CAST(NUMERO AS VARCHAR(20));
update aditivocontrato_aud set NUMERO_TEMP = CAST(NUMERO AS VARCHAR(20));

update aditivocontrato set numero = NULL;
update aditivocontrato_aud set numero = NULL;
