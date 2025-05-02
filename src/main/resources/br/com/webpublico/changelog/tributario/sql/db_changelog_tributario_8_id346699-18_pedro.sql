UPDATE PROCESSOLICENCIAMENTOAMBIENTAL PROC
SET PROC.PROCESSOCALCULOLICENCIAMENTOAMBIENTAL_ID = (SELECT PROCCALC.ID
                                                     FROM PROCESSOCALCULOLICENCIAMENTOAMBIENTAL PROCCALC
                                                     WHERE PROCCALC.PROCESSOLICENCIAMENTOAMBIENTAL_ID = PROC.ID
                                                       AND (PROCCALC.STATUSGEROUDEBITO = 'ABERTO' OR
                                                            PROCCALC.STATUSGEROUDEBITO =
                                                            'ANALISE_DOCUMENTAL')
                                                         FETCH FIRST 1 ROW ONLY);

UPDATE PROCESSOLICENCIAMENTOAMBIENTAL PROC
SET PROC.PROCESSOCALCULOTAXAEXPEDIENTE_ID = (SELECT PROCCALC.ID
                                             FROM PROCESSOCALCULOLICENCIAMENTOAMBIENTAL PROCCALC
                                             WHERE PROCCALC.PROCESSOLICENCIAMENTOAMBIENTAL_ID = PROC.ID
                                               AND PROCCALC.STATUSGEROUDEBITO = 'TAXA_EXPEDIENTE' FETCH FIRST 1 ROW ONLY);
