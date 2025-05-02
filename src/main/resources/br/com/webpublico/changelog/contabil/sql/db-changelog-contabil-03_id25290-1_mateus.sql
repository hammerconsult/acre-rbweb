delete from ITEMEVENTOCLP where EVENTOCONTABIL_ID in (select id from eventocontabil where TIPOEVENTOCONTABIL = 'CONVENIO_RECEITA')
