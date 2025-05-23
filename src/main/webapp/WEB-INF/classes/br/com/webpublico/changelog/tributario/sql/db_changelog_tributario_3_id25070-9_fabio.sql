update recursosistema set nome = replace(nome, 'INTEGRAÇÃO ESTORNO DE RECEITA REALIZADA/ARRECADAÇÃO', 'INTEGRAÇÃO DE ESTORNO DE ARRECADAÇÃO')
where caminho = '/financeiro/orcamentario/lancamentoreceitaorcestorno/integracao.xhtml';

update MENU set LABEL = replace(LABEL, 'INTEGRAÇÃO ESTORNO DE RECEITA REALIZADA/ARRECADAÇÃO', 'INTEGRAÇÃO DE ESTORNO DE ARRECADAÇÃO')
where caminho = '/financeiro/orcamentario/lancamentoreceitaorcestorno/integracao.xhtml';

update recursosistema set caminho = '/financeiro/integracao/estorno/integracao.xhtml'
where caminho = '/financeiro/orcamentario/lancamentoreceitaorcestorno/integracao.xhtml';

update MENU set caminho = '/financeiro/integracao/estorno/integracao.xhtml'
where caminho = '/financeiro/orcamentario/lancamentoreceitaorcestorno/integracao.xhtml';
