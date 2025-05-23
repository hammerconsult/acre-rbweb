update solicitacaomaterial set tipostatussolicitacao = 'APROVADA' where tipostatussolicitacao = 'ACEITA';
update solicitacaomaterial set tipostatussolicitacao = 'REJEITADA' where tipostatussolicitacao = 'DEVOLVIDA';
update solicitacaomaterial set tipostatussolicitacao = 'NAO_INFORMADA' where tipostatussolicitacao is null;