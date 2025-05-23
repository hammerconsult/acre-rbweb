update subpaginamenuhorizontal set titulo = 'Servidores' where titulo = 'Aposentados e Pensionistas' and ordem = 1;
update subpaginamenuhorizontal set titulo = 'Aposentados e Pensionistas', chave = 'servidor-aposentado-pensionista' where titulo = 'Relação Completa de Empregados Terceirizados' and ordem = 2;
update subpaginamenuhorizontal set titulo = 'Relação Completa de Empregados Terceirizados', chave = '#' where titulo = 'Lista de Estagiários' and ordem = 3;
update subpaginamenuhorizontal set chave = 'concurso-publico' where titulo = 'Concursos Públicos' and ordem = 5;
