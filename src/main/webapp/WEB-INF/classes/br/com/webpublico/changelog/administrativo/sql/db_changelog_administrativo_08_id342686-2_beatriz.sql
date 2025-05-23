UPDATE MENU
SET CAMINHO = REPLACE(CAMINHO, 'relatoriobensrecebidosporcessao', 'relatoriocessaobensmoveis')
WHERE CAMINHO LIKE '%relatoriobensrecebidosporcessao%' ;
