INSERT INTO ITEMPREGAOLANCEVENCEDOR
  SELECT hibernate_sequence.nextval, dados.* from (select ip.ID as idItem, lp.ID as idLance, lp.STATUSLANCEPREGAO, lp.VALOR, lp.PERCENTUALDESCONTO
                                                   from ITEMPREGAO ip
                                                     inner join lancepregao lp on lp.ID = ip.LANCEPREGAOVENCEDOR_ID) dados
