update RECURSOSISTEMA
set CAMINHO = '/administrativo/licitacao/ajuste-processo-compra/edita.xhtml'
where CAMINHO = '/administrativo/licitacao/alteracao-fornecedor-licitacao/edita.xhtml';

update RECURSOSISTEMA
set CAMINHO = '/administrativo/licitacao/ajuste-processo-compra/lista.xhtml'
where CAMINHO = '/administrativo/licitacao/alteracao-fornecedor-licitacao/lista.xhtml';

update RECURSOSISTEMA
set CAMINHO = '/administrativo/licitacao/ajuste-processo-compra/visualizar.xhtml'
where CAMINHO = '/administrativo/licitacao/alteracao-fornecedor-licitacao/visualizar.xhtml';

update menu
set CAMINHO = '/administrativo/licitacao/ajuste-processo-compra/lista.xhtml',
    LABEL   ='AJUSTE PROCESSO DE COMPRA'
where CAMINHO = '/administrativo/licitacao/alteracao-fornecedor-licitacao/lista.xhtml';

delete
from RECURSOSISTEMA
where CAMINHO like '%/administrativo/licitacao/alteracao-item-processo/%';

delete
from menu
where CAMINHO like '%/administrativo/licitacao/alteracao-item-processo/%';
