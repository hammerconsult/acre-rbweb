delete from CADASTROECONOMICO_SERVICO where SERVICO_ID in (Select id from SERVICO where ativo = 0)
