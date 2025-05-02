update divida set permissaoEmissaoDAM = 'HABILITA_BLOQUEIA_EXERCICIOS_POSTERIORES' where permissaoEmissaoDAM is null;
update divida set permissaoEmissaoDAM = 'BLOQUEIA' where id = 363412618;
