UPDATE PROVISAOPPADESPESA PD SET PD.UNIDADEORGANIZACIONAL_ID = (SELECT ACAO.RESPONSAVEL_ID
                                                                FROM ACAOPPA ACAO 
                                                                INNER JOIN SUBACAOPPA SUB ON SUB.ACAOPPA_ID = ACAO.ID
                                                                INNER JOIN PROVISAOPPA P ON P.SUBACAO_ID = SUB.ID
                                                                INNER JOIN PROVISAOPPADESPESA PPD ON PPD.PROVISAOPPA_ID = P.ID
                                                                WHERE PPD.ID = PD.ID); 
commit;