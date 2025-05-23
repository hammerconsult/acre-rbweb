update periodo
set INICIO = to_date('01/01/1970 ' || to_char(INICIO, 'HH24:mi:ss'), 'dd/MM/yyyy HH24:mi:ss'),
    TERMINO = to_date('01/01/1970 ' || to_char(TERMINO, 'HH24:mi:ss'), 'dd/MM/yyyy HH24:mi:ss')
where INICIO is not null and TERMINO is not null
