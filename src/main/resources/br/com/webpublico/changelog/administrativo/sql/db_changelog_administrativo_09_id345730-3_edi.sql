update RECURSOSISTEMA
set CAMINHO = '/administrativo/contrato/publicacao-aditivo/lista-aditivo.xhtml'
where CAMINHO = '/administrativo/contrato/publicacao-aditivo/lista.xhtml';

update MENU
set CAMINHO = '/administrativo/contrato/publicacao-aditivo/lista-aditivo.xhtml'
where CAMINHO = '/administrativo/contrato/publicacao-aditivo/lista.xhtml';

insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval,
        'CONTRATOS > APOSTILAMENTO > PUBLICAÇÃO DE APOSTILAMENTO > LISTA',
        '/administrativo/contrato/publicacao-aditivo/lista-apostilamento.xhtml',
        0,
        'CONTRATOS');

insert into GRUPORECURSOSISTEMA (RECURSOSISTEMA_ID, GRUPORECURSO_ID)
VALUES (HIBERNATE_SEQUENCE.currval, 898677895);

update MENU
set CAMINHO = '/administrativo/contrato/publicacao-aditivo/lista-apostilamento.xhtml'
where CAMINHO = '/administrativo/contrato/publicacao-apostilamento/lista.xhtml';

delete
from RECURSOSISTEMA
where CAMINHO like '%administrativo/contrato/publicacao-apostilamento/%';
