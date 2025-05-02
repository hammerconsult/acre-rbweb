update CONTRATOFP set SITUACAOADMISSAOBBPREV = 'PARTICIPANTE_ADMITIDO_NA_EMPRESA'
                  where id in (select contr.id from contratofp contr inner join vinculofp vinc on contr.ID = vinc.ID where vinc.NUMERO = 1);
update CONTRATOFP set SITUACAOADMISSAOBBPREV = 'PARTICIPANTE_READMITIDO_NA_EMPRESA'
                  where id in (select contr.id from contratofp contr inner join vinculofp vinc on contr.ID = vinc.ID where vinc.NUMERO > 1);
