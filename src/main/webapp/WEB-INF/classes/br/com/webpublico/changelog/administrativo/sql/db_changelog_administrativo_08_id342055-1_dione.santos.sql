update adesao ad
set ad.numerorequisicao = (select sol.numero
                           from solicitacaomaterialext sol
                           where sol.id = ad.solicitacaomaterialexterno_id)
where ad.numerorequisicao is null;
