update RECURSOSISTEMA
set CAMINHO = '/administrativo/contrato/aditivo-contrato/lista-aditivo.xhtml'
where CAMINHO = '/administrativo/contrato/aditivo-contrato/lista.xhtml';

update MENU
set CAMINHO = '/administrativo/contrato/aditivo-contrato/lista-aditivo.xhtml'
where CAMINHO = '/administrativo/contrato/aditivo-contrato/lista.xhtml';

insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
VALUES (HIBERNATE_SEQUENCE.nextval,
        'CONTRATOS > APOSTILAMENTO > APOSTILAMENTO DE CONTRATO',
        '/administrativo/contrato/aditivo-contrato/lista-apostilamento.xhtml',
        0,
        'CONTRATOS');

insert into GRUPORECURSOSISTEMA (RECURSOSISTEMA_ID, GRUPORECURSO_ID)
VALUES (HIBERNATE_SEQUENCE.currval, 898677895);

update MENU
set CAMINHO = '/administrativo/contrato/aditivo-contrato/lista-apostilamento.xhtml'
where CAMINHO = '/administrativo/contrato/apostilamento-contrato/lista.xhtml';

delete
from RECURSOSISTEMA
where CAMINHO in ('/administrativo/contrato/apostilamento-contrato/lista.xhtml',
                  '/administrativo/contrato/apostilamento-contrato/edita.xhtml',
                  '/administrativo/contrato/apostilamento-contrato/visualizar.xhtml');
