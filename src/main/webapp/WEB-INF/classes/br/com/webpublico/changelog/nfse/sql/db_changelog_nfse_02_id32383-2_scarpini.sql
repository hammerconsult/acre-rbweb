INSERT INTO RECURSOSISTEMA VALUES
    (HIBERNATE_SEQUENCE.nextval, 'TRIBUTÁRIO > NFSE > LOGS > IMPRESSAO',
     '/tributario/nfse/nfse/listaLogImpressao.xhtml', 0, 'TRIBUTARIO');

INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, (select id from GRUPORECURSO where nome = 'Nfse'));
INSERT INTO GRUPORECURSOSISTEMA VALUES (HIBERNATE_SEQUENCE.currval, (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

INSERT INTO menu VALUES (HIBERNATE_SEQUENCE.nextval, 'LOG IMPRESSÃO NFSE',
                         '/tributario/nfse/nfse/listaLogImpressao.xhtml', (select ID from menu where LABEL = 'NOTA FISCAL'), 80);
