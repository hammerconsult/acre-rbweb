update requisicaomaterial req set req.localestoqueorigem_id = (
    select rm.ORIGEM_ID from REQUISICAOCONSUMOMAT rm
    where rm.id = req.id
);
