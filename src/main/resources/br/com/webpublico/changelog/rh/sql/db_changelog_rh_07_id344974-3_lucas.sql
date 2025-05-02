update INCIDENCIATRIBUTARIARPPS set DESCRICAO = 'Não é base de cálculo de contribuições devidas' where CODIGO = '00';
update INCIDENCIATRIBUTARIARPPS set DESCRICAO = 'Base de cálculo de contribuições devidas' where CODIGO = '11';
update INCIDENCIATRIBUTARIARPPS set DESCRICAO = 'Contribuição descontada do segurado ou beneficiário' where CODIGO = '31';
update INCIDENCIATRIBUTARIARPPS set DESCRICAO = 'Suspensão de incidência em decorrência de decisão judicial' where CODIGO = '91';
