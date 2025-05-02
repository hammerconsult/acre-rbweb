delete from RECURSOSISTEMA where upper(nome) like upper('%EXAUSTÃO DE BEM INTANGÍVEL%');

delete from RECURSOSISTEMA where upper(nome) like upper('%EXAUSTÃO DE BENS MÓVEIS%');

delete from MENU where upper(label) = 'EXAUSTÃO DE BEM INTANGÍVEL';

delete from MENU where upper(label) = 'EXAUSTÃO DE BENS MÓVEIS';


