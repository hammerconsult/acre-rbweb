update ATAREGISTROPRECO ata
set ata.gerenciadora = 1
where ata.id in (select arp.id
                 from ATAREGISTROPRECO arp
                          inner join licitacao lic on lic.id = arp.LICITACAO_ID
                          inner join processodecompra pc on pc.id = lic.PROCESSODECOMPRA_ID
                 where pc.UNIDADEORGANIZACIONAL_ID = ata.UNIDADEORGANIZACIONAL_ID);

update ATAREGISTROPRECO ata
set ata.gerenciadora = 0
where ata.id in (select arp.id
                 from ATAREGISTROPRECO arp
                          inner join licitacao lic on lic.id = arp.LICITACAO_ID
                          inner join processodecompra pc on pc.id = lic.PROCESSODECOMPRA_ID
                 where pc.UNIDADEORGANIZACIONAL_ID <> ata.UNIDADEORGANIZACIONAL_ID);
