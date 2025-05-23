update contratofp c set c.situacaofuncional_id =
(select st.SITUACAOFUNCIONAL_ID from situacaocontratofp st where st.CONTRATOFP_ID =  c.id
and st.INICIOVIGENCIA = (select max(s.inicioVigencia) from situacaocontratofp s where s.contratofp_id = st.contratofp_id)
and rownum = 1) where c.SITUACAOFUNCIONAL_ID is null
