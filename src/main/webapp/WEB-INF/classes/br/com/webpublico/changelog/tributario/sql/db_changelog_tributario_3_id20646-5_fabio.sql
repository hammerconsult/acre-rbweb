update divida set permissaoEmissaoDAM = 'HABILITA';
update divida set permissaoEmissaoDAM = 'HABILITA_BLOQUEIA_EXERCICIOS_POSTERIORES' where coalesce(isparcelamento,0) = 1;
update divida set permissaoEmissaoDAM = 'BLOQUEIA' where id = 363412618;
