update enderecocadastroeconomico ece
set areautilizacao = (select areautilizacao from cadastroeconomico cmc where ece.cadastroeconomico_id = cmc.id and ece.tipoendereco = 'COMERCIAL')
