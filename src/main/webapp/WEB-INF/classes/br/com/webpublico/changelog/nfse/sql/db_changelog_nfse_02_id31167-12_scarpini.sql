INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NFSE > RPS > LISTA XML',
     '/tributario/nfse/rps/listaXml.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, (select id from GRUPORECURSO where nome = 'Nfse'));
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));


INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NFSE > MENSAGEM > LISTA',
     '/tributario/nfse/mensagem-contribuinte/lista.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, (select id from GRUPORECURSO where nome = 'Nfse'));
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NFSE > MENSAGEM > EDITA',
     '/tributario/nfse/mensagem-contribuinte/edita.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, (select id from GRUPORECURSO where nome = 'Nfse'));
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NFSE > MENSAGEM > VISUALIZA',
     '/tributario/nfse/mensagem-contribuinte/visualizar.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, (select id from GRUPORECURSO where nome = 'Nfse'));
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));


INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'XML ENVIADOS',
                         '/tributario/nfse/rps/listaXml.xhtml', (select ID from menu where LABEL = 'NFS-E'), 65);


INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'MENSAGEM AOS CONTRIBUINTES',
                         '/tributario/nfse/mensagem-contribuinte/lista.xhtml', (select ID from menu where LABEL = 'ISS-ONLINE'), 30);
