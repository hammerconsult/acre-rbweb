delete
from documentooficial
where modelodoctooficial_id in (select id
                                from modelodoctooficial
                                where tipodoctooficial_id in (select id
                                                              from tipodoctooficial
                                                              where modulotipodoctooficial = 'DECLARACAO_DISPENSA_LICENCIAMENTO'));

delete
from modelodoctooficial
where tipodoctooficial_id in (select id
                              from tipodoctooficial
                              where modulotipodoctooficial = 'DECLARACAO_DISPENSA_LICENCIAMENTO');

delete
from usuariotipodocto
where tipodoctooficial_id in (select id
                              from tipodoctooficial
                              where modulotipodoctooficial = 'DECLARACAO_DISPENSA_LICENCIAMENTO');

delete
from tipodoctooficial
where modulotipodoctooficial = 'DECLARACAO_DISPENSA_LICENCIAMENTO';
