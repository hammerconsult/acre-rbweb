update situacaocadastroeconomico set situacaocadastral = 'ATIVA_REGULAR' where situacaocadastral = 'ATIVO';
update situacaocadastroeconomico set situacaocadastral = 'BAIXADA' where situacaocadastral = 'BAIXADO';
update situacaocadastroeconomico set situacaocadastral = 'SUSPENSA' where situacaocadastral = 'SUSPENSO';
update situacaocadastroeconomico set situacaocadastral = 'NULA' where situacaocadastral = 'NAO_IDENTIFICADO';
update situacaocadastroeconomico set situacaocadastral = 'ATIVA_REGULAR' where situacaocadastral = 'REATIVO';

update categoriadividapublica set ativoinativo = 'ATIVA_REGULAR' where ativoinativo = 'ATIVO';
update categoriadividapublica set ativoinativo = 'BAIXADA' where ativoinativo = 'BAIXADO';
update categoriadividapublica set ativoinativo = 'SUSPENSA' where ativoinativo = 'SUSPENSO';
update categoriadividapublica set ativoinativo = 'NULA' where ativoinativo = 'NAO_IDENTIFICADO';
update categoriadividapublica set ativoinativo = 'ATIVA_REGULAR' where ativoinativo = 'REATIVO';

update historicoinscricaocadastral set situacaocadastral = 'ATIVA_REGULAR' where situacaocadastral = 'ATIVO';
update historicoinscricaocadastral set situacaocadastral = 'BAIXADA' where situacaocadastral = 'BAIXADO';
update historicoinscricaocadastral set situacaocadastral = 'SUSPENSA' where situacaocadastral = 'SUSPENSO';
update historicoinscricaocadastral set situacaocadastral = 'NULA' where situacaocadastral = 'NAO_IDENTIFICADO';
update historicoinscricaocadastral set situacaocadastral = 'ATIVA_REGULAR' where situacaocadastral = 'REATIVO';
