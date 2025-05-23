INSERT INTO menu
VALUES (hibernate_sequence.nextval(),
        'CANCELAMENTOS',
        '/tributario/nfse/cancelamento/lista.xhtml',
        (select ID from menu where LABEL = 'NFS-E'),
        20);



