update paginamenuhpaginapref
set ordem = 1, nome = 'DECRETOS'
where nome = 'Decretos' and paginamenuhorizontalportal_id = (select id from paginamenuhorizportal where tipo = 'MENU_HORIZONTAL');

update paginamenuhpaginapref
set ordem = 2, nome = 'PORTARIAS'
where nome = 'Portarias' and paginamenuhorizontalportal_id = (select id from paginamenuhorizportal where tipo = 'MENU_HORIZONTAL');

update paginamenuhpaginapref
set ordem = 3, nome = 'RESOLUÇÕES'
where nome = 'Resoluções' and paginamenuhorizontalportal_id = (select id from paginamenuhorizportal where tipo = 'MENU_HORIZONTAL');

update paginamenuhpaginapref
set ordem = 4, nome = 'INSTRUÇÃO NORMATIVA'
where nome = 'Instrução Normativa' and paginamenuhorizontalportal_id = (select id from paginamenuhorizportal where tipo = 'MENU_HORIZONTAL');

update paginamenuhpaginapref
set ordem = 5, nome = 'CÓDIGO MUNICIPAL'
where nome = 'Código Municipal' and paginamenuhorizontalportal_id = (select id from paginamenuhorizportal where tipo = 'MENU_HORIZONTAL');

update paginamenuhpaginapref
set ordem = 6, nome = 'LEIS MUNICIPAIS'
where nome = 'Leis Municipais' and paginamenuhorizontalportal_id = (select id from paginamenuhorizportal where tipo = 'MENU_HORIZONTAL');
