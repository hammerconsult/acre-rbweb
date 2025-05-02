update PREVIDENCIAARQUIVO
set previdenciaVinculoFP_id = (select max(prev.id)
                               from PREVIDENCIAVINCULOFP prev
                                        inner join TIPOPREVIDENCIAFP tipo on prev.TIPOPREVIDENCIAFP_ID = tipo.ID
                               where tipo.CODIGO = 5
                                 and prev.CONTRATOFP_ID = PREVIDENCIAARQUIVO.CONTRATOFP_ID and rownum <= 1);

