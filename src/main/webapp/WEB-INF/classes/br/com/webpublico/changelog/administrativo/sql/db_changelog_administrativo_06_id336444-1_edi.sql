update ataregistropreco ata set ata.unidadeorganizacional_id = (
    select pc.unidadeorganizacional_id from processodecompra pc
                                                inner join licitacao lic on lic.PROCESSODECOMPRA_ID = pc.id
    where lic.id = ata.licitacao_id);
