update RECURSOSISTEMA RC
set RC.NOME    = 'TRIBUTÁRIO > ITBI > LANÇAMENTO DE ITBI > LISTA LANÇAMENTO DE ITBI',
    RC.CAMINHO = '/tributario/itbi/calculoitbi/lista.xhtml'
WHERE RC.ID = (SELECT R.ID FROM RECURSOSISTEMA R WHERE R.CAMINHO = '/tributario/itbi/cadeiadominial/lista.xhtml');

update RECURSOSISTEMA RC
set RC.NOME    = 'TRIBUTÁRIO > ITBI > LANÇAMENTO DE ITBI > EDITA LANÇAMENTO DE ITBI',
    RC.CAMINHO = '/tributario/itbi/calculoitbi/edita.xhtml'
WHERE RC.ID = (SELECT R.ID FROM RECURSOSISTEMA R WHERE R.CAMINHO = '/tributario/itbi/cadeiadominial/edita.xhtml');

update RECURSOSISTEMA RC
set RC.NOME    = 'TRIBUTÁRIO > ITBI > LANÇAMENTO DE ITBI > VISUALIZA LANÇAMENTO DE ITBI',
    RC.CAMINHO = '/tributario/itbi/calculoitbi/visualizar.xhtml'
WHERE RC.ID = (SELECT R.ID FROM RECURSOSISTEMA R WHERE R.CAMINHO = '/tributario/itbi/cadeiadominial/visualizar.xhtml');

delete
from menu
where label = 'LANÇAMENTO DE ITBI'
  and caminho = '/tributario/itbi/calculo/lista.xhtml';

UPDATE MENU
SET LABEL   = 'LANÇAMENTO DE ITBI',
    CAMINHO = '/tributario/itbi/calculoitbi/lista.xhtml'
WHERE LABEL = 'CADEIA DOMINIAL DE ITBI';





