update DOTACAOSOLMATITEM set tipoObjetoCompra = 'CONSUMO'
where tipocontadespesa = 'BEM_ESTOQUE';

update DOTACAOSOLMATITEM set tipoObjetoCompra = 'SERVICO'
where tipocontadespesa = 'NAO_APLICAVEL';

update DOTACAOSOLMATITEM set tipoObjetoCompra = 'PERMANENTE_IMOVEL'
where tipocontadespesa = 'BEM_IMOVEL';

update DOTACAOSOLMATITEM set tipoObjetoCompra = 'PERMANENTE_MOVEL'
where tipocontadespesa = 'BEM_MOVEL'
