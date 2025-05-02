update contareceita set descricaocodreduzido = null;
update contareceita_aud set descricaocodreduzido = null;
alter table contareceita rename column descricaocodreduzido to descricaoreduzida;
alter table contareceita_aud rename column descricaocodreduzido to descricaoreduzida;
commit;