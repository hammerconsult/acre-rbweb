update EXECUCAOCONTRATOTIPO set tipoObjetoCompra = 'CONSUMO'
where tipocontadespesa = 'BEM_ESTOQUE';

update EXECUCAOCONTRATOTIPO set tipoObjetoCompra = 'SERVICO'
where tipocontadespesa = 'NAO_APLICAVEL';

update EXECUCAOCONTRATOTIPO set tipoObjetoCompra = 'PERMANENTE_IMOVEL'
where tipocontadespesa = 'BEM_IMOVEL';

update EXECUCAOCONTRATOTIPO set tipoObjetoCompra = 'PERMANENTE_MOVEL'
where tipocontadespesa = 'BEM_MOVEL'
