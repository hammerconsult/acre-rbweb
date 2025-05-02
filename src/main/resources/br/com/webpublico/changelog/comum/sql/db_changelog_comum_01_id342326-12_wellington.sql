insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'COMUM > FORMULÁRIO > LISTA',
        '/comum/formulario/lista.xhtml', 0, 'CADASTROS');
insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'COMUM > FORMULÁRIO > EDITA',
        '/comum/formulario/edita.xhtml', 1, 'CADASTROS');
insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'COMUM > FORMULÁRIO > VISUALIZAR',
        '/comum/formulario/visualizar.xhtml', 0, 'CADASTROS');
insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'FORMULÁRIO',
        '/comum/formulario/lista.xhtml',
        (select id from menu where label = 'CADASTROS GERAIS'), 0);

