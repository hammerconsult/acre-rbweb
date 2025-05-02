 UPDATE ESTADOBEM EST SET EST.TIPOAQUISICAOBEM = (SELECT LEV.TIPOAQUISICAOBEM
                                                    FROM LEVANTAMENTOBEMPATRIMONIAL LEV
                                                  INNER JOIN BEM B ON B.ID = LEV.BEM_ID
                                                  WHERE B.IDENTIFICACAO = EST.IDENTIFICACAO
                                                  )
