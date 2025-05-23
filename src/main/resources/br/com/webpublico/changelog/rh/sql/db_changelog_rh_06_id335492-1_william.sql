update LOTACAOFUNCIONAL
set FINALVIGENCIA = null
where id in (
    select LOTACAO.id
    from CEDENCIACONTRATOFP CEDENCIA
             INNER JOIN VINCULOFP VINCULO ON CEDENCIA.CONTRATOFP_ID = VINCULO.ID
             INNER JOIN LOTACAOFUNCIONAL LOTACAO ON VINCULO.ID = LOTACAO.VINCULOFP_ID
    where TIPOCONTRATOCEDENCIAFP = 'COM_ONUS'
      AND SYSDATE BETWEEN VINCULO.INICIOVIGENCIA AND COALESCE(VINCULO.FINALVIGENCIA, SYSDATE)
      AND LOTACAO.INICIOVIGENCIA =
          (SELECT MAX(LOTSUB.INICIOVIGENCIA) FROM LOTACAOFUNCIONAL LOTSUB WHERE LOTSUB.VINCULOFP_ID = VINCULO.ID)
      and LOTACAO.FINALVIGENCIA is not null);
