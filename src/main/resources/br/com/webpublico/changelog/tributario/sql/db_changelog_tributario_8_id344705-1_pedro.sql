UPDATE DADOSRETIFICACAOPESSOACDA
SET MUNICIPIO = 'XAPURI',
    UF        = 'AC'
WHERE ID IN (select ID
             from DADOSRETIFICACAOPESSOACDA
             where UPPER(TRIM(MUNICIPIO)) <> 'XAPURI'
               AND UPPER(TRIM(MUNICIPIO)) LIKE '%XAPURI%');

UPDATE DADOSRETIFICACAOPESSOACDA
SET MUNICIPIO = 'ACRELANDIA',
    UF        = 'AC'
WHERE ID IN (select ID
             from DADOSRETIFICACAOPESSOACDA
             where UPPER(TRIM(MUNICIPIO)) <> 'ACRELANDIA'
               AND (UPPER(TRIM(MUNICIPIO)) LIKE '%ACRELANDIA%' OR UPPER(TRIM(MUNICIPIO)) LIKE '%ACRELÂNDIA%'));

UPDATE DADOSRETIFICACAOPESSOACDA
SET MUNICIPIO = 'BUJARI',
    UF        = 'AC'
WHERE ID IN (select ID
             from DADOSRETIFICACAOPESSOACDA
             where UPPER(TRIM(MUNICIPIO)) <> 'BUJARI'
               AND UPPER(TRIM(MUNICIPIO)) LIKE '%BUJARI%');

UPDATE DADOSRETIFICACAOPESSOACDA
SET MUNICIPIO = 'JORDÃO',
    UF        = 'AC'
WHERE ID IN (select ID
             from DADOSRETIFICACAOPESSOACDA
             where UPPER(TRIM(MUNICIPIO)) <> 'JORDÃO'
               AND UPPER(TRIM(MUNICIPIO)) LIKE '%JORDÃO%');

UPDATE DADOSRETIFICACAOPESSOACDA
SET MUNICIPIO = 'BRASILÉIA',
    UF        = 'AC'
WHERE ID IN (select ID
             from DADOSRETIFICACAOPESSOACDA
             where UPPER(TRIM(MUNICIPIO)) <> 'BRASILÉIA'
               AND (UPPER(TRIM(MUNICIPIO)) LIKE '%BRASILÉIA%' OR UPPER(TRIM(MUNICIPIO)) LIKE '%BASILÉIA%'));

UPDATE DADOSRETIFICACAOPESSOACDA
SET MUNICIPIO = 'SENADOR GUIOMARD',
    UF        = 'AC'
WHERE ID IN (select ID
             from DADOSRETIFICACAOPESSOACDA
             where UPPER(TRIM(MUNICIPIO)) <> 'SENADOR GUIOMARD'
               AND (UPPER(TRIM(MUNICIPIO)) LIKE '%SENADOR GUIOMARD%' OR
                    UPPER(TRIM(MUNICIPIO)) LIKE '%SENADOR GUIMARD%'));

UPDATE DADOSRETIFICACAOPESSOACDA
SET MUNICIPIO = 'PORTO ACRE',
    UF        = 'AC'
WHERE ID IN (select ID
             from DADOSRETIFICACAOPESSOACDA
             where UPPER(TRIM(MUNICIPIO)) <> 'PORTO ACRE'
               AND UPPER(TRIM(MUNICIPIO)) LIKE '%PORTO ACRE%');

UPDATE DADOSRETIFICACAOPESSOACDA
SET MUNICIPIO = 'MANCIO LIMA',
    UF        = 'AC'
WHERE ID IN (select ID
             from DADOSRETIFICACAOPESSOACDA
             where UPPER(TRIM(MUNICIPIO)) <> 'MANCIO LIMA'
               AND UPPER(TRIM(MUNICIPIO)) LIKE '%MANCIO LIMA%');

UPDATE DADOSRETIFICACAOPESSOACDA
SET MUNICIPIO = 'SENA MADUREIRA',
    UF        = 'AC'
WHERE ID IN (select ID
             from DADOSRETIFICACAOPESSOACDA
             where UPPER(TRIM(MUNICIPIO)) <> 'SENA MADUREIRA'
               AND UPPER(TRIM(MUNICIPIO)) LIKE '%SENA MADUREIRA%');

UPDATE DADOSRETIFICACAOPESSOACDA
SET MUNICIPIO = 'RIO BRANCO',
    UF        = 'AC'
WHERE ID IN (select ID
             from DADOSRETIFICACAOPESSOACDA
             WHERE UPPER(TRIM(MUNICIPIO)) <> 'RIO BRANCO'
               AND UPPER(TRIM(MUNICIPIO)) LIKE '%RIO BRANCO%'
               and UPPER(TRIM(MUNICIPIO)) NOT LIKE '%RIO BRANCO DO SUL%'
               and UPPER(TRIM(MUNICIPIO)) NOT LIKE '%RIO BRANCO DO IVAÍ%'
               and UPPER(TRIM(MUNICIPIO)) NOT LIKE '%VISCONDE DO RIO BRANCO%');
