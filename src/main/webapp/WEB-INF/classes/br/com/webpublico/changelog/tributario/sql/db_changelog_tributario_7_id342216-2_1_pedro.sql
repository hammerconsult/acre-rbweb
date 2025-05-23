UPDATE PERMISSIONARIO P
SET FINALVIGENCIA = SYSDATE
WHERE P.ID IN (SELECT PERM.ID
               FROM PERMISSIONARIO PERM
               WHERE PERM.PERMISSAOTRANSPORTE_ID in (SELECT PERMTRANS.ID
                                                     FROM PERMISSAOTRANSPORTE PERMTRANS
                                                     where NUMERO in
                                                           (25, 47, 80, 87, 164, 167, 189, 224, 441, 517,
                                                            548, 657, 659, 682)
                                                       AND TIPOPERMISSAORBTRANS = 'TAXI')
                 AND PERM.FINALVIGENCIA IS NULL);
