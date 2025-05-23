 UPDATE ESTADOBEM EST SET EST.LOCALIZACAO = (SELECT LEV.LOCALIZACAO
                                                    FROM LEVANTAMENTOBEMPATRIMONIAL LEV
                                                  INNER JOIN BEM B ON B.ID = LEV.BEM_ID
                                                  WHERE B.IDENTIFICACAO = EST.IDENTIFICACAO
                                                  )
