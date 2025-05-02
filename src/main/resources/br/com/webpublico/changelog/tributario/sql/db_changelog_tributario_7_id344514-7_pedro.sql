insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'TRIBUTÁRIO > LICENCIAMENTO AMBIENTAL > PARÂMETRO LICENCIAMENTO AMBIENTAL > LISTA',
        '/tributario/licenciamentoambiental/parametro/lista.xhtml', 1, 'TRIBUTARIO');


insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval,
        'TRIBUTÁRIO > LICENCIAMENTO AMBIENTAL > PARÂMETRO LICENCIAMENTO AMBIENTAL > VISUALIZA',
        '/tributario/licenciamentoambiental/parametro/visualizar.xhtml', 1, 'TRIBUTARIO');

insert into recursosistema (id, nome, caminho, cadastro, modulo)
values (hibernate_sequence.nextval, 'TRIBUTÁRIO > LICENCIAMENTO AMBIENTAL > PARÂMETRO LICENCIAMENTO AMBIENTAL > EDITA',
        '/tributario/licenciamentoambiental/parametro/edita.xhtml', 1, 'TRIBUTARIO');

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
VALUES ((SELECT ID FROM GRUPORECURSO WHERE NOME = 'ADMINISTRADOR TRIBUTÁRIO'),
        (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/licenciamentoambiental/parametro/lista.xhtml'));

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
VALUES ((SELECT ID FROM GRUPORECURSO WHERE NOME = 'ADMINISTRADOR TRIBUTÁRIO'),
        (SELECT ID
         FROM RECURSOSISTEMA
         WHERE CAMINHO = '/tributario/licenciamentoambiental/parametro/visualizar.xhtml'));

insert into gruporecursosistema (gruporecurso_id, recursosistema_id)
VALUES ((SELECT ID FROM GRUPORECURSO WHERE NOME = 'ADMINISTRADOR TRIBUTÁRIO'),
        (SELECT ID FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/licenciamentoambiental/parametro/edita.xhtml'));

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval, 'PARÂMETRO', '/tributario/licenciamentoambiental/parametro/lista.xhtml',
        (SELECT ID FROM MENU WHERE LABEL = 'LICENCIAMENTO AMBIENTAL'), 30, null);
