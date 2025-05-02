INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'RELATÓRIOS DA NFSE',
        NULL,
        (select ID from menu where LABEL = 'NFS-E'),
        100);
INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'RELATÓRIO DE NOTAS FICAIS',
        '/tributario/nfse/relatorio/notas-fiscais.xhtml',
        (select ID from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        9);
INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'APURAÇÃO DO ISSQN',
        '/tributario/nfse/relatorio/apuracao-issqn.xhtml',
        (select ID from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        10);
INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'CONTRIBUINTES SEM MOVIMENTO DE ISSQN',
        '/tributario/nfse/relatorio/cadastro-economico-sem-movimento.xhtml',
        (select ID from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        20);
INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'RELAÇÃO DAS CONTAS DE RECEITAS BANCÁRIAS',
        '/tributario/nfse/relatorio/conta-receita-bancaria.xhtml',
        (select ID from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        30);
INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'RELAÇÃO DE CUPONS PARTICIPANTES',
        '/tributario/nfse/relatorio/cupom-participante.xhtml',
        (select ID from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        40);
INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'RELATÓRIO DE DMS (CONSOLIDADO)',
        '/tributario/nfse/relatorio/declaracao-mensal-consolidado.xhtml',
        (select ID from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        50);
INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'RELATÓRIO DE DMS (ANALITICO)',
        '/tributario/nfse/relatorio/declaracao-mensal-analitico.xhtml',
        (select ID from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        60);
INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'DEMONSTRATIVO DE NOTAS POR SITUAÇÃO',
        '/tributario/nfse/relatorio/notas-por-situacao.xhtml',
        (select ID from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        70);
INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'RELAÇÃO DE EMPRESAS ATIVAS NO SIMPLES',
        '/tributario/nfse/relatorio/empresas-ativas-no-simples-nacional.xhtml',
        (select ID from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        80);
INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'RELATORIO DE MARIORES TOMADORES DE SERVIÇO',
        '/tributario/nfse/relatorio/maiores-tomadores-servico.xhtml',
        (select ID from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        90);
INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'RELAÇÃO DE NOTAS COM ALIQUOTA DIFERENTE DO PGDAS',
        '/tributario/nfse/relatorio/notas-divergentes-pgdas.xhtml',
        (select ID from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        100);
INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'RELAÇÃO DE NOTAS NÃO ESCRITURADAS',
        '/tributario/nfse/relatorio/notas-nao-escrituradas.xhtml',
        (select ID from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        110);
INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'RELAÇÃO DE PRESTADORES AUTORIZADOS',
        '/tributario/nfse/relatorio/prestadores-autorizados.xhtml',
        (select ID from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        120);
INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'RELATÓRIO DE RECEITA BRUTA TOTAL',
        '/tributario/nfse/relatorio/receita-bruta-total.xhtml',
        (select ID from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        130);
INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'RELATÓRIO DE RECEITAS DECLARADAS',
        '/tributario/nfse/relatorio/receita-declarada-iss.xhtml',
        (select ID from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        140);
INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'RESUMO DE RPS',
        '/tributario/nfse/relatorio/resumo-arquivos-rps.xhtml',
        (select ID from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        150);
INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'RESUMO DE NOTAS POR ATIVIDADE',
        '/tributario/nfse/relatorio/notas-fiscais-por-atividade.xhtml',
        (select ID from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        160);
INSERT INTO menu
VALUES (HIBERNATE_SEQUENCE.nextval,
        'RELATORIO DE SOLICITAÇÃO DE EMISSÃO DE NOTA',
        '/tributario/nfse/relatorio/solicitacao-emissao-nota.xhtml',
        (select ID from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        170);
INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'NFS-E > RELATORIOS',
        '/tributario/nfse/relatorio/notas-fiscais.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'NFS-E > RELATORIOS',
        '/tributario/nfse/relatorio/apuracao-issqn.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'NFS-E > RELATORIOS',
        '/tributario/nfse/relatorio/cadastro-economico-sem-movimento.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'NFS-E > RELATORIOS',
        '/tributario/nfse/relatorio/conta-receita-bancaria.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'NFS-E > RELATORIOS',
        '/tributario/nfse/relatorio/cupom-participante.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'NFS-E > RELATORIOS',
        '/tributario/nfse/relatorio/declaracao-mensal-consolidado.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'NFS-E > RELATORIOS',
        '/tributario/nfse/relatorio/declaracao-mensal-analitico.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'NFS-E > RELATORIOS',
        '/tributario/nfse/relatorio/notas-por-situacao.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'NFS-E > RELATORIOS',
        '/tributario/nfse/relatorio/empresas-ativas-no-simples-nacional.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'NFS-E > RELATORIOS',
        '/tributario/nfse/relatorio/maiores-tomadores-servico.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'NFS-E > RELATORIOS',
        '/tributario/nfse/relatorio/notas-divergentes-pgdas.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'NFS-E > RELATORIOS',
        '/tributario/nfse/relatorio/notas-nao-escrituradas.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'NFS-E > RELATORIOS',
        '/tributario/nfse/relatorio/prestadores-autorizados.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'NFS-E > RELATORIOS',
        '/tributario/nfse/relatorio/receita-bruta-total.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'NFS-E > RELATORIOS',
        '/tributario/nfse/relatorio/receita-declarada-iss.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'NFS-E > RELATORIOS',
        '/tributario/nfse/relatorio/resumo-arquivos-rps.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'NFS-E > RELATORIOS',
        '/tributario/nfse/relatorio/notas-fiscais-por-atividade.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

INSERT INTO RECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.nextval,
        'NFS-E > RELATORIOS',
        '/tributario/nfse/relatorio/solicitacao-emissao-nota.xhtml',
        0,
        'TRIBUTARIO');
INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, 622038399);

