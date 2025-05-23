update cnae set situacao = 'INATIVO' where meioambiente is null;
update cnae set situacao = 'ATIVO' where meioambiente is not null and situacao is null;
