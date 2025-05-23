update cedenciacontratofp set cessionario_id = (select uni.id from unidadeexterna uni where uni.pessoajuridica_id = 73848588 and rownum = 1) where id = 11013468867;
