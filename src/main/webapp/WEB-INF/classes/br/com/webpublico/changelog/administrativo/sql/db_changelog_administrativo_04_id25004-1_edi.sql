update solicitacaomaterialext set subtipoobjetocompra = 'NAO_APLICAVEL';

update solicitacaomaterialext sol set sol.tipoobjetocompra =
  (select distinct
  o.tipoobjetocompra
  from itemsolicitacaoexterno item
  inner join objetocompra o on o.id = item.objetocompra_id
  where item.solicitacaomaterialexterno_id = sol.id);
