update documentooficial set conteudo = null where id in (select documentooficial_id from SOLICITACAODOCTOOFICIAL) and CONTEUDO like '%qrserver%';
