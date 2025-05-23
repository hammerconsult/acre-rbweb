INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > FISCALIZAÇÃO AMBIENTAL > LICENCIAMENTO AMBIENTAL > ASSUNTO > VISUALIZAR',
     '/tributario/licenciamentoambiental/assunto/visualizar.xhtml', 0, 'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > FISCALIZAÇÃO AMBIENTAL > LICENCIAMENTO AMBIENTAL > ASSUNTO > EDITA',
     '/tributario/licenciamentoambiental/assunto/edita.xhtml', 0, 'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > FISCALIZAÇÃO AMBIENTAL > LICENCIAMENTO AMBIENTAL > ASSUNTO > LISTA',
     '/tributario/licenciamentoambiental/assunto/lista.xhtml', 0, 'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'FISCALIZAÇÃO AMBIENTAL',
        null,
        (SELECT ID FROM menu WHERE LABEL = 'TRIBUTÁRIO'),
        (SELECT max(ORDEM) + 10 FROM menu WHERE PAI_ID = (SELECT ID FROM menu WHERE LABEL = 'TRIBUTÁRIO')));

insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'LICENCIAMENTO AMBIENTAL',
        null,
        (SELECT ID FROM menu WHERE LABEL = 'FISCALIZAÇÃO AMBIENTAL'),
        1);

insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'ASSUNTO',
        '/tributario/licenciamentoambiental/assunto/lista.xhtml',
        (SELECT ID FROM menu WHERE LABEL = 'LICENCIAMENTO AMBIENTAL'),
        1);
