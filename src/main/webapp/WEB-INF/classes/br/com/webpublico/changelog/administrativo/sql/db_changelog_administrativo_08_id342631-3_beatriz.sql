update SOLICITACAOMATERIAL set amparolegal_id = (select id from amparolegal where codigo = 0);
