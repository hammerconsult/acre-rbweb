update CertidaoAtividadeBCE set situacao = 'ATIVO' where situacao = 'REATIVADO';
update CertidaoAtividadeBCE set situacao = 'ENCERRADO' where situacao = 'INATIVO';
