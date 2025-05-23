update InfracaoFiscalizacao set descricao_temp = descricao ;
update InfracaoFiscalizacao_aud set descricao_temp = descricao;
update InfracaoFiscalizacao set descricao = null;
update InfracaoFiscalizacao_aud set descricao = null;