update RECURSOSISTEMA
set CAMINHO = '/administrativo/licitacao/execucao-sem-contrato/edita.xhtml',
    NOME = 'COMPRAS E LICITAÇÕES > EXECUÇÃO SEM CONTRATO > EDITA'
where CAMINHO = '/administrativo/licitacao/execucao-ata/edita.xhtml';

update RECURSOSISTEMA
set CAMINHO = '/administrativo/licitacao/execucao-sem-contrato/visualizar.xhtml',
    NOME = 'COMPRAS E LICITAÇÕES > EXECUÇÃO SEM CONTRATO > VISUALIZA'
where CAMINHO = '/administrativo/licitacao/execucao-ata/visualizar.xhtml';

update RECURSOSISTEMA
set CAMINHO = '/administrativo/licitacao/execucao-sem-contrato/lista.xhtml',
    NOME = 'COMPRAS E LICITAÇÕES > EXECUÇÃO SEM CONTRATO > LISTA'
where CAMINHO = '/administrativo/licitacao/execucao-ata/lista.xhtml';

delete
from menu
where CAMINHO = '/administrativo/licitacao/execucao-ata/lista.xhtml';

update MENU
set CAMINHO = '/administrativo/licitacao/execucao-sem-contrato/lista.xhtml'
where LABEL = 'EXECUÇÃO SEM CONTRATO';


update RECURSOSISTEMA
set CAMINHO = '/administrativo/licitacao/execucao-sem-contrato-estorno/edita.xhtml',
    NOME = 'COMPRAS E LICITAÇÕES > EXECUÇÃO SEM CONTRATO ESTORNO > EDITA'
where CAMINHO = '/administrativo/licitacao/execucao-ata-estorno/edita.xhtml';

update RECURSOSISTEMA
set CAMINHO = '/administrativo/licitacao/execucao-sem-contrato-estorno/visualizar.xhtml',
    NOME = 'COMPRAS E LICITAÇÕES > EXECUÇÃO SEM CONTRATO ESTORNO > VISUALIZA'
where CAMINHO = '/administrativo/licitacao/execucao-ata-estorno/visualizar.xhtml';

update RECURSOSISTEMA
set CAMINHO = '/administrativo/licitacao/execucao-sem-contrato-estorno/lista.xhtml',
    NOME = 'COMPRAS E LICITAÇÕES > EXECUÇÃO SEM CONTRATO ESTORNO > LISTA'
where CAMINHO = '/administrativo/licitacao/execucao-ata-estorno/lista.xhtml';

delete
from menu
where CAMINHO = '/administrativo/licitacao/execucao-ata-estorno/lista.xhtml';

insert into MENU (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
VALUES (HIBERNATE_SEQUENCE.nextval, 'ESTORNO DE EXECUÇÃO SEM CONTRATO',
        '/administrativo/licitacao/execucao-sem-contrato-estorno/lista.xhtml',
        (select ID from menu where LABEL = 'COMPRAS E LICITAÇÕES'), 47, null);
