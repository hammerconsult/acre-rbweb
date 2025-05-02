update recursosistema set nome = replace(nome, 'INTEGRAÇÃO RECEITA REALIZADA/ARRECADAÇÃO', 'INTEGRAÇÃO DE ARRECADAÇÃO')
where caminho = '/financeiro/orcamentario/lancamentoreceitaorc/integracao.xhtml';

update MENU set LABEL = replace(LABEL, 'INTEGRAÇÃO RECEITA REALIZADA/ARRECADAÇÃO', 'INTEGRAÇÃO DE ARRECADAÇÃO')
where caminho = '/financeiro/orcamentario/lancamentoreceitaorc/integracao.xhtml';

update recursosistema set caminho = '/financeiro/integracao/arrecadacao/integracao.xhtml'
where caminho = '/financeiro/orcamentario/lancamentoreceitaorc/integracao.xhtml';

update MENU set caminho = '/financeiro/integracao/arrecadacao/integracao.xhtml'
where caminho = '/financeiro/orcamentario/lancamentoreceitaorc/integracao.xhtml';
