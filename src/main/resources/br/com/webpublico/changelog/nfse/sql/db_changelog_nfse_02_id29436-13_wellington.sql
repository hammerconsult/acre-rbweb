INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NFSE > RPS > LISTA',
     '/tributario/nfse/rps/lista.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NFSE > RPS > VISUALIZAR',
     '/tributario/nfse/rps/visualizar.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NFSE > SOLICITAÇÃO DE RPS > LISTA',
     '/tributario/nfse/solicitacao-rps/lista.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NFSE > SOLICITAÇÃO DE RPS > VISUALIZAR',
     '/tributario/nfse/solicitacao-rps/visualizar.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NFSE > DASHBOARD PRESTADOR',
     '/tributario/nfse/dashboard-prestador/edita.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'SOLICITAÇÃO DE RPS',
                         '/tributario/nfse/solicitacao-rps/lista.xhtml', (select ID from menu where LABEL = 'NFS-E'), 65);

INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'RPS',
                         '/tributario/nfse/rps/lista.xhtml', (select ID from menu where LABEL = 'NFS-E'), 70);

INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'DASHBOARD PRESTADOR',
                         '/tributario/nfse/dashboard-prestador/edita.xhtml', (select ID from menu where LABEL = 'NFS-E'), 75);
