UPDATE banco
SET PADRAOCOSIF = NUMEROBANCO where exists(SELECT distinct PADRAO
                            FROM cosif
                            WHERE padrao = banco.numerobanco);

UPDATE banco
SET PADRAOCOSIF = '123' where not exists(SELECT distinct PADRAO
                            FROM cosif
                            WHERE padrao = banco.numerobanco);
