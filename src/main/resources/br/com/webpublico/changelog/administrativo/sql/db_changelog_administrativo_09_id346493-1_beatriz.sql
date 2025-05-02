delete from MENU where label = 'PLANO ANUAL DE CONTRATAÇÕES - PAC';

update RECURSOSISTEMA
set CAMINHO = '/administrativo/licitacao/pca/lista.xhtml' , NOME = 'LICITAÇÃO > COMPRAS E LICITAÇÕES > PLANO DE CONTRATAÇÕES ANUAL > LISTAR'
where CAMINHO = '/administrativo/licitacao/pac/lista.xhtml';

update RECURSOSISTEMA
set CAMINHO = '/administrativo/licitacao/pca/edita.xhtml', NOME = 'LICITAÇÃO > COMPRAS E LICITAÇÕES > PLANO DE CONTRATAÇÕES ANUAL > EDITAR'
where CAMINHO = '/administrativo/licitacao/pac/edita.xhtml';

update RECURSOSISTEMA
set CAMINHO = '/administrativo/licitacao/pca/visualizar.xhtml', NOME = 'LICITAÇÃO > COMPRAS E LICITAÇÕES > PLANO DE CONTRATAÇÕES ANUAL > VISUALIZAR'
where CAMINHO = '/administrativo/licitacao/pac/visualizar.xhtml';

INSERT INTO MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'PLANO DE CONTRATAÇÕES ANUAL - PCA',
        '/administrativo/licitacao/pca/lista.xhtml',
        (select ID from menu where LABEL = 'COMPRAS E LICITAÇÕES'),
        (select max(ORDEM) + 2 from menu where LABEL = 'CADASTROS GERAIS - LICITAÇÃO'));


