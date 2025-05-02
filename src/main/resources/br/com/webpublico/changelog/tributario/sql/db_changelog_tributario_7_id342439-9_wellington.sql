insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'TRIBUTÁRIO > CADASTRO MUNICIPAL > LICENCIAMENTO DE ETR > PARÂMETRO DE ETR',
        '/tributario/cadastromunicipal/licenciamentoetr/parametroetr/edita.xhtml', 1, 'TRIBUTARIO');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval,
        'TRIBUTÁRIO > CADASTRO MUNICIPAL > LICENCIAMENTO DE ETR > REQUERIMENTO DE LICENCIAMENTO DE ETR',
        '/tributario/cadastromunicipal/licenciamentoetr/requerimentolicenciamentoetr/lista.xhtml', 1, 'TRIBUTARIO');
insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval,
        'TRIBUTÁRIO > CADASTRO MUNICIPAL > LICENCIAMENTO DE ETR > REQUERIMENTO DE LICENCIAMENTO DE ETR > VISUALIZAR',
        '/tributario/cadastromunicipal/licenciamentoetr/requerimentolicenciamentoetr/visualizar.xhtml', 1,
        'TRIBUTARIO');


insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'LICENCIAMENTO DE ETR',
        null,
        (select id from menu where label = 'CADASTRO TÉCNICO MOBILIÁRIO - C.M.C.'), 200);
insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'PARÂMETRO DE ETR',
        '/tributario/cadastromunicipal/licenciamentoetr/parametroetr/edita.xhtml',
        (select id from menu where label = 'LICENCIAMENTO DE ETR'), 1);
insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'REQUERIMENTO DE LICENCIAMENTO DE ETR',
        '/tributario/cadastromunicipal/licenciamentoetr/requerimentolicenciamentoetr/lista.xhtml',
        (select id from menu where label = 'LICENCIAMENTO DE ETR'), 50);
