INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'MATERIAIS > ALIMENTAÇÃO ESCOLAR > CARDÁPIO - REQUISIÇÃO DE COMPRA>  EDITAR',
        '/administrativo/materiais/alimentacao-escolar/cardapio-requisicao/edita.xhtml', 0, 'MATERIAIS');

INSERT INTO GRUPORECURSOSISTEMA (RECURSOSISTEMA_ID, GRUPORECURSO_ID)
VALUES (HIBERNATE_SEQUENCE.currval, 75756874);

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'MATERIAIS > ALIMENTAÇÃO ESCOLAR > CARDÁPIO - REQUISIÇÃO DE COMPRA > VISUALIZAR',
        '/administrativo/materiais/alimentacao-escolar/cardapio-requisicao/visualizar.xhtml', 0, 'MATERIAIS');

INSERT INTO GRUPORECURSOSISTEMA (RECURSOSISTEMA_ID, GRUPORECURSO_ID)
VALUES (HIBERNATE_SEQUENCE.currval, 75756874);
;

INSERT INTO RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL, 'MATERIAIS > ALIMENTAÇÃO ESCOLAR > CARDÁPIO - REQUISIÇÃO DE COMPRA > LISTAR',
        '/administrativo/materiais/alimentacao-escolar/cardapio-requisicao/lista.xhtml', 0, 'MATERIAIS');

INSERT INTO GRUPORECURSOSISTEMA (RECURSOSISTEMA_ID, GRUPORECURSO_ID)
VALUES (HIBERNATE_SEQUENCE.currval, 75756874);

INSERT INTO menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval, 'CARDÁPIO - REQUISIÇÃO DE COMPRA',
        '/administrativo/materiais/alimentacao-escolar/cardapio-requisicao/lista.xhtml',
        (select ID from menu where LABEL = 'ALIMENTAÇÃO ESCOLAR'), 30, null);
