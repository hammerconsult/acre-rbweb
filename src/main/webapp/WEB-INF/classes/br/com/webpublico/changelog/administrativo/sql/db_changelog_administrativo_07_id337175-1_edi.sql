update ataregistropreco ata
set ata.valor = (select lic.valormaximo from licitacao lic where lic.id = ata.licitacao_id),
    ata.saldo = (select lic.valormaximo from licitacao lic where lic.id = ata.licitacao_id);
