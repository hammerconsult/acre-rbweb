update requisicaodecompra set tipocontadespesa = 'BEM_MOVEL'
where tipoobjetocompra = 'PERMANENTE_MOVEL';

update requisicaodecompra set tipocontadespesa = 'BEM_IMOVEL'
where tipoobjetocompra = 'PERMANENTE_IMOVEL';

update requisicaodecompra set tipocontadespesa = 'BEM_ESTOQUE'
where tipoobjetocompra = 'CONSUMO';

update requisicaodecompra set tipocontadespesa = 'SERVICO_DE_TERCEIRO'
where tipoobjetocompra = 'SERVICO';
