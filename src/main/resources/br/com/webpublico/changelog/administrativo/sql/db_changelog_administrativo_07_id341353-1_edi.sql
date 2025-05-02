insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
values (HIBERNATE_SEQUENCE.nextval,
        'COMPRAS E LICITAÇÕES > SOLICITAÇÃO DE ADESÃO À ATA DE REGISTRO DE PREÇO - EXTERNA',
        '/administrativo/licitacao/solicitacaomaterialexterno/lista-externa.xhtml',
        0,
        'LICITACAO');

update MENU set CAMINHO = '/administrativo/licitacao/solicitacaomaterialexterno/lista-externa.xhtml'
where id = 10736446579;

update MENU set CAMINHO = '/administrativo/licitacao/solicitacaomaterialexterno/lista-interna.xhtml'
where id = 10736446386;

update RECURSOSISTEMA set CAMINHO = '/administrativo/licitacao/solicitacaomaterialexterno/lista-interna.xhtml',
                          NOME = 'COMPRAS E LICITAÇÕES > SOLICITAÇÃO DE ADESÃO À ATA DE REGISTRO DE PREÇO - INTERNA'
where id = 170898802;
