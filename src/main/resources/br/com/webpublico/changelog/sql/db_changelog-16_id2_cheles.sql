UPDATE LEVANTAMENTOBEMPATRIMONIAL LEV
   SET LEV.DATALEVANTAMENTO = (SELECT TO_DATE(TO_CHAR(REV.DATAHORA, 'YYYY-MON-DD HH24:MI:SS'), 'YYYY-MON-DD HH24:MI:SS') AS DATA_INSERCAO                               
                                 FROM REVISAOAUDITORIA REV                         
                           INNER JOIN LEVANTAMENTOBEMPATRIMONIAL_AUD LEVAUD ON LEVAUD.REV = REV.ID                              
                                WHERE LEVAUD.ID = LEV.ID                                
                                  AND LEVAUD.REVTYPE = 0)