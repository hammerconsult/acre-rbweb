INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'NFS-E > RPS > LISTA RPS',
     '/tributario/nfse/rps/listaRps.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'NFS-E > RPS > LISTA LOTE ROS',
     '/tributario/nfse/rps/listaLote.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

UPDATE MENU SET CAMINHO = '/tributario/nfse/rps/listaLote.xhtml', LABEL = 'LOTE RPS' WHERE CAMINHO = '/tributario/nfse/rps/listaRps.xhtml';

INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'RPS',
                         '/tributario/nfse/rps/listaRps.xhtml', (select ID from menu where LABEL = 'NOTA FISCAL'), 10);



