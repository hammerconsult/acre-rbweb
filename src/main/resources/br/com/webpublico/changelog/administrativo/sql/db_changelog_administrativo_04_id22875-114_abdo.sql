update menu set pai_id = (select id from menu where label = 'PREGÃO') where label in('PREGÃO POR LOTE DE MATERIAL E SERVIÇO',
'PREGÃO POR MATERIAL E SERVIÇO');
delete from menu where label = 'PREGÃO DE MATERIAL E SERVIÇO';
update menu set caminho = '/administrativo/licitacao/pregao/por-item/lista.xhtml'
where label = 'PREGÃO POR MATERIAL E SERVIÇO';
update menu set caminho = '/administrativo/licitacao/pregao/por-lote/lista.xhtml'
where label = 'PREGÃO POR LOTE DE MATERIAL E SERVIÇO';
