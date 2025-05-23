UPDATE parcelavalordivida parcela
SET valor = (
  SELECT situacao.saldo from situacaoparcelavalordivida situacao where situacao.parcela_id = parcela.id 
and situacao.id = (select max(sit.id) from situacaoparcelavalordivida sit where sit.parcela_id = parcela.id)
);