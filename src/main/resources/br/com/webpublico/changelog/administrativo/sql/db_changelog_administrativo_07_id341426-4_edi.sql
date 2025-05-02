update contrato c
set c.tipoaquisicao = 'INTENCAO_REGISTRO_PRECO'
where c.id in (select cl.contrato_id
               from conlicitacao cl
               where  cl.contrato_id = c.id
                 and cl.participanteirp_id is not null
                 and cl.solicitacaomaterialexterno_id is null);
