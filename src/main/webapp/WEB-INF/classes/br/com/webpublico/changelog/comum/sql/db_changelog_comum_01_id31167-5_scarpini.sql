INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'CONFIGURAÇÕES > EMAIL > LISTA',
     '/admin/configuracaoemail/lista.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'CONFIGURAÇÕES > EMAIL > VISUALIZAR',
     '/admin/configuracaoemail/visualizar.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'CONFIGURAÇÕES > EMAIL >  LISTA',
     '/admin/configuracaoemail/lista.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'CONFIGURAÇÕES DE EMAIL',
                         '/admin/configuracaoemail/lista.xhtml', (select ID from menu where LABEL = 'CONFIGURAÇÕES'), 75);

INSERT INTO configuracaoemail (id, host, port, username, email, password, protocol, tls)
values (HIBERNATE_SEQUENCE.nextval,
        'smtp.gmail.com',
        '587',
        'suporte@webpublico.com.br',
        'suporte@webpublico.com.br',
        'suportemga',
        'smtp', 1);

