update menu
set label = 'RELATÓRIO DE CESSÃO DE BENS MÓVEIS'
where id = (select id from menu where label = 'RELATÓRIO DE BENS MÓVEIS RECEBIDOS POR CESSÃO');

delete from  menu where label = 'RELATÓRIO DE BENS MÓVEIS CEDIDOS';
