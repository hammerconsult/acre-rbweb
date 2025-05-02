update telefone set principal = 0 where principal is null;
commit;

update enderecocorreio set principal = 0 where principal is null;
commit;

update contacorrentebancpessoa set principal = 0 where principal is null;
commit;