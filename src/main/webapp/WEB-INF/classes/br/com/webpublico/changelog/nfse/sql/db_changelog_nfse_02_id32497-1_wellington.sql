DELETE FROM RECURSOSISTEMA WHERE CAMINHO = '/tributario/nfse/relatorio/declaracao-mensal-analitico.xhtml';

UPDATE RECURSOSISTEMA SET nome = 'TRIBUTÁRIO > NOTA FISCAL > RELATÓRIOS DA NFSE > RELATÓRIO DE DMS', CAMINHO = '/tributario/nfse/relatorio/declaracao-mensal.xhtml'
WHERE caminho = '/tributario/nfse/relatorio/declaracao-mensal-consolidado.xhtml';

UPDATE MENU SET LABEL = 'RELATÓRIO DE DMS', CAMINHO = '/tributario/nfse/relatorio/declaracao-mensal.xhtml'
WHERE CAMINHO  = '/tributario/nfse/relatorio/declaracao-mensal-consolidado.xhtml';

DELETE FROM MENU WHERE LABEL = 'RELATÓRIO DE DMS (ANALITICO)';
