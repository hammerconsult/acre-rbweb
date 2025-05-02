delete from MENU where label = 'CBO';

update RECURSOSISTEMA
set CAMINHO = '/rh/cbo/lista.xhtml' , NOME = 'RECURSOS HUMANOS > CBO > LISTA'
where CAMINHO = '/tributario/cadastromunicipal/cbo/lista.xhtml';

update RECURSOSISTEMA
set CAMINHO = '/rh/cbo/edita.xhtml', NOME = 'RECURSOS HUMANOS > CBO > EDITA'
where CAMINHO = '/tributario/cadastromunicipal/cbo/edita.xhtml';

update RECURSOSISTEMA
set CAMINHO = '/rh/cbo/visualizar.xhtml', NOME = 'RECURSOS HUMANOS > CBO > VISUALIZA'
where CAMINHO = '/tributario/cadastromunicipal/cbo/visualizar.xhtml';

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'CBO',
        '/rh/cbo/lista.xhtml',
        (SELECT ID FROM MENU WHERE LABEL = 'RECURSOS HUMANOS' AND CAMINHO IS NULL), 210);
