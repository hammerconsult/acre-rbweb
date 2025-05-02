INSERT INTO RECURSOSISTEMA
select HIBERNATE_SEQUENCE.nextval,
       'TRIBUTÁRIO > DOMICÍLIO TRIBUTÁRIO ELETRÔNICO  > CONFIGURAÇÕES DO DTE',
       '/tributario/dte/configuracao/configurar.xhtml',
       0,
       'TRIBUTARIO'
from dual
where not exists(Select id from RECURSOSISTEMA where CAMINHO = '/tributario/dte/configuracao/configurar.xhtml');

INSERT INTO GRUPORECURSOSISTEMA
VALUES (HIBERNATE_SEQUENCE.currval, (select id from GRUPORECURSO where nome = 'ADMINISTRADOR TRIBUTÁRIO'));

INSERT INTO menu (id, label, caminho, pai_id, ordem)
select HIBERNATE_SEQUENCE.nextval,
       'DOMICÍLIO TRIBUTÁRIO ELETRÔNICO',
       null,
       (select ID from menu where LABEL = 'TRIBUTÁRIO'),
       190
from dual
where not exists(select id from menu where label = 'DOMICÍLIO TRIBUTÁRIO ELETRÔNICO');

INSERT INTO menu (id, label, caminho, pai_id, ordem)
select HIBERNATE_SEQUENCE.nextval,
       'CONFIGURAÇÕES DO DTE',
       '/tributario/dte/configuracao/configurar.xhtml',
       (select ID from menu where LABEL = 'DOMICÍLIO TRIBUTÁRIO ELETRÔNICO'),
       0
from dual
where not exists(select id from menu where CAMINHO = '/tributario/dte/configuracao/configurar.xhtml');

